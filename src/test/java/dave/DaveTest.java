package dave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    private Path filePath;

    @BeforeEach
    public void setUp() {
        this.filePath = this.tempDir.resolve("test-dave.txt");
        this.dave = new Dave(this.filePath.toString());
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
    public void getResponse_addDuplicateTodo_returnsErrorMessage() {
        this.dave.getResponse("todo read book");
        String duplicateResponse = this.dave.getResponse("todo read book");
        assertTrue(duplicateResponse.startsWith("NEGATIVE! This task already exists in your list:"));
        assertTrue(duplicateResponse.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_addDuplicateDeadline_returnsErrorMessage() {
        this.dave.getResponse("deadline return book /by 2026-10-01");
        String duplicateResponse = this.dave.getResponse("deadline return book /by 2026-10-01");
        assertTrue(duplicateResponse.startsWith("NEGATIVE! This task already exists in your list:"));
    }

    @Test
    public void getResponse_addDuplicateEvent_returnsErrorMessage() {
        this.dave.getResponse("event party /from 2026-10-01 18:00 /to 2026-10-01 22:00");
        String duplicateResponse = this.dave.getResponse(
                "event party /from 2026-10-01 18:00 /to 2026-10-01 22:00");
        assertTrue(duplicateResponse.startsWith("NEGATIVE! This task already exists in your list:"));
    }

    @Test
    public void getResponse_eventStartAfterEnd_returnsErrorMessage() {
        String response = this.dave.getResponse("event meeting /from 2026-10-05 16:00 /to 2026-10-05 14:00");
        assertTrue(response.startsWith("NEGATIVE!"));
        assertTrue(response.contains("strictly earlier than end date/time"));
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
    public void getResponse_findTask_returnsMatchingTasksWithOriginalIndices() {
        this.dave.getResponse("todo buy groceries");
        this.dave.getResponse("todo read book");
        this.dave.getResponse("deadline return book /by 2026-10-01");

        String findResponse = this.dave.getResponse("find book");
        String expected = "Here are the matching tasks in your list:\n"
                + "2. [T][ ] read book\n"
                + "3. [D][ ] return book (by: Oct 01 2026)";
        assertEquals(expected, findResponse);
    }

    @Test
    public void getResponse_snoozeDefault_postponesDeadlineByOneDay() {
        this.dave.getResponse("deadline submit essay /by 2026-10-01");
        String snoozeResponse = this.dave.getResponse("snooze 1");
        String expected = "Affirmative! I've snoozed this task:\n    [D][ ] submit essay (by: Oct 02 2026)";
        assertEquals(expected, snoozeResponse);
    }

    @Test
    public void getResponse_snoozeRelative_shiftsDeadlineBySpecifiedDays() {
        this.dave.getResponse("deadline submit essay /by 2026-10-01 18:00");
        String snoozeResponse = this.dave.getResponse("snooze 1 3 days");
        String expected = "Affirmative! I've snoozed this task:\n    [D][ ] submit essay (by: Oct 04 2026 18:00)";
        assertEquals(expected, snoozeResponse);
    }

    @Test
    public void getResponse_snoozeTargetDateTime_updatesDeadlineDate() {
        this.dave.getResponse("deadline submit essay /by 2026-10-01");
        String snoozeResponse = this.dave.getResponse("snooze 1 /to 2026-10-15 20:00");
        String expected = "Affirmative! I've snoozed this task:\n    [D][ ] submit essay (by: Oct 15 2026 20:00)";
        assertEquals(expected, snoozeResponse);
    }

    @Test
    public void getResponse_snoozeEventReschedule_updatesEventBoundaries() {
        this.dave.getResponse("event project meeting /from 2026-10-01 10:00 /to 2026-10-01 12:00");
        String snoozeResponse = this.dave.getResponse("snooze 1 /from 2026-10-05 14:00 /to 2026-10-05 16:00");
        String expected = "Affirmative! I've snoozed this task:\n    "
                + "[E][ ] project meeting (from: Oct 05 2026 14:00 to: Oct 05 2026 16:00)";
        assertEquals(expected, snoozeResponse);
    }

    @Test
    public void getResponse_snoozeTodo_returnsErrorMessage() {
        this.dave.getResponse("todo read book");
        String snoozeResponse = this.dave.getResponse("snooze 1");
        assertEquals("NEGATIVE! Todo tasks cannot be snoozed as they have no date or time.", snoozeResponse);
    }

    @Test
    public void getResponse_snoozeInvalidIndex_returnsWrongNumber() {
        String snoozeResponse = this.dave.getResponse("snooze 5");
        assertEquals("Wrong number!", snoozeResponse);
    }

    @Test
    public void getResponse_snoozePersists_newDaveInstanceLoadsUpdatedTask() {
        this.dave.getResponse("deadline return book /by 2026-10-01");
        this.dave.getResponse("snooze 1 2 days");

        Dave newDave = new Dave(this.filePath.toString());
        String listResponse = newDave.getResponse("list");
        assertEquals("1. [D][ ] return book (by: Oct 03 2026)", listResponse);
    }

    @Test
    public void getWelcome_corruptedLinesInFile_showsWarningInWelcome() throws IOException {
        Files.write(this.filePath, List.of("T | 0 | valid todo", "garbage unparseable line"));
        Dave corruptDave = new Dave(this.filePath.toString());
        String welcome = corruptDave.getWelcome();
        assertTrue(welcome.contains("Warning: 1 corrupted task entry(ies)"));
    }

    @Test
    public void isErrorResponse_negativePrefix_returnsTrue() {
        assertTrue(this.dave.isErrorResponse("NEGATIVE! The description of a todo cannot be empty"));
    }

    @Test
    public void isErrorResponse_wrongNumber_returnsTrue() {
        assertTrue(this.dave.isErrorResponse("Wrong number!"));
    }

    @Test
    public void isErrorResponse_unknownCommandMessage_returnsTrue() {
        assertTrue(this.dave.isErrorResponse("I'm afraid I cannot understand you"));
    }

    @Test
    public void isErrorResponse_warningPrefix_returnsTrue() {
        assertTrue(this.dave.isErrorResponse("Warning: Unable to load tasks from disk: File not found"));
    }

    @Test
    public void isErrorResponse_successfulResponse_returnsFalse() {
        assertFalse(this.dave.isErrorResponse("added: [T][ ] read book"));
        assertFalse(this.dave.isErrorResponse("Another one down!\n[T][X] read book"));
        assertFalse(this.dave.isErrorResponse("Affirmative! I've snoozed this task: ..."));
    }

    @Test
    public void isErrorResponse_nullOrEmptyResponse_returnsFalse() {
        assertFalse(this.dave.isErrorResponse(null));
        assertFalse(this.dave.isErrorResponse(""));
        assertFalse(this.dave.isErrorResponse("   "));
    }
}
