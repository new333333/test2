package org.kablink.teaming.extension.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ExtensionExistsException;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.ObjectBuilder;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;

public class ExtensionManagerImpl extends CommonDependencyInjection implements ExtensionManager {
	
	protected ExtensionDeployer extDeployer;
	public ExtensionDeployer getExtDeployer() {
		if(extDeployer == null)
			extDeployer = (ExtensionDeployer) SpringContextUtil.getBean("extDeployer"); 
		
		return extDeployer;
	}
	
	public void addExtension(String name, Long zoneId, Map updates) {
		ExtensionInfo extInfo = new ExtensionInfo();
		extInfo.setName(name);
		extInfo.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		
		if(updates != null)
		{
			ObjectBuilder.updateObject(extInfo, updates);
		}
		
		List extensions = getExtensions();
		if (extensions.contains(extInfo)) {
			//Extension already exists
			throw new ExtensionExistsException(extInfo.getName());
		}
		getExtDeployer().addExtension(extInfo);
	 
    }
	public List<ExtensionInfo> getExtensions() {
		//return new ArrayList<ExtensionInfo>();
		return getExtDeployer().findExtensions();
    }
	public void modifyExtension(String id, Map updates) {
		ExtensionInfo extension = getExtDeployer().getExtension(id);

		if(updates != null){
			ObjectBuilder.updateObject(extension, updates);
			getExtDeployer().updateExtension(extension);
		}
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