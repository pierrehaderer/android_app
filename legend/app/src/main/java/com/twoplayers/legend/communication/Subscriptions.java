package com.twoplayers.legend.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.dto.StompMessage;

class Subscriptions<T> implements Consumer<StompMessage> {
	private final String subject;
	final Class<T> expectedClass;
	private final List<Consumer<T>> consumers = new LinkedList<Consumer<T>>();

	private Gson gson = new GsonBuilder().create();
	
	public Subscriptions(String subject, Class<T> expectedClass) {
		this.subject = subject;
		this.expectedClass = expectedClass;
	}
	public String getSubject() {
		return subject;
	}
	public Class<?> getExpectedClass() {
		return expectedClass;
	}
	
	@SuppressWarnings("unchecked")
	public void addConsumer(Consumer<?> consumer) {
		synchronized (consumers) {
			this.consumers.add((Consumer<T>)consumer);
		}
	}
	
	@SuppressWarnings("unchecked")
	void publish(Object o) throws Exception {
		synchronized (consumers) {
			for (Consumer<T> consumer : consumers) {
				consumer.accept((T)o);
			}
		}			
	}

	@Override
	public void accept(StompMessage stompMessage) throws Exception {
		publish(gson.fromJson(stompMessage.getPayload(), expectedClass));
	}
}