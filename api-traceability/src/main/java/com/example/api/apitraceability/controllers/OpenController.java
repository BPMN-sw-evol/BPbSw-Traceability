package com.example.api.apitraceability.controllers;

import Interfaces.Traceability;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class OpenController {
    
    @GetMapping("/traceability")
    public void openTraceability(@RequestParam String variable){
        //atraves de un metodo de Traceability mandar la variable para luego abrir la ventana
        System.out.println("Variable: " + variable);
        Traceability.instance.setVisible(true);
    }
}