package br.com.provensi.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.provensi.configs.TestsConfigs;
import br.com.provensi.data.vo.v1.security.AccountCredentialsVO;
import br.com.provensi.data.vo.v1.security.TokenVO;
import br.com.provensi.integrationtests.controller.withyaml.mapper.YamlMapper;
import br.com.provensi.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.provensi.integrationtests.vo.PersonVO;
import br.com.provensi.integrationtests.vo.pagedmodels.PagedModelPerson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YamlMapper objectMapper;

	private static PersonVO person;

	@BeforeAll
	public static void setUp() {
		objectMapper = new YamlMapper();

		person = new PersonVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		String accessToken = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.basePath("/auth/signin")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.body(user, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenVO.class, objectMapper)
				.getAccessToken();

		specification = new RequestSpecBuilder()
				.addHeader(TestsConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1/")
				.setPort(TestsConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		var persistedPerson = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.body(person, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasilia", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		var persistedPerson = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_PROVENSI)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasilia", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");

		var persistedPerson = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.body(person, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasilia", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		mockPerson();

		var persistedPerson = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_PROVENSI)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonVO.class, objectMapper);

		person = persistedPerson;

		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasilia", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var wrapper = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
//				.as(new TypeRef<List<PersonVO>>() {
//				});
				.body()
				.as(PagedModelPerson.class, objectMapper);

		List<PersonVO> people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);

		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());

		assertEquals(668, foundPersonOne.getId());

		assertEquals("Alic", foundPersonOne.getFirstName());
		assertEquals("Terbrug", foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());

		PersonVO foundPersonFive = people.get(5);
		
		assertNotNull(foundPersonFive.getId());
		assertNotNull(foundPersonFive.getLastName());
		assertNotNull(foundPersonFive.getAddress());
		assertNotNull(foundPersonFive.getGender());
		
		assertEquals(902, foundPersonFive.getId());
		
		assertEquals("Allegra", foundPersonFive.getFirstName());
		assertEquals("Dome", foundPersonFive.getLastName());
		assertEquals("57 Roxbury Pass", foundPersonFive.getAddress());
		assertEquals("Female", foundPersonFive.getGender());
	}

	@Test
	@Order(7)
	public void testFindByFirstName() throws JsonMappingException, JsonProcessingException {
		var wrapper = given()
				.spec(specification)
				.config(
						RestAssuredConfig
						.config()
						.encoderConfig(
								EncoderConfig
								.encoderConfig()
								.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.pathParam("firstName", "adrea")
				.queryParams("page", 0, "size", 10, "direction", "asc")
				.when()
				.get("findsPeopleByFirstName/{firstName}")
				.then()
				.statusCode(200)
				.extract()
//				.as(new TypeRef<List<PersonVO>>() {
//				});
				.body()
				.as(PagedModelPerson.class, objectMapper);
		
		List<PersonVO> people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		
		assertTrue(foundPersonOne.getEnabled());
		
		assertEquals(583, foundPersonOne.getId());
		
		assertEquals("Adrea", foundPersonOne.getFirstName());
		assertEquals("De Lorenzo", foundPersonOne.getLastName());
		assertEquals("2274 Ohio Terrace", foundPersonOne.getAddress());
		assertEquals("Female", foundPersonOne.getGender());
	}

	@Test
	@Order(8)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var content = given()
				.spec(specification)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 3, "size", 10, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
//				.as(new TypeRef<List<PersonVO>>() {
//				});
				.body()
				.asString();

		assertTrue(content.contains("rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/668\""));
		assertTrue(content.contains("rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/677\""));
		assertTrue(content.contains("rel: \"self\"\n"
				+ "    href: \"http://localhost:8888/api/person/v1/400\""));

		assertTrue(content.contains("rel: \"first\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1/?direction=asc&page=0&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"prev\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1/?direction=asc&page=2&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"self\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1/?page=3&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1/?direction=asc&page=4&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"\n"
				+ "  href: \"http://localhost:8888/api/person/v1/?direction=asc&page=100&size=10&sort=firstName,asc\""));
	}

	
	@Test
	@Order(9)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		var specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1/")
				.setPort(TestsConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		given()
				.spec(specificationWithoutToken)
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.when()
				.get()
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasilia");
		person.setGender("Male");
		person.setEnabled(true);
	}
}
