package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.entities.User;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@NoRepositoryBean
public interface ICreatorJPARepository<T> extends IJpaRepository<T>
{
	T findByCreatedBy(User createdBy);

	List<T> findTop100ByCreatedByOrderByCreatedOnDesc(User createdBy);
}
