package org.ajaxer.tgb.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer
{
	@Value("${ghost.allowedOriginPattern}")
	private String allowedOriginPattern;

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		registry.addMapping("/**") // Allow all origins
		        .allowedOriginPatterns(allowedOriginPattern)
		        //.allowedOriginPatterns("*")
		        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
		        .allowedHeaders("*") // Allow all headers
		        .allowCredentials(true) // Allow credentials like cookies, authorization headers
		        .maxAge(3600); // Max age of the cache control
	}
}
