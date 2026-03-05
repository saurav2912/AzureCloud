package org.saurav;

import com.azure.messaging.servicebus.*;
import com.azure.messaging.servicebus.models.SubQueue;

import java.util.Iterator;

public class ServiceBusQueueApp {

    public static void main(String[] args) {
        String serviceBusSendConnectionString = System.getenv("AZURE_SERVICE_BUS_SEND_CONNECTION_STRING");
        String serviceBusListenConnectionString = System.getenv("AZURE_SERVICE_BUS_LISTEN_CONNECTION_STRING");
        String queueName = "sauravqueue";
        ServiceBusSenderClient senderClient = buildSenderClient(queueName, serviceBusSendConnectionString);
        ServiceBusReceiverClient recieverClient = buildRecieverClient(queueName, serviceBusListenConnectionString);
        //sendMessage(senderClient);
        //sendMessageBatch(senderClient);
        //peekMessages(recieverClient);
        //peekMessagesFrmDeadLetterQueue(queueName,serviceBusListenConnectionString);
        //recieveMessages(recieverClient);
        //completeMessages(recieverClient);
        duplicateMessageTest();
    }

    private static ServiceBusReceiverClient buildRecieverClient(String queueName, String serviceBusConnectionString) {
        ServiceBusReceiverClient client = new ServiceBusClientBuilder()
                .connectionString(serviceBusConnectionString)
                .receiver()
                .queueName(queueName)
                .buildClient();
        return client;
    }

    private static ServiceBusSenderClient buildSenderClient(String queueName, String serviceBusConnectionString) {
        ServiceBusSenderClient client = new ServiceBusClientBuilder()
                .connectionString(serviceBusConnectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
        return client;
    }

    private static void sendMessage(ServiceBusSenderClient client) {
        ServiceBusMessage message1 = new ServiceBusMessage("First Message");
        ServiceBusMessage message2 = new ServiceBusMessage("Second Message");
        ServiceBusMessage message3 = new ServiceBusMessage("Third Message");
        client.sendMessage(message1);
        client.sendMessage(message2);
        client.sendMessage(message3);
    }

    private static void sendMessageBatch(ServiceBusSenderClient client) {

        ServiceBusMessage [] messages = {
                new ServiceBusMessage("First Batch Message"),
                new ServiceBusMessage("Second Batch Message"),
                new ServiceBusMessage("Third Batch Message")
        };
        ServiceBusMessageBatch messegeBatch = client.createMessageBatch();
        for (ServiceBusMessage message : messages) {
            if (!messegeBatch.tryAddMessage(message)) {
                throw new IllegalArgumentException("Message is too large to fit in the batch.");
            }
        }
        client.sendMessages(messegeBatch);
    }

    private static void peekMessages(ServiceBusReceiverClient client) {
        Iterable<ServiceBusReceivedMessage> messages = client.peekMessages(10);
        Iterator<ServiceBusReceivedMessage> msgIterator = messages.iterator();
        while (msgIterator.hasNext()) {
            ServiceBusReceivedMessage message = msgIterator.next();
            System.out.println(message.getMessageId());
            System.out.println(message.getBody().toString());
        }
    }

    private static void recieveMessages(ServiceBusReceiverClient recieverClient) {
        Iterable<ServiceBusReceivedMessage> messages = recieverClient.receiveMessages(10);
        Iterator<ServiceBusReceivedMessage> msgIterator = messages.iterator();
        while (msgIterator.hasNext()) {
            ServiceBusReceivedMessage message = msgIterator.next();
            System.out.println(message.getMessageId());
            System.out.println(message.getBody().toString());
        }
    }


    private static void completeMessages(ServiceBusReceiverClient recieverClient) {
        Iterable<ServiceBusReceivedMessage> messages = recieverClient.receiveMessages(10);
        Iterator<ServiceBusReceivedMessage> msgIterator = messages.iterator();
        while (msgIterator.hasNext()) {
            ServiceBusReceivedMessage message = msgIterator.next();
            System.out.println(message.getMessageId());
            System.out.println(message.getBody().toString());
            recieverClient.complete(message);
        }
    }

    private static void peekMessagesFrmDeadLetterQueue(String queueName, String serviceBusListenConnectionString) {
        ServiceBusReceiverClient client = new ServiceBusClientBuilder()
                .connectionString(serviceBusListenConnectionString)
                .receiver()
                .queueName(queueName)
                .subQueue(SubQueue.DEAD_LETTER_QUEUE)
                .buildClient();
        Iterable<ServiceBusReceivedMessage> messages = client.peekMessages(10);
        Iterator<ServiceBusReceivedMessage> msgIterator = messages.iterator();
        while (msgIterator.hasNext()) {
            ServiceBusReceivedMessage message = msgIterator.next();
            System.out.println(message.getMessageId());
            System.out.println(message.getBody().toString());
        }
    }

    private static void duplicateMessageTest() {
        String connectionString = "Endpoint=sb://sbus-saurav-az.servicebus.windows.net/;SharedAccessKeyName=sendPolicy;SharedAccessKey=1ZTCPTXM68PJLrr5CQUu29aZoSISLJh7E+ASbAuV6oo=;EntityPath=sauravqueue2";
        String queueName = "sauravqueue2";
        ServiceBusSenderClient senderClient = buildSenderClient(queueName, connectionString);
        for(int i=1;i<5;i++) {
            ServiceBusMessage message = new ServiceBusMessage("Duplicate Message " + i);
            message.setMessageId(String.valueOf(i));
            senderClient.sendMessage(message);
        }
    }

}
