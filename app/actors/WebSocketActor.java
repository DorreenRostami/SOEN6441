package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import models.SearchHistory;
import services.SentimentAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * @author Dorreen
     */
    private WebSocketActor(ActorRef out) {
        this.out = out;
        this.apiActor = getContext().actorOf(APIActor.getProps());
        this.sentimentAnalyzerActor = getContext().actorOf(SentimentAnalyzerActor.getProps());
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
                        switch (msgType){
                            case "QUERY":
                                if (Objects.equals(msgValue, "")){
                                    getSelf().tell(searchResults, getSelf());
                                }
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.QUERY), getSelf());
                                break;
                            case "CHANNEL":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.CHANNEL), getSelf());
                                break;
                            case "STATISTICS":
                                break;
                            case "TAG":
                                apiActor.tell(new APIActor.SearchMessage(msgValue, APIActor.SearchType.TAG), getSelf());
                                break;
                            default:
                                System.out.println("Invalid Socket Call");
                        }
                    }
                    }
                )
                .match(APIActor.QueryResponse.class, queryResponse -> {
                    CompletableFuture<SearchHistory> future = queryResponse.future;
                    APIActor.SearchType type = queryResponse.type;
                    switch (type){
                        case QUERY:
                            if (searchResults.size() == 10){
                                searchResults.remove(9);
                            }
                            SearchHistory result = future.get();
                            sentimentAnalyzerActor.tell(result, getSelf());
                            searchResults.add(0, result);
                            break;
                        case CHANNEL:
                            break;
                        case TAG:
                            break;
                        default:
                            break;
                    }
                })
                .match(SentimentAnalyzer.Sentiment.class, response -> {
                    System.out.println("HERE: SENTIMENT");
                    getSelf().tell(searchResults, getSelf());
                })
                .match(ResponseMessage.class, response -> {
                    out.tell(response.msg, getSelf());
                })
                .match(List.class, response -> {
                    System.out.println("HERE: LIST");
                    StringBuilder responseString = new StringBuilder();
                    for (SearchHistory searchHistory: searchResults){
                        responseString.append(searchHistory.getHTML());
                    }
                    getSelf().tell(new ResponseMessage(responseString.toString()), getSelf());
                })
                .build();
    }
}
