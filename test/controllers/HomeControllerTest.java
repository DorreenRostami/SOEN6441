package controllers;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static play.mvc.Results.ok;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchResult;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import services.*;
import views.html.hello;
import views.html.videoDetails;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import static play.inject.Bindings.bind;

import com.google.api.services.youtube.model.*;


class HomeControllerTest extends WithApplication {

    @Mock private Cache cache;
    @Mock private YouTubeService youtubeService;
    @Mock private VideoDetailSevice videoDetailSevice;
    @Mock private Http.Request request;
    @Mock private Database database;
    @Mock private Channel channel;
    @Mock private ChannelListResponse channelListResponse;
    @Mock private Video video;

    @InjectMocks private HomeController homeController;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(YouTubeServiceInterface.class).to(MockYouTubeService.class))
                .build();
    }

    @BeforeEach
    void setup() throws GeneralSecurityException, IOException {
        MockitoAnnotations.openMocks(this);
        homeController = new HomeController(youtubeService, cache, videoDetailSevice);
    }

    @Test
    void testRedirectToYtLytics() {
        Result result = homeController.redirectToYtLytics();
        assertEquals(303, result.status());
    }

    @Test
    void testShowStatistics() throws IOException {
        String query = "statistics query";

        when(cache.get(query, false)).thenReturn(Collections.emptyList());

        CompletionStage<Result> resultStage = homeController.showStatistics(query);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(200, result.status());
    }

    @Test
    void testSearchByTag() {
        String tag = "sampleTag";

        CompletionStage<Result> resultStage = homeController.searchByTag(tag);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(200, result.status());
    }

    @Test
    void testShowStatisticsWithIOException() throws IOException {
        String query = "statistics query";

        when(cache.get(query, false)).thenThrow(new IOException("Error fetching data"));

        CompletionStage<Result> resultStage = homeController.showStatistics(query);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(500, result.status());
    }

    @Test
    void testShowVideoDetailsWithIOException() throws IOException {
        String videoId = "video123";

        when(cache.getVideo(videoId)).thenThrow(new IOException("Error fetching data"));

        CompletionStage<Result> resultStage = homeController.showVideoDetails(videoId);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(500, result.status());
    }

    @Test
    void testSearchChannelSuccess() throws IOException {
        String channelId = "testChannelId";
        List<VideoInfo> videoInfoList = Collections.singletonList(
                new VideoInfo("Sample Video Title", "https://www.youtube.com/watch?v=sampleVideo",
                        "Sample Channel Title", "/channel?query=testChannelId",
                        "https://img.youtube.com/vi/sample/hqdefault.jpg", "Sample description", "tags"));

        when(cache.getChannelDetails(channelId)).thenReturn(channelListResponse);
        when(channelListResponse.getItems()).thenReturn(List.of(channel));

        try (MockedStatic<ChannelService> mockedChannelService = mockStatic(ChannelService.class)) {
            mockedChannelService.when(() -> ChannelService.getChannelInfo(channel))
                    .thenReturn(new ChannelInfo("Mock Channel", "mockChannelId", "https://www.youtube.com/channel/mockChannelId", "https://img.youtube.com/vi/sample/hqdefault.jpg", "This is a test channel description", 1000L, 100L, 5000L));
            mockedChannelService.when(() -> ChannelService.searchChannel(channelId, cache))
                    .thenReturn(videoInfoList);

            CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
            Result result = resultStage.toCompletableFuture().join();

            assertEquals(200, result.status());
            verify(cache, times(1)).getChannelDetails(channelId);
            mockedChannelService.verify(() -> ChannelService.getChannelInfo(channel), times(1));
            mockedChannelService.verify(() -> ChannelService.searchChannel(channelId, cache), times(1));
        }
    }

    @Test
    void testSearchChannelIOException() throws IOException {
        String channelId = "testChannelId";

        // 模拟抛出 IOException 异常
        when(cache.getChannelDetails(channelId)).thenThrow(new IOException());

        // 使用 MockedStatic 模拟 ChannelService 的静态方法
        try (MockedStatic<ChannelService> mockedChannelService = mockStatic(ChannelService.class)) {
            // 调用方法并获取结果
            CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
            Result result = resultStage.toCompletableFuture().join();

            // 验证异常处理的结果
            assertEquals(500, result.status());  // HTTP 500 - Internal Server Error
            verify(cache, times(1)).getChannelDetails(channelId);
            mockedChannelService.verify(() -> ChannelService.getChannelInfo(any()), never());
            mockedChannelService.verify(() -> ChannelService.searchChannel(anyString(), any(Cache.class)), never());
        }
    }
}


//TODO: change to give some more videos
class MockYouTubeService implements YouTubeServiceInterface {

    @Override
    public List<SearchResult> searchVideos(String query) throws IOException {
        // Create a mock SearchResult
        SearchResult searchResult = new SearchResult();
        searchResult.setId(new ResourceId().setVideoId("mockVideoId1"));
        searchResult.setSnippet(new SearchResultSnippet().setTitle("Mock Video Title").setDescription("Mock video description"));

        // Return a list with the mock SearchResult
        return Collections.singletonList(searchResult);
    }

    @Override
    public ChannelListResponse getChannelDetails(String channelId) throws IOException {
        // Create a mock ChannelListResponse
        Channel channel = new Channel();
        channel.setId("mockChannelId");
        channel.setSnippet(new ChannelSnippet().setTitle("Mock Channel Title"));

        ChannelListResponse response = new ChannelListResponse();
        response.setItems(Collections.singletonList(channel));

        return response;
    }

    @Override
    public List<SearchResult> searchChannelVideos(String channelId) throws IOException {
        // Similar mock setup as searchVideos, returning mock SearchResults
        SearchResult searchResult = new SearchResult();
        searchResult.setId(new ResourceId().setVideoId("mockChannelVideoId1"));
        searchResult.setSnippet(new SearchResultSnippet().setTitle("Mock Channel Video Title").setDescription("Mock channel video description"));

        return Collections.singletonList(searchResult);
    }

    @Override
    public List<Video> getVideoDetails(List<String> videoIds) throws IOException {
        // Create a mock Video
        Video video = new Video();
        video.setId("mockVideoId1");
        video.setSnippet(new VideoSnippet().setTitle("Mock Video Title").setDescription("Mock video description"));

        return Collections.singletonList(video);
    }

    @Override
    public String getDescription(String videoId) throws IOException {
        return "Mock video description for video ID: " + videoId;
    }
}