package org.ajaxer.tgb.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ajaxer.tgb.constants.TokenDescription;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "user_token_history")
public class UserTokenHistory extends AbstractCreatorEntity
{
	@Column(name = "tokens")
	private long token;

	@Enumerated(EnumType.STRING)
	@Column(name = "description")
	private TokenDescription tokenDescription;
}
