package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.pattern.StatusReply;
import models.Cache;
import services.SearchByTagSevice;
import services.YouTubeService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class APIActor extends AbstractActor {
    enum SearchType{
        QUERY,
        CHANNEL,
        STATS,
        TAG,
        QUERY_UPDATE
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
                                        return Cache.get(query, false);
                                    case CHANNEL:
                                        return Cache.getChannelDetails(query);
                                    case STATS:
                                        return YouTubeService.searchVideos(query, 50L);
                                    case TAG:
                                        return SearchByTagSevice.searchByTag(query);
                                    case QUERY_UPDATE:
                                        return YouTubeService.searchVideos(query);
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
                            case QUERY_UPDATE:
                                getSender().tell(new QueryUpdateResponse(result), getSelf());
                                break;
                        }
                    } catch (Exception e) {
                        // Error occurred, return an error message.
                        getSender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
                }})
                .build();
    }
}
