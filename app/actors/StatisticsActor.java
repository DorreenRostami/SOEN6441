package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.SearchHistory;
import models.VideoInfo;
import scala.Tuple2;
import services.WordStatistics;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for handling requests to compute word-level statistics.
 * @author Dorreen Rostami
 */
public class StatisticsActor extends AbstractActor {

    private final ActorRef webSocketActor;
    private final ActorRef apiActor;

    /**
     * Constructs a new StatisticsActor
     *
     * @param webSocketActor the WebSocket actor responsible for client communication
     * @param apiActor       the API actor responsible for fetching search results
     * @author Dorreen Rostami
     */
    public StatisticsActor(ActorRef webSocketActor, ActorRef apiActor) {
        this.webSocketActor = webSocketActor;
        this.apiActor = apiActor;
    }

    /**
     * A message class for the word-level statistics
     */
    public static class StatisticsMessage{

        String query;
        List<Tuple2<String, Long>> sortedWordCount;

        /**
         * Constructs a new StatisticsMessage
         *
         * @param query           the search query
         * @param sortedWordCount the sorted list of word-frequency pairs
         * @author Dorreen
         */
        public StatisticsMessage(String query, List<Tuple2<String, Long>> sortedWordCount){
            this.query = query;
            this.sortedWordCount = sortedWordCount;
        }

        public String getJson() {
            StringBuilder json = new StringBuilder();
            json.append("{ \"query\":\"" + this.query + "\",")
                    .append("\"words\": [");
            Iterator<Tuple2<String, Long>> iterator = this.sortedWordCount.iterator();
            while (iterator.hasNext()) {
                Tuple2<String, Long> tuple = iterator.next();
                json.append("{\"word\":\"" + tuple._1 + "\",")
                        .append("\"count\":" + tuple._2 + "}");
                if (iterator.hasNext()) {
                    json.append(",");
                }
            }
            json.append("]}");
            return json.toString();
        }

    }

    /**
     * Creates and returns a Props instance for the statistics actor
     *
     * @param webSocketActor the WebSocket actor responsible for client communication
     * @param apiActor       the API actor responsible for fetching search results
     * @return a Props instance for creating a StatisticsActor
     * @author Dorreen
     */
    public static Props getProps(ActorRef webSocketActor, ActorRef apiActor) {
        return Props.create(StatisticsActor.class, () -> new StatisticsActor(webSocketActor, apiActor));
    }

    /**
     * Defines the behavior of the actor by specifying how it handles incoming messages
     * - Receives a search query as a String and sends the query to the APIActor for processing
     * - Receives a {@link APIActor.StatsResponse} containing search results
     *   Processes the results to compute word-level statistics and sends the
     *   statistics back to the client via the WebSocketActor
     *
     * @return the receive handler defining the actor's behavior
     * @author Dorreen
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    apiActor.tell(new APIActor.SearchMessage(msg, APIActor.SearchType.STATS), getSelf());
                })
                .match(APIActor.StatsResponse.class, response -> {
                    CompletableFuture<Object> future = response.future;
                    SearchHistory history = ((SearchHistory) future.get());
                    List<VideoInfo> results = history.getResults();

                    List<String> resultText = results.stream()
                            .flatMap(result -> Stream.of(
                                    result.getVideoTitle(),
                                    result.getDescription()
                            ))
                            .collect(Collectors.toList());

                    List<Tuple2<String, Long>> sortedWordCount = WordStatistics.getWordStats(resultText);
                    webSocketActor.tell(new WebSocketActor.ResponseMessage(new StatisticsMessage(history.getQuery(), sortedWordCount).getJson()), getSelf());
                }).build();
    }
}
