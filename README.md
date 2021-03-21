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

**NOTE**

Neo4j requires `apoc` core plugins to be installed.

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
    -jar target/fos-svc.jar
```

If building the UI, you must use the correct Maven profile:

```$bash
mvn -Pwith-ui clean package
```

This will pull in the UI code under `./fos-ui` and build it into the final
JAR.

### Docker

Running `docker-compose up` will start up the service as well as Mongo and Neo4j
ready to test. Note that you _must_ have built the source under `./fos-ui` before
the frontend is served.

### Administration

By default, the application will try to create the node indices on startup. See
the `Neo4JConfig.class` file for further info.

Neo4j index creation (for reference):

```
CALL db.index.fulltext.createNodeIndex("clients-fts", ["Client"], ["name", "postCode"], {analyzer: "english"})
CALL db.index.fulltext.createNodeIndex("orgs-fts", ["Organisation"], ["name", "id"], {analyzer: "english"})
# clear via CALL db.index.fulltext.drop("clients-fts")
# clear via CALL db.index.fulltext.drop("orgs-fts")
```

Verify:
```
SHOW INDEXES
```
