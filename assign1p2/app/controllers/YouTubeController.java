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

            // 将结果转换为简单字符串格式用于显示
            String output = results.stream()
                    .map(result -> String.format("Title: %s\nChannel: %s\nDescription: %s\nThumbnail: %s\n\n",
                            result.getSnippet().getTitle(),
                            result.getSnippet().getChannelTitle(),
                            result.getSnippet().getDescription(),
                            result.getSnippet().getThumbnails().getDefault().getUrl()))
                    .collect(Collectors.joining("\n"));

            return ok(output).as("text/plain");  // 以纯文本格式返回
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }
}