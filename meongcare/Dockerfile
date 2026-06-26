# Build stage
FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/meongcare-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
