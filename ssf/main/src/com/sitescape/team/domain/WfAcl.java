/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
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
