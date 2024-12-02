package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.StatusReply;
import models.Cache;
import models.ChannelInfo;
import services.SearchByTagSevice;
import services.YouTubeService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Class that handles incoming search requests and interacts with services to handle request
 * and sends the results back to the requesting actor.
 *
 * @author Hamza - intial implemenentation
 * @author Dorreen - added stuff related to word stats and periodic query search
 */
public class APIActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    /**
     * Enum representing the types of searches handled by the APIActor
     * @author Hamza - initial
     * @author Dorreen - added STATS and QUERY_UPDATE
     */
    enum SearchType{
        QUERY,
        CHANNEL,
        STATS,
        TAG,
        QUERY_UPDATE
    }

    /**
     * Represents a response to a video query search request
     * @author Hamza
     */
    static class QueryResponse{
        CompletableFuture<Object> future;
        public QueryResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    /**
     * Represents a response to a query update request which is done periodically
     * @author Dorreen
     */
    static class QueryUpdateResponse{
        CompletableFuture<Object> future;
        public QueryUpdateResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    /**
     * Represents a response to a channel search request
     * @author Hamza
     */
    static class ChannelResponse{
        CompletableFuture<Object> future;
        public ChannelResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    /**
     * Represents a response to a tag search request
     * @author Hamza
     */
    static class TagResponse{
        CompletableFuture<Object> future;
        public TagResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    /**
     * Represents a response to a word-level statistics request
     * @author Dorreen
     */
    static class StatsResponse{
        CompletableFuture<Object> future;
        public StatsResponse(CompletableFuture<Object> future){
            this.future = future;
        }
    }

    /**
     * Represents a search message that includes a query and the type of search
     * @author Hamza
     */
    static class SearchMessage{
        String query;
        SearchType type;

        public SearchMessage(String query, SearchType type) {
            this.query = query;
            this.type = type;
        }
    }

    /**
     * Creates a new instance of the APIActor
     *
     * @return A Props object used to create an APIActor instance
     *
     * @author Hamza
     */
    public static Props getProps() {
        return Props.create(APIActor.class);
    }

    /**
     * Logs a message indicating the APIActor has started
     * @author Yi Tian
     */
    @Override
    public void preStart() {
        log.info("APIActor started");
    }

    /**
     * Handles incoming messages and processes them based on the message type
     *
     * @return The actor's receive behavior, which defines how it reacts to messages
     *
     * @author Hamza - intial implementation
     * @author Dorreen - added functions related to statistics and query periodic update
     */
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
                                        System.out.println("."+ query+ ".");
                                        return Cache.get(query, false);
                                    case CHANNEL:
                                        ChannelInfo response = Cache.getChannelDetails(query);
                                        System.out.println(response);
                                        return response;
                                    case STATS:
                                        return YouTubeService.searchVideos(query, 50L);
//                                        return Cache.get(query, false);
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
