/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.domain;

/**
 * An element in a set of {@link org.kablink.teaming.domain.CustomAttribute CustomAttribute} values.
 * @hibernate.subclass discriminator-value="L" dynamic-update="true" lazy="false"
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
