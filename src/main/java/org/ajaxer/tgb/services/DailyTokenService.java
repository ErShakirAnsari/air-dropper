package org.ajaxer.tgb.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.constants.Token;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserDailyToken;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.repo.UserDailyTokenRepository;
import org.ajaxer.tgb.repo.UserRepository;
import org.ajaxer.tgb.repo.UserTokenHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyTokenService
{
	final private UserDailyTokenRepository userDailyTokenRepository;
	final private UserTokenHistoryRepository historyRepository;
	final private UserRepository userRepository;

	final private RequestService requestService;

	@Value("${ghost.dailyTokenTime}")
	private int dailyTokenTime;

	public ResponseDto isRewardAvailable()
	{
		User loggedInUser = requestService.getLoggedInUser();
		UserDailyToken userDailyToken = userDailyTokenRepository.findByCreatedBy(loggedInUser);

		if (userDailyToken == null)
			return new ResponseDto(true)
					.addParam("rewardAvailable", true)
					.addParam("tokens", Token.DAILY_CLAIM_TOKENS);

		long lastClaimedOn = userDailyToken.getLastClaimedOn().getTime();
		log.debug("lastClaimedOn: {}ms, lastClaimedOn: {}", lastClaimedOn, userDailyToken.getLastClaimedOn());

		long nextClaimedOn = lastClaimedOn + (dailyTokenTime * 60 * 60 * 1000L);
		log.debug("nextClaimedOn: {}ms, nextClaimedOn: {}", nextClaimedOn, new Date(nextClaimedOn));

		//cool downtime expire or not
		if (nextClaimedOn > System.currentTimeMillis())
		{
			long durationInMillis = nextClaimedOn - System.currentTimeMillis(); // diff in milliseconds

			// Convert milliseconds to hours and minutes
			long totalMinutes = durationInMillis / 1000 / 60;
			long hours = totalMinutes / 60;
			long minutes = totalMinutes % 60;

			log.debug("hours: {}, minutes: {}", hours, minutes);

			return new ResponseDto(false)
					.addParam("rewardAvailable", false)
					.addParam("hour", hours)
					.addParam("minute", minutes);
		}

		return new ResponseDto(true)
				.addParam("rewardAvailable", true)
				.addParam("tokens", Token.DAILY_CLAIM_TOKENS);
	}

	@Transactional
	public ResponseDto claim()
	{
		ResponseDto rewardAvailable = isRewardAvailable();
		if (rewardAvailable == null
		    || !rewardAvailable.getParameter("rewardAvailable").asBoolean())
			return new ResponseDto(false);

		User loggedInUser = requestService.getLoggedInUser();

		// -- 1
		UserDailyToken userDailyToken = userDailyTokenRepository.findByCreatedBy(loggedInUser);

		if (userDailyToken == null)
		{
			userDailyToken = new UserDailyToken();
			userDailyToken.setCreatedBy(loggedInUser);
		}

		userDailyToken.setLastClaimedOn(new Timestamp(System.currentTimeMillis()));

		userDailyTokenRepository.saveAndFlush(userDailyToken);

		// -- 2
		UserTokenHistory history = new UserTokenHistory();
		history.setToken(Token.DAILY_CLAIM_TOKENS);
		history.setTokenDescription(TokenDescription.DAILY_CLAIM);
		history.setCreatedBy(loggedInUser);

		historyRepository.saveAndFlush(history);

		// -- 3
		loggedInUser.setTotalTokens(loggedInUser.getTotalTokens() + history.getToken());
		userRepository.saveAndFlush(loggedInUser);

		return new ResponseDto(true)
				.addParam("tokens", Token.DAILY_CLAIM_TOKENS)
				.addParam("hour", dailyTokenTime)
				.addParam("minute", 0);
	}

}
