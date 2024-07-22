package org.ajaxer.tgb.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.services.AuthService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Shakir
 * @since 2023-05-27
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringWebMvcConfigurer implements WebMvcConfigurer
{
	final private AuthHandlerInterceptor authHandlerInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(authHandlerInterceptor).addPathPatterns("/**");
	}

	@Component
	@RequiredArgsConstructor
	public static class AuthHandlerInterceptor implements HandlerInterceptor
	{
		final private AuthService authService;

		@Override
		public boolean preHandle(@NonNull HttpServletRequest request,
		                         @NonNull HttpServletResponse response,
		                         @NonNull Object handler)
		{
			log.debug("preHandle");
			return authService.authVerification(request, response);
		}
	}
}
