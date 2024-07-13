package org.ajaxer.tgb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Shakir
 * @version 2022-12-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
public class User extends AbstractEntity
{
	@Column(name = "telegram_user_id", nullable = false)
	private long telegramUserId;

	@Column(name = "username")
	private String telegramUsername;

	@Column(name = "firstname", nullable = false)
	private String telegramFirstname;

	@Column(name = "lastname")
	private String telegramLastname;

	/**
	 * referral code will be @{{@link User#telegramUserId}}
	 */
	@Column(name = "referred_by")
	private String referred_by;

	@PrePersist
	public void prePersist()
	{
		super.prePersist();
	}
}
