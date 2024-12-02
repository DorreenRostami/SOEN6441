package models;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import java.util.Iterator;
import java.util.List;

import static models.SearchHistory.escapeJson;

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

    /**
     * Constructor for VideoInfo
     * @param videoTitle The title of the video
     * @param videoUrl The URL of the video linking to YouTube
     * @param channelTitle The title of the channel
     * @param channelUrl The URL of the channel
     * @param thumbnailUrl The URL of the thumbnail
     * @param description The description of the video
     * @param tagsUrl The URL of the tags
     * @author Dorreen Rostami
     */
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

    /**
     * Constructor for VideoInfo
     * @param searchResult The search result from the YouTube API
     * @author Yi Tian & Yongqi Hao
     */
    public VideoInfo(SearchResult searchResult) {
        SearchResultSnippet snippet = searchResult.getSnippet();
        String videoId = searchResult.getId() != null ? searchResult.getId().getVideoId() : null;
        this.videoTitle = snippet != null ? snippet.getTitle() : null;
        this.videoUrl = videoId != null ? "https://www.youtube.com/watch?v=" + videoId : null;
        this.channelTitle = snippet != null ? snippet.getChannelTitle() : null;
        this.channelUrl = snippet != null && snippet.getChannelId() != null
                ? snippet.getChannelId()
                : null;
        this.thumbnailUrl = snippet != null && snippet.getThumbnails() != null
                && snippet.getThumbnails().getDefault() != null
                ? snippet.getThumbnails().getDefault().getUrl()
                : null;
        this.description = snippet != null ? snippet.getDescription() : null;
        this.tagsUrl = videoId != null ? videoId : "null";
    }

    /**
     * Constructor for VideoInfo
     * @param searchResult The search result from the YouTube API
     * @param description The description to replace the one from the search result
     * @author Yi Tian
     */
    public VideoInfo(SearchResult searchResult, String description) {
        this(searchResult);
        this.description = description;
    }


    /**
     * Getters for VideoInfo
     * @return The corresponding field of the VideoInfo object
     * @author Dorreen Rostami & Yongqi Hao & Yi Tian
     */
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

    public String getHTML() {
        return "<div class=\"video-result\">" +
                "<div class=\"video-result-left\">" +
                "<p class=\"video-result-left-title\">Video: <a href=\"" + videoUrl + "\">" + videoTitle + "</a></p>" +
                "<p class=\"video-result-left-channel\">Channel: <a href=\"#\" onclick=\"return onChannelClick('" + channelUrl + "')\">" + channelTitle + "</a></p>" +
                "<p class=\"video-result-left-tags\"><a href=\"#\" onclick=\"return onTagsClick('" + tagsUrl + "')\"> Tags </a></p>" +
                "<p class=\"video-result-left-description\">" + description + "</p>" +
                "</div>" +
                "<div class=\"video-result-right\">" +
                "<a href=\"" + videoUrl + "\"><img src=\"" + thumbnailUrl + "\"/></a>" +
                "</div>" +
                "</div>";
    }

    public String getJson(){
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"videoTitle\":\"" + escapeJson(videoTitle) + "\",")
                .append("\"videoUrl\":\"" + escapeJson(videoUrl) + "\",")
                .append("\"channelTitle\":\"" + escapeJson(channelTitle) + "\",")
                .append("\"channelUrl\":\"" + escapeJson(channelUrl) + "\",")
                .append("\"thumbnailUrl\":\"" + escapeJson(thumbnailUrl) + "\",")
                .append("\"description\":\"" + escapeJson(description) + "\",")
                .append("\"tagsUrl\":\"" + escapeJson(tagsUrl) + "\"")
                .append("}");
        return json.toString();
    }

    /**
     * Returns true if and only if two SearchHistory objects are equal.
     * @param that Other SearchHistory Object
     * @return true if and only if the two objects are equal.
     * @author Hamza Asghar Khan
     */
    public boolean equals(VideoInfo that){
        return (
                this.channelTitle.equals(that.channelTitle) &&
                this.videoUrl.equals(that.videoUrl) &&
                this.videoTitle.equals(that.videoTitle) &&
                this.channelUrl.equals(that.channelUrl) &&
                this.description.equals(that.description) &&
                this.thumbnailUrl.equals(that.thumbnailUrl) &&
                this.tagsUrl.equals(that.tagsUrl) &&
                this.tags.equals(that.tags)
        );
    }
}