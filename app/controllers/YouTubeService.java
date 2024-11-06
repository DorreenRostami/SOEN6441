package controllers;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import models.ChannelInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class YouTubeService {
    private static final String API_KEY = "AIzaSyCxx9hUhwCa4RlyJKp3tps1Q7xW398bxsc"; // API key
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final YouTube youtubeService;

    /**
     * Constructor for the YouTubeService class which initializes the YouTube service
     * @author Dorreen - initial implementation of searchVideos
     * @author Hao - added channel video search and channel details
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

    public ChannelListResponse getChannelDetails(String channelId) throws IOException {
        YouTube.Channels.List request = youtubeService.channels().list("snippet,statistics");
        ChannelListResponse response = request
                .setKey(API_KEY)
                .setId(channelId)
                .execute();

        return response;
    }
}