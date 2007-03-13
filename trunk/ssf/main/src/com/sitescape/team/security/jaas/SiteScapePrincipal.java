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