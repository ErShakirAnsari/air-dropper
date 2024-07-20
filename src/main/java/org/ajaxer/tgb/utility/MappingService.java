package org.ajaxer.tgb.utility;

import org.ajaxer.tgb.dto.UserTokenHistoryDto;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.springframework.stereotype.Service;

/**
 * @author Shakir Ansari
 * @since 2024-07-21
 */
@Service
public class MappingService
{
	public UserTokenHistoryDto getDto(UserTokenHistory entity)
	{
		UserTokenHistoryDto dto = new UserTokenHistoryDto();

		dto.token = entity.getToken();
		dto.description = entity.getTokenDescription().getDescription();

		dto.id = entity.getId();
		dto.activeStatus = entity.getActiveStatus();
		dto.createdOn = entity.getCreatedOn().getTime();

		return dto;
	}
}
