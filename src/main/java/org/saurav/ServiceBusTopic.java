package org.saurav;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.servicebus.*;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.azure.messaging.servicebus.models.SubQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceBusTopic {

    @Value("${mi.clientId}")
    private String miClientId;

    private final static String QUALIFIED_NAME="sb-sauravaz.servicebus.windows.net";
    private final static String END_POINT = "https://sb-sauravaz.servicebus.windows.net";

    private DefaultAzureCredential getCredential() {
        DefaultAzureCredential credential;
        String profile = System.getenv("ACTIVE_PROFILE");
        if(profile.equalsIgnoreCase("local"))
            credential = new DefaultAzureCredentialBuilder().build();
        else
            credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(miClientId).build();
        return credential;
    }

    public String createTopic(String topicName) {
        String status = "";
        try {
            ServiceBusAdministrationClient client = new ServiceBusAdministrationClientBuilder().credential(getCredential())
                    .endpoint(END_POINT).buildClient();
            client.createTopic(topicName);
            status = "Topic Created Successfully with name : "+topicName;
        } catch (Exception ex) {
            ex.printStackTrace();
            status = "Topic Creation failed";
        }
        return status;
    }

    private ServiceBusSenderClient builtSenderClient(String topicName) {
        ServiceBusSenderClient client= new ServiceBusClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(QUALIFIED_NAME).sender().topicName(topicName).buildClient();
        return client;
    }

    private ServiceBusReceiverClient builtReceiverClient(String topicName,String subName) {
        ServiceBusReceiverClient client= new ServiceBusClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(QUALIFIED_NAME).
                receiver().topicName(topicName).subscriptionName(subName).receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE).
                buildClient();
        return client;
    }

    private ServiceBusReceiverClient builtReceiverClientforDeadLetterqueue(String topicName,String subName) {
        ServiceBusReceiverClient client= new ServiceBusClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(QUALIFIED_NAME).
                receiver().topicName(topicName).subQueue(SubQueue.DEAD_LETTER_QUEUE).
                subscriptionName(subName).receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE).
                buildClient();
        return client;
    }




    public String sendMessage(String topicName,String message) {
        String status="";
        try {
            ServiceBusSenderClient client = builtSenderClient(topicName);
            client.sendMessage(new ServiceBusMessage(message));
            status = "Message sent Successfully";
        } catch(Exception ex) {
            ex.printStackTrace();
            status = "Message sending failed";
        }
        return status;
    }

    public String sendMessages(String topicName, List<String> messages) {
        String status="";
        try {
            ServiceBusSenderClient client = builtSenderClient(topicName);
            ServiceBusMessageBatch batch = client.createMessageBatch();
            messages.forEach(m->batch.tryAddMessage(new ServiceBusMessage(m)));
            client.sendMessages(batch);
            status = "Message List sent Successfully";
        } catch(Exception ex) {
            ex.printStackTrace();
            status = "Message sending failed";
        }
        return status;
    }

    public String sendStudents(String topicName, List<Student> studentList) {
        String status="";
        try {
            ServiceBusSenderClient client = builtSenderClient(topicName);
            ServiceBusMessageBatch batch = client.createMessageBatch();
            studentList.forEach(s-> {
                try {
                    batch.tryAddMessage(new ServiceBusMessage(new ObjectMapper().writeValueAsString(s)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            client.sendMessages(batch);
            status = "Message List sent Successfully";
        } catch(Exception ex) {
            ex.printStackTrace();
            status = "Message sending failed";
        }
        return status;
    }
    public List<String> receiveMessages(String topicName, String subscription) {
        List<String> messages=new ArrayList<>();
        try {
            ServiceBusReceiverClient client = builtReceiverClient(topicName,subscription);
            Iterable<ServiceBusReceivedMessage> msgList = client.receiveMessages(50, Duration.ofMinutes(1));
            if(!msgList.iterator().hasNext()) {
                client = builtReceiverClientforDeadLetterqueue(topicName,subscription);
                msgList = client.receiveMessages(50,Duration.ofMinutes(1));
            }
            msgList.forEach(m->messages.add(m.getBody().toString()));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    public List<String> receiveMessage(String topicName, String subscription) {
        List<String> messageList= new ArrayList<>();
        try {
            ServiceBusReceiverClient client = builtReceiverClient(topicName,subscription);
            Iterable<ServiceBusReceivedMessage> msgs = client.peekMessages(20);
            if(!msgs.iterator().hasNext()) {
                client = builtReceiverClientforDeadLetterqueue(topicName,subscription);
                msgs = client.peekMessages(20);
            }
            msgs.forEach(m->messageList.add(m.getBody().toString()));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return messageList;
    }

}
