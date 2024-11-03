package controllers;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeService {
    private static final String API_KEY = "AIzaSyCxx9hUhwCa4RlyJKp3tps1Q7xW398bxsc"; // 使用你的 API Key
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final YouTube youtubeService;

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
}