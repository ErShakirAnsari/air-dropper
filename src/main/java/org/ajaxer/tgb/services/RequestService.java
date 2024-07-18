package org.ajaxer.tgb.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.ExceptionUtils;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Slf4j
@Service
public class RequestService
{
	public final static String REQUEST_PARAM_USER = "request.attr.user";

	private HttpServletRequest getHttpServletRequest()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public User getLoggedInUser()
	{
		User user = (User) getHttpServletRequest().getAttribute(REQUEST_PARAM_USER);
		log.debug("loggedInUser: {}", user);

		ExceptionUtils.throwWhenNull(user, new GreedyGhostTokenException("User not found in request"));

		return user;
	}

	public void setLoggedInUser(User user)
	{
		log.debug("user: {}", user);
		if (user == null)
			return;

		getHttpServletRequest().setAttribute(REQUEST_PARAM_USER, user);
	}
}
