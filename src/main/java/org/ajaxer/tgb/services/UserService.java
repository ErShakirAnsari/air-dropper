package org.ajaxer.tgb.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.NumberUtils;
import org.ajaxer.simple.utils.StringUtils;
import org.ajaxer.tgb.constants.Token;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.dto.UserDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserToken;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.ajaxer.tgb.repo.UserRepository;
import org.ajaxer.tgb.repo.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService
{
	final private UserRepository userRepository;
	final private UserTokenRepository userTokenRepository;

	@Transactional
	public boolean saveUser(UserDto userDto)
	{
		log.info("userDto: {}", userDto);

		if (userDto.telegramUserId == null)
			throw new GreedyGhostTokenException("Telegram User Id is null");

		if (userDto.firstname == null)
			throw new GreedyGhostTokenException("Telegram User Firstname is null");

		Optional<User> byTelegramUserId = userRepository.findByTelegramUserId(userDto.telegramUserId);
		if (byTelegramUserId.isPresent())
		{
			log.info("User with telegram newUser id {} already exists", userDto.telegramUserId);
			return false;
		}

		User newUser = new User();
		newUser.setTelegramUserId(userDto.telegramUserId);
		newUser.setFirstname(userDto.firstname);
		newUser.setLastname(userDto.lastname);
		newUser.setTotalTokens(0);
		newUser.setReferredBy(userDto.referredBy);
		log.debug("newUser: {}", newUser);

		userRepository.save(newUser);
		UserToken userToken = new UserToken();
		userToken.setToken(Token.NEW_USER_POINTS);
		userToken.setTokenDescription(TokenDescription.NEW_USER);
		userToken.setCreatedBy(newUser);

		userTokenRepository.save(userToken);

		if (StringUtils.isNotBlank(userDto.referredBy))
		{
			long referrerId = NumberUtils.toLong(userDto.referredBy, 0L);
			User referrer = userRepository.findByTelegramUserId(referrerId).orElse(null);
			if (referrer != null)
			{
				// for referrer
				userToken = new UserToken();
				userToken.setToken(Token.FRIEND_REFERRAL_POINTS);
				userToken.setTokenDescription(TokenDescription.REFERRAL);
				userToken.setCreatedBy(referrer);

				userTokenRepository.save(userToken);

				// for newUser
				userToken = new UserToken();
				userToken.setToken(Token.SELF_REFERRAL_POINTS);
				userToken.setTokenDescription(TokenDescription.REFERRAL);
				userToken.setCreatedBy(newUser);

				userTokenRepository.save(userToken);
			}
		}

		return true;
	}
}
