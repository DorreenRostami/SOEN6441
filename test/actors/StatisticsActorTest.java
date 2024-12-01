package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.ChannelInfo;
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
import static org.mockito.Mockito.*;

public class StatisticsActorTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

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

    @Test
    public void testStatsResponse() {
        TestKit testProbe = new TestKit(system);
        TestKit apiProbe = new TestKit(system);

        ActorRef statisticsActor = system.actorOf(StatisticsActor.getProps(testProbe.getRef(), apiProbe.getRef()));

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

        WebSocketActor.ResponseMessage responseMessage = testProbe.expectMsgClass(WebSocketActor.ResponseMessage.class);

        String expectedHTML = views.html.statistics.render("TestQuery", mockWordStats).toString();
        assertEquals(expectedHTML, responseMessage.msg);
    }

}
