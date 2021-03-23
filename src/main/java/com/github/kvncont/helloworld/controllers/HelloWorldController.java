package com.github.kvncont.helloworld.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {


	@Value("${env.message}")
	private String myEnvVar;

    @RequestMapping("/")
	public String index() {
		return "Hello Wolrd from " + myEnvVar;
	}
}
