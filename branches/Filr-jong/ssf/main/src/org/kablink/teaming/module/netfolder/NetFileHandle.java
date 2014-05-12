/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.netfolder;

import java.util.List;
import java.util.Set;

import org.kablink.teaming.client.ws.model.FolderEntry;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.security.function.AccessCheckable;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * @author jong
 *
 */
public class NetFileHandle implements AccessCheckable {

	public static final String WORKAREA_TYPE = "netFileHandle";

	/*
	 * ID of a NetFolderConfig object
	 */
	private Long netFolderConfigId;
	
	/*
	 * File system resource path relative to the NetFolderConfig object (rather than to
	 * the Net Folder Server or Resource Driver Config object). This is the value we
	 * persist in the database.
	 */
	private String relRscPath;
	
	/*
	 * Indicates whether this object represents a directory or a file
	 */
	private boolean directory;
	
	/// Cache fields
	private Folder topNetFolder; // Folder object representing the top level of the net folder in which this file belongs.
	private boolean pathComputed = false;
	private String resourcePath; // Resource path relative to the Net Folder Server (= Reource Drivver Config) object. Used for interaction with resource driver.
	private String parentResourcePath; // Parent portion of the resourcePath (e.g. a/b/c -> a/b)
	private String name; // Name portion of the resource path (e.g. a/b/c -> c)

	public NetFileHandle(Long netFolderConfigId, String relRscPath, boolean directory) {
		if(netFolderConfigId == null)
			throw new IllegalArgumentException("Net folder config ID must be specified");
		if(resourcePath == null)
			throw new IllegalArgumentException("Resource path must be specified");
		this.netFolderConfigId = netFolderConfigId;
		this.relRscPath = relRscPath;
		this.directory = directory;
	}

	/**
	 * Return a Folder object representing the top level of the net folder in which this file belongs in
	 * or <code>null</code> if such information is not obtainable.
	 * 
	 * @return
	 */
	public Folder getTopNetFolder() {
		if(topNetFolder == null) {
			if(netFolderConfigId != null) {
				NetFolderConfig nfc = NetFolderUtil.getNetFolderConfig(netFolderConfigId);
				if(nfc != null) {
					if(nfc.getFolderId() != null) {
						try {
							topNetFolder = getFolderDao().loadFolder(nfc.getFolderId(), RequestContextHolder.getRequestContext().getZoneId());
						}
						catch(NoFolderByTheIdException e) {}
					}
				}
			}
		}
		return topNetFolder;
	}
	
	
	public Long getNetFolderConfigId() {
		return netFolderConfigId;
	}

	public boolean isDirectory() {
		return directory;
	}

	public String getRelRscPath() {
		return relRscPath;
	}

	public String getResourcePath() {
		computePath();
		return resourcePath;
	}
	
	public String getParentResourcePath() {
		computePath();
		return parentResourcePath;
	}

	public String getResourceName() {
		computePath();
		return name;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		.append("netFolderConfigId=")
		.append(netFolderConfigId)
		.append(",relRscPath=")
		.append(relRscPath)
		.append(",directory=")
		.append(directory)
		.append(")");
		return sb.toString();
	}

	public String getIdKey() {
		return netFolderConfigId + "-" + relRscPath;
	}
	
	private void computePath() {
		if(!pathComputed) {
			NetFolderConfig nfc = NetFolderUtil.getNetFolderConfig(netFolderConfigId);
			resourcePath = nfc.buildResourcePathRelativeToNetFolderServer(getRelRscPath());	
			ResourceDriver driver = NetFolderUtil.getResourceDriverByNetFolderConfig(nfc);
			parentResourcePath = driver.getParentResourcePath(resourcePath);
			name = driver.getResourceName(resourcePath);
			pathComputed = true;
		}
	}

	private FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}

	@Override
	public String getWorkAreaType() {
		return WORKAREA_TYPE;
	}

	@Override
	public boolean isFunctionMembershipInherited() {
		return false;
	}

	@Override
	public boolean isExtFunctionMembershipInherited() {
		return false;
	}

	@Override
	public Long getOwnerId() {
		return null;
	}

	@Override
	public Principal getOwner() {
		return null;
	}

	@Override
	public Set<Long> getTeamMemberIds() {
		return null;
	}

	@Override
	public AccessCheckable getParentAccessCheckable() {
		return null;
	}

	@Override
	public boolean isAclExternallyControlled() {
		return true;
	}

	@Override
	public List<WorkAreaOperation> getExternallyControlledRights() {
		AclResourceDriver driver = NetFolderUtil.getResourceDriverByNetFolderConfigId(netFolderConfigId);
		return driver.getExternallyControlledlRights();
	}

	@Override
	public boolean noAclDredged() {
		ResourceDriverConfig rdc = NetFolderUtil.getNetFolderServerByNetFolderConfigId(netFolderConfigId);
		return rdc.isAclAware();
	}

	@Override
	public WorkArea asShareableWorkArea() {
		if(isDirectory())
			return getNetFolderModule().obtainFolder(netFolderConfigId, getRelRscPath(), false);
		else
			return getNetFolderModule().obtainFolderEntry(netFolderConfigId, getRelRscPath(), false);
	}
	
	private NetFolderModule getNetFolderModule() {
		return (NetFolderModule) SpringContextUtil.getBean("netFolderModule");
	}
}
