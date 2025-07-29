# --- Estágio 1: Build da Aplicação ---
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests


# --- Estágio 2: Imagem Final ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/operational-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
