package org.saurav;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceBusTopic {

    public static void main(String[] args) {
        String topicConnectionString = System.getenv("AZURE_SERVICE_BUS_TOP_MANAGE_CONNECTION_STRING");
        ServiceBusReceiverClient receiverClient = builtReceiverClient(topicConnectionString);
        ServiceBusSenderClient senderClient = builtSenderClient(topicConnectionString);
        sendMessage(senderClient);
        //reciveMessages(receiverClient);
    }


    private static ServiceBusSenderClient builtSenderClient(String topicConnectionString) {
        ServiceBusSenderClient client = new ServiceBusClientBuilder()
                .connectionString(topicConnectionString)
                .sender()
                .topicName("saurav-az-topic")
                .buildClient();
        return client;
    }

    private static ServiceBusReceiverClient builtReceiverClient(String topicConnectionString) {
        ServiceBusReceiverClient client = new ServiceBusClientBuilder()
                .connectionString(topicConnectionString)
                .receiver()
                .topicName("saurav-az-topic")
                .subscriptionName("sub2")
                .buildClient();
        return client;
    }

    private static void sendMessage(ServiceBusSenderClient senderClient) {

        ObjectMapper mapper = new ObjectMapper();
        try {
                ServiceBusMessage message1 = new ServiceBusMessage(mapper.writeValueAsString(new Student("1","Saurav","Tulasipur",35,"Finance")));
                message1.getApplicationProperties().put("Department","Finance");
                ServiceBusMessage message2 = new ServiceBusMessage(mapper.writeValueAsString(new Student("2","Swati","Rajabagicha",33,"HR")));
                message2.getApplicationProperties().put("Department","HR");
                ServiceBusMessage message3 = new ServiceBusMessage(mapper.writeValueAsString(new Student("3","Satyam","Tulasipur",53,"IT")));
                message3.getApplicationProperties().put("Department","IT");
                senderClient.sendMessage(message1);
                senderClient.sendMessage(message2);
                senderClient.sendMessage(message3);

            } catch (Exception e) {
                System.out.println("Error sending message: " + e.getMessage());
            } finally {
                senderClient.close();
        }

    }
    private static void reciveMessages(ServiceBusReceiverClient receiverClient) {
        try {
            receiverClient.receiveMessages(10).forEach(message -> {
                System.out.println("Received message: " + message.getBody().toString());
                receiverClient.complete(message);
            });
        } catch (Exception e) {
            System.out.println("Error receiving messages: " + e.getMessage());
        } finally {
            receiverClient.close();
        }
    }

}
