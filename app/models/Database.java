package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This model maintains all the searches that have been carried out by all the users on the server.
 * @author Hamza Asghar Khan
 */
public class Database {
    /**
     * Maps SessionIds to their associated list of SearchHistory objects.
     */
    private final Map<String, List<SearchHistory>> data = new HashMap<>();

    /**
     * Initializes the record for the provided SessionId.
     * @param sessionId Target SessionId
     * @author Hamza Asghar Khan
     */
    public void initRecord(String sessionId){
        if (data.containsKey(sessionId)){
            data.get(sessionId).clear();
        } else {
            data.put(sessionId, new ArrayList<SearchHistory>());
        }
    }

    /**
     * Places the provided list of SearchHistory objects at the record for the provided SessionId
     * @param sessionId Target SessionId
     * @param searchResults List of SearchHistory objects to be mapped against the provided sessionId
     * @author Hamza Asghar Khan
     */
    public void put(String sessionId, List<SearchHistory> searchResults){
        data.put(sessionId, searchResults);
    }

    /**
     * Provides the list of SearchHistory objects recorded against a specific SessionId
     * @param sessionId Target SessionId
     * @return List of SearchHistory objects recorded against the provided SessionId
     * @author Hamza Asghar Khan
     */
    public List<SearchHistory> get(String sessionId){
        if (data.containsKey(sessionId)){
            return data.get(sessionId);
        } else {
            List<SearchHistory> emptyList = new ArrayList<SearchHistory>();
            data.put(sessionId, emptyList);
            return emptyList;
        }
    }
}
