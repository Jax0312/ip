public class Todo extends Task {

    Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return String.format("T | %d | %s", super.getMark() ? 1 : 0, super.getDescription());
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
