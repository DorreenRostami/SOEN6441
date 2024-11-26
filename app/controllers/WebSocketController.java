package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import com.fasterxml.jackson.databind.JsonNode;
import akka.NotUsed;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.WebSocketActor;
import services.YouTubeServiceActor;

import javax.inject.Inject;

public class WebSocketController extends Controller {

    private final ActorSystem actorSystem;
    private final ActorRef youtubeServiceActor;
    public Result wsp() {
        return ok(views.html.actor.render());
    }
    @Inject
    public WebSocketController(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        // Initialize YouTubeServiceActor
        this.youtubeServiceActor = actorSystem.actorOf(YouTubeServiceActor.props());
    }

    public WebSocket webSocket() {
        return WebSocket.Json.accept(request -> {
            // Initialize YouTubeServiceActor
            ActorRef webSocketActor = actorSystem.actorOf(WebSocketActor.props(youtubeServiceActor, null));

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
