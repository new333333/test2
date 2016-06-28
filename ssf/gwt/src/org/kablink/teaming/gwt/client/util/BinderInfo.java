/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.binderviews.MobileDevicesViewSpec;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about a Binder between the
 * client and server.
 * 
 * @author drfoster@novell.com
 */
public class BinderInfo implements IsSerializable, VibeRpcResponseData {
	private BinderType    			m_binderType               = BinderType.OTHER;					//
	private boolean       			m_binderDescExpanded       = true;								//
	private boolean       			m_binderDescHTML;												//
	private boolean       			m_folderHome;													//
	private boolean       			m_folderMyFilesStorage;											//
	private boolean       			m_library;														//
	private boolean       			m_mirroredDriverConfigured;										//
	private CollectionType			m_collectionType           = CollectionType.NOT_A_COLLECTION;	//
	private FolderType    			m_folderType               = FolderType.NOT_A_FOLDER;			//
	private Long          			m_numUnread;													// Number of unread entries in this binder and sub binders.
	private MobileDevicesViewSpec	m_mdvSpec;														//
	private String        			m_binderId                 = "";								//
	private String        			m_binderTitle              = "";								//
	private String		  			m_binderDesc	           = "";								//
	private String        			m_entityType               = "";								//
	private WorkspaceType 			m_wsType                   = WorkspaceType.NOT_A_WORKSPACE;		//
	private String					m_cloudFolderRoot;												//
	private String        			m_parentBinderId           = "";								//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public BinderInfo() {
		// Initialize the super class.
		super();
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
		reply.setParentBinderId(          m_parentBinderId          );
		reply.setCloudFolderRoot(         m_cloudFolderRoot         );
		reply.setNumUnread(               m_numUnread               );
		reply.setEntityType(              m_entityType              );
		reply.setBinderType(              m_binderType              );
		reply.setCollectionType(          m_collectionType          );
		reply.setFolderHome(              m_folderHome              );
		reply.setFolderMyFilesStorage(    m_folderMyFilesStorage    );
		reply.setFolderType(              m_folderType              );
		reply.setLibrary(                 m_library                 );
		reply.setWorkspaceType(           m_wsType                  );
		reply.setMirroredDriverConfigured(m_mirroredDriverConfigured);
		reply.setMobileDevicesViewSpec(   m_mdvSpec                 );
		return reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public BinderType            getBinderType()              {return                            m_binderType;              }
	public boolean               isBinderAccessible()         {return (!(BinderType.OTHER.equals(m_binderType)));           }
	public boolean               isBinderDescExpanded()       {return                            m_binderDescExpanded;      }
	public boolean               isBinderDescHTML()           {return                            m_binderDescHTML;          }
	public boolean               isFolderHome()               {return                            m_folderHome;              }
	public boolean               isFolderMyFilesStorage()     {return                            m_folderMyFilesStorage;    }
	public boolean               isLibrary()                  {return                            m_library;                 }
	public boolean               isMirroredDriverConfigured() {return                            m_mirroredDriverConfigured;}
	public CollectionType        getCollectionType()          {return                            m_collectionType;          }
	public FolderType            getFolderType()              {return                            m_folderType;              }
	public Long                  getBinderIdAsLong()          {return Long.parseLong(            m_binderId);               }
	public MobileDevicesViewSpec getMobileDevicesViewSpec()   {return                            m_mdvSpec;                 }
	public String                getBinderId()                {return                            m_binderId;                }
	public String                getParentBinderId()          {return                            m_parentBinderId;          }
	public String                getBinderTitle()             {return                            m_binderTitle;             }
	public String		         getBinderDesc()              {return                            m_binderDesc;              }
	public String                getCloudFolderRoot()         {return                            m_cloudFolderRoot;         }
	public Long                  getNumUnread()               {return                            m_numUnread;               }
	public String                getEntityType()              {return                            m_entityType;              }
	public WorkspaceType         getWorkspaceType()           {return                            m_wsType;                  }
	public Long                  getParentBinderIdAsLong()    {
		Long reply;
		if ((null == m_parentBinderId) || (0 == m_parentBinderId.length()))
		     reply = null;
		else reply = Long.parseLong(m_parentBinderId);
		return reply;
	}

	/**
	 * Constructs and returns an EntityId that refers to this binder.
	 * 
	 * If the BinderInfo doesn't refer to a specific binder (e.g., it
	 * refers to a collection, ...), null is returned.
	 * 
	 * @return
	 */
	public EntityId buildEntityId() {
		String eidType;
		switch (getBinderType()) {
		case FOLDER:     eidType = EntityId.FOLDER;    break;
		case WORKSPACE:  eidType = EntityId.WORKSPACE; break;
		default:         eidType = null;               break;
		}
		EntityId reply;
		if (null == eidType)
		     reply = null;
		else reply = new EntityId(getParentBinderIdAsLong(), getBinderIdAsLong(), eidType);
		return reply;
	}
	
