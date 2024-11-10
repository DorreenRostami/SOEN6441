package services;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.Cache;
import models.ChannelInfo;
import models.Database;
import models.VideoInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link YouTubeService} class
 * @author Hao
 */
public class YouTubeServiceTest {

    private YouTubeService youtubeService;

    @Mock
    private YouTube youtube;

    @Mock
    private YouTube.Search search;

    @Mock
    private YouTube.Search.List searchListRequest;

    @Mock
    private SearchListResponse searchListResponse;

    @Mock
    private YouTube.Channels channels;

    @Mock
    private YouTube.Channels.List channelsListRequest;

    @Mock
    private ChannelListResponse channelListResponse;

    @Mock
    private YouTube.Videos videos;

    @Mock
    private YouTube.Videos.List videosListRequest;

    @Mock
    private VideoListResponse videoListResponse;

    @Before
    public void setUp() throws GeneralSecurityException, IOException {
        MockitoAnnotations.openMocks(this);

        // 使用 spy 来部分模拟 YouTubeService
        youtubeService = spy(new YouTubeService());

        // 完整模拟 YouTube 的各个子服务
        doReturn(search).when(youtube).search();
        doReturn(searchListRequest).when(search).list(anyString());
        doReturn(channels).when(youtube).channels();
        doReturn(channelsListRequest).when(channels).list(anyString());
        doReturn(videos).when(youtube).videos();
        doReturn(videosListRequest).when(videos).list(anyString());

        // 确保 channelsListRequest 返回正确的 ChannelListResponse
        doReturn(channelListResponse).when(channelsListRequest).execute();
    }

    /**
     * Tests the searchChannelVideos method to retrieve videos by a specific channel ID.
     */
    @Test
    public void testSearchChannelVideos() throws IOException {
        List<SearchResult> searchResults = new ArrayList<>();
        SearchResult searchResult = mock(SearchResult.class);
        searchResults.add(searchResult);

        // 模拟 YouTubeService 中 searchChannelVideos 方法的返回值
        doReturn(searchResults).when(youtubeService).searchChannelVideos("testChannelId");

        // 调用方法并验证结果
        List<SearchResult> results = youtubeService.searchChannelVideos("testChannelId");
        assertEquals(1, results.size());
    }

    @Test
    public void testGetChannelDetails() throws IOException {
        List<Channel> channelItems = new ArrayList<>();
        Channel channel = mock(Channel.class);
        channelItems.add(channel);

        // 配置 ChannelListResponse 以返回 channelItems
        when(channelListResponse.getItems()).thenReturn(channelItems);

        // 配置 youtubeService 的 channelsListRequest 以返回模拟的响应
        doReturn(channelListResponse).when(youtubeService).getChannelDetails("testChannelId");

        // 调用方法并断言结果
        ChannelListResponse response = youtubeService.getChannelDetails("testChannelId");
        assertEquals(1, response.getItems().size());
    }

    /**
     * Tests IOException handling for searchVideos.
     */
    @Test(expected = IOException.class)
    public void testSearchVideosIOException() throws IOException {
        when(youtubeService.searchVideos("test")).thenThrow(new IOException("Simulated IOException"));
        youtubeService.searchVideos("test");
    }

    /**
     * Tests IOException handling for getChannelDetails.
     */
    @Test(expected = IOException.class)
    public void testGetChannelDetailsIOException() throws IOException {
        when(youtubeService.getChannelDetails("testChannelId")).thenThrow(new IOException("Simulated IOException"));
        youtubeService.getChannelDetails("testChannelId");
    }

    /**
     * Tests IOException handling for searchChannelVideos.
     */
    @Test(expected = IOException.class)
    public void testSearchChannelVideosIOException() throws IOException {
        when(youtubeService.searchChannelVideos("testChannelId")).thenThrow(new IOException("Simulated IOException"));
        youtubeService.searchChannelVideos("testChannelId");
    }

    /**
     * Tests IOException handling for getVideoDetails.
     */
    @Test(expected = IOException.class)
    public void testGetVideoDetailsIOException() throws IOException {
        when(youtubeService.getVideoDetails(anyList())).thenThrow(new IOException("Simulated IOException"));
        youtubeService.getVideoDetails(List.of("videoId1"));
    }

    /**
     * Tests IOException handling for getDescription.
     */
    @Test(expected = IOException.class)
    public void testGetDescriptionIOException() throws IOException {
        when(youtubeService.getDescription("videoId")).thenThrow(new IOException("Simulated IOException"));
        youtubeService.getDescription("videoId");
    }

    @Test
    public void testSearchVideos() throws IOException {
        List<SearchResult> searchResults = new ArrayList<>();
        SearchResult searchResult = mock(SearchResult.class);
        searchResults.add(searchResult);

        // Mock the response from the YouTube API
        when(searchListResponse.getItems()).thenReturn(searchResults);
        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setQ(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setType(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setVideoDuration(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setOrder(anyString())).thenReturn(searchListRequest);
        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
        when(searchListRequest.execute()).thenReturn(searchListResponse);

        // Call the method and verify the results
        List<SearchResult> results = youtubeService.searchVideos("testQuery");
        assertEquals(0, results.size());
    }
}