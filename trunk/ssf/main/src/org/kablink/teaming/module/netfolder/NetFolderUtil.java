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

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.AppNetFolderSyncSettings;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoNetFolderConfigByTheIdException;
import org.kablink.teaming.domain.NoNetFolderConfigByTheNameException;
import org.kablink.teaming.domain.NoNetFolderServerByTheIdException;
import org.kablink.teaming.domain.NoNetFolderServerByTheNameException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.util.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jong
 *
 */
public class NetFolderUtil {
	
	// DEFINITION: 
	//
	// Net Folder Server == Resource Driver Config (This configuration information is stored in the database)
	// 
	// For legacy Mirrored Folders, their configuration information is stored in the applicationContext-ext.xml file.
	//
	// The system instantiates and initializes in-memory data structure called resource drivers (ResourceDriver).
	// For net folders, these resource drivers are created from the configuration information in the corresponding
	// net folder server (resource driver config) objects. Consequently, a call to getConfig() method on the resource
	// driver object returns a reference to the resource driver config object.
	// For legacy mirrored folders, these resource drivers are created from the configuration information associated
	// with the corresponding resource driver beans specified in the applicationContext-ext.xml file. Consequently,
	// a call to getConfig() method on the resource driver object returns null because there is no reource driver
	// config object associated with it.
	// 

	public static Binder getNetFolderTopBinder( NetFolderConfig nfc ) throws NoBinderByTheIdException
	{
		return getCoreDao().loadBinder(nfc.getTopFolderId(), RequestContextHolder.getRequestContext().getZoneId());
	}

	public static Folder getNetFolderTopFolder(NetFolderConfig nfc) throws NoFolderByTheIdException {
		return getFolderDao().loadFolder(nfc.getTopFolderId(), RequestContextHolder.getRequestContext().getZoneId());
	}
	
	public static Binder getNetFolderTopBinder(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException, NoBinderByTheIdException {
		return getNetFolderTopBinder(getNetFolderConfig(netFolderConfigId));
	}
	
	public static Folder getNetFolderTopFolder(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException, NoFolderByTheIdException {
		return getNetFolderTopFolder(getNetFolderConfig(netFolderConfigId));
	}
	
	public static NetFolderConfig getNetFolderConfig(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException {
		return getCoreDao().loadNetFolderConfig(netFolderConfigId);
	}
	
	public static NetFolderConfig getNetFolderConfigByName(String netFolderName) throws NoNetFolderConfigByTheNameException {
		return getCoreDao().loadNetFolderConfigByName(netFolderName);
	}
	
	public static ResourceDriverConfig getNetFolderServerById(Long netFolderServerId) throws NoNetFolderServerByTheIdException {
		return getCoreDao().loadNetFolderServer(netFolderServerId);
	}
	
	public static ResourceDriverConfig getNetFolderServerByName(String netFolderServerName) throws NoNetFolderServerByTheNameException {
		return getCoreDao().loadNetFolderServerByName(netFolderServerName);
	}
	
	public static ResourceDriverConfig getNetFolderServerByNetFolderConfigId(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException, NoNetFolderServerByTheIdException {
		return getNetFolderServerById(getNetFolderConfig(netFolderConfigId).getNetFolderServerId());
	}
	
	public static AclResourceDriver getResourceDriverByNetFolderConfigId(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException, FIException {
		NetFolderConfig nf = getNetFolderConfig(netFolderConfigId);
		return (AclResourceDriver)getResourceDriverByNetFolderServerId(nf.getNetFolderServerId());
	}
	
	public static AclResourceDriver getResourceDriverByNetFolderConfig(NetFolderConfig netFolderConfig) throws NoNetFolderConfigByTheIdException, FIException {
		return (AclResourceDriver)getResourceDriverByNetFolderServerId(netFolderConfig.getNetFolderServerId());
	}
	
	public static AclResourceDriver getResourceDriverByNetFolderConfigName(String netFolderConfigName) throws NoNetFolderConfigByTheIdException, FIException {
		NetFolderConfig nf = getNetFolderConfigByName(netFolderConfigName);
		return (AclResourceDriver)getResourceDriverByNetFolderServerId(nf.getNetFolderServerId());
	}
	
	public static AclResourceDriver getResourceDriverByNetFolderServerId(Long netFolderServerId) throws NoNetFolderConfigByTheIdException, FIException {
		return (AclResourceDriver)ResourceDriverManagerUtil.findResourceDriver(netFolderServerId);
	}
	
	public static AclResourceDriver getResourceDriverByNetFolderServerName(String netFolderServerName) throws NoNetFolderConfigByTheIdException, FIException {
		return (AclResourceDriver)ResourceDriverManagerUtil.findResourceDriver(netFolderServerName);
	}

	public static Map<Long, AppNetFolderSyncSettings> getAppNetFolderSyncSettings(List<Long> netFolderIds) {
		List<AppNetFolderSyncSettings> appNetFolderSyncSettings = getCoreDao().getAppNetFolderSyncSettings(netFolderIds);
		Map<Long, AppNetFolderSyncSettings> map = new HashMap<>();
		for (AppNetFolderSyncSettings settings : appNetFolderSyncSettings) {
			map.put(settings.getId(), settings);
		}
		return map;
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	private static FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}
}
