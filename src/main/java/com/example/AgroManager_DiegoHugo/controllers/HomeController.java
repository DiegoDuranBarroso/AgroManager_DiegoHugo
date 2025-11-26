package com.example.AgroManager_DiegoHugo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String index() {
        // Carga templates/index.html
        return "index";
    }
}
