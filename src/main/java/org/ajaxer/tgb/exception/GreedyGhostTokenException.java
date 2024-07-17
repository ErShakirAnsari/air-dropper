package org.ajaxer.tgb.exception;

import org.ajaxer.simple.utils.exceptions.SimpleException;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
public class GreedyGhostTokenException extends SimpleException
{
	public GreedyGhostTokenException()
	{
	}

	public GreedyGhostTokenException(String message)
	{
		super(message);
	}

	public GreedyGhostTokenException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public GreedyGhostTokenException(Throwable cause)
	{
		super(cause);
	}

	public GreedyGhostTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
