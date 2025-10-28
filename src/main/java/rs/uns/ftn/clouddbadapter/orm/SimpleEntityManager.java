package rs.uns.ftn.clouddbadapter.orm;

import rs.uns.ftn.clouddbadapter.entity.EntityMapper;
import rs.uns.ftn.clouddbadapter.store.DocumentStore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tiny ORM-like implementation backed by the generic DocumentStore.
 * Uses @Entity(collection=...) and @Id on fields. Generates UUID if @Id is missing.
 */
public final class SimpleEntityManager implements EntityManager {

    private final DocumentStore store;

    public SimpleEntityManager(DocumentStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    @Override
    public <T> T save(T entity) {
        Objects.requireNonNull(entity, "entity");
        Class<?> type = entity.getClass();

        // resolve collection and id
        String collection = EntityMapper.collection(type);
        String id = EntityMapper.idValue(entity);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
            EntityMapper.setId(entity, id);
        }

        // convert POJO -> Map and persist
        Map<String, Object> data = EntityMapper.toMap(entity);
        store.updateById(collection, id, data); // full overwrite (upsert)

        return entity; // entity now has id set
    }

    @Override
    public <T> Optional<T> find(Class<T> type, String id) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(id, "id");
        String collection = EntityMapper.collection(type);
        return store.getById(collection, id).map(m -> EntityMapper.fromMap(type, m));
    }

    @Override
    public <T> void delete(Class<T> type, String id) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(id, "id");
        String collection = EntityMapper.collection(type);
        store.deleteById(collection, id);
    }

    @Override
    public <T> List<T> list(Class<T> type, int limit) {
        Objects.requireNonNull(type, "type");
        String collection = EntityMapper.collection(type);
        return store.list(collection, limit).stream()
                .map(m -> EntityMapper.fromMap(type, m))
                .collect(Collectors.toList());
    }
}
