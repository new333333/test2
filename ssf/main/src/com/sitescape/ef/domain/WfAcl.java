package com.sitescape.ef.domain;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.util.StringUtil;

/**
 * Helper class for workflow Acl descriptions
 */
public class WfAcl {
	boolean creator=false;
	boolean useDefault=true;
	Set ids;
	
	public WfAcl() {
	}
	public void setPrincipals(Set ids) {
		this.ids = ids;
	}
	public void setPrincipals(String stringIds) {
		String [] result = StringUtil.split(stringIds);
		this.ids = new HashSet();
		for (int i=0; i<result.length; ++i) {
			try {
				this.ids.add(Long.getLong(result[i]));
			} catch (Exception e) {}

		}
	}
	public Set getPrincipals() {
		if (ids == null) ids = new HashSet();
		return ids;
	}
	public void setCreator(boolean creator) {
		this.creator = creator;
	}
	public boolean isCreator() {
		return creator;
	}
	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}
	public boolean isUseDefault() {
		return useDefault;
	}	
}
