package org.saurav;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;

public class CosmosDBTable {

    private static String CONNECTION_KEY = "DefaultEndpointsProtocol=https;AccountName=cosmosaccsauravaztable;AccountKey=RrVUj3PFWlyw80feTHZi9tyZ6Cxo9R4meMpQq1pPJHgNmH93cAkm511VCIeCQHSwiZZYt2ZMZyGQACDbobkYnA==;TableEndpoint=https://cosmosaccsauravaztable.table.cosmos.azure.com:443/;";

    public static void main(String [] args) {
        //createRecord();
        //readRecord();
        updateRecord();
    }


    private static void createRecord() {
        TableClient tableClient = new TableClientBuilder().tableName("EMPLOYEE").connectionString(CONNECTION_KEY).buildClient();
        tableClient.createTable();
        TableEntity entity = new TableEntity("IT","E01");
        entity.addProperty("name","Saurav");
        entity.addProperty("Department","Engineering");
        tableClient.createEntity(entity);
    }

    private static void readRecord() {
        TableClient tableClient = new TableClientBuilder().tableName("EMPLOYEE").connectionString(CONNECTION_KEY).buildClient();
        TableEntity entity = tableClient.getEntity("IT","E01");
        System.out.println(entity.getProperty("Department"));
    }

    private static void updateRecord() {
        TableClient tableClient = new TableClientBuilder().tableName("EMPLOYEE").connectionString(CONNECTION_KEY).buildClient();
        TableEntity entity = tableClient.getEntity("IT","E01");
        entity.addProperty("Department","CCA");
        tableClient.updateEntity(entity);
    }

}
