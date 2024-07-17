package org.ajaxer.tgb.configs;

import lombok.extern.slf4j.Slf4j;
import org.ajaxer.tgb.core.GreedyGhostTokenBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Slf4j
@Configuration
public class BotConfig
{
	@Autowired
	public void startBot(GreedyGhostTokenBot greedyGhostTokenBot) throws TelegramApiException
	{
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(greedyGhostTokenBot);
		log.info("airDropperBot: {}", greedyGhostTokenBot.getMe());
	}
}
