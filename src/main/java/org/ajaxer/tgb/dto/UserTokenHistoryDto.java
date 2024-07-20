package org.ajaxer.tgb.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@ToString
@JsonPropertyOrder({"id", "token", "description", " activeStatus", "createdOn"})
public class UserTokenHistoryDto
{
	public long token;
	public String description;

	//super class
	public Long id;
	public int activeStatus;
	public long createdOn;
}
