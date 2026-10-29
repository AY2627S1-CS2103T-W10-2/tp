package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     * The content is first written to a temporary file in the same folder and then moved over {@code file},
     * so an interrupted write leaves the previous content of {@code file} intact instead of a half-written file.
     * If {@code file} is a symbolic link, the file it points to is written and the link is kept.
     */
    public static void writeToFile(Path linkOrFile, String content) throws IOException {
        Path file = followSymbolicLink(linkOrFile);
        Path folder = file.toAbsolutePath().getParent();
        Path tempFile = Files.createTempFile(folder, file.getFileName().toString(), ".tmp");
        try {
            Files.write(tempFile, content.getBytes(CHARSET));
            try {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Returns the file that {@code file} points to if it is a symbolic link, otherwise {@code file} itself.
     */
    private static Path followSymbolicLink(Path file) throws IOException {
        if (!Files.isSymbolicLink(file)) {
            return file;
        }
        try {
            return file.toRealPath();
        } catch (NoSuchFileException e) {
            // the link points to a file that does not exist yet: create it there
            return file.resolveSibling(Files.readSymbolicLink(file)).normalize();
        }
    }

    /**
     * Backs up {@code file} by renaming it to a sibling file named {@code <name>.<UTC time>.bak}, for example
     * {@code addressbook.json.20261015T101723Z.bak}. {@code file} no longer exists afterwards, so a later write to
     * it cannot overwrite the backup. A backup made earlier keeps its own name; only a backup with the exact same
     * name (made in the same second) is replaced.
     *
     * @return the backup file, or {@code Optional.empty()} if {@code file} does not exist.
     * @throws IOException if the file cannot be renamed.
     */
    public static Optional<Path> backUp(Path file) throws IOException {
        return backUp(file, Clock.systemUTC());
    }

    /**
     * Same as {@link #backUp(Path)}, but takes the time for the backup name from {@code clock}.
     */
    static Optional<Path> backUp(Path file, Clock clock) throws IOException {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        String timestamp = BACKUP_TIMESTAMP_FORMAT.format(clock.instant());
        Path backup = file.resolveSibling(file.getFileName() + "." + timestamp + ".bak");
        // two failures in the same second must not overwrite each other's backup
        for (int copy = 1; Files.exists(backup); copy++) {
            backup = file.resolveSibling(file.getFileName() + "." + timestamp + "-" + copy + ".bak");
        }
        Files.move(file, backup);
        return Optional.of(backup);
    }

}
