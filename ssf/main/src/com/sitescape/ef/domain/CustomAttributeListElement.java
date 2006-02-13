
package com.sitescape.ef.domain;

/**
 * @hibernate.subclass discriminator-value="L" dynamic-update="true" lazy="false"
 * @author Janet McCann
 *
 */
public class CustomAttributeListElement extends CustomAttribute {
	private CustomAttribute parent;
	protected CustomAttributeListElement() {
	}
	protected CustomAttributeListElement(String name, CustomAttribute parent, Entry owner) {
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
