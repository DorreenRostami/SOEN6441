package models;

import com.google.api.services.youtube.model.Video;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import services.YouTubeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * This class contains unit tests for the {@link Cache} class.
 * @author Hamza Asghar Khan
 */
public class CacheTest {

    AutoCloseable openMocks;

    /**
     * Initialize the mocks before each test.
     * @author Hamza Asghar Khan
     */
    @Before
    public void setUp() {
        Cache.reset();
        openMocks = MockitoAnnotations.openMocks(this);
    }

    /**
     * Close the mocks after each test
     * @throws Exception In case an exception is thrown while closing the Mocks.
     * @author Hamza Asghar Khan
     */
    @After
    public void tearDown() throws Exception {
        openMocks.close();
    }

    /**
     * Test whether the cache functions properly when retrieving search results. Also makes sure that
     * once an entry is in the cache, a call is not made to the external API.
     * @throws IOException In case an IOException is thrown by the YouTube API.
     * @author Hamza Asghar Khan
     */
    @Test
    public void testGetSearchHistory_ChannelFalse() throws IOException {
        String query = "video";
        List<VideoInfo> results = new ArrayList<>();
        results.add(new VideoInfo("testVideo", "video.com", "testChannel", "channel.com", "thumbnail.com", "Test description", "tags.com"));
        SearchHistory mockResult = new SearchHistory("testQuery", results);
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.searchVideos(query)).thenReturn(mockResult);

            SearchHistory result = Cache.getSearchHistory(query, false); //cache miss
            assertEquals(mockResult, result);

            result = Cache.getSearchHistory(query, false); //should be in cache now
            assertEquals(mockResult, result);

            mockedYoutubeService.verify(() -> YouTubeService.searchVideos(query), times(1));
        }
    }

    @Test
    public void testGetSearchHistory_ChannelTrue() throws IOException {
        String query = "video";
        List<VideoInfo> results = new ArrayList<>();
        results.add(new VideoInfo("testVideo", "video.com", "testChannel", "channel.com", "thumbnail.com", "Test description", "tags.com"));
        SearchHistory mockResult = new SearchHistory("testQuery", results);
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.searchChannelVideos(query)).thenReturn(mockResult);

            SearchHistory result = Cache.getSearchHistory(query, true); //cache miss
            assertEquals(mockResult, result);

            result = Cache.getSearchHistory(query, true); //should be in cache now
            assertEquals(mockResult, result);

            mockedYoutubeService.verify(() -> YouTubeService.searchChannelVideos(query), times(1));
        }
    }

    @Test
    public void testGetChannelDetails() throws IOException {
        String query = "video";
        List<VideoInfo> videos = new ArrayList<>();
        videos.add(new VideoInfo("video", "video.com", "testChannel", "channel.com", "thumbnail.com", "Test description", "tags.com"));
        SearchHistory results = new SearchHistory("video", videos);
        ChannelInfo mockResult = new ChannelInfo("video", "123", "channel.com", "thumbnail.com", "test description", 32, 32, 12, results);
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.getChannelDetails(query)).thenReturn(mockResult);
            ChannelInfo result = Cache.getChannelDetails(query); //cache miss
            assertEquals(mockResult, result);
            result = Cache.getChannelDetails(query); //should be in cache now
            assertEquals(mockResult, result);
            mockedYoutubeService.verify(() -> YouTubeService.getChannelDetails(query), times(1));
        }
    }

    @Test
    public void testGetDescription() throws IOException {
        String query = "video";
        String mockDescription = "Mock Description";
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.getDescription(query)).thenReturn(mockDescription);
            String result = Cache.getDescription(query); //cache miss
            assertEquals(mockDescription, result);
            result = Cache.getDescription(query); //should be in cache now
            assertEquals(mockDescription, result);
            mockedYoutubeService.verify(() -> YouTubeService.getDescription(query), times(1));
        }
    }

    @Test
    public void testGetVideo() throws IOException {
        String query = "video";
        Video video = mock(Video.class);
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.getVideoDetails(Collections.singletonList(query))).thenReturn(Collections.singletonList(video));
            MockedStatic<Cache> mockedCache = mockStatic(Cache.class);
            mockedCache.when(() -> Cache.getVideo(query)).thenReturn(video);
            Video result = Cache.getVideo(query); //cache miss
            assertEquals(video, result);
            result = Cache.getVideo(query); //should be in cache now
            assertEquals(video, result);
            mockedCache.close();
        }
    }

    /**
     * Tests the validity of a cache entry for an invalid object and ensures it returns false
     * @author Dorreen
     */
    @Test
    public void testHasAValidEntry_Query() {
        Object invalidObject = new Object();
        boolean result = Cache.hasAValidEntry(invalidObject);
        assertFalse(result);
    }


    @Test
    public void testHasAValidEntry_Object() throws IOException {
        String query = "video";
        String description = "Mock Description";
        List<VideoInfo> videos = new ArrayList<>();
        videos.add(new VideoInfo("video", "video.com", "testChannel", "channel.com", "thumbnail.com", "Test description", "tags.com"));
        SearchHistory history = new SearchHistory("video", videos);
        ChannelInfo channelInfo = new ChannelInfo("testTitle", "video", "channel.com", "thumbnail.com", "test description", 32, 32, 12, history);
        assertFalse(Cache.hasAValidEntry(history));
        assertFalse(Cache.hasAValidEntry(channelInfo));
        assertFalse(Cache.hasAValidEntry(query));
        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class);) {
            mockedYoutubeService.when(() -> YouTubeService.getDescription(query)).thenReturn(description);
            mockedYoutubeService.when(() -> YouTubeService.searchVideos(query, 10)).thenReturn(history);
            mockedYoutubeService.when(() -> YouTubeService.getChannelDetails(query)).thenReturn(channelInfo);
            Cache.getDescription(query); //cache miss
            Cache.getSearchHistory(query, false); //cache miss
            Cache.getChannelDetails(query); //cache miss
            assertTrue(Cache.hasAValidEntry(channelInfo));
            assertTrue(Cache.hasAValidEntry(history));
            assertTrue(Cache.hasAValidEntry(query));
            mockedYoutubeService.verify(() -> YouTubeService.getDescription(query), times(1));
        }
    }

    /**
     * Tests the caching behavior of retrieving video details using a video ID.
     * first call should be cache miss, second should be hit
     *
     * @author Dorreen
     */
    @Test
    public void testGetVideoWithID() throws IOException {
        String videoId = "testVideoId";
        Video mockVideo = mock(Video.class);

        try (MockedStatic<YouTubeService> mockedYoutubeService = mockStatic(YouTubeService.class)) {
            mockedYoutubeService.when(() -> YouTubeService.getVideoDetails(Collections.singletonList(videoId)))
                    .thenReturn(Collections.singletonList(mockVideo));

            Video result = Cache.getVideo(videoId);
            assertEquals(mockVideo, result);

            result = Cache.getVideo(videoId);
            assertEquals(mockVideo, result);

            mockedYoutubeService.verify(() -> YouTubeService.getVideoDetails(Collections.singletonList(videoId)), times(1));
        }
    }

}