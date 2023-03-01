package br.com.provensi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.provensi.serialization.converter.YamlJakson2HttpMessageConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

	@Value("${cors.originPatterns:default}")
	private String corsOriginPatterns = "";

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new YamlJakson2HttpMessageConverter());
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		var allowedOrigins = corsOriginPatterns.split(",");
		registry
				.addMapping("/**")
				// .allowedMethods("GET", "POST", "PUT")
				.allowedMethods("*")
				.allowedOrigins(allowedOrigins)
				.allowCredentials(true);

		// WebMvcConfigurer.super.addCorsMappings(registry);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
// VAI QUERY PARAMETER
//		configurer.favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(true)
//				.useRegisteredExtensionsOnly(false).defaultContentType(MediaType.APPLICATION_JSON)
//				.mediaType("json", MediaType.APPLICATION_JSON).mediaType("json", MediaType.APPLICATION_XML);

		configurer
				.favorParameter(false)
				//.parameterName("mediaType")
				.ignoreAcceptHeader(false)
				.useRegisteredExtensionsOnly(false)
				.defaultContentType(MediaType.APPLICATION_JSON)
				.mediaType("json", MediaType.APPLICATION_JSON)
				.mediaType("xml", MediaType.APPLICATION_XML)
				.mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML);
	}
}
