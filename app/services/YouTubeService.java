package services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class YouTubeService {
    private static final String API_KEY = "AIzaSyACVI8Yoz4mFuWy_ZRfXIIrohZgNtHLRyQ"; // API key
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final YouTube youtubeService;

    /**
     * Constructor for the YouTubeService class which initializes the YouTube service
     * @author Dorreen - initial implementation of searchVideos
     */
    public YouTubeService() throws GeneralSecurityException, IOException {
        youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                request -> {}
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public List<SearchResult> searchVideos(String query) throws IOException {
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request
                .setKey(API_KEY)
                .setQ(query)
                .setType("video")
                .setMaxResults(10L)
                .execute();
        return response.getItems();
    }
    /**
     * Get the details of a channel
     * @param channelId the id of the channel
     * @return a ChannelListResponse object containing the details of the channel
     * @author Hao - initial implementation and changed channelURL
     * so that clicking on it opens a web page containing all available profile
     */
    public ChannelListResponse getChannelDetails(String channelId) throws IOException {
        YouTube.Channels.List request = youtubeService.channels().list("snippet,statistics");
        ChannelListResponse response = request
                .setKey(API_KEY)
                .setId(channelId)
                .execute();
        return response;
    }
    /**
     * Print for videos in a channel
     * @param channelId the id of the channel
     * @return a list of search results containing the videos in the channel
     * @author Hao - changed channelURL so that clicking on it opens a web page containing all available profile
     * information about a channel instead of opening the channel in Youtube
     */
    public List<SearchResult> searchChannelVideos(String channelId) throws IOException {
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request
                .setKey(API_KEY)
                .setChannelId(channelId)
                .setType("video")
                .setOrder("date")
                .setMaxResults(10L)
                .execute();

        return response.getItems();
    }
}