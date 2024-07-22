package org.ajaxer.tgb.constants;

import lombok.Getter;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Getter
public enum TokenDescription
{
	NEW_USER("Registration"),
	CLICK_EARNING("Click earning"),
	REFERRAL("Referral"),
	REFERRAL_SHARE("Referral commission"),
	DAILY_CLAIM("Daily claim"),
	TELEGRAM_CHANNEL_SUBSCRIPTION("Telegram channel subscription"),
	TELEGRAM_GROUP_SUBSCRIPTION("Telegram group subscription"),
	YOUTUBE_CHANNEL_SUBSCRIPTION("Youtube channel subscription"),
	;

	private final String description;

	TokenDescription(String description)
	{
		this.description = description;
	}
}
