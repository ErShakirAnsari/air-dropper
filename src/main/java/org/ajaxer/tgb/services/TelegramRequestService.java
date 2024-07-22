package org.ajaxer.tgb.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.constants.JobStep;
import org.ajaxer.tgb.entities.TelegramRequest;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.ajaxer.tgb.repo.TelegramRequestRepo;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramRequestService
{
	final public TelegramRequestRepo telegramRequestRepo;
	final public ObjectMapper objectMapper;

	public void saveRequest(Update update)
	{
		if (update == null || !update.hasMessage())
			return;

		try
		{
			TelegramRequest telegramRequest = new TelegramRequest();
			telegramRequest.setRequest(objectMapper.writeValueAsString(update));

			telegramRequestRepo.save(telegramRequest);

		} catch (Exception greedyGhostTokenException)
		{
			log.error("Exception when saving telegram payload");
			throw new GreedyGhostTokenException(greedyGhostTokenException);
		}
	}

	public List<TelegramRequest> getAllPendingRequests()
	{
		return telegramRequestRepo.findAllByStepOrderByCreatedOn(JobStep.QUEUE.getCode());
	}

	@Transactional
	public void save(TelegramRequest telegramRequest)
	{
		telegramRequestRepo.save(telegramRequest);
	}
}
