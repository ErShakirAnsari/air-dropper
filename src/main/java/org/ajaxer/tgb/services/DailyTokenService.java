package org.ajaxer.tgb.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.dto.ResponseDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserDailyToken;
import org.ajaxer.tgb.repo.UserDailyTokenRepository;
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
	final private UserDailyTokenRepository repository;
	final private RequestService requestService;

	@Value("${ghost.dailyTokenTime}")
	private int dailyTokenTime;

	public ResponseDto isRewardAvailable()
	{
		User loggedInUser = requestService.getLoggedInUser();
		UserDailyToken userDailyToken = repository.findByCreatedBy(loggedInUser);

		if (userDailyToken == null)
			return new ResponseDto(true).setParameter("rewardAvailable", true);

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
					.setParameter("rewardAvailable", false)
					.setParameter("hour", hours)
					.setParameter("minute", minutes);
		}

		return new ResponseDto(true).setParameter("rewardAvailable", true);
	}

	public ResponseDto claim()
	{
		ResponseDto rewardAvailable = isRewardAvailable();
		if (rewardAvailable == null
		    || !rewardAvailable.getParameter("rewardAvailable").asBoolean())
			return new ResponseDto(false);

		User loggedInUser = requestService.getLoggedInUser();
		UserDailyToken userDailyToken = repository.findByCreatedBy(loggedInUser);

		if (userDailyToken == null)
		{
			userDailyToken = new UserDailyToken();
			userDailyToken.setCreatedBy(loggedInUser);
		}

		userDailyToken.setLastClaimedOn(new Timestamp(System.currentTimeMillis()));

		repository.save(userDailyToken);

		return new ResponseDto(true);
	}
}
