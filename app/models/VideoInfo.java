package models;

public class VideoInfo {
    private final String videoTitle;
    private final String videoUrl;
    private final String channelTitle;
    private final String channelUrl;
    private final String thumbnailUrl;
    private final String description;

    public VideoInfo(String videoTitle, String videoUrl, String channelTitle, String channelUrl, String thumbnailUrl, String description) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.channelTitle = channelTitle;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }

    public String getVideoTitle() { return videoTitle; }
    public String getVideoUrl() { return videoUrl; }
    public String getChannelTitle() { return channelTitle; }
    public String getChannelUrl() { return channelUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getDescription() { return description; }
}
