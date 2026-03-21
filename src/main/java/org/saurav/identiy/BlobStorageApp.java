package org.saurav.identiy;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class BlobStorageApp {

    private static final String APP_ID = "ffcf709a-9de1-4e33-88bf-b487e53e71b2";
    private static final String TENANT_ID = "f40262c3-5794-45b5-8fbd-bcdba3b6cfec";
    private static final String SECRET = "Qpz8Q~O5_l3bTJLQUxx2FmkFyoS~ocesSN-3-aRN";

    public static void main(String args[]) {
        ClientSecretCredential credential = buildClientSecret();
        retriveBlobFile(credential);
    }

    private static void retriveBlobFile(ClientSecretCredential credential) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .endpoint("https://storageaccsauravaz.blob.core.windows.net/").credential(credential).buildClient();
        BlobContainerClient containerClient = serviceClient.getBlobContainerClient("sauravazblob");
        //BlobClient blobClient = containerClient.getBlobClient("AZ-204.webp");
        //blobClient.delete();
        containerClient.delete();
    }

    private static ClientSecretCredential buildClientSecret() {
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(APP_ID).tenantId(TENANT_ID).clientSecret(SECRET).build();
        return clientSecretCredential;
    }

}
