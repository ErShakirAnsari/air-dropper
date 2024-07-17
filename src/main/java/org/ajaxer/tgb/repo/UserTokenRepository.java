package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long>
{
}
