package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import akka.NotUsed;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ChannelServiceActor;
import services.WebSocketActor;
import services.YouTubeServiceActor;

import javax.inject.Inject;

public class WebSocketController extends Controller {

    private final ActorSystem actorSystem;
    private final ActorRef youtubeServiceActor;
    private final ActorRef channelServiceActor;

    @Inject
    public WebSocketController(ActorSystem actorSystem) throws Exception {
        this.actorSystem = actorSystem;

        // Initialize YouTube API client
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                null
        ).setApplicationName("YourAppName").build();

        // Initialize YouTubeServiceActor and ChannelServiceActor
        this.youtubeServiceActor = actorSystem.actorOf(YouTubeServiceActor.props());
        this.channelServiceActor = actorSystem.actorOf(ChannelServiceActor.props(new services.ChannelService(), youtubeService));
    }

    // Render the WebSocket search page
    public Result wsp() {
        return ok(views.html.actor.render());
    }

    // Render the Channel Details page
    public Result channelPage(String query) {
        return ok(views.html.channelactor.render(query));
    }

    public WebSocket webSocket() {
        return WebSocket.Json.accept(request -> {
            // Initialize WebSocketActor
            ActorRef webSocketActor = actorSystem.actorOf(WebSocketActor.props(youtubeServiceActor, channelServiceActor, null));

            return Flow.fromSinkAndSource(
                    // Sink for processing incoming messages
                    Sink.actorRef(webSocketActor, "completed")
                            .contramap(message -> (JsonNode) message), // Convert incoming Object to JsonNode

                    // Source for sending outgoing messages
                    Source.<JsonNode>actorRef(10, OverflowStrategy.fail())
                            .mapMaterializedValue(sourceActor -> {
                                // Pass Source actor reference (out) to WebSocketActor
                                webSocketActor.tell(sourceActor, ActorRef.noSender());
                                return sourceActor;
                            })
            );
        });
    }
}
