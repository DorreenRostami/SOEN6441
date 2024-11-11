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
        when(youTubeService.searchVideos(query)).thenReturn(mockResults);

        List<SearchResult> result = cache.get(query, false); //cache miss
        Assertions.assertEquals(mockResults, result);

        result = cache.get(query, false); //should be in cache now
        Assertions.assertEquals(mockResults, result);

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
        when(youTubeService.searchChannelVideos(query)).thenReturn(mockResults);

        List<SearchResult> result = cache.get(query, true); //cache miss
        Assertions.assertEquals(mockResults, result);

        result = cache.get(query, true); //should be in cache now
        Assertions.assertEquals(mockResults, result);

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
        when(youTubeService.getChannelDetails(channelId)).thenReturn(mockResponse);

        ChannelListResponse result = cache.getChannelDetails(channelId); //cache miss
        Assertions.assertEquals(mockResponse, result);

        result = cache.getChannelDetails(channelId); //should be in cache now
        Assertions.assertEquals(mockResponse, result);

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

        when(youTubeService.getDescription(videoId)).thenReturn(mockDescription);

        String result = cache.getDescription(videoId); //cache miss
        Assertions.assertEquals(mockDescription, result);

        result = cache.getDescription(videoId); //should be in cache now
        Assertions.assertEquals(mockDescription, result);

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
        when(youTubeService.getVideoDetails(Collections.singletonList(videoId)))
                .thenReturn(Collections.singletonList(mockVideo));

        Video result = cache.getVideo(videoId); //cache miss
        Assertions.assertEquals(mockVideo, result);

        result = cache.getVideo(videoId); //should be in cache now
        Assertions.assertEquals(mockVideo, result);

        verify(youTubeService, times(1)).getVideoDetails(Collections.singletonList(videoId));
    }
}