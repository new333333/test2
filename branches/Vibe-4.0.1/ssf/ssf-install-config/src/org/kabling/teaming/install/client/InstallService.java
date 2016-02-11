package org.kabling.teaming.install.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.FilrLocale;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.UpdateStatus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("install")
public interface InstallService extends RemoteService
{
	/**
	 * Get the installer.xml configuration (read from file system)
	 * @return - installer configuration
	 */
	InstallerConfig getConfiguration();

	/**
	 * Save the installer configuration back to the installer.xml
	 * @param config - configuration details that needs to be saved
	 * @throws ConfigurationSaveException
	 */
	void saveConfiguration(InstallerConfig config,List<LeftNavItemType> sectionsToUpdate) throws ConfigurationSaveException;

	/**
	 * Create the database. 
	 * @param database - database information
	 * @throws ConfigurationSaveException
	 */
	void createDatabase(Database database) throws ConfigurationSaveException;

	/**
	 * Update the database 
	 * @param database
	 * @throws ConfigurationSaveException
	 */
	void updateDatabase(Database database) throws ConfigurationSaveException;

	void reconfigure( boolean restartServer);

	/**
	 * Start the filr server
	 */
	void startFilrServer();

	/**
	 * Login in to the filr configuration UI
	 * @param userName - username
	 * @param password - password
	 * @return
	 * @throws LoginException
	 */
	LoginInfo login(String userName, String password) throws LoginException;

	/**
	 * Get the product information
	 * @return
	 */
	ProductInfo getProductInfo();

	/**
	 * Authenticate to the database
	 * @param url - url to the database
	 * @param userName - user name
	 * @param password - password
	 * @throws LoginException
	 */
	void authenticateDbCredentials(String url, String userName, String password) throws LoginException;;

	/**
	 * Check to see if the lucene server information is valid
	 * @param host - lucene server address 
	 * @param port - lucene server port
	 * @return
	 * @throws LuceneConnectException
	 */
	Boolean isLuceneServerValid(String host, long port) throws LuceneConnectException;

	/**
	 * Get the list of timezones. This information is used for setting up outbound email
	 * @return - list of timezone description and id
	 */
	Map<String, String> getTimeZones();
	
	void logout();
	
	void markConfigurationDone(String configType);
	
	void reverConfiguration() throws IOException;
	
	boolean isUnsavedConfigurationExists();
	
	void setupLocalMySqlUserPassword(Database db);
	
	LicenseInformation getLicenseInformation();
	
	boolean testSmtpConnection(EmailSettings emailSettings);
	
	UpdateStatus updateFilr(boolean ignoreDataDrive, boolean ignoreHostNameNotMatch);
	
	ProductInfo getProductInfoFromZipFile();
	
	List<FilrLocale> getFilrLocales();

	void updateDbUrlAndstartFilrServer();
}
