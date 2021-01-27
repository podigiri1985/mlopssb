package com.fedex.jms.eventhub.poc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

@Component
public class EventHubProducer {

	private static final Logger log = LoggerFactory.getLogger(EventHubProducer.class);

	@Value("${eventHub.inbound.name}")
	private String inboundEventHubName;

	@Value("${eventHub.inbound.connectionString}")
	private String inboundConnectionString;

	public EventHubProducerClient createEventHubProducerClient() {

		return new EventHubClientBuilder().connectionString(inboundConnectionString, inboundEventHubName)
				.buildProducerClient();

	}

}
