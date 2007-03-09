package com.sitescape.team.domain;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.util.StringUtil;

/**
 * Helper class for workflow Acl descriptions
 */
public class WfAcl {
	private boolean useDefault=true;
	private Set ids;
	private AccessType type;
    public enum AccessType {
		read ,
		write,
		modify, 
		delete, 
		transitionIn, 
		transitionOut;
	};
	public WfAcl(AccessType type) {
		this.type = type;
	}
	public AccessType getType() {
		return type;
	}
	public void setPrincipals(Set ids) {
		this.ids = ids;
	}
	public void setPrincipals(String stringIds) {
		String [] result = StringUtil.split(stringIds);
		this.ids = new HashSet();
		for (int i=0; i<result.length; ++i) {
			try {
				this.ids.add(Long.valueOf(result[i]));
			} catch (Exception e) {}

		}
	}
	public Set getPrincipals() {
		if (ids == null) ids = new HashSet();
		return ids;
	}
	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}
	public boolean isUseDefault() {
		return useDefault;
	}	
}
