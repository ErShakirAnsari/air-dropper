package org.ajaxer.tgb.jobs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.MathUtils;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.entities.projection.UserDailyReferralProjection;
import org.ajaxer.tgb.services.UserService;
import org.ajaxer.tgb.services.UserTokenHistoryService;
import org.ajaxer.tgb.utility.CommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReferralCommission
{
	final private UserTokenHistoryService historyService;
	final private UserService userService;

	@Value("${jobs.dailyReferralCommissionJob.maxResultPerPage}")
	private int maxResultPerPage;

	@Value("${jobs.dailyReferralCommissionJob.commissionPercentage}")
	private int commissionPercentage;

	@Transactional
	@Scheduled(cron = "${jobs.dailyReferralCommissionJob.cron}")
	public void dailyReferralCommissionJobScheduler()
	{
		processJob();
	}

	@SneakyThrows
	public void processJob()
	{
		// Calculate yesterday's date
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		log.info("Today is {}", today);

		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		log.info("yesterday: {}", yesterday);

		// Get total tokens by yesterday date
		int pageNumber = 0;
		List<UserDailyReferralProjection> results;
		do
		{
			Pageable pageable = PageRequest.of(pageNumber, maxResultPerPage);
			log.debug("pageNumber: {}, pageable: {}", pageNumber, pageable);
			results = historyService.getTotalTokensByUserAndDate(yesterday, today, pageable);

			// Process the results (e.g., logging, saving to a file, etc.)
			for (UserDailyReferralProjection result : results)
			{
				log.debug("totalTokens: {}", result.getTotalTokens());

				User createdBy = result.getCreatedBy();

				int commissionTokens = result.getTotalTokens();
				if (commissionTokens > 10)
					commissionTokens = MathUtils.getPercentage(result.getTotalTokens(), commissionPercentage);

				if (commissionTokens <= 0)
				{
					log.warn("commissionTokens less 0 {}", commissionTokens);
					continue;
				}

				User referrer = userService.findByReferredBy(createdBy.getReferredBy());
				log.debug("referrer: {}", referrer);

				if (referrer == null)
				{
					log.warn("referrer is null for referrerId {}", createdBy.getReferredBy());
					continue;
				}

				UserTokenHistory history = new UserTokenHistory();
				history.setToken(commissionTokens);
				history.setTokenDescription(TokenDescription.REFERRAL_SHARE);
				history.setComment(CommonUtils.getUserDisplayName(createdBy));
				history.setCreatedBy(referrer);

				historyService.save(history);

				log.info("userID: {}, date: {} totalTokens: {}, commissionTokens: {}",
				         createdBy.getId(),
				         result.getCreatedOn(),
				         result.getTotalTokens(),
				         commissionTokens);
			}

			pageNumber++;
		}
		while (results.size() == maxResultPerPage);

	}

}
