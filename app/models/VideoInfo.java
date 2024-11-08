package models;

import java.util.List;

public class VideoInfo {
    private String videoTitle;
    private String videoUrl;
    private String channelTitle;
    private String channelUrl;
    private String thumbnailUrl;
    private String description;
    private List<String> tags;

    public VideoInfo(String videoTitle, String videoUrl, String channelTitle, String channelUrl, String thumbnailUrl, String description, List<String> tags) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.channelTitle = channelTitle;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.tags = tags;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }
}