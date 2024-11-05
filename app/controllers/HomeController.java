package controllers;

import models.SearchHistory;
import models.VideoInfo;
import models.ChannelInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.hello;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;

public class HomeController extends Controller {
    private static List<SearchHistory> searchHistoryList = new ArrayList<>();

    private final YouTubeService youtubeService;

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    // Clear search results and display homepage asynchronously
    public CompletionStage<Result> hello() {
        return CompletableFuture.supplyAsync(() -> {
            searchHistoryList.clear();
            return ok(hello.render(searchHistoryList));
        });
    }

    // Get the results of a new search and save them asynchronously
    public CompletionStage<Result> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SearchResult> results = youtubeService.searchVideos(query);

                // Convert each result to a VideoInfo object
                List<VideoInfo> videoDataList = results.stream().map(result -> new VideoInfo(
                        result.getSnippet().getTitle(),
                        "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                        result.getSnippet().getChannelTitle(),
                        "/channel?query=" + result.getSnippet().getChannelId(),
                        result.getSnippet().getThumbnails().getDefault().getUrl(),
                        result.getSnippet().getDescription()
                )).toList();

                // Add the query and its results to the search history
                searchHistoryList.add(0, new SearchHistory(query, videoDataList));

                // Limit to the 10 most recent searches
                if (searchHistoryList.size() > 10) {
                    searchHistoryList = searchHistoryList.subList(0, 10);
                }

                return ok(hello.render(searchHistoryList));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }

    public CompletionStage<Result> searchchannel(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch videos for the channel
                List<SearchResult> results = youtubeService.searchChannelVideos(channelId);

                // Convert each video result into a VideoData object
                List<VideoInfo> videoInfoList = results.stream().map(result -> new VideoInfo(
                        result.getSnippet().getTitle(),
                        "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                        result.getSnippet().getChannelTitle(),
                        "channel?query=" + result.getSnippet().getChannelId(),
                        result.getSnippet().getThumbnails().getDefault().getUrl(),
                        result.getSnippet().getDescription()
                )).collect(Collectors.toList());

                // Keep only the 10 most recent results
                if (videoInfoList.size() > 10) {
                    videoInfoList.subList(0, 10);
                }

                // Fetch channel details
                ChannelListResponse channelResponse = youtubeService.getChannelDetails(channelId);
                Channel channel = channelResponse.getItems().get(0);
                ChannelInfo channelInfo = getChannelInfo(channel);

                // Render and return the response
                return ok(views.html.channel.render(channelId, videoInfoList, channelInfo));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }

    private ChannelInfo getChannelInfo(Channel channel) {
        return new ChannelInfo(
                channel.getSnippet().getTitle(),
                channel.getId(),
                "https://www.youtube.com/channel/" + channel.getId(),
                channel.getSnippet().getThumbnails().getDefault().getUrl(),
                channel.getSnippet().getDescription(),
                channel.getStatistics().getSubscriberCount().longValue(),
                channel.getStatistics().getVideoCount().longValue(),
                channel.getStatistics().getViewCount().longValue()
        );
    }
}
