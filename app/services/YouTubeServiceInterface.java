package services;

import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.List;

public interface YouTubeServiceInterface {
    List<SearchResult> searchVideos(String query) throws IOException;
    ChannelListResponse getChannelDetails(String channelId) throws IOException;
    List<SearchResult> searchChannelVideos(String channelId) throws IOException;
    List<Video> getVideoDetails(List<String> videoIds) throws IOException;
    String getDescription(String videoId) throws IOException;
}
