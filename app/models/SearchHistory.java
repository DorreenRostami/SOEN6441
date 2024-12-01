package models;

import com.google.api.services.youtube.model.SearchResult;
import services.SentimentAnalyzer;

import java.io.IOException;
import java.util.stream.Collectors;
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
     * Append the results found from the YouTube API to the search history (which includes the 10 most
     * recent queries and 10 videos for each query, so 100 videos in total)
     *
     * @param searchHistoryList The current list of search history to which the new entry will be added
     * @param query The search query
     * @param results A list of YouTube SearchResult objects containing video details
     * @return The updated searchHistoryList containing the new entries
     * @author Dorreen Rostami - implementation
     *
     * @author Hao - changed channelURL so that clicking on it opens a web page containing all available profile
     * information about a channel instead of opening the channel in YouTube
     *
     * @author Hamza Asghar Khan - Updated the video description to get the full description and not a snippet
     */
    public static List<SearchHistory> addToSearchHistory(List<SearchHistory> searchHistoryList, String query, List<SearchResult> results, Cache cache){
        List<VideoInfo> videoInfoList = results.stream().map(result -> {
            String videoId = result.getId().getVideoId();

            //get full description instead of a snippet of it
            String description = "";
            try {
                description = cache.getDescription(videoId);
            } catch (IOException e){
                System.out.println("Unable to fetch description for videoId: " + videoId);
            }
            return new VideoInfo(result, description);
        }).collect(Collectors.toList());

        // limit to 10 most recent search histories
        if (searchHistoryList.size() > 10) {
            searchHistoryList = searchHistoryList.subList(0, 10);
        }

        return searchHistoryList;
    }

    /**
     * Replace the results of one of the queries in the search history with new results
     *
     * @param searchHistoryList The current list of search history to which the new entry will be added
     * @param query The search query
     * @param results A list of YouTube SearchResult which will replace the old ones
     * @return The updated searchHistoryList containing the new entries
     * @author Dorreen Rostami
     */
    public static List<SearchHistory> editSearchHistory(List<SearchHistory> searchHistoryList, String query, List<SearchResult> results, Cache cache){
        List<VideoInfo> videoInfoList = results.stream().map(result -> {
            String videoId = result.getId().getVideoId();
            String description = "";
            try {
                description = cache.getDescription(videoId);
            } catch (IOException e){
                System.out.println("Unable to fetch description for videoId: " + videoId);
            }
            return new VideoInfo(result, description);
        }).collect(Collectors.toList());

        for (int i = 0; i < searchHistoryList.size(); i++) {
            if (searchHistoryList.get(i).getQuery().equalsIgnoreCase(query)) {
                searchHistoryList.set(i, new SearchHistory(query, videoInfoList));
                break;
            }
        }
        return searchHistoryList;
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
