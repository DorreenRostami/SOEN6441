package models;

public class VideoInfo {
    public String videoTitle;
    public String videoUrl;
    public String channelTitle;
    public String channelId;
    public String channelUrl;
    public String thumbnailUrl;
    public String description;

    public VideoInfo(String videoTitle, String videoUrl, String channelTitle, String channelId, String channelUrl, String thumbnailUrl, String description) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.channelTitle = channelTitle;
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }
}