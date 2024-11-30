package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.ChannelInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ChannelActor class.
 */
public class ChannelActorTest {

    private static ActorSystem system;

    @BeforeAll
    public static void setup() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterAll
    public static void teardown() {
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
        TestKit probe = new TestKit(system);
        ActorRef mockApiActor = mock(ActorRef.class);
        ActorRef channelActor = system.actorOf(ChannelActor.getProps(probe.getRef(), mockApiActor));

        // Mock ChannelInfo and future
        ChannelInfo mockChannelInfo = mock(ChannelInfo.class);
        when(mockChannelInfo.getHTML()).thenReturn("<h1>Mock Channel</h1>");

        CompletableFuture<Object> future = CompletableFuture.completedFuture(mockChannelInfo);
        APIActor.ChannelResponse response = new APIActor.ChannelResponse(future);

        // Send the response to the ChannelActor
        channelActor.tell(response, probe.getRef());

        // Verify that WebSocketActor receives the correct response
        WebSocketActor.ResponseMessage expectedResponse = new WebSocketActor.ResponseMessage("<h1>Mock Channel</h1>");
        WebSocketActor.ResponseMessage actualResponse = probe.expectMsgClass(WebSocketActor.ResponseMessage.class);
        assertEquals(expectedResponse.msg, actualResponse.msg);
    }

    @Test
    public void testChannelActorHandlesFailedResponse() {
        TestKit probe = new TestKit(system);
        ActorRef mockApiActor = mock(ActorRef.class);
        ActorRef channelActor = system.actorOf(ChannelActor.getProps(probe.getRef(), mockApiActor));

        // Mock a failed future
        CompletableFuture<Object> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Test exception"));
        APIActor.ChannelResponse response = new APIActor.ChannelResponse(failedFuture);

        // Send the response to the ChannelActor
        channelActor.tell(response, probe.getRef());

        // Verify that WebSocketActor receives an error response
        WebSocketActor.ResponseMessage expectedErrorResponse = new WebSocketActor.ResponseMessage("<p>Error: Unable to fetch channel details</p>");
        WebSocketActor.ResponseMessage actualResponse = probe.expectMsgClass(WebSocketActor.ResponseMessage.class);
        assertEquals(expectedErrorResponse.msg, actualResponse.msg);
    }
}