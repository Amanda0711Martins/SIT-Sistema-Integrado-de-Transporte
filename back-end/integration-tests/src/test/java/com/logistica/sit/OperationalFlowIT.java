package com.logistica.sit;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OperationalFlowIT extends BaseIntegrationTest {

    @Test
    void testCreateCollectionOrder_forNewCustomer_shouldSucceed() {
        // --- ETAPA 1: Criar um novo cliente no customer-service ---

        // Configura o RestAssured para falar com o customer-service
        RestAssured.baseURI = "http://" + environment.getServiceHost("customer", 8081);
        RestAssured.port = environment.getServicePort("customer", 8081);

        // Cria o corpo da requisição para o novo cliente
        Map<String, Object> newCustomer = Map.of(
            "name", "Cliente de Teste de Integração",
            "cnpj", "11222333000144",
            "email", "integration.test@cliente.com",
            "phone", "11987654321",
            "address", "Rua da Integração, 123"
        );

        // Faz a chamada POST para criar o cliente e extrai o ID da resposta
        Long customerId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(newCustomer)
        .when()
            .post("/api/v1/customers")
        .then()
            .statusCode(201) // Verifica se o cliente foi criado com sucesso
            .body("id", notNullValue())
            .extract()
            .path("id");

        System.out.println("Cliente criado com sucesso. ID: " + customerId);

        // --- ETAPA 2: Criar um pedido de coleta no operational-service ---

        // Configura o RestAssured para falar com o operational-service
        RestAssured.baseURI = "http://" + environment.getServiceHost("operational", 8084);
        RestAssured.port = environment.getServicePort("operational", 8084);

        // Cria o corpo da requisição para o novo pedido de coleta, usando o ID do cliente criado
        Map<String, Object> newCollectionOrder = Map.of(
            "customerId", customerId,
            "originAddress", "Ponto de Coleta A",
            "destinationAddress", "Ponto de Entrega B",
            "goodsDescription", "Material de Teste"
        );

        // Faz a chamada POST para criar o pedido
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(newCollectionOrder)
        .when()
            .post("/api/v1/collection-orders")
        .then()
            .statusCode(201) // Verifica se o pedido foi criado com sucesso
            .body("orderNumber", notNullValue())
            .body("customerId", equalTo(customerId.intValue()))
            // A asserção mais importante: verifica se o nome do cliente foi preenchido!
            // Isto prova que a comunicação entre os serviços funcionou.
            .body("customerName", equalTo("Cliente de Teste de Integração"));

        System.out.println("Pedido de coleta criado com sucesso e enriquecido com os dados do cliente!");
    }
}
