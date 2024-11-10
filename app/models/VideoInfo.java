package models;

import java.io.IOException;
import java.util.List;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Video;

/**
 * Class for information about a YouTube video
 * @author Hao & Dorreen
 */
public class VideoInfo {
    private String videoTitle;
    private String videoUrl;
    private String channelTitle;
    private String channelUrl;
    private String thumbnailUrl;
    private String description;
    private List<String> tags;
    private String tagsUrl;

    public VideoInfo(String videoTitle, String videoUrl, String channelTitle, String channelUrl, String thumbnailUrl,
                     String description, String tagsUrl) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.channelTitle = channelTitle;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.tagsUrl = tagsUrl;
    }

    public VideoInfo(SearchResult searchResult) {
        SearchResultSnippet snippet = searchResult.getSnippet();
        String videoId = searchResult.getId().getVideoId();
        this.videoTitle = snippet.getTitle();
        this.videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        this.channelTitle = snippet.getChannelTitle();
        this.channelUrl = "https://www.youtube.com/channel/" + snippet.getChannelId();
        this.thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
        this.description = snippet.getDescription();
        this.tagsUrl = "/video?videoId=" + videoId;
    }

    public VideoInfo(SearchResult searchResult, String description) {
        this(searchResult);
        this.description = description;
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

    public String getTagsUrl() {
        return tagsUrl;
    }
}