public class Deadline extends Task {

    private final String by;

    Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return String.format("D | %d | %s | %s", super.getMark() ? 1 : 0, super.getDescription(), by);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.by);
    }
}
