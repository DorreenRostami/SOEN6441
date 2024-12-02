package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.ChannelInfo;
import models.SearchHistory;

import java.util.concurrent.CompletableFuture;

public class TagActor extends AbstractActor {
    private final ActorRef webSocketActor;
    private final ActorRef apiActor;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static class TagActorMessage{
        String msg;
        public TagActorMessage(String msg) {
            this.msg = msg;
        }
    }

    /**
     * Props for creating the ChannelActor.
     *
     * @param webSocketActor Reference to the WebSocketActor.
     * @param apiActor       Reference to the APIActor.
     * @return Props for ChannelActor.
     * @author Hao
     */
    public static Props getProps(ActorRef webSocketActor, ActorRef apiActor) {
        return Props.create(TagActor.class, () -> new TagActor(webSocketActor, apiActor));
    }

    /**
     * Constructor for ChannelActor.
     *
     * @param webSocketActor Reference to the WebSocketActor.
     * @param apiActor       Reference to the APIActor.
     * @author Hao
     */
    public TagActor(ActorRef webSocketActor, ActorRef apiActor) {
        this.webSocketActor = webSocketActor;
        this.apiActor = apiActor;
    }

    @Override
    public void preStart() {
        log.info("TagActor started");
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                // Handle search message
                .match(String.class, msg -> {
                    apiActor.tell(new APIActor.SearchMessage(msg, APIActor.SearchType.TAG), getSelf());
                })
                // Handle APIActor responses
                .match(APIActor.TagResponse.class, response -> {
                    try {
                        CompletableFuture<Object> future = response.future;
                        SearchHistory searchHistory = (SearchHistory) future.get(); // Get result from the future
                        webSocketActor.tell(new TagActorMessage(searchHistory.getJson()), getSelf());
                    } catch (Exception e) {
                        String errorMessage = "<p>Error: Unable to fetch tag related videos</p>";
                        webSocketActor.tell(new TagActorMessage(errorMessage), getSelf());
                    }
                })
                .build();
    }
}
