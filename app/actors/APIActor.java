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
        TAG
    }

    static class QueryResponse{
        CompletableFuture<Object> future;
        public QueryResponse(CompletableFuture<Object> future){
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

    static class SearchMessage{
        String query;
        SearchType type;
        long len;

        public SearchMessage(String query, SearchType type, long len) {
            this.query = query;
            this.type = type;
            this.len = len;
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
                                        if (message.len == 10)
                                            return Cache.get(query, false);
                                        else
                                            return YouTubeService.searchVideos(query, message.len); //word stats of 50 videos
                                    case CHANNEL:
                                        return Cache.getChannelDetails(query);
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
                            case TAG:
                                getSender().tell(new TagResponse(result), getSelf());
                                break;
                        }
                    } catch (Exception e) {
                        // Error occurred, return an error message.
                        getSender().tell(new StatusReply.ErrorMessage("An error occurred"), self());
                }})
                .build();
    }
}