	/**
	 * Returns true if this BinderInfo defines the root profiles binder
	 * in administrator management mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderAdministratorManagement() {
		return (isBinderWorkspace() && m_wsType.isAdministratorManagement());
	}
	
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
	 * Returns true if this BinderInfo defines an email templates view
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderEmailTemplates() {
		return (isBinderWorkspace() && m_wsType.isEmailTemplates());
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
	 * Returns true if this BinderInfo defines the root global
	 * workspaces binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderGlobalRootWS() {
		return (isBinderWorkspace() && m_wsType.isGlobalRoot());
	}
	
	/**
	 * Returns true if this BinderInfo defines the root binder
	 * in limit user visibility mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderLimitUserVisibility() {
		return (isBinderWorkspace() && m_wsType.isLimitUserVisibility());
	}
	
	/**
	 * Returns true of this BinderInfo defines a Mirrored Folder and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderMirroredFolder() {
		return (isBinderFolder() && FolderType.MIRROREDFILE.equals(getFolderType()));
	}

	/**
	 * Returns true if this BinderInfo defines mobile devices view
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderMobileDevices() {
		return (isBinderWorkspace() && m_wsType.isMobileDevices());
	}
	
	/**
	 * Returns true if this BinderInfo defines the root profiles binder
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderProfilesRootWS() {
		return (isBinderWorkspace() && m_wsType.isProfileRoot());
	}
	
	/**
	 * Returns true if this BinderInfo defines the root profiles binder
	 * in management mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderProfilesRootWSManagement() {
		return (isBinderWorkspace() && m_wsType.isProfileRootManagement());
	}
	
	/**
	 * Returns true if this BinderInfo defines the top workspace and false otherwise.
	 *
	 * @return
	 */
	public boolean isBinderLandingPageWS() {
		return (isBinderWorkspace() && m_wsType.isLandingPage());
	}

	/**
	 * Returns true if this BinderInfo defines proxy identities view
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderProxyIdentities() {
		return (isBinderWorkspace() && m_wsType.isProxyIdentities());
	}
	
	/**
	 * Returns true if this BinderInfo defines the root team workspaces
	 * binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderTeamsRootWS() {
		return (isBinderWorkspace() && m_wsType.isTeamRoot());
	}
	
	/**
	 * Returns true if this BinderInfo defines the root team workspaces
	 * binder in management mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderTeamsRootWSManagement() {
		return (isBinderWorkspace() && m_wsType.isTeamRootManagement());
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
	 * Returns true if this BinderInfo is a Cloud Folder and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isCloudFolder() {
		return ((null != m_cloudFolderRoot) && (0 < m_cloudFolderRoot.length()));
	}
	
	/**
	 * Returns true if the given BinderInfo is considered equal to this
	 * one.
	 * 
	 * @param bi
	 * 
	 * @return
	 */
	public boolean isEqual(BinderInfo bi) {
		// If we weren't given a BinderInfo to compare...
		if (null == bi) {
			// ...they can't be equals.
			return false;
		}

		// Are all the  types equal?
		boolean reply =
			(m_binderType.equals(    bi.getBinderType())    &&
			 m_folderType.equals(    bi.getFolderType())    &&
			 m_wsType.equals(        bi.getWorkspaceType()) &&
			 m_collectionType.equals(bi.getCollectionType()));
		
		if (reply) {
			// Yes!  Are the binder IDs equal?
			reply = m_binderId.equals(bi.getBinderId());
			if (reply) {
				// Yes!  Are the MobileDevicesViewSpec's equal?
				MobileDevicesViewSpec mdvSpecIn = bi.getMobileDevicesViewSpec();
				boolean hasMDVSpec_This = (null != m_mdvSpec  );
				boolean hasMDVSpec_In   = (null !=   mdvSpecIn);
				reply = (hasMDVSpec_This == hasMDVSpec_In);
				if (reply && hasMDVSpec_This) {
					reply = m_mdvSpec.equals(mdvSpecIn);
					if (reply && m_mdvSpec.isUser()) {
						reply = m_mdvSpec.getUserId().equals(mdvSpecIn.getUserId());
					}
				}
			}
		}
		
		// If we get here, reply is true if the given BinderInfo
		// matches this one and false otherwise.  Return it.
		return reply;
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
	 * Stores the ID of a Binder's parent Binder.
	 * 
	 * @param parentBinderId
	 */
	public void setParentBinderId(String parentBinderId) {
		m_parentBinderId = parentBinderId;
	}
	
	public void setParentBinderId(Long parentBinderId) {
		setParentBinderId((null == parentBinderId) ? "" : String.valueOf(parentBinderId));
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
	 * Stores the name of a Cloud Folder root.
	 * 
	 * @param cloudFolderRoot
	 */
	public void setCloudFolderRoot(String cloudFolderRoot) {
		m_cloudFolderRoot = cloudFolderRoot;
	}
	
	/**
	 * Stores whether the given binder is a home folder.
	 * 
	 * @param folderHome
	 */
	public void setFolderHome(boolean folderHome) {
		m_folderHome = folderHome;
	}

	/**
	 * Stores whether the given binder is a My Files Storage folder.
	 * 
	 * @param folderMyFilesStorage
	 */
	public void setFolderMyFilesStorage(boolean folderMyFilesStorage) {
		m_folderMyFilesStorage = folderMyFilesStorage;
	}

	/**
	 * Stores whether the given binder is a library.
	 * 
	 * @param library
	 */
	public void setLibrary(boolean library) {
		m_library = library;
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
	 * Stores a new MobileDevicesViewSpec.
	 * 
	 * @param mdvSpec
	 */
	public void setMobileDevicesViewSpec(MobileDevicesViewSpec mdvSpec) {
		m_mdvSpec = mdvSpec;
		if (null == m_mdvSpec) {
			if (m_wsType.isMobileDevices()) {
				m_wsType = WorkspaceType.PROFILE_ROOT_MANAGEMENT;
			}
		}
		else {
			m_wsType = WorkspaceType.MOBILE_DEVICES;
		}
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
