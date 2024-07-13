package org.ajaxer.tgb.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.ajaxer.tgb.dto.ActiveStatus;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Data
@MappedSuperclass
public class AbstractEntity implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "active_status", nullable = false)
	private int activeStatus;

	@Column(name = "created_on", nullable = false)
	private Timestamp createdOn;

	@PrePersist
	public void prePersist()
	{
		this.activeStatus = ActiveStatus.active;
		this.createdOn = new Timestamp(System.currentTimeMillis());
	}
}
