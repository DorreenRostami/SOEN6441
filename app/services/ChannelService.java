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
        return new ChannelInfo(
                channel.getSnippet().getTitle(),
                channel.getId(),
                "https://www.youtube.com/channel/" + channel.getId(),
                channel.getSnippet().getThumbnails().getDefault().getUrl(),
                channel.getSnippet().getDescription(),
                channel.getStatistics().getSubscriberCount().longValue(),
                channel.getStatistics().getVideoCount().longValue(),
                channel.getStatistics().getViewCount().longValue(),
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
        SearchHistory results = cache.get(channelId, true);
        List<VideoInfo> videoInfoList = results.getResults();

        if (videoInfoList.size() > 10) {
            videoInfoList = videoInfoList.subList(0, 10);
        }

        return videoInfoList;
    }
}