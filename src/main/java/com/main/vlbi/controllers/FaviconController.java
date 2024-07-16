package com.main.vlbi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    @GetMapping("favicon.ico")
    @ResponseBody
    String favicon() {
        return "forward:favicon.ico";
    }
}
