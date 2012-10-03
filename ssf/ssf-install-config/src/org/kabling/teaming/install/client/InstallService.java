package org.kabling.teaming.install.client;

import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.ProductInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("install")
public interface InstallService extends RemoteService
{
	InstallerConfig getConfiguration();

	void saveConfiguration(InstallerConfig config) throws ConfigurationSaveException;

	void createDatabase(Database database) throws ConfigurationSaveException;

	void updateDatabase(Database database) throws ConfigurationSaveException;

	void reconfigure(InstallerConfig config) throws ConfigurationSaveException;

	void startFilrServer();

	LoginInfo login(String userName, String password) throws LoginException;

	ProductInfo getProductInfo();

	void authenticateDbCredentials(String url, String userName, String password) throws LoginException;;

	Boolean isLuceneServerValid(String host, long port) throws LuceneConnectException;

}
