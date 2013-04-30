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
package org.kablink.teaming.extension.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * 
 * @author Nathan Jensen
 *
 */
public class ExtensionManagerImpl extends CommonDependencyInjection implements ExtensionManager {
	
	protected ExtensionDeployer extDeployer;
	public ExtensionDeployer getExtDeployer() {
		if(extDeployer == null)
			extDeployer = (ExtensionDeployer) SpringContextUtil.getBean("extDeployer"); 
		
		return extDeployer;
	}

	public List<ExtensionInfo> getExtensions() {
		return getExtDeployer().findExtensions();
    }

	public void removeExtensions(String id) {
		ExtensionInfo ext = getExtDeployer().getExtension(id);

		if(!getExtDeployer().removeExtension(ext)) {
			//TODO throw some error
		}
    }
	
	public ExtensionInfo getExtension(String id) {
		return getExtDeployer().getExtension(id);
	}
	
	public ArrayList<String> getExtensionFiles(String id, String zoneName) {
		ArrayList<String> paths = new ArrayList<String>();

		ExtensionInfo ext = getExtDeployer().getExtension(id);
		String zoneFolderKey = SZoneConfig.getDefaultZoneName();
		if(!(zoneName.equals(SZoneConfig.getDefaultZoneName()))){
			Long zoneId = ext.getZoneId();
			zoneFolderKey = zoneName + "_" + zoneId;
		}

		//Get the extension name
		final String extensionPrefix = ext.getName();
		//Extension dir under WEB-INF
		File extensionDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneFolderKey + File.separator + extensionPrefix);
		if(extensionDir.exists()){
			walkTheTree(paths, extensionDir);
		}

		//Extension dir under webapp
		File extensionWebDir = new File(DirPath.getExtensionWebPath() + File.separator + zoneFolderKey +  File.separator + extensionPrefix);
		if(extensionWebDir.exists()){
			walkTheTree(paths, extensionWebDir);
		}
		
		return paths;
	}
	
	public void deploy(){
		getExtDeployer().check();
	}

	public boolean checkDefinitionsInUse(String id){
		ExtensionInfo ext = getExtDeployer().getExtension(id);
		return getExtDeployer().checkDefinitionsInUse(ext);
	}
	
	private void walkTheTree(ArrayList<String> list, File path){
		if(path.isDirectory()){
			File[] files = path.listFiles();
			int cnt = ((files != null) ?  files.length : 0);
			for(int i =0; i < cnt; i++){
				File file = files[i];
				walkTheTree(list, file);
			}
		} else {
			list.add(path.getAbsolutePath());
		}
	}
	
}