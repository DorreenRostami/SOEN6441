package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.StatusReply;
import models.Cache;
import models.SearchHistory;
import services.YouTubeService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;

public class APIActor extends AbstractActor {
    enum SearchType{
        QUERY,
        CHANNEL,
        TAG
    }

    YouTubeService youTubeService;
    Cache cache;

    static class QueryResponse{
        CompletableFuture<SearchHistory> future;
        SearchType type;
        public QueryResponse(CompletableFuture<SearchHistory> future, SearchType type){
            this.future = future;
            this.type = type;
        }
    }
    static class SearchMessage{
        String query;
        SearchType type;

        public SearchMessage(String query, SearchType type) {
            this.query = query;
            this.type = type;
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
        return receiveBuilder()
                .match(SearchMessage.class, message -> {
                    String query = message.query;
                    SearchType type = message.type;
                    try {
                        CompletableFuture<SearchHistory> result = CompletableFuture.supplyAsync(() -> {
                            try {
                                switch (type){
                                    case QUERY:
                                        return cache.get(query, false);
                                    case CHANNEL:
                                        return cache.get(query, true);
                                    case TAG:
                                    default:
                                        /*TODO FIX*/
                                        return null;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        // Send a message back to requester
                        sender().tell(new QueryResponse(result, type), self());
                    } catch (Exception e) {
                        // Error occurred, return an error message.
                        sender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
                }})
                .build();
    }
}
