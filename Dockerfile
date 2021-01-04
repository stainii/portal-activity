FROM adoptopenjdk:14-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:14-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT exec java ${JAVA_OPTS_ACTIVITY} org.springframework.boot.loader.JarLauncher \
 --spring.data.mongodb.host=${DATABASE_HOST_ACTIVITY} \
 --spring.data.mongodb.port=${DATABASE_PORT_ACTIVITY} \
 --spring.data.mongodb.username=${DATABASE_USERNAME_ACTIVITY} \
 --spring.data.mongodb.password=${DATABASE_PASSWORD_ACTIVITY} \
 --spring.data.mongodb.database=${DATABASE_NAME_ACTIVITY} \
 --eureka.client.service-url.defaultZone=${EUREKA_SERVICE_URL}