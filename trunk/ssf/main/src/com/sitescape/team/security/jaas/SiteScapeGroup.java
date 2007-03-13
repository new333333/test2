package com.sitescape.team.security.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SiteScapeGroup extends SiteScapePrincipal implements Group {
	
	private static final long serialVersionUID = 1L;
	
	private Map members = new HashMap();

	public SiteScapeGroup(String groupName) {
		super(groupName);
	}

	public boolean addMember(Principal user) {
		if (!this.members.containsKey(user)) {
			this.members.put(user, user);

			return true;
		}
		else {
			return false;
		}
	}

	public boolean isMember(Principal member) {
		boolean isMember = this.members.containsKey(member);

		if (!isMember) {
			Iterator itr = this.members.values().iterator();

			while (!isMember && itr.hasNext()) {
				Object obj = itr.next();

				if (obj instanceof Group) {
					Group group = (Group)obj;

					isMember = group.isMember(member);
				}
			}
		}

		return isMember;
	}

	public Enumeration members() {
		return Collections.enumeration(this.members.values());
	}

	public boolean removeMember(Principal user) {
		Object obj = this.members.remove(user);

		if (obj != null) {
			return true;
		}
		else {
			return false;
		}
	}
}
