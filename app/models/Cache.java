package models;

import com.google.api.services.youtube.model.Video;
import services.YouTubeService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class implements a cache to optimize the request handling of the server.
 * @author Hamza Asghar Khan
 */
public class Cache {
    /**
     * A map containing all the queries that return a List of SearchResult objects as a response
     */
    private final static Map<String, SearchHistory> listCache = new HashMap<>();
    /**
     * A map containing all the queries that return a ChannelListResponse object as a response
     */
    private final static Map<String, ChannelInfo> channelCache = new HashMap<>();
    /**
     * A map containing all the videoIds mapped to their descriptions.
     */
    private final static Map<String, String> descriptionCache = new HashMap<>();
    /**
     * A map containing all the videoIds mapped to their according constructed Video object.
     */
    private final static Map<String, Video> videoCache = new HashMap<>();

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
    public static SearchHistory get(String query, boolean isChannelQuery) throws IOException {
//        LinkedList<VideoInfo> sampleResult = new LinkedList<>();
//        sampleResult.add(new VideoInfo("testTitle", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
//        sampleResult.add(new VideoInfo("test 2", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
//        return new SearchHistory("testQuery", sampleResult);
        String key = isChannelQuery ? "channel:" + query : "video:" + query;
        if (listCache.containsKey(key)){
            return listCache.get(key);
        }
        SearchHistory response;
        if (isChannelQuery){
            response = YouTubeService.searchChannelVideos(query);
        } else {
            response = YouTubeService.searchVideos(query);
        }
        listCache.put(key, response);
        return response;
    }

    public static void put(String query, SearchHistory response, boolean isChannelQuery){
        String key = isChannelQuery ? "channel:" + query : "video:" + query;
        listCache.put(key, response);
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
    public static ChannelInfo getChannelDetails(String channelId) throws IOException {
        if (channelCache.containsKey(channelId)){
            return channelCache.get(channelId);
        }
        ChannelInfo response = YouTubeService.getChannelDetails(channelId);
        channelCache.put(channelId, response);
        return response;
//        LinkedList<VideoInfo> sampleResult = new LinkedList<>();
//        sampleResult.add(new VideoInfo("testTitle", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
//        sampleResult.add(new VideoInfo("test 2", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
//        SearchHistory searches =  new SearchHistory("", sampleResult);
//        return new ChannelInfo("Pulkit Channel", "250", "obama", "https://picsum.photos/536/354", "Pulkit bhosdiwala", 200, 5000, 420, searches);
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
    public static String getDescription(String videoId) throws IOException{
        if (descriptionCache.containsKey(videoId)){
            return descriptionCache.get(videoId);
        }
        String description = YouTubeService.getDescription(videoId);
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
    public static Video getVideo(String videoId) throws IOException {
        if (videoCache.containsKey(videoId)) {
            return videoCache.get(videoId);
        }
        Video video = YouTubeService.getVideoDetails(Collections.singletonList(videoId)).get(0);
        videoCache.put(videoId, video);
        return video;
    }
}
