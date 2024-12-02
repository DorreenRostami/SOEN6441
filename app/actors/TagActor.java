package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.ChannelInfo;

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
                    apiActor.tell(new APIActor.SearchMessage(msg, APIActor.SearchType.CHANNEL), getSelf());
                })
                // Handle APIActor responses
                .match(APIActor.ChannelResponse.class, response -> {
                    try {
                        CompletableFuture<Object> future = response.future;
                        ChannelInfo channelInfo = (ChannelInfo) future.get(); // Get result from the future
                        webSocketActor.tell(new ChannelActor.ChannelActorMessage(channelInfo.getHTML()), getSelf());
                    } catch (Exception e) {
                        String errorMessage = "<p>Error: Unable to fetch channel details</p>";
                        webSocketActor.tell(new ChannelActor.ChannelActorMessage(errorMessage), getSelf());
                    }
                })
                .build();

    }
}
