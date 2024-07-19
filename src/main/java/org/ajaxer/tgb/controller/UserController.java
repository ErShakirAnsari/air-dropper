package org.ajaxer.tgb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajaxer.simple.utils.dtos.ResponseDto;
import org.ajaxer.tgb.dto.UserSyncRequestDto;
import org.ajaxer.tgb.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shakir Ansari
 * @since 2024-07-19
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
	final UserService userService;

	@PostMapping("/sync")
	public ResponseDto sync(@RequestBody UserSyncRequestDto userSyncRequestDto)
	{
		return userService.sync(userSyncRequestDto);
	}
}
