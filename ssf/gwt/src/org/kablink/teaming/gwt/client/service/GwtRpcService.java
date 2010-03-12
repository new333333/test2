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
package org.kablink.teaming.gwt.client.service;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.util.TreeInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * This interface defines the methods that can be called when we want to make a remote
 * procedure call.
 * 
 * @author jwootton
 */
@RemoteServiceRelativePath("gwtTeaming.rpc")
public interface GwtRpcService extends RemoteService
{
	// Do a search given the criteria found in the GwtSearchCriteria object.
	public GwtSearchResults executeSearch( GwtSearchCriteria searchCriteria ) throws Exception;
	
	// Return a GwtBrandingData object for the given binder.
	public GwtBrandingData getBinderBrandingData( String binderId ) throws GwtTeamingException;
	
	// Return a GwtBrandingData object for the corporate branding.
	public GwtBrandingData getCorporateBrandingData();
	
	// Return an Entry object for the given entry id.
	public GwtFolderEntry getEntry( String zoneUUID, String entryId ) throws GwtTeamingException;
	
	// Return a list of the names of the files that are attachments of the given binder.
	public ArrayList<String> getFileAttachments( String binderId ) throws GwtTeamingException;
	
	// Return a Folder object for the given folder id.
	public GwtFolder getFolder( String zoneUUID, String folderId ) throws GwtTeamingException;
	
	public String getTutorialPanelState();
	public ExtensionInfoClient[] getExtensionInfo();
	public ExtensionInfoClient[] removeExtension(String id) throws ExtensionDefinitionInUseException;
	public ExtensionFiles getExtensionFiles(String id, String zoneName);
	
	// Returns a permalink to the currently logged in user's workspace.
	public String getUserWorkspacePermalink();
	
	// The following are used in the implementation of the various
	// forms of the WorkspaceTreeControl.
	public List<TreeInfo> getHorizontalTree(  String binderId);
	public TreeInfo       getHorizontalNode(  String binderId);
	public TreeInfo       getVerticalTree(    String binderId);
	public TreeInfo       getVerticalNode(    String binderId);
	public Boolean        persistNodeCollapse(String binderId);
	public Boolean        persistNodeExpand(  String binderId);
	
	// Save the branding data to the given binder.
	public Boolean saveBrandingData( String binderId, GwtBrandingData brandingData ) throws GwtTeamingException;
}// end GwtRpcService
