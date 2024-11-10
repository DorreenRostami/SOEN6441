package controllers;

import org.junit.jupiter.api.*;
import org.mockito.*;
import util.TestHelper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static play.mvc.Results.internalServerError;
import static play.test.Helpers.*;
import com.google.api.services.youtube.model.*;
import models.*;
import org.mockito.MockedStatic;
import play.mvc.Http;
import play.mvc.Result;
import services.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Unit tests for the {@link HomeController} class
 * @author Dorreen & Hao
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeControllerTest {
    @Mock
    private YouTubeService youTubeService;

    @Mock
    private Cache cache;

    @Mock
    private VideoDetailSevice videoDetailSevice;

    @Mock
    private Http.Request request;

    @Mock
    private Database database;

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() throws IOException, GeneralSecurityException {
        MockitoAnnotations.openMocks(this);
        reset(cache, database, request, youTubeService, videoDetailSevice, channelService);
        homeController = new HomeController(youTubeService, cache, videoDetailSevice, database, channelService);
    }

    @AfterEach
    void tearDown() {
        Mockito.clearAllCaches();
    }

    /**
     * Tests redirection to the home page
     * @author Hao & Dorreen
     */
    @Test
    void testRedirectToYtLytics() {
        Result result = homeController.redirectToYtLytics();
        assertEquals(303, result.status());
        assertEquals("/ytlytics", result.redirectLocation().orElse(""));
    }

    /**
     * Tests hello() when there is no existing session
     * @author Dorreen Rostami
     */
    @Test
    public void testHello_newSession() {
        when(request.session()).thenReturn(new Http.Session(Collections.emptyMap()));

        CompletionStage<Result> resultStage = homeController.hello(request);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(OK, result.status());
    }

    /**
     * Tests hello() when a session with an existing ID is present
     * @author Dorreen Rostami
     */
    @Test
    public void testHello_existingSession() {
        String sessionId = "id";
        Http.Session mockSession = mock(Http.Session.class);
        when(mockSession.get("sessionId")).thenReturn(Optional.of(sessionId));
        when(request.session()).thenReturn(mockSession);

        List<SearchHistory> searchHistory = new ArrayList<>();
        searchHistory.add(new SearchHistory("query", Collections.emptyList(), null));
        Database database = new Database();
        database.put(sessionId, searchHistory);

        CompletionStage<Result> resultStage = homeController.hello(request);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(OK, result.status());
        String content = contentAsString(result);
        assert content.contains("query");
    }

    /**
     * Tests search() with a valid query, ensuring the session ID is correctly handled,
     * search results are fetched from the cache, and the search history is updated in the database.
     * @author Dorreen Rostami
     */
    @Test
    @Order(3)
    public void testSearch() throws IOException {
        String query = "query";
        String sessionId = "id";
        Http.Session mockSession = mock(Http.Session.class);
        when(mockSession.get("sessionId")).thenReturn(Optional.of(sessionId));
        when(request.session()).thenReturn(mockSession);


        try (MockedStatic<SessionsService> mockedSessionsService = mockStatic(SessionsService.class)) {
            mockedSessionsService.when(() -> SessionsService.getSessionId(request)).thenReturn(sessionId);
            mockedSessionsService.when(() -> SessionsService.hasSessionId(request)).thenReturn(true);

            SearchResult res = TestHelper.createMockSearchResult("V-id", "Title", "Channel",
                    "c1", "https://thumbnail/1", "desc");
            List<SearchResult> cachedResults = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                cachedResults.add(res);
            }

            when(cache.get(query, false)).thenReturn(cachedResults);
            when(cache.getDescription("V-id")).thenReturn("desc");
            when(database.get(sessionId)).thenReturn(new ArrayList<>());

            CompletionStage<Result> resultStage = homeController.search(request, query);
            Result result = resultStage.toCompletableFuture().join();

            assertEquals(OK, result.status());

            String content = contentAsString(result);
            assert content.contains("Title");

            verify(database).put(eq(sessionId), anyList());
        }

        MockedStatic<SessionsService> mockedSessions = mockStatic(SessionsService.class);
        mockedSessions.close();
    }

    /**
     * Tests search() with a valid query, when request has no session ID (happens when user's
     * first request is a query, e.g: http://localhost:9000/search?query=something)
     * @author Dorreen Rostami
     */
    @Test
    public void testSearch_noSessionID() throws IOException {
        String query = "query";
        String sessionId = "id";
        Http.Session mockSession = mock(Http.Session.class);
        when(mockSession.get("sessionId")).thenReturn(Optional.empty());
        when(request.session()).thenReturn(mockSession);

        try (MockedStatic<SessionsService> mockedSessionsService = mockStatic(SessionsService.class)) {
            mockedSessionsService.when(() -> SessionsService.getSessionId(request)).thenReturn(sessionId);
            mockedSessionsService.when(() -> SessionsService.hasSessionId(request)).thenReturn(false);


            SearchResult res = TestHelper.createMockSearchResult("V-id", "Title", "Channel",
                    "c1", "https://thumbnail/1", "desc");
            List<SearchResult> cachedResults = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                cachedResults.add(res);
            }
            when(cache.get(query, false)).thenReturn(cachedResults);
            when(cache.getDescription("V-id")).thenReturn("desc");
            when(database.get(sessionId)).thenReturn(new ArrayList<>());

            CompletionStage<Result> resultStage = homeController.search(request, query);
            Result result = resultStage.toCompletableFuture().join();

            assertEquals(OK, result.status());

            String content = contentAsString(result);
            assert content.contains("Title");
        }

        MockedStatic<SessionsService> mockedSessions = mockStatic(SessionsService.class);
        mockedSessions.close();
    }

    /**
     * Tests search() to ensure it returns an internal server error when an IOException occurs
     * @author Dorreen Rostami
     */
    @Test
    public void testSearch_withIOException() throws IOException {
        String query = "query";
        String sessionId = "id";
        Http.Session mockSession = mock(Http.Session.class);
        when(mockSession.get("sessionId")).thenReturn(Optional.of(sessionId));
        when(request.session()).thenReturn(mockSession);

        when(cache.get(query, false)).thenThrow(new IOException("IOException"));

        CompletionStage<Result> resultStage = homeController.search(request, query);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(INTERNAL_SERVER_ERROR, result.status());
        verify(cache).get(query, false);
        verify(database, never()).put(anyString(), anyList());
    }

    /**
     * Tests the showStatistics method when data is successfully retrieved from the cache
     *
     * @throws IOException if an error occurs while retrieving the data from the cache (but since
     * cache is mocked for this test, it should not throw an exception).
     * @author Hao & Dorreen
     */
    @Test
    void testShowStatistics() throws IOException {
        String query = "statistics query";

        SearchResult res = TestHelper.createMockSearchResult("V-id", "Title", "Channel",
                "c1", "https://thumbnail/1", "desc");
        List<SearchResult> cachedResults = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cachedResults.add(res);
        }
        when(cache.get(query, false)).thenReturn(cachedResults);

        CompletionStage<Result> resultStage = homeController.showStatistics(query);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(200, result.status());
    }

    /**
     * Tests the showStatistics method when an IOException is thrown while retrieving data from the cache
     *
     * @throws IOException if an error occurs while retrieving the data from the cache (but since
     * cache is mocked for this test, it should not throw an exception).
     * @author Hao
     */
    @Test
    @Order(1)
    void testShowStatisticsWithIOException() throws IOException {
        String query = "statistics query";

        when(cache.get(query, false)).thenThrow(new IOException("Error fetching data"));

        CompletionStage<Result> resultStage = homeController.showStatistics(query);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(500, result.status());
    }

    @Test
    void testSearchByTag() {
        String tag = "sampleTag";

        CompletionStage<Result> resultStage = homeController.searchByTag(tag);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(200, result.status());
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

        Channel mockChannel = mock(Channel.class);
        ChannelListResponse mockChannelListResponse = mock(ChannelListResponse.class);
        ChannelInfo mockChannelInfo = new ChannelInfo(
                "Test Channel Title",
                channelId,
                "https://www.youtube.com/channel/" + channelId,
                "http://example.com/thumbnailUrl",
                "Channel Description",
                1000L,
                50L,
                50000L
        );

        when(cache.getChannelDetails(channelId)).thenReturn(mockChannelListResponse);
        when(mockChannelListResponse.getItems()).thenReturn(Collections.singletonList(mockChannel));
        when(channelService.getChannelInfo(mockChannel)).thenReturn(mockChannelInfo);
        when(channelService.searchChannel(channelId, cache)).thenReturn(Collections.emptyList());

        CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("Test Channel Title"));

        verify(cache, times(1)).getChannelDetails(channelId);
        verify(channelService, times(1)).getChannelInfo(mockChannel);
        verify(channelService, times(1)).searchChannel(channelId, cache);
    }

    @Test
    @Order(2)
    void testSearchChannelIOException() throws IOException {
        String channelId = "testChannelId";

        when(cache.getChannelDetails(channelId)).thenThrow(new IOException("Simulated IOException"));

        CompletionStage<Result> resultStage = homeController.searchChannel(channelId);
        Result result = resultStage.toCompletableFuture().join();

        assertEquals(INTERNAL_SERVER_ERROR, result.status());
        assertTrue(contentAsString(result).contains("Error fetching data from YouTube API"));
        verify(cache, times(1)).getChannelDetails(channelId);
        verify(channelService, never()).getChannelInfo(any());
//        verify(channelService, never()).searchChannel(anyString(), any(Cache.class));
    }
}