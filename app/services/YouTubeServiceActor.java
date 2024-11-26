package services;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

public class YouTubeServiceActor extends AbstractActor {

    private static final String API_KEY = "AIzaSyB5mlvHtkz-BD5LDx2xyJGirRQvl-Yz6GI";
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final YouTube youtubeService;

    // Cache to track already returned video IDs for each query
    private final Map<String, Set<String>> queryCache = new HashMap<>();

    public YouTubeServiceActor() {
        try {
            this.youtubeService = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    request -> {}
            ).setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to initialize YouTubeServiceActor: " + e.getMessage(), e);
        }
    }

    public static Props props() {
        return Props.create(YouTubeServiceActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(YoutubeProtocol.SearchVideos.class, this::onSearchVideos)
                .build();
    }

    private void onSearchVideos(YoutubeProtocol.SearchVideos message) {
        try {
            System.out.println("Received search request: " + message.query);

            List<SearchResult> uniqueResults = searchUniqueVideos(message.query);
            List<String> videoTitles = uniqueResults.stream()
                    .map(result -> result.getSnippet().getTitle())
                    .collect(Collectors.toList());

            System.out.println("Search successful: " + videoTitles.size() + " unique titles found.");
            sender().tell(new YoutubeProtocol.VideoSearchResults(videoTitles), self());
        } catch (IOException e) {
            System.err.println("YouTube API error: " + e.getMessage());
            sender().tell(new YoutubeProtocol.ErrorMessage("Failed to search videos: " + e.getMessage()), self());
        }
    }

    private List<SearchResult> searchUniqueVideos(String query) throws IOException {
        // Get already returned video IDs for this query
        Set<String> returnedVideoIds = queryCache.getOrDefault(query, new HashSet<>());

        List<SearchResult> uniqueResults = new ArrayList<>();
        String nextPageToken = null;

        do {
            // Perform API call with pageToken
            YouTube.Search.List request = youtubeService.search().list("snippet");
            SearchListResponse response = request
                    .setKey(API_KEY)
                    .setQ(query)
                    .setType("video")
                    .setVideoDuration("medium")
                    .setOrder("date")
                    .setMaxResults(10L)
                    .setPageToken(nextPageToken)
                    .execute();

            // Filter results to exclude already returned videos
            List<SearchResult> filteredResults = response.getItems().stream()
                    .filter(result -> !returnedVideoIds.contains(result.getId().getVideoId()))
                    .collect(Collectors.toList());

            uniqueResults.addAll(filteredResults);

            // Add new video IDs to the cache
            returnedVideoIds.addAll(filteredResults.stream()
                    .map(result -> result.getId().getVideoId())
                    .collect(Collectors.toSet()));

            // Update nextPageToken for pagination
            nextPageToken = response.getNextPageToken();

        } while (uniqueResults.size() < 10 && nextPageToken != null);

        // Save the updated cache for this query
        queryCache.put(query, returnedVideoIds);

        return uniqueResults;
    }
}
