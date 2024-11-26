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
import java.util.ArrayList;
import java.util.List;

public class YouTubeServiceActor extends AbstractActor {

    private static final String API_KEY = "AIzaSyB5mlvHtkz-BD5LDx2xyJGirRQvl-Yz6GI";
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final YouTube youtubeService;

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

            List<SearchResult> results = searchVideos(message.query);
            List<String> videoTitles = new ArrayList<>();
            for (SearchResult result : results) {
                videoTitles.add(result.getSnippet().getTitle());
            }

            System.out.println("Search successful: " + videoTitles.size() + " titles found.");
            sender().tell(new YoutubeProtocol.VideoSearchResults(videoTitles), self());
        } catch (IOException e) {
            System.err.println("YouTube API error: " + e.getMessage());
            sender().tell(new YoutubeProtocol.ErrorMessage("Failed to search videos: " + e.getMessage()), self());
        }
    }

    private List<SearchResult> searchVideos(String query) throws IOException {
        YouTube.Search.List request = youtubeService.search().list("snippet");
        SearchListResponse response = request
                .setKey(API_KEY)
                .setQ(query)
                .setType("video")
                .setVideoDuration("medium") // Filter out short videos
                .setOrder("date")
                .setMaxResults(10L)
                .execute();

        System.out.println("YouTube API returned " + response.getItems().size() + " results.");
        return response.getItems();
    }
}
