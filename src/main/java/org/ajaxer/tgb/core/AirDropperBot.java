package org.ajaxer.tgb.core;

import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.services.BotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Slf4j
@Component
public class AirDropperBot extends TelegramLongPollingBot
{
	private BotService botService;

	@Value("${tgb.bot.username}")
	private String botUsername;

	public AirDropperBot(@Value("${tgb.bot.token}") String botToken, BotService botService)
	{
		super(botToken);
		this.botService = botService;
	}

	@Override
	public void onUpdateReceived(Update update)
	{
		log.debug("update: {}", update);
		botService.processUpdate(update);
	}

	@Override
	public String getBotUsername()
	{
		return botUsername;
	}
}
