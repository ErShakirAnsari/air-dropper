package org.ajaxer.tgb.entities;

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
@Table(name = "managed_channels_groups")
public class ManagedChannelGroup extends AbstractEntity
{
}
