package org.ajaxer.tgb.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.CollectionUtils;
import org.ajaxer.tgb.constants.JobStep;
import org.ajaxer.tgb.entities.TelegramRequest;
import org.ajaxer.tgb.services.BotService;
import org.ajaxer.tgb.services.TelegramRequestService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestJob
{
	final private TelegramRequestService telegramRequestService;
	final private BotService botService;

	@Scheduled(cron = "${jobs.requestJob.cron}")
	public void requestJobScheduler()
	{
		//log.debug("requestJobScheduler is running: {}", new Date());
		List<TelegramRequest> allPendingRequests = telegramRequestService.getAllPendingRequests();
		if (CollectionUtils.isBlank(allPendingRequests))
			return;

		for (TelegramRequest telegramRequest : allPendingRequests)
		{
			try
			{
				botService.processRequests(telegramRequest);
				telegramRequest.setStep(JobStep.DONE.getCode());
			} catch (Exception exception)
			{
				log.error(exception.getMessage(), exception);
				telegramRequest.setStep(JobStep.DONE.getCode());
			} finally
			{
				telegramRequestService.save(telegramRequest);
			}
		}
	}
}
