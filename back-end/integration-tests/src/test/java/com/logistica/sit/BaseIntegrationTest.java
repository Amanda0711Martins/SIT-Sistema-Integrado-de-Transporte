package com.logistica.sit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class BaseIntegrationTest {

    private static final File DOCKER_COMPOSE_FILE = new File("backend/docker-compose.yml");

    @Container
    public static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(DOCKER_COMPOSE_FILE)
                    // Adicionando todos os serviços à estratégia de espera para garantir que o ambiente esteja 100% pronto
                    .withExposedService("config", 8888, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("eureka", 8761, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("postgres", 5432, Wait.forListeningPort())
                    .withExposedService("user", 8080, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("customer", 8081, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("financial", 8082, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("human-resources", 8083, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))
                    .withExposedService("operational", 8084, Wait.forHttp("/actuator/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)))

    public String getServiceHost(String serviceName, int port) {
        return environment.getServiceHost(serviceName, port);
    }

    public int getServicePort(String serviceName, int port) {
        return environment.getServicePort(serviceName, port);
    }
}