package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Tuple2;
import services.*;
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
    private static final Database database = new Database();
    private final Cache cache;
    private final YouTubeService youtubeService;

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
        cache = new Cache(youtubeService);
    }

    /**
     * redicrects the / route to /ytlytis route (typing in localhost:9000 will redirect to localhost:9000/ytlytics
     * which is the main search page)
     *
     * @author - Dorreen Rostami
     */
    public Result redirectToYtLytics() {
        return redirect(routes.HomeController.hello());
    }

    /**
     * method for showing the homepage with an empty search history
     * @return a CompletableFuture containing a result which renders the hello page
     * @author Hamza - initial implementation
     * @author Dorreen - made it asynchronous
     */
    public CompletionStage<Result> hello(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            String sessionId = SessionsService.getSessionId(request);
            database.initRecord(sessionId);
            if (SessionsService.hasSessionId(request)){
                return ok(hello.render(database.get(sessionId)));
            }
            return ok(hello.render(database.get(sessionId))).addingToSession(request, "sessionId", sessionId);
        });
    }

    /**
     * Search for a query and asynchronously get the top 10 resulting videos from the Youtube API and append
     * the query and list of videos to the search history (which includes the 10 most recent queries and 10 videos
     * for each query, so 100 videos in total)
     *
     * @param query the query for which the videos are searched through
     * @return a CompletableFuture which includes the search history (queries until now and their top 10 videos)
     * @author Dorreen Rostami
     */
    public CompletionStage<Result> search(Http.Request request, String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String sessionId = SessionsService.getSessionId(request);
                List<SearchResult> results = cache.get(query, false);
                List<SearchHistory> searchHistory = SearchHistory.addToSearchHistory(database.get(sessionId), query, results, cache);
                database.put(sessionId, searchHistory);
                Result response = ok(hello.render(searchHistory));
                if (!SessionsService.hasSessionId(request)){
                    response = response.addingToSession(request, "sessionId", sessionId);
                }
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }

    /**
     * Provides a page containing all the information about the request channel.
     * @param channelId Id of the target channel
     * @return a CompletableFuture containing the webpage.
     * @author Dorreen - initial implementation
     * @author Hao
     */
    public CompletionStage<Result> searchChannel(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch videos for the channel
                List<VideoInfo> videoInfoList = ChannelService.searchChannel(channelId, cache);

                // Fetch channel details
                ChannelListResponse channelResponse = cache.getChannelDetails(channelId);
                Channel channel = channelResponse.getItems().get(0);
                ChannelInfo channelInfo = ChannelService.getChannelInfo(channel);

                // Render and return the response
                return ok(views.html.channel.render(channelId, videoInfoList, channelInfo));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
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
                List<SearchResult> results = cache.get(query, false).stream()
                        .limit(50)
                        .collect(Collectors.toList());

                List<String> resultText = results.stream()
                        .flatMap(result -> Stream.of(
                                result.getSnippet().getTitle(),
                                result.getSnippet().getDescription()
                        ))
                        .collect(Collectors.toList());

                List<Tuple2<String, Long>> sortedWordCount = WordStatistics.getWordStats(resultText);

                return ok(views.html.statistics.render(query, sortedWordCount));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }
}