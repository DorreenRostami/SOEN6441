package actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import com.google.api.services.youtube.model.Video;
import models.SearchHistory;
import play.api.libs.json.Json;
import scala.concurrent.duration.Duration;
import services.SentimentAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Actor class responsible for managing WebSocket interactions like
 * sending response messages back to the client.
 * @author Hamza - intial implementation
 * @author Dorreen - added functions related to statistics and query periodic update
 */
public class WebSocketActor extends AbstractActorWithTimers {
    /**
     * Represents a response message sent by and to the WebSocketActor
     * @author Hamza
     */
    public static class ResponseMessage{
        String msg;
        public ResponseMessage(String msg){
            this.msg = msg;
        }
    }

    /**
     * Represents a message sent periodically by the WebSocketActor
     * @author Dorreen
     */
    private static final class Tick {}

    private final ActorRef out;
    private ActorRef apiActor;
    private ActorRef sentimentAnalyzerActor;
    private ActorRef channelActor;
    private ActorRef statisticsActor;
    private ActorRef tagActor;
    private final List<SearchHistory> searchResults;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    /**
     * called when the actor starts
     * A periodic timer is started that triggers with a "Tick" message
     * Also, actors for interactions throughout the app are created
     * @author Dorreen Rostami
     */
    @Override
    public void preStart() {
        log.info("Starting WebSocketActor");
        getTimers().startPeriodicTimer(
                "Timer",
                new Tick(),
                Duration.create(30, TimeUnit.HOURS));
    }

    /**
     * Defines how the actor handles child failures
     * - Resume the child actor for TimeoutException
     * - Restart the child actor for RuntimeException
     * - Escalate other exceptions to the actor system
     *
     * @return SupervisorStrategy the fault handling strategy
     * @author Hamza
     */
    @Override
    public SupervisorStrategy supervisorStrategy(){
        return new OneForOneStrategy(
                -1,
                java.time.Duration.ofMinutes(3),
                DeciderBuilder.match(TimeoutException.class,
                        // Timeout exception, should be able to resolve in the next message, so resume.
                        e -> SupervisorStrategy.resume()
                ).match(RuntimeException.class,
                        // Runtime exception, probably not be able to get resolve by itself, restart it.
                        e -> SupervisorStrategy.restart()
                ).matchAny(
                        // Any other issue, escalate it to actor system.
                        e -> SupervisorStrategy.escalate()
                ).build()
        );
    }

    /**
     * Constructor for WebSocketActor
     *
     * @param out WebSocket connection
     * @author Hamza Asghar Khan
     */
    private WebSocketActor(ActorRef out) {
        this.out = out;
        this.searchResults = new ArrayList<>();
        this.apiActor = getContext().actorOf(APIActor.getProps());
        this.sentimentAnalyzerActor = getContext().actorOf(SentimentAnalyzerActor.getProps());
        this.channelActor = getContext().actorOf(ChannelActor.getProps(getSelf(), apiActor));
        this.statisticsActor = getContext().actorOf(StatisticsActor.getProps(getSelf(), apiActor));
        this.tagActor = getContext().actorOf(TagActor.getProps(getSelf(), apiActor));
    }

