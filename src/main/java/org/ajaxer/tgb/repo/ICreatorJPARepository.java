package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.User;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@NoRepositoryBean
public interface ICreatorJPARepository<T> extends IJpaRepository<T>
{
	T findByCreatedBy(User createdBy);
}
