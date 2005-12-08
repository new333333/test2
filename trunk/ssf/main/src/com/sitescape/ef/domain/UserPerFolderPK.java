
package com.sitescape.ef.domain;

import java.io.Serializable;

/**
 * @author Janet McCann
 *
 */
public class UserPerFolderPK implements Serializable {
	private int serializableVersion=1;
	private Long folderId, principalId;
	public UserPerFolderPK() {
		
	}
	public UserPerFolderPK(Long principalId, Long folderId) {
		this.principalId = principalId;
		this.folderId = folderId;
	}
	/**
 	 * @hibernate.key-property position="1"
 	 */
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}
	/**
 	 * @hibernate.key-property position="2"
 	 */
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof UserPerFolderPK) {
			UserPerFolderPK seen = (UserPerFolderPK) obj;
			if (seen.getPrincipalId().equals(principalId) && 
				seen.getFolderId().equals(folderId)) return true;
		}
		return false;
	}
	
}

