public abstract class Task {
    private final String description;
    private boolean isMark;

    Task(String description) {
        this.description = description;
    }

    public void setMark(boolean isMark) {
        this.isMark = isMark;
    }
    public abstract String toFileFormat();

    public boolean getMark() {
        return this.isMark;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        String statusString = this.isMark ? "X" : " ";
        return String.format("[%s] %s", statusString, this.description);
    }
}
