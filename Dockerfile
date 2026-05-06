FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /build
COPY --from=build /build/target/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]