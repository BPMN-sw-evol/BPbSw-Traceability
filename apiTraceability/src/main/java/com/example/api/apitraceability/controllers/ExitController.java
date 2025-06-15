package com.example.api.apitraceability.controllers;

import Interfaces.Traceability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exit")
public class ExitController {
    
    @GetMapping
    public void exitTraceability(){
        System.out.println("saliendo...");
        System.exit(0);
    }
}