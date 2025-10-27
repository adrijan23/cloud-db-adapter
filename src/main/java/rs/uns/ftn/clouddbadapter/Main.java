package rs.uns.ftn.clouddbadapter;


import rs.uns.ftn.clouddbadapter.core.DocumentStoreFactory;
import rs.uns.ftn.clouddbadapter.store.DocumentStore;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        DocumentStore store = DocumentStoreFactory.fromEnv();

        String collection = "Users";
        String id = UUID.randomUUID().toString();

        // CREATE / UPSERT
        store.updateById(collection, id, Map.of("name","Ana","email","ana@example.com"));
        System.out.println("Created: " + id);

        // READ
        Optional<Map<String,Object>> got = store.getById(collection, id);
        System.out.println("Read: " + got.orElse(Map.of()));

        // UPDATE
        store.updateById(collection, id, Map.of("name","Ana Maric","email","ana@example.com"));
        System.out.println("Updated.");

        // READ
        got = store.getById(collection, id);
        System.out.println("Read: " + got.orElse(Map.of()));


        // DELETE
        store.deleteById(collection, id);
        System.out.println("Deleted.");

        System.out.println("The End!");
    }
}