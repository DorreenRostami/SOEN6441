package models;

import com.google.api.services.youtube.model.SearchResult;
import java.util.List;

/**
 * a class which contains a search query and the videos found for that query through the Youtube API
 * @author Dorreen Rostami
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

    /**
     * Append the results found from the Youtube API to the search history (which includes the 10 most
     * recent queries and 10 videos for each query, so 100 videos in total)
     *
     * @param searchHistoryList The current list of search history to which the new entry will be added
     * @param query The search query
     * @param results A list of Youtube SearchResult objects containing video details
     * @return The updated searchHistoryList containing the new entries
     * @author Dorreen Rostami - implementation
     *
     * @author Hao - changed channelURL so that clicking on it opens a web page containing all available profile
     * information about a channel instead of opening the channel in Youtube
     */
    public static List<SearchHistory> addToSearchHistory(List<SearchHistory> searchHistoryList, String query, List<SearchResult> results){
        List<VideoInfo> videoInfoList = results.stream().map(result -> new VideoInfo(
                result.getSnippet().getTitle(),
                "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                result.getSnippet().getChannelTitle(),
                "/channel?query=" + result.getSnippet().getChannelId(),
                result.getSnippet().getThumbnails().getDefault().getUrl(),
                result.getSnippet().getDescription()
        )).toList();

        searchHistoryList.add(0, new SearchHistory(query, videoInfoList));

        // limit to 10 most recent search histories
        if (searchHistoryList.size() > 10) {
            searchHistoryList = searchHistoryList.subList(0, 10);
        }

        return searchHistoryList;
    }
}
