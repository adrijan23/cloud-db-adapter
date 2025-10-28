package rs.uns.ftn.clouddbadapter.store;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Core provider-neutral CRUD interface.
 * Cloud adapters (AWS/Azure/GCP) will implement this.
 */
public interface DocumentStore {

    void create(String collection, String id, Map<String, Object> data);

    Optional<Map<String, Object>> getById(String collection, String id);

    void updateById(String collection, String id, Map<String, Object> data);

    void deleteById(String collection, String id);

    List<Map<String, Object>> list(String collection, int limit);
}
