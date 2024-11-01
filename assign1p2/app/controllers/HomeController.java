package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.YouTubeService;
import com.google.api.services.youtube.model.SearchResult;
import views.html.hello;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController extends Controller {

    private final YouTubeService youtubeService;

    // 静态列表用于保存搜索结果，在多次搜索时累积
    private static List<String> allSearchResults = new ArrayList<>();

    @Inject
    public HomeController() throws GeneralSecurityException, IOException {
        this.youtubeService = new YouTubeService();
    }

    // 主页：清空搜索结果并显示空页面
    public Result hello() {
        allSearchResults.clear(); // 清空搜索结果
        return ok(hello.render("", allSearchResults)); // 显示空页面
    }

    // 搜索功能：将新的搜索结果添加到现有结果中
    public Result search(String query) {
        try {
            // 获取新的搜索结果
            List<SearchResult> results = youtubeService.searchVideos(query);

            // 将新的结果转换为字符串格式
            List<String> videoDetails = results.stream()
                    .map(result -> String.format("Title: %s\nChannel: %s\nDescription: %s\nThumbnail: %s",
                            result.getSnippet().getTitle(),
                            result.getSnippet().getChannelTitle(),
                            result.getSnippet().getDescription(),
                            result.getSnippet().getThumbnails().getDefault().getUrl()))
                    .collect(Collectors.toList());

            // 新的结果添加到列表顶部
            allSearchResults.addAll(0, videoDetails);

            // 保留最新的 100 个项目
            if (allSearchResults.size() > 100) {
                allSearchResults = allSearchResults.subList(0, 100);
            }

            return ok(hello.render(query, allSearchResults));
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError("Error fetching data from YouTube API");
        }
    }
}