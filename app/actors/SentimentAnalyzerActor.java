package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.SearchHistory;
import services.SentimentAnalyzer;

public class SentimentAnalyzerActor extends AbstractActor {

    public static Props getProps() {
        return Props.create(SentimentAnalyzerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchHistory.class, result -> {
                    SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.getSentiment(result.getResults().stream());
                    result.setSentiment(sentiment);
                    getSender().tell(sentiment, getSelf());
                }).build();
    }
}
