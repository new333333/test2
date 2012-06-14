/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about a Binder between the
 * client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.)
 * 
 * @author drfoster@novell.com
 */
public class BinderInfo implements IsSerializable, VibeRpcResponseData {
	private BinderType    	m_binderType               = BinderType.OTHER;				//
	private boolean       	m_binderDescExpanded       = true;							//
	private boolean       	m_binderDescHTML           = false;							//
	private boolean       	m_isLibrary                = false;							//
	private boolean       	m_mirroredDriverConfigured = false;							//
	private CollectionType	m_collectionType           = CollectionType.OTHER;			//
	private FolderType    	m_folderType               = FolderType.NOT_A_FOLDER;		//
	private Long          	m_numUnread                = 0L;							// Number of unread entries in this binder and sub binders.
	private String        	m_binderId                 = "";							//
	private String        	m_binderTitle              = "";							//
	private String		  	m_binderDesc	             = "";							//
	private String        	m_entityType               = "";							//
	private WorkspaceType 	m_wsType                   = WorkspaceType.NOT_A_WORKSPACE;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public BinderInfo() {
		// Nothing to do.
	}

	/**
	 * Returns a copy of this BinderInfo object.
	 * 
	 * @return
	 */
	public BinderInfo copyBinderInfo() {
		BinderInfo reply = new BinderInfo();
		reply.setBinderId(                m_binderId                );
		reply.setBinderTitle(             m_binderTitle             );
		reply.setBinderDesc(              m_binderDesc              );
		reply.setBinderDescExpanded(      m_binderDescExpanded      );
		reply.setBinderDescHTML(          m_binderDescHTML          );
		reply.setNumUnread(               m_numUnread               );
		reply.setEntityType(              m_entityType              );
		reply.setBinderType(              m_binderType              );
		reply.setCollectionType(          m_collectionType          );
		reply.setFolderType(              m_folderType              );
		reply.setIsLibrary(               m_isLibrary               );
		reply.setWorkspaceType(           m_wsType                  );
		reply.setMirroredDriverConfigured(m_mirroredDriverConfigured);
		return reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public BinderType     getBinderType()              {return                m_binderType;              }
	public boolean        isBinderDescExpanded()       {return                m_binderDescExpanded;      }
	public boolean        isBinderDescHTML()           {return                m_binderDescHTML;          }
	public boolean        isLibrary()                  {return                m_isLibrary;               }
	public boolean        isMirroredDriverConfigured() {return                m_mirroredDriverConfigured;}
	public CollectionType getCollectionType()          {return                m_collectionType;          }
	public FolderType     getFolderType()              {return                m_folderType;              }
	public Long           getBinderIdAsLong()          {return Long.parseLong(m_binderId);               }
	public String         getBinderId()                {return                m_binderId;                }
	public String         getBinderTitle()             {return                m_binderTitle;             }
	public String		  getBinderDesc()              {return                m_binderDesc;              }
	public Long           getNumUnread()               {return                m_numUnread;               }
	public String         getEntityType()              {return                m_entityType;              }
	public WorkspaceType  getWorkspaceType()           {return                m_wsType;                  }
	
	/**
	 * Returns true of this BinderInfo defines a Collection and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderCollection() {
		return (BinderType.COLLECTION == m_binderType);
	}

	/**
	 * Returns true of this BinderInfo defines a Folder and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderFolder() {
		return (BinderType.FOLDER == m_binderType);
	}

	/**
	 * Returns true if this BinderInfo defines the root profiles binder
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderProfilesRootWS() {
		return (isBinderWorkspace() && (WorkspaceType.PROFILE_ROOT == m_wsType));
	}
	
	/**
	 * Returns true if this BinderInfo refers to a trash Binder and
	 * false otherwise.
	 *
	 * @return
	 */
	public boolean isBinderTrash() {
		boolean reply;
		switch (m_binderType) {
		default:         reply = false;                                 break;
		case FOLDER:     reply = (FolderType.TRASH    == m_folderType); break;
		case WORKSPACE:  reply = (WorkspaceType.TRASH == m_wsType);     break;
		}
		return reply;
	}
	
	/**
	 * Returns true of this BinderInfo defines a Workspace and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderWorkspace() {
		return (BinderType.WORKSPACE == m_binderType);
	}

	/**
	 * Stores the description of a Binder.
	 */
	public void setBinderDesc( String binderDesc )
	{
		m_binderDesc = binderDesc;
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
	 * Stores the title of a Binder.
	 * 
	 * @param binderTitle
	 */
	public void setBinderTitle(String binderTitle) {
		m_binderTitle = binderTitle;
	}
	
	/**
	 * Stores the entity type of a Binder.
	 * 
	 * @param entityTYpe
	 */
	public void setEntityType(String entityType) {
		m_entityType = entityType;
	}
	
	/**
	 * Stores the type of Binder.
	 * 
	 * @param binderType
	 */
	public void setBinderType(BinderType binderType) {
		// Store the BinderType...
		m_binderType = binderType;
		
		// ...and reset the FolderType and WorkspaceType.
		if      (m_binderType == BinderType.FOLDER)     {m_collectionType = CollectionType.NOT_A_COLLECTION; m_folderType = FolderType.OTHER;        m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
		else if (m_binderType == BinderType.COLLECTION) {m_collectionType = CollectionType.OTHER;            m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
		else if (m_binderType == BinderType.WORKSPACE)  {m_collectionType = CollectionType.NOT_A_COLLECTION; m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.OTHER;          }
		else                                            {m_collectionType = CollectionType.NOT_A_COLLECTION; m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
	}
	
	/**
	 * Stores the type of Collection if it references a Collection.
	 * 
	 * @param collectionType
	 */
	public void setCollectionType(CollectionType collectionType) {
		// Validate the CollectionType for the BinderType...
		if (isBinderCollection()) {
			if (CollectionType.NOT_A_COLLECTION == collectionType) {
				collectionType = CollectionType.OTHER;
			}
		}
		else {
			collectionType = CollectionType.NOT_A_COLLECTION;
		}
		
		// ...and store it.
		m_collectionType = collectionType;
	}

	/**
	 * Stores the type of Folder if it references a Folder.
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
	 * Stores whether the given binder's description is expanded when
	 * view in a binder's description panel.
	 * 
	 * @param binderDescExpanded
	 */
	public void setBinderDescExpanded(boolean binderDescExpanded) {
		m_binderDescExpanded = binderDescExpanded;
	}
	
	/**
	 * Stores whether the given binder's description is in HTML.
	 * 
	 * @param binderDescHTML
	 */
	public void setBinderDescHTML(boolean binderDescHTML) {
		m_binderDescHTML = binderDescHTML;
	}
	
	/**
	 * Stores whether the given binder is a library.
	 * 
	 * @param isLibrary
	 */
	public void setIsLibrary(boolean isLibrary) {
		m_isLibrary = isLibrary;
	}

	/**
	 * Stores whether the binder as has mirrored driver configured.
	 * 
	 * @param mirroredDriverConfigured
	 */
	public void setMirroredDriverConfigured(boolean mirroredDriverConfigured) {
		m_mirroredDriverConfigured = mirroredDriverConfigured;
	}
	
	/**
	 * Stores the number of unread entries this binder and sub binders
	 * have.
	 * 
	 * @param numUnread
	 */
	public void setNumUnread(Long numUnread) {
		m_numUnread = numUnread;
	}
	
	/**
	 * Stores the type of Workspace if it references a Workspace.
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
