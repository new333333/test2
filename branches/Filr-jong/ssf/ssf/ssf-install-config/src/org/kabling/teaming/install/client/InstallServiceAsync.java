package org.kabling.teaming.install.client;

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
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.UpdateStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InstallServiceAsync
{
	void getConfiguration(AsyncCallback<InstallerConfig> callback);

	void saveConfiguration(InstallerConfig config, List<LeftNavItemType> sectionsToUpdate,AsyncCallback<Void> callback) throws ConfigurationSaveException;

	void login(String userName, String password, AsyncCallback<LoginInfo> callback) throws LoginException;

	void getProductInfo(AsyncCallback<ProductInfo> callback);

	void createDatabase(Database database, AsyncCallback<Void> callback);

	void updateDatabase(Database database, AsyncCallback<Void> callback);

	void reconfigure(boolean restartServer,AsyncCallback<Void> callback);

	void startFilrServer(AsyncCallback<Void> callback);
	
	void updateDbUrlAndstartFilrServer(AsyncCallback<Void> callback);

	void authenticateDbCredentials(String url, String userName, String password, AsyncCallback<Void> callback);

	void isLuceneServerValid(String host, long port, AsyncCallback<Boolean> callback);

	void getTimeZones(AsyncCallback<Map<String, String>> callback);

	void logout(AsyncCallback<Void> callback);

	void markConfigurationDone(String configType, AsyncCallback<Void> callback);

	void reverConfiguration(AsyncCallback<Void> callback);

	void isUnsavedConfigurationExists(AsyncCallback<Boolean> callback);

	void setupLocalMySqlUserPassword(Database db, AsyncCallback<Void> callback);

	void getLicenseInformation(AsyncCallback<LicenseInformation> callback);

	void testSmtpConnection(EmailSettings emailSettings, AsyncCallback<Boolean> callback);

	void updateFilr(boolean ignoreDataDrive, boolean ignoreHostNameNotMatch, AsyncCallback<UpdateStatus> callback);

	void getProductInfoFromZipFile(AsyncCallback<ProductInfo> callback);

	void getFilrLocales(AsyncCallback<List<FilrLocale>> callback);
}
