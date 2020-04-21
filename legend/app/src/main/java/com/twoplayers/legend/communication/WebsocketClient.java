package com.twoplayers.legend.communication;

import io.reactivex.functions.Consumer;

public interface WebsocketClient {
	<T> void listen(String subject, Class<T> expectedObject, Consumer<T> consumer);
	
	<T> void send(String subject, T data);
}
