package models;

/**
 * a class which contains all the information we need to keep about channels to show them in the main search page
 * @author Hao
 */
public class ChannelInfo {
    private String title;
    private String channelId;
    private String channelUrl;
    private String thumbnailUrl;
    private String description;
    private long subscriberCount;
    private long videoCount;
    private long viewCount;
    private SearchHistory videos;

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
                       String description, long subscriberCount, long videoCount, long viewCount, SearchHistory videos) {
        this.title = title;
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.subscriberCount = subscriberCount;
        this.videoCount = videoCount;
        this.viewCount = viewCount;
        this.videos = videos;
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
    public SearchHistory getVideos(){return videos;}

    public String getHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<button class=\"back-button\" onclick=\"return onBackClick()\">Back</button>");

        // Add HTML to display the channel details
        html.append("<div class=\"channel-info\">");
        html.append("<h1>").append(title).append("</h1>");
        html.append("<img src=\"").append(thumbnailUrl).append("\" alt=\"Channel Thumbnail\">");
        html.append("<p>").append(description).append("</p>");
        html.append("<p>Subscribers: ").append(subscriberCount).append("</p>");
        html.append("<p>Videos: ").append(videoCount).append("</p>");
        html.append("<p>Views: ").append(viewCount).append("</p>");
        html.append("<a href=\"").append(channelUrl).append("\">Visit Channel</a>");
        html.append("</div>");

        // Add HTML to display the videos
        html.append(videos.getHTML(false));

        return html.toString();
    }
}
