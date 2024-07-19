package org.ajaxer.tgb.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.NumberUtils;
import org.ajaxer.simple.utils.SimpleUtils;
import org.ajaxer.simple.utils.StringUtils;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.constants.Token;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.dto.UserDto;
import org.ajaxer.tgb.dto.UserSyncRequestDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.ajaxer.tgb.repo.UserRepository;
import org.ajaxer.tgb.repo.UserTokenHistoryRepository;
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
	final private UserTokenHistoryRepository userTokenHistoryRepository;

	final private RequestService requestService;

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
		UserTokenHistory userTokenHistory = new UserTokenHistory();
		userTokenHistory.setToken(Token.NEW_USER_POINTS);
		userTokenHistory.setTokenDescription(TokenDescription.NEW_USER);
		userTokenHistory.setCreatedBy(newUser);

		userTokenHistoryRepository.save(userTokenHistory);

		if (StringUtils.isNotBlank(userDto.referredBy))
		{
			long referrerId = NumberUtils.toLong(userDto.referredBy, 0L);
			User referrer = userRepository.findByTelegramUserId(referrerId).orElse(null);
			if (referrer != null)
			{
				// for referrer
				userTokenHistory = new UserTokenHistory();
				userTokenHistory.setToken(Token.FRIEND_REFERRAL_POINTS);
				userTokenHistory.setTokenDescription(TokenDescription.REFERRAL);
				userTokenHistory.setCreatedBy(referrer);

				userTokenHistoryRepository.save(userTokenHistory);

				// for newUser
				userTokenHistory = new UserTokenHistory();
				userTokenHistory.setToken(Token.SELF_REFERRAL_POINTS);
				userTokenHistory.setTokenDescription(TokenDescription.REFERRAL);
				userTokenHistory.setCreatedBy(newUser);

				userTokenHistoryRepository.save(userTokenHistory);
			}
		}

		return true;
	}

	public ResponseDto sync(UserSyncRequestDto userSyncRequestDto)
	{
		SimpleUtils.sleep();

		User loggedInUser = requestService.getLoggedInUser();

		boolean increased = false;
		if (userSyncRequestDto != null && userSyncRequestDto.getClickEarning() > 0)
		{
			loggedInUser.setTotalTokens(loggedInUser.getTotalTokens() + userSyncRequestDto.getClickEarning());
			userRepository.saveAndFlush(loggedInUser);
			increased = true;
		}

		ResponseDto responseDto = new ResponseDto(true).setParameter("totalPoints", loggedInUser.getTotalTokens());
		if (increased)
			responseDto.setParameter("increased", true);

		return responseDto;
	}
}
