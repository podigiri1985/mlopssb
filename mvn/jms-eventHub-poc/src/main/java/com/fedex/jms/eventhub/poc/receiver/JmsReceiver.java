package com.fedex.jms.eventhub.poc.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fedex.jms.eventhub.poc.processor.EventHubProcessor;
import com.fedex.jms.eventhub.poc.util.JmsEventHubConstants;

@Component
public class JmsReceiver {

	private static final Logger log = LoggerFactory.getLogger(JmsReceiver.class);

	@Autowired
	private EventHubProcessor eventHubProcessor;

	@JmsListener(destination = JmsEventHubConstants.INBOUND_QUEUE_NAME, containerFactory = JmsEventHubConstants.CONNECTION_FACTORY)
	public void receiveInboundJmsMsg(String message) {
		log.info("Received message from inbound jmsQueue <" + message + ">");
		// push the message into AZURE Event Hub
		eventHubProcessor.pushMessageToEventHub(message);
	}

	@JmsListener(destination = JmsEventHubConstants.OUTBOUND_QUEUE_NAME, containerFactory = JmsEventHubConstants.CONNECTION_FACTORY)
	public void receiveEventHubMessage(String message) {
		log.info("Received message from jmsOutboundQueue ************ ########## :: <" + message + ">");
	}

}
