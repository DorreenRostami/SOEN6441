package services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.ChannelInfo;
import models.SearchHistory;
import models.VideoInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class contains methods for searching for videos and getting channel information through the Youtube API
 * @author Dorreen Rostami
 */
public class YouTubeService {
    private static final String API_KEY = "AIzaSyCUvVF0909szfTqcxgYzXIed4oflnNAtgY"; // API key
//    private static final String API_KEY = "AIzaSyACVI8Yoz4mFuWy_ZRfXIIrohZgNtHLRyQ";
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final YouTube youtubeService;

    static {
        try {
            youtubeService = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    request -> {})
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search for videos based on a query
     * @param query the query to search for
     * @return a list of SearchResult containing the videos that match the query
     * @throws IOException If an error occurs while fetching the search results
     * @author Hao
     */
    public static SearchHistory searchVideos(String query, long len) throws IOException {
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request
                .setKey(API_KEY)
                .setQ(query)
                .setType("video")
                .setVideoDuration("medium") //filter out short videos
                .setOrder("date")
                .setMaxResults(len)
                .execute();
        List<VideoInfo> videoInfoList = new ArrayList<>();
        for (SearchResult result: response.getItems()){
            videoInfoList.add(new VideoInfo(result));
        }
        return new SearchHistory(query, videoInfoList);
    }

    /**
     * Searches for videos on YouTube based on the given query and retrieves up to 10 results by default.
     * This method acts as a convenience wrapper for the overloaded {@link #searchVideos(String, long)}
     * method, providing a default value of 10 for the maximum number of results.
     *
     * @param query the query to search for
     * @return a list of SearchResult containing the videos that match the query
     * @throws IOException If an error occurs while fetching the search results
     * @author Dorreen
     */
    public static SearchHistory searchVideos(String query) throws IOException {
        return searchVideos(query, 10L);
    }

    /**
     * Get the details of a channel
     * @param channelId the id of the channel
     * @return a ChannelListResponse object containing the details of the channel
     * @author Hao
     */
    public static ChannelInfo getChannelDetails(String channelId) throws IOException {
        YouTube.Channels.List request = youtubeService.channels().list("snippet,statistics");
        ChannelListResponse response = request
                .setKey(API_KEY)
                .setId(channelId)
                .execute();
        SearchHistory videos = searchChannelVideos(channelId);
        Channel channel = response.getItems().get(0);
        return ChannelService.getChannelInfo(channel, videos);
    }

    /**
     * Print for videos in a channel
     * @param channelId the id of the channel
     * @return a list of search results containing the videos in the channel
     * @author Hao
     */
    public static SearchHistory searchChannelVideos(String channelId) throws IOException {
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request
                .setKey(API_KEY)
                .setChannelId(channelId)
                .setType("video")
                .setOrder("date")
                .setMaxResults(10L)
                .execute();
        List<VideoInfo> videoInfoList = new ArrayList<>();
        for (SearchResult result: response.getItems()){
            videoInfoList.add(new VideoInfo(result));
        }
        return new SearchHistory(channelId, videoInfoList);
    }

    /**
     * Get the details of a video
     * @param videoIds the ids of the videos
     * @return a list of Video objects containing the details of the videos
     * @throws IOException If an error occurs while fetching the video details
     * @author Hao
     */
    public static List<Video> getVideoDetails(List<String> videoIds) throws IOException {
        YouTube.Videos.List request = youtubeService.videos().list("snippet");
        VideoListResponse response = request
                .setKey(API_KEY)
                .setId(String.join(",", videoIds))
                .execute();
        return response.getItems();
    }

    /**
     * Returns the description for the provided video
     * @param videoId Target VideoId
     * @return Description of the video or an empty string in case of no description
     * @throws IOException In the event that the API fails.
     * @author Hamza Asghar Khan
     */
    public static String getDescription(String videoId) throws IOException {
        YouTube.Videos.List request = youtubeService.videos().list("snippet");
        VideoListResponse response = request.setId(videoId).setKey(API_KEY).execute();
        List<Video> items = response.getItems();
        if (!items.isEmpty()){
            return items.get(0).getSnippet().getDescription();
        } else {
            return "";
        }
    }
}