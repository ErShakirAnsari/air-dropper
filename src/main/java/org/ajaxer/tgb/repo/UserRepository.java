package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Repository
public interface UserRepository extends IJpaRepository<User>
{
	Optional<User> findByTelegramUserId(Long userId);

	List<User> findAllByReferredByOrderByIdDesc(String telegramUserId);
}
