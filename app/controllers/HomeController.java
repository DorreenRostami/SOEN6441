package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.hello;


public class HomeController extends Controller {


    // Clear search results and display homepage
    public Result hello() {
        return ok(hello.render(""));
    }

    // Search function, get the results of a new search and save them
    public Result search(String query) {
        return ok(hello.render(""));
    }
}