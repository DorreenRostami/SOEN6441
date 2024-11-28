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
                .match(String.class, msg -> apiActor.tell(new APIActor.QuerySearch(msg), getSelf()))
                .match(CompletableFuture.class, future -> {
                    if (searchResults.size() == 10){
                        searchResults.remove(9);
                    }
                    SearchHistory result = (SearchHistory) future.get();
                    sentimentAnalyzerActor.tell(result, getSelf());
                    searchResults.add(0, result);
                })
                .match(SentimentAnalyzer.Sentiment.class, response -> {
                    StringBuilder responseString = new StringBuilder();
                    for (SearchHistory searchHistory: searchResults){
                        responseString.append(searchHistory.getHTML());
                    }
                    out.tell(responseString.toString(), getSelf());
                })
                .build();
    }
}
