package org.saurav;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.servicebus.*;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ServiceBusQueueApp {

    @Value("${mi.clientId}")
    private String miClientId;

    private static final String QUALIFIED_NAME_SBUS = "sb-sauravaz.servicebus.windows.net";

    private DefaultAzureCredential getCredential() {
        DefaultAzureCredential credential;
        String ACTIVE_PROFILE = System.getenv("ACTIVE_PROFILE");
        if("local".equalsIgnoreCase(ACTIVE_PROFILE)) {
            credential = new DefaultAzureCredentialBuilder().build();
        } else {
            credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(miClientId).build();
        }
        return credential;
    }

    private ServiceBusReceiverClient buildRecieverClient(String queueName) {
        DefaultAzureCredential credential = getCredential();
        ServiceBusReceiverClient client = new ServiceBusClientBuilder().credential(credential).fullyQualifiedNamespace(QUALIFIED_NAME_SBUS)
                .receiver().queueName(queueName).receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE).
                buildClient();
        return client;
    }

    private ServiceBusSenderClient buildSenderClient(String queueName) {
        DefaultAzureCredential credential = getCredential();
        ServiceBusSenderClient client = new ServiceBusClientBuilder().credential(credential).fullyQualifiedNamespace(QUALIFIED_NAME_SBUS)
                .sender().queueName(queueName).buildClient();
        return client;
    }

    public String sendMessage(String queueName,String message) {
        String status = "";
        try {
            ServiceBusSenderClient client = buildSenderClient(queueName);
            client.sendMessage(new ServiceBusMessage(message));
            status = "Message Sent Successfully";
        } catch(Exception ex) {
            ex.printStackTrace();
            status = "Message Sending Failed";
        }
        return status;
    }

    public String sendMessageBatch(String queueName,List<String> messages) {
        String status = "";
        try {
            ServiceBusSenderClient client = buildSenderClient(queueName);
            ServiceBusMessageBatch batch = client.createMessageBatch();
            AtomicInteger i = new AtomicInteger(0);
            messages.forEach(m->{
                ServiceBusMessage msg = new ServiceBusMessage(m);
                msg.setMessageId(String.valueOf(i.incrementAndGet()));
                msg.setContentType("Text");
                msg.setSubject("Testing message Batch");
                msg.getApplicationProperties().put(String.valueOf(i.get()),"Value"+i.get());
                batch.tryAddMessage(msg);
            });
            client.sendMessages(batch);
            status = "Message Sent Successfully";
        } catch(Exception ex) {
            ex.printStackTrace();
            status = "Message Sending Failed";
        }
        return status;
    }

    public String peekMessage(String queueName) {
        String message = null;
        try {
            ServiceBusReceiverClient client = buildRecieverClient(queueName);
            ServiceBusReceivedMessage receivedMessage=client.peekMessage();
            if(null==receivedMessage) {
                client = buildRecieverClient(queueName+"/$deadletterqueue");
                receivedMessage = client.peekMessage();
            }
            message = receivedMessage.getBody().toString();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public List<String> recieveMessages(String queueName, boolean isDead) {
        List<String> messages = new ArrayList<>();
        try {
            ServiceBusReceiverClient client = null;
            if(isDead) {
                client = buildRecieverClient(queueName+"/$deadletterqueue");
            } else {
                client = buildRecieverClient(queueName);
            }
            Iterable<ServiceBusReceivedMessage> messagesIter = client.receiveMessages(50);
            messagesIter.forEach(i->messages.add(i.getBody().toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }


    public List<String> completeMessages(String queueName, boolean isDead) {
        List<String> messages = new ArrayList<>();
        try {
            AtomicReference<ServiceBusReceiverClient> atomicReference = new AtomicReference<>();
             ServiceBusReceiverClient client = null;
            if(isDead) {
                client = buildRecieverClient(queueName+"/$deadletterqueue");
            } else {
                client = buildRecieverClient(queueName);
            }
            atomicReference.set(client);
            Iterable<ServiceBusReceivedMessage> messagesIter = client.receiveMessages(50);
            messagesIter.forEach(i-> {
                messages.add(i.getBody().toString());
                atomicReference.get().complete(i);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    public List<String> peekMessages(String queueName) {
        List<String> messages = new ArrayList<>();
        try {
            ServiceBusReceiverClient client = buildRecieverClient(queueName);
            Iterable<ServiceBusReceivedMessage> messagesIter = client.peekMessages(20);
            messagesIter.forEach(i->messages.add(i.getBody().toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    public List<String> peekMessagesFrmDeadLetterQueue(String queueName) {
        List<String> messages = new ArrayList<>();
        try {
            ServiceBusReceiverClient client = buildRecieverClient(queueName+"/$deadletterqueue");
            Iterable<ServiceBusReceivedMessage> messagesIter = client.peekMessages(20);
            messagesIter.forEach(i->messages.add(i.getBody().toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return messages;
    }

    private static void duplicateMessageTest() {

    }

}
