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

import org.dom4j.Element;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.util.AllModulesInjected;


public class SearchTreeHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {return true;}
		return false;
	}
	public boolean hasChildren(AllModulesInjected bs, Object source, int type) {
		return ((Binder)source).getBinderCount() > 0;
	}

	public String getAction(int type, Object source) {
		//use action to indicate type of name that is choose.  This allows us to 
		//determine the name of the checkbox.  We use variable names so the
		//profile binder can show up as the parent of the users/groups and 
		//as the parent of user workspaces. 
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return "search";}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return "search";}
		if (type == DomTreeBuilder.TYPE_PEOPLE) {return "search";}
		
		return null;
	}
	public String getURL(int type, Object source) {return "";}
	public String getDisplayOnly(int type, Object source) {return "false";}
	//each name must be unqiue
	public String getTreeNameKey() {return "search";}
	public String getPage() {return "";}
	public void customize(AllModulesInjected bs, Object source, int type, Element element) {};
	
}

