package com.twoplayer.legend.server;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

class ServerApplicationTests {
	public static class Message {
		public String hello = "hello";

		public String getHello() {
			return hello;
		}

		public void setHello(String hello) {
			this.hello = hello;
		}
	}

	@Test
	void testSubscribe() throws InterruptedException {
		WebSocketClient client = new StandardWebSocketClient();

		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return Message.class;
			}
			
			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				System.out.print(((Message)payload).hello);
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				var sub = session.subscribe("/topic/zelda", this);
				session.send("/app/data/zelda", new Message());
			}
		};
		stompClient.connect("wss://jean-backend.k8s.keyconsulting.fr/socket", sessionHandler);
		Thread.sleep(100000);
	}

}
