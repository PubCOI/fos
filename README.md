## Fos backend

### Quickstart

For running locally, you will need to create a `.properties` file under `./config`. I 
tend to name mine `application-local.properties`: you can then use the Spring `local`
profile on start.

You will need to be running a local Neo4j instance. The Community version is perfectly
sufficient for our purposes.

**Dependencies**

Note that the project requires a couple of external dependencies that have been
packaged separately. You'll need to clone these and run the `mvn clean install` on
each dependency (if pointing to SNAPSHOT).

I'll eventually publish ‘final’ versions to Maven Central.

**Mandatory params**

- `spring.neo4j.authentication.username` (no default)
- `spring.neo4j.authentication.password` (no default)
- `spring.neo4j.uri` (defaults to `bolt://127.0.0.1:7687`)
- `spring.data.neo4j.database` (defaults to `neo4j`)

Once you've specified all your parameters, you should be able to build your project via
Maven:

```$bash
mvn clean package
```

Then to run:

```$bash
java -Dspring.profiles.active=<your_profile_name> \
    -jar target/fos-svc-<version>.jar
```

### Administration

Neo4j index creation:

```
CALL db.index.fulltext.createNodeIndex("clients-fts", ["Client"], ["name", "postCode"], {analyzer: "english"})

# clear via CALL db.index.fulltext.drop("clients-fts")
```

Verify:

```
SHOW INDEXES
```

Development only: clearing schemas -

```
CALL apoc.schema.assert({},{})
```