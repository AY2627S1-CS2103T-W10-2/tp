package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
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

    public static final String MESSAGE_NO_CONTEXTS =
            "No contexts found. Add a person with c/CONTEXT to get started.";
    public static final String MESSAGE_CONTEXT_SINGULAR = "%1$s: %2$d person";
    public static final String MESSAGE_CONTEXT_PLURAL = "%1$s: %2$d people";
    public static final String MESSAGE_SUCCESS_SINGULAR = "%1$d context listed.";
    public static final String MESSAGE_SUCCESS_PLURAL = "%1$d contexts listed.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Map<String, Integer> contactCounts = countContexts(model.getAddressBook().getPersonList());
        if (contactCounts.isEmpty()) {
            return new CommandResult(MESSAGE_NO_CONTEXTS);
        }
        return new CommandResult(formatOverview(contactCounts));
    }

    /**
     * Counts each person's contexts, grouping equivalent descriptions and retaining the first spelling.
     */
    private Map<String, Integer> countContexts(List<Person> persons) {
        // Context descriptions are already normalised, and each person's contexts form a set.
        // TreeMap groups and sorts ignoring case, retaining the first encountered spelling.
        Map<String, Integer> contactCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Person person : persons) {
            for (Context context : person.getContexts()) {
                contactCounts.merge(context.description, 1, Integer::sum);
            }
        }

        return contactCounts;
    }

    /**
     * Formats the counted contexts in map order, followed by the distinct-context summary.
     */
    private String formatOverview(Map<String, Integer> contactCounts) {
        String contextLines = contactCounts.entrySet().stream()
                .map(entry -> formatContext(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        String summaryFormat = contactCounts.size() == 1 ? MESSAGE_SUCCESS_SINGULAR : MESSAGE_SUCCESS_PLURAL;
        String summary = String.format(summaryFormat, contactCounts.size());
        return contextLines + "\n\n" + summary;
    }

    /**
     * Formats a context's count using person/people wording.
     */
    private String formatContext(String context, int count) {
        String messageFormat = count == 1 ? MESSAGE_CONTEXT_SINGULAR : MESSAGE_CONTEXT_PLURAL;
        return String.format(messageFormat, context, count);
    }
}
