package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import com.example.restservice.nats.NatsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	private NatsClient natsClient;

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

		natsClient.createStream();
//		natsClient.publishMessage();
//		natsClient.subscribeSubject(NatsClient.subject1);

		natsClient.subscribeWithOccurance();
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
