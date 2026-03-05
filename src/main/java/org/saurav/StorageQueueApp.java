package org.saurav;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import com.azure.storage.queue.models.PeekedMessageItem;
import com.azure.storage.queue.models.QueueMessageItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Iterator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class StorageQueueApp {
    public static void main(String[] args) throws JsonProcessingException {
        String storageConnectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        QueueClient client = buildQueue("sauravazqueue", storageConnectionString);
        createQueue(client);
        sendmessage(client);
        //peekMessage(client);
        //recieveMessage(client);
        //updateMessage(client);
        //getQueueLength(client);
        //deleteMessages(client);
        //deleteQueue(client);

    }

    private static QueueClient buildQueue(String qName, String connString) {
        QueueClient client = new QueueClientBuilder()
                .connectionString(connString).queueName(qName).messageEncoding(QueueMessageEncoding.BASE64)
                .buildClient();
        return client;

    }

    private static void createQueue(QueueClient client) {
        client.create();

    }

    private static void sendmessage(QueueClient client) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        client.sendMessage(mapper.writeValueAsString(new Student("Saurav","Tulasipur",35)));
        client.sendMessage(mapper.writeValueAsString(new Student("Swati","Rajabagicha",33)));
        client.sendMessage("3rd Message");
        client.sendMessage("4th Message");

    }

    private static void peekMessage(QueueClient client) {
        Iterable<PeekedMessageItem> iter = client.peekMessages(4,null,null);
        Iterator<PeekedMessageItem> iterator = iter.iterator();
        while(iterator.hasNext()) {
            PeekedMessageItem msgItem = iterator.next();
            System.out.println(msgItem.getMessageId());
            System.out.println(msgItem.getMessageText());
            System.out.println(msgItem.getBody());
        }
    }

    private static void recieveMessage(QueueClient client) {
        Iterable<QueueMessageItem> iter = client.receiveMessages(4);
        Iterator<QueueMessageItem> iterator = iter.iterator();
        while(iterator.hasNext()) {
            QueueMessageItem msgItem = iterator.next();
            System.out.println(msgItem.getMessageId());
            System.out.println(msgItem.getBody());
        }
    }

    private static void updateMessage(QueueClient client) {
        Iterable<QueueMessageItem> iter = client.receiveMessages(4);
        Iterator<QueueMessageItem> iterator = iter.iterator();
        int i=1;
        while(iterator.hasNext()) {
            QueueMessageItem msgItem = iterator.next();
            client.updateMessage(msgItem.getMessageId(),msgItem.getPopReceipt(),
                    "Updated Message "+i,Duration.ofSeconds(2));
            i++;
        }
    }
    private static void getQueueLength(QueueClient client) {
        System.out.println(client.getProperties().getApproximateMessagesCount());
    }

    private static void deleteMessages(QueueClient client) {
        Iterable<QueueMessageItem> iter = client.receiveMessages(4);
        Iterator<QueueMessageItem> iterator = iter.iterator();
        while(iterator.hasNext()) {
            QueueMessageItem msgItem = iterator.next();
            client.deleteMessage(msgItem.getMessageId(),msgItem.getPopReceipt());
        }
    }

    private static void deleteQueue(QueueClient client) {
        client.delete();
    }

}