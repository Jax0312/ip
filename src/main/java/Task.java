public abstract class Task {
    private final String description;
    private boolean isDone;

    Task(String description) {
        this.description = description;
    }

    public void setDone(boolean isMark) {
        this.isDone = isMark;
    }
    public abstract String toFileFormat();

    public boolean getDone() {
        return this.isDone;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        String statusString = this.isDone ? "X" : " ";
        return String.format("[%s] %s", statusString, this.description);
    }
}
