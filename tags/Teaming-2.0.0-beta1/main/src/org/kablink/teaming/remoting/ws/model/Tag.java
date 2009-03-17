package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;


public class Tag implements Serializable {
	protected String id;
	protected String name="";
	protected boolean isPublic=false;
	private Long entityId;
	
   public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
	
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}	

	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
}
