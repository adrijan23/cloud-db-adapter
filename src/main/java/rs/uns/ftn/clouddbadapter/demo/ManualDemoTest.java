package rs.uns.ftn.clouddbadapter.demo;

import rs.uns.ftn.clouddbadapter.core.DocumentStoreFactory;
import rs.uns.ftn.clouddbadapter.store.DocumentStore;
import rs.uns.ftn.clouddbadapter.orm.SimpleEntityManager;
import rs.uns.ftn.clouddbadapter.demo.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ManualDemoTest {

    public static void runOrmDemo() {
        // ORM:
        DocumentStore store = DocumentStoreFactory.fromEnv();
        SimpleEntityManager em = new SimpleEntityManager(store);

        // Create
        User u = new User()
                .setName("Ana")
                .setEmail("ana@example.com");
        em.save(u);
        System.out.println("Saved: " + u);

        // Read
        Optional<User> got = em.find(User.class, u.getId());
        System.out.println("Found: " + got.orElse(null));

        // Update
        u.setName("Ana Maric");
        em.save(u);
        System.out.println("Updated: " + u);

        // List
        System.out.println("List: " + em.list(User.class, 10));

        // Delete
        em.delete(User.class, u.getId());
        System.out.println("Deleted.");
    }

    public static void runRawDemo() {
        // RAW:
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
    }
}
