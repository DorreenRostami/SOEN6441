package controllers;

import actors.APIActor;
import actors.WebSocketActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the home page and search functionality
 */
public class HomeController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    /**
     * Initialize class attributes
     * @author Hamza Asghar Khan
     */
    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    /**
     * Returns the html page for the website
     * @author Dorreen Rostami
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    /**
     * method for showing the homepage with an empty search history
     * @return a CompletableFuture containing a result which renders the hello page
     * @author Hamza
     * @author Dorreen - added api actor
     */
    public WebSocket ws(){
        ActorRef apiActor = actorSystem.actorOf(Props.create(APIActor.class));
        return WebSocket.Text.acceptOrResult(request -> CompletableFuture.completedFuture(F.Either.Right(
                ActorFlow.actorRef(out -> WebSocketActor.props(out, apiActor), actorSystem, materializer)
        )));
    }

}
