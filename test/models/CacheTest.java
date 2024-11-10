package models;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.YouTubeService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * This class contains unit tests for the {@link Cache} class.
 * @author Hamza Asghar Khan
 */
class CacheTest {

    AutoCloseable openMocks;
    @Mock
    private YouTubeService youTubeService;

    @InjectMocks
    private Cache cache;

    /**
     * Initialize the mocks before each test.
     */
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    /**
     * Close the mocks after each test
     * @throws Exception In case an exception is thrown while closing the Mocks.
     */
    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    /**
     * Test whether the cache functions properly when retrieving search results. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    void testGetSearchResultsFromCache() throws IOException {
        String query = "testQuery";
        List<SearchResult> mockResults = Collections.singletonList(new SearchResult());
        // Mock YouTube API search results
        when(youTubeService.searchVideos(query)).thenReturn(mockResults);
        // Retrieve the value from the cache for the first time (cache miss)
        List<SearchResult> result = cache.get(query, false);
        Assertions.assertEquals(mockResults, result);
        // Retrieve the same query again to ensure that is it being fetched from the cache.
        result = cache.get(query, false);
        Assertions.assertEquals(mockResults, result);
        // Verify that the YouTube API is only being called once.
        verify(youTubeService, times(1)).searchVideos(query);
    }

    /**
     * Test whether the cache functions properly when retrieving videos of a channel. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    void testGetSearchChannelResultsFromCache() throws IOException {
        String query = "channelQuery";
        List<SearchResult> mockResults = Collections.singletonList(new SearchResult());
        // Mock YouTube API search results
        when(youTubeService.searchChannelVideos(query)).thenReturn(mockResults);
        // Retrieve the value from the cache for the first time (cache miss)
        List<SearchResult> result = cache.get(query, true);
        Assertions.assertEquals(mockResults, result);
        // Retrieve the same query again to ensure that is it being fetched from the cache.
        result = cache.get(query, true);
        Assertions.assertEquals(mockResults, result);
        // Verify that the YouTube API is only being called once.
        verify(youTubeService, times(1)).searchChannelVideos(query);
    }

    /**
     * Test whether the cache functions properly when retrieving channel details. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    void testGetChannelDetailsFromCache() throws IOException {
        String channelId = "testChannelId";
        ChannelListResponse mockResponse = new ChannelListResponse();
        // Mock YouTube API search results
        when(youTubeService.getChannelDetails(channelId)).thenReturn(mockResponse);
        // Retrieve the value from the cache for the first time (cache miss)
        ChannelListResponse result = cache.getChannelDetails(channelId);
        Assertions.assertEquals(mockResponse, result);
        // Retrieve the same query again to ensure that is it being fetched from the cache.
        result = cache.getChannelDetails(channelId);
        Assertions.assertEquals(mockResponse, result);
        // Verify that the YouTube API is only being called once.
        verify(youTubeService, times(1)).getChannelDetails(channelId);
    }

    /**
     * Test whether the cache functions properly when retrieving video descriptions. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    void testGetDescriptionFromCache() throws IOException {
        String videoId = "testVideoId";
        String mockDescription = "This is a test description";
        // Mock YouTube API search results
        when(youTubeService.getDescription(videoId)).thenReturn(mockDescription);
        // Retrieve the value from the cache for the first time (cache miss)
        String result = cache.getDescription(videoId);
        Assertions.assertEquals(mockDescription, result);
        // Retrieve the same query again to ensure that is it being fetched from the cache.
        result = cache.getDescription(videoId);
        Assertions.assertEquals(mockDescription, result);
        // Verify that the YouTube API is only being called once.
        verify(youTubeService, times(1)).getDescription(videoId);
    }

    /**
     * Test whether the cache functions properly when retrieving Video Objects. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    void testGetVideoFromCache() throws IOException {
        String videoId = "testVideoId";
        Video mockVideo = new Video();
        // Mock YouTube API search results
        when(youTubeService.getVideoDetails(Collections.singletonList(videoId)))
                .thenReturn(Collections.singletonList(mockVideo));
        // Retrieve the value from the cache for the first time (cache miss)
        Video result = cache.getVideo(videoId);
        Assertions.assertEquals(mockVideo, result);
        // Retrieve the same query again to ensure that is it being fetched from the cache.
        result = cache.getVideo(videoId);
        Assertions.assertEquals(mockVideo, result);
        // Verify that the YouTube API is only being called once.
        verify(youTubeService, times(1)).getVideoDetails(Collections.singletonList(videoId));
    }
}