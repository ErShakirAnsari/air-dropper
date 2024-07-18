package org.ajaxer.tgb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@NoRepositoryBean
public interface IJpaRepository<T> extends JpaRepository<T, Long>
{
}
