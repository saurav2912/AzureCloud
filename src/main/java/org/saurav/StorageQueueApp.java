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
import java.util.List;

@Service
public class StorageQueueApp {

    @Value("${storage.queue.endpoint}")
    private String endpoint;

    @Value("${mi.clientId}")
    private String miClientId;


    private QueueClient buildQueue(String qName) {
        String ACTIVE_PROFILE = System.getenv("ACTIVE_PROFILE");
        DefaultAzureCredential credential;
        if("local".equals(ACTIVE_PROFILE)) {
            credential = new DefaultAzureCredentialBuilder().build();
        } else {
            credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(miClientId).build();
        }

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

    public String sendmessage(String queueName, List<String> messages) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            messages.forEach(m->client.sendMessage(m));
            message = "Messages are successfully pushed ";
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "Message Pushing Failed";
        }
        return message;

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