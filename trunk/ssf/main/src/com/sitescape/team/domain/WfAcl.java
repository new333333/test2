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
	public void addPrincipalId(Long id) {
		if (this.ids == null) this.ids = new HashSet();
		this.ids.add(id);
	}
	public void addPrincipalIds(Set<Long> ids) {
		if (this.ids == null) this.ids = new HashSet();
		this.ids.addAll(ids);
	}
	public Set getPrincipalIds() {
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
