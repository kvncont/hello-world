package com.github.kvncont.helloworld.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @RequestMapping("/")
	public String index() {
		return "Hello Wolrd from Spring Boot!";
	}
    
}
