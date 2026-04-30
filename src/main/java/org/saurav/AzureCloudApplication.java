package org.saurav;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AzureCloudApplication {

    @Value("${mi.clientId}")
    private String miClientId;

    public static void main(String[] args) {
        String ACTIVE_PROFILE = System.getenv("ACTIVE_PROFILE");
        System.setProperty("spring.profiles.active",ACTIVE_PROFILE);
        SpringApplication.run(AzureCloudApplication.class, args);
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


    @Bean
    public EventHubProducerClient eventProducerClient() {
        EventHubProducerClient client = new EventHubClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace("event-hub-ns-sauravaz.servicebus.windows.net").
                eventHubName("student-events").
                buildProducerClient();
        return client;
    }

    @Bean
    public EventHubConsumerClient eventConsumerClient() {
        EventHubConsumerClient client = new EventHubClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace("event-hub-ns-sauravaz.servicebus.windows.net").
                eventHubName("student-events").
                consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME).
                buildConsumerClient();
        return client;
    }

    private BlobContainerAsyncClient blobClient() {
        return new BlobContainerClientBuilder().credential(getCredential()).
                endpoint("https://storageaccsauravaz.blob.core.windows.net/event-checkpoint").
                containerName("event-checkpoint").buildAsyncClient();
    }

    @Bean
    public EventProcessorClient eventProcesserClient() {
        EventProcessorClient client = new EventProcessorClientBuilder().credential(getCredential()).
                fullyQualifiedNamespace("event-hub-ns-sauravaz.servicebus.windows.net").
                eventHubName("student-events").
                checkpointStore(new BlobCheckpointStore(blobClient())).
                consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME).
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
