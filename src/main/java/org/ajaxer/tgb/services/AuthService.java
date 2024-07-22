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
import org.ajaxer.tgb.dto.UserDto;
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

	final private UserService userService;
	final private RequestService requestService;

	@Value("${telegram.bot.token}")
	private String botToken;

	public Map<String, Object> getUserMap(String telegramToken)
	{
		String initData = new String(Base64.getDecoder().decode(telegramToken));
		log.debug("initData: {}", telegramToken);

		List<NameValuePair> params = URLEncodedUtils.parse(initData, StandardCharsets.UTF_8);

		Optional<NameValuePair> valuePair = params.stream().filter(pair -> pair.getName().equals("user")).findFirst();

		String mapString = valuePair.map(NameValuePair::getValue).orElse(null);
		log.debug("mapString: {}", mapString);
		//{"id":657183858,"first_name":"Shakir","last_name":"Ansari","username":"ershakiransari","language_code":"en","allows_write_to_pm":true}

		try
		{
			//noinspection unchecked
			return objectMapper.readValue(mapString, Map.class);
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
					                         .addParam("httpStatus", HttpServletResponse.SC_FORBIDDEN)
					                         .addParam("description", "Missing Bearer Token"));

		if (!authorization.startsWith(bearer))
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .addParam("httpStatus", HttpServletResponse.SC_UNAUTHORIZED)
					                         .addParam("description", "Malformed bearer token"));

		String initDataB64 = authorization.substring(bearer.length());

		if (!isValidTelegramToken(initDataB64))
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .addParam("httpStatus", HttpServletResponse.SC_UNAUTHORIZED)
					                         .addParam("description", "Invalid bearer token"));
		Map<String, Object> userMap = getUserMap(initDataB64);
		if (userMap == null)
			return sendErrorResponse(response,
			                         new ResponseDto(false)
					                         .addParam("httpStatus", HttpServletResponse.SC_BAD_REQUEST)
					                         .addParam("description", "User map not available"));

		Long id = NumberUtils.toLong(userMap.get("id").toString());
		log.debug("id: {}", id);

		User user = userRepository.findByTelegramUserId(id).orElse(null);
		log.debug("user: {}", user);

		if (user == null)
		{
			//{"id":657183858,"first_name":"Shakir","last_name":"Ansari","username":"ershakiransari","language_code":"en","allows_write_to_pm":true}

			log.info("user {} not found, persisting in db", id);
			UserDto userDto = new UserDto();
			userDto.telegramUserId = id;

			if (userMap.get("first_name") != null)
				userDto.firstname = userMap.get("first_name").toString();

			if (userMap.get("last_name") != null)
				userDto.lastname = userMap.get("last_name").toString();

			if (userMap.get("username") != null)
				userDto.username = userMap.get("username").toString();

			userService.saveUser(userDto);

			user = userRepository.findByTelegramUserId(id).orElse(null);
		}

		requestService.setLoggedInUser(user);

		return true;
	}
}
