package services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.ChannelInfo;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class YouTubeServiceTest {

    @Mock
    private YouTube youtubeMock;

    @Mock
    private YouTube.Search searchMock;

    @Mock
    private YouTube.Search.List searchListMock;

    @Mock
    private YouTube.Channels channelsMock;

    @Mock
    private YouTube.Channels.List channelsListMock;

    @Mock
    private YouTube.Videos videosMock;

    @Mock
    private YouTube.Videos.List videosListMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock YouTubeService internal YouTube instance
        YouTubeService.youtubeService = youtubeMock;
    }

    @Test
    public void testSearchVideos() throws IOException {
        // Mock SearchResult and its associated properties
        SearchResult mockResult = new SearchResult();
        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setTitle("Mock Video Title");
        snippet.setChannelTitle("Mock Channel Title");
        snippet.setDescription("Mock Video Description");

        // Set ResourceId directly to the SearchResult
        ResourceId resourceId = new ResourceId();
        resourceId.setVideoId("mockVideoId");
        mockResult.setId(resourceId); // 修复这里

        mockResult.setSnippet(snippet);

        SearchListResponse mockResponse = new SearchListResponse();
        mockResponse.setItems(Collections.singletonList(mockResult));

        // Mock YouTube Search.List behavior
        when(youtubeMock.search()).thenReturn(searchMock);
        when(searchMock.list(anyString())).thenReturn(searchListMock);
        when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
        when(searchListMock.setQ(anyString())).thenReturn(searchListMock);
        when(searchListMock.setType(anyString())).thenReturn(searchListMock);
        when(searchListMock.setVideoDuration(anyString())).thenReturn(searchListMock);
        when(searchListMock.setOrder(anyString())).thenReturn(searchListMock);
        when(searchListMock.setMaxResults(anyLong())).thenReturn(searchListMock);
        when(searchListMock.execute()).thenReturn(mockResponse);

        // Call the method under test
        SearchHistory result = YouTubeService.searchVideos("testQuery", 1);

        // Verify results
        assertEquals(1, result.getResults().size());
        assertEquals("Mock Video Title", result.getResults().get(0).getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=mockVideoId", result.getResults().get(0).getVideoUrl());
    }

    @Test
    public void testGetChannelDetails() throws IOException {
        // Mock Channel and its properties
        Channel mockChannel = new Channel();
        ChannelSnippet snippet = new ChannelSnippet();
        snippet.setTitle("Mock Channel Title");
        mockChannel.setSnippet(snippet);
        ChannelStatistics statistics = new ChannelStatistics();
        statistics.setSubscriberCount(BigInteger.valueOf(1000L));
        mockChannel.setStatistics(statistics);

        ChannelListResponse mockResponse = new ChannelListResponse();
        mockResponse.setItems(Collections.singletonList(mockChannel));

        // Mock YouTube Channels.List behavior
        when(youtubeMock.channels()).thenReturn(channelsMock);
        when(channelsMock.list(anyString())).thenReturn(channelsListMock);
        when(channelsListMock.setKey(anyString())).thenReturn(channelsListMock);
        when(channelsListMock.setId(anyString())).thenReturn(channelsListMock);
        when(channelsListMock.execute()).thenReturn(mockResponse);

        // Mock searchChannelVideos
        SearchResult mockResult = new SearchResult();
        SearchResultSnippet videoSnippet = new SearchResultSnippet();
        videoSnippet.setTitle("Mock Video Title");
        videoSnippet.setChannelTitle("Mock Channel Title");
        mockResult.setSnippet(videoSnippet);

        ResourceId videoResourceId = new ResourceId();
        videoResourceId.setVideoId("mockVideoId");
        mockResult.setId(videoResourceId);

        SearchListResponse searchResponse = new SearchListResponse();
        searchResponse.setItems(Collections.singletonList(mockResult));

        when(youtubeMock.search()).thenReturn(searchMock);
        when(searchMock.list(anyString())).thenReturn(searchListMock);
        when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
        when(searchListMock.setChannelId(anyString())).thenReturn(searchListMock);
        when(searchListMock.setType(anyString())).thenReturn(searchListMock);
        when(searchListMock.setOrder(anyString())).thenReturn(searchListMock);
        when(searchListMock.setMaxResults(anyLong())).thenReturn(searchListMock);
        when(searchListMock.execute()).thenReturn(searchResponse);

        // Call the method under test
        ChannelInfo result = YouTubeService.getChannelDetails("mockChannelId");

        // Verify results
        assertEquals("Mock Channel Title", result.getTitle());
        assertEquals(1000L, result.getSubscriberCount());
    }

    @Test
    public void testGetDescription() throws IOException {
        // Mock Video and its properties
        Video mockVideo = new Video();
        VideoSnippet snippet = new VideoSnippet();
        snippet.setDescription("Mock Video Description");
        mockVideo.setSnippet(snippet);

        VideoListResponse mockResponse = new VideoListResponse();
        mockResponse.setItems(Collections.singletonList(mockVideo));

        // Mock YouTube Videos.List behavior
        when(youtubeMock.videos()).thenReturn(videosMock);
        when(videosMock.list(anyString())).thenReturn(videosListMock);
        when(videosListMock.setKey(anyString())).thenReturn(videosListMock);
        when(videosListMock.setId(anyString())).thenReturn(videosListMock);
        when(videosListMock.execute()).thenReturn(mockResponse);

        // Call the method under test
        String description = YouTubeService.getDescription("mockVideoId");

        // Verify results
        assertEquals("Mock Video Description", description);
    }

    @Test
    public void testSearchVideosDefaultLength() throws IOException {
        // Mock SearchResult and its associated properties
        SearchResult mockResult = new SearchResult();
        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setTitle("Mock Video Title");
        snippet.setChannelTitle("Mock Channel Title");
        snippet.setDescription("Mock Video Description");

        // Set ResourceId directly to the SearchResult
        ResourceId resourceId = new ResourceId();
        resourceId.setVideoId("mockVideoId");
        mockResult.setId(resourceId);

        mockResult.setSnippet(snippet);

        SearchListResponse mockResponse = new SearchListResponse();
        mockResponse.setItems(Collections.singletonList(mockResult));

        // Mock YouTube Search.List behavior
        when(youtubeMock.search()).thenReturn(searchMock);
        when(searchMock.list(anyString())).thenReturn(searchListMock);
        when(searchListMock.setKey(anyString())).thenReturn(searchListMock);
        when(searchListMock.setQ(anyString())).thenReturn(searchListMock);
        when(searchListMock.setType(anyString())).thenReturn(searchListMock);
        when(searchListMock.setVideoDuration(anyString())).thenReturn(searchListMock);
        when(searchListMock.setOrder(anyString())).thenReturn(searchListMock);
        when(searchListMock.setMaxResults(anyLong())).thenReturn(searchListMock);
        when(searchListMock.execute()).thenReturn(mockResponse);

        // Call the method under test
        SearchHistory result = YouTubeService.searchVideos("testQuery");

        // Verify results
        assertEquals(1, result.getResults().size());
        assertEquals("Mock Video Title", result.getResults().get(0).getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=mockVideoId", result.getResults().get(0).getVideoUrl());
    }

    @Test
    public void testGetVideoDetails() throws IOException {
        // Mock Video and its properties
        Video mockVideo = new Video();
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle("Mock Video Title");
        snippet.setDescription("Mock Video Description");
        mockVideo.setSnippet(snippet);

        VideoListResponse mockResponse = new VideoListResponse();
        mockResponse.setItems(Collections.singletonList(mockVideo));

        // Mock YouTube Videos.List behavior
        when(youtubeMock.videos()).thenReturn(videosMock);
        when(videosMock.list(anyString())).thenReturn(videosListMock);
        when(videosListMock.setKey(anyString())).thenReturn(videosListMock);
        when(videosListMock.setId(anyString())).thenReturn(videosListMock);
        when(videosListMock.execute()).thenReturn(mockResponse);

        // Call the method under test
        List<Video> result = YouTubeService.getVideoDetails(Collections.singletonList("mockVideoId"));

        // Verify results
        assertEquals(1, result.size());
        assertEquals("Mock Video Title", result.get(0).getSnippet().getTitle());
        assertEquals("Mock Video Description", result.get(0).getSnippet().getDescription());
    }
}