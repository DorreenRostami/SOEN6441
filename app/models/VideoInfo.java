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
        String videoId = searchResult.getId() != null ? searchResult.getId().getVideoId() : null;
        this.videoTitle = snippet != null ? snippet.getTitle() : null;
        this.videoUrl = videoId != null ? "https://www.youtube.com/watch?v=" + videoId : null;
        this.channelTitle = snippet != null ? snippet.getChannelTitle() : null;
        this.channelUrl = snippet != null && snippet.getChannelId() != null
                ? "https://www.youtube.com/channel/" + snippet.getChannelId()
                : null;
        this.thumbnailUrl = snippet != null && snippet.getThumbnails() != null
                && snippet.getThumbnails().getDefault() != null
                ? snippet.getThumbnails().getDefault().getUrl()
                : null;
        this.description = snippet != null ? snippet.getDescription() : null;
        this.tagsUrl = videoId != null ? "/video?videoId=" + videoId : "/video?videoId=null";
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