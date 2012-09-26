package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InstallServiceAsync
{
	void getConfiguration(AsyncCallback<InstallerConfig> callback);

	void saveConfiguration(InstallerConfig config, AsyncCallback<Void> callback);

	void login(String userName, String password,
			AsyncCallback<LoginInfo> callback) throws LoginException;

	void getProductInfo(AsyncCallback<ProductInfo> callback);
}
