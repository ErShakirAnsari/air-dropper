package org.ajaxer.tgb.constants;

import lombok.Getter;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Getter
public enum JobStep
{
	QUEUE('Q'),
	WORK_IN_PROGRESS('W'),
	DONE('D'),
	ERROR('E'),

	INACTIVE('I'),
	ABORTED('A');

	final private char code;

	JobStep(char code)
	{
		this.code = code;
	}
}
