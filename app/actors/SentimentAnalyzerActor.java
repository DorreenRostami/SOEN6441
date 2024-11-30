package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.SearchHistory;
import services.SentimentAnalyzer;

/**
 * Actor class to handle the sentiment analysis for a particular search.
 * @author Hamza Asghar Khan
 */
public class SentimentAnalyzerActor extends AbstractActor {

    /**
     * @return A thread-safe prop
     * @author Hamza Asghar Khan
     */
    public static Props getProps() {
        return Props.create(SentimentAnalyzerActor.class);
    }

    /**
     * @return A receiver that handles all the messages related to the SentimentAnalyzer
     * @author Hamza Asghar Khan
     */
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
