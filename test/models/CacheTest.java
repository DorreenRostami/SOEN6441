package models;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
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

class CacheTest {

    @Mock
    private YouTubeService youTubeService;

    @InjectMocks
    private Cache cache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSearchResultsFromCache() throws IOException {
        String query = "testQuery";
        String cacheKey = "video:" + query;
        List<SearchResult> mockResults = Collections.singletonList(new SearchResult());

        // Mock YouTubeService response
        when(youTubeService.searchVideos(query)).thenReturn(mockResults);

        // Simulate cache miss (first call)
        List<SearchResult> result = cache.get(query, false);
        Assertions.assertEquals(mockResults, result);

        // Manually insert the mock result into the cache to simulate a cache hit
        cache.getListCache().put(cacheKey, mockResults);

        // Simulate cache hit (second call should use cache)
        result = cache.get(query, false);
        Assertions.assertEquals(mockResults, result);

        // Verify the YouTubeService was called only once for the cache miss
        verify(youTubeService, times(1)).searchVideos(query);
    }

    @Test
    void testGetSearchChannelResultsFromCache() throws IOException {
        String query = "channelQuery";
        String cacheKey = "channel:" + query;
        List<SearchResult> mockResults = Collections.singletonList(new SearchResult());

        // Mock YouTubeService response
        when(youTubeService.searchChannelVideos(query)).thenReturn(mockResults);

        // Simulate cache miss (first call)
        List<SearchResult> result = cache.get(query, true);
        Assertions.assertEquals(mockResults, result);

        // Manually insert the mock result into the cache to simulate a cache hit
        cache.getListCache().put(cacheKey, mockResults);

        // Simulate cache hit (second call should use cache)
        result = cache.get(query, true);
        Assertions.assertEquals(mockResults, result);

        // Verify the YouTubeService was called only once for the cache miss
        verify(youTubeService, times(1)).searchChannelVideos(query);
    }

    @Test
    void testGetChannelDetailsFromCache() throws IOException {
        String channelId = "testChannelId";
        ChannelListResponse mockResponse = new ChannelListResponse();

        // Mock YouTubeService response
        when(youTubeService.getChannelDetails(channelId)).thenReturn(mockResponse);

        // Simulate cache miss (first call)
        ChannelListResponse result = cache.getChannelDetails(channelId);
        Assertions.assertEquals(mockResponse, result);

        // Manually insert the mock result into the cache to simulate a cache hit
        cache.getChannelCache().put(channelId, mockResponse);

        // Simulate cache hit (second call should use cache)
        result = cache.getChannelDetails(channelId);
        Assertions.assertEquals(mockResponse, result);

        // Verify the YouTubeService was called only once for the cache miss
        verify(youTubeService, times(1)).getChannelDetails(channelId);
    }

    @Test
    void testGetDescriptionFromCache() throws IOException {
        String videoId = "testVideoId";
        String mockDescription = "This is a test description";

        // Mock YouTubeService response
        when(youTubeService.getDescription(videoId)).thenReturn(mockDescription);

        // Simulate cache miss (first call)
        String result = cache.getDescription(videoId);
        Assertions.assertEquals(mockDescription, result);

        // Manually insert the mock result into the cache to simulate a cache hit
        cache.getDescriptionCache().put(videoId, mockDescription);

        // Simulate cache hit (second call should use cache)
        result = cache.getDescription(videoId);
        Assertions.assertEquals(mockDescription, result);

        // Verify the YouTubeService was called only once for the cache miss
        verify(youTubeService, times(1)).getDescription(videoId);
    }

    @Test
    void testGetVideoFromCache() throws IOException {
        String videoId = "testVideoId";
        Video mockVideo = new Video();

        // Mock YouTubeService response
        when(youTubeService.getVideoDetails(Collections.singletonList(videoId)))
                .thenReturn(Collections.singletonList(mockVideo));

        // Simulate cache miss (first call)
        Video result = cache.getVideo(videoId);
        Assertions.assertEquals(mockVideo, result);

        // Manually insert the mock result into the cache to simulate a cache hit
        cache.getVideoCache().put(videoId, mockVideo);

        // Simulate cache hit (second call should use cache)
        result = cache.getVideo(videoId);
        Assertions.assertEquals(mockVideo, result);

        // Verify the YouTubeService was called only once for the cache miss
        verify(youTubeService, times(1)).getVideoDetails(Collections.singletonList(videoId));
    }
}