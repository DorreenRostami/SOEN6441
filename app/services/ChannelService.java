package services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchResult;
import models.Cache;
import models.ChannelInfo;
import models.VideoInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a class which contains methods for getting channel information and searching for videos of a channel
 * through the Youtube API
 * @author Hao
 */
public class ChannelService {

    public static ChannelInfo getChannelInfo(Channel channel) {
        return new ChannelInfo(
                channel.getSnippet().getTitle(),
                channel.getId(),
                "https://www.youtube.com/channel/" + channel.getId(),
                channel.getSnippet().getThumbnails().getDefault().getUrl(),
                channel.getSnippet().getDescription(),
                channel.getStatistics().getSubscriberCount().longValue(),
                channel.getStatistics().getVideoCount().longValue(),
                channel.getStatistics().getViewCount().longValue()
        );
    }

    public static List<VideoInfo> searchChannel(String channelId, Cache cache) throws IOException {
        // Fetch videos for the channel
        List<SearchResult> results = cache.get(channelId, true);

        // Convert each video result into a VideoData object
        List<VideoInfo> videoInfoList = results.stream().map(result -> new VideoInfo(
                result.getSnippet().getTitle(),
                "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                result.getSnippet().getChannelTitle(),
                "channel?query=" + result.getSnippet().getChannelId(),
                result.getSnippet().getThumbnails().getDefault().getUrl(),
                result.getSnippet().getDescription()

        )).collect(Collectors.toList());

        // Keep only the 10 most recent results
        if (videoInfoList.size() > 10) {
            videoInfoList = videoInfoList.subList(0, 10);
        }

        return videoInfoList;
    }
}