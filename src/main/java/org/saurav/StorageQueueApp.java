package org.saurav;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import com.azure.storage.queue.models.PeekedMessageItem;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Iterator;

@Service
public class StorageQueueApp {

    @Value("${storage.queue.endpoint}")
    private String endpoint;


    private QueueClient buildQueue(String qName) {
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
        QueueClient client = new QueueClientBuilder().credential(credential).endpoint(endpoint)
                .queueName(qName)
                .buildClient();
        return client;

    }

    public String createQueue(String queueName) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            client.create();
            message = "Queue Created SuccessFully with Name : "+queueName;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "Queue Creation Failed";
        }
        return message;
    }

    private static void sendmessage(QueueClient client) throws JsonProcessingException {


    }

    private static void peekMessage(QueueClient client) {

    }

    private static void recieveMessage(QueueClient client) {

    }

    private static void updateMessage(QueueClient client) {

    }
    private static void getQueueLength(QueueClient client) {
    }

    private static void deleteMessages(QueueClient client) {

    }

    public String deleteQueue(String queueName) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            client.delete();
            message = "Queue Deleted SuccessFully with Name : "+queueName;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "Queue Deletion Failed";
        }
        return message;
    }

}