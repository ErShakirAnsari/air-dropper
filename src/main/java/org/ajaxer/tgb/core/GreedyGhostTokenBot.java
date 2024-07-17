package org.ajaxer.tgb.core;

import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.exception.GreedyGhostTokenException;
import org.ajaxer.tgb.services.TelegramRequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Slf4j
@Component
public class GreedyGhostTokenBot extends TelegramLongPollingBot
{
	final private TelegramRequestService telegramRequestService;

	@Value("${telegram.bot.username}")
	private String botUsername;

	public GreedyGhostTokenBot(@Value("${telegram.bot.token}") String botToken,
	                           TelegramRequestService telegramRequestService)
	{
		super(botToken);
		this.telegramRequestService = telegramRequestService;
	}

	@Override
	public void onUpdateReceived(Update update)
	{
		log.info("update: {}", update);

		telegramRequestService.saveRequest(update);
	}

	@Override
	public String getBotUsername()
	{
		return botUsername;
	}

	public void sendMessage(BotOption option)
	{
		SendMessage message = new SendMessage();
		message.setChatId(option.getChatId());

		message.setText(option.getTextMessage().replaceAll("\\.", "\\\\."));
		message.setParseMode(ParseMode.MARKDOWNV2);

		if (option.getReplyKeyboardMarkup() != null)
			message.setReplyMarkup(option.getReplyKeyboardMarkup());
		else if (option.getInlineKeyboardMarkup() != null)
			message.setReplyMarkup(option.getInlineKeyboardMarkup());
		else
			message.setReplyMarkup(null);

		try
		{
			execute(message);
		} catch (Exception e)
		{
			log.error("Error sending message", e);
			throw new GreedyGhostTokenException(e);
		}
	}
}
