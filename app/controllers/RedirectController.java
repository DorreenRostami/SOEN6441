package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;

/**
 * redicrects the / route to /ytlytis route (typing in localhost:9000 will redirect to localhost:9000/ytlytics
 * which is the main search page)
 *
 * @author - Dorreen Rostami
 */
public class RedirectController extends Controller {

    @Inject
    public RedirectController() {}

    public Result redirectToYtLytics() {
        return redirect(routes.HomeController.hello());
    }
}
