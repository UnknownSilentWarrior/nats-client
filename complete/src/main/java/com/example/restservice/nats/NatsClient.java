package com.example.restservice.nats;


import io.nats.client.*;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class NatsClient
{
  private Connection connection;

  public static final String subject1 = "subject.one";
  public static final String subject2 = "subject.two";

  public NatsClient()
  {
    try {
      connection = Nats.connect("nats://localhost:4222");
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }
  public void createStream()
  {
    try {
      JetStreamManagement jsm = connection.jetStreamManagement();

      // Build the configuration
      StreamConfiguration streamConfiguration = StreamConfiguration.builder()
              .name("Widgets")
              .storageType(StorageType.Memory)
              .subjects(subject1, subject2)
              .build();

      // Create the stream
      StreamInfo streamInfo = jsm.addStream(streamConfiguration);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (JetStreamApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void publishMessage()
  {
   connection.publish(subject1, "Hi their!".getBytes());
  }

  public void subscribeSubject(String subjectName)
  {
    Dispatcher d = connection.createDispatcher(msg -> {
      String response = new String(msg.getData(), StandardCharsets.UTF_8);
      System.out.println("Receive response " + response);

    });

    d.subscribe(subjectName);
  }

  public void subscribeWithOccurance()
  {
    Dispatcher d = connection.createDispatcher(msg -> {});

    Subscription s = d.subscribe(subject1,
            msg -> {
              String response = new String(msg.getData(), StandardCharsets.UTF_8);
              System.out.println("Message received up to 100 times: " + response);
            });
    // unsubscribe after 100 times received
    d.unsubscribe(s, 100);
  }
}
