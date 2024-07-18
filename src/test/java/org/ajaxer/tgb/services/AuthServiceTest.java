package org.ajaxer.tgb.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTest
{
	@Autowired
	private AuthService authService;

	private String tokenB64;

	@BeforeEach
	void setUp()
	{
		tokenB64 = "cXVlcnlfaWQ9QUFGeTFDc25BQUFBQUhMVUt5ZVhaT05uJnVzZXI9JTdCJTIyaWQlMjIlM0E2NTcxODM4NTglMkMlMjJmaXJzdF9uYW1lJTIyJTNBJTIyU2hha2lyJTIyJTJDJTIybGFzdF9uYW1lJTIyJTNBJTIyQW5zYXJpJTIyJTJDJTIydXNlcm5hbWUlMjIlM0ElMjJlcnNoYWtpcmFuc2FyaSUyMiUyQyUyMmxhbmd1YWdlX2NvZGUlMjIlM0ElMjJlbiUyMiUyQyUyMmFsbG93c193cml0ZV90b19wbSUyMiUzQXRydWUlN0QmYXV0aF9kYXRlPTE3MjEyOTI3NDUmaGFzaD1hZDg5ZDdmZmMyYzYwYmFiYWM4ZGY5NzliZmQzZGM1NzRlODRmMGJlN2RmYjgyNTFkNzhkNmM1YjUwMDdiZmEy";
	}

	@Test
	void shouldReturnTrue()
	{
		//Arrange

		//Action
		boolean validHash = authService.isValidTelegramToken(tokenB64);

		//Assert
		Assertions.assertThat(validHash).isTrue();
	}

	@Test
	void shouldReturnValidId()
	{
		//Arrange

		//Action
		Long id = authService.getUserId(tokenB64);

		//Assert
		Assertions.assertThat(id).isNotNull().isEqualTo(657183858);
	}
}