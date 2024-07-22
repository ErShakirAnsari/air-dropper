package org.ajaxer.tgb.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.StringUtils;
import org.ajaxer.tgb.constants.Token;
import org.ajaxer.tgb.core.BotOption;
import org.ajaxer.tgb.core.GreedyGhostTokenBot;
import org.ajaxer.tgb.dto.UserDto;
import org.ajaxer.tgb.entities.TelegramRequest;
import org.ajaxer.tgb.utility.CommonUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotService
{
	final private GreedyGhostTokenBot ghostBot;
	final public ObjectMapper objectMapper;
	final private UserService userService;

	private void processUpdate(Update update)
	{
		boolean uniqueUser = false;
		if (update != null
		    && update.hasMessage()
		    && update.getMessage().hasText()
		    && update.getMessage().getText().startsWith("/"))
			uniqueUser = handleCommand(update);

		String userMessage = getWelcomeMessage(update, uniqueUser);

		BotOption option = BotOption
				.builder()
				.chatId(update.getMessage().getChatId())
				.textMessage(userMessage)
				.inlineKeyboardMarkup(getInlineKeyboard())
				.build();

		ghostBot.sendMessage(option);
	}

	private static InlineKeyboardMarkup getInlineKeyboard()
	{
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		List<List<InlineKeyboardButton>> rows = new ArrayList<>();

		// Add YouTube channel link button
		String youtubeChannelUrl = "https://www.youtube.com/@ghost-combat";
		InlineKeyboardButton youtubeButton = new InlineKeyboardButton();
		youtubeButton.setText("Subscribe YouTube Channel");
		youtubeButton.setUrl(youtubeChannelUrl);
		List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
		rowInline1.add(youtubeButton);
		rows.add(rowInline1);

		// Add YouTube channel link button
		List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
		String telegramChannelUrl = "https://t.me/ghost_combat";
		InlineKeyboardButton telegramButton = new InlineKeyboardButton();
		telegramButton.setText("Join Telegram Channel");
		telegramButton.setUrl(telegramChannelUrl);
		rowInline2.add(telegramButton);
		rows.add(rowInline2);

		inlineKeyboardMarkup.setKeyboard(rows);

		return inlineKeyboardMarkup;
	}


	private boolean handleCommand(Update update)
	{
		String command = update.getMessage().getText();
		log.info("command: {}", command);

		if (command.startsWith("/start"))
		{
			UserDto userDto = new UserDto();
			userDto.telegramUserId = update.getMessage().getFrom().getId();
			userDto.username = update.getMessage().getFrom().getUserName();
			userDto.firstname = update.getMessage().getFrom().getFirstName();

			String referrerCode = null;
			if (command.contains(" "))
			{
				String[] split = command.split(" ");
				if (split.length > 1 && StringUtils.isNotBlank(split[1]))
					referrerCode = CommonUtils.getReferralCode(split[1]);
				userDto.referredBy = referrerCode;
			}

			return userService.saveUser(userDto);
		}
		return false;
	}

	private static String getWelcomeMessage(Update update, boolean uniqueUser)
	{
		String userMessage = "Hi " + update.getMessage().getFrom().getFirstName() + ",";
		userMessage += System.lineSeparator();
		userMessage += "Welcome to Ghost Airdrop, ";
		if (uniqueUser)
		{
			userMessage += "You have earned " + CommonUtils.numberLocal(Token.NEW_USER_TOKENS) + " tokens as joining bonus.";
			userMessage += System.lineSeparator();
		}
		userMessage += "You can start Ghost Combat and earn more points.";
		userMessage += System.lineSeparator();
		userMessage += System.lineSeparator();
		userMessage += "Please Join our below channels for extra tokens:";
		userMessage += System.lineSeparator();
		userMessage += "Telegram Channel " + CommonUtils.numberLocal(Token.CHANNEL_JOINED_TELEGRAM) + " tokens";
		userMessage += System.lineSeparator();
		userMessage += "YouTube Channel " + CommonUtils.numberLocal(Token.CHANNEL_JOINED_YOUTUBE) + " tokens";
		return userMessage;
	}

	@Transactional
	public void processRequests(TelegramRequest telegramRequest) throws Exception
	{
		Update update = objectMapper.readValue(telegramRequest.getRequest(), Update.class);
		processUpdate(update);
	}
}
