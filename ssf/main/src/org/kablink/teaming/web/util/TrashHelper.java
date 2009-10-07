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
package org.kablink.teaming.web.util;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;

public class TrashHelper {
	public static final String[] trashColumns= new String[] {"title", "date", "author", "location"};
	
	/**
	 * When required, builds the Trash toolbar for a binder.
	 * 
	 * @param binderId
	 * @param model
	 * @param qualifiers
	 * @param trashToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildTrashToolbar(String binderId, Map model, Map qualifiers, Toolbar trashToolbar) {
//!		...when do we NOT want to show access to the trash?...
		
		// Show the trash sidebar widget...
		model.put(WebKeys.TOOLBAR_TRASH_SHOW, Boolean.TRUE);
		
		// ...and add trash to the menu bar.
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trash"));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.trashMenu");
		qualifiers.put("icon", "trash.png");
		qualifiers.put("iconFloatRight", "true");
		qualifiers.put("onClick", "ss_treeShowId('"+binderId+"',this,'view_folder_listing','&showTrash=true');return false;");
		trashToolbar.addToolbarMenu("1_trash", NLT.get("toolbar.menu.trash"), "javascript: //;", qualifiers);	
	}

	/**
	 * Build the menu bar within the trash viewer.
	 * 
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	public static void buildTrashViewToolbar(Map model) {
		Toolbar trashViewToolbar = new Toolbar();

		Map qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashRestore"));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.trashRestore");
		qualifiers.put("onClick", "ss_trashRestore();return false;");
		trashViewToolbar.addToolbarMenu("1_trashRestore", NLT.get("toolbar.menu.trash.restore"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashPurge"));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.trashPurge");
		qualifiers.put("onClick", "ss_trashPurge();return false;");
		trashViewToolbar.addToolbarMenu("2_trashPurge", NLT.get("toolbar.menu.trash.purge"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashRestoreAll"));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.trashRestoreAll");
		qualifiers.put("onClick", "ss_trashRestoreAll();return false;");
		trashViewToolbar.addToolbarMenu("3_trashRestoreAll", NLT.get("toolbar.menu.trash.restoreAll"), "javascript: //;", qualifiers);	
		
		qualifiers = new HashMap();
		qualifiers.put("title", NLT.get("toolbar.menu.title.trashPurgeAll"));
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.trashPurgeAll");
		qualifiers.put("onClick", "ss_trashPurgeAll();return false;");
		trashViewToolbar.addToolbarMenu("4_trashPurgeAll", NLT.get("toolbar.menu.trash.purgeAll"), "javascript: //;", qualifiers);	
		
		model.put(WebKeys.TRASH_VIEW_TOOLBAR, trashViewToolbar.getToolbar());
	}

	/**
	 * Builds the beans for display the trash from either a folder or
	 * workspace binder.
	 *  
	 * @param bs
	 * @param request
	 * @param response
	 * @param binderId
	 * @param zoneUUID
	 * @param model
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map buildTrashBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId, Map model) throws Exception{
		// Access the binder...
		Binder binder = bs.getBinderModule().getBinder(binderId);
		
		// ...store the tabs...
		Tabs.TabEntry tab= BinderHelper.initTabs(request, binder);
		model.put(WebKeys.TABS, tab.getTabs());

		
		// Access the user properties... 
		UserProperties userProperties       = ((UserProperties) model.get(WebKeys.USER_PROPERTIES_OBJ));
		UserProperties userFolderProperties = ((UserProperties) model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ));

		// ...initialize the page counts and sort order.
		Map options = ListFolderHelper.getSearchFilter(bs, request, binder, userFolderProperties, true);
		ListFolderHelper.initPageCounts(bs, request, userProperties.getProperties(), tab, options);
		BinderHelper.initSortOrder(bs, userFolderProperties, options);
		return options;
	}
	
	/**
	 * Returns the trash entries for the given binder.
	 * 
	 * @param bs
	 * @param binder
	 * @param options
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map getTrashEntries(AllModulesInjected bs, Binder binder, Map options) {
//!		...this needs to be implemented...
		Map trashEntries = null;
		if (binder instanceof Folder) {
			Folder folder = ((Folder) binder);
			Long folderId = folder.getId();
			FolderModule fm = bs.getFolderModule();
			trashEntries = fm.getEntries(folderId, options);
		}
		else if (binder instanceof Workspace) {
			Workspace ws = ((Workspace) binder);
			BinderModule bm = bs.getBinderModule();
			trashEntries = bm.getBinders(ws, options);
		}
		return trashEntries;
	}
}
