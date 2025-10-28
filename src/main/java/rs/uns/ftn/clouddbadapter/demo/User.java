package rs.uns.ftn.clouddbadapter.demo;

import rs.uns.ftn.clouddbadapter.entity.Entity;
import rs.uns.ftn.clouddbadapter.entity.Id;

/**
 * Minimal example entity to demonstrate the ORM layer.
 */
@Entity(collection = "Users")
public class User {
    @Id
    private String id;

    private String name;
    private String email;

    // Getters/Setters (or make fields public for brevity)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public User setName(String name) { this.name = name; return this; }

    public String getEmail() { return email; }
    public User setEmail(String email) { this.email = email; return this; }

    @Override public String toString() {
        return "User{id='%s', name='%s', email='%s'}".formatted(id, name, email);
    }
}
