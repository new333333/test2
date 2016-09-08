package org.kabling.teaming.install.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;
import org.jvnet.libpam.PAM;
import org.jvnet.libpam.PAMException;
import org.jvnet.libpam.UnixUser;
import org.kabling.teaming.install.client.InstallService;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.EmailSettings.EmailProtocol;
import org.kabling.teaming.install.shared.FilrLocale;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;
import org.kabling.teaming.install.shared.UpdateStatus;

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
	public void saveConfiguration(InstallerConfig config, List<LeftNavItemType> sectionsToUpdate) throws ConfigurationSaveException
	{
		ConfigService.saveConfiguration(config, sectionsToUpdate);
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
		return ConfigService.isLuceneServerValid(host, port);
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

	@Override
	public boolean testSmtpConnection(final EmailSettings settings)
	{
		Properties props = new Properties();
		final String protocol = settings.getTransportProtocol().equals(EmailProtocol.SMTP) ? "smtp" : "smtps";

		if (protocol.equals("smtp"))
		{
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", settings.getSmtpHost());
			if (settings.isSmtpAuthEnabled())
				props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", settings.getSmtpPort());
			// props.put("mail.smtp.user", settings.getSmtpUser());
			// props.put("mail.smtp.password", settings.getSmtpPassword());
		}
		else
		{
			props.put("mail.smtps.host", settings.getSmtpsHost());
			props.put("mail.smtps.socketFactory.port", "465");
			props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			if (settings.isSmtpAuthEnabled())
				props.put("mail.smtps.auth", "true");
			props.put("mail.smtps.port", settings.getSmtpsPort());
		}

		// Create a session with the password authenticator
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				
				if (protocol.equals("smtp"))
				{
					String password = settings.getSmtpPassword();
					if (password != null && password.equals(""))
						password = null;
					return new PasswordAuthentication(settings.getSmtpUser(), password);
				}
				
				String password = settings.getSmtpsPassword();
				if (password != null && password.equals(""))
					password = null;
				
				return new PasswordAuthentication(settings.getSmtpsUser(), password);
			}
		});

		try
		{
			Transport transport = session.getTransport(protocol);

			// See if you can connect
			if (protocol.equals("smtp"))
			{
				String password = settings.getSmtpPassword();
				if (password != null && password.equals(""))
					password = null;
				
				transport.connect(settings.getSmtpHost(), settings.getSmtpUser(), password);
				logger.debug("smtp connection success ");
			}
			else
			{
				String password = settings.getSmtpsPassword();
				if (password != null && password.equals(""))
					password = null;
				
				transport.connect(settings.getSmtpsHost(), settings.getSmtpsUser(), password);
			}
		}
		catch (NoSuchProviderException e)
		{
			return false;
		}
		catch (MessagingException e)
		{
			// AuthenticationFailureException will also go through this
			logger.debug("smtp connection failed with " + e.getMessage());
			return false;
		}

		// Able to connect
		return true;
	}

	@Override
	public UpdateStatus updateFilr(boolean ignoreDataDrive, boolean ignoreHostNameNotMatch)
	{
		return UpdateService.updateFilrSystem(ignoreDataDrive, ignoreHostNameNotMatch);
	}

	@Override
	public ProductInfo getProductInfoFromZipFile()
	{
		return UpdateService.getProductInfoFromZipFile();
	}

	@Override
	public List<FilrLocale> getFilrLocales()
	{
		Locale[] localeList = Locale.getAvailableLocales();
		List<FilrLocale> filrLocaleList = new ArrayList<FilrLocale>();
		for (Locale loc : localeList)
		{
			if (isSupported(loc))
			{
				filrLocaleList.add(new FilrLocale(loc.getLanguage(), getCountryFromLocale(loc.toString()), loc.getDisplayName()));
			}
		}

		Collections.sort(filrLocaleList);

		return filrLocaleList;
	}

	private boolean isSupported(Locale locale)
	{
		if (locale.getCountry() == null || locale.getCountry().equals("") || locale.getDisplayVariant().equals("PREEURO"))
		{
			return false;
		}
		
		
		String language = locale.getLanguage();

		if (language.equals("fr") || language.equals("en") || language.equals("zh") || language.equals("da") || language.equals("de")
				|| language.equals("nl") || language.equals("hu") || language.equals("it") || language.equals("ja")
				|| language.equals("pl") || language.equals("pt") || language.equals("ru") || language.equals("es")
				|| language.equals("sv") || language.equals("cs"))
		{
			return true;
		}
		return false;
	}

	@Override
	public void updateDbUrlAndstartFilrServer()
	{
		ConfigService.startFilrServer();
		ConfigService.saveFilrConfigLocally();
		ConfigService.updateFsaUpdateUrl();
	}
	
	public String getCountryFromLocale(String localeString)
	{
		int index = localeString.indexOf("_");
		
		return localeString.substring(index+1);
	}
}
