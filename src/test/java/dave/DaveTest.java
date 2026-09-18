package dave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the {@link Dave} class.
 */
public class DaveTest {

    @TempDir
    private Path tempDir;

    private Dave dave;

    @BeforeEach
    public void setUp() {
        Path filePath = this.tempDir.resolve("test-dave.txt");
        this.dave = new Dave(filePath.toString());
    }

    @Test
    public void isExit_initialState_returnsFalse() {
        assertFalse(this.dave.isExit());
    }

    @Test
    public void getResponse_emptyInput_returnsEmptyString() {
        assertEquals("", this.dave.getResponse(""));
        assertEquals("", this.dave.getResponse("   "));
        assertEquals("", this.dave.getResponse(null));
    }

    @Test
    public void getResponse_unknownCommand_returnsUnknownMessage() {
        assertEquals("I'm afraid I cannot understand you", this.dave.getResponse("blah"));
    }

    @Test
    public void getResponse_byeCommand_signalsExitAndReturnsGoodbye() {
        String response = this.dave.getResponse("bye");
        assertTrue(this.dave.isExit());
        assertEquals("The wind calls. Farewell!", response);
    }

    @Test
    public void getResponse_addTodo_returnsSuccessMessage() {
        String response = this.dave.getResponse("todo read book");
        assertEquals("added: [T][ ] read book", response);
    }

    @Test
    public void getResponse_invalidTodo_returnsErrorMessage() {
        String response = this.dave.getResponse("todo");
        assertEquals("NEGATIVE! The description of a todo cannot be empty", response);
    }

    @Test
    public void getResponse_listEmpty_returnsNoTasksMessage() {
        String response = this.dave.getResponse("list");
        assertEquals("There are no tasks in your list.", response);
    }

    @Test
    public void getResponse_listWithTasks_returnsFormattedTaskList() {
        this.dave.getResponse("todo borrow book");
        this.dave.getResponse("todo return book");
        String expected = "1. [T][ ] borrow book\n2. [T][ ] return book";
        assertEquals(expected, this.dave.getResponse("list"));
    }

    @Test
    public void getResponse_markAndUnmark_updatesStatusCorrectly() {
        this.dave.getResponse("todo read book");
        String markResponse = this.dave.getResponse("mark 1");
        assertEquals("Another one down!\n[T][X] read book", markResponse);

        String unmarkResponse = this.dave.getResponse("unmark 1");
        assertEquals("Negative progress...\n[T][ ] read book", unmarkResponse);
    }

    @Test
    public void getResponse_deleteTask_removesTaskAndConfirms() {
        this.dave.getResponse("todo buy milk");
        String deleteResponse = this.dave.getResponse("delete 1");
        String expected = "Affirmative! This task was removed:\n    [T][ ] buy milk";
        assertEquals(expected, deleteResponse);

        assertEquals("There are no tasks in your list.", this.dave.getResponse("list"));
    }

    @Test
    public void getResponse_findTask_returnsMatchingTasks() {
        this.dave.getResponse("todo read book");
        this.dave.getResponse("todo buy groceries");
        String findResponse = this.dave.getResponse("find book");
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book", findResponse);
    }
}
