package services;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import models.ChannelInfo;
import models.VideoInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<VideoInfo> searchChannel(String channelId, YouTubeService youtubeService) throws IOException {
        // Fetch videos for the channel
        List<SearchResult> results = youtubeService.searchChannelVideos(channelId);

        // Extract video IDs
        List<String> videoIds = results.stream()
                .map(result -> result.getId().getVideoId())
                .collect(Collectors.toList());

        // Fetch video details
        List<Video> videoDetails = youtubeService.getVideoDetails(videoIds);

        // Convert each video detail into a VideoData object
        List<VideoInfo> videoInfoList = videoDetails.stream().map(video -> new VideoInfo(
                video.getSnippet().getTitle(),
                "https://www.youtube.com/watch?v=" + video.getId(),
                video.getSnippet().getChannelTitle(),
                "channel?query=" + video.getSnippet().getChannelId(),
                video.getSnippet().getThumbnails().getDefault().getUrl(),
                video.getSnippet().getDescription(),  // Correctly set the video's description
                video.getSnippet().getTags())).collect(Collectors.toList());

        // Keep only the 10 most recent results
        if (videoInfoList.size() > 10) {
            videoInfoList = videoInfoList.subList(0, 10);
        }

        return videoInfoList;
    }
}