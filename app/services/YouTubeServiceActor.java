package services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
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

    private static final String API_KEY = "AIzaSyACVI8Yoz4mFuWy_ZRfXIIrohZgNtHLRyQ";
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final YouTube youtubeService;

    // Cache to track videos for each user (max 100 videos per user)
    private final Map<ActorRef, LinkedList<Map<String, String>>> userCaches = new HashMap<>();

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
                .match(YoutubeProtocol.ClearCache.class, this::onClearCache)
                .build();
    }

    private void onSearchVideos(YoutubeProtocol.SearchVideos message) {
        try {
            System.out.println("Received search request from user: " + sender());

            // Get the user-specific cache
            LinkedList<Map<String, String>> userCache = userCaches.computeIfAbsent(sender(), key -> new LinkedList<>());

            // Perform search and filter results
            Set<String> existingVideoIds = userCache.stream()
                    .map(video -> video.get("videoId"))
                    .collect(Collectors.toSet());
            List<SearchResult> newVideos = searchUniqueVideos(message.query, existingVideoIds);

            // Transform results into detailed video data
            List<Map<String, String>> videoDataList = newVideos.stream()
                    .map(result -> {
                        Map<String, String> videoData = new HashMap<>();
                        String videoId = result.getId().getVideoId();
                        videoData.put("videoId", videoId);
                        videoData.put("title", result.getSnippet().getTitle());
                        videoData.put("thumbnailUrl", result.getSnippet().getThumbnails().getDefault().getUrl());
                        videoData.put("videoUrl", "https://www.youtube.com/watch?v=" + videoId);
                        videoData.put("description", result.getSnippet().getDescription());
                        videoData.put("channelTitle", result.getSnippet().getChannelTitle());
                        videoData.put("channelUrl", "https://www.youtube.com/channel/" + result.getSnippet().getChannelId());
                        return videoData;
                    })
                    .collect(Collectors.toList());

            // Add new videos to the user's cache
            synchronized (userCache) {
                userCache.addAll(0, videoDataList); // Add new videos to the top
                while (userCache.size() > 100) {
                    userCache.removeLast(); // Ensure cache size is max 100
                }
            }

            // Send only the 10 newly added videos
            List<Map<String, String>> newDisplayVideos = videoDataList.stream()
                    .limit(10)
                    .collect(Collectors.toList());
            sender().tell(new YoutubeProtocol.VideoSearchResults(newDisplayVideos), self());
        } catch (IOException e) {
            System.err.println("YouTube API error: " + e.getMessage());
            sender().tell(new YoutubeProtocol.ErrorMessage("Failed to search videos: " + e.getMessage()), self());
        }
    }

    private void onClearCache(YoutubeProtocol.ClearCache message) {
        System.out.println("Clearing cache for user: " + sender());
        userCaches.remove(sender());
    }

    private List<SearchResult> searchUniqueVideos(String query, Set<String> excludedVideoIds) throws IOException {
        List<SearchResult> uniqueResults = new ArrayList<>();
        String nextPageToken = null;

        do {
            // Perform API call
            YouTube.Search.List request = youtubeService.search().list("snippet");
            SearchListResponse response = request
                    .setKey(API_KEY)
                    .setQ(query)
                    .setType("video")
                    .setVideoDuration("medium")
                    .setOrder("date")
                    .setMaxResults(50L) // Fetch more videos to allow filtering
                    .setPageToken(nextPageToken)
                    .execute();

            // Filter out videos already in the view
            List<SearchResult> filteredResults = response.getItems().stream()
                    .filter(result -> !excludedVideoIds.contains(result.getId().getVideoId()))
                    .collect(Collectors.toList());

            uniqueResults.addAll(filteredResults);
            nextPageToken = response.getNextPageToken();

        } while (uniqueResults.size() < 10 && nextPageToken != null);

        return uniqueResults.stream().limit(10).collect(Collectors.toList()); // Return up to 10 videos
    }
}
