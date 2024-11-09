package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private Database database;

    @BeforeEach
    void setUp() {
        database = new Database();
    }

    @Test
    void testInitRecordWhenSessionExists() {
        String sessionId = "existingSession";
        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("query1", new ArrayList<>(), null));
        database.put(sessionId, searchHistory);

        // Ensure the record exists before init
        assertEquals(1, database.get(sessionId).size());

        // Initialize the record, should clear existing entries
        database.initRecord(sessionId);

        // Verify that the record for the session ID is now empty
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testInitRecordWhenSessionDoesNotExist() {
        String sessionId = "newSession";

        // Initialize the record for a new session ID
        database.initRecord(sessionId);

        // Verify that the record for the session ID exists and is empty
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testPutNewSession() {
        String sessionId = "newSession";
        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("query1", new ArrayList<>(), null));

        // Put a record for a new session ID
        database.put(sessionId, searchHistory);

        // Verify that the record is correctly saved
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("query1", result.get(0).getQuery());
    }

    @Test
    void testPutOverwriteExistingSession() {
        String sessionId = "existingSession";
        List<SearchHistory> initialHistory = new ArrayList<>();
        initialHistory.add(new SearchHistory("query1", new ArrayList<>(), null));
        database.put(sessionId, initialHistory);

        // Overwrite with a new list of search history
        List<SearchHistory> newHistory = new ArrayList<>();
        newHistory.add(new SearchHistory("query2", new ArrayList<>(), null));
        database.put(sessionId, newHistory);

        // Verify that the record was overwritten
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("query2", result.get(0).getQuery());
    }

    @Test
    void testGetWhenSessionExists() {
        String sessionId = "existingSession";
        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("query1", new ArrayList<>(), null));
        database.put(sessionId, searchHistory);

        // Retrieve and verify the record
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("query1", result.get(0).getQuery());
    }

    @Test
    void testGetWhenSessionDoesNotExist() {
        String sessionId = "nonExistingSession";

        // Retrieve and verify an empty list is returned for a non-existent session ID
        List<SearchHistory> result = database.get(sessionId);
        assertNotNull(result);
        assertEquals(0, result.size());

        // Verify that the session ID is now added to the data map with an empty list
        List<SearchHistory> storedResult = database.get(sessionId);
        assertSame(result, storedResult);
    }
}