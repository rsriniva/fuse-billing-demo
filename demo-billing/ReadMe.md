# Telecom Billing Fuse Project

## Instructions
To build this project use

    mvn clean install

Next you wil need a Fuse server running with fabric8 running. After installing the Fuse server, add a user to the
`etc/users.properties`. Once that is complete, you start the server from `bin/fuse` and create the fabric with
`fabric:create --clean --wait-for-provisioning`. Once that is complete, you can deploy the bundle to the server.

In order to deploy to fabric, you will need to setup a server in your ~/.m2/settings.xml

    <server>
      <id>fabric8.local</id>
      <username>admin</username>
      <password>admin</password>
    </server>

To deploy to a local Fuse server

    mvn -Dfabric8.serverId=fabric8.local fabric8:deploy

## Project Properties
fabric8.serverId - This tells which server to use from the settings.xml.
jolokia.url - This is the Fabric url to deploy to. For local servers, this should be `http://localhost:8181/jolokia`

## Project Components
This project is based on the Camel Blueprint archetype. The Camel routes have been moved from the blueprint.xml into a
`RouteBuilder` class to make development and testing easier.

There are multiple routes within the `RouteBuilder`:
- genData - This is a timer that generates data into the src/data directory for processing.
- processData - This pulls from the src/data directory, converts the data, calculates the charge, and sends to be persisted.
- persistDB - This inserts the data into the database.
