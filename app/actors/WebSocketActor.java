package actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import models.SearchHistory;
import play.api.libs.json.Json;
import scala.concurrent.duration.Duration;
import services.SentimentAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketActor extends AbstractActorWithTimers {
    public static class ResponseMessage{
        String msg;
        public ResponseMessage(String msg){
            this.msg = msg;
        }
    }

    private static final class Tick {}

    private final ActorRef out;
    private ActorRef apiActor;
    private ActorRef sentimentAnalyzerActor;
    private ActorRef channelActor;
    private ActorRef statisticsActor;
    private final List<SearchHistory> searchResults;
    private int searchResultsUpdatedCount = 0;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() {
        log.info("WebSocketActor started");
        getTimers().startPeriodicTimer(
                "Timer",
                new Tick(),
                Duration.create(1000000, TimeUnit.SECONDS));

        this.apiActor = getContext().actorOf(APIActor.getProps());
        this.sentimentAnalyzerActor = getContext().actorOf(SentimentAnalyzerActor.getProps());
        this.channelActor = getContext().actorOf(ChannelActor.getProps(getSelf(), apiActor));
        this.statisticsActor = getContext().actorOf(StatisticsActor.getProps(getSelf(), apiActor));
    }

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
    }

    /**
     * Create Props for WebSocketActor
     *
     * @param out WebSocket connection
     * @return Props instance
     * @author Dorreen
     */
    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(out));
    }

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
                                System.out.println("DEBUG: CHANNEL");
                                channelActor.tell(msgValue, getSelf());
                                break;
                            case "STATS":
                                statisticsActor.tell(msgValue, getSelf());
                                break;
                            case "TAG":
                                /*TODO
                                * Create a TagActor Class (essentially copy the ChannelActor class already created).
                                * Implement a getHTML method somewhere appropriate to create the required HTML for the page
                                * Add the back button to the HTML as well (as can be seen in the ChannelInfo method's getHTML) -- JUST COPY THAT LINE
                                * */
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
                    /**
                     * Add another if here to add the tags part.
                     */
                })
                .match(ChannelActor.ChannelActorMessage.class, response -> {
                    System.out.println("CHANNEL ACTOR MESSAGE");
                    String responseString = "{ \"type\": \"channel\", \"response\": \"" + response.msg + "\"}";
                    System.out.println(response.msg);
                    out.tell(responseString, getSelf());
                })
                .match(Tick.class, msg -> {
                    System.out.println("TICK TOCK");
                    searchResultsUpdatedCount = 0;
                    for (int i = 0; i < searchResults.size(); i++) {
                        SearchHistory searchHistory = searchResults.get(i);
                        apiActor.tell(
                                new APIActor.SearchMessage(searchHistory.getQuery(), APIActor.SearchType.QUERY_UPDATE),
                                getSelf()
                        );
                    }
                })
                .match(APIActor.QueryUpdateResponse.class, queryResponse -> {
//                    CompletableFuture<Object> future = queryResponse.future;
//                    SearchHistory updatedResult = (SearchHistory) future.get();
//                    sentimentAnalyzerActor.tell(updatedResult, getSelf());
//                    for (int i = 0; i < searchResults.size(); i++) {
//                        if (searchResults.get(i).getQuery().equals(updatedResult.getQuery())) {
//                            searchResults.set(i, updatedResult);
//                            searchResultsUpdatedCount++;
//                            break;
//                        }
//                    }
//
//                    if(searchResultsUpdatedCount == searchResults.size()){
//                        getSelf().tell(searchResults, getSelf());
//                    }
                })
                .build();
    }
}
