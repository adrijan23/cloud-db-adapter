package rs.uns.ftn.clouddbadapter.store.dynamo;

import rs.uns.ftn.clouddbadapter.store.BaseAdapter;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Core DynamoDB adapter implementing the BaseAdapter low-level hooks.
 * Assumes each collection maps to a table with primary key "id" (String).
 */
public final class DynamoDbAdapter extends BaseAdapter {

    private final DynamoDbClient ddb;

    /**
     * Creates a client using default credentials and region. If the environment
     * var AWS_DYNAMO_ENDPOINT is set (e.g. http://localhost:8000), uses it.
     */
    public static DynamoDbClient defaultClient() {
//        String endpoint = System.getenv("AWS_DYNAMO_ENDPOINT");
        String accessKey = System.getenv().getOrDefault("AWS_ACCESS_KEY_ID", "dummy");
        String secretKey = System.getenv().getOrDefault("AWS_SECRET_ACCESS_KEY", "dummy");

        DynamoDbClientBuilder b = DynamoDbClient.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "us-east-1")));
//        if (endpoint != null && !endpoint.isBlank()) {
//            b = b.endpointOverride(URI.create(endpoint));
//        }
        return b.build();
    }

    public DynamoDbAdapter() {
        this(defaultClient());
    }

    public DynamoDbAdapter(DynamoDbClient client) {
        this.ddb = client;
    }

    @Override
    protected void doCreate(String collection, String id, Map<String, Object> data) {
        Map<String, AttributeValue> item = DynamoMapper.toAttributes(id, data);
        PutItemRequest req = PutItemRequest.builder()
                .tableName(collection)
                .item(item)
                // ensure we error if id already exists
                .conditionExpression("attribute_not_exists(#id)")
                .expressionAttributeNames(Map.of("#id", "id"))
                .build();
        try {
            ddb.putItem(req);
        } catch (ConditionalCheckFailedException e) {
            throw new AlreadyExists("Document exists: " + id);
        } catch (DynamoDbException e) {
            throw new StoreException("Dynamo create failed", e);
        }
    }

    @Override
    protected Optional<Map<String, Object>> doGet(String collection, String id) {
        GetItemRequest req = GetItemRequest.builder()
                .tableName(collection)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        try {
            GetItemResponse resp = ddb.getItem(req);
            if (resp.item() == null || resp.item().isEmpty()) return Optional.empty();
            return Optional.of(DynamoMapper.fromAttributes(resp.item()));
        } catch (DynamoDbException e) {
            throw new StoreException("Dynamo get failed", e);
        }
    }

    @Override
    protected void doUpdate(String collection, String id, Map<String, Object> data) {
        Map<String, AttributeValue> item = DynamoMapper.toAttributes(id, data);
        PutItemRequest req = PutItemRequest.builder()
                .tableName(collection)
                .item(item) // full overwrite (upsert)
                .build();
        try {
            ddb.putItem(req);
        } catch (DynamoDbException e) {
            throw new StoreException("Dynamo update failed", e);
        }
    }

    @Override
    protected void doDelete(String collection, String id) {
        DeleteItemRequest req = DeleteItemRequest.builder()
                .tableName(collection)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();
        try {
            ddb.deleteItem(req);
        } catch (DynamoDbException e) {
            throw new StoreException("Dynamo delete failed", e);
        }
    }
}
