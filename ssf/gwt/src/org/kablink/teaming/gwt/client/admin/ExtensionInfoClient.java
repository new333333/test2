package org.kablink.teaming.gwt.client.admin;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExtensionInfoClient implements IsSerializable {
	
	private String id;
	private String name;
	private String description;
	private Long zoneId;

	public ExtensionInfoClient(){
		
	}

	public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	
	public Long getZoneId(){
		return zoneId;
	}
	
	
}
