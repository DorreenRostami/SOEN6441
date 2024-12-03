package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebSocketActorTest {
    private ActorSystem system;
    private TestKit apiProbe;
    private ActorRef webSocketActor;

    @Before
    public void setup() {
        system = ActorSystem.create();
        apiProbe = new TestKit(system);
        webSocketActor = system.actorOf(WebSocketActor.props(apiProbe.getRef(), apiProbe.getRef()));
    }

    @After
    public void tearDown() {
        TestKit.shutdownActorSystem(system);
    }

    /**
     * Test how QUERY message is handled
     * @author Dorreen
     */
    @Test
    public void testHandleQuery() {
        String testMessage = "QUERY:::testQuery";
        webSocketActor.tell(testMessage, ActorRef.noSender());
        APIActor.SearchMessage receivedMessage = apiProbe.expectMsgClass(APIActor.SearchMessage.class);
        assertEquals("testQuery", receivedMessage.query);
        assertEquals(APIActor.SearchType.QUERY, receivedMessage.type);
    }

}
