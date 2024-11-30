package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.SearchHistory;
import models.VideoInfo;
import scala.Tuple2;
import services.WordStatistics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsActor extends AbstractActor {
    public static class StatisticsMessage{
        List<Tuple2<String, Long>> sortedWordCount;
        public StatisticsMessage(List<Tuple2<String, Long>> sortedWordCount){
            this.sortedWordCount = sortedWordCount;
        }

        public String getHTML(){
            return views.html.statistics.render(query, sortedWordCount).toString();
        }
    }
    public static Props getProps() {
        return Props.create(StatisticsActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchHistory.class, search -> {
                    List<VideoInfo> results = search.getResults();

                    List<String> resultText = results.stream()
                            .flatMap(result -> Stream.of(
                                    result.getVideoTitle(),
                                    result.getDescription()
                            ))
                            .collect(Collectors.toList());

                    List<Tuple2<String, Long>> sortedWordCount = WordStatistics.getWordStats(resultText);
                    getSender().tell(new StatisticsMessage(sortedWordCount), getSelf());
                }).build();
    }
}
