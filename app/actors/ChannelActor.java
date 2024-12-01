package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.ChannelInfo;

import java.util.concurrent.CompletableFuture;

/**
 * Actor to handle channel-related operations, forwarding requests to the APIActor and responses to the WebSocketActor.
 */
public class ChannelActor extends AbstractActor {

    private final ActorRef webSocketActor;
    private final ActorRef apiActor;

    /**
     * Props for creating the ChannelActor.
     *
     * @param webSocketActor Reference to the WebSocketActor.
     * @param apiActor       Reference to the APIActor.
     * @return Props for ChannelActor.
     * @author Hao
     */
    public static Props getProps(ActorRef webSocketActor, ActorRef apiActor) {
        return Props.create(ChannelActor.class, () -> new ChannelActor(webSocketActor, apiActor));
    }

    /**
     * Constructor for ChannelActor.
     *
     * @param webSocketActor Reference to the WebSocketActor.
     * @param apiActor       Reference to the APIActor.
     * @author Hao
     */
    public ChannelActor(ActorRef webSocketActor, ActorRef apiActor) {
        this.webSocketActor = webSocketActor;
        this.apiActor = apiActor;
    }

    @Override
    public Receive createReceive() {
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
                        webSocketActor.tell(new WebSocketActor.ResponseMessage(channelInfo.getHTML()), getSelf());
                    } catch (Exception e) {
                        String errorMessage = "<p>Error: Unable to fetch channel details</p>";
                        webSocketActor.tell(new WebSocketActor.ResponseMessage(errorMessage), getSelf());
                    }
                })
                .build();
    }
}