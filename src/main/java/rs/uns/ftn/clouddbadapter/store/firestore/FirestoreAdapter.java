package rs.uns.ftn.clouddbadapter.store.firestore;

import rs.uns.ftn.clouddbadapter.store.BaseAdapter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import java.util.Map;
import java.util.Optional;

/**
 * Core Firestore adapter.
 * Each collection maps to a Firestore collection.
 * Documents are stored and retrieved as Map<String,Object>.
 */
public final class FirestoreAdapter extends BaseAdapter {

    private final Firestore db;

    /**
     * Creates a Firestore client using GOOGLE_APPLICATION_CREDENTIALS env var.
     */
    public FirestoreAdapter() {
        this.db = FirestoreOptions.getDefaultInstance().getService();
    }

    public FirestoreAdapter(Firestore db) {
        this.db = db;
    }

    @Override
    protected void doCreate(String collection, String id, Map<String, Object> data) {
        // Firestore create() fails if document exists
        ApiFuture<WriteResult> future = db.collection(collection).document(id).create(data);
        try {
            future.get();
        } catch (Exception e) {
            // if exists → fallback to AlreadyExists to match BaseAdapter contract
            if (e.getCause() != null && e.getCause().getMessage() != null &&
                    e.getCause().getMessage().contains("Already exists")) {
                throw new AlreadyExists("Document exists: " + id);
            }
            throw new StoreException("Firestore create failed", e);
        }
    }

    @Override
    protected Optional<Map<String, Object>> doGet(String collection, String id) {
        try {
            DocumentSnapshot snap = db.collection(collection).document(id).get().get();
            if (!snap.exists()) return Optional.empty();
            return Optional.of(snap.getData());
        } catch (Exception e) {
            throw new StoreException("Firestore get failed", e);
        }
    }

    @Override
    protected void doUpdate(String collection, String id, Map<String, Object> data) {
        try {
            db.collection(collection).document(id).set(data).get(); // full overwrite (upsert)
        } catch (Exception e) {
            throw new StoreException("Firestore update failed", e);
        }
    }

    @Override
    protected void doDelete(String collection, String id) {
        try {
            db.collection(collection).document(id).delete().get();
        } catch (Exception e) {
            throw new StoreException("Firestore delete failed", e);
        }
    }
}
