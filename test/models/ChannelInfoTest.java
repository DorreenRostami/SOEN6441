package models;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link ChannelInfo} class
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
    private SearchHistory videos;

    @Before
    public void setUp() {
        // Initialize SearchHistory with valid data
        videos = new SearchHistory("12345", List.of(
                new VideoInfo("Video 1", "https://www.youtube.com/watch?v=abc123", "Test Channel",
                        "https://www.youtube.com/channel/12345",
                        "https://example.com/video1.jpg", "Description 1", null),
                new VideoInfo("Video 2", "https://www.youtube.com/watch?v=def456", "Test Channel",
                        "https://www.youtube.com/channel/12345",
                        "https://example.com/video2.jpg", "Description 2", null)
        ));

        // Initialize ChannelInfo object with test data
        channelInfo = new ChannelInfo(title, channelId, channelUrl, thumbnailUrl, description, subscriberCount, videoCount, viewCount, videos);
    }

    /**
     * Test the constructor of the ChannelInfo class
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

    /**
     * Test the getHTML method of the ChannelInfo class
     */
    @Test
    public void testGetHTML() {
        String expectedHTML = "<button class=\"back-button\" onclick=\"return onBackClick()\">Back</button>" +
                "<div class=\"channel-info\">" +
                "<h1>" + title + "</h1>" +
                "<img src=\"" + thumbnailUrl + "\" alt=\"Channel Thumbnail\">" +
                "<p>" + description + "</p>" +
                "<p>Subscribers: " + subscriberCount + "</p>" +
                "<p>Videos: " + videoCount + "</p>" +
                "<p>Views: " + viewCount + "</p>" +
                "<a href=\"" + channelUrl + "\">Visit Channel</a>" +
                "</div>" +
                videos.getHTML(false);

        assertEquals(expectedHTML, channelInfo.getHTML());
    }
}