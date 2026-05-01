package org.saurav;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventHubService {

    @Value("${mi.clientId}")
    private String miClientId;

    @Value("${event.hub.ns}")
    private String eventHubNS;

    public String ingestStringEvents(List<String> eventList,String hubName) {
        String status="";
        try {
            EventHubProducerClient client =  eventProducerClient(hubName);
            EventDataBatch batch = client.createBatch();
            eventList.forEach(e->batch.tryAdd(new EventData(e)));
            client.send(batch);
            status="Event sent Successfully";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event sent Failed";
        }
        return status;
    }

    public String ingestStudentEvents(List<Student> eventList, String hubName) {
        String status="";
        try {
            EventHubProducerClient client =  eventProducerClient(hubName);
            EventDataBatch batch = client.createBatch();
            eventList.forEach(e-> {
                try {
                    batch.tryAdd(new EventData(new ObjectMapper().writeValueAsString(e)));
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);

                }
            });
            client.send(batch);
            status="Event sent Successfully";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event sent Failed";
        }
        return status;
    }

    public List<String> processEventByPartition(String partitionId,String hubName, String consGrp){
        List<String> eventList = new ArrayList<>();
        eventConsumerClient(hubName,consGrp).receiveFromPartition(partitionId,100, EventPosition.earliest()).
                forEach(e-> {
                    String body  = e.getData().getBodyAsString();
                    System.out.println(body);
                    eventList.add(body);
                });
        return eventList;
    }

    public String processEvents(String hubName, String consGrp) {
        String status="";
        try {
            eventProcessorClient(hubName,consGrp).start();
            status = "Event processing started";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event processing Failed";
        }
       return status;
    }

    public String stopEvents(String hubName, String consGrp) {
        String status="";
        try {
            eventProcessorClient(hubName,consGrp).stop();
            status = "Event processing stopped";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Stop Event processing Failed";
        }
        return status;

    }

    private DefaultAzureCredential getCredential() {
        String ACTIVE_PROFILE = System.getenv("ACTIVE_PROFILE");
        DefaultAzureCredential credential;
        if("local".equalsIgnoreCase(ACTIVE_PROFILE))
            credential = new DefaultAzureCredentialBuilder().build();
        else
            credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(miClientId).build();
        return credential;
    }



    private EventHubProducerClient eventProducerClient(String hubName) {
        EventHubProducerClient client = new EventHubClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(eventHubNS).
                eventHubName(hubName).
                buildProducerClient();
        return client;
    }


    private EventHubConsumerClient eventConsumerClient(String hubName,String consGroup) {
        EventHubConsumerClient client = new EventHubClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(eventHubNS).
                eventHubName(hubName).
                consumerGroup(consGroup).
                buildConsumerClient();
        return client;
    }

    private BlobContainerAsyncClient blobClient() {
        return new BlobContainerClientBuilder().credential(getCredential()).
                endpoint("https://storageaccsauravaz.blob.core.windows.net/event-checkpoint").
                containerName("event-checkpoint").buildAsyncClient();
    }

    private EventProcessorClient eventProcessorClient(String hubName,String consGroup) {
        EventProcessorClient client = new EventProcessorClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace(eventHubNS).
                eventHubName(hubName).
                checkpointStore(new BlobCheckpointStore(blobClient())).
                consumerGroup(consGroup).
                processEvent(this::processEvent).
                processError(this::processError).
                buildEventProcessorClient();
        return client;
    }

    private void processEvent(EventContext context) {
        String body = context.getEventData().getBodyAsString();
        System.out.println("Received: " + body);
        context.updateCheckpoint();
    }
    private void processError(ErrorContext errorContext) {
        System.err.println("Error: " + errorContext.getThrowable());
    }


}
