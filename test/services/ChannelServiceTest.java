package services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import models.Cache;
import models.ChannelInfo;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChannelServiceTest {

    private Cache mockCache;
    private SearchHistory mockSearchHistory;
    private List<VideoInfo> mockVideoList;

    @BeforeEach
    void setUp() {
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
    void testSearchChannel() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            mockedStaticCache.when(() -> Cache.get("MockChannelID", true)).thenReturn(mockSearchHistory);

            List<VideoInfo> videoInfoList = ChannelService.searchChannel("MockChannelID", mockCache);

            assertNotNull(videoInfoList);
            assertEquals(10, videoInfoList.size()); // Ensure the list is capped at 10
            for (int i = 0; i < 10; i++) {
                assertEquals("Video Title " + i, videoInfoList.get(i).getVideoTitle());
            }

            mockedStaticCache.verify(() -> Cache.get("MockChannelID", true), times(1));
        }
    }

    @Test
    void testSearchChannelWithEmptyResults() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            when(mockSearchHistory.getResults()).thenReturn(new ArrayList<>()); // Empty results
            mockedStaticCache.when(() -> Cache.get("EmptyChannelID", true)).thenReturn(mockSearchHistory);

            List<VideoInfo> videoInfoList = ChannelService.searchChannel("EmptyChannelID", mockCache);

            assertNotNull(videoInfoList);
            assertTrue(videoInfoList.isEmpty());

            mockedStaticCache.verify(() -> Cache.get("EmptyChannelID", true), times(1));
        }
    }

    @Test
    void testSearchChannelThrowsIOException() throws IOException {
        try (MockedStatic<Cache> mockedStaticCache = Mockito.mockStatic(Cache.class)) {
            mockedStaticCache.when(() -> Cache.get("InvalidChannelID", true))
                    .thenThrow(new IOException("Error accessing cache"));

            IOException exception = assertThrows(IOException.class, () -> {
                ChannelService.searchChannel("InvalidChannelID", mockCache);
            });

            assertEquals("Error accessing cache", exception.getMessage());
            mockedStaticCache.verify(() -> Cache.get("InvalidChannelID", true), times(1));
        }
    }
}