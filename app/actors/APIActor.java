package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.StatusReply;
import models.Cache;
import models.ChannelInfo;
import models.SearchHistory;
import services.SearchByTagSevice;
import services.SentimentAnalyzer;
import services.YouTubeService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class APIActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static class CacheUpdateMessage{
        SearchHistory value;
        CacheUpdateMessage(SearchHistory value){
            this.value = value;
        }
    }

    enum SearchType{
        QUERY,
        CHANNEL,
        STATS,
        TAG,
    }

    static class QueryResponse{
        CompletableFuture<Object> future;
        public QueryResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    static class QueryUpdateResponse{
        CompletableFuture<Object> future;
        public QueryUpdateResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    static class ChannelResponse{
        CompletableFuture<Object> future;
        public ChannelResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    static class TagResponse{
        CompletableFuture<Object> future;
        public TagResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    static class StatsResponse{
        CompletableFuture<Object> future;
        public StatsResponse(CompletableFuture<Object> future){
            this.future = future;
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

    public static Props getProps() {
        return Props.create(APIActor.class);
    }

    @Override
    public void preStart() {
        log.info("APIActor started");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchMessage.class, message -> {
                    String query = message.query;
                    SearchType type = message.type;

                    try {
                        CompletableFuture<Object> result = CompletableFuture.supplyAsync(() -> {
                            try {
                                switch (type){
                                    case QUERY:
                                        return Cache.getSearchHistory(query, false);
                                    case CHANNEL:
                                        ChannelInfo response = Cache.getChannelDetails(query);
                                        System.out.println(response);
                                        return response;
                                    case STATS:
                                        return Cache.getSearchHistory(query, false);
                                    case TAG:
                                        return SearchByTagSevice.searchByTag(query);
                                    default:
                                        /*TODO FIX*/
                                        return null;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        switch (type){
                            case QUERY:
                                getSender().tell(new QueryResponse(result), getSelf());
                                break;
                            case CHANNEL:
                                getSender().tell(new ChannelResponse(result), getSelf());
                                break;
                            case STATS:
                                getSender().tell(new StatsResponse(result), getSelf());
                                break;
                            case TAG:
                                getSender().tell(new TagResponse(result), getSelf());
                                break;
                        }
                    } catch (Exception e) {
                        // Error occurred, return an error message.
                        getSender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
                }})
                .match(CacheUpdateMessage.class, message -> {
                    SearchHistory searchObject = message.value;
                    try {
                        if (!Cache.hasAValidEntry(searchObject)){
                            SearchHistory newValue = Cache.getSearchHistory(searchObject.getQuery(), false);
                            if (!newValue.equals(searchObject)){
                                newValue.setSentiment(SentimentAnalyzer.getSentiment(newValue.getResults().stream()));
                                getSender().tell(new CacheUpdateMessage(newValue), getSelf());
                            }
                        }
                    } catch (Exception e){
                        getSender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
                    }
                })
                .build();
    }
}
