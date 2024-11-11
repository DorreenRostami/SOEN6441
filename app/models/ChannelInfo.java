package models;

/**
 * a class which contains all the information we need to keep about channels to show them in the main search page
 * @author Hao
 */
public class ChannelInfo {
    public String title;
    public String channelId;
    public String channelUrl;
    public String thumbnailUrl;
    public String description;
    public long subscriberCount;
    public long videoCount;
    public long viewCount;

    /**
     * Constructor for ChannelInfo
     * @param title Title of the channel
     * @param channelId ChannelId of the channel
     * @param channelUrl URL of the channel
     * @param thumbnailUrl URL of the channel's thumbnail
     * @param description Description of the channel
     * @param subscriberCount Number of subscribers of the channel
     * @param videoCount Number of videos uploaded by the channel
     * @param viewCount Number of views on the channel
     * @author Hao
     */
    public ChannelInfo(String title, String channelId, String channelUrl, String thumbnailUrl,
                       String description, long subscriberCount, long videoCount, long viewCount) {
        this.title = title;
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.subscriberCount = subscriberCount;
        this.videoCount = videoCount;
        this.viewCount = viewCount;
    }

    /**
     * Getters for ChannelInfo
     * @return The corresponding field of the ChannelInfo object
     * @author Hao
     */
    public String getTitle() { return title; }
    public String getChannelId() { return channelId; }
    public String getChannelUrl() { return channelUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getDescription() { return description; }
    public long getSubscriberCount() { return subscriberCount; }
    public long getVideoCount() { return videoCount; }
    public long getViewCount() { return viewCount; }
}