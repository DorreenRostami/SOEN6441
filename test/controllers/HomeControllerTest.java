package controllers;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static play.mvc.Results.ok;
import static play.test.Helpers.*;
import com.google.api.services.youtube.model.*;

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
import views.html.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;




class HomeControllerTest {
    @Mock
    private YouTubeService youTubeService;

    @Mock
    private Cache cache;

    @Mock
    private VideoDetailSevice videoDetailSevice;

    @Mock
    private Http.Request request;

    @InjectMocks
    private HomeController homeController;


    @BeforeEach
    public void setup() throws GeneralSecurityException, IOException {
        MockitoAnnotations.openMocks(this);
        homeController = new HomeController(youTubeService, cache, videoDetailSevice);
    }

    /**
     * Tests redirection to the home page
     * @author Hao
     */
    @Test
    void testRedirectToYtLytics() {
        Result result = homeController.redirectToYtLytics();
        assertEquals(303, result.status());
    }

    /**
     * Tests that hello method renders the home page with an empty search history
     * @author Dorreen
     */
    @Test
    public void testHello_withNewSession() {
        when(request.session()).thenReturn(new Http.Session(Collections.emptyMap()));

        CompletionStage<Result> resultStage = homeController.hello(request);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(OK, result.status());
    }

    /**
     * Tests that a successful search returns OK status
     * @author Dorreen
     */
    @Test
    void testSearch() throws IOException {
//        String query = "query";
//        Http.Request request = fakeRequest().build();
//        List<SearchResult> res = new ArrayList<>();
//        when(cache.get(query, false)).thenReturn(res);
//        when(hello.render(anyList())).thenReturn(null);
//
//        CompletionStage<Result> result = homeController.search(request, query);
//
//        assertEquals(OK, result.toCompletableFuture().join().status());
//        verify(cache, times(1)).get(query, false);
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
    void testSearchChannelIOException() throws IOException {
        String channelId = "testChannelId";

        when(cache.getChannelDetails(channelId)).thenThrow(new IOException());

        try (MockedStatic<ChannelService> mockedChannelService = mockStatic(ChannelService.class)) {

            CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
            Result result = resultStage.toCompletableFuture().join();

            assertEquals(500, result.status());  // HTTP 500 - Internal Server Error
            verify(cache, times(1)).getChannelDetails(channelId);
            mockedChannelService.verify(() -> ChannelService.getChannelInfo(any()), never());
            mockedChannelService.verify(() -> ChannelService.searchChannel(anyString(), any(Cache.class)), never());
        }
    }
}