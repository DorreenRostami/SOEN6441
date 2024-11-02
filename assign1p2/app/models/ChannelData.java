package models;

public class ChannelData {
    public String title;
    public String videoUrl;
    public String channelTitle;
    public String channelId;
    public String channelUrl;
    public String thumbnailUrl;
    public String description;

    public ChannelData(String title, String videoUrl, String channelTitle,
                     String channelId,
                     String channelUrl, String thumbnailUrl, String description) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.channelTitle = channelTitle;
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }
}