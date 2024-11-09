package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Database} class
 * @author Dorreen Rostami
 */
public class DatabaseTest {

    private Database database;

    /**
     * make a new instance before each test
     * @author Dorreen Rostami
     */
    @BeforeEach
    void setUp() {
        database = new Database();
    }

    /**
     * Tests that a new session ID is initialized correctly. When a new session is started, it should have an
     * empty arrayList, regardless of whether the session ID already existed or not.
     * @author Dorreen Rostami
     */
    @Test
    void testInitRecord() {
        String sessionId = "id";
        database.initRecord(sessionId);
        assertTrue(database.get(sessionId).isEmpty());

        List<SearchHistory> sh = new ArrayList<>();
        sh.add(new SearchHistory("query", new ArrayList<>(), null));
        database.put(sessionId, sh);
        database.initRecord(sessionId);
        assertTrue(database.get(sessionId).isEmpty());
    }

    /**
     * Tests that search history data is correctly added for a session ID.
     * @author Dorreen Rostami
     */
    @Test
    void testPutAndGet() {
        String sessionId = "id";
        List<SearchHistory> sh = new ArrayList<>();
        sh.add(new SearchHistory("queryy", new ArrayList<>(), null));
        database.put(sessionId, sh);
        List<SearchHistory> retrievedSearchHistory = database.get(sessionId);
        assertEquals(1, retrievedSearchHistory.size());
        assertEquals("queryy", retrievedSearchHistory.get(0).getQuery());
    }
}
