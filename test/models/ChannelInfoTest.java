package models;

import org.junit.Assert;
import org.junit.Test;

public class ChannelInfoTest {

    @Test
    public void testChannelInfoConstructorAndFields() {
        String title = "Test Channel";
        String channelId = "12345";
        String channelUrl = "https://www.youtube.com/channel/12345";
        String thumbnailUrl = "https://example.com/thumbnail.jpg";
        String description = "This is a test channel.";
        long subscriberCount = 1000L;
        long videoCount = 50L;
        long viewCount = 100000L;

        ChannelInfo channelInfo = new ChannelInfo(title, channelId, channelUrl, thumbnailUrl, description, subscriberCount, videoCount, viewCount);

        Assert.assertEquals(title, channelInfo.getTitle());
        Assert.assertEquals(channelId, channelInfo.getChannelId());
        Assert.assertEquals(channelUrl, channelInfo.getChannelUrl());
        Assert.assertEquals(thumbnailUrl, channelInfo.getThumbnailUrl());
        Assert.assertEquals(description, channelInfo.getDescription());
        Assert.assertEquals(subscriberCount, channelInfo.getSubscriberCount());
        Assert.assertEquals(videoCount, channelInfo.getVideoCount());
        Assert.assertEquals(viewCount, channelInfo.getViewCount());
    }
}