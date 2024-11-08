package services;

import com.google.api.services.youtube.model.*;
import models.ChannelInfo;
import models.VideoInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ChannelServiceTest {

    private YouTubeService youtubeService;
    private Channel channel;
    private SearchResult searchResult;
    private Thumbnail thumbnail;
    private ThumbnailDetails thumbnailDetails;
    private ChannelSnippet channelSnippet;
    private ChannelStatistics channelStatistics;
    private SearchResultSnippet searchResultSnippet;
    private ResourceId resourceId;

    @Before
    public void setUp() {
        youtubeService = mock(YouTubeService.class);
        channel = mock(Channel.class);
        searchResult = mock(SearchResult.class);
        thumbnail = mock(Thumbnail.class);
        thumbnailDetails = mock(ThumbnailDetails.class);
        channelSnippet = mock(ChannelSnippet.class);
        channelStatistics = mock(ChannelStatistics.class);
        searchResultSnippet = mock(SearchResultSnippet.class);
        resourceId = mock(ResourceId.class);

        when(channel.getSnippet()).thenReturn(channelSnippet);
        when(channel.getStatistics()).thenReturn(channelStatistics);
        when(searchResult.getSnippet()).thenReturn(searchResultSnippet);
        when(searchResult.getId()).thenReturn(resourceId);
    }

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

        ChannelInfo channelInfo = ChannelService.getChannelInfo(channel);

        assertEquals("Test Channel", channelInfo.getTitle());
        assertEquals("12345", channelInfo.getChannelId());
        assertEquals("https://www.youtube.com/channel/12345", channelInfo.getChannelUrl());
        assertEquals("https://example.com/thumbnail.jpg", channelInfo.getThumbnailUrl());
        assertEquals("This is a test channel.", channelInfo.getDescription());
        assertEquals(1000L, channelInfo.getSubscriberCount());
        assertEquals(50L, channelInfo.getVideoCount());
        assertEquals(100000L, channelInfo.getViewCount());
    }

    @Test
    public void testSearchChannel() throws IOException {
        List<SearchResult> searchResults = new ArrayList<>();
        searchResults.add(searchResult);

        when(youtubeService.searchChannelVideos("12345")).thenReturn(searchResults);
        when(searchResultSnippet.getTitle()).thenReturn("Test Video");
        when(resourceId.getVideoId()).thenReturn("video123");
        when(searchResultSnippet.getChannelTitle()).thenReturn("Test Channel");
        when(searchResultSnippet.getChannelId()).thenReturn("12345");
        when(searchResultSnippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn("https://example.com/video_thumbnail.jpg");
        when(searchResultSnippet.getDescription()).thenReturn("This is a test video.");

        List<VideoInfo> videoInfoList = ChannelService.searchChannel("12345", youtubeService);

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