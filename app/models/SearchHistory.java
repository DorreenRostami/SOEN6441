package models;

import services.SentimentAnalyzer;

import java.util.List;

/**
 * a class which contains a search query and the videos found for that query through the Youtube API
 * @author Dorreen Rostami
 */
public class SearchHistory {
    private final String query;
    private final List<VideoInfo> results;
    private SentimentAnalyzer.Sentiment sentiment;
    /**
     * Constructor for SearchHistory
     * @param query The search query
     * @param results A list of VideoInfo objects containing video details
     * @author Dorreen Rostami
     */
    public SearchHistory(String query, List<VideoInfo> results) {
        this.query = query;
        this.results = results;
    }

    public SentimentAnalyzer.Sentiment getSentiment(){
        return sentiment;
    }

    public void setSentiment(SentimentAnalyzer.Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Getters for SearchHistory
     * @return The corresponding field of the SearchHistory object
     * @author Dorreen Rostami
     */
    public String getQuery() {
        return query;
    }

    public List<VideoInfo> getResults() {
        return results;
    }

    /**
     * Generates an HTML string for displaying search results (and the query title if on results page)
     * @param showQuery a boolean indicating whether to display the query title and sentiment
     * @return a string containing the HTML representation
     * @author Hamza Asghar Khan
     * @author Dorreen Rostami - added query hyperlink to word statistics
     */
    public String getHTML(boolean showQuery){
        StringBuilder html = new StringBuilder();
        if (showQuery){
            html.append("<p class=\"query-title\">Search results for: <a href=\"#\" onclick=\"return onQueryStatsClick('").append(query).append("')\">").append(query).append("</a>")
                    .append("<span class=\"sentiment\">").append(sentiment != null ? sentiment.emoji : "").append("</span></p>");
        }
        for (VideoInfo videoInfo: results){
            html.append(videoInfo.getHTML());
        }
        html.append("<div class=\"separator\"></div>");
        return html.toString();
    }
}
