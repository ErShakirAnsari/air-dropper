package org.ajaxer.tgb.entities;

import jakarta.persistence.*;
import lombok.*;
import org.ajaxer.tgb.constants.JobStep;

/**
 * @author Shakir
 * @version 2022-12-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tgb_requests")
public class TelegramRequest extends AbstractEntity
{
	/*@Column(name = "uuid", nullable = false, unique = true)
	private String uuid;*/

	@Column(name = "step", nullable = false)
	private char step;

	@Lob //long text
	@Column(name = "request", nullable = false, columnDefinition = "TEXT")
	private String request;


	@PrePersist
	public void prePersist()
	{
		super.prePersist();
		this.step = JobStep.QUEUE.getCode();
		//this.uuid = UUID.randomUUID().toString();
	}
}
