package com.nhnacademy.hexashoppingmallservice.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping(value = {"/index.html","/"})
    public String index(){
        return "index/index";
    }
}