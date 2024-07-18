package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.TelegramRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-17
 */
@Repository
public interface TelegramRequestRepo extends IJpaRepository<TelegramRequest>
{
	List<TelegramRequest> findAllByStepOrderByCreatedOn(char step);
}
