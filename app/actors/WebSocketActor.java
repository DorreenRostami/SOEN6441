package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import models.SearchHistory;
import services.SentimentAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class WebSocketActor extends AbstractActor {
    public static class ResponseMessage{
        String msg;
        public ResponseMessage(String msg){
            this.msg = msg;
        }
    }
    private final ActorRef out;
    private final ActorRef apiActor;
    private final ActorRef sentimentAnalyzerActor;
    private final ActorRef channelActor;
    private final List<SearchHistory> searchResults;

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
        this.apiActor = getContext().actorOf(APIActor.getProps());
        this.sentimentAnalyzerActor = getContext().actorOf(SentimentAnalyzerActor.getProps());
        this.channelActor = getContext().actorOf(ChannelActor.getProps(getSelf(), apiActor));
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
                    if (msgSplit.length == 2){
                        String msgType = msgSplit[0];
                        String msgValue = msgSplit[1];
                        System.out.println("MESSAGE RECEIVED: " + msgValue);
                        switch (msgType){
                            case "QUERY":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.QUERY, 10), getSelf());
                                break;
                            case "CHANNEL":
                                channelActor.tell(msgValue, getSelf());
                                break;
                            case "STATISTICS":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.QUERY, 50), getSelf());
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
                .match(APIActor.QueryResponse.class, queryResponse -> {
                    CompletableFuture<Object> future = queryResponse.future;
                    if (searchResults.size() == 10){
                        searchResults.remove(9);
                    }
                    SearchHistory result = (SearchHistory) future.get();
                    sentimentAnalyzerActor.tell(result, getSelf());
                    searchResults.add(0, result);
                })
                .match(StatisticsActor.StatisticsMessage.class, msg -> {
                    /*TODO*/
                })
                .match(SentimentAnalyzer.Sentiment.class, response -> {
                    getSelf().tell(searchResults, getSelf());
                })
                .match(ResponseMessage.class, response -> {
                    out.tell(response.msg, getSelf());
                })
                .match(List.class, response -> {
                    StringBuilder responseString = new StringBuilder();
                    for (SearchHistory searchHistory: searchResults){
                        responseString.append(searchHistory.getHTML(true));
                    }
                    getSelf().tell(new ResponseMessage(responseString.toString()), getSelf());
                })
                .build();
    }
}
