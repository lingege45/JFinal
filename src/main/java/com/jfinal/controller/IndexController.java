package com.jfinal.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {

    public void index(){
        renderText("hello jfinal!");
    }
}
