# Etapa de build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/franchise-api-1.0.0.jar app.jar
COPY src/main/resources/application-docker.yaml ./application-docker.yaml
EXPOSE 8080
ENV SPRING_CONFIG_LOCATION=classpath:/application.yml,classpath:/application-docker.yaml
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","app.jar","--spring.config.additional-location=application-docker.yaml"] 