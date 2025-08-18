package com.aipply.aipply.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FrontendController {

    @GetMapping("/ping")
    @ResponseBody
    public String checkPingStatus() {
        return "ping checked";
    }

    @GetMapping({"/", "/login", "/register"})
    public String forwardToReactApp() {
        return "forward:/index.html";
    }

    @GetMapping("/{path:[^\\.]*}")
    public String forwardToReactAppWithPath() {
        return "forward:/index.html";
    }

}
