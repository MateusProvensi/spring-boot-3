package br.com.provensi.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
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
import br.com.provensi.integrationtests.vo.BookVO;
import br.com.provensi.integrationtests.vo.pagedmodels.PagedModelBook;
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
public class BookControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YamlMapper objectMapper;

	private static BookVO book;

	@BeforeAll
	public static void setUp() {
		objectMapper = new YamlMapper();

		book = new BookVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO();
		user.setUserName("leandro");
		user.setPassword("admin123");

		var token = given()
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
				.addHeader(TestsConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token)
				.setBasePath("/api/books/v1/")
				.setPort(TestsConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();

		var persistedBook = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.body(book, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(BookVO.class, objectMapper);

		book = persistedBook;

		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertTrue(persistedBook.getId() > 0);

		assertEquals("Author", persistedBook.getAuthor());
		assertEquals("Title", persistedBook.getTitle());
		assertEquals(21.00, persistedBook.getPrice());
	}

	@Test
	@Order(2)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBook();

		var persistedBook = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_PROVENSI)
				.pathParam("id", book.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(BookVO.class, objectMapper);

		book = persistedBook;

		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertEquals(book.getId(), persistedBook.getId());

		assertEquals("Author", persistedBook.getAuthor());
		assertEquals("Title", persistedBook.getTitle());
		assertEquals(21.00, persistedBook.getPrice());
	}

	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("Title 2");

		var persistedBook = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.body(book, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(BookVO.class, objectMapper);

		book = persistedBook;

		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertTrue(persistedBook.getId() > 0);

		assertEquals("Author", persistedBook.getAuthor());
		assertEquals("Title 2", persistedBook.getTitle());
		assertEquals(21.00, persistedBook.getPrice());
	}

	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		var wrapper = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 0, "size", 30, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PagedModelBook.class, objectMapper);

		List<BookVO> books = wrapper.getContent();

		BookVO foundBookOne = books.get(0);

		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getLaunchDate());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getTitle());

		assertEquals(12, foundBookOne.getId());

		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
		assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());
		assertEquals(54.0, foundBookOne.getPrice());
		
		BookVO foundBookSix = books.get(5);
		
		assertNotNull(foundBookSix.getId());
		assertNotNull(foundBookSix.getAuthor());
		assertNotNull(foundBookSix.getLaunchDate());
		assertNotNull(foundBookSix.getPrice());
		assertNotNull(foundBookSix.getTitle());
		
		assertEquals(11, foundBookSix.getId());
		
		assertEquals("Roger S. Pressman", foundBookSix.getAuthor());
		assertEquals("Engenharia de Software: uma abordagem profissional", foundBookSix.getTitle());
		assertEquals(56.0, foundBookSix.getPrice());
	}

	@Test
	@Order(6)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		var unpackageContent = given()
				.config(
						RestAssuredConfig
						.config()
						.encoderConfig(
								EncoderConfig
								.encoderConfig()
								.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_YAML)
				.accept(TestsConfigs.CONTENT_TYPE_YAML)
				.queryParams("page", 0, "size", 30, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();
		
		var content = unpackageContent.replaceAll("\n", "").replaceAll("\r", "");
	
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/books/v1/12\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/books/v1/3\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/books/v1/5\""));

//		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/books/v1/?direction=asc&page=0&size=3&sort=title,asc\""));
//		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/books/v1/?page=0&size=3&direction=asc\""));
//		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/books/v1/?direction=asc&page=1&size=3&sort=title,asc\""));
//		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/books/v1/?direction=asc&page=5&size=3&sort=title,asc\""));
	}

	@Test
	@Order(7)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		var specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/books/v1/")
				.setPort(TestsConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();

		given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(
										EncoderConfig
												.encoderConfig()
												.encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.spec(specificationWithoutToken)
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

	private void mockBook() {
		book.setAuthor("Author");
		book.setTitle("Title");
		book.setPrice(Double.valueOf(21.00));
		book.setLaunchDate(new Date());
	}
}
