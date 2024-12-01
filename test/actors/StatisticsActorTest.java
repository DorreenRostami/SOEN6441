package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.Tuple2;
import services.WordStatistics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link StatisticsActor} class
 *
 * @author Dorreen Rostami
 */
public class StatisticsActorTest {
    private static ActorSystem system;

    /**
     * Sets up the test environment by creating an actor system
     * @author Dorreen Rostami
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    /**
     * Cleans up the test environment by shutting down the actor system
     * @author Dorreen Rostami
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    /**
     * When the actor receives a search query, it should send {@link APIActor.SearchMessage} to the API actor.
     * @author Dorreen Rostami
     */
    @Test
    public void testSearchQuery() {
        TestKit wsProbe = new TestKit(system);
        TestKit apiProbe = new TestKit(system);
        ActorRef statisticsActor = system.actorOf(StatisticsActor.getProps(wsProbe.getRef(), apiProbe.getRef()));

        String searchQuery = "TestQuery";
        statisticsActor.tell(searchQuery, ActorRef.noSender());

        APIActor.SearchMessage searchMessage = apiProbe.expectMsgClass(APIActor.SearchMessage.class);
        assertEquals(searchQuery, searchMessage.query);
        assertEquals(APIActor.SearchType.STATS, searchMessage.type);
    }

    /**
     * When the actor receives a {@link APIActor.StatsResponse}, it processes the response to compute
     * word-level statistics and sends the correct JSON response to the WebSocket actor
     * @author Dorreen Rostami
     */
    @Test
    public void testStatsResponse() {
        TestKit wsProbe = new TestKit(system);
        TestKit apiProbe = new TestKit(system);

        ActorRef statisticsActor = system.actorOf(StatisticsActor.getProps(wsProbe.getRef(), apiProbe.getRef()));

        List<VideoInfo> videoInfoList = List.of(
                new VideoInfo("Video 1", "https://www.youtube.com/watch?v=1", "Channel1", "https://channel1", "https://thumbnail1", "Description 1", "https://tags1"),
                new VideoInfo("Video 2", "https://www.youtube.com/watch?v=2", "Channel2", "https://channel2", "https://thumbnail2", "Description 2", "https://tags2")
        );
        SearchHistory mockSearchHistory = new SearchHistory("TestQuery", videoInfoList);
        CompletableFuture<Object> mockFuture = CompletableFuture.completedFuture(mockSearchHistory);
        APIActor.StatsResponse mockStatsResponse = new APIActor.StatsResponse(mockFuture);

        // Mock word statistics calculation
        List<Tuple2<String, Long>> mockWordStats = Arrays.asList(
                new Tuple2<>("description", 2L),
                new Tuple2<>("video", 2L)
        );
        mockStatic(WordStatistics.class);
        when(WordStatistics.getWordStats(anyList())).thenReturn(mockWordStats);

        statisticsActor.tell(mockStatsResponse, apiProbe.getRef());

        WebSocketActor.ResponseMessage responseMessage = wsProbe.expectMsgClass(WebSocketActor.ResponseMessage.class);

        String expectedJson = "{ \"query\":\"TestQuery\",\"words\": [{\"word\":\"description\",\"count\":2},{\"word\":\"video\",\"count\":2}]}";
        assertEquals(expectedJson, responseMessage.msg);
    }

}
