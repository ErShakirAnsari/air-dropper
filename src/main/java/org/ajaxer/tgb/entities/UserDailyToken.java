package org.ajaxer.tgb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "user_daily_tokens")
public class UserDailyToken extends AbstractCreatorEntity
{
	@Column(name = "last_claimed_on", nullable = false)
	private Timestamp lastClaimedOn;
}
