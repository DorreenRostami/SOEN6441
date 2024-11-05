package controllers;

import models.VideoInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.hello;
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class HomeController extends Controller {
    private static List<VideoInfo> allSearchResults = new ArrayList<>();

    private final YouTubeService youtubeService;

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    // Clear search results and display homepage asynchronously
    public CompletionStage<Result> hello() {
        return CompletableFuture.supplyAsync(() -> {
            allSearchResults.clear();
            return ok(hello.render("", allSearchResults));
        });
    }

    // Get the results of a new search and save them asynchronously
    public CompletionStage<Result> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SearchResult> results = youtubeService.searchVideos(query);

                // Convert each result into a VideoInfo
                List<VideoInfo> videoDataList = results.stream().map(result -> new VideoInfo(
                        result.getSnippet().getTitle(),
                        "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                        result.getSnippet().getChannelTitle(),
                        "https://www.youtube.com/channel/" + result.getSnippet().getChannelId(),
                        result.getSnippet().getThumbnails().getDefault().getUrl(),
                        result.getSnippet().getDescription()
                )).toList();

                // Add new results to the top of existing results
                allSearchResults.addAll(0, videoDataList);

                // Keep only the 100 most recent results
                if (allSearchResults.size() > 100) {
                    allSearchResults = allSearchResults.subList(0, 100);
                }

                return ok(hello.render(query, allSearchResults));
            } catch (IOException e) {
                e.printStackTrace();
                return internalServerError("Error fetching data from YouTube API");
            }
        });
    }
}
