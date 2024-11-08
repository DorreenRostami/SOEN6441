//package controllers;
//
//import models.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import play.mvc.Http;
//import play.mvc.Result;
//import services.SessionsService;
//import views.html.hello;
//import views.html.channel;
//import views.html.statistics;
//
//import com.google.api.services.youtube.model.SearchResult;
//import com.google.api.services.youtube.model.ChannelListResponse;
//import com.google.api.services.youtube.model.Channel;
//import scala.Tuple2;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletionStage;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static play.mvc.Results.ok;
//import static play.test.Helpers.*;
//
//public class HomeControllerTest {
//
//    @Mock
//    private YouTubeService youtubeService;
//
//    @Mock
//    private Database database;
//
//    @InjectMocks
//    private HomeController homeController;
//
//    @BeforeEach
//    public void setUp() throws GeneralSecurityException, IOException {
//        MockitoAnnotations.openMocks(this);
//        homeController = new HomeController();
//    }
//
//    @Test
//    public void testRedirectToYtLytics() {
//        Result result = homeController.redirectToYtLytics();
//        assertEquals(SEE_OTHER, result.status());
//    }
//
//    @Test
//    public void testHelloWithExistingSession() {
//        Http.Request request = mock(Http.Request.class);
//        String sessionId = "existingSessionId";
//
//        when(SessionsService.getSessionId(request)).thenReturn(sessionId);
//        when(SessionsService.hasSessionId(request)).thenReturn(true);
//        when(database.get(sessionId)).thenReturn(new ArrayList<>());
//        when(hello.render(any())).thenReturn(ok("Rendered hello view"));
//
//        CompletionStage<Result> resultStage = homeController.hello(request);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(OK, result.status());
//        verify(database, times(1)).get(sessionId);
//    }
//
//    @Test
//    public void testHelloWithNewSession() {
//        Http.Request request = mock(Http.Request.class);
//        String sessionId = "newSessionId";
//
//        when(SessionsService.getSessionId(request)).thenReturn(sessionId);
//        when(SessionsService.hasSessionId(request)).thenReturn(false);
//        when(database.get(sessionId)).thenReturn(new ArrayList<>());
//        when(hello.render(any())).thenReturn(ok("Rendered hello view"));
//
//        CompletionStage<Result> resultStage = homeController.hello(request);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(OK, result.status());
//        assertTrue(result.session(request).get("sessionId").isPresent());
//        verify(database, times(1)).get(sessionId);
//    }
//
//    @Test
//    public void testSearchWithExistingSession() throws IOException {
//        Http.Request request = mock(Http.Request.class);
//        String query = "test query";
//        String sessionId = "existingSessionId";
//
//        when(SessionsService.getSessionId(request)).thenReturn(sessionId);
//        when(SessionsService.hasSessionId(request)).thenReturn(true);
//        List<SearchResult> searchResults = new ArrayList<>();
//        when(youtubeService.searchVideos(query)).thenReturn(searchResults);
//        List<SearchHistory> searchHistory = new ArrayList<>();
//        when(database.get(sessionId)).thenReturn(searchHistory);
//        when(hello.render(any())).thenReturn(ok("Rendered hello view"));
//
//        CompletionStage<Result> resultStage = homeController.search(request, query);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(OK, result.status());
//        verify(youtubeService, times(1)).searchVideos(query);
//        verify(database, times(1)).put(sessionId, searchHistory);
//    }
//
//    @Test
//    public void testSearchIOException() throws IOException {
//        Http.Request request = mock(Http.Request.class);
//        String query = "error query";
//        when(youtubeService.searchVideos(query)).thenThrow(new IOException());
//
//        CompletionStage<Result> resultStage = homeController.search(request, query);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(INTERNAL_SERVER_ERROR, result.status());
//    }
//
//    @Test
//    public void testSearchChannel() throws IOException {
//        String channelId = "testChannelId";
//        List<VideoInfo> videoInfoList = new ArrayList<>();
//        ChannelListResponse channelResponse = mock(ChannelListResponse.class);
//        Channel channel = mock(Channel.class);
//        List<Channel> channels = new ArrayList<>();
//        channels.add(channel);
//
//        when(youtubeService.getChannelDetails(channelId)).thenReturn(channelResponse);
//        when(channelResponse.getItems()).thenReturn(channels);
//        when(ChannelService.getChannelInfo(channel)).thenReturn(new ChannelInfo("Test Channel", "url", "thumbnail", "description", 1000L, 100L, 50L));
//        when(ChannelService.searchChannel(anyString(), any())).thenReturn(videoInfoList);
//        when(channel.render(any(), any(), any())).thenReturn(ok("Rendered channel view"));
//
//        CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(OK, result.status());
//        verify(youtubeService, times(1)).getChannelDetails(channelId);
//    }
//
//    @Test
//    public void testShowStatistics() throws IOException {
//        String query = "test query";
//        List<SearchResult> searchResults = new ArrayList<>();
//        when(youtubeService.searchVideos(query)).thenReturn(searchResults);
//        when(statistics.render(any(), any())).thenReturn(ok("Rendered statistics view"));
//
//        CompletionStage<Result> resultStage = homeController.showStatistics(query);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(OK, result.status());
//        verify(youtubeService, times(1)).searchVideos(query);
//    }
//
//    @Test
//    public void testShowStatisticsIOException() throws IOException {
//        String query = "error query";
//        when(youtubeService.searchVideos(query)).thenThrow(new IOException());
//
//        CompletionStage<Result> resultStage = homeController.showStatistics(query);
//        Result result = resultStage.toCompletableFuture().join();
//
//        assertEquals(INTERNAL_SERVER_ERROR, result.status());
//    }
//}