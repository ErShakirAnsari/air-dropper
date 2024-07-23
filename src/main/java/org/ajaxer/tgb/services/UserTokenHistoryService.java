package org.ajaxer.tgb.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.CollectionUtils;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.entities.projection.UserDailyReferralProjection;
import org.ajaxer.tgb.repo.UserTokenHistoryRepository;
import org.ajaxer.tgb.utility.MappingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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

	public UserTokenHistory save(UserTokenHistory history)
	{
		return repository.saveAndFlush(history);
	}

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

	public List<UserDailyReferralProjection> getTotalTokensByUserAndDate(Date inputDate, Date excludedDate, Pageable pageable)
	{
		List<TokenDescription> includedTokenDescriptions = List.of(TokenDescription.CLICK_EARNING);
		log.info("includedTokenDescriptions: {}", includedTokenDescriptions);

		List<TokenDescription> excludedTokenDescriptions = List.of(TokenDescription.REFERRAL_SHARE);
		log.info("excludedTokenDescriptions: {}", excludedTokenDescriptions);

		Page<UserDailyReferralProjection> page = repository.findTotalTokensByUserAndDate(includedTokenDescriptions,
		                                                                                 excludedTokenDescriptions,
		                                                                                 inputDate,
		                                                                                 excludedDate,
		                                                                                 pageable);

		return page.getContent();
	}
}
