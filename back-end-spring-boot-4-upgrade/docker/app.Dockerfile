ARG BASE_IMAGE
FROM ${BASE_IMAGE}

COPY target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
