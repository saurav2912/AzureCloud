package org.saurav;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class StorageBlob {
    public static void main(String[] args) {
        String containerName = "sauravazjava";
        BlobServiceClient serviceClient = createBlobServiceClient();
        //createBlobContainer(serviceClient,containerName);
        BlobContainerClient blobContainerClient = getBlobContainerClient(serviceClient,containerName);
        //uploadFiletoBlob(blobContainerClient);
        //listBlobs(blobContainerClient);
        //downloadBlob(blobContainerClient);
        //deleteBlob(blobContainerClient);
        deleteBlobCcontainer(serviceClient,containerName);
    }

    private static void deleteBlobCcontainer(BlobServiceClient serviceClient, String containerName) {
        serviceClient.deleteBlobContainer(containerName);
    }


    private static BlobServiceClient createBlobServiceClient() {
        String storageConnectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(storageConnectionString).buildClient();
        return blobServiceClient;
    }

    private static void createBlobContainer(BlobServiceClient client,String containerName) {
        client.createBlobContainer(containerName);
    }

    private static BlobContainerClient getBlobContainerClient(BlobServiceClient client,String containerName) {
         BlobContainerClient blobServiceClient = client.getBlobContainerClient(containerName);
        return blobServiceClient;
    }

    private static void uploadFiletoBlob(BlobContainerClient blobContainerClient) {
        blobContainerClient.getBlobClient("sample.sql").uploadFromFile("C:\\Users\\saura\\OneDrive\\Desktop\\sample.sql");
    }

    private static void listBlobs(BlobContainerClient blobContainerClient) {
        blobContainerClient.listBlobs().forEach(a-> System.out.println(a.getName()));
    }

    private static void downloadBlob(BlobContainerClient blobContainerClient) {
        blobContainerClient.getBlobClient("AZ-204.webp").downloadToFile("C:\\Users\\saura\\OneDrive\\Desktop\\az\\AZ-204.webp");
    }
    private static void deleteBlob(BlobContainerClient blobContainerClient) {
        blobContainerClient.getBlobClient("sample.sql").delete();
    }
}

