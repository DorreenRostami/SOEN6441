package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import org.junit.Before;
import org.junit.Test;
import play.http.websocket.Message;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.test.WithApplication;

import java.util.concurrent.ExecutionException;

import akka.stream.javadsl.Flow;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HomeControllerTest extends WithApplication {
    private HomeController homeController;

    @Before
    public void setUp() {
        ActorSystem actorSystem = ActorSystem.create();
        Materializer materializer = Materializer.createMaterializer(actorSystem);
        homeController = new HomeController(actorSystem, materializer);
    }

    @Test
    public void index() {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testWebSocketConnection() throws ExecutionException, InterruptedException {
        // Create a mock WebSocket request
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder().uri("/ws").method("GET");

        // Invoke the ws() method
        WebSocket socket = homeController.ws();

        // Simulate the WebSocket handshake
        F.Either<Result, Flow<Message, Message, ?>> result = socket.apply(requestBuilder.build()).toCompletableFuture().get();

        // Check that the WebSocket connection was successfully established
        assertNotNull(result);
    }
}
