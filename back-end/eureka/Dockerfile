# --- Estágio 1: Build da Aplicação Eureka ---
# Usa uma imagem com JDK e Maven para compilar o projeto
FROM openjdk:17-jdk-slim AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do Maven Wrapper para o contêiner
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Baixa as dependências do projeto para aproveitar o cache do Docker
RUN ./mvnw dependency:go-offline

# Copia o código-fonte do projeto
COPY src ./src

# Compila e empacota a aplicação, pulando os testes
RUN ./mvnw package -DskipTests


# --- Estágio 2: Imagem Final ---
# Usa uma imagem base leve, apenas com o Java Runtime
FROM eclipse-temurin:17-jre-alpine

# CORREÇÃO: Usa o gerenciador de pacotes 'apk' do Alpine para instalar o curl
# A flag '--no-cache' atualiza, instala e limpa em um único passo.
RUN apk add --no-cache curl

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o arquivo JAR gerado no estágio de build para a imagem final
COPY --from=builder /app/target/eureka-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta do Eureka Server
EXPOSE 8761

# Comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java", "-jar", "app.jar"]
