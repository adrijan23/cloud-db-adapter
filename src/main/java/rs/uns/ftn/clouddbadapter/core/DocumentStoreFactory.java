package rs.uns.ftn.clouddbadapter.core;

import rs.uns.ftn.clouddbadapter.store.DocumentStore;
import rs.uns.ftn.clouddbadapter.store.dynamo.DynamoDbAdapter;
import rs.uns.ftn.clouddbadapter.store.firestore.FirestoreAdapter;
import rs.uns.ftn.clouddbadapter.store.cosmos.CosmosAdapter;

/**
 * Chooses a concrete adapter based on CLOUD_PROVIDER env var or parameter: aws | gcp | azure.
 */
public final class DocumentStoreFactory {
    private DocumentStoreFactory() {}

    /**
     * Returns a DocumentStore instance for the provider configured via the CLOUD_PROVIDER env var (default 'aws').
     * @throws IllegalArgumentException if the provider value is unrecognized.
     */
    public static DocumentStore fromEnv() {
        String p = System.getenv().getOrDefault("CLOUD_PROVIDER", "aws");
        return from(Provider.parse(p));
    }


    /**
     * Creates a DocumentStore instance for the given provider enum value.
     * @param p the cloud provider enum value (AWS, GCP, AZURE)
     * @return concrete adapter implementing DocumentStore
     */
    public static DocumentStore from(Provider p) {
        return switch (p) {
            case AWS   -> new DynamoDbAdapter();
            case GCP   -> new FirestoreAdapter();
            case AZURE -> new CosmosAdapter();
        };
    }
}
