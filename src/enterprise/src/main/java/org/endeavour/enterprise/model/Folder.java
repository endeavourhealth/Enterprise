package org.endeavour.enterprise.model;

import java.util.UUID;

public class Folder {
	private UUID itemUuid;
	private String title;
	private boolean hasChildren;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UUID getItemUuid() {
		return itemUuid;
	}

	public void setItemUuid(UUID itemUuid) {
		this.itemUuid = itemUuid;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

}
