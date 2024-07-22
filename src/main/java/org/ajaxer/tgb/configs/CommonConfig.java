package org.ajaxer.tgb.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Configuration
public class CommonConfig
{
	@Bean
	public ObjectMapper getObjectMapper()
	{
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zZ");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(dateFormat);

		return objectMapper;
	}
}
