FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/ids-0.0.1-SNAPSHOT.jar ids.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ids.jar"]