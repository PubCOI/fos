## Fos backend

### Quickstart

For running locally, you will need to create a `.properties` file under `./config`. I 
tend to name mine `application-local.properties`: you can then use the Spring `local`
profile on start.

You will need:

- Elasticsearch 7.x running somewhere
  - Ensure it's listening globally
  - Elasticsearch attachments processor installed (ingest-attachment)
- Mariadb running somewhere
  - Again ensure it's listening globally
- non-docker install of neo4j (preferred ... but a docker config is provided) - community version

You will need to be running a local Neo4j instance. Fos is developed to run on the 
Community edition of Neo4j.

**NOTE**

Neo4j requires `apoc` core plugins to be installed. If you're using the dockerised
setup (further down), the apoc plugins will be pulled in automatically.

**Mandatory params**

- `spring.neo4j.authentication.username` (no default)
- `spring.neo4j.authentication.password` (no default)
- `spring.neo4j.uri` (defaults to `bolt://127.0.0.1:7687`)
- `spring.data.neo4j.database` (defaults to `neo4j`)

### Building

```$bash
mvn clean package
```

If building the UI, you must use the correct Maven profile:

```$bash
mvn -Pwith-ui clean package
```

This will pull in the UI code under `./fos-ui` and build it into the final
JAR. Note this is not required for the Dockerised version (below).

### Running

```$bash
java -Dspring.profiles.active=<your_profile_name> \
    -jar target/fos-svc.jar
```

### Docker

Running `docker-compose up` will start up the service as well as Mongo and Neo4j
ready to test. Note that you _must_ have built the source under `./fos-ui` before
the frontend is served.

### GraphDB

By default, the application will try to create the node indices on startup. See
the `Neo4JConfig.class` file for further info.

### Additional data

Elastic currently houses two sets of data for us: attachments (scanned / OCRd), and members
interests (Lords and MPs).

#### Attachments data

Note that the attachments processor is NOT publicly available yet as it does some funky stuff
that I don't want to break if it's reverse-engineered by anyone who - let's say - would like it
to stop working.

I am, however, happy to share it with those who have altruistic intentions: please send your 
request via email to the project, remembering to include your GitHub username or some other form
of 'verification' so that I know who you are.

#### Members interests

Members interests are loaded onto the system in two stages. First we load individual interests
(from Lords and Commons) onto Mongo via some initial cleanup. Then we load each 'set' of interests
onto ES to provide us with FTS. Yes I know I can do FTS on Mongo but I prefer Lucene, sorry...

**MPs** (via TWFY):

```
## don't judge my regexes, ok?
mkdir /tmp/twfy-data && cd $_
curl -o - "https://www.theyworkforyou.com/pwdata/scrapedxml/regmem/" | \
  sed -E 's/.+(regmem20(15|16|17|18|19|20|21|22)-[0-9]{2}-[0-9]{2}.xml).+/\1/g' | \
  grep regmem | sort | uniq | \
  xargs -I{} sh -c 'echo "downloading {}" && curl -s -O "https://www.theyworkforyou.com/pwdata/scrapedxml/regmem/{}"'

# then (on mac at least)
noglob find . -name *.xml -exec gsed -i 's/encoding="ISO-8859-1/encoding="UTF-8/g' {} \;
# or less posixy (read: picky) boxes
set -f && find . -name *.xml -exec sed -i 's/encoding="ISO-8859-1/encoding="UTF-8/g' {} \;

# yes I know I could have done all these in one line but readability > coolness
for file in `find . -type f -name *.xml` ; do
    iconv -f ISO-8859-1 -t UTF-8 "$file" > "$file".utf && mv "$file".utf "$file"
done

# now post ...
# note that the api token must be set under application.properties (fos.api.key)
for file in `find . -type f -name *.xml` ; do curl \
    "http://127.0.0.1:8084/api/admin/interests/upload?dataset=$file" -XPOST -d@"$file" \
    -v -H "Content-Type: application/xml" -H "admin-api-key: <token>" ; done
```