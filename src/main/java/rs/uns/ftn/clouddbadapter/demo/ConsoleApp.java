package rs.uns.ftn.clouddbadapter.demo;

import rs.uns.ftn.clouddbadapter.core.DocumentStoreFactory;
import rs.uns.ftn.clouddbadapter.core.Provider;
import rs.uns.ftn.clouddbadapter.orm.EntityManager;
import rs.uns.ftn.clouddbadapter.orm.SimpleEntityManager;
import rs.uns.ftn.clouddbadapter.store.DocumentStore;

import java.util.*;

public class ConsoleApp {
    private final Scanner in = new Scanner(System.in);

    public void run() {
        System.out.println("=== Cloud DB Adapter Demo ===");

        while (true) {
            Provider p = pickProvider();
            if (p == null) {
                System.out.println("Bye!");
                return;
            }

            DocumentStore store = DocumentStoreFactory.from(p);
            EntityManager em = new SimpleEntityManager(store);

            while (true) {
                System.out.println("""
                    \nMain Menu
                    1) Manage Users (ORM)
                    2) Generic CRUD (RAW)
                    0) Back
                    """);
                System.out.print("Choice: ");
                String choice = in.nextLine().trim();
                switch (choice) {
                    case "1" -> usersMenu(em);
                    case "2" -> rawMenu(store);
                    case "0" -> { break; }
                    default -> System.out.println("Unknown option.");
                }
                if (choice.equals("0")) break;
            }
        }
    }

    private Provider pickProvider() {
        System.out.println("""
            Choose provider:
            1) AWS DynamoDB
            2) Google Firestore
            3) Azure Cosmos (Core/SQL)
            0) Exit
            """);
        System.out.print("Choice: ");
        String c = in.nextLine().trim();
        return switch (c) {
            case "1" -> Provider.AWS;
            case "2" -> Provider.GCP;
            case "3" -> Provider.AZURE;
            case "0" -> null;
            default -> Provider.AWS;
        };
    }

    // ---------- ORM USERS ----------
    private void usersMenu(EntityManager em) {
        while (true) {
            System.out.println("""
                \nUsers (ORM)
                1) Create
                2) Get by id
                3) Update
                4) Delete
                5) List
                0) Back
                """);
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1" -> {
                        System.out.print("name: "); String name = in.nextLine();
                        System.out.print("email: "); String email = in.nextLine();
                        User u = new User().setName(name).setEmail(email);
                        em.save(u);
                        System.out.println("Saved: " + u);
                    }
                    case "2" -> {
                        System.out.print("id: "); String id = in.nextLine();
                        System.out.println(em.find(User.class, id).orElse(null));
                    }
                    case "3" -> {
                        System.out.print("id: "); String id = in.nextLine();
                        var u = em.find(User.class, id).orElse(null);
                        if (u == null) { System.out.println("Not found."); break; }
                        System.out.print("new name (blank=skip): "); String n = in.nextLine();
                        System.out.print("new email (blank=skip): "); String e = in.nextLine();
                        if (!n.isBlank()) u.setName(n);
                        if (!e.isBlank()) u.setEmail(e);
                        em.save(u);
                        System.out.println("Updated: " + u);
                    }
                    case "4" -> {
                        System.out.print("id: "); String id = in.nextLine();
                        em.delete(User.class, id);
                        System.out.println("Deleted (if existed).");
                    }
                    case "5" -> {
                        var list = em.list(User.class, 20);
                        list.forEach(System.out::println);
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Unknown.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    // ---------- RAW GENERIC ----------
    private void rawMenu(DocumentStore store) {
        while (true) {
            System.out.println("""
                \nGeneric CRUD (RAW)
                1) Upsert (collection, id, fields)
                2) Get by id
                3) Delete
                0) Back
                """);
            System.out.print("Choice: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1" -> {
                        System.out.print("collection: "); String coll = in.nextLine();
                        String id = UUID.randomUUID().toString();
                        System.out.print("fields (key=value,comma-separated): ");
                        String line = in.nextLine();
                        Map<String,Object> data = parseKVP(line);
                        store.updateById(coll, id, data);
                        System.out.println("Upserted.");
                    }
                    case "2" -> {
                        System.out.print("collection: "); String coll = in.nextLine();
                        System.out.print("id: "); String id = in.nextLine();
                        System.out.println(store.getById(coll, id).orElse(Map.of()));
                    }
                    case "3" -> {
                        System.out.print("collection: "); String coll = in.nextLine();
                        System.out.print("id: "); String id = in.nextLine();
                        store.deleteById(coll, id);
                        System.out.println("Deleted (if existed).");
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Unknown.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private Map<String,Object> parseKVP(String line) {
        Map<String,Object> m = new HashMap<>();
        if (line == null || line.isBlank()) return m;
        for (String pair : line.split(",")) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2) m.put(kv[0].trim(), kv[1].trim());
        }
        return m;
    }
}