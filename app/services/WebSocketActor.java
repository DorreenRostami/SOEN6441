package services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class WebSocketActor extends AbstractActor {

    private final ActorRef youtubeServiceActor;
    private final ActorRef channelServiceActor;
    private ActorRef out; // WebSocket output actor

    public WebSocketActor(ActorRef youtubeServiceActor, ActorRef channelServiceActor, ActorRef out) {
        this.youtubeServiceActor = youtubeServiceActor;
        this.channelServiceActor = channelServiceActor;
        this.out = out;
    }

    public static Props props(ActorRef youtubeServiceActor, ActorRef channelServiceActor, ActorRef out) {
        return Props.create(WebSocketActor.class, youtubeServiceActor, channelServiceActor, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onWebSocketMessage)
                .match(YoutubeProtocol.VideoSearchResults.class, this::onSearchResults)
                .match(YoutubeProtocol.ChannelDetailsResponse.class, this::onChannelDetailsResponse)
                .match(YoutubeProtocol.ErrorMessage.class, this::onErrorMessage)
                .match(ActorRef.class, this::onSourceActorInitialized) // Handle Source actor initialization
                .build();
    }

    private void onWebSocketMessage(JsonNode json) {
        if (json.has("query")) {
            String query = json.get("query").asText();
            youtubeServiceActor.tell(new YoutubeProtocol.SearchVideos(query), self());
        } else if (json.has("channelId")) {
            String channelId = json.get("channelId").asText();
            channelServiceActor.tell(new YoutubeProtocol.GetChannelDetails(channelId), self());
        }
    }

    private void onSearchResults(YoutubeProtocol.VideoSearchResults results) {
        if (out == null) {
            System.err.println("Output ActorRef (out) is null. Cannot send message to WebSocket.");
            return;
        }
        // Convert the detailed video information to JSON
        JsonNode jsonResponse = Json.toJson(results.videos);
        out.tell(jsonResponse, self());
    }

    private void onChannelDetailsResponse(YoutubeProtocol.ChannelDetailsResponse response) {
        if (out == null) {
            System.err.println("Output ActorRef (out) is null. Cannot send message to WebSocket.");
            return;
        }
        // Create a new ObjectNode to combine channel and videos data
        ObjectNode jsonResponse = Json.newObject();
        jsonResponse.set("channel", Json.toJson(response.channelData));
        jsonResponse.set("videos", Json.toJson(response.videoDetails));
        out.tell(jsonResponse, self());
    }

    private void onErrorMessage(YoutubeProtocol.ErrorMessage error) {
        if (out == null) {
            System.err.println("Output ActorRef (out) is null. Cannot send error message to WebSocket.");
            return;
        }
        ObjectNode jsonResponse = Json.newObject().put("error", error.error);
        out.tell(jsonResponse, self());
    }

    private void onSourceActorInitialized(ActorRef sourceActor) {
        System.out.println("WebSocket Source ActorRef initialized.");
        this.out = sourceActor;
    }
}
