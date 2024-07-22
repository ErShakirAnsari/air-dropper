package org.ajaxer.tgb.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.CollectionUtils;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.repo.UserTokenHistoryRepository;
import org.ajaxer.tgb.utility.MappingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTokenHistoryService
{
	final UserTokenHistoryRepository repository;
	final RequestService requestService;
	final MappingService mappingService;

	public ResponseDto getHistory()
	{
		User loggedInUser = requestService.getLoggedInUser();

		List<UserTokenHistory> historyList = repository.findTop100ByCreatedByOrderByCreatedOnDesc(loggedInUser);
		log.debug("historyList: {}", historyList);

		ResponseDto responseDto = new ResponseDto(true);
		if (CollectionUtils.isNotBlank(historyList))
			historyList.forEach(history -> responseDto.addToList("userTokenHistoryDtoList", mappingService.getDto(history)));

		return responseDto;
	}
}
