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
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoNetFolderByTheIdException;
import org.kablink.teaming.domain.NoNetFolderByTheNameException;
import org.kablink.teaming.domain.NoNetFolderServerByTheIdException;
import org.kablink.teaming.domain.NoNetFolderServerByTheNameException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * @author jong
 *
 */
public class NetFolderUtil {

	public static NetFolderConfig getNetFolderConfigById(Long netFolderConfigId) throws NoNetFolderByTheIdException {
		return getCoreDao().loadNetFolderConfig(netFolderConfigId);
	}
	
	public static NetFolderConfig getNetFolderConfigByName(String netFolderName) throws NoNetFolderByTheNameException {
		return getCoreDao().loadNetFolderConfigByName(netFolderName);
	}
	
	public static ResourceDriver getResourceDriverByNetFolderConfigId(Long netFolderConfigId) throws NoNetFolderByTheIdException, FIException {
		NetFolderConfig nf = getNetFolderConfigById(netFolderConfigId);
		return getResourceDriverByNetFolderServerId(nf.getNetFolderServerId());
	}
	
	public static ResourceDriver getResourceDriverByNetFolderConfigName(String netFolderConfigName) throws NoNetFolderByTheIdException, FIException {
		NetFolderConfig nf = getNetFolderConfigByName(netFolderConfigName);
		return getResourceDriverByNetFolderServerId(nf.getNetFolderServerId());
	}
	
	/**
	 * Return the Net Folder binder associated with the NetFolderConfig
	 */
	public static Binder getNetFolderBinder( NetFolderConfig nfc ) throws NoBinderByTheIdException
	{
		return getCoreDao().loadBinder(nfc.getFolderId(), RequestContextHolder.getRequestContext().getZoneId());
	}

	public static Folder getNetFolderFolder(NetFolderConfig nfc) throws NoFolderByTheIdException {
		return getFolderDao().loadFolder(nfc.getFolderId(), RequestContextHolder.getRequestContext().getZoneId());
	}
	
	public static Binder getNetFolderBinder(Long netFolderConfigId) throws NoNetFolderByTheIdException, NoBinderByTheIdException {
		return getNetFolderBinder(getNetFolderConfigById(netFolderConfigId));
	}
	
	public static Folder getNetFolderFolder(Long netFolderConfigId) throws NoNetFolderByTheIdException, NoFolderByTheIdException {
		return getNetFolderFolder(getNetFolderConfigById(netFolderConfigId));
	}
	
	public static ResourceDriverConfig getNetFolderServerById(Long netFolderServerId) throws NoNetFolderServerByTheIdException {
		return getCoreDao().loadNetFolderServer(netFolderServerId);
	}
	
	public static ResourceDriverConfig getNetFolderServerByName(String netFolderServerName) throws NoNetFolderServerByTheNameException {
		return getCoreDao().loadNetFolderServerByName(netFolderServerName);
	}
	
	public static ResourceDriver getResourceDriverByNetFolderServerId(Long netFolderServerId) throws NoNetFolderByTheIdException, FIException {
		return ResourceDriverManagerUtil.findResourceDriver(netFolderServerId);
	}
	
	public static ResourceDriver getResourceDriverByNetFolderServerName(String netFolderServerName) throws NoNetFolderByTheIdException, FIException {
		return ResourceDriverManagerUtil.findResourceDriver(netFolderServerName);
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	private static FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}
}
