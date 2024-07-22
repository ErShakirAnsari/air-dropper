package org.ajaxer.tgb.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.CollectionUtils;
import org.ajaxer.simple.utils.NumberUtils;
import org.ajaxer.simple.utils.SimpleUtils;
import org.ajaxer.simple.utils.StringUtils;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.constants.Token;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.dto.ReferredFriendDto;
import org.ajaxer.tgb.dto.UserDto;
import org.ajaxer.tgb.dto.UserSyncRequestDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.ajaxer.tgb.repo.UserRepository;
import org.ajaxer.tgb.repo.UserTokenHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

	public void increaseUserTotalPoints(User user, int additionalPoints)
	{
		user.setTotalTokens(user.getTotalTokens() + additionalPoints);
		userRepository.save(user);
	}

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

		// -- create new user
		User newUser = new User();
		newUser.setUsername(userDto.username);
		newUser.setTelegramUserId(userDto.telegramUserId);
		newUser.setFirstname(userDto.firstname);
		newUser.setLastname(userDto.lastname);
		newUser.setTotalTokens(Token.NEW_USER_TOKENS);
		newUser.setReferredBy(userDto.referredBy);

		userRepository.save(newUser);
		log.debug("newUser: {}", newUser);

		// -- UserTokenHistory
		UserTokenHistory userTokenHistory = new UserTokenHistory();
		userTokenHistory.setToken(Token.NEW_USER_TOKENS);
		userTokenHistory.setTokenDescription(TokenDescription.NEW_USER);
		userTokenHistory.setCreatedBy(newUser);

		log.debug("userTokenHistory: {}", userTokenHistory);
		userTokenHistoryRepository.save(userTokenHistory);

		if (StringUtils.isNotBlank(userDto.referredBy))
		{
			long referrerId = NumberUtils.toLong(userDto.referredBy, 0L);
			User referrer = userRepository.findByTelegramUserId(referrerId).orElse(null);
			log.debug("referrer: {}", referrer);

			if (referrer != null)
			{
				if (referrer.getTelegramUserId() == newUser.getTelegramUserId())
				{
					newUser.setReferredBy(null);
					userRepository.save(newUser);
				} else
				{
					// for referrer
					userTokenHistory = new UserTokenHistory();
					userTokenHistory.setToken(Token.REFERRAL_TOKENS_TO_FRIEND);
					userTokenHistory.setTokenDescription(TokenDescription.REFERRAL);
					userTokenHistory.setCreatedBy(referrer);

					userTokenHistoryRepository.save(userTokenHistory);

					increaseUserTotalPoints(referrer, Token.REFERRAL_TOKENS_TO_FRIEND);

					// for newUser
					userTokenHistory = new UserTokenHistory();
					userTokenHistory.setToken(Token.REFERRAL_TOKENS_TO_SELF);
					userTokenHistory.setTokenDescription(TokenDescription.REFERRAL);
					userTokenHistory.setCreatedBy(newUser);

					userTokenHistoryRepository.save(userTokenHistory);

					increaseUserTotalPoints(newUser, Token.REFERRAL_TOKENS_TO_SELF);
				}
			}
		}

		return true;
	}

	@Transactional
	public ResponseDto sync(UserSyncRequestDto userSyncRequestDto)
	{
		SimpleUtils.sleep();

		User loggedInUser = requestService.getLoggedInUser();

		boolean increased = false;
		int clickEarning = userSyncRequestDto.getClickEarning();

		if (clickEarning > 0)
		{
			loggedInUser.setTotalTokens(loggedInUser.getTotalTokens() + clickEarning);
			userRepository.saveAndFlush(loggedInUser);

			// -- history
			UserTokenHistory history = new UserTokenHistory();
			history.setToken(clickEarning);
			history.setTokenDescription(TokenDescription.CLICK_EARNING);
			history.setCreatedBy(loggedInUser);
			userTokenHistoryRepository.saveAndFlush(history);

			increased = true;
		}

		ResponseDto responseDto = new ResponseDto(true).addParam("totalPoints", loggedInUser.getTotalTokens());
		if (increased)
			responseDto.addParam("increased", true);

		return responseDto;
	}

	public ResponseDto getReferredFriends()
	{
		User loggedInUser = requestService.getLoggedInUser();

		List<User> referredFriendList = userRepository
				.findAllByReferredByOrderByIdDesc(String.valueOf(loggedInUser.getTelegramUserId()));

		ResponseDto responseDto = new ResponseDto(true);
		if (CollectionUtils.isNotBlank(referredFriendList))
			referredFriendList.forEach(friend -> {
				String name = StringUtils.isBlank(friend.getUsername())
						? friend.getFirstname()
						: "@" + friend.getUsername();
				responseDto.addToList("referredFriendDtoList", new ReferredFriendDto(name, friend.getCreatedOn().getTime()));
			});

		return responseDto;
	}
}
