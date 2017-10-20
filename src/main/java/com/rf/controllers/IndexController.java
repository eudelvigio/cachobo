package com.rf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    String login() {

        return "login";
    }
}
