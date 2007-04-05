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

/**
 * @hibernate.subclass discriminator-value="L" dynamic-update="true" lazy="false"
 * @author Janet McCann
 *
 */
public class CustomAttributeListElement extends CustomAttribute {
	private CustomAttribute parent;
	protected CustomAttributeListElement() {
	}
	protected CustomAttributeListElement(String name, CustomAttribute parent, DefinableEntity owner) {
		setName(name);
		setParent(parent);
		//don't set foreign key, so not read in to map
		setOwner(new AnyOwner(owner, false));
	}
	/**
	 * @hibernate.many-to-one
     * @hibernate.column name="parent" sql-type="char(32)"
	 * @return
	 */
	public CustomAttribute getParent() {
		return parent;
	}
	protected void setParent(CustomAttribute parent) {
		this.parent = parent;
	}
	
}
