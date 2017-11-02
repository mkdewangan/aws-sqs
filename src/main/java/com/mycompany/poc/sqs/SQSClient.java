package com.mycompany.poc.sqs;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SQSClient {

	private static SQSConnectionFactory connectionFactory = null;

	static {

		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.standard()
				.withRegion(Regions.US_EAST_1).withCredentials(new SystemPropertiesCredentialsProvider()));

	}

	public void send() {

		SQSConnection connection = null;

		// Create the connection.
		try {
			connection = connectionFactory.createConnection();
			System.out.println("SQSClient.enclosing_method() " + connection);

			// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create a queue identity and specify the queue name to the session
			Queue queue = session.createQueue("eps-poc-queue");

			// Create a producer for the 'TestQueue'
			MessageProducer producer = session.createProducer(queue);
			
			// Create the text message
			TextMessage message = session.createTextMessage("Hello World!");

			// Set the message group ID
			//message.setStringProperty("JMSXGroupID", "Default");

			// You can also set a custom message deduplication ID
			// message.setStringProperty("JMS_SQS_DeduplicationId", "hello");
			// Here, it's not needed because content-based deduplication is enabled for the queue

			// Send the message
			producer.send(message);
			System.out.println("JMS Message " + message.getJMSMessageID());
			System.out.println("JMS Message Sequence Number " + message.getStringProperty("JMS_SQS_SequenceNumber"));

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SQSClient sqsClient = new SQSClient();
		
		sqsClient.send();

	}

}
