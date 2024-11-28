package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.api.services.youtube.model.SearchResult;
import models.Cache;
import models.Database;
import models.SearchHistory;
import models.VideoInfo;
import scala.concurrent.duration.Duration;
import services.YouTubeService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WebSocketActor extends AbstractActorWithTimers {
    private final ActorRef out;
    private final ActorRef apiActor;
    private final List<SearchHistory> searchResults;

    private static final int POLL_TIME = 20;

    /**
     * Constructor for WebSocketActor
     *
     * @param out               WebSocket connection
     * @author Dorreen
     */
    private WebSocketActor(ActorRef out) {
        this.out = out;
        this.apiActor = getContext().actorOf(APIActor.getProps());
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
                    future.thenAccept(result -> {
                        if (searchResults.size() == 10){
                            searchResults.remove(0);
                        }
                        searchResults.add((SearchHistory) result);
                    });
                    String response = "";
                    for (SearchHistory search: searchResults){
                        response += search.
                    }
                    out.tell(searchResults, getSelf());
                })
                .build();
    }


    /**
     * Compares two lists of search results by comparing the video URLs
     *
     * @param oldResults The previous list of search results
     * @param newResults The latest list of search results
     * @return true if both lists are equal, false otherwise
     * @author Dorreen
     */
    private boolean areResultsEqual(List<VideoInfo> oldResults, List<VideoInfo> newResults) {
        List<String> oldIds = oldResults.stream().map(VideoInfo::getVideoUrl).collect(Collectors.toList());
        List<String> newIds = newResults.stream().map(VideoInfo::getVideoUrl).collect(Collectors.toList());
        return oldIds.equals(newIds);
    }
}
