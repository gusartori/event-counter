package com.message.counter;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.utility.DockerImageName.parse;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers
@ResourceLock("kafka")
class CounterApplicationCTest {

	@LocalServerPort
	private int port;

	@Container
	private static final KafkaContainer KAFKA_CONTAINER =
			new KafkaContainer(parse("confluentinc/cp-kafka").withTag("6.2.0"));

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap.servers", KAFKA_CONTAINER::getBootstrapServers);
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
		RestAssured.basePath = "/v1/messages";
	}

	@Test
	@Order(1)
	void getNumberOfMessages_noEventsProducedOrConsumed_returnsZero() throws Exception {
		given()
				.when()
				.get("/count")
				.then()
				.assertThat()
				.statusCode(200)
				.body("producedMessages", is("0"))
				.body("consumedMessages", is("0"));
		given()
				.when()
				.get("/produced/count")
				.then()
				.assertThat()
				.statusCode(200)
				.body("numberOfMessages", is("0"));
		given()
				.when()
				.get("/consumed/count")
				.then()
				.assertThat()
				.statusCode(200)
				.body("numberOfMessages", is("0"));
	}

	@Test
	@Order(2)
	void produceMessage_oneEventProduce_getMessagesReturnsOneProduceAndOneConsumed() throws Exception {
		given()
				.when()
				.get("/count")
				.then()
				.assertThat()
				.statusCode(200)
				.body("producedMessages", is("0"))
				.body("consumedMessages", is("0"));

		given()
				.when()
				.contentType(JSON)
				.body("{\n" +
						"  \"message\": \"this is a component test\"\n" +
						"}")
				.post("/produce")
				.then()
				.assertThat()
				.statusCode(200)
				.body("producedMessage", is("this is a component test"));

		//TODO need to have a think on how to avoid this
		Thread.sleep(30000);

		given()
				.when()
				.get("/count")
				.then()
				.assertThat()
				.statusCode(200)
				.body("producedMessages", is("1"))
				.body("consumedMessages", is("1"));
	}
}
