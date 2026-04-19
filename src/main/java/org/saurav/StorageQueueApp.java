package org.saurav;

import com.azure.core.http.rest.PagedIterable;
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
import java.util.stream.Collectors;

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

    public String sendStudent(String queueName, List<Student> students) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            students.forEach(s-> {
                try {
                    client.sendMessage(new ObjectMapper().writeValueAsString(s));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            message = "Messages are successfully pushed ";
        }catch (Exception ex) {
            ex.printStackTrace();
            message = "Message Pushing Failed";
        }
        return message;

    }

    public List<String> peekMessages(String queueName) {
        List<String> messages= null;
        try {
            QueueClient client = buildQueue(queueName);
            Long length = client.getProperties().getApproximateMessagesCountLong();
            PagedIterable<PeekedMessageItem> messageList = client.peekMessages(length.intValue(),null,null);
            messages = messageList.stream().map(m->m.getMessageText()).toList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    public List<String> recieveMessage(String queueName) {
        List<String> messages= null;
        try {
            QueueClient client = buildQueue(queueName);
            Long length = client.getProperties().getApproximateMessagesCountLong();
            PagedIterable<QueueMessageItem> messageList = client.receiveMessages(length.intValue());
            messages = messageList.stream().map(m->m.getMessageText()).toList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    public String updateMessage(String queueName, String id) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            Long length = client.getProperties().getApproximateMessagesCountLong();
            for (int i=0;i<length.intValue();i++) {
                QueueMessageItem item =  client.receiveMessage();
                Student stu = new ObjectMapper().readValue(item.getMessageText(), Student.class);
                if(stu.getId().equals(id)) {
                    stu.setAge(40);
                    client.updateMessage(item.getMessageId(), item.getPopReceipt(),new ObjectMapper().writeValueAsString(stu),null);
                }
            }

            message = "Messages are successfully updated ";
        }catch (Exception ex) {
            ex.printStackTrace();
            message = "Message Update Failed";
        }
        return message;
    }
    private static void getQueueLength(QueueClient client) {
    }

    public String deleteAllMessages(String queueName) {
        String message = "";
        try {
            QueueClient client = buildQueue(queueName);
            client.clearMessages();
            message = "All messsages cleared from queue: "+queueName;
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "Message clearance failed";
        }
        return message;
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