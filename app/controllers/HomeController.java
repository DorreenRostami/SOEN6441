package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import actors.WebSocketActor;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the home page and search functionality
 */
public class HomeController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    /**
     * Constructor for HomeController
     * @author Yi Tian
     */
    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    /**
     * redicrects the / route to /ytlytis route (typing in localhost:9000 will redirect to localhost:9000/ytlytics
     * which is the main search page)
     *
     * @author Dorreen Rostami
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    /**
     * method for showing the homepage with an empty search history
     * @return a CompletableFuture containing a result which renders the hello page
     * @author Hamza - initial implementation
     * @author Dorreen - made it asynchronous
     */
    public WebSocket ws(){
        return WebSocket.Text.acceptOrResult(request -> {
            return CompletableFuture.completedFuture(F.Either.Right(
                    ActorFlow.actorRef(out -> WebSocketActor.props(out), actorSystem, materializer)
            ));
        });
    }

}
