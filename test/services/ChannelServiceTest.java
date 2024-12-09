package services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import models.Cache;
import models.ChannelInfo;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.math.BigInteger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChannelServiceTest {

    private Cache mockCache;
    private SearchHistory mockSearchHistory;
    private List<VideoInfo> mockVideoList;

    @Before
    public void setUp() {
        // Mock Cache
        mockCache = mock(Cache.class);

        // Mock SearchHistory
        mockSearchHistory = mock(SearchHistory.class);

        // Create mock VideoInfo list
        mockVideoList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            VideoInfo mockVideoInfo = mock(VideoInfo.class);
            when(mockVideoInfo.getVideoTitle()).thenReturn("Video Title " + i);
            mockVideoList.add(mockVideoInfo);
        }

        when(mockSearchHistory.getResults()).thenReturn(mockVideoList);
    }

    @Test
    public void testSearchChannel() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            mockedStaticCache.when(() -> Cache.getSearchHistory("MockChannelID", true)).thenReturn(mockSearchHistory);

            List<VideoInfo> videoInfoList = ChannelService.searchChannel("MockChannelID", mockCache);

            assertNotNull(videoInfoList);
            assertEquals(10, videoInfoList.size()); // Ensure the list is capped at 10
            for (int i = 0; i < 10; i++) {
                assertEquals("Video Title " + i, videoInfoList.get(i).getVideoTitle());
            }

            mockedStaticCache.verify(() -> Cache.getSearchHistory("MockChannelID", true), times(1));
        }
    }

    @Test
    public void testSearchChannelWithEmptyResults() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            when(mockSearchHistory.getResults()).thenReturn(new ArrayList<>()); // Empty results
            mockedStaticCache.when(() -> Cache.getSearchHistory("EmptyChannelID", true)).thenReturn(mockSearchHistory);

            List<VideoInfo> videoInfoList = ChannelService.searchChannel("EmptyChannelID", mockCache);

            assertNotNull(videoInfoList);
            assertTrue(videoInfoList.isEmpty());

            mockedStaticCache.verify(() -> Cache.getSearchHistory("EmptyChannelID", true), times(1));
        }
    }

    @Test
    public void testSearchChannelThrowsIOException() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            mockedStaticCache.when(() -> Cache.getSearchHistory("InvalidChannelID", true))
                    .thenThrow(new IOException("Error accessing cache"));

            IOException exception = assertThrows(IOException.class, () -> {
                ChannelService.searchChannel("InvalidChannelID", mockCache);
            });

            assertEquals("Error accessing cache", exception.getMessage());
            mockedStaticCache.verify(() -> Cache.getSearchHistory("InvalidChannelID", true), times(1));
        }
    }

    @Test
    public void testGetChannelInfo() {
        // Mock Channel
        Channel mockChannel = mock(Channel.class);
        ChannelSnippet snippet = mock(ChannelSnippet.class);
        ChannelStatistics statistics = mock(ChannelStatistics.class);
        ThumbnailDetails thumbnails = mock(ThumbnailDetails.class);
        Thumbnail thumbnail = mock(Thumbnail.class);

        when(mockChannel.getSnippet()).thenReturn(snippet);
        when(snippet.getTitle()).thenReturn("Mock Channel");
        when(snippet.getDescription()).thenReturn("Mock Description");
        when(snippet.getThumbnails()).thenReturn(thumbnails);
        when(thumbnails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn("http://example.com/thumbnail.jpg");

        when(mockChannel.getId()).thenReturn("MockChannelID");
        when(mockChannel.getStatistics()).thenReturn(statistics);
        when(statistics.getSubscriberCount()).thenReturn(BigInteger.valueOf(12345L));
        when(statistics.getVideoCount()).thenReturn(BigInteger.valueOf(50L));
        when(statistics.getViewCount()).thenReturn(BigInteger.valueOf(123456789L));

        // Test ChannelInfo creation
        ChannelInfo channelInfo = ChannelService.getChannelInfo(mockChannel, mockSearchHistory);

        assertNotNull(channelInfo);
        assertEquals("Mock Channel", channelInfo.getTitle());
        assertEquals("MockChannelID", channelInfo.getChannelId());
        assertEquals("http://example.com/thumbnail.jpg", channelInfo.getThumbnailUrl());
        assertEquals(12345L, channelInfo.getSubscriberCount());
        assertEquals(50L, channelInfo.getVideoCount());
        assertEquals(123456789L, channelInfo.getViewCount());
        assertNotNull(channelInfo.getVideos());
    }
}