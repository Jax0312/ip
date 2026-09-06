package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

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
        assertTrue(this.taskList.get(0).isDone());

        this.taskList.setDone(0, false);
        assertFalse(this.taskList.get(0).isDone());
    }

    @Test
    public void setDone_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.setDone(0, true));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        Task bookTask = new Todo("read book");
        Task otherTask = new Todo("eat lunch");
        this.taskList.add(bookTask);
        this.taskList.add(otherTask);

        ArrayList<Task> results = this.taskList.find("book");
        assertEquals(1, results.size());
        assertEquals(bookTask, results.get(0));
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        this.taskList.add(new Todo("read book"));
        this.taskList.add(new Todo("write code"));

        ArrayList<Task> results = this.taskList.find("dinner");
        assertTrue(results.isEmpty());
    }

    @Test
    public void find_multipleMatchesAcrossTasks_returnsAllMatchingTasks() {
        Task readBook = new Todo("read book");
        Task returnBook = new Todo("return book");
        Task cookDinner = new Todo("cook dinner");
        this.taskList.add(readBook);
        this.taskList.add(returnBook);
        this.taskList.add(cookDinner);

        ArrayList<Task> results = this.taskList.find("book");
        assertEquals(2, results.size());
        assertEquals(readBook, results.get(0));
        assertEquals(returnBook, results.get(1));
    }
}
