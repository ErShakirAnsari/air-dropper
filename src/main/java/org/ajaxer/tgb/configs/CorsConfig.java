package org.ajaxer.tgb.configs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Slf4j
@Configuration
public class CorsConfig
{
	@Value("${ghost.allowedOriginPattern}")
	private String allowedOriginPattern;

	@PostConstruct
	public void init()
	{
		log.info("cors config loaded for allowedOriginPattern: {}", allowedOriginPattern);
	}


	@Bean
	public WebMvcConfigurer corsConfigurer()
	{
		return new WebMvcConfigurer()
		{
			@Override
			public void addCorsMappings(CorsRegistry registry)
			{
				registry.addMapping("/**") // Allow all origins
				        .allowedOriginPatterns(allowedOriginPattern)
				        //.allowedOriginPatterns("*")
				        .allowedMethods("*") // Allow specific HTTP methods
				        .allowedHeaders("*") // Allow all headers
				        .allowCredentials(true) // Allow credentials like cookies, authorization headers
				        .maxAge(3600); // Max age of the cache control
			}
		};
	}
}
