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
    private final SentimentAnalyzer.Sentiment sentiment;

    /**
     * Constructor for SearchHistory
     * @param query The search query
     * @param results A list of VideoInfo objects containing video details
     * @param sentiment The sentiment of the search results
     * @author Dorreen Rostami
     */
    public SearchHistory(String query, List<VideoInfo> results, SentimentAnalyzer.Sentiment sentiment) {
        this.query = query;
        this.results = results;
        this.sentiment = sentiment;
    }

    /**
     * Constructor for SearchHistory
     * @param query The search query
     * @param results A list of VideoInfo objects containing video details
     * @author Yi Tian
     */
    public SearchHistory(String query, List<VideoInfo> results) {
        this.query = query;
        this.results = results;
        this.sentiment = SentimentAnalyzer.getSentiment(results.stream());
    }


    /**
     * Getters for SearchHistory
     * @return The corresponding field of the SearchHistory object
     * @author Dorreen Rostami
     */
    public String getQuery() {
        return query;
    }

    public String getSentimentEmoji() {
        return sentiment.emoji;
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
        SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.getSentiment(videoInfoList.stream());
        searchHistoryList.add(0, new SearchHistory(query, videoInfoList, sentiment));

        // limit to 10 most recent search histories
        if (searchHistoryList.size() > 10) {
            searchHistoryList = searchHistoryList.subList(0, 10);
        }

        return searchHistoryList;
    }
}
