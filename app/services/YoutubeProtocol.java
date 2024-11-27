package services;

import java.util.List;
import java.util.Map;

public class YoutubeProtocol {

    // Base interface for all messages
    public interface YoutubeMessage {}

    // Message to search for videos
    public static class SearchVideos implements YoutubeMessage {
        public final String query;

        public SearchVideos(String query) {
            this.query = query;
        }
    }

    // Response with video results
    public static class VideoSearchResults implements YoutubeMessage {
        public final List<Map<String, String>> videos;

        public VideoSearchResults(List<Map<String, String>> videos) {
            this.videos = videos;
        }
    }

    public static class ClearCache implements YoutubeMessage {}

    // Error message
    public static class ErrorMessage implements YoutubeMessage {
        public final String error;

        public ErrorMessage(String error) {
            this.error = error;
        }
    }
}
