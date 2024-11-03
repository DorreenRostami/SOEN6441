package controllers;

import models.VideoInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.hello;
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
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

    // Clear search results and display homepage
    public Result hello() {
        allSearchResults.clear();
        return ok(hello.render("", allSearchResults));
    }

    // Get the results of a new search and save them
    public Result search(String query) {
        List<SearchResult> results;
        try {
            results = youtubeService.searchVideos(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        allSearchResults = results.stream().map(result -> new VideoInfo(
                result.getSnippet().getTitle(),
                "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                result.getSnippet().getChannelTitle(),
                "https://www.youtube.com/channel/" + result.getSnippet().getChannelId(),
                result.getSnippet().getThumbnails().getDefault().getUrl(),
                result.getSnippet().getDescription()
        )).toList();

        return ok(hello.render("", allSearchResults));
    }
}

