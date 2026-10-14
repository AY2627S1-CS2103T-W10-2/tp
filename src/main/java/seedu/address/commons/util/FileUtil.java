package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

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
     */
    public static void writeToFile(Path file, String content) throws IOException {
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
     * Copies {@code file} to a sibling file with the extra extension {@code .bak}, replacing an older backup.
     *
     * @return the backup file, or {@code Optional.empty()} if {@code file} does not exist.
     * @throws IOException if the copy fails.
     */
    public static Optional<Path> backUp(Path file) throws IOException {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
        return Optional.of(backup);
    }

}
