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
		TEAM,
		USER,
		
		OTHER,
		NOT_A_WORKSPACE,
	}
	
	private ArrayList<TreeInfo> m_childBindersAL = new ArrayList<TreeInfo>();
	private BinderType m_binderType = BinderType.OTHER;
	private FolderType m_folderType = FolderType.NOT_A_FOLDER;
	private int m_binderChildren = 0;
	private String m_binderId;
	private String m_binderTitle = "";
	private WorkspaceType m_wsType = WorkspaceType.NOT_A_WORKSPACE;

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TreeInfo() {
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
	 * Returns the Binder ID for the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderId() {
		return m_binderId;
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
	 * If this TreeInfo object refers to a Folder, returns its type.
	 * 
	 * @return
	 */
	public FolderType getFolderType() {
		return ((BinderType.FOLDER == m_binderType) ? m_folderType : FolderType.NOT_A_FOLDER);
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
	 * If this TreeInfo object refers to a Workspace, returns its type.
	 * 
	 * @return
	 */
	public WorkspaceType getWorkspaceType() {
		return ((BinderType.WORKSPACE == m_binderType) ? m_wsType : WorkspaceType.NOT_A_WORKSPACE);
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
	 * @param targetPanel
	 */
	public void render(FlowPanel targetPanel) {
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
	 * Store a count of the children of a Binder.
	 * 
	 * @param binderChildren
	 */
	public void setBinderChildren(int binderChildren) {
		m_binderChildren = binderChildren;
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
