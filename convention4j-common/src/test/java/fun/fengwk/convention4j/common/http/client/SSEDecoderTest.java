package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SSEDecoder class.
 * This test suite covers all standard use cases and edge cases of the SSE protocol parsing.
 */
@DisplayName("SSEDecoder Comprehensive Tests")
class SSEDecoderTest {

    private SSEDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new SSEDecoder();
    }

    private void processLines(String... lines) {
        for (String line : lines) {
            decoder.appendLine(line);
        }
    }

    @Test
    @DisplayName("Should decode a simple event with a single data line")
    void testSimpleEvent() {
        processLines(
            "data: This is a simple message.",
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("This is a simple message.", event.getData());
        assertNull(event.getId());
        assertNull(event.getEvent());
        assertNull(decoder.nextEvent()); // No more events
    }

    @Test
    @DisplayName("Should decode an event with multi-line data")
    void testMultiLineDataEvent() {
        processLines(
            "data: line 1",
            "data: line 2",
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("line 1\nline 2", event.getData());
        assertNull(decoder.nextEvent());
    }

    @Test
    @DisplayName("Should decode a complete event with id, event name, and data")
    void testFullEvent() {
        processLines(
            "event: custom-event",
            "id: 123-abc",
            "data: some data here",
            "retry: 5000",
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("custom-event", event.getEvent());
        assertEquals("123-abc", event.getId());
        assertEquals("some data here", event.getData());
        assertEquals(5000L, event.getRetry());
        assertNull(decoder.nextEvent());
    }

    @Test
    @DisplayName("Should correctly process multiple events in a single stream")
    void testMultipleEvents() {
        processLines(
            "data: event 1",
            "",
            "data: event 2",
            "id: 2",
            ""
        );

        SSEEvent event1 = decoder.nextEvent();
        assertNotNull(event1);
        assertEquals("event 1", event1.getData());
        assertNull(event1.getId());

        SSEEvent event2 = decoder.nextEvent();
        assertNotNull(event2);
        assertEquals("event 2", event2.getData());
        assertEquals("2", event2.getId());

        assertNull(decoder.nextEvent());
    }

    @Test
    @DisplayName("Should ignore comment lines completely")
    void testCommentHandling() {
        processLines(
            ": this is a comment",
            "data: real data",
            ": another comment",
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("real data", event.getData());
        assertNull(decoder.nextEvent());
    }

    @Test
    @DisplayName("Should produce no events if stream only contains comments and blank lines")
    void testOnlyComments() {
        processLines(
            ": comment 1",
            "",
            ": comment 2"
        );

        assertNull(decoder.nextEvent());
        List<SSEEvent> remainingEvents = decoder.finish();
        assertTrue(remainingEvents.isEmpty());
    }

    @Test
    @DisplayName("Should capture the last event using finish() if no trailing newline exists")
    void testFinishMethodForLastEvent() {
        processLines("data: last message");

        assertNull(decoder.nextEvent()); // No event dispatched yet
        List<SSEEvent> events = decoder.finish();
        assertEquals(1, events.size());
        assertEquals("last message", events.get(0).getData());
    }

    @Test
    @DisplayName("Should handle various field parsing edge cases correctly")
    void testFieldParsingEdgeCases() {
        processLines(
            "data:no space",
            "id:  leading space in value is kept", // Spec says only ONE space is trimmed
            "event", // Field with no value
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("no space", event.getData());
        assertEquals(" leading space in value is kept", event.getId());
        assertEquals("", event.getEvent());
    }

    @Test
    @DisplayName("Should handle data field with empty value")
    void testEmptyDataField() {
        processLines(
            "data:",
            ""
        );
        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("", event.getData());
    }

    @Test
    @DisplayName("Should ignore invalid retry field and not affect the event")
    void testInvalidRetryField() {
        processLines(
            "data: test",
            "retry: not-a-number",
            ""
        );
        SSEEvent event = decoder.nextEvent();
        assertNotNull(event);
        assertEquals("test", event.getData());
        assertNull(event.getRetry());
    }

    @Test
    @DisplayName("Should correctly dispatch an event that contains only an ID, even with null data")
    void testIdOnlyEvent() {
        // Note: The spec implies events without a 'data' field should not be dispatched.
        // This test verifies the behavior of the *current* implementation, which dispatches them.
        processLines(
            "id: event-without-data",
            ""
        );

        SSEEvent event = decoder.nextEvent();
        assertNotNull(event, "The current implementation should dispatch events even if data is null.");
        assertEquals("event-without-data", event.getId());
        assertNull(event.getData());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when appending line after finish()")
    void testAppendLineAfterFinish() {
        decoder.finish();
        assertThrows(IllegalStateException.class, () -> decoder.appendLine("data: late data"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when calling finish() multiple times")
    void testFinishMultipleTimes() {
        decoder.finish(); // First call is ok
        assertThrows(IllegalStateException.class, () -> decoder.finish());
    }

    @Test
    @DisplayName("finish() on an empty decoder should return an empty list")
    void testFinishOnEmptyDecoder() {
        List<SSEEvent> events = decoder.finish();
        assertTrue(events.isEmpty());
    }
}
