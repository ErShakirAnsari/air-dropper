package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.User;
import org.ajaxer.tgb.entities.UserDailyToken;
import org.springframework.stereotype.Repository;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Repository
public interface UserDailyTokenRepository extends IJpaRepository<UserDailyToken>
{
	UserDailyToken findByCreatedBy(User createdBy);
}
