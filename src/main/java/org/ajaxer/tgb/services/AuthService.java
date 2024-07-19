package org.ajaxer.tgb.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.NumberUtils;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.repo.UserRepository;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
	final private UserRepository userRepository;
	final private ObjectMapper objectMapper;

	final private RequestService requestService;

	@Value("${telegram.bot.token}")
	private String botToken;

	public Long getUserId(String telegramToken)
	{
		String initData = new String(Base64.getDecoder().decode(telegramToken));
		log.debug("initData: {}", telegramToken);

		List<NameValuePair> params = URLEncodedUtils.parse(initData, StandardCharsets.UTF_8);

		Optional<NameValuePair> valuePair = params.stream().filter(pair -> pair.getName().equals("user")).findFirst();

		String mapString = valuePair.map(NameValuePair::getValue).orElse(null);

		try
		{
			Map<String, Object> map = objectMapper.readValue(mapString, Map.class);
			return NumberUtils.toLong(map.get("id").toString());
		} catch (JsonProcessingException e)
		{
			return null;
		}
	}

	public boolean isValidTelegramToken(String telegramToken)
	{
		String initData = new String(Base64.getDecoder().decode(telegramToken));
		log.debug("initData: {}", telegramToken);

		List<NameValuePair> params = URLEncodedUtils.parse(initData, StandardCharsets.UTF_8);

		List<NameValuePair> preparedData = new java.util.ArrayList<>(params.stream()
		                                                                   .filter(e -> !e.getName().equals("hash"))
		                                                                   .toList());
		preparedData.sort(Comparator.comparing(NameValuePair::getName));

		String dataCheckString = String.join("\n", preparedData.stream()
		                                                       .map(e -> e.getName() + "=" + e.getValue())
		                                                       .toList());

		String botTokenData = "WebAppData";

		byte[] hmacSecret = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, botTokenData).hmac(botToken);
		String calculatedHash = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, hmacSecret).hmacHex(dataCheckString);
		log.debug("calculatedHash: {}", calculatedHash);

		Optional<NameValuePair> valuePair = params.stream().filter(p -> p.getName().equals("hash")).findFirst();
		String presentedHash = valuePair.map(NameValuePair::getValue).orElse(null);

		log.debug("presentedHash: {}", presentedHash);

		return calculatedHash.equals(presentedHash);
	}

	@SneakyThrows
	public boolean sendErrorResponse(HttpServletResponse response, ResponseDto responseDto)
	{
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(responseDto.getParameter("httpStatus").asInt());

		try (PrintWriter printWriter = response.getWriter())
		{
			String errorResponse = objectMapper.writeValueAsString(responseDto);
			printWriter.write(errorResponse);
			printWriter.flush();
		}
		return false;
	}

	public boolean authVerification(HttpServletRequest request, HttpServletResponse response)
	{
		String servletPath = request.getServletPath();

		log.info("[{}], servletPath: {}", request.getMethod().toUpperCase(), servletPath);

		if (servletPath.startsWith("/public"))
			return true;

		if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
			return true;

		String authorization = request.getHeader("Authorization");

		final String bearer = "Bearer ";
		if (authorization == null)
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .setParameter("httpStatus", HttpServletResponse.SC_FORBIDDEN)
					                         .setParameter("description", "Missing Bearer Token"));

		if (!authorization.startsWith(bearer))
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .setParameter("httpStatus", HttpServletResponse.SC_UNAUTHORIZED)
					                         .setParameter("description", "Malformed bearer token"));

		String initDataB64 = authorization.substring(bearer.length());

		if (!isValidTelegramToken(initDataB64))
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .setParameter("httpStatus", HttpServletResponse.SC_UNAUTHORIZED)
					                         .setParameter("description", "Invalid bearer token"));

		Long id = getUserId(initDataB64);
		log.debug("id: {}", id);

		User user = userRepository.findByTelegramUserId(id).orElse(null);
		log.debug("user: {}", user);

		if (user == null)
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .setParameter("httpStatus", HttpServletResponse.SC_UNAUTHORIZED)
					                         .setParameter("description", "User not available"));

		requestService.setLoggedInUser(user);

		return true;
	}
}
