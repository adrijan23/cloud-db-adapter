package rs.uns.ftn.clouddbadapter.store.cosmos;

import rs.uns.ftn.clouddbadapter.store.BaseAdapter;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.implementation.NotFoundException;

import java.util.Map;
import java.util.Optional;

/**
 * Core Cosmos DB adapter (Core/SQL API).
 * Assumes one Cosmos "container" per collection, with partition key '/id'.
 * Stores documents as Map<String,Object>.
 */
public final class CosmosAdapter extends BaseAdapter {

    private final CosmosClient client;
    private final String databaseName;

    /**
     * Creates a Cosmos client from ENV:
     *  - AZURE_COSMOS_ENDPOINT (e.g. https://<account>.documents.azure.com:443/)
     *  - AZURE_COSMOS_KEY
     *  - COSMOS_DB (database name; default: "appdb")
     *
     * For local emulator:
     *  - AZURE_COSMOS_ENDPOINT=https://localhost:8081/
     *  - AZURE_COSMOS_KEY=the emulator master key
     *  NOTE: import emulator SSL cert into JVM truststore if needed.
     */
    public CosmosAdapter() {
        String endpoint = System.getenv("AZURE_COSMOS_ENDPOINT");
        String key = System.getenv("AZURE_COSMOS_KEY");
        if (endpoint == null || endpoint.isBlank() || key == null || key.isBlank()) {
            throw new IllegalArgumentException("AZURE_COSMOS_ENDPOINT and AZURE_COSMOS_KEY must be set");
        }
        this.databaseName = System.getenv().getOrDefault("COSMOS_DB", "appdb");

        // Use Gateway mode for simplest connectivity (esp. with emulator)
        CosmosClientBuilder builder = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .gatewayMode();

        this.client = builder.buildClient();
    }

    public CosmosAdapter(CosmosClient client, String databaseName) {
        this.client = client;
        this.databaseName = databaseName;
    }

    private CosmosContainer container(String collection) {
        return client.getDatabase(databaseName).getContainer(collection);
    }

    @Override
    protected void doCreate(String collection, String id, Map<String, Object> data) {
        Map<String, Object> payload = new java.util.HashMap<>(data); // copy
        payload.put("id", id); // ensure PK is present
        try {
            container(collection).createItem(payload, new PartitionKey(id), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            if (e.getStatusCode() == 409) throw new AlreadyExists("Document exists: " + id);
            throw new StoreException("Cosmos create failed", e);
        }
    }

    @Override
    protected Optional<Map<String, Object>> doGet(String collection, String id) {
        try {
            var resp = container(collection)
                    .readItem(id, new PartitionKey(id), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> item = (Map<String, Object>) resp.getItem();
            return Optional.of(item);
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) return Optional.empty();
            throw new StoreException("Cosmos get failed", e);
        }
    }

    @Override
    protected void doUpdate(String collection, String id, Map<String, Object> data) {
        Map<String, Object> payload = new java.util.HashMap<>(data); // copy
        payload.put("id", id);
        try {
            container(collection).upsertItem(payload, new PartitionKey(id), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            throw new StoreException("Cosmos update failed", e);
        }
    }

    @Override
    protected void doDelete(String collection, String id) {
        try {
            container(collection).deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) return; // no-op
            throw new StoreException("Cosmos delete failed", e);
        }
    }
}
