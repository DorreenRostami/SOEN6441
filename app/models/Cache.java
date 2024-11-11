package models;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import services.YouTubeService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements a cache to optimize the request handling of the server.
 * @author Hamza Asghar Khan
 */
public class Cache {
    /**
     * A map containing all the queries that return a List of SearchResult objects as a response
     */
    private final Map<String, List<SearchResult>> listCache = new HashMap<>();
    /**
     * A map containing all the queries that return a ChannelListResponse object as a response
     */
    private final Map<String, ChannelListResponse> channelCache = new HashMap<>();
    /**
     * A map containing all the videoIds mapped to their descriptions.
     */
    private final Map<String, String> descriptionCache = new HashMap<>();
    /**
     * A map containing all the videoIds mapped to their according constructed Video object.
     */
    private final Map<String, Video> videoCache = new HashMap<>();

    private final YouTubeService youTubeService;

    /**
     * Public constructor for Cache
     * @param youTubeService YouTubeService Object
     * @author Hamza Asghar Khan
     */
    @Inject
    public Cache(YouTubeService youTubeService){
        this.youTubeService = youTubeService;
    }

    /**
     * Returns the response for the provided query. This method takes a query and a boolean to denote whether the query
     * pertains to videos from a channel or generic videos search. That query is then checked in the cache. If the query
     * results in a cache hit, the response is returned from the cache. In case of a cache miss, the cache is
     * populated through a response from YouTube's API and then the response is returned.
     * @param query Search Query
     * @param isChannelQuery Boolean to indicate whether the query relates to videos from a specific channel or not
     * @return List of SearchResult objects pertaining to the provided query
     * @throws IOException In case of an IOException caused by the YouTube API.
     * @author Hamza Asghar Khan
     */
    public List<SearchResult> get(String query, boolean isChannelQuery) throws IOException {
        String key = isChannelQuery ? "channel:" + query : "video:" + query;
        if (listCache.containsKey(key)){
            return listCache.get(key);
        }
        List<SearchResult> response;
        if (isChannelQuery){
            response = youTubeService.searchChannelVideos(query);
        } else {
            response = youTubeService.searchVideos(query);
        }
        listCache.put(key, response);
        return response;
    }

    /**
     * Retrieves the details for the provided YouTube channel. This method takes a query that identifies a YouTube
     * channel. That query is then checked in the cache. If the query results in a cache hit, the response is returned
     * from the cache. In case of a cache miss, the cache is populated through a response from YouTube's API and
     * then the response is returned.
     * @param channelId Target ChannelId
     * @return ChannelListResponse Object containing the details of the channel
     * @throws IOException In case of an IOException caused by the YouTube API.
     * @author Hamza Asghar Khan
     */
    public ChannelListResponse getChannelDetails(String channelId) throws IOException {
        if (channelCache.containsKey(channelId)){
            return channelCache.get(channelId);
        }
        ChannelListResponse response = youTubeService.getChannelDetails(channelId);
        channelCache.put(channelId, response);
        return response;
    }

    /**
     * Retrieves the description for the provided videoId. In the event of a cache hit, the description is fetched
     * from the cache. In the event of a cache miss, the cache is populated using the YouTube API and the description is
     * returned
     * @param videoId Target VideoId
     * @return Description of the request video
     * @throws IOException In case of API failures
     * @author Hamza Asghar Khan
     */
    public String getDescription(String videoId) throws IOException{
        if (descriptionCache.containsKey(videoId)){
            return descriptionCache.get(videoId);
        }
        String description = youTubeService.getDescription(videoId);
        descriptionCache.put(videoId, description);
        return description;
    }

    /**
     * Retrieve Video object for the provided videoId. In the event of a cache hit, the according Video object is
     * fetched from the cache. In the event of a cache miss, the cache is populated using the YouTube API
     * @param videoId Target VideoId
     * @return Video object for the request video
     * @throws IOException In case of API failures
     * @author Yi Tian
     */
    public Video getVideo(String videoId) throws IOException {
        if (videoCache.containsKey(videoId)) {
            return videoCache.get(videoId);
        }
        Video video = youTubeService.getVideoDetails(Collections.singletonList(videoId)).get(0);
        videoCache.put(videoId, video);
        return video;
    }
}
