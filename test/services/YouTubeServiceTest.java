package services;

import com.google.api.services.youtube.model.*;
import models.Cache;
import models.ChannelInfo;
import models.VideoInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class YouTubeServiceTest {

    @InjectMocks
    private YouTubeService youtubeService;

    @Mock
    private Channel channel;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Thumbnail thumbnail;

    @Mock
    private ThumbnailDetails thumbnailDetails;

    @Mock
    private ChannelSnippet channelSnippet;

    @Mock
    private ChannelStatistics channelStatistics;

    @Mock
    private SearchResultSnippet searchResultSnippet;

    @Mock
    private ResourceId resourceId;

    @Mock
    private Cache cache;

    // 新增 ChannelService 实例
    private ChannelService channelService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化 ChannelService 实例
        channelService = new ChannelService();

        when(channel.getSnippet()).thenReturn(channelSnippet);
        when(channel.getStatistics()).thenReturn(channelStatistics);
        when(searchResult.getSnippet()).thenReturn(searchResultSnippet);
        when(searchResult.getId()).thenReturn(resourceId);
    }

    /**
     * 测试 getChannelInfo 方法
     */
    @Test
    public void testGetChannelInfo() {
        when(channelSnippet.getTitle()).thenReturn("Test Channel");
        when(channel.getId()).thenReturn("12345");
        when(channelSnippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn("https://example.com/thumbnail.jpg");
        when(channelSnippet.getDescription()).thenReturn("This is a test channel.");
        when(channelStatistics.getSubscriberCount()).thenReturn(BigInteger.valueOf(1000L));
        when(channelStatistics.getVideoCount()).thenReturn(BigInteger.valueOf(50L));
        when(channelStatistics.getViewCount()).thenReturn(BigInteger.valueOf(100000L));

        // 使用 channelService 实例调用 getChannelInfo
        ChannelInfo channelInfo = channelService.getChannelInfo(channel);

        assertEquals("Test Channel", channelInfo.getTitle());
        assertEquals("12345", channelInfo.getChannelId());
        assertEquals("https://www.youtube.com/channel/12345", channelInfo.getChannelUrl());
        assertEquals("https://example.com/thumbnail.jpg", channelInfo.getThumbnailUrl());
        assertEquals("This is a test channel.", channelInfo.getDescription());
        assertEquals(1000L, channelInfo.getSubscriberCount());
        assertEquals(50L, channelInfo.getVideoCount());
        assertEquals(100000L, channelInfo.getViewCount());
    }

    /**
     * 测试 searchChannel 方法
     */
    @Test
    public void testSearchChannel() throws IOException {

        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.add(searchResult);

        when(cache.get("12345", true)).thenReturn(searchResults);
        when(searchResultSnippet.getTitle()).thenReturn("Test Video");
        when(resourceId.getVideoId()).thenReturn("video123");
        when(searchResultSnippet.getChannelTitle()).thenReturn("Test Channel");
        when(searchResultSnippet.getChannelId()).thenReturn("12345");
        when(searchResultSnippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn("https://example.com/video_thumbnail.jpg");
        when(searchResultSnippet.getDescription()).thenReturn("This is a test video.");

        // 使用 channelService 实例调用 searchChannel
        List<VideoInfo> videoInfoList = channelService.searchChannel("12345", cache);

        assertEquals(1, videoInfoList.size());
        VideoInfo videoInfo = videoInfoList.get(0);
        assertEquals("Test Video", videoInfo.getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=video123", videoInfo.getVideoUrl());
        assertEquals("Test Channel", videoInfo.getChannelTitle());
        assertEquals("channel?query=12345", videoInfo.getChannelUrl());
        assertEquals("https://example.com/video_thumbnail.jpg", videoInfo.getThumbnailUrl());
        assertEquals("This is a test video.", videoInfo.getDescription());
    }
}