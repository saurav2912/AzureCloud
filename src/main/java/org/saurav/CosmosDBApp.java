package org.saurav;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CosmosDBApp {

    private static String COSMOS_URL = "https://cosmosaccsauravaz.documents.azure.com:443/";
    private static String CONNECTION_KEY = "MimVX3pqgBlAdUx6EbddKrXt3Kat3M4ZGTkZGBSJKhdsqFnohVGUrMxg1mYQAubRKSyh1z4vRbOgACDbfR3DZg==";

    public static void main(String [] args) throws JsonProcessingException {
        //createDatabase();
        addRecords();
        //readRecords();
        //updateRecords();
        //insertSubjects();
        //deleteRecord();
        //deleteContainer();
        //deleteDatabase();
        //callStoredProcedure();
    }



    private static void createDatabase() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        client.createDatabaseIfNotExists("StudentDB");
        CosmosDatabase db = client.getDatabase("StudentDB");
        db.createContainer("StudentContainer", "/department");
    }

    private static void addRecords() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        Student student1 = new Student("4","Rakesh", "Bangalore", 30, "IT");
        Student student2 = new Student("5","Ravi", "Delhi", 20, "Operation");
        Student student3 = new Student("6","Rekha", "Bangalore", 23, "Cleaning");
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        options.setPreTriggerInclude(List.of("dateTrigger"));
        container.createItem(student1,new PartitionKey(student1.getDepartment()), options);
        container.createItem(student2,new PartitionKey(student2.getDepartment()), options);
        container.createItem(student3,new PartitionKey(student3.getDepartment()), options);
    }

    private static void readRecords() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        String sqlQuery = "SELECT * FROM c where c.address = 'Bangalore'";
        container.queryItems(sqlQuery, new CosmosQueryRequestOptions(), Student.class)
                .forEach(student -> System.out.println(student));
    }
    private static void updateRecords() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        Student student = container.readItem("1", new PartitionKey("Saurav"), Student.class).getItem();
        student.setDepartment("IT");
        container.upsertItem(student);
    }

    private static void insertSubjects() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        String sqlQuery = "SELECT * FROM c";
        CosmosPagedIterable<Student> items =  container.queryItems(sqlQuery,new CosmosQueryRequestOptions(),Student.class);
        items.forEach(student -> {
            Subject subject1 = new Subject("1","Maths");
            Subject subject2 = new Subject("2","Science");
            Subject subject3 = new Subject("3","English");
            List<Subject> subjectList = Arrays.asList(subject1,subject2,subject3);
            student.setSubjects(subjectList);
           container.upsertItem(student);
        });
    }

    private static void deleteRecord() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        Student stu = container.readItem("3", new PartitionKey("Sawarvi"),Student.class).getItem();
        container.deleteItem(stu.getId(), new PartitionKey(stu.getName()), new CosmosItemRequestOptions());
    }

    private static void deleteContainer() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        container.delete();
    }

    private static void deleteDatabase() {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosDatabase db = client.getDatabase("StudentDB");
        db.delete();
    }

    private static void callStoredProcedure() throws JsonProcessingException {
        CosmosClient client = new CosmosClientBuilder().endpoint(COSMOS_URL).key(CONNECTION_KEY).buildClient();
        CosmosContainer container = client.getDatabase("StudentDB").getContainer("StudentContainer");
        CosmosStoredProcedureResponse response = container.getScripts().getStoredProcedure("findByDepartment")
                .execute(Arrays.asList("Finance"),
                        new CosmosStoredProcedureRequestOptions().setPartitionKey(new PartitionKey("Finance")));
        String studentsString = response.getResponseAsString();
        System.out.println(studentsString);
        Student[] studentList = new ObjectMapper().readValue(studentsString, Student[].class);
        System.out.println(Arrays.asList(studentList));
    }
}
