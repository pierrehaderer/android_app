package com.twoplayer.legend.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class PublishController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/data/{subject}")
	public void publish(@DestinationVariable String subject, ObjectNode data) {
		simpMessagingTemplate.convertAndSend("/topic/" + subject, data);
	}
}
