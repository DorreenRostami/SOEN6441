package controllers;

import models.ChannelData;
import models.VideoData;
import play.mvc.Controller;
import play.mvc.Result;
import services.YouTubeService;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import views.html.hello;
import views.html.channel;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController extends Controller {

    private final YouTubeService youtubeService;
    private static List<VideoData> allSearchResults = new ArrayList<>();

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    // Clear search results and display homepage
    public Result hello() {
        allSearchResults.clear();
        return ok(hello.render("", allSearchResults));
    }

    // Search function, get the results of a new search and save them
    public Result search(String query) {
        try {
            List<SearchResult> results = youtubeService.searchVideos(query);

            // Convert each video result into a VideoData object
            List<VideoData> videoDataList = results.stream().map(result -> new VideoData(
                    result.getSnippet().getTitle(),
                    "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                    result.getSnippet().getChannelTitle(),
                    "channel?query=" + result.getSnippet().getChannelId(),
                    result.getSnippet().getChannelId(),
                    result.getSnippet().getThumbnails().getDefault().getUrl(),
                    result.getSnippet().getDescription()
            )).collect(Collectors.toList());

            // Add new results to the top of existing results
            allSearchResults.addAll(0, videoDataList);

            // keep 100
            if (allSearchResults.size() > 100) {
                allSearchResults = allSearchResults.subList(0, 100);
            }

            return ok(hello.render(query, allSearchResults));
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }

    public Result searchchannel(String channelId) {
        try {
            List<SearchResult> results = youtubeService.searchChannelVideos(channelId);

            // Convert each video result into a VideoData object
            List<VideoData> videoDataList = results.stream().map(result -> new VideoData(
                    result.getSnippet().getTitle(),
                    "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                    result.getSnippet().getChannelTitle(),
                    "channel?query=" + result.getSnippet().getChannelId(),
                    result.getSnippet().getChannelId(),
                    result.getSnippet().getThumbnails().getDefault().getUrl(),
                    result.getSnippet().getDescription()
            )).collect(Collectors.toList());

            // Add new results to the top of existing results
            allSearchResults.addAll(0, videoDataList);

            // keep 10
            if (allSearchResults.size() > 10) {
                allSearchResults = allSearchResults.subList(0, 10);
            }

            // Fetch channel details
            ChannelListResponse channelResponse = youtubeService.getChannelDetails(channelId);
            Channel channel = channelResponse.getItems().get(0);
            ChannelData channelData = getChannelData(channel);

            return ok(views.html.channel.render(channelId, allSearchResults, channelData));
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }

    private ChannelData getChannelData(Channel channel) {
        return new ChannelData(
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