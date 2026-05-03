package org.saurav;

import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class CustomEventGrid {

    @Value("${mi.clientId}")
    private String miClientId;

    @PostMapping("/trigger/student")
    public void triggerEvent(@RequestBody Student student) {

        DefaultAzureCredential credential;
        if(System.getenv("ACTIVE_PROFILE").equals("local"))
            credential = new DefaultAzureCredentialBuilder().build();
        else
            credential = new DefaultAzureCredentialBuilder().managedIdentityClientId(miClientId).build();

        EventGridPublisherClient<EventGridEvent> client =
                new EventGridPublisherClientBuilder().credential(credential).endpoint("https://custom-event-sauravaz.centralindia-1.eventgrid.azure.net/api/events")
                        .buildEventGridEventPublisherClient();
        EventGridEvent event = new EventGridEvent( "custom/subject","student",
                BinaryData.fromObject(student),"1.0");
        client.sendEvents(Collections.singletonList(event));
    }
}
