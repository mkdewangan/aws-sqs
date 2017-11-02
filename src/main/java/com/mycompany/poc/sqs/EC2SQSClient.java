package com.mycompany.poc.sqs;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class EC2SQSClient {
	
	private static SQSConnectionFactory connectionFactory = null;

	static {
		System.out.println("EC2SQSClient .........");
		
		/*System.setProperty("http.proxyHost", "proxy.ebiz.verizon.com");
		System.setProperty("http.proxyPort", "80");
		System.setProperty("https.proxyHost", "proxy.ebiz.verizon.com");
		System.setProperty("https.proxyPort", "80");
		*/
		
		InstanceProfileCredentialsProvider credentialProvider = new InstanceProfileCredentialsProvider(true);
		String awsAccessKeyId = credentialProvider.getCredentials().getAWSAccessKeyId();
		String awsSecretKey = credentialProvider.getCredentials().getAWSSecretKey();
		
		System.out.println("EC2SQSClient awsAccessKeyId and awsSecretKey " + awsAccessKeyId + "  " + awsSecretKey);
		
		//AWSCredentialsProvider credentialsProvider2 = new DefaultAWSCredentialsProviderChain();
		
		//System.out.println("EC2SQSClient credentialsProvider2... " + credentialsProvider2);

		//AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(credentialsProvider2).build();
						
		//connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), credentialProvider);
		
		//System.out.println("connectionFactory " + connectionFactory);
	
		
		
		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.standard()
				.withRegion(Regions.US_EAST_1).withCredentials(credentialProvider));

	}
	
	
	/*static {
		
		
		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.standard()
				.withRegion(Regions.US_EAST_1).withCredentials(new InstanceProfileCredentialsProvider(true)));

	}*/
	

 

	public void send() {

		SQSConnection connection = null;

		// Create the connection.
		try {
			connection = connectionFactory.createConnection();
			System.out.println("connection ..." + connection);
			
			//AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
			//boolean isQueueExist = client.queueExists("mkd-test-queue");
			//System.out.println("isQueueExist " + isQueueExist);
			
			// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create a queue identity and specify the queue name to the session
			Queue queue = session.createQueue("mkd-test-queue");
			

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
		}finally{
			
			try {
				connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println( "Connection closed" );
		}

	}
	
	private void ensureQueueExists(SQSConnection connection, String queueName) throws JMSException {
        AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
        
        /**
         * In most cases, you can do this with just a createQueue call, but GetQueueUrl 
         * (called by queueExists) is a faster operation for the common case where the queue 
         * already exists. Also many users and roles have permission to call GetQueueUrl
         * but do not have permission to call CreateQueue.
         */
        if( !client.queueExists(queueName) ) {
            client.createQueue( queueName );
        }
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EC2SQSClient sqsClient = new EC2SQSClient();
		
		sqsClient.send();

	}


}
