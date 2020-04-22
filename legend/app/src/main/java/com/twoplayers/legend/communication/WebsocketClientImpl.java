package com.twoplayers.legend.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class WebsocketClientImpl implements WebsocketClient {
	private StompClient stompSession = null;
	private StompClient unopenedStompSession = null;

	private final Map<String, Subscriptions<?>> subjectToSubscriptions = new ConcurrentHashMap<String, Subscriptions<?>>();

	private final Queue<String> pendingSubscriptions = new LinkedList<String>();
	private final Map<String, List<Object>> pendingSend = new HashMap<String, List<Object>>();
	private Gson gson = new GsonBuilder().create();

	public WebsocketClientImpl(String socketUrl) {
		unopenedStompSession = Stomp.over(Stomp.ConnectionProvider.OKHTTP, socketUrl);
		unopenedStompSession.lifecycle().subscribe(new io.reactivex.functions.Consumer<LifecycleEvent>() {
			@Override
			public void accept(LifecycleEvent lifecycleEvent) {
				if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED) {
					WebsocketClientImpl.this.stompSession = WebsocketClientImpl.this.unopenedStompSession;
					subscribePending();
					sendPending();
				}
			}
		});
		unopenedStompSession.connect();
	}

	public <T> void listen(String subject, Class<T> expectedClass, Consumer<T> consumer) {
		String subjectToSubscribe = null;
		synchronized (subjectToSubscriptions) {
			Subscriptions<T> sub = (Subscriptions<T>) subjectToSubscriptions.get(subject);
			if (sub == null) {
				subjectToSubscribe = subject;
				subjectToSubscriptions.put(subject, sub = new Subscriptions<T>(subject, expectedClass));
				subjectToSubscriptions.put("/topic/" + subject, sub);
			}
			if (!sub.getExpectedClass().equals(expectedClass)) {
				throw new RuntimeException("On ne peut pas utiliser le même sujet avec deux classes différentes");
			}
			sub.addConsumer(consumer);
		}
		
		if (subjectToSubscribe != null)
			createSubscription(subjectToSubscribe);
	}

	private void createSubscription(String subject) {
		if (stompSession == null) {
			synchronized (this) {
				if (stompSession == null) {
					pendingSubscriptions.add(subject);
					return;
				}
			}
		}
		stompSession.topic("/topic/" + subject).subscribe(subjectToSubscriptions.get(subject));
	}

	public <T> void send(String subject, T data) {
		if (stompSession == null) {
			synchronized (this) {
				if (stompSession == null) {
					List<Object> list = pendingSend.get(subject);
					if (list == null) {
						pendingSend.put(subject, list = new LinkedList<Object>());
					}
					list.add(data);
					return;
				}
			}
		}
		stompSession.send("/app/data/" + subject, gson.toJson(data));
	}

	private void sendPending() {
		for (Map.Entry<String, List<Object>> entry : pendingSend.entrySet()) {
			for (Object object : entry.getValue()) {
				stompSession.send("/app/data/" + entry.getKey(), gson.toJson(object)).subscribe();
			}
		}
	}

	private void subscribePending() {
		String subject = pendingSubscriptions.poll();
		while (subject != null) {
			stompSession.topic("/topic/" + subject).subscribe(this.subjectToSubscriptions.get(subject));
			subject = pendingSubscriptions.poll();
		}
	}
}
