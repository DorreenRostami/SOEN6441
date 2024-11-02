package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.YouTubeService;
import com.google.api.services.youtube.model.SearchResult;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeController extends Controller {

    private final YouTubeService youtubeService;

    @Inject
    public YouTubeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    public Result search(String query) {
        try {
            List<SearchResult> results = youtubeService.searchVideos(query);
            Html html = views.html.youtubeResults.render(results);
            return ok(html).as("text/html");
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }
}