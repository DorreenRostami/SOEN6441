package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Database} class
 */
public class DatabaseTest {

    private Database database;

    /**
     * Initializes a new Database instance before each test
     */
    @BeforeEach
    void setUp() {
        database = new Database();
    }

    /**
     * Tests that initializing a session ID creates an empty record, and reinitializing clears the record if it exists.
     */
    @Test
    void testInitRecord() {
        String sessionId = "id";

        // Initialize a new session ID and check that it creates an empty record
        database.initRecord(sessionId);
        assertTrue(database.get(sessionId).isEmpty(), "Newly initialized session should be empty.");

        // Add search history data and reinitialize the same session ID to check that it clears the record
        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("query", new ArrayList<>(), null));
        database.put(sessionId, searchHistory);

        assertEquals(1, database.get(sessionId).size(), "Session should contain one search history entry before clearing.");

        // Reinitialize to check that it clears the record
        database.initRecord(sessionId);
        assertTrue(database.get(sessionId).isEmpty(), "Reinitialized session should be empty.");
    }

    /**
     * Tests that search history data is correctly added and retrieved for a session ID.
     */
    @Test
    void testPutAndGet() {
        String sessionId = "id";

        // Check that a new session ID returns an empty list
        assertTrue(database.get(sessionId).isEmpty(), "New session should initially have no entries.");

        // Add search history data for the session ID and retrieve it to verify
        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("queryy", new ArrayList<>(), null));
        database.put(sessionId, searchHistory);

        List<SearchHistory> retrievedSearchHistory = database.get(sessionId);
        assertEquals(1, retrievedSearchHistory.size(), "Session should contain one search history entry.");
        assertEquals("queryy", retrievedSearchHistory.get(0).getQuery(), "Query in search history should match the inserted query.");
    }

    /**
     * Tests the scenario where an existing session ID is retrieved before it has any entries added.
     */
    @Test
    void testGetEmptySession() {
        String sessionId = "newSession";

        // Retrieve an uninitialized session to confirm it initializes and returns an empty list
        List<SearchHistory> retrievedSearchHistory = database.get(sessionId);
        assertTrue(retrievedSearchHistory.isEmpty(), "Uninitialized session should return an empty list.");

        // Confirm that calling get() has initialized the session in the database
        assertTrue(database.get(sessionId).isEmpty(), "Session should be initialized and empty.");
    }
}