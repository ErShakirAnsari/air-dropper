package org.ajaxer.tgb.entities;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class AbstractCreatorEntity extends AbstractEntity
{
	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false, updatable = false)
	private User createdBy;

	@PrePersist
	public void prePersist()
	{
		super.prePersist();
	}
}
