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
}