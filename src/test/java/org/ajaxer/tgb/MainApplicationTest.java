package org.ajaxer.tgb;

import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.jobs.DailyReferralCommission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class MainApplicationTest
{
	@Autowired
	private DailyReferralCommission dailyReferralCommission;

	@Test
	void contextLoaded()
	{
		log.info("contextLoaded");
	}

	@Test
	void name()
	{
		//dailyReferralCommission.processJob();
	}
}