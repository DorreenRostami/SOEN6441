package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;

public class TagActorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void getProps() {
    }

    @Test
    public void preStart() {
        new TestKit(system) {{
            ActorRef webSocketActor = getTestActor();
            ActorRef apiActor = getTestActor();
            system.actorOf(TagActor.getProps(webSocketActor, apiActor));
            expectNoMessage();
        }};
    }

    @Test
    public void createReceive() {
    }
}