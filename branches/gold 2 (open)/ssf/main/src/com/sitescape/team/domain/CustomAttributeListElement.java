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
	private Integer position=null;
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
	/**
	 * @hibernate.property
	 * We are adding list semantics after the fact, so instead of changeing everything to use hibernate lists we will manage
	 * list ordering ourselves.
	 * Users better be careful or things could change from sets to lists and back arbitrarily
	 */
	protected Integer getPosition() {
		return position;
	}
	protected void setPosition(Integer position) {
		this.position = position;
	}
 

}
