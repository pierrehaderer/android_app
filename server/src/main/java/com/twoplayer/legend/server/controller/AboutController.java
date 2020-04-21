package com.twoplayer.legend.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/about")
public class AboutController {
	@GetMapping
	public String about() {
		return "Web socket application for two player";
	}
}
