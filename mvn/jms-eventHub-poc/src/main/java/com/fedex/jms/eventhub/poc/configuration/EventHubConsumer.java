package com.fedex.jms.eventhub.poc.configuration;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fedex.jms.eventhub.poc.receiver.JmsSender;
import com.fedex.jms.eventhub.poc.util.JmsEventHubConstants;

@Component
public class EventHubConsumer {

	private static final Logger log = LoggerFactory.getLogger(EventHubConsumer.class);

	@Value("${eventHub.outbound.name}")
	private String outboundEventHubName;

	@Value("${eventHub.outbound.connectionString}")
	private String outboundConnectionString;

	@Value("${eventHub.storage.consumerGroupName}")
	private String consumerGroupName;

	@Value("${eventHub.storage.storageConnectionString}")
	private String storageConnectionString;

	@Value("${eventHub.storage.storageContainerName}")
	private String storageContainerName;

	@Autowired
	private JmsSender jmsSender;

	public final Consumer<EventContext> PARTITION_PROCESSOR = eventContext -> {
		log.info("Processing event from partition :: " + eventContext.getPartitionContext().getPartitionId()
				+ " with sequence number " + " :: " + eventContext.getEventData().getSequenceNumber() + " with body: "
				+ eventContext.getEventData().getBodyAsString() + " %n");

		// send message to jmsQueue
		jmsSender.sendMsgToJmsQueue(JmsEventHubConstants.OUTBOUND_QUEUE_NAME,
				eventContext.getEventData().getBodyAsString());

		// if (eventContext.getEventData().getSequenceNumber() % 10 == 0) {
		eventContext.updateCheckpoint();
		// }

	};

	public final Consumer<ErrorContext> ERROR_HANDLER = errorContext -> {
		log.info("Error occurred in partition processor for partition %s, %s.%n",
				errorContext.getPartitionContext().getPartitionId(), errorContext.getThrowable());
	};

	public EventProcessorClient createEventHubProcessorClient() throws IOException, InterruptedException {
		// Create a blob container client that you use later to build an event processor
		// client to receive and process events
		BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
				.connectionString(storageConnectionString).containerName(storageContainerName).buildAsyncClient();

		// Create a builder object that you will use later to build an event processor
		// client to receive and process events and errors.
		EventProcessorClientBuilder eventProcessorClientBuilder = new EventProcessorClientBuilder()
				.connectionString(outboundConnectionString, outboundEventHubName)
				.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME).processEvent(PARTITION_PROCESSOR)
				.processError(ERROR_HANDLER).checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient));

		// Use the builder object to create an event processor client
		return eventProcessorClientBuilder.buildEventProcessorClient();

	}
}
