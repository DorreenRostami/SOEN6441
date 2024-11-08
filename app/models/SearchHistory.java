package models;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import services.YouTubeService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<SearchHistory> addToSearchHistory(List<SearchHistory> searchHistoryList, String query, List<SearchResult> results, YouTubeService youTubeService) throws IOException {
        List<String> videoIds = results.stream()
                .map(result -> result.getId().getVideoId())
                .collect(Collectors.toList());

        List<Video> videoDetails = youTubeService.getVideoDetails(videoIds);
        List<VideoInfo> videoInfoList = videoDetails.stream().map(video -> new VideoInfo(
                video.getSnippet().getTitle(),
                "https://www.youtube.com/watch?v=" + video.getId(),
                video.getSnippet().getChannelTitle(),
                "/channel?query=" + video.getSnippet().getChannelId(),
                video.getSnippet().getThumbnails().getDefault().getUrl(),
                video.getSnippet().getDescription(),
                video.getSnippet().getTags()
        )).collect(Collectors.toList());

        searchHistoryList.add(0, new SearchHistory(query, videoInfoList));

        if (searchHistoryList.size() > 10) {
            searchHistoryList = searchHistoryList.subList(0, 10);
        }

        return searchHistoryList;
    }
}