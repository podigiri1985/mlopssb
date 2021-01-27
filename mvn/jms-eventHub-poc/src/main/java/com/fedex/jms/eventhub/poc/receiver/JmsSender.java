package com.fedex.jms.eventhub.poc.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JmsSender {

	private static final Logger log = LoggerFactory.getLogger(JmsSender.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	public String sendMsgToJmsQueue(String jmsQueue, String message) {
		ObjectMapper mapper = new ObjectMapper();
		String data = null;
		try {
			data = mapper.writeValueAsString(message);
			jmsTemplate.convertAndSend(jmsQueue, message);
		} catch (Exception e) {
			log.error("Error while executing sendMsgToJmsQueue :: " + e.getMessage());
		}

		return data;
	}

}
