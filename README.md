# portal-activity
[![Build Status](https://server.stijnhooft.be/jenkins/buildStatus/icon?job=portal-activity/master)](https://server.stijnhooft.be/jenkins/job/portal-activity/job/master/)

Goodbye to days of boredom, hello to days filled with fun activities!

How, you may ask? Well, in 3 simple steps:

1. When you encounter a great activity you would like to do sometimes
1. Add preconditions: how should the weather be? Does the activity only take place during only certain periods? Is there a minimum amount of participants?
1. Every week, you receive a notification with propositions of fun activities which can be done during the weekend, taking every precondition into account!

Also, you can search for fitting activities via the front-end.


## Dependencies
This service is dependent on a deployment of 
* portal-location
* portal-weather
* portal-image

## Release
### How to release
To release a module, this project makes use of the JGitflow plugin and the Dockerfile-maven-plugin.

1. Make sure all changes have been committed and pushed to Github.
1. Switch to the dev branch.
1. Make sure that the dev branch has at least all commits that were made to the master branch
1. Make sure that your Maven has been set up correctly (see below)
1. Run `mvn jgitflow:release-start -Pproduction`.
1. Run `mvn jgitflow:release-finish -Pproduction`.
1. In Github, mark the release as latest release.
1. Congratulations, you have released both a Maven and a Docker build!

More information about the JGitflow plugin can be found [here](https://gist.github.com/lemiorhan/97b4f827c08aed58a9d8).

#### Maven configuration
At the moment, releases are made on a local machine. No Jenkins job has been made (yet).
Therefore, make sure you have the following config in your Maven `settings.xml`;

````$xml
<servers>
    <server>
        <id>docker.io</id>
        <username>your_username</username>
        <password>*************</password>
    </server>
    <server>
        <id>portal-nexus-releases</id>
        <username>your_username</username>
        <password>*************</password>
    </server>
</servers>
````
* docker.io points to the Docker Hub.
* portal-nexus-releases points to my personal Nexus (see `<distributionManagement>` in the project's `pom.xml`)

### Running with Docker
When running with Docker, you need to provide required environment variables.

| Name | Example value | Description | Required? |
| ---- | ------------- | ----------- | -------- |
| POSTGRES_URL | jdbc:postgresql://localhost:5433/portal-activity | JDBC url to connect to the database | required
| POSTGRES_USERNAME | my-username | Username to log in to the database | required
| POSTGRES_PASSWORD | secret | Password to log in to the database | required
| EUREKA_SERVICE_URL | http://portal-eureka:8761/eureka | Url of Eureka | required
| JAVA_OPTS_ACTIVITY | -Xmx400m -Xms400m | Java opts you want to pass to the JVM | optional

