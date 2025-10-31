package com.assignment.clinic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloWord {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}

