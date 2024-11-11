package services;

import models.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link SessionsService} class
 * @author Hao
 */
class SessionsServiceTest {

    @Mock
    private Http.Request request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test getSessionId method when session id exists
     * @author Yongqi Hao
     */
    @Test
    void testGetSessionIdWhenSessionIdExists() {
        String existingSessionId = UUID.randomUUID().toString();
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.of(existingSessionId));

        String sessionId = SessionsService.getSessionId(request);

        assertEquals(existingSessionId, sessionId);
    }

    /**
     * Test getSessionId method when session id does not exist
     * @author Yongqi Hao
     */
    @Test
    void testGetSessionIdWhenSessionIdDoesNotExist() {
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.empty());

        String sessionId = SessionsService.getSessionId(request);

        assertNotNull(sessionId);
    }

    /**
     * Test hasSessionId method when session id exists
     * @author Yongqi Hao
     */
    @Test
    void testHasSessionIdWhenSessionIdExists() {
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.of("existingSessionId"));

        boolean hasSessionId = SessionsService.hasSessionId(request);
        assertTrue(hasSessionId);
    }

    /**
     * Test hasSessionId method when session id does not exist
     * @author Yongqi Hao
     */
    @Test
    void testHasSessionIdWhenSessionIdDoesNotExist() {
        Http.Session session = mock(Http.Session.class);
        when(request.session()).thenReturn(session);
        when(session.get("sessionId")).thenReturn(Optional.empty());

        boolean hasSessionId = SessionsService.hasSessionId(request);
        assertFalse(hasSessionId);
    }
}