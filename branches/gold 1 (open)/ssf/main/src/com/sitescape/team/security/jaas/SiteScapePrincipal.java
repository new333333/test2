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
package com.sitescape.team.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class SiteScapePrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	public SiteScapePrincipal() {
		this.name = "";
	}
	
	public SiteScapePrincipal(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
    public int hashCode() {
        return getName().hashCode();
    }
    
    public String toString() {
        return getName();
    }
    
    public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this == obj)
			return true;

		if (obj instanceof SiteScapePrincipal) {
			SiteScapePrincipal ssPrincipal = (SiteScapePrincipal)obj;

			if (ssPrincipal.getName().equals(this.name)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
    }
}