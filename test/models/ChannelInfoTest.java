package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link ChannelInfo} class
 * @author Hao
 */
public class ChannelInfoTest {

    private ChannelInfo channelInfo;
    private final String title = "Test Channel";
    private final String channelId = "12345";
    private final String channelUrl = "https://www.youtube.com/channel/12345";
    private final String thumbnailUrl = "https://example.com/thumbnail.jpg";
    private final String description = "This is a test channel.";
    private final long subscriberCount = 1000L;
    private final long videoCount = 50L;
    private final long viewCount = 100000L;
    private final SearchHistory videos = new SearchHistory("12345", null);

    @BeforeEach
    public void setUp() {
        // Initialize ChannelInfo object with test data
        channelInfo = new ChannelInfo(title, channelId, channelUrl, thumbnailUrl, description, subscriberCount, videoCount, viewCount, videos);
    }

    /**
     * Test the constructor of the ChannelInfo class
     * @author Yongqi Hao
     */
    @Test
    public void testConstructor() {
        // Verify constructor initializes fields correctly
        assertEquals(title, channelInfo.getTitle());
        assertEquals(channelId, channelInfo.getChannelId());
        assertEquals(channelUrl, channelInfo.getChannelUrl());
        assertEquals(thumbnailUrl, channelInfo.getThumbnailUrl());
        assertEquals(description, channelInfo.getDescription());
        assertEquals(subscriberCount, channelInfo.getSubscriberCount());
        assertEquals(videoCount, channelInfo.getVideoCount());
        assertEquals(viewCount, channelInfo.getViewCount());
        assertEquals(videos, channelInfo.getVideos());
    }


    /**
     * Test the getter methods of the ChannelInfo class
     * @author Yongqi Hao
     */
    @Test
    public void testGetTitle() {
        assertEquals(title, channelInfo.getTitle());
    }

    @Test
    public void testGetChannelId() {
        assertEquals(channelId, channelInfo.getChannelId());
    }

    @Test
    public void testGetChannelUrl() {
        assertEquals(channelUrl, channelInfo.getChannelUrl());
    }

    @Test
    public void testGetThumbnailUrl() {
        assertEquals(thumbnailUrl, channelInfo.getThumbnailUrl());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, channelInfo.getDescription());
    }

    @Test
    public void testGetSubscriberCount() {
        assertEquals(subscriberCount, channelInfo.getSubscriberCount());
    }

    @Test
    public void testGetVideoCount() {
        assertEquals(videoCount, channelInfo.getVideoCount());
    }

    @Test
    public void testGetViewCount() {
        assertEquals(viewCount, channelInfo.getViewCount());
    }
    @Test
    public void testGetVideos() {
        assertEquals(videos, channelInfo.getVideos());
    }
}