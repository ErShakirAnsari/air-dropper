package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	Optional<User> findByTelegramUserId(Long userId);
}