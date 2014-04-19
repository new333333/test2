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
package org.kablink.teaming.module.simplefile;

import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.module.netfolder.NetFolderUtil;

/**
 * @author jong
 *
 */
public class NetFileId extends SimpleFileId {

	/*
	 * ID of a NetFolderConfig object
	 */
	private Long netFolderConfigId;

	/*
	 * File system resource path relative to the NetFolderConfig object.
	 * This value is required and meaningful only if netFolderConfigId is greater than zero.
	 */
	private String resourcePath;
	
	// Cache fields
	private String parentPath;
	private String name;

	public NetFileId(Long netFolderConfigId, String resourcePath, boolean directory) {
		super(directory);
		if(netFolderConfigId == null)
			throw new IllegalArgumentException("Net folder config ID must be specified");
		if(resourcePath == null)
			throw new IllegalArgumentException("Resource path must be specified");
		this.netFolderConfigId = netFolderConfigId;
		this.resourcePath = resourcePath;
	}

	public String getResourceName() {
		readyName();
		return name;
	}
	
	public String getParentResourcePath() {
		readyName();
		return parentPath;
	}
	
	public Long getNetFolderConfigId() {
		return netFolderConfigId;
	}

	public void setNetFolderConfigId(Long netFolderConfigId) {
		this.netFolderConfigId = netFolderConfigId;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		.append("netFolderConfigId=")
		.append(netFolderConfigId)
		.append(",resourcePath=")
		.append(resourcePath)
		.append(",directory=")
		.append(directory)
		.append(")");
		return sb.toString();
	}
	
	private void readyName() {
		if(name == null) {
			ResourceDriver driver = NetFolderUtil.getResourceDriverByNetFolderConfigId(netFolderConfigId);
			name = driver.getResourceName(resourcePath);
			parentPath = driver.getParentResourcePath(resourcePath);
		}
	}
	
}
