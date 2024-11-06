package models;

import java.util.List;

/**
 * a class which contains a search query and the videos found for that query through the Youtube API
 * @author Dorreen
 */
public class SearchHistory {
    private final String query;
    private final List<VideoInfo> results;

    public SearchHistory(String query, List<VideoInfo> results) {
        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public List<VideoInfo> getResults() {
        return results;
    }
}
