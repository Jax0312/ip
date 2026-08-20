public class Task {
    private final String description;
    private boolean isMark;

    Task(String description) {
        this.description = description;
    }

    public void setMark(boolean isMark) {
        this.isMark = isMark;
    }

    @Override
    public String toString() {
        String statusString = this.isMark ? "X" : " ";
        return String.format("[%s] %s", statusString, this.description);
    }
}
