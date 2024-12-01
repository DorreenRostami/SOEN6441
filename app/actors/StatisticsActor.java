package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.SearchHistory;
import models.VideoInfo;
import scala.Tuple2;
import services.WordStatistics;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsActor extends AbstractActor {

    private final ActorRef webSocketActor;
    private final ActorRef apiActor;
    public StatisticsActor(ActorRef webSocketActor, ActorRef apiActor) {
        this.webSocketActor = webSocketActor;
        this.apiActor = apiActor;
    }

    public static class StatisticsMessage{

        String query;
        List<Tuple2<String, Long>> sortedWordCount;
        public StatisticsMessage(String query, List<Tuple2<String, Long>> sortedWordCount){
            this.sortedWordCount = sortedWordCount;
        }

        public String getHTML(){
//            StringBuilder html = new StringBuilder();
//            html.append("<button class=\"back-button\" onclick=\"return onBackClick()\">Back</button>");
//
//            html.append("<h1>Word-Level Statistics for ").append(query).append("</h1>");

            return views.html.statistics.render(query, sortedWordCount).toString();
        }
    }
    public static Props getProps(ActorRef webSocketActor, ActorRef apiActor) {
//        return Props.create(StatisticsActor.class);
        return Props.create(StatisticsActor.class, () -> new StatisticsActor(webSocketActor, apiActor));
    }

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
                    webSocketActor.tell(new WebSocketActor.ResponseMessage(new StatisticsMessage(history.getQuery(), sortedWordCount).getHTML()), getSelf());
                }).build();
    }
}
