package controllers;

import models.VideoInfo;
import play.mvc.Controller;
import play.mvc.Result;
import scala.jdk.javaapi.CollectionConverters;
import views.html.hello;

import java.util.ArrayList;
import java.util.List;

public class HomeController extends Controller {
    private static List<VideoInfo> allSearchResults = new ArrayList<>();

    // Clear search results and display homepage
    public Result hello() {
        allSearchResults.clear();
        return ok(hello.render("", CollectionConverters.asScala(allSearchResults).toList()));
    }

    // Get the results of a new search and save them
    public Result search(String query) {
        return ok(hello.render("", CollectionConverters.asScala(allSearchResults).toList()));
    }
}