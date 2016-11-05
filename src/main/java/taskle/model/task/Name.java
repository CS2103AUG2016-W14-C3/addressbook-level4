package taskle.model.task;

/**
 * Represents a Tasks's name in the Task Manager.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String NAME_VALIDATION_REGEX = "[\\p{Alnum} ]+";

    public final String fullName;

    /**
     * Constructor for name
     *
     * @param name String for the name object
     */
    public Name(String name) {
        assert name != null;
        name = name.trim();
        this.fullName = name;
    }

    /**
     * Returns true if a given string is a valid task name.
     * 
     * @param test given string
     */
    public static boolean isValidName(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Name // instanceof handles nulls
                && this.fullName.equals(((Name) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
