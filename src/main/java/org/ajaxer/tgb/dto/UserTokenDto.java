package org.ajaxer.tgb.dto;

import lombok.ToString;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@ToString
public class UserTokenDto
{
	public long points;
	public String pointDescription;

	//super class
	public Long id;
	public int activeStatus;
	public long createdOn;
}
