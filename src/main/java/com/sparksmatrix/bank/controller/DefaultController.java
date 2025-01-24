package com.sparksmatrix.bank.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class DefaultController {

    @GetMapping("/")
    public String index() {
        return "ok";
    }
}
