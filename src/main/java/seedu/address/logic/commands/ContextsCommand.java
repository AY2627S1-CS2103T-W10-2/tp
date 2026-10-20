package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;

/**
 * Lists all contexts with contact counts without changing the displayed person list.
 */
public class ContextsCommand extends Command {

    public static final String COMMAND_WORD = "contexts";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all contexts and the number of contacts linked to each.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_NO_CONTEXTS =
            "No contexts found. Add a person with c/CONTEXT to get started.";
    public static final String MESSAGE_CONTEXT_SINGULAR = "%1$s: %2$d contact";
    public static final String MESSAGE_CONTEXT_PLURAL = "%1$s: %2$d contacts";
    public static final String MESSAGE_SUCCESS_SINGULAR = "%1$d context listed.";
    public static final String MESSAGE_SUCCESS_PLURAL = "%1$d contexts listed.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        // Context descriptions are already normalised, and each person's contexts form a set.
        // TreeMap groups and sorts ignoring case, retaining the first encountered spelling.
        Map<String, Integer> contactCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Person person : model.getAddressBook().getPersonList()) {
            for (Context context : person.getContexts()) {
                contactCounts.merge(context.description, 1, Integer::sum);
            }
        }

        if (contactCounts.isEmpty()) {
            return new CommandResult(MESSAGE_NO_CONTEXTS);
        }

        String contextLines = contactCounts.entrySet().stream()
                .map(entry -> String.format(entry.getValue() == 1
                        ? MESSAGE_CONTEXT_SINGULAR : MESSAGE_CONTEXT_PLURAL, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        String summary = String.format(contactCounts.size() == 1
                ? MESSAGE_SUCCESS_SINGULAR : MESSAGE_SUCCESS_PLURAL, contactCounts.size());
        return new CommandResult(contextLines + "\n\n" + summary);
    }
}
