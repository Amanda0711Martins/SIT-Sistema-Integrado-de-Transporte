FROM openjdk:11-jdk-slim as build

WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências
RUN ./mvnw dependency:go-offline -B

# Copiar código-fonte
COPY src src

# Construir o aplicativo
RUN ./mvnw package -DskipTests

FROM openjdk:11-jre-slim

WORKDIR /app

# Copiar o JAR construído da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configurar variáveis de ambiente para Java
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expor a porta que o aplicativo escuta
EXPOSE 8082

# Definir o ponto de entrada para iniciar o aplicativo
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]