package rs.uns.ftn.clouddbadapter.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a class as an entity that should be stored in a document store.
 * The `collection` attribute defines the name of the collection/container/table
 * where the entity will be saved.
 *
 * Example:
 * @Entity(collection = "users")
 * public class User { ... }
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Entity {
    String collection();
}
