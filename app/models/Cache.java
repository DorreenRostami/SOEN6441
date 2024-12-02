package models;

import com.google.api.services.youtube.model.Video;
import services.YouTubeService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a cache to optimize the request handling of the server.
 * @author Hamza Asghar Khan
 */
public class Cache {
    /**
     * The time a cache value is valid (in milliseconds)
     */
    private static final long TTL = 60000;

    /**
     * Denotes a single cache entry.
     * @author Hamza Asghar Khan
     */
    private static class CacheEntry{
        String key;
        Object value;
        long timestamp;

        /**
         * Creates a cache entry and assigns it the current time as its timestamp
         * @param query Key/Query for the cache entry
         * @param value Value to be stored in cache
         * @author Hamza Asghar Khan
         */
        CacheEntry(String query, Object value){
            this.key = query;
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Map to store the CacheEntry Objects
     */
    private final static Map<String, CacheEntry> store = new HashMap<>();


    /**
     * Checks whether a given key has a valid cache entry
     * @param query Target Query/Key
     * @return true if and only if the object exists in cache and the TTL has not expired.
     * @author Hamza Asghar Khan
     */
    public static boolean hasAValidEntry(String query){
        if (store.containsKey(query)){
            CacheEntry entry = store.get(query);
            if ((System.currentTimeMillis() - entry.timestamp) < TTL){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given object is valid in the cache
     * @param object Target Object
     * @return true if and only if the object exists in cache and the TTL has not expired.
     * @author Hamza Asghar Khan
     */
    public static boolean hasAValidEntry(Object object){
        if (object instanceof SearchHistory){
            String channelQuery = "channel:::" + ((SearchHistory) object).getQuery();
            String videoQuery = "video:::" + ((SearchHistory) object).getQuery();
            return (hasAValidEntry(channelQuery) || hasAValidEntry(videoQuery));
        } else if (object instanceof ChannelInfo){
            String channelInfoQuery = "channelInfo:::" + ((ChannelInfo) object).getChannelId();
            return hasAValidEntry(channelInfoQuery);
        } else if (object instanceof String){
            String descriptionQuery = "description:::" + object;
            return hasAValidEntry(descriptionQuery);
        }
        return false;
    }

    /**
     * Places a given key/value pair in the cache
     * @param key Target Key
     * @param value Target Value
     * @author Hamza Asghar Khan
     */
    private static void put(String key, Object value){
        CacheEntry entry = new CacheEntry(key, value);
        store.put(key, entry);
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
    public static SearchHistory getSearchHistory(String query, boolean isChannelQuery) throws IOException {
        String key = isChannelQuery ? "channel:::" + query : "video:::" + query;
        if (hasAValidEntry(key)){
            return (SearchHistory) store.get(key).value;
        }
        SearchHistory response;
        if (isChannelQuery){
            response = YouTubeService.searchChannelVideos(query);
        } else {
            response = YouTubeService.searchVideos(query);
        }
        put(key, response);
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
    public static ChannelInfo getChannelDetails(String channelId) throws IOException {
        String key = "channelInfo:::" + channelId;
        if (hasAValidEntry(key)){
            return (ChannelInfo) store.get(key).value;
        }
        ChannelInfo response = YouTubeService.getChannelDetails(channelId);
        put(key, response);
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
    public static String getDescription(String videoId) throws IOException{
        String key = "description:::" + videoId;
        if (hasAValidEntry(key)){
            return (String) store.get(key).value;
        }
        String description = YouTubeService.getDescription(videoId);
        put(key, description);
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
        String key = "videoDetail:::" + videoId;
        if (hasAValidEntry(key)){
            return (Video) store.get(key).value;
        }
        Video video = YouTubeService.getVideoDetails(Collections.singletonList(videoId)).get(0);
        put(key, video);
        return video;
    }
}
