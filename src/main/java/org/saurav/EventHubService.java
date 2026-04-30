package org.saurav;

import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventHubService {

    @Autowired
    private EventHubProducerClient eventHubProducerClient;

    @Autowired
    private EventHubConsumerClient eventHubConsumerClient;

    @Autowired
    private EventProcessorClient eventProcessorClient;

    public String ingestStringEvents(List<String> eventList) {
        String status="";
        try {
            EventDataBatch batch = eventHubProducerClient.createBatch();
            eventList.forEach(e->batch.tryAdd(new EventData(e)));
            eventHubProducerClient.send(batch);
            status="Event sent Successfully";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event sent Failed";
        }
        return status;
    }

    public String ingestStudentEvents(List<Student> eventList) {
        String status="";
        try {
            EventDataBatch batch = eventHubProducerClient.createBatch();
            eventList.forEach(e-> {
                try {
                    batch.tryAdd(new EventData(new ObjectMapper().writeValueAsString(e)));
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);

                }
            });
            eventHubProducerClient.send(batch);
            status="Event sent Successfully";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event sent Failed";
        }
        return status;
    }

    public List<String> processEventByPartition(String partitionId){
        List<String> eventList = new ArrayList<>();
        eventHubConsumerClient.receiveFromPartition(partitionId,100, EventPosition.earliest()).
                forEach(e-> {
                    String body  = e.getData().getBodyAsString();
                    System.out.println(body);
                    eventList.add(body);
                });
        return eventList;
    }

    public String processEvents() {
        String status="";
        try {
            eventProcessorClient.start();
            status = "Event processing started";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Event processing Failed";
        }
       return status;
    }

    public String stopEvents() {
        String status="";
        try {
            eventProcessorClient.stop();
            status = "Event processing stopped";
        } catch (Exception ex) {
            ex.printStackTrace();
            status="Stop Event processing Failed";
        }
        return status;

    }


}
