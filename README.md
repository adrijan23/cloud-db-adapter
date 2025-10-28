# 🌩️ Cloud DB Adapter

### 🎓 University Project – Software Engineering (FTN)

**Cloud DB Adapter** is a universal interface for working with multiple cloud NoSQL databases through a single unified API.  
It enables basic CRUD operations across three major cloud providers:

- 🟢 **AWS DynamoDB**  
- 🔵 **Google Firestore**  
- 🟣 **Azure Cosmos DB (Core/SQL API)**  

In addition to the basic adapter layer, the project includes a **mini ORM** that uses annotations (`@Entity`, `@Id`) to map Java objects to database documents.

---

## 📂 Project Structure

```
rs.uns.ftn.clouddbadapter
├── core/                    # Factory + provider selection
├── demo/                    # ConsoleApp + User entity (demo)
├── entity/                  # @Entity, @Id, EntityMapper
├── orm/                     # SimpleEntityManager (mini ORM)
└── store/                   # BaseAdapter + provider adapters
    ├── dynamo/              # AWS implementation
    ├── firestore/           # GCP implementation
    └── cosmos/              # Azure implementation
```

---

## ⚙️ Environment Configuration

All required cloud provider data is configured through **environment variables**.  
Current example `.env` file:

```bash
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=us-east-1

AZURE_COSMOS_ENDPOINT=https://clouddb-adapter-azure.documents.azure.com:443/
AZURE_COSMOS_KEY=your-azure-key
COSMOS_DB=appdb

GOOGLE_APPLICATION_CREDENTIALS=C:\Users\AdrijanRadjevic\Downloads\cloud-db-adapter-demo-b64277286354.json

CLOUD_PROVIDER=azure
```

> 💡 Change `CLOUD_PROVIDER` to `aws`, `gcp`, or `azure` to use the corresponding adapter.

---

## 🚀 Setup by Provider

### 🟢 AWS DynamoDB

1. Go to [AWS Console → IAM → Users → Security credentials → Create access key](https://console.aws.amazon.com/iam/home#/users)
2. Select **"Local code"** as the use case
3. Set the following environment variables:

```bash
export CLOUD_PROVIDER=aws
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1
```

4. Create a DynamoDB table (e.g., `users`) with primary key `id` (String)

### 🔵 Google Firestore

1. Go to [Firebase Console → Firestore → Create Database](https://console.firebase.google.com/)
2. Select **Standard mode**
3. Choose region: `us` (e.g., multi-region nam5)
4. Security rule: Open (for testing)
5. In IAM & Admin → Service Accounts → Create Key → JSON, download the key
6. Set environment variables:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your-key.json
export CLOUD_PROVIDER=gcp
```

7. Firestore automatically creates collections on first insert

### 🟣 Azure Cosmos DB

1. In Azure Portal, go to Create Resource → Azure Cosmos DB for NoSQL
2. After creating the database:
   - Go to the Keys section and copy the PRIMARY CONNECTION STRING and KEY
   - Note the database name (e.g., `appdb`)
3. Set environment variables:

```bash
export CLOUD_PROVIDER=azure
export AZURE_COSMOS_ENDPOINT=https://your-instance.documents.azure.com:443/
export AZURE_COSMOS_KEY=your-primary-key
export COSMOS_DB=appdb
```

4. Create a collection within the database (e.g., `users`)

---

## 💻 Running the Project

In IntelliJ or terminal, run:

```bash
mvn clean compile exec:java -Dexec.mainClass=rs.uns.ftn.clouddbadapter.Main
```

An interactive console menu will appear:

```
=== Cloud DB Adapter Demo ===
Choose provider:
1) AWS DynamoDB
2) Google Firestore
3) Azure Cosmos (Core/SQL)
0) Exit
Choice: 2

Main Menu
1) Manage Users (ORM)
2) Generic CRUD (RAW)
0) Back
```

---

## 🧱 Demo Options

### 🔹 ORM Mode (@Entity annotations)

Work with classes like `User`:

```java
@Entity(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
}
```

Available console options:

```
1) Create
2) Get by ID
3) Update
4) Delete
5) List
```

All CRUD operations work identically regardless of the selected provider.

### 🔹 RAW Mode

Direct access to collections without the ORM layer:

```
1) Upsert (collection, id, fields)
2) Get by ID
3) Delete
```

Example usage:

```
collection: test
id: 123
fields: name=Marko,email=marko@test.com
```

---

## 🧩 Technical Features

- ✅ Unified `DocumentStore` interface
- ✅ Implementations for AWS / GCP / Azure
- ✅ Mini ORM with `@Entity` and `@Id` annotations
- ✅ Reflective mapping via `EntityMapper`
- ✅ Provider configuration through ENV variables
- ✅ Console-based demo application

---

## 📦 Maven Dependencies

```xml
<dependencies>
    <!-- AWS SDK -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>dynamodb</artifactId>
        <version>2.25.17</version>
    </dependency>

    <!-- GCP Firestore -->
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>google-cloud-firestore</artifactId>
        <version>3.11.5</version>
    </dependency>

    <!-- Azure Cosmos -->
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-cosmos</artifactId>
        <version>4.53.0</version>
    </dependency>

    <!-- Logging (disable warnings) -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-nop</artifactId>
        <version>2.0.12</version>
    </dependency>
</dependencies>
```

---

## 🧠 Output Example (AWS)

```
=== Cloud DB Adapter Demo ===
Choose provider: 1

Users (ORM)
1) Create
...
Saved: User{id='8e97f9...', name='Ana', email='ana@example.com'}
Updated: User{id='8e97f9...', name='Ana Maric', email='ana@example.com'}

List:
User{id='8e97f9...', name='Ana Maric', email='ana@example.com'}

Deleted.
```

---

## 📘 Author

**Adrijan Radjevic**  
Faculty of Technical Sciences – Software Engineering  
Novi Sad, 2025
