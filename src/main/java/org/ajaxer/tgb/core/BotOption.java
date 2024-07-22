package org.ajaxer.tgb.core;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Data
@Builder
public class BotOption
{
	private long chatId;
	private String textMessage;
	private ReplyKeyboardMarkup replyKeyboardMarkup;
	private InlineKeyboardMarkup inlineKeyboardMarkup;
}
