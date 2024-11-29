package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.ChannelInfo;

import java.util.concurrent.CompletableFuture;

public class ChannelActor extends AbstractActor {
    public ActorRef webSocketActor;
    public ActorRef apiActor;

    public static Props getProps(ActorRef webSocketActor, ActorRef apiActor) {
        return Props.create(ChannelActor.class, () -> new ChannelActor(webSocketActor, apiActor));
    }

    public ChannelActor(ActorRef webSocketActor, ActorRef apiActor){
        this.webSocketActor = webSocketActor;
        this.apiActor = apiActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    apiActor.tell(new APIActor.SearchMessage(msg, APIActor.SearchType.CHANNEL), getSelf());
                })
                .match(APIActor.ChannelResponse.class, response -> {
                    CompletableFuture<Object> future = response.future;
                    ChannelInfo channelInfo = (ChannelInfo) future.get();
                    webSocketActor.tell(new WebSocketActor.ResponseMessage(channelInfo.getHTML()), getSelf());
                })
                .build();
    }
}
