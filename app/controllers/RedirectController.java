package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;

public class RedirectController extends Controller {

    @Inject
    public RedirectController() {}

    public Result redirectToYtLytics() {
        return redirect(routes.HomeController.hello());
    }
}
