package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.StatusReply;
import com.google.api.services.youtube.model.SearchResult;
import models.Cache;
import models.SearchHistory;
import services.YouTubeService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class APIActor extends AbstractActor {
    YouTubeService youTubeService;
    Cache cache;

    static class QuerySearch{
        String query;

        public QuerySearch(String query) {
            this.query = query;
        }
    }

    public APIActor() throws GeneralSecurityException, IOException {
        this.youTubeService = new YouTubeService();
        this.cache = new Cache(youTubeService);
    }

    public static Props getProps() {
        return Props.create(APIActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(QuerySearch.class, message -> {
            String query = message.query;
            try {
                CompletableFuture<SearchHistory> result = CompletableFuture.supplyAsync(() -> {
                    try {
                        return cache.get(query, false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                // Send a message back to requester
                sender().tell(result, self());
            } catch (Exception e) {
                // Error occurred, return an error message.
                sender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
            }
        }).build();
    }
}
