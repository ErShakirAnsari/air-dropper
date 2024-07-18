package org.ajaxer.tgb.services;

import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.dto.ResponseDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserDailyToken;
import org.ajaxer.tgb.repo.UserDailyTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@SpringBootTest
class DailyTokenServiceTest
{
	@Autowired
	private DailyTokenService dailyTokenService;

	@MockBean
	private UserDailyTokenRepository repository;

	@MockBean
	private RequestService requestService;

	@Value("${ghost.dailyTokenTime}")
	private int dailyTokenTime;

	@Test
	public void shouldReturnFalse()
	{
		//Arrange
		int claimedHourBefore = 1;
		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now().minus(Duration.ofHours(claimedHourBefore)).toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(false);

		Assertions.assertThat(rewardAvailable.getParameter("minute").asInt()).isEqualTo(59);

		// because minute will 59
		int expectedRemainingHours = 1;

		Assertions.assertThat(rewardAvailable.getParameter("hour").asInt())
		          .isEqualTo(dailyTokenTime - claimedHourBefore - expectedRemainingHours);
	}

	@RepeatedTest(10)
	public void shouldReturnFalse_minute_diff()
	{
		//Arrange
		int claimedHourBefore = 18;
		int claimedMinuteBefore = 25;

		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now()
		                        .minus(Duration.ofHours(claimedHourBefore))
		                        .minus(Duration.ofMinutes(claimedMinuteBefore))
		                        .toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(false);

		Assertions.assertThat(rewardAvailable.getParameter("minute").asInt()).isGreaterThanOrEqualTo(59 - claimedMinuteBefore);

		// because minute will greater zero
		int expectedRemainingHours = 1;

		Assertions.assertThat(rewardAvailable.getParameter("hour").asInt())
		          .isEqualTo(dailyTokenTime - claimedHourBefore - expectedRemainingHours);
	}

	@Test
	public void shouldReturnFalse_minute_diff_2()
	{
		//Arrange
		int claimedHourBefore = 20;
		int claimedMinuteBefore = 59;

		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now()
		                        .minus(Duration.ofHours(claimedHourBefore))
		                        .minus(Duration.ofMinutes(claimedMinuteBefore))
		                        .toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(false);

		Assertions.assertThat(rewardAvailable.getParameter("minute").asInt()).isEqualTo(0);

		// because minute will greater zero
		int expectedRemainingHours = 1;

		Assertions.assertThat(rewardAvailable.getParameter("hour").asInt())
		          .isEqualTo(dailyTokenTime - claimedHourBefore - expectedRemainingHours);
	}

	@Test
	public void shouldReturnTrue()
	{
		//Arrange
		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now().minus(Duration.ofHours(dailyTokenTime)).toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(true);
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("hour");
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("minute");
	}

	@Test
	public void shouldReturnTrue_2()
	{
		//Arrange
		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now().minus(Duration.ofHours(dailyTokenTime)).minus(Duration.ofSeconds(1)).toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(true);
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("hour");
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("minute");
	}

	@Test
	public void shouldReturnTrue_4()
	{
		//Arrange
		User loggedInUser = new User();
		UserDailyToken userDailyToken = new UserDailyToken();
		long claimedOn = Instant.now().minus(Duration.ofHours(48)).toEpochMilli();
		userDailyToken.setLastClaimedOn(new Timestamp(claimedOn));

		Mockito.when(requestService.getLoggedInUser()).thenReturn(loggedInUser);
		Mockito.when(repository.findByCreatedBy(loggedInUser)).thenReturn(userDailyToken);

		//Action
		ResponseDto rewardAvailable = dailyTokenService.isRewardAvailable();

		//Assert
		Assertions.assertThat(rewardAvailable).isNotNull();
		Assertions.assertThat(rewardAvailable.getParameter("rewardAvailable").asBoolean()).isEqualTo(true);
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("hour");
		Assertions.assertThat(rewardAvailable.getData()).doesNotContainKey("minute");
	}
}