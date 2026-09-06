package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dave.exception.DaveCommandException;

/**
 * Unit tests for the {@link TaskList} class.
 */
public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        this.taskList = new TaskList();
    }

    @Test
    public void add_singleTask_increasesSizeAndRetrievesCorrectly() {
        Task todo = new Todo("borrow book");
        this.taskList.add(todo);

        assertEquals(1, this.taskList.size());
        assertEquals(todo, this.taskList.get(0));
    }

    @Test
    public void get_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.get(0));

        this.taskList.add(new Todo("test task"));
        assertThrows(DaveCommandException.class, () -> this.taskList.get(-1));
        assertThrows(DaveCommandException.class, () -> this.taskList.get(1));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        this.taskList.add(first);
        this.taskList.add(second);

        Task removed = this.taskList.delete(0);
        assertEquals(first, removed);
        assertEquals(1, this.taskList.size());
        assertEquals(second, this.taskList.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(0));

        this.taskList.add(new Todo("task"));
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(-1));
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(2));
    }

    @Test
    public void setDone_validIndex_updatesTaskStatus() {
        Task task = new Todo("task to complete");
        this.taskList.add(task);

        this.taskList.setDone(0, true);
        assertTrue(this.taskList.get(0).getDone());

        this.taskList.setDone(0, false);
        assertFalse(this.taskList.get(0).getDone());
    }

    @Test
    public void setDone_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.setDone(0, true));
    }
}
