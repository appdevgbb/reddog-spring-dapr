package com.microsoft.gbb.rasa.orderservice.messaging;

import com.microsoft.gbb.rasa.orderservice.dto.OrderSummaryDto;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DaprPublisher {

    @Value("${messaging.pubsub.TOPIC_NAME}")
    private String TOPIC_NAME;
    @Value("${messaging.pubsub.SUB_NAME}")
    private String SUB_NAME;

    public void publishEvent(OrderSummaryDto message) {
        DaprClient client = new DaprClientBuilder().build();
        client.publishEvent(
                SUB_NAME,
                TOPIC_NAME,
                message).block();
    }

}

