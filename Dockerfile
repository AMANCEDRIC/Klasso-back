## Stage 1 : Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
USER root
RUN mvn -f /home/app/pom.xml clean package -DskipTests

## Stage 2 : Run the application
FROM eclipse-temurin:17-jre-jammy
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
WORKDIR /work/
COPY --from=build /home/app/target/quarkus-app/ /work/

EXPOSE 8081
USER 1001

CMD ["java", "-jar", "/work/quarkus-run.jar"]
