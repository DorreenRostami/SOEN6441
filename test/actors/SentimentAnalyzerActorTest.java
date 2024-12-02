package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import services.SentimentAnalyzer;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Test Driver for SentimentAnalyzerActor class
 * @author Hamza Asghar Khan
 */
public class SentimentAnalyzerActorTest {

    private static ActorSystem system;

    /**
     * Sets up the actor system before the tests
     * @author Hamza Asghar Khan
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    /**
     * Takes down the actor system after the tests
     * @author Hamza Asghar Khan
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests to see that the actor fetches the sentiment results correctly.
     * @author Hamza Asghar Khan
     */
    @Test
    public void testSentimentAnalysis() {
        new TestKit(system) {{
            ActorRef sentimentAnalyzerActor = system.actorOf(SentimentAnalyzerActor.getProps());
            LinkedList<VideoInfo> sampleResult = new LinkedList<>();
            sampleResult.add(new VideoInfo("testTitle", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
            sampleResult.add(new VideoInfo("test 2", "vidoeURL/dsads", "Channel Title", "channelURL/sdas", "https://picsum.photos/536/354", "This is the test description", "tagsUrl/dsa"));
            SearchHistory searchHistory = new SearchHistory("testQUery", sampleResult);
            sentimentAnalyzerActor.tell(searchHistory, getRef());
            SentimentAnalyzer.Sentiment sentiment = expectMsgClass(SentimentAnalyzer.Sentiment.class);
            assertEquals(SentimentAnalyzer.Sentiment.NEUTRAL, sentiment);
            assertEquals(SentimentAnalyzer.Sentiment.NEUTRAL, searchHistory.getSentiment());
        }};
    }
}
