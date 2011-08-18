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
package org.kablink.teaming.web.tree;
import java.util.List;

import org.dom4j.Element;

public interface DomTreeBuilder {
	public static final int TYPE_WORKSPACE=1;
	public static final int TYPE_FOLDER=2;
	public static final int TYPE_PEOPLE=3;
	public static final int TYPE_FAVORITES=4;
	public static final int TYPE_TEMPLATE=5;
	public static final int TYPE_SKIPLIST=6;

	public static final String SKIP_TUPLE="tuple";
	public static final String SKIP_PAGE="page";
	public static final String SKIP_BINDER_ID="binderId";
	
	public static final String PAGE_DELIMITER="//";

	public static final String NODE_ROOT="root";
	public static final String NODE_CHILD="child";
	public static final String NODE_TYPE_WORKSPACE="workspace";
	public static final String NODE_TYPE_FOLDER="folder";
	public static final String NODE_TYPE_PEOPLE="people";
	public static final String NODE_TYPE_FAVORITES="favorites";
	public static final String NODE_TYPE_TEMPLATE="template";
	public static final String NODE_TYPE_RANGE="range";
	
	public Element setupDomElement(int type, Object source, Element element);
	public boolean supportsType(int type, Object source);
	public String getPage();
	public void setPage(String page);
	public List getTuple();

}
