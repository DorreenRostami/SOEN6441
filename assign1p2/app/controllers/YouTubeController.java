package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.YouTubeService;
import com.google.api.services.youtube.model.SearchResult;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

public class YouTubeController extends Controller {

    private final YouTubeService youtubeService;

    @Inject
    public YouTubeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    public Result search(String query) {
        try {
            List<SearchResult> results = youtubeService.searchVideos(query);

            // Convert results to HTML format for display
            String output = results.stream()
                    .map(result -> {
                        String videoUrl = "https://www.youtube.com/watch?v=" + result.getId().getVideoId();
                        String channelUrl = "https://www.youtube.com/channel/" + result.getSnippet().getChannelId();
                        return String.format(
                                "<div style='margin-bottom: 20px;'>"
                                        + "<h3><a href='%s' target='_blank'>%s</a></h3>"  // Video title and link
                                        + "<p>Channel: <a href='%s' target='_blank'>%s</a></p>"  // Channel title and link
                                        + "<p>%s</p>"  // Video description
                                        + "<img src='%s' alt='Thumbnail' style='max-width: 200px; height: auto;'/>"  // Thumbnail
                                        + "</div>",
                                videoUrl,
                                result.getSnippet().getTitle(),
                                channelUrl,
                                result.getSnippet().getChannelTitle(),
                                result.getSnippet().getDescription(),
                                result.getSnippet().getThumbnails().getDefault().getUrl()
                        );
                    })
                    .collect(Collectors.joining("<hr/>"));  // Separate each video with a horizontal line

            // Return response with HTML content
            return ok("<html><body>" + output + "</body></html>").as("text/html");
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }
}