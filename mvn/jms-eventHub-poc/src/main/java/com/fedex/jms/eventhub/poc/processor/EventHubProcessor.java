package com.fedex.jms.eventhub.poc.processor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.fedex.jms.eventhub.poc.configuration.EventHubConsumer;
import com.fedex.jms.eventhub.poc.configuration.EventHubProducer;

@Component
public class EventHubProcessor {

	private static final Logger log = LoggerFactory.getLogger(EventHubProcessor.class);

	@Autowired
	private EventHubProducer eventHubProducer;

	@Autowired
	private EventHubConsumer eventHubConsumer;

	public void pushMessageToEventHub(String data) {
		try (EventHubProducerClient producer = eventHubProducer.createEventHubProducerClient();){
			// Get producer object
			
			log.info("CSV Data To Event Hub :" + data);

			// prepare a batch of events to send to the event hub

			EventDataBatch batch = producer.createBatch();
			batch.tryAdd(new EventData(data));

			// send the batch of events to the event hub
			producer.send(batch);
			log.info("Message successfully sent to eventHub :: ");

			// close the producer
			//producer.close();

		} catch (Exception e) {
			log.error("Error in pushMessageToEventHub :: " + e.getMessage());
		}

	}

	public EventProcessorClient pullMessageFromEventHub() throws IOException, InterruptedException {
		EventProcessorClient eventProcessorClient = eventHubConsumer.createEventHubProcessorClient();
		if (eventProcessorClient != null) {
			log.info("Starting event processor");
			eventProcessorClient.start();

			// TimeUnit.MINUTES.sleep(6);

		}
		return eventProcessorClient;

	}

	public void stopEventProcessor(EventProcessorClient eventProcessorClient) throws IOException, InterruptedException {

		if (eventProcessorClient != null && eventProcessorClient.isRunning()) {
			log.info("Stopping event processor");
			eventProcessorClient.stop();
			log.info("Event processor stopped.");

			log.info("Exiting process");
		} else {
			log.info(" Event hub consumer already stopped");
		}
	}
}
