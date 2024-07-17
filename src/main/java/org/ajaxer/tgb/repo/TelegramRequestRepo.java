package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.TelegramRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Repository
public interface TelegramRequestRepo extends JpaRepository<TelegramRequest, Integer>
{
	List<TelegramRequest> findAllByStepOrderByCreatedOn(char step);
}
