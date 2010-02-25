/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo.BinderType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.util.AllModulesInjected;


/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
public class GwtServerHelper {
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtServerHelper() {
	}

	/**
	 * Builds a TreeInfo object for a given Binder.
	 *
	 * @param bs
	 * @param binder
	 * @param processChildren
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(AllModulesInjected bs, Binder binder, boolean processChildren) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderId(binder.getId());
		reply.setBinderTitle(binder.getTitle());
		reply.setBinderChildren(binder.getBinderCount());
		if      (binder instanceof Workspace) reply.setBinderType(BinderType.WORKSPACE);
		else if (binder instanceof Folder)    reply.setBinderType(BinderType.FOLDER);

		// Do we need to add the Binder's children? 
		if (processChildren) {
			// Yes!  Scan them...
			List<TreeInfo> childTIList = reply.getChildBindersList(); 
			List<Binder> childBinderList = GwtServerHelper.getVisibleBinderDecendents(bs, binder);
			for (Iterator<Binder> bi = childBinderList.iterator(); bi.hasNext(); ) {
				// ...creating a TreeInfo for each.
				Binder subBinder = bi.next();
				TreeInfo subWsTI = GwtServerHelper.buildTreeInfoFromBinder(bs, subBinder, false);
				childTIList.add(subWsTI);
			}
			
			// ...and update the count of Binder children as it may
			// ...have changed based on visibility.
			reply.setBinderChildren(childBinderList.size());
		}
		
		// If we get here, reply refers to the TreeInfo object for this
		// Binder.  Return it.
		return reply;
	}
	
	/**
	 * Returns the User object of the currently logged in user.
	 * @return
	 */
	public static User getCurrentUser() {
		return RequestContextHolder.getRequestContext().getUser();
	}

	/**
	 * Returns a List<Binder> of the child Binder's of binder that are
	 * visible to the user.
	 * 
	 * Note:  For a way to do this using the search index, start with
	 *    TrashHelper.containsVisibleBinders().
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Binder> getVisibleBinderDecendents(AllModulesInjected bs, Binder binder) {
		ArrayList<Binder> reply = new ArrayList<Binder>();
		
		List allSubBinders = binder.getBinders();
		for (Iterator bi = allSubBinders.iterator(); bi.hasNext(); ) {
			Binder subBinder = ((Binder) bi.next());
			if (bs.getBinderModule().testAccess(subBinder, BinderOperation.readEntries)) {
				reply.add(subBinder);
			}
		}
		
		return reply;
	}
}
