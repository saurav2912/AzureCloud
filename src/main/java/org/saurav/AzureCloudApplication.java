package org.saurav;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AzureCloudApplication { 

    public static void main(String[] args) {
        String ACTIVE_PROFILE = System.getenv("ACTIVE_PROFILE");
        System.setProperty("spring.profiles.active",ACTIVE_PROFILE);
        SpringApplication.run(AzureCloudApplication.class, args);
    }
}
