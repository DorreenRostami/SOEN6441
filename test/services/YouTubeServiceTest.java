//package services;
//
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.*;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.*;
//
///**
// * Unit tests for the {@link YouTubeService} class
// * @author Yongqi Hao
// */
//public class YouTubeServiceTest {
//
//    private YouTubeService youtubeService;
//
//    @Mock
//    private YouTube youtube;
//
//    @Mock
//    private YouTube.Search search;
//
//    @Mock
//    private YouTube.Search.List searchListRequest;
//
//    @Mock
//    private SearchListResponse searchListResponse;
//
//    @Mock
//    private YouTube.Channels channels;
//
//    @Mock
//    private YouTube.Channels.List channelsListRequest;
//
//    @Mock
//    private ChannelListResponse channelListResponse;
//
//    @Mock
//    private YouTube.Videos videos;
//
//    @Mock
//    private YouTube.Videos.List videosListRequest;
//
//    @Mock
//    private VideoListResponse videoListResponse;
//
//    @Before
//    public void setUp() throws IOException {
//        MockitoAnnotations.openMocks(this);
//        youtubeService = new YouTubeService(youtube);
//
//        // Mock YouTube service calls
//        when(youtube.search()).thenReturn(search);
//        when(search.list("snippet")).thenReturn(searchListRequest);
//        when(youtube.channels()).thenReturn(channels);
//        when(channels.list("snippet,statistics")).thenReturn(channelsListRequest);
//        when(youtube.videos()).thenReturn(videos);
//        when(videos.list("snippet")).thenReturn(videosListRequest);
//
//        // Mock chainable methods for search request
//        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setQ(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setType(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setVideoDuration(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setOrder(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
//
//        // Mock chainable methods for channels request
//        when(channelsListRequest.setKey(anyString())).thenReturn(channelsListRequest);
//        when(channelsListRequest.setId(anyString())).thenReturn(channelsListRequest);
//
//        // Mock chainable methods for videos request
//        when(videosListRequest.setKey(anyString())).thenReturn(videosListRequest);
//        when(videosListRequest.setId(anyString())).thenReturn(videosListRequest);
//    }
//
//    /**
//     * Test that an IOException is thrown when the searchVideos method encounters an IOException
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test(expected = IOException.class)
//    public void testSearchVideosIOException() throws IOException {
//        when(searchListRequest.execute()).thenThrow(new IOException("Simulated IOException"));
//        youtubeService.searchVideos("testQuery");
//    }
//
//    /**
//     * Test that an IOException is thrown when the getChannelDetails method encounters an IOException
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test(expected = IOException.class)
//    public void testGetChannelDetailsIOException() throws IOException {
//        when(channelsListRequest.execute()).thenThrow(new IOException("Simulated IOException"));
//        youtubeService.getChannelDetails("testChannelId");
//    }
//
//    /**
//     * Test that an IOException is thrown when the searchChannelVideos method encounters an IOException
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test(expected = IOException.class)
//    public void testSearchChannelVideosIOException() throws IOException {
//
//        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setChannelId(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setType(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setOrder(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
//
//        when(searchListRequest.execute()).thenThrow(new IOException("Simulated IOException"));
//        youtubeService.searchChannelVideos("testChannelId");
//    }
//
//    /**
//     * Test that an IOException is thrown when the getVideoDetails method encounters an IOException
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test(expected = IOException.class)
//    public void testGetVideoDetailsIOException() throws IOException {
//        when(videosListRequest.execute()).thenThrow(new IOException("Simulated IOException"));
//        youtubeService.getVideoDetails(List.of("videoId1"));
//    }
//
//    /**
//     * Test that an IOException is thrown when the getDescription method encounters an IOException
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test(expected = IOException.class)
//    public void testGetDescriptionIOException() throws IOException {
//        when(videosListRequest.execute()).thenThrow(new IOException("Simulated IOException"));
//        youtubeService.getDescription("videoId");
//    }
//
//    /**
//     * Test searchVideos method with a single search result
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testSearchVideos() throws IOException {
//        List<SearchResult> searchResults = new ArrayList<>();
//        SearchResult searchResult = mock(SearchResult.class);
//        searchResults.add(searchResult);
//
//        when(searchListRequest.execute()).thenReturn(searchListResponse);
//        when(searchListResponse.getItems()).thenReturn(searchResults);
//
//        List<SearchResult> results = youtubeService.searchVideos("testQuery");
//
//        assertEquals(1, results.size());
//        assertEquals(searchResult, results.get(0));
//    }
//
//    /**
//     * Test getChannelDetails
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testGetChannelDetails() throws IOException {
//        List<Channel> channelItems = new ArrayList<>();
//        Channel channel = mock(Channel.class);
//        channelItems.add(channel);
//
//        when(channelsListRequest.execute()).thenReturn(channelListResponse);
//        when(channelListResponse.getItems()).thenReturn(channelItems);
//
//        ChannelListResponse response = youtubeService.getChannelDetails("testChannelId");
//
//        assertEquals(1, response.getItems().size());
//        assertEquals(channel, response.getItems().get(0));
//    }
//
//    /**
//     * Test searchChannelVideos method with a single search result
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testSearchChannelVideos() throws IOException {
//        List<SearchResult> searchResults = new ArrayList<>();
//        SearchResult searchResult = mock(SearchResult.class);
//        searchResults.add(searchResult);
//
//        when(searchListRequest.setKey(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setChannelId(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setType(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setOrder(anyString())).thenReturn(searchListRequest);
//        when(searchListRequest.setMaxResults(anyLong())).thenReturn(searchListRequest);
//        when(searchListRequest.execute()).thenReturn(searchListResponse);
//        when(searchListResponse.getItems()).thenReturn(searchResults);
//
//        List<SearchResult> results = youtubeService.searchChannelVideos("testChannelId");
//
//        assertEquals(1, results.size());
//        assertEquals(searchResult, results.get(0));
//    }
//
//    /**
//     * Test getVideoDetails method with a single video
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testGetVideoDetails() throws IOException {
//        List<Video> videos = new ArrayList<>();
//        Video video = mock(Video.class);
//        videos.add(video);
//
//        when(videosListRequest.execute()).thenReturn(videoListResponse);
//        when(videoListResponse.getItems()).thenReturn(videos);
//
//        List<Video> results = youtubeService.getVideoDetails(List.of("videoId1"));
//
//        assertEquals(1, results.size());
//        assertEquals(video, results.get(0));
//    }
//
//    /**
//     * Test getDescription method
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testGetDescription() throws IOException {
//        List<Video> videos = new ArrayList<>();
//        Video video = mock(Video.class);
//        VideoSnippet snippet = mock(VideoSnippet.class);
//
//        when(video.getSnippet()).thenReturn(snippet);
//        when(snippet.getDescription()).thenReturn("Test Description");
//        videos.add(video);
//
//        when(videosListRequest.execute()).thenReturn(videoListResponse);
//        when(videoListResponse.getItems()).thenReturn(videos);
//
//        String description = youtubeService.getDescription("videoId");
//        assertEquals("Test Description", description);
//    }
//
//    @Test
//    public void testYouTubeServiceInitialization() throws GeneralSecurityException, IOException {
//        YouTubeService youTubeService = new YouTubeService();
//        assertNotNull(youTubeService, "YouTubeService 应该被成功初始化");
//    }
//}