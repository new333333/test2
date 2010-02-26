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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.RequestInfo;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used to communicate workspace tree information between the
 * client (i.e., the WorkspaceTreeControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTreeInfo().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeInfo implements IsSerializable {
	private ArrayList<TreeInfo> m_childBindersAL = new ArrayList<TreeInfo>();
	private BinderType m_binderType = BinderType.OTHER;
	private boolean m_binderExpanded;
	private FolderType m_folderType = FolderType.NOT_A_FOLDER;
	private int m_binderChildren = 0;
	private String m_binderIconName;
	private String m_binderId;
	private String m_binderTitle = "";
	private String m_binderPermalink = "";
	private WorkspaceType m_wsType = WorkspaceType.NOT_A_WORKSPACE;

	/**
	 * The type of Binder referenced by this TreeInfo object.  
	 */
	public enum BinderType implements IsSerializable {
		FOLDER,
		WORKSPACE,
		
		OTHER,
	}
	
	/**
	 * If the referenced Binder is a Folder, the type of Folder
	 * referenced by this TreeInfo object.  
	 */
	public enum FolderType implements IsSerializable {
		BLOG,
		CALENDAR,
		DISCUSSION,
		FILE,
		MINIBLOG,
		PHOTOALBUM,
		TASK,
		TRASH,
		SURVEY,
		WIKI,
		
		OTHER,
		NOT_A_FOLDER,
	}
	
	/**
	 * If the referenced Binder is a Workspace, the type of Workspace
	 * referenced by this TreeInfo object.  
	 */
	public enum WorkspaceType implements IsSerializable {
		GLOBAL_ROOT,
		PROFILE_ROOT,
		TEAM,
		TEAM_ROOT,
		TOP,
		USER,
		
		OTHER,
		NOT_A_WORKSPACE,
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TreeInfo() {
		// Nothing to do.
	}
	
	/**
	 * Returns the number of children in the Binder corresponding to
	 * this TreeInfo object.
	 * 
	 * @return
	 */
	public int getBinderChildren() {
		return m_binderChildren;
	}

	/**
	 * Returns the name of the Binder icon for the Binder corresponding
	 * to this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderIconName() {
		return m_binderIconName;
	}

	/**
	 * Returns the Binder ID for the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderId() {
		return m_binderId;
	}

	/**
	 * Returns the permalink to the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderPermalink() {
		return m_binderPermalink;
	}

	/**
	 * Returns the title of the Binder corresponding to this TreeInfo
	 * object.
	 * 
	 * @return
	 */
	public String getBinderTitle() {
		return m_binderTitle;
	}

	/**
	 * Returns the type of Binder this TreeInfo object refers
	 * to.
	 * 
	 * @return
	 */
	public BinderType getBinderType() {
		return m_binderType;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder.
	 * 
	 * @return
	 */
	public ImageResource getBinderImage() {
//!		...this needs to be implemented...
		
		ImageResource reply = null;
		GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
		switch (getBinderType()) {
		case FOLDER:
			switch (getFolderType()) {
			case BLOG:        reply = images.folder_comment();  break;
			case CALENDAR:    reply = images.folder_calendar(); break;
			case DISCUSSION:  reply = images.folder_comment();  break;
			case FILE:        reply = images.folder_file();     break;
			case MINIBLOG:    reply = images.folder_comment();  break;
			case PHOTOALBUM:  reply = images.folder_photo();    break;
			case TASK:        reply = images.folder_task();     break;
			case TRASH:       reply = images.folder_trash();    break;
			case SURVEY:                                        break;
			case WIKI:                                          break;
			case OTHER:                                         break;
			}
		case WORKSPACE:
			switch (getWorkspaceType()) {
			case TEAM:   break;
			case USER:   break;
			case OTHER:  break;
			}
		}
		
		return reply;
	}
	
	/**
	 * Returns the List<TreeInfo> of the Binder's contained in the
	 * Binder corresponding to this TreeInfo.
	 * 
	 * @return
	 */
	public List<TreeInfo> getChildBindersList() {
		return m_childBindersAL;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display for the
	 * expander next to the Binder.  If no expander should be shown,
	 * null is returned.
	 * 
	 * @return
	 */
	public ImageResource getExpanderImage() {
		ImageResource reply = null;
		
		if (0 < getBinderChildren()) {
			GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
			if (isBinderExpanded())
			     reply = images.tree_closer();
			else reply = images.tree_opener();
		}

		return reply;
	}
	
	/**
	 * If this TreeInfo object refers to a Folder, returns its type.
	 * 
	 * @return
	 */
	public FolderType getFolderType() {
		return ((BinderType.FOLDER == m_binderType) ? m_folderType : FolderType.NOT_A_FOLDER);
	}

	/**
	 * If this TreeInfo object refers to a Workspace, returns its type.
	 * 
	 * @return
	 */
	public WorkspaceType getWorkspaceType() {
		return ((BinderType.WORKSPACE == m_binderType) ? m_wsType : WorkspaceType.NOT_A_WORKSPACE);
	}

	/**
	 * Returns the the Binder corresponding to this TreeInfo object
	 * should be expanded and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderExpanded() {
		return m_binderExpanded;
	}

	/**
	 * Returns true of this TreeInfo object refers to a Binder that's a
	 * Folder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderFolder() {
		return (BinderType.FOLDER == m_binderType);
	}
	
	/**
	 * Returns true of this TreeInfo object refers to a Binder that's a
	 * Workspace and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderWorkspace() {
		return (BinderType.WORKSPACE == m_binderType);
	}
	
	/**
	 * Called to render the information in this TreeInfo object into a
	 * FlowPanel.
	 *
	 * @param ri
	 * @param targetPanel
	 */
	public void render(RequestInfo ri, FlowPanel targetPanel) {
		Label label = new Label(getBinderTitle());
		label.addStyleName( "workspaceTreeControlHeader" );
		targetPanel.add( label );
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName( "workspaceTreeControlBody" );
		
		for (Iterator<TreeInfo> tii = getChildBindersList().iterator(); tii.hasNext(); ) {
			TreeInfo ti = tii.next();
			label = new Label(ti.getBinderTitle());
			panel.add( label );
		}
		
		targetPanel.add( panel );
	}

	/**
	 * Store a count of the children of a Binder.
	 * 
	 * @param binderChildren
	 */
	public void setBinderChildren(int binderChildren) {
		m_binderChildren = binderChildren;
	}
	
	/**
	 * Stores the name of the icon for the Binder.
	 * 
	 * @param binderIconName
	 */
	public void setBinderIconName(String binderIconName) {
		m_binderIconName = binderIconName;
		
	}
	
	/**
	 * Stores the ID of a Binder.
	 * 
	 * @param binderId
	 */
	public void setBinderId(String binderId) {
		m_binderId = binderId;
		
	}
	public void setBinderId(Long binderId) {
		setBinderId(String.valueOf(binderId));
	}

	/**
	 * Stores whether the Binder should be expanded.
	 * 
	 * @param binderExpanded
	 */
	public void setBinderExpanded(boolean binderExpanded) {
		m_binderExpanded = binderExpanded;
	}

	/**
	 * Stores a Binder's permalink in this TreeInfo object.
	 * 
	 * @param binderPermalink
	 */
	public void setBinderPermalink(String binderPermalink) {
		m_binderPermalink = binderPermalink;
	}

	/**
	 * Stores a Binder's title in this TreeInfo object.
	 * 
	 * @param binderTitle
	 */
	public void setBinderTitle(String binderTitle) {
		m_binderTitle = binderTitle;
	}

	/**
	 * Stores the type of Binder referenced by this TreeInfo object.
	 * 
	 * @param binderType
	 */
	public void setBinderType(BinderType binderType) {
		// Store the BinderType...
		m_binderType = binderType;
		
		// ...and reset the FolderType and WorkspaceType.
		if      (m_binderType == BinderType.FOLDER)    {m_folderType = FolderType.OTHER;        m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
		else if (m_binderType == BinderType.WORKSPACE) {m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.OTHER;}
		else                                           {m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
	}
	
	/**
	 * Stores the type of Folder referenced by this TreeInfo object, if
	 * it references a Folder.
	 * 
	 * @param folderType
	 */
	public void setFolderType(FolderType folderType) {
		// Validate the FolderType for the BinderType...
		if (isBinderFolder()) {
			if (FolderType.NOT_A_FOLDER == folderType) {
				folderType = FolderType.OTHER;
			}
		}
		else {
			folderType = FolderType.NOT_A_FOLDER;
		}
		
		// ...and store it.
		m_folderType = folderType;
	}

	/**
	 * Stores the type of Workspace referenced by this TreeInfo object,
	 * if it references a Workspace.
	 * 
	 * @param wsType
	 */
	public void setWorkspaceType(WorkspaceType wsType) {
		// Validate the WorkspaceType for the BinderType...
		if (isBinderWorkspace()) {
			if (WorkspaceType.NOT_A_WORKSPACE == wsType) {
				wsType = WorkspaceType.OTHER;
			}
		}
		else {
			wsType = WorkspaceType.NOT_A_WORKSPACE;
		}
		
		// ...and store it.
		m_wsType = wsType;
	}
}
