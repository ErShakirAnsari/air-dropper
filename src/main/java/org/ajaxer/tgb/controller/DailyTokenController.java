package org.ajaxer.tgb.controller;

import lombok.RequiredArgsConstructor;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.services.DailyTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@RestController
@RequestMapping("/daily-tokens")
@RequiredArgsConstructor
public class DailyTokenController
{
	final private DailyTokenService dailyTokenService;

	@GetMapping("/available")
	public ResponseDto isRewardAvailable()
	{
		return dailyTokenService.isRewardAvailable();
	}

	@PostMapping("/claim")
	public ResponseDto claim()
	{
		return dailyTokenService.claim();
	}
}
