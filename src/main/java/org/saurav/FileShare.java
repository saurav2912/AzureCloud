package org.saurav;

import com.azure.storage.file.share.*;

import java.io.File;

public class FileShare {

    public static String connectionString = "DefaultEndpointsProtocol=https;AccountName=storageaccsauravaz;AccountKey=pB0capBd+Mh86NvSIIIG/bLI3CEQ+4mm/g5Ie7gETRRgaeh3JdOUZcrWG+KCEUMjS+YWAWkN8Sel+AStL02EtQ==;EndpointSuffix=core.windows.net";
    public static String fileShareName = "filesharesauravaz";
    public static String dirName = "smp";
    public static String fileName = "swati.txt";

    public static void main(String args []) {
        ShareServiceClient client = buildShareServiceClient(connectionString);
        //createFileShare(client);
        //createCustomDirectories(client);
        //createFilesInDir(client);
        //uploadLocalFile(client);
        //listAllFiles(client);
        //deleteFiles(client,fileName);
        deleteAllFiles(client);
    }

    private static ShareServiceClient buildShareServiceClient(String connectionString) {
       return new ShareServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    private static void createFileShare(ShareServiceClient client) {
        client.createShare(fileShareName);
    }

    private static void createCustomDirectories(ShareServiceClient client) {
        for (int i=0; i<5; i++) {
            client.getShareClient(fileShareName).createDirectory(dirName+i);
        }

    }

    private static void createFilesInDir(ShareServiceClient client) {
        for (int i=0; i<5; i++) {
            client.getShareClient(fileShareName).getDirectoryClient(dirName+i).createFile(fileName,50);
        }
    }

    private static void uploadLocalFile(ShareServiceClient client) {
        for (int i=0; i<5; i++) {
            String filePath = "C:\\Users\\saura\\OneDrive\\Desktop\\AZ-204.webp";
            String fileName = "AZ-204.webp";
            File file = new File(filePath);
            ShareFileClient fileClient = client.getShareClient(fileShareName).getDirectoryClient(dirName+i)
                    .getFileClient(fileName);
            fileClient.create(file.length());
            fileClient.uploadFromFile(filePath);
        }
    }

    private static void listAllFiles(ShareServiceClient client) {
        ShareDirectoryClient directoryClient = client.getShareClient(fileShareName).getRootDirectoryClient();
        directoryClient.listFilesAndDirectories().forEach(dir->{
            System.out.println(dir.getName());
            System.out.println("----------------------------START-----------------------------------------");
            client.getShareClient(fileShareName).getDirectoryClient(dir.getName())
                    .listFilesAndDirectories().forEach(f->System.out.println(f.getName()));
            System.out.println("----------------------------END-----------------------------------------");
        });
    }

    private static void deleteFiles(ShareServiceClient client, String fileName) {
        client.getShareClient(fileShareName).getRootDirectoryClient()
                .listFilesAndDirectories().forEach(d-> {
                    client.getShareClient(fileShareName).getDirectoryClient(d.getName()).getFileClient(fileName).delete();
                });
    }
    private static void deleteAllFiles(ShareServiceClient client) {
        client.getShareClient(fileShareName).delete();
    }

}
