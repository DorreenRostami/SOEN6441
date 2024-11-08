package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionsServiceTest {

    @Mock
    private Http.Request request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSessionIdWhenSessionIdExists() {
        // Mock a request with an existing session ID
        String existingSessionId = UUID.randomUUID().toString();
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.of(existingSessionId));

        // Call the method
        String sessionId = SessionsService.getSessionId(request);

        // Verify the session ID is the same as the existing one
        assertEquals(existingSessionId, sessionId);
    }

    @Test
    void testGetSessionIdWhenSessionIdDoesNotExist() {
        // Mock a request with no session ID
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.empty());

        // Call the method
        String sessionId = SessionsService.getSessionId(request);

        // Verify a new session ID is generated
        assertNotNull(sessionId);
    }

    @Test
    void testHasSessionIdWhenSessionIdExists() {
        // Mock a request with an existing session ID
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.of("existingSessionId"));

        // Call the method
        boolean hasSessionId = SessionsService.hasSessionId(request);

        // Verify the result is true
        assertTrue(hasSessionId);
    }

    @Test
    void testHasSessionIdWhenSessionIdDoesNotExist() {
        // Mock a request with no session ID
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.empty());

        // Call the method
        boolean hasSessionId = SessionsService.hasSessionId(request);

        // Verify the result is false
        assertFalse(hasSessionId);
    }
}