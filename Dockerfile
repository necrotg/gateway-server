FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /gateway-server
COPY opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /gateway-server
COPY --from=build /gateway-server/target/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]