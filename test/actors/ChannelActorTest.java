package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.ChannelInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ChannelActor class.
 */
public class ChannelActorTest {

    private static ActorSystem system;

    @Before
    public void setup() {
        system = ActorSystem.create("TestSystem");
    }

    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testChannelActorProcessesMessages() {
        TestKit probe = new TestKit(system); // WebSocketActor mock
        TestKit apiProbe = new TestKit(system); // APIActor mock
        ActorRef channelActor = system.actorOf(ChannelActor.getProps(probe.getRef(), apiProbe.getRef()));

        String testMessage = "Test Channel ID";

        // Send a test message to the ChannelActor
        channelActor.tell(testMessage, probe.getRef());

        // Verify APIActor receives the correct SearchMessage
        APIActor.SearchMessage capturedMessage = apiProbe.expectMsgClass(APIActor.SearchMessage.class);
        assertEquals(testMessage, capturedMessage.query);
        assertEquals(APIActor.SearchType.CHANNEL, capturedMessage.type);
    }

    @Test
    public void testChannelActorHandlesSuccessfulResponse() {
        TestKit probe = new TestKit(system); // WebSocketActor mock
        TestKit apiProbe = new TestKit(system); // APIActor mock
        ActorRef channelActor = system.actorOf(ChannelActor.getProps(probe.getRef(), apiProbe.getRef()));

        // Mock ChannelInfo and its HTML response
        ChannelInfo mockChannelInfo = mock(ChannelInfo.class);
        when(mockChannelInfo.getHTML()).thenReturn("<h1>Mock Channel</h1>");

        // Mock a successful future
        CompletableFuture<Object> future = CompletableFuture.completedFuture(mockChannelInfo);
        APIActor.ChannelResponse response = new APIActor.ChannelResponse(future);

        // Send the mocked response to the ChannelActor
        channelActor.tell(response, probe.getRef());

        // Verify WebSocketActor receives the correct HTML message
        ChannelActor.ChannelActorMessage actualMessage = probe.expectMsgClass(ChannelActor.ChannelActorMessage.class);
        assertEquals("<h1>Mock Channel</h1>", actualMessage.msg);
    }

    @Test
    public void testChannelActorHandlesFailedResponse() {
        TestKit probe = new TestKit(system); // WebSocketActor mock
        TestKit apiProbe = new TestKit(system); // APIActor mock
        ActorRef channelActor = system.actorOf(ChannelActor.getProps(probe.getRef(), apiProbe.getRef()));

        // Mock a failed future
        CompletableFuture<Object> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Test exception"));
        APIActor.ChannelResponse response = new APIActor.ChannelResponse(failedFuture);

        // Send the mocked failed response to the ChannelActor
        channelActor.tell(response, probe.getRef());

        // Verify WebSocketActor receives an error message
        ChannelActor.ChannelActorMessage actualMessage = probe.expectMsgClass(ChannelActor.ChannelActorMessage.class);
        assertEquals("<p>Error: Unable to fetch channel details</p>", actualMessage.msg);
    }
}
