package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.hello;

public class HomeController extends Controller {

    // 渲染初始页面
    public Result hello() {
        return ok(hello.render(""));
    }

    // 处理搜索请求
    public Result search(String query) {
        return ok(hello.render(query));
    }
}