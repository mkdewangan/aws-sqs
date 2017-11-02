<h1>aws-sqs</h1>

It is some set of classes to interact with AWS SQS in different environments.


**SQSClient**

This program can run on our local laptop and send message to amazon-sqs queue. Below are the steps to make it run. This has been tested in personal aws account.

 1. We need to create a queue i.e mkd-test-queue in amazon-sqs.
 2. We need to get aws-access-key and aws-secret-key.
 3. We can run the program by passing aws-access-key and aws-secret-key as system properties using java -D option.
 4. It will send message to aws sqs queue defined in (1).
 
**EC2SQSClient**

This program is developed to run in EC2 instance and send message to aws-sqs queue.
 1. We need to create EC2 instance 
	  - with appropriate role which has permission to access to sqs.
	  - with appropriate security group which can make https connection to sqs.
 2. We have used InstanceProfileCredentialsProvider. This credential provider gets credentials directly from instance metadata, so no need to provide explicitly.
 3. If EC2 is running behind proxy , we need to supply it in system properties e.g.
  
 java -Dhttps.nonProxyHosts=169.254.169.254 -Dhttp.nonProxyHosts=169.254.169.254 -Dhttp.proxyHost=<proxyHost> -Dhttp.proxyPort=<proxyPort> -Dhttps.proxyHost=<proxyHost>  -Dhttps.proxyPort=<proxyPort> -jar sqs-poc-1.0-SNAPSHOT-jar-with-dependencies.jar 

Here value of nonProxyHosts is specific value i.e 169.254.169.254. The service which provides credentials is running here. InstanceProfileCredentialsProvider access this service. 

