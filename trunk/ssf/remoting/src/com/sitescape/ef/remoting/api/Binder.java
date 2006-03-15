package com.sitescape.ef.remoting.api;

public class Binder {
	
	private long id;
	private String name;
	private String zoneName;
	private String type;
	private String title;
	private long parentBinderId;
	private String[] entryDefinitionIds;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getEntryDefinitionIds() {
		return entryDefinitionIds;
	}

	public void setEntryDefinitionIds(String[] entryDefinitionIds) {
		this.entryDefinitionIds = entryDefinitionIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParentBinderId() {
		return parentBinderId;
	}

	public void setParentBinderId(long parentBinderId) {
		this.parentBinderId = parentBinderId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
}
