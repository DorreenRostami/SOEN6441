package controllers;

import models.SearchHistory;
import models.VideoInfo;
import models.ChannelInfo;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Tuple2;
import views.html.hello;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomeController extends Controller {
    private static List<SearchHistory> searchHistoryList = new ArrayList<>();

    private final YouTubeService youtubeService;

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    /**
     * method for showing the homepage with an empty search history
     * @return a CompletableFuture containing a result which renders the hello page
     * @author Hamza - initial implementation
     * @author Dorreen - made it asynchronous
     */
    public CompletionStage<Result> hello() {
        return CompletableFuture.supplyAsync(() -> {
            searchHistoryList.clear();
            return ok(hello.render(searchHistoryList));
        });
    }

    /**
     * Search for a query and asynchronously get the top 10 resulting videos from the Youtube API and append
     * the query and list of videos to the search history (which includes the 10 most recent queries and 10 videos
     * for each query, so 100 videos in total)
     *
     * @param query the query for which the videos are searched through
     * @return a CompletableFuture which includes the search history (queries until now and their top 10 videos)
     * @author Dorreen - implementation
     *
     * @author Hao - changed channelURL so that clicking on it opens a web page containing all available profile
     * information about a channel instead of opening the channel in Youtube
     */
    public CompletionStage<Result> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SearchResult> results = youtubeService.searchVideos(query);

                // Convert each result to a VideoInfo object
                List<VideoInfo> videoInfoList = results.stream().map(result -> new VideoInfo(
                        result.getSnippet().getTitle(),
                        "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                        result.getSnippet().getChannelTitle(),
                        "/channel?query=" + result.getSnippet().getChannelId(),
                        result.getSnippet().getThumbnails().getDefault().getUrl(),
                        result.getSnippet().getDescription()
                )).toList();

                // add the query and its results to the search history
                searchHistoryList.add(0, new SearchHistory(query, videoInfoList));

                // limit to 10 most recent searche histories
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

    public CompletionStage<Result> searchChannel(String channelId) {
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

    /**
     * For a search query (one or multiple keywords), display word-level statistics for the 50 latest
     * (less if fewer are available), counting all unique words in descending order (by frequency of the words)
     *
     * @param query the query for which the word statistics are computed
     * @return a CompletableFuture which includes a list of all the words found when searching the query
     * and the words' count with the list being sorted in descending order of count
     * @author Dorreen Rostami
     */
    public CompletionStage<Result> showStatistics(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SearchResult> results = youtubeService.searchVideos(query).stream()
                        .limit(50).toList();

                // get word frequency from video titles and descriptions
                Map<String, Long> wordCount = results.stream()
                        .flatMap(result -> Stream.of(
                                result.getSnippet().getTitle(),
                                result.getSnippet().getDescription()
                        ))
                        .filter(s -> !Objects.equals(s, ""))
                        .flatMap(text -> Arrays.stream(text.split(" ")))
                        .map(String::toLowerCase)
                        .filter(word -> !word.equals(""))
                        .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

                // sort in descending order
                List<Tuple2<String, Long>> sortedWordCount = wordCount.entrySet().stream()
                        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                        .map(entry -> new Tuple2<>(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

                return ok(views.html.statistics.render(query, sortedWordCount));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }

}
