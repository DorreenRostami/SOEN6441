package services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class YouTubeService {
    private static final String API_KEY = "AIzaSyACVI8Yoz4mFuWy_ZRfXIIrohZgNtHLRyQ"; // API key
//    private static final String API_KEY = "AIzaSyDkLPt2l_05n_Y8POacidCn3snYAOyI3D4";
    private static final String APPLICATION_NAME = "Play YouTube Search";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final YouTube youtubeService;

    /**
     * Constructor for the YouTubeService class which initializes the YouTube service
     * @author Hao
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
                .setVideoDuration("medium") //filter out short videos
                .setOrder("date")
                .setMaxResults(10L)
                .execute();
        return response.getItems();
    }

    /**
     * Get the details of a channel
     * @param channelId the id of the channel
     * @return a ChannelListResponse object containing the details of the channel
     * @author Hao
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
     * @author Hao
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

    public List<Video> getVideoDetails(List<String> videoIds) throws IOException {
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
    public String getDescription(String videoId) throws IOException {
        YouTube.Videos.List request = youtubeService.videos().list("snippet");
        VideoListResponse response = request.setId(videoId).setKey(API_KEY).execute();
        List<Video> items = response.getItems();
        if (!items.isEmpty()){
            return items.get(0).getSnippet().getDescription();
        } else {
            return "";
        }
    }

//    /**
//     * Returns list of SearchResults for the provided tag
//     */
//    public List<SearchResult> searchVideosByTag(String tag) throws IOException {
//        YouTube.Search.List request = youtubeService.search().list("snippet");
//        SearchListResponse response = request
//                .setKey(API_KEY)
//                .setQ(tag)
//                .setType("video")
//                .setOrder("date")
//                .setMaxResults(10L)
//                .execute();
//        return response.getItems();
//    }
}