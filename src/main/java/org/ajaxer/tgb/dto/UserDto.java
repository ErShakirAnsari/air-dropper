package org.ajaxer.tgb.dto;

import lombok.ToString;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@ToString
public class UserDto
{
	public Long telegramUserId;
	public String firstname;
	public String username;
	public String lastname;
	public long totalTokens;
	public String referredBy;

	//super class
	public Long id;
	public int activeStatus;
	public long createdOn;
}
