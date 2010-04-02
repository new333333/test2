package org.kablink.teaming.gwt.client.profile;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Nathan Jensen
 *
 */
public class UserStatus implements IsSerializable{
	
	private String status;
	private Date modifyDate;
	private Long miniBlogId;
	
	public UserStatus() {};
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public void setMiniBlogId(Long miniBlogId) {
		this.miniBlogId = miniBlogId;
	}

	public Long getMiniBlogId() {
		return miniBlogId;
	}
}
