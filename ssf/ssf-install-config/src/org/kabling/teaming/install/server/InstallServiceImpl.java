package org.kabling.teaming.install.server;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvnet.libpam.PAM;
import org.jvnet.libpam.PAMException;
import org.jvnet.libpam.UnixUser;
import org.kabling.teaming.install.client.InstallService;
import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstallServiceImpl extends RemoteServiceServlet implements InstallService
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.InstallServiceImpl");

	@Override
	public LoginInfo login(String userName, String password) throws LoginException
	{
		LoginInfo loginInfo = new LoginInfo();

		ProductType productType = getProductInfo().getType();

		// For Filr Appliance, we will do PAM Authentication
		if (ConfigService.isUnix() && productType.equals(ProductType.NOVELL_FILR))
		{
			try
			{
				UnixUser user = new PAM("passwd").authenticate(userName, password);
				loginInfo.setUser(user.getUserName());
			}
			catch (PAMException e)
			{
				throw new LoginException();
			}
		}
		else
		{
			loginInfo.setUser(userName);
		}

		return loginInfo;
	}

	@Override
	public void logout()
	{
		// Invalidate the session
		getThreadLocalRequest().getSession().invalidate();
	}

	public InstallerConfig getConfiguration()
	{
		return ConfigService.getConfiguration();
	}

	@Override
	public void saveConfiguration(InstallerConfig config) throws ConfigurationSaveException
	{
		ConfigService.saveConfiguration(config);
	}

	@Override
	public void createDatabase(Database database) throws ConfigurationSaveException
	{
		ConfigService.createDatabase(database);
	}

	@Override
	public void updateDatabase(Database database) throws ConfigurationSaveException
	{
		ConfigService.updateDatabase(database);
		
	}

	@Override
	public void reconfigure(boolean restartServer)
	{
		ConfigService.reconfigure(restartServer);
		
	}

	@Override
	public void startFilrServer()
	{
		ConfigService.startFilrServer();
	}

	@Override
	public ProductInfo getProductInfo()
	{
		return ConfigService.getProductInfo();
	}

	@Override
	public void authenticateDbCredentials(String url, String userName, String password) throws LoginException
	{
		ConfigService.authenticateDbCredentials(url, userName, password);
	}

	@Override
	public Boolean isLuceneServerValid(String host, long port) throws LuceneConnectException
	{
		return ConfigService.isLuceneServerValid(host,port);
	}

	@Override
	public Map<String, String> getTimeZones()
	{
		return ConfigService.getTimeZones();
	}

	@Override
	public void markConfigurationDone(String configType)
	{
		ConfigService.markConfigurationDone(configType);
	}

	@Override
	public void reverConfiguration() throws IOException
	{
		ConfigService.reverConfiguration();
	}

	@Override
	public boolean isUnsavedConfigurationExists()
	{
		return ConfigService.isUnsavedConfigurationExists();
	}

	@Override
	public void setupLocalMySqlUserPassword(Database db)
	{
		ConfigService.setupLocalMySqlUserPassword(db);
	}

	@Override
	public LicenseInformation getLicenseInformation()
	{
		return ConfigService.getLicenseInformation();
	}
}
