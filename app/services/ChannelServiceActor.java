package services;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import models.ChannelInfo;
import models.VideoInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelServiceActor extends AbstractActor {

    private final ChannelService channelService;
    private final YouTube youtubeService;

    public ChannelServiceActor(ChannelService channelService, YouTube youtubeService) {
        this.channelService = channelService;
        this.youtubeService = youtubeService;
    }

    public static Props props(ChannelService channelService, YouTube youtubeService) {
        // Pass both ChannelService and YouTube as parameters
        return Props.create(ChannelServiceActor.class, () -> new ChannelServiceActor(channelService, youtubeService));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(YoutubeProtocol.GetChannelDetails.class, this::onGetChannelDetails)
                .build();
    }

    private void onGetChannelDetails(YoutubeProtocol.GetChannelDetails message) {
        try {
            System.out.println("Fetching channel details for channel ID: " + message.channelId);

            // Fetch channel details from YouTube API
            ChannelListResponse channelResponse = youtubeService.channels()
                    .list("snippet,statistics")
                    .setId(message.channelId)
                    .setKey(YouTubeServiceActor.API_KEY) // Make sure API_KEY is accessible
                    .execute();

            if (channelResponse.getItems().isEmpty()) {
                sender().tell(new YoutubeProtocol.ErrorMessage("Channel not found"), self());
                return;
            }

            var channel = channelResponse.getItems().get(0);
            ChannelInfo channelInfo = channelService.getChannelInfo(channel);

            // Fetch videos for the channel
            SearchListResponse videoResponse = youtubeService.search()
                    .list("snippet")
                    .setChannelId(message.channelId)
                    .setType("video")
                    .setOrder("date")
                    .setMaxResults(10L)
                    .setKey(YouTubeServiceActor.API_KEY)
                    .execute();

            List<VideoInfo> videoInfoList = videoResponse.getItems().stream()
                    .map(result -> new VideoInfo(
                            result.getSnippet().getTitle(),
                            "https://www.youtube.com/watch?v=" + result.getId().getVideoId(),
                            result.getSnippet().getChannelTitle(),
                            result.getSnippet().getChannelId(),
                            result.getSnippet().getThumbnails().getDefault().getUrl(),
                            result.getSnippet().getDescription(),
                            null
                    ))
                    .collect(Collectors.toList());

            // Respond with channel details and video list
            sender().tell(new YoutubeProtocol.ChannelDetailsResponse(channelInfo, videoInfoList), self());
        } catch (IOException e) {
            System.err.println("Error fetching channel details: " + e.getMessage());
            sender().tell(new YoutubeProtocol.ErrorMessage("Error fetching channel details: " + e.getMessage()), self());
        }
    }
}
