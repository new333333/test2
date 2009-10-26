package org.kablink.teaming.extension.impl;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ExtensionExistsException;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.ObjectBuilder;
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
	
	public void deploy(){
		getExtDeployer().check();
	}

}