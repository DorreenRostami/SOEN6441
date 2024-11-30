//package services;
//
//import com.google.api.services.youtube.model.*;
//import models.Cache;
//import models.ChannelInfo;
//import models.SearchHistory;
//import models.VideoInfo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//
///**
// * Unit tests for the {@link ChannelService} class
// * @author Yongqi Hao
// */
//public class ChannelServiceTest {
//
//    private ChannelService channelService;
//
//    @Mock
//    private Cache cache;
//
//    @Mock
//    private Channel channel;
//
//    @Mock
//    private ChannelSnippet channelSnippet;
//
//    @Mock
//    private ChannelStatistics channelStatistics;
//
//    @Mock
//    private Thumbnail thumbnail;
//
//    @Mock
//    private ThumbnailDetails thumbnailDetails;
//
//    @Mock
//    private SearchResult searchResult;
//
//    @Mock
//    private SearchResultSnippet searchResultSnippet;
//
//    @Mock
//    private ResourceId resourceId;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        channelService = new ChannelService();
//
//        when(channel.getSnippet()).thenReturn(channelSnippet);
//        when(channel.getStatistics()).thenReturn(channelStatistics);
//        when(channelSnippet.getTitle()).thenReturn("Test Channel");
//        when(channel.getId()).thenReturn("12345");
//        when(channelSnippet.getThumbnails()).thenReturn(thumbnailDetails);
//        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
//        when(thumbnail.getUrl()).thenReturn("https://example.com/thumbnail.jpg");
//        when(channelSnippet.getDescription()).thenReturn("This is a test channel.");
//        when(channelStatistics.getSubscriberCount()).thenReturn(BigInteger.valueOf(1000));
//        when(channelStatistics.getVideoCount()).thenReturn(BigInteger.valueOf(50));
//        when(channelStatistics.getViewCount()).thenReturn(BigInteger.valueOf(100000));
//    }
//
//    /**
//     * Test the searchChannel method with search results
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testSearchChannel() throws IOException {
//
//        SearchHistory searchResults = new SearchHistory("12345", null);
//        searchResults.add(searchResult);
//
//        when(cache.get("12345", true)).thenReturn(searchResults);
//        when(searchResult.getSnippet()).thenReturn(searchResultSnippet);
//        when(searchResult.getId()).thenReturn(resourceId);
//        when(searchResultSnippet.getTitle()).thenReturn("Test Video Title");
//        when(resourceId.getVideoId()).thenReturn("video123");
//        when(searchResultSnippet.getChannelTitle()).thenReturn("Test Channel");
//        when(searchResultSnippet.getChannelId()).thenReturn("12345");
//        when(searchResultSnippet.getThumbnails()).thenReturn(thumbnailDetails);
//        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
//        when(thumbnail.getUrl()).thenReturn("https://example.com/video_thumbnail.jpg");
//        when(searchResultSnippet.getDescription()).thenReturn("This is a test video.");
//
//        List<VideoInfo> videoInfoList = ChannelService.searchChannel("12345", cache);
//
//        assertEquals(1, videoInfoList.size());
//        VideoInfo videoInfo = videoInfoList.get(0);
//        assertEquals("Test Video Title", videoInfo.getVideoTitle());
//        assertEquals("https://www.youtube.com/watch?v=video123", videoInfo.getVideoUrl());
//        assertEquals("Test Channel", videoInfo.getChannelTitle());
//        assertEquals("channel?query=12345", videoInfo.getChannelUrl());
//        assertEquals("https://example.com/video_thumbnail.jpg", videoInfo.getThumbnailUrl());
//        assertEquals("This is a test video.", videoInfo.getDescription());
//    }
//
//    /**
//     * Test the searchChannel method with more than 10 search results
//     * @throws IOException In case an IOException is thrown by the YouTube API.
//     * @author Yongqi Hao
//     */
//    @Test
//    public void testSearchChannelWithMoreThan10Results() throws IOException {
//
//        List<SearchResult> searchResults = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            SearchResult searchResultMock = mock(SearchResult.class);
//            SearchResultSnippet snippetMock = mock(SearchResultSnippet.class);
//            ResourceId resourceIdMock = mock(ResourceId.class);
//            ThumbnailDetails thumbnailsMock = mock(ThumbnailDetails.class);
//            Thumbnail defaultThumbnailMock = mock(Thumbnail.class);
//
//            when(searchResultMock.getSnippet()).thenReturn(snippetMock);
//            when(searchResultMock.getId()).thenReturn(resourceIdMock);
//            when(snippetMock.getTitle()).thenReturn("Test Video Title " + i);
//            when(resourceIdMock.getVideoId()).thenReturn("video" + i);
//            when(snippetMock.getChannelTitle()).thenReturn("Test Channel");
//            when(snippetMock.getChannelId()).thenReturn("12345");
//            when(snippetMock.getThumbnails()).thenReturn(thumbnailsMock);
//            when(thumbnailsMock.getDefault()).thenReturn(defaultThumbnailMock);
//            when(defaultThumbnailMock.getUrl()).thenReturn("https://example.com/video_thumbnail_" + i + ".jpg");
//            when(snippetMock.getDescription()).thenReturn("This is test video " + i);
//
//            searchResults.add(searchResultMock);
//        }
//
//        when(cache.get("12345", true)).thenReturn(searchResults);
//
//        List<VideoInfo> videoInfoList = ChannelService.searchChannel("12345", cache);
//
//        assertEquals(10, videoInfoList.size());
//        for (int i = 0; i < 10; i++) {
//            assertEquals("Test Video Title " + i, videoInfoList.get(i).getVideoTitle());
//            assertEquals("https://www.youtube.com/watch?v=video" + i, videoInfoList.get(i).getVideoUrl());
//            assertEquals("Test Channel", videoInfoList.get(i).getChannelTitle());
//            assertEquals("channel?query=12345", videoInfoList.get(i).getChannelUrl());
//            assertEquals("https://example.com/video_thumbnail_" + i + ".jpg", videoInfoList.get(i).getThumbnailUrl());
//            assertEquals("This is test video " + i, videoInfoList.get(i).getDescription());
//        }
//    }
//}