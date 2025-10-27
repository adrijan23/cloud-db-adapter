package rs.uns.ftn.clouddbadapter.core;

import rs.uns.ftn.clouddbadapter.store.DocumentStore;
import rs.uns.ftn.clouddbadapter.store.dynamo.DynamoDbAdapter;
import rs.uns.ftn.clouddbadapter.store.firestore.FirestoreAdapter;
import rs.uns.ftn.clouddbadapter.store.cosmos.CosmosAdapter;

/**
 * Chooses a concrete adapter based on CLOUD_PROVIDER env var: aws | gcp | azure.
 */
public final class DocumentStoreFactory {
    private DocumentStoreFactory() {}

    public static DocumentStore fromEnv() {
        String p = System.getenv().getOrDefault("CLOUD_PROVIDER", "aws").toLowerCase();
        return switch (p) {
            case "aws"   -> new DynamoDbAdapter();
            case "gcp"   -> new FirestoreAdapter();
            case "azure" -> new CosmosAdapter();
            default -> throw new IllegalArgumentException("Unknown CLOUD_PROVIDER: " + p);
        };
    }
}
