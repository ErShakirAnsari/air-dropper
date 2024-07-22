package org.ajaxer.tgb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.services.UserTokenHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shakir Ansari
 * @since 2024-07-21
 */
@Slf4j
@RestController
@RequestMapping("/user-token-history")
@RequiredArgsConstructor
public class UserTokenHistoryController
{
	final UserTokenHistoryService userTokenHistoryService;

	@GetMapping()
	public ResponseDto getHistory()
	{
		return userTokenHistoryService.getHistory();
	}
}
