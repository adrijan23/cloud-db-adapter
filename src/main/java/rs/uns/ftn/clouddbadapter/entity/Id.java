package rs.uns.ftn.clouddbadapter.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks the field that represents the primary identifier of the entity.
 * The value of this field will be used as the document key in the storage system.
 *
 * Example:
 * @Id
 * private String id;
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {}