    /**
     * Create Props for WebSocketActor
     *
     * @param out WebSocket connection
     * @return Props instance
     * @author Dorreen Rostami
     */
    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(out));
    }


    /**
     * Constructor for WebSocketActor used for injecting actors during testing
     *
     * @param out The WebSocket connection
     * @param apiActor The API actor
     * @param channelActor The actor for channel queries
     * @param statisticsActor The actor for word level statistics
     * @param tagActor The actor for video tags
     * @author Dorreen Rostami
     */
    private WebSocketActor(ActorRef out, ActorRef apiActor, ActorRef channelActor, ActorRef statisticsActor, ActorRef tagActor) {
        this.out = out;
        this.apiActor = apiActor;
        this.channelActor = channelActor;
        this.statisticsActor = statisticsActor;
        this.tagActor = tagActor;
        this.searchResults = new ArrayList<>();
    }

    /**
     * Create Props for WebSocketActor used for injecting actors during testing
     *
     * @param out The WebSocket connection
     * @param apiActor The API actor
     * @param channelActor The actor for channel queries
     * @param statisticsActor The actor for word level statistics
     * @param tagActor The actor for video tags
     * @return Props instance
     * @author Dorreen Rostami
     */
    public static Props props(ActorRef out, ActorRef apiActor, ActorRef channelActor, ActorRef statisticsActor, ActorRef tagActor) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(out, apiActor, channelActor, statisticsActor, tagActor));
    }

    /**
     * Defines the actor's behavior by specifying how it handles incoming messages.
     * - WebSocket messages: QUERY, CHANNEL, STATS, VIDEOINFO, TAG
     * - Periodic updates
     * - Responses from child actors
     *
     * @return Receive message handling behavior
     * @author Hamza - initial & cache lookup for periodic updates
     * @author Dorreen - implmentations for STATS and Tick for periodic updates
     * @author Yi Tian - implementations related to TAG and VIDEOINFO
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    String[] msgSplit = msg.split(":::");
                    System.out.println(msg);
                    if (msgSplit.length == 2){
                        String msgType = msgSplit[0];
                        String msgValue = msgSplit[1];
                        System.out.println("MESSAGE RECEIVED: " + msgValue + " type: " + msgType);
                        switch (msgType){
                            case "QUERY":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.QUERY), getSelf());
                                break;
                            case "CHANNEL":
                                channelActor.tell(msgValue, getSelf());
                                break;
                            case "STATS":
                                statisticsActor.tell(msgValue, getSelf());
                                break;
                            case "VIDEOINFO":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.VIDEO_DETAILS), getSelf());
                                break;
                            case "TAG":
                                tagActor.tell(msgValue, getSelf());
                                break;
                            default:
                                System.out.println("Invalid Socket Call");
                        }
                    } else if (msgSplit.length == 1){
                        // EMPTY QUERY. SENT WHEN BACK BUTTON IS PRESSED.
                        getSelf().tell(searchResults, getSelf());
                    }
                })
                .match(APIActor.QueryResponse.class, queryResponse -> { //keep 10 recent searched queries
                    CompletableFuture<Object> future = queryResponse.future;
                    if (searchResults.size() == 10){
                        searchResults.remove(9);
                    }
                    SearchHistory result = (SearchHistory) future.get();
                    sentimentAnalyzerActor.tell(result, getSelf());
                    searchResults.add(0, result);
                })
                .match(SentimentAnalyzer.Sentiment.class, response -> {
                    getSelf().tell(searchResults, getSelf());
                })
                .match(List.class, response -> {
                    for (SearchHistory searchHistory: searchResults){
                        getSelf().tell(new ResponseMessage(searchHistory.getJson()), getSelf());
                    }
                })
                .match(ResponseMessage.class, response -> {
                    ActorRef sender = getSender();
                    System.out.println(sender);
                    System.out.println(channelActor);
                    System.out.println(sender.equals(channelActor));
                    if (sender.equals(getSelf())){
                        String responseString = "{ \"type\": \"query\", \"response\": " + response.msg + "}";
                        out.tell(responseString, getSelf());
                    } else if (sender.equals(channelActor)){
                        String responseString = "{ \"type\": \"channel\", \"response\": " + response.msg + "}";
                        System.out.println(response.msg);
                        out.tell(responseString, getSelf());
                    } else if (sender.equals(statisticsActor)){
                        String responseString = "{ \"type\": \"statistics\", \"response\": " + response.msg + "}";
                        out.tell(responseString, getSelf());
                    }
                })
                .match(ChannelActor.ChannelActorMessage.class, response -> {
                    String responseString = "{ \"type\": \"channel\", \"response\": \"" + response.msg + "\"}";
                    out.tell(responseString, getSelf());
                })
                .match(APIActor.VideoDetailsResponse.class, response -> {
                    log.info("Received Video Details Response");
                    Video v = (Video) response.future.join();
                    String s =v.toString();
                    log.info(s);
                    String responseString =
                            "{ \"type\": \"videoDetails\", \"response\": " + s +
                            "}";
                    out.tell(responseString, getSelf());
                })
                .match(TagActor.TagActorMessage.class, response -> {
                    String responseString = "{ \"type\": \"tag\", \"response\": " + response.msg + "}";
                    out.tell(responseString, getSelf());
                })
                .match(Tick.class, msg -> {
                    System.out.println("TICK TOCK");
                    for (SearchHistory searchHistory : searchResults) {
                        apiActor.tell(
                                new APIActor.CacheUpdateMessage(searchHistory),
                                getSelf()
                        );
                    }
                })
                .match(APIActor.CacheUpdateMessage.class, queryResponse -> {
                    if (queryResponse.value != null){
                        for (int i = 0; i < searchResults.size(); i++){
                            if (searchResults.get(i).getQuery().equals(queryResponse.value.getQuery())){
                                searchResults.add(i, queryResponse.value);
                                getSelf().tell(new ResponseMessage(queryResponse.value.getJson()), getSelf());
                            }
                        }
                    }
                })
                .build();
    }
}
