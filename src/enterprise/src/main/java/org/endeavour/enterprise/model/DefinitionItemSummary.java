package org.endeavour.enterprise.model;

import java.util.UUID;

public class DefinitionItemSummary {
	private UUID itemUuid;
	private String title;
	private DefinitionItemType type;

	public UUID getItemUuid() {
		return itemUuid;
	}

	public void setItemUuid(UUID itemUuid) {
		this.itemUuid = itemUuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DefinitionItemType getType() {
		return type;
	}

	public void setType(DefinitionItemType type) {
		this.type = type;
	}

}
