package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Context;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                getContextSet("CS2103T")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                getContextSet("CS2103T", "Hiking club")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                getContextSet("Hall")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                getContextSet("Hackathon")),
            // no email
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), null,
                getContextSet("CS2101")),
            // no phone
            new Person(new Name("Roy Balakrishnan"), null, new Email("royb@example.com"),
                getContextSet("Internship"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a context set containing the list of strings given.
     */
    public static Set<Context> getContextSet(String... strings) {
        return Arrays.stream(strings)
                .map(Context::new)
                .collect(Collectors.toSet());
    }

}
