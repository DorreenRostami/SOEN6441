package models;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link VideoInfo} class
 * @author Yongqi Hao
 */
public class VideoInfoTest extends WithApplication {

    private VideoInfo videoInfo;

    private final String videoTitle = "Sample Video";
    private final String videoUrl = "https://www.youtube.com/watch?v=sample";
    private final String channelTitle = "Sample Channel";
    private final String channelUrl = "https://www.youtube.com/channel/sample";
    private final String thumbnailUrl = "https://img.youtube.com/vi/sample/hqdefault.jpg";
    private final String description = "This is a sample description.";
    private final String tagsUrl = "https://www.youtube.com/tags/sample";

    @Before
    public void setUp() {
        videoInfo = new VideoInfo(videoTitle, videoUrl, channelTitle, channelUrl, thumbnailUrl, description, tagsUrl);
    }


    /**
     * Test the constructor with all parameters
     * @author Yongqi Hao
     */
    @Test
    public void testConstructorWithSearchResult() {

        SearchResult mockResult = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ThumbnailDetails thumbnailDetails = mock(ThumbnailDetails.class);
        Thumbnail thumbnail = mock(Thumbnail.class);
        ResourceId resourceId = mock(ResourceId.class);

        when(mockResult.getSnippet()).thenReturn(snippet);
        when(mockResult.getId()).thenReturn(resourceId);
        when(resourceId.getVideoId()).thenReturn("sample");
        when(snippet.getTitle()).thenReturn(videoTitle);
        when(snippet.getChannelTitle()).thenReturn(channelTitle);
        when(snippet.getChannelId()).thenReturn("sample");
        when(snippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn(thumbnailUrl);
        when(snippet.getDescription()).thenReturn(description);

        VideoInfo videoInfoFromResult = new VideoInfo(mockResult);

        assertEquals(videoTitle, videoInfoFromResult.getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=sample", videoInfoFromResult.getVideoUrl());
        assertEquals(channelTitle, videoInfoFromResult.getChannelTitle());
        assertEquals(thumbnailUrl, videoInfoFromResult.getThumbnailUrl());
        assertEquals(description, videoInfoFromResult.getDescription());
//        assertEquals("/video?videoId=sample", videoInfoFromResult.getTagsUrl());
    }

    @Test
    public void testConstructorWithSearchResultAndDescription() {

        SearchResult mockResult = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ThumbnailDetails thumbnailDetails = mock(ThumbnailDetails.class);
        Thumbnail thumbnail = mock(Thumbnail.class);
        ResourceId resourceId = mock(ResourceId.class);

        when(mockResult.getSnippet()).thenReturn(snippet);
        when(mockResult.getId()).thenReturn(resourceId);
        when(resourceId.getVideoId()).thenReturn("sample");
        when(snippet.getTitle()).thenReturn(videoTitle);
        when(snippet.getChannelTitle()).thenReturn(channelTitle);
        when(snippet.getChannelId()).thenReturn("sample");
        when(snippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn(thumbnailUrl);
        when(snippet.getDescription()).thenReturn("Original description");


        VideoInfo videoInfoFromResultWithDesc = new VideoInfo(mockResult, description);

        assertEquals(videoTitle, videoInfoFromResultWithDesc.getVideoTitle());
        assertEquals("https://www.youtube.com/watch?v=sample", videoInfoFromResultWithDesc.getVideoUrl());
        assertEquals(channelTitle, videoInfoFromResultWithDesc.getChannelTitle());
        assertEquals(thumbnailUrl, videoInfoFromResultWithDesc.getThumbnailUrl());
        assertEquals(description, videoInfoFromResultWithDesc.getDescription());
//        assertEquals("/video?videoId=sample", videoInfoFromResultWithDesc.getTagsUrl());
    }

    @Test
    public void testConstructorWithNullValues() {

        SearchResult mockResult = mock(SearchResult.class);
        SearchResultSnippet snippet = mock(SearchResultSnippet.class);
        ThumbnailDetails thumbnailDetails = mock(ThumbnailDetails.class);
        ResourceId resourceId = mock(ResourceId.class);

        when(mockResult.getSnippet()).thenReturn(snippet);
        when(mockResult.getId()).thenReturn(resourceId);
        when(resourceId.getVideoId()).thenReturn(null);
        when(snippet.getTitle()).thenReturn(null);
        when(snippet.getChannelTitle()).thenReturn(null);
        when(snippet.getChannelId()).thenReturn(null);
        when(snippet.getThumbnails()).thenReturn(thumbnailDetails);
        when(thumbnailDetails.getDefault()).thenReturn(null);
        when(snippet.getDescription()).thenReturn(null);

        VideoInfo videoInfoFromNullResult = new VideoInfo(mockResult);


        assertNull(videoInfoFromNullResult.getVideoTitle());
        assertNull(videoInfoFromNullResult.getVideoUrl());
        assertNull(videoInfoFromNullResult.getChannelTitle());
        assertNull(videoInfoFromNullResult.getChannelUrl());
        assertNull(videoInfoFromNullResult.getThumbnailUrl());
        assertNull(videoInfoFromNullResult.getDescription());
//        assertEquals("/video?videoId=null", videoInfoFromNullResult.getTagsUrl());
    }


    /**
     * Test the getters of the VideoInfo class
     * @author Yongqi Hao
     */
    @Test
    public void testGetVideoTitle() {
        assertEquals(videoTitle, videoInfo.getVideoTitle());
    }

    @Test
    public void testGetVideoUrl() {
        assertEquals(videoUrl, videoInfo.getVideoUrl());
    }

    @Test
    public void testGetChannelTitle() {
        assertEquals(channelTitle, videoInfo.getChannelTitle());
    }

    @Test
    public void testGetChannelUrl() {
        assertEquals(channelUrl, videoInfo.getChannelUrl());
    }

    @Test
    public void testGetThumbnailUrl() {
        assertEquals(thumbnailUrl, videoInfo.getThumbnailUrl());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, videoInfo.getDescription());
    }

    @Test
    public void testGetTags() {
        assertNull(videoInfo.getTags()); //TODO: set tags if you want to
    }

    @Test
    public void testGetTagsUrl() {
        assertEquals(tagsUrl, videoInfo.getTagsUrl());
    }

    /**
     * test the equals method when two videos are the same
     * @author Dorreen
     */
    @Test
    public void testEquals_SameVideoInfo() {
        VideoInfo video1 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl1");
        VideoInfo video2 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl1");
        assertTrue(video1.equals(video2));
    }

    /**
     * test the equals method when two videos are the same
     * @author Dorreen
     */
    @Test
    public void testEquals_DiffURLInfo() {
        VideoInfo video1 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl1");
        VideoInfo video2 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl2");
        assertFalse(video1.equals(video2));
    }
}