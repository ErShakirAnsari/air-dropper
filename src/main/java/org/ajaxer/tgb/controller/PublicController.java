package org.ajaxer.tgb.controller;

import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@RestController
@RequestMapping("/public")
public class PublicController
{
	@GetMapping("/health")
	public ResponseDto getDailyRewards()
	{
		return new ResponseDto(true).addParam("date", new Date());
	}
}
