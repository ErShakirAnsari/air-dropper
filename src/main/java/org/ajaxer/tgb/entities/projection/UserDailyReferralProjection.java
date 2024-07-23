package org.ajaxer.tgb.entities.projection;

import org.ajaxer.tgb.entities.User;

import java.util.Date;

/**
 * @author Shakir Ansari
 * @since 2024-07-23
 */
public interface UserDailyReferralProjection
{
	User getCreatedBy();

	Date getCreatedOn();

	Integer getTotalTokens();
}
