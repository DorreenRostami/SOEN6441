package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import models.SearchHistory;
import models.VideoInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class WebSocketActorTest {
    private ActorSystem system;
    private TestKit probe;

    TestProbe outProbe;
    TestProbe apiActorProbe;
    TestProbe sentimentActorProbe;
    TestProbe channelActorProbe;
    TestProbe statisticsActorProbe;
    TestProbe tagActorProbe;
    ActorRef webSocketActor;

    @Before
    public void setup() {
        system = ActorSystem.create();
        probe = new TestKit(system);

        outProbe = new TestProbe(system);
        apiActorProbe = new TestProbe(system);
        sentimentActorProbe = new TestProbe(system);
        channelActorProbe = new TestProbe(system);
        statisticsActorProbe = new TestProbe(system);
        tagActorProbe = new TestProbe(system);

        webSocketActor = system.actorOf(WebSocketActor.props(outProbe.ref(), apiActorProbe.ref(), sentimentActorProbe.ref(), channelActorProbe.ref(), statisticsActorProbe.ref(), tagActorProbe.ref()));
    }

    @After
    public void tearDown() {
        TestKit.shutdownActorSystem(system);
    }

    /**
     * Test how QUERY and VIDEOINFO message is handled
     * @author Dorreen
     */
    @Test
    public void testHandleQueryAndVideoInfo() {
        String testMessage = "QUERY:::testQuery";
        webSocketActor.tell(testMessage, ActorRef.noSender());
        APIActor.SearchMessage receivedMessage = apiActorProbe.expectMsgClass(APIActor.SearchMessage.class);
        assertEquals("testQuery", receivedMessage.query);
        assertEquals(APIActor.SearchType.QUERY, receivedMessage.type);

        testMessage = "VIDEOINFO:::testQuery";
        webSocketActor.tell(testMessage, ActorRef.noSender());
        receivedMessage = apiActorProbe.expectMsgClass(APIActor.SearchMessage.class);
        assertEquals("testQuery", receivedMessage.query);
        assertEquals(APIActor.SearchType.VIDEO_DETAILS, receivedMessage.type);
    }

    /**
     * Test how CHANNEL & STATS & TAG message is handled (all should send a string)
     * @author Dorreen
     */
    @Test
    public void testHandle_CH_S_T() {
        String chMsg = "CHANNEL:::test";
        webSocketActor.tell(chMsg, probe.getRef());
        channelActorProbe.expectMsg("test");

        String sMsg = "STATS:::test";
        webSocketActor.tell(sMsg, probe.getRef());
        statisticsActorProbe.expectMsg("test");

        String tMsg = "TAG:::test";
        webSocketActor.tell(tMsg, probe.getRef());
        tagActorProbe.expectMsg("test");
    }

    @Test
    public void testQueryResponseProcessing() throws Exception {
        VideoInfo video1 = new VideoInfo("Title1", "url1", "Channel1", "channelUrl1", "thumbUrl1", "Description1", "tagsUrl1");
        SearchHistory mockSearchHistory = new SearchHistory("test query", List.of(video1));

        CompletableFuture<Object> mockFuture = CompletableFuture.completedFuture(mockSearchHistory);
        APIActor.QueryResponse mockQueryResponse = new APIActor.QueryResponse(mockFuture);

        webSocketActor.tell(mockQueryResponse, ActorRef.noSender());

        sentimentActorProbe.expectMsg(mockSearchHistory);
    }

    @Test
    public void testChannelActorMessage() {
        ChannelActor.ChannelActorMessage message = new ChannelActor.ChannelActorMessage("Test channel msg");

        webSocketActor.tell(message, probe.getRef());
        outProbe.expectMsg("{ \"type\": \"channel\", \"response\": \"Test channel msg\"}");
    }

    @Test
    public void testVideoDetailsResponseProcessing() throws Exception {
        Video video1 = new Video();
        video1.setId("testVideoId");
        video1.setSnippet(new VideoSnippet());
        video1.getSnippet().setTitle("Test Title");
        video1.getSnippet().setDescription("Test Description");
        CompletableFuture<Object> mockFuture = CompletableFuture.completedFuture(video1);
        APIActor.VideoDetailsResponse res = new APIActor.VideoDetailsResponse(mockFuture);
        webSocketActor.tell(res, ActorRef.noSender());

        String expectedResponse = "{ \"type\": \"videoDetails\", \"response\": " + video1.toString() + "}";
        outProbe.expectMsg(expectedResponse);
    }

}
