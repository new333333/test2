package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InstallServiceAsync
{
	void getConfiguration(AsyncCallback<InstallerConfig> callback);

	void saveConfiguration(InstallerConfig config, AsyncCallback<Void> callback) throws ConfigurationSaveException;

	void login(String userName, String password, AsyncCallback<LoginInfo> callback) throws LoginException;

	void getProductInfo(AsyncCallback<ProductInfo> callback);

	void createDatabase(Database database, AsyncCallback<Void> callback);

	void updateDatabase(Database database, AsyncCallback<Void> callback);

	void reconfigure(InstallerConfig config, AsyncCallback<Void> callback);

	void startFilrServer(AsyncCallback<Void> callback);

	void authenticateDbCredentials(String url, String userName, String password, AsyncCallback<Void> callback);

	void isLuceneServerValid(String host, long port, AsyncCallback<Boolean> callback);
}
