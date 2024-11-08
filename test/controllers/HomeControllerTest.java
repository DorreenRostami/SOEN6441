package controllers;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static play.mvc.Results.ok;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Channel;
import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import services.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.CompletionStage;



class HomeControllerTest {

    @Mock private Cache cache;
    @Mock private YouTubeService youtubeService;
    @Mock private VideoDetailSevice videoDetailSevice;
    @Mock private Http.Request request;
    @Mock private Database database;
    @Mock private Channel channel;
    @Mock private ChannelListResponse channelListResponse;
    @Mock private Video video;

    @InjectMocks private HomeController homeController;

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
}