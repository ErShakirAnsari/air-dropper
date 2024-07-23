package org.ajaxer.tgb.utility;

import org.ajaxer.simple.utils.NumberUtils;
import org.ajaxer.simple.utils.StringUtils;
import org.ajaxer.tgb.entities.User;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
public class CommonUtils
{
	public static String getReferralCode(String telegramUserId)
	{
		//return "r" + telegramUserId;
		return telegramUserId;
	}

	public static String numberLocal(long number)
	{
		return NumberUtils.toLocalNumber(number);
	}

	public static String getUserDisplayName(User user)
	{
		if (user == null)
			return null;

		if (StringUtils.isBlank(user.getUsername()))
			return user.getFirstname();

		return user.getUsername();
	}
}
