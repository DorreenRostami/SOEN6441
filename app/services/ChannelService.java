package services;

import com.google.api.services.youtube.model.Channel;
import models.Cache;
import models.ChannelInfo;
import models.SearchHistory;
import models.VideoInfo;

import java.io.IOException;
import java.util.List;

/**
 * a class which contains methods for getting channel information and searching for videos of a channel
 * through the Youtube API
 * @author Hao
 */
public class ChannelService {

    public static ChannelInfo getChannelInfo(Channel channel, SearchHistory videos) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }

        if (channel.getSnippet() == null || channel.getStatistics() == null) {
            throw new IllegalStateException("Channel snippet or statistics cannot be null");
        }

        return new ChannelInfo(
                channel.getSnippet().getTitle(),
                channel.getId(),
                "https://www.youtube.com/channel/" + channel.getId(),
                channel.getSnippet().getThumbnails() != null ?
                        channel.getSnippet().getThumbnails().getDefault().getUrl() : null,
                channel.getSnippet().getDescription(),
                channel.getStatistics().getSubscriberCount() != null ?
                        channel.getStatistics().getSubscriberCount().longValue() : 0L,
                channel.getStatistics().getVideoCount() != null ?
                        channel.getStatistics().getVideoCount().longValue() : 0L,
                channel.getStatistics().getViewCount() != null ?
                        channel.getStatistics().getViewCount().longValue() : 0L,
                videos
        );
    }

    /**
     * Get the channel information for a given channel ID
     * @param channelId The ID of the channel
     * @author Hao
     */
    public static List<VideoInfo> searchChannel(String channelId, Cache cache) throws IOException {
        // Fetch videos for the channel
        SearchHistory results = cache.getSearchHistory(channelId, true);
        List<VideoInfo> videoInfoList = results.getResults();

        if (videoInfoList.size() > 10) {
            videoInfoList = videoInfoList.subList(0, 10);
        }

        return videoInfoList;
    }
}
