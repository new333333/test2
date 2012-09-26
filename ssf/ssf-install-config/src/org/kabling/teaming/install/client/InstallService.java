package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.LoginException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("install")
public interface InstallService extends RemoteService
{
	InstallerConfig getConfiguration();
	
	void saveConfiguration(InstallerConfig config); 
	
	LoginInfo login(String userName,String password) throws LoginException;
	
	ProductInfo getProductInfo();
	
}
