# Highly Dependable Systems - Bulletin Board System

A fault tolerant bulletin board built for the HDS course @ IST.

## Dependencies
In order to run the project you'll need:
- Java > 1.8
- Maven
- Docker and/or Tomcat > 9.0

## Building and running

### Server

To compile and bundle the web server run:
```
mvn install
```

This will generate a `bbs.war` file in the target dir that you can deploy in Tomcat. If you don't have Tomcat installed and configured though you can run the web server with Docker, which copies the resources from the target dir and runs it in a Tomcat server. To run and build the Dockerfile:
```
docker build --tag bbs . && docker run --rm -p 8080:8080 bbs
```

By default it will run in port 8080 (feel free to map it to another port with the docker -p options) under the path /bbs. Example:
```
jgantunes@BRUTOZORD ~/I/hds-bbs> curl -X POST -H "Content-Type: application/json" "http://localhost:8080/bbs/users" -d '{"pubKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxiUrH00bdLboTGwtsYcMTscHDLyGwEsHHWxEtzxUC/VzFNC4N0SOap14TDtI9kgBYaoLmnti1CZ35KMetUyXVJp4A4O15sir3e0uxWSyErhPQ9X/2e3AvIGfmhPMiOC6zmMnZfcSjXcAKCaRCDx6C3MhFaHtC8MCLiBcJO09nBYUK7B1te1MDwYq5YAhoFgjDFlb6GKMSMRT2MsK5VYDT9srSlq5e94RlF1hTOhNyhLjuWfxuVxK5okmaoQUcDsHOYXmJPU9t4VE+djz946vM4sVOzq3hxFHCrI/jPyrFNJ0jRFvQk3lgpE9+muDsrbW/3r/XjCswlW7mhmiHGgSrQIDAQAB", "id": "4a74ec5e2108a415c5ec3e4f6e3f5962b212f5e42f34b763e2d97e7fe3ec70fe"}'

```

### Client

The client lib under bbs.client package has all the necessary resources to interact with the API, specifically the `Operations` class. For commodity, we've packaged a small CLI (bbs.client.Client) based utility that can be used to load user keys and interact with the BBS service of your choice.

To compile the source code and generate a JAR under the `target` dir:
```
mvn install -Dp.type=jar
```

To run the CLI utility and interact with the BBS service:
```
mvn exec:java -Dexec.mainClass="bbs.client.Client" -Dexec.args="<YOUR-PRIVATE-PEM-FORMATTED-KEY> <YOUR-PRIVATE-PEM-FORMATTED-KEY> <BBS-SERVER-1>,<BBS-SERVER-2>,..."
```

The last argument is a comma separated list (or just a single one) of the URLs where the BBS service is running.

Example:
```
mvn exec:java -Dexec.mainClass="bbs.client.Client" -Dexec.args="./examples/private_key.pem ./examples/public_key.pem \"http://localhost:8080/bbs\""
```

There's a set of example PEM encoded RSA keys under the `examples` dir which you can use for tests (for obvious reasons don't use it for anything other then tests and examples). The `README.md` under `examples` also has instructions on how to create RSA keys to use with the BBS system using `openssl`.
