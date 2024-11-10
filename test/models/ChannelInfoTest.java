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

    @BeforeEach
    public void setUp() {
        channelInfo = new ChannelInfo(title, channelId, channelUrl, thumbnailUrl, description, subscriberCount, videoCount, viewCount);
    }

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
}