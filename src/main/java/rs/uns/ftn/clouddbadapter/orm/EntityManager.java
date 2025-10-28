package rs.uns.ftn.clouddbadapter.orm;

import java.util.List;
import java.util.Optional;

/**
 * Minimal ORM-like facade over the raw DocumentStore.
 * Works with annotated POJOs instead of Map<String,Object>.
 */
public interface EntityManager {
    /**
     * Saves (creates or updates) the given entity.
     * If @Id is null/empty, an ID will be generated.
     * Returns the (possibly mutated) entity with ID set.
     */
    <T> T save(T entity);

    /**
     * Loads an entity by id, or Optional.empty() if not found.
     */
    <T> Optional<T> find(Class<T> type, String id);

    /**
     * Deletes an entity by id (no-op if not found).
     */
    <T> void delete(Class<T> type, String id);

    /**
     * Returns up to 'limit' entities from the collection.
     */
    <T> List<T> list(Class<T> type, int limit);
}
