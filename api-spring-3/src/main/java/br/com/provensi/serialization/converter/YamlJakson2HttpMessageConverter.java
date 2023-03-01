package br.com.provensi.serialization.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlJakson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

	public YamlJakson2HttpMessageConverter() {
		super(
				new YAMLMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL),
				MediaType.parseMediaType("application/x-yaml"));
	}

}
