package services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class WebSocketActor extends AbstractActor {

    private final ActorRef youtubeServiceActor;
    private ActorRef out; // WebSocket output actor

    public WebSocketActor(ActorRef youtubeServiceActor, ActorRef out) {
        this.youtubeServiceActor = youtubeServiceActor;
        this.out = out;
    }

    public static Props props(ActorRef youtubeServiceActor, ActorRef out) {
        return Props.create(WebSocketActor.class, youtubeServiceActor, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onWebSocketMessage)
                .match(YoutubeProtocol.VideoSearchResults.class, this::onSearchResults)
                .match(YoutubeProtocol.ErrorMessage.class, this::onErrorMessage)
                .match(ActorRef.class, this::onSourceActorInitialized) // Handle Source actor initialization
                .build();
    }

    private void onWebSocketMessage(JsonNode json) {
        String query = json.get("query").asText();
        youtubeServiceActor.tell(new YoutubeProtocol.SearchVideos(query), self());
    }

    private void onSearchResults(YoutubeProtocol.VideoSearchResults results) {
        if (out == null) {
            System.err.println("Output ActorRef (out) is null. Cannot send message to WebSocket.");
            return;
        }
        JsonNode jsonResponse = Json.toJson(results.videoTitles);
        out.tell(jsonResponse, self());
    }

    private void onErrorMessage(YoutubeProtocol.ErrorMessage error) {
        if (out == null) {
            System.err.println("Output ActorRef (out) is null. Cannot send error message to WebSocket.");
            return;
        }
        JsonNode jsonResponse = Json.newObject().put("error", error.error);
        out.tell(jsonResponse, self());
    }

    private void onSourceActorInitialized(ActorRef sourceActor) {
        System.out.println("WebSocket Source ActorRef initialized.");
        this.out = sourceActor;
    }
}
