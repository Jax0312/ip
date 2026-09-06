package dave.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Todo;

/**
 * Unit tests for the {@link Parser} class.
 */
public class ParserTest {

    @Test
    public void parseCommand_recognizedCommands_returnsMatchingEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline submit essay /by 2026-10-01"));
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting /from 2026-10-01 /to 2026-10-02"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.FIND, Parser.parseCommand("find book"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 2"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 2"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 3"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUnknownEnum() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("foobar"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
    }

    @Test
    public void parseTodo_validDescription_returnsTodo() {
        Todo todo = Parser.parseTodo("read documentation");
        assertEquals("read documentation", todo.getDescription());
    }

    @Test
    public void parseTodo_emptyDescription_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline("submit report /by 2026-11-20");
        assertEquals("submit report", deadline.getDescription());
    }

    @Test
    public void parseDeadline_missingByDelimiter_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseDeadline("submit report"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseDeadline(" /by 2026-11-20"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() {
        Event event = Parser.parseEvent("project meeting /from 2026-11-20 14:00 /to 2026-11-20 16:00");
        assertEquals("project meeting", event.getDescription());
    }

    @Test
    public void parseEvent_missingDelimiters_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseEvent("project meeting /from 2026-11-20"));
        assertThrows(DaveCommandException.class, () -> Parser.parseEvent("project meeting /to 2026-11-20"));
    }

    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseIndex("1"));
        assertEquals(4, Parser.parseIndex("5"));
    }

    @Test
    public void parseIndex_nonInteger_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseIndex("abc"));
        assertThrows(DaveCommandException.class, () -> Parser.parseIndex(""));
    }

    @Test
    public void parseFind_validKeyword_returnsTrimmedKeyword() {
        assertEquals("book", Parser.parseFind("book"));
        assertEquals("read book", Parser.parseFind("  read book  "));
    }

    @Test
    public void parseFind_emptyKeyword_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseFind(""));
        assertThrows(DaveCommandException.class, () -> Parser.parseFind("   "));
    }
}
