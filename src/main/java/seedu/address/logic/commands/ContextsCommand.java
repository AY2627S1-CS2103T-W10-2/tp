package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.NaturalOrderComparator;
import seedu.address.model.Model;
import seedu.address.model.person.Context;
import seedu.address.model.person.Person;

/**
 * Lists active persons' contexts with person counts without changing the displayed person list.
 */
public class ContextsCommand extends Command {

    public static final String COMMAND_WORD = "contexts";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all contexts of persons who are not archived, with person counts. "
            + "Does not change the displayed person list.\n"
            + "Parameters: none\n"
            + "Example: " + COMMAND_WORD + "\n"
            + "To search by context, use: " + FindCommand.COMMAND_WORD + " c/CONTEXT";

    public static final String MESSAGE_NO_CONTEXTS =
            "No contexts found. Add a person with c/CONTEXT to get started.";
    public static final String MESSAGE_NO_ACTIVE_CONTEXTS = "No active contexts found. "
            + "Run archives to view archived persons, then use unarchive INDEX to include them.";
    public static final String MESSAGE_CONTEXT_SINGULAR = "%1$s: %2$d person";
    public static final String MESSAGE_CONTEXT_PLURAL = "%1$s: %2$d people";
    public static final String MESSAGE_SUCCESS_SINGULAR = "%1$d context listed.";
    public static final String MESSAGE_SUCCESS_PLURAL = "%1$d contexts listed.";

    private static final Logger logger = LogsCenter.getLogger(ContextsCommand.class);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        List<Person> persons = model.getAddressBook().getPersonList();
        Map<String, Integer> personCountsByContext = countContexts(persons);
        logger.fine(() -> "Listing " + personCountsByContext.size() + " active contexts from "
                + persons.size() + " stored persons.");
        if (personCountsByContext.isEmpty()) {
            return new CommandResult(persons.isEmpty() ? MESSAGE_NO_CONTEXTS : MESSAGE_NO_ACTIVE_CONTEXTS);
        }
        return new CommandResult(formatOverview(personCountsByContext));
    }

    /**
     * Counts active persons' contexts, grouping equivalent descriptions and retaining the first active spelling.
     */
    private static Map<String, Integer> countContexts(List<Person> persons) {
        // Context descriptions are already normalized, and each person's contexts form a set.
        // TreeMap groups ignoring case and sorts naturally, retaining the first encountered spelling.
        Map<String, Integer> personCountsByContext = new TreeMap<>(NaturalOrderComparator.INSTANCE);
        for (Person person : persons) {
            if (!PREDICATE_SHOW_ACTIVE.test(person)) {
                continue;
            }
            for (Context context : person.getContexts()) {
                personCountsByContext.merge(context.description, 1, Integer::sum);
            }
        }

        return personCountsByContext;
    }

    /**
     * Formats the counted contexts in map order, followed by the distinct-context summary.
     */
    private static String formatOverview(Map<String, Integer> personCountsByContext) {
        String contextLines = personCountsByContext.entrySet().stream()
                .map(entry -> formatContext(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        String summaryFormat = personCountsByContext.size() == 1 ? MESSAGE_SUCCESS_SINGULAR : MESSAGE_SUCCESS_PLURAL;
        String summary = String.format(summaryFormat, personCountsByContext.size());
        return contextLines + "\n\n" + summary;
    }

    /**
     * Formats a context's count using person/people wording.
     */
    private static String formatContext(String context, int count) {
        String messageFormat = count == 1 ? MESSAGE_CONTEXT_SINGULAR : MESSAGE_CONTEXT_PLURAL;
        return String.format(messageFormat, context, count);
    }
}
