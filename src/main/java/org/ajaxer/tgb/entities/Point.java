package org.ajaxer.tgb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "points")
public class Point extends AbstractEntity
{
	@Column(name = "points")
	private long points;

	@Column(name = "description")
	private String description;
}
