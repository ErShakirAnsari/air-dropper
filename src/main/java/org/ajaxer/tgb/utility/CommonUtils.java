package org.ajaxer.tgb.utility;

import java.text.NumberFormat;

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
		try
		{
			return NumberFormat.getNumberInstance().format(number);
		} catch (Exception e)
		{
			return String.valueOf(number);
		}
	}
}
