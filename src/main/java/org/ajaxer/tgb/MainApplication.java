package org.ajaxer.tgb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@EnableTransactionManagement
@SpringBootApplication
public class MainApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(MainApplication.class, args);
	}
}
