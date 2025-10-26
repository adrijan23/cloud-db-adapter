package rs.uns.ftn.clouddbadapter.store;

import java.util.Map;
import java.util.Optional;

/**
 * Template class that enforces validation and provides a unified upsert logic.
 * Concrete cloud adapters only need to implement the low-level doX() methods.
 */
public abstract class BaseAdapter implements DocumentStore {

    @Override
    public final void create(String c, String id, Map<String, Object> d) {
        validate(c, id, d);
        try {
            doCreate(c, id, d);
        } catch (AlreadyExists e) {
            doUpdate(c, id, d);
        }
    }

    @Override
    public final Optional<Map<String, Object>> getById(String c, String id) {
        validate(c, id);
        return doGet(c, id);
    }

    @Override
    public final void updateById(String c, String id, Map<String, Object> d) {
        validate(c, id, d);
        doUpdate(c, id, d);
    }

    @Override
    public final void deleteById(String c, String id) {
        validate(c, id);
        doDelete(c, id);
    }

    // ==== methods cloud adapters implement ====
    protected abstract void doCreate(String c, String id, Map<String,Object> d);
    protected abstract Optional<Map<String,Object>> doGet(String c, String id);
    protected abstract void doUpdate(String c, String id, Map<String,Object> d);
    protected abstract void doDelete(String c, String id);

    // ==== shared validation ====
    protected void validate(String c, String id) {
        if (c == null || c.isBlank()) throw new IllegalArgumentException("collection empty");
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id empty");
    }
    protected void validate(String c, String id, Map<String,Object> d) {
        validate(c, id);
        if (d == null) throw new IllegalArgumentException("data null");
    }

    // ==== unified exception model ====
    public static class StoreException extends RuntimeException {
        public StoreException(String m, Throwable t) { super(m, t); }
        public StoreException(String m) { super(m); }
    }
    public static class AlreadyExists extends StoreException {
        public AlreadyExists(String m) { super(m); }
    }
}
