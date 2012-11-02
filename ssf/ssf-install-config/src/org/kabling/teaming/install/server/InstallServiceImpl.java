package org.kabling.teaming.install.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.jvnet.libpam.PAM;
import org.jvnet.libpam.PAMException;
import org.jvnet.libpam.UnixUser;
import org.kabling.teaming.install.client.InstallService;
import org.kabling.teaming.install.shared.Clustered;
import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.DatabaseConfig.DatabaseType;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.EmailSettings.EmailProtocol;
import org.kabling.teaming.install.shared.Environment;
import org.kabling.teaming.install.shared.FileConfig;
import org.kabling.teaming.install.shared.FileSystem;
import org.kabling.teaming.install.shared.HASearchNode;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.InstallerConfig.WebDAV;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.LoginInfo;
import org.kabling.teaming.install.shared.Lucene;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.MirroredFolder;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.Presence;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;
import org.kabling.teaming.install.shared.RSS;
import org.kabling.teaming.install.shared.RequestsAndConnections;
import org.kabling.teaming.install.shared.SSO;
import org.kabling.teaming.install.shared.ShellCommandInfo;
import org.kabling.teaming.install.shared.WebService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstallServiceImpl extends RemoteServiceServlet implements InstallService
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.InstallServiceImpl");
	private final int MAX_TRIES = 2;
	private final String FILR_SERVER_URL = "http://localhost:8080";

	@Override
	public LoginInfo login(String userName, String password) throws LoginException
	{
		LoginInfo loginInfo = new LoginInfo();

		ProductType productType = getProductInfo().getType();

		// For Filr Appliance, we will do PAM Authentication
		if (isUnix() && productType.equals(ProductType.NOVELL_FILR))
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
		InstallerConfig config = null;
		Document document = null;
		try
		{
			document = getDocument();
		}
		catch (IOException e)
		{
		}

		if (document != null)
			config = getInstallerConfig(document);

		// TODO Get the data from Lucene server and other servers and make sure
		// we
		// update it again with those data
		return config;
	}

	private Document getDocument() throws IOException
	{
		InputStream is = null;
		try
		{
			ProductType productType = getProductInfo().getType();
			// create a DOM parser
			DOMParser parser = new DOMParser();

			File file = null;

			if (isUnix() && productType.equals(ProductType.NOVELL_FILR))
			{
				file = new File("/filrinstall/installer.xml");
			}

			if (file != null && file.exists())
			{
				is = new FileInputStream(file);
			}
			// For Now, we will read it locally
			else
			{
				is = getServletContext().getResourceAsStream("/WEB-INF/installer.xml");
			}

			// parse the document
			parser.parse(new InputSource(is));
			is.close();
			return parser.getDocument();
		}
		catch (IOException e)
		{
			logger.debug("IOException - while reading the file in getDocument(");
		}
		catch (SAXException e)
		{
			logger.debug("SAXException - while reading the file in getDocument(");
		}
		finally
		{
			is.close();
		}

		return null;

	}

	/**
	 * Get the installer configuration parsing the xml document
	 * 
	 * @param document
	 *            - document to parse
	 * @return - installer config java object
	 */
	private InstallerConfig getInstallerConfig(Document document)
	{
		InstallerConfig config = new InstallerConfig();

		// TODO: install type, version, product, zoneName

		// Environment Section
		config.setEnvironment(getEnvironmentData(document));

		// Network Section
		config.setNetwork(getNetworkData(document));

		// Memory Section
		// This is small enough, we don't need to put in a separate method
		Element memoryElement = getElement(document.getDocumentElement(), "Memory");
		if (memoryElement != null)
		{

			Element jvmElement = getElement(memoryElement, "JavaVirtualMachine");

			if (jvmElement != null)
			{
				config.setJvmMemory(jvmElement.getAttribute("mx"));
			}
		}

		// Requests And Connections
		config.setRequestsAndConnections(getRequestsAndConnections(document));

		// File System
		config.setFileSystem(getFileSystemData(document));

		// WebDAV Section
		Element webDavElement = getElement(document.getDocumentElement(), "WebDAV");

		if (webDavElement != null)
		{
			String method = webDavElement.getAttribute("method");

			if (method == null || method.equals("basic"))
				config.setWebDav(WebDAV.BASIC);
			else
				config.setWebDav(WebDAV.DIGEST);
		}

		// Database Configuration
		config.setDatabase(getDatabaseData(document));

		// Lucene Configuration
		config.setLucene(getLuceneData(document));

		// RSS Configuration
		config.setRss(getRSSData(document));

		// Email Settings
		config.setEmailSettings(getEmailSettingsData(document));

		// Novell Messenger Presence
		config.setPresence(getPresenceData(document));

		// Mirrored Folders
		config.setMirroredFolderList(getMirroredFolders(document));

		// SSO
		config.setSso(getSSOData(document));

		// Cluster
		config.setClustered(getClusteredData(document));

		return config;
	}

	/**
	 * Parse through the installer.xml "Network" section and return a java object
	 * 
	 * <Network> <Host name="localhost" port="8080" listenPort="8080" securePort="8443" secureListenPort="8443" shutdownPort="8005"
	 * ajpPort="8009" keystoreFile="" /> <WebServices enable="true" /> <WebServicesBasic enable="true" /> <WebServicesToken enable="true" />
	 * <WebServicesAnonymous enable="false" /> <Session sessionTimeoutMinutes="240" /> </Network>
	 * 
	 * @param document
	 *            - DOM document
	 * @return - Network java bean object
	 */
	private Network getNetworkData(Document document)
	{
		Network network = new Network();
		Element networkNode = getElement(document.getDocumentElement(), "Network");

		// Nothing to parse if the node is null, return
		if (networkNode == null)
			return network;

		// Host Information
		Element hostNode = getElement(networkNode, "Host");

		if (hostNode != null)
		{

			network.setHost(hostNode.getAttribute("name"));
			network.setPort(getIntegerValue(hostNode.getAttribute("port")));
			network.setListenPort(getIntegerValue(hostNode.getAttribute("listenPort")));
			network.setSecurePort(getIntegerValue(hostNode.getAttribute("securePort")));
			network.setSecureListenPort(getIntegerValue(hostNode.getAttribute("secureListenPort")));
			network.setShutdownPort(getIntegerValue(hostNode.getAttribute("shutdownPort")));
			network.setAjpPort(getIntegerValue(hostNode.getAttribute("ajpPort")));
			network.setKeystoreFile(hostNode.getAttribute("keystoreFile"));

		}

		WebService webService = new WebService();
		network.setWebService(webService);

		// Web Services
		Element webServiceNode = getElement(networkNode, "WebServices");

		if (webServiceNode != null)
		{
			// Web Service enabled?
			webService.setEnabled(getBooleanValue(webServiceNode.getAttribute("enable")));
		}

		// Web Service Basic
		Element basicNode = getElement(networkNode, "WebServicesBasic");
		if (basicNode != null)
		{
			webService.setBasicEnabled(getBooleanValue(basicNode.getAttribute("enable")));
		}

		// Web Service Token
		Element tokenNode = getElement(networkNode, "WebServicesToken");
		if (tokenNode != null)
		{
			webService.setTokenEnabled(getBooleanValue(tokenNode.getAttribute("enable")));
		}

		// Web Service Anonymous
		Element anonymousNode = getElement(networkNode, "WebServicesAnonymous");
		if (anonymousNode != null)
		{
			webService.setAnonymousEnabled(getBooleanValue(anonymousNode.getAttribute("enable")));
		}

		// Session Time out minutes
		Element sessionNode = getElement(networkNode, "Session");

		if (sessionNode != null)
		{
			network.setSessionTimeoutMinutes(getIntegerValue(sessionNode.getAttribute("sessionTimeoutMinutes")));
		}

		return network;
	}

	/**
	 * Parse through the installer.xml "Environment" section and return a java object
	 * 
	 * <JDK JAVA_HOME="" type="Sun" />
	 * 
	 * <!-- What userid to run as (Linux-only) --> <!-- Also what userId and groupId to use --> <!-- as owner of the data directories. -->
	 * <Ids userId="" groupId="" />
	 * 
	 * <!-- Where does the Kablink software reside? --> <SoftwareLocation path="" />
	 * 
	 * <!-- The default locale to be used by Teaming. Defaults --> <!-- to the i18n.default.locale.* settings in --> <!-- ssf.properties.
	 * --> <DefaultLocale language="en" country="US" />
	 * 
	 * <!-- The default password into the Teaming keystore. --> <Keystore password="changeit" />
	 * 
	 * @param document
	 *            - DOM document
	 * @return - Environment java bean object
	 */
	private Environment getEnvironmentData(Document document)
	{
		Environment env = new Environment();
		Element envNode = getElement(document.getDocumentElement(), "Environment");

		if (envNode != null)
		{

			// Parse JDK Tag
			Element currentElement = getElement(envNode, "JDK");

			if (currentElement != null)
			{
				env.setJdkHome(currentElement.getAttribute("JAVA_HOME"));
				env.setJdkType(currentElement.getAttribute("type"));
			}

			// Look for Ids
			currentElement = getElement(envNode, "Ids");

			if (currentElement != null)
			{
				env.setUserId(currentElement.getAttribute("userId"));
				env.setGroupId(currentElement.getAttribute("groupId"));
			}

			// Look for DefaultLocale
			currentElement = getElement(envNode, "DefaultLocale");

			if (currentElement != null)
			{
				env.setDefaultLanguage(currentElement.getAttribute("language"));
				env.setDefaultCountry(currentElement.getAttribute("country"));
			}

			// Look For KeyStore
			currentElement = getElement(envNode, "Keystore");

			if (currentElement != null)
			{
				env.setKeyStorePassword(currentElement.getAttribute("password"));
			}

			// SoftwareLocation
			currentElement = getElement(envNode, "SoftwareLocation");

			if (currentElement != null)
			{
				env.setSoftwareLocation(currentElement.getAttribute("path"));
			}
		}
		return env;
	}

	/**
	 * Parse through the installer.xml "RequestsAndConnections" section and return a java object
	 * 
	 * <RequestsAndConnections> <maxThreads value="200" /> <maxActive value="50" /> <maxIdle value="20" /> </RequestsAndConnections>
	 * 
	 * @param document
	 *            - DOM document
	 * 
	 * @return - RequestsAndConnections java bean object
	 * 
	 **/
	private RequestsAndConnections getRequestsAndConnections(Document document)
	{

		RequestsAndConnections requestAndConnections = new RequestsAndConnections();
		Element rootNode = getElement(document.getDocumentElement(), "RequestsAndConnections");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return requestAndConnections;

		// Properties (maxThreads, maxActive and maxIdle)
		requestAndConnections.setMaxThreads(getIntegerValue(rootNode.getAttribute("maxThreads")));
		requestAndConnections.setMaxActive(getIntegerValue(rootNode.getAttribute("maxActive")));
		requestAndConnections.setMaxIdle(getIntegerValue(rootNode.getAttribute("maxIdle")));

		return requestAndConnections;
	}

	/**
	 * Parse through the installer.xml "RequestsAndConnections" section and return a java object
	 * 
	 * <RequestsAndConnections> <maxThreads value="200" /> <maxActive value="50" /> <maxIdle value="20" /> </RequestsAndConnections>
	 * 
	 * @param document
	 *            - DOM document
	 * 
	 * @return - RequestsAndConnections java bean object
	 * 
	 **/
	private FileSystem getFileSystemData(Document document)
	{

		FileSystem fileSystem = new FileSystem();
		Element rootNode = getElement(document.getDocumentElement(), "FileSystem");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return fileSystem;

		fileSystem.setConfigName(rootNode.getAttribute("configName"));

		// Get the Config nodes under the file system
		NodeList configNodeLists = rootNode.getElementsByTagName("Config");

		if (configNodeLists != null)
		{

			List<FileConfig> fileConfigLists = new ArrayList<FileConfig>();
			fileSystem.setConfigList(fileConfigLists);

			// Go through each config nodes
			for (int i = 0; i < configNodeLists.getLength(); i++)
			{

				Element configElement = (Element) configNodeLists.item(i);

				FileConfig config = new FileConfig();
				fileConfigLists.add(config);

				String configId = configElement.getAttribute("id");
				config.setId(configId);

				// Root Directory
				Element currentElement = getElement(configElement, "RootDirectory");
				if (currentElement != null)
				{
					config.setRootDirPath(currentElement.getAttribute("path"));
				}

				// Stellant Linux Fonts (Only used by linux)
				currentElement = getElement(configElement, "StellentLinuxFonts");
				if (currentElement != null)
				{
					config.setRootDirPath(currentElement.getAttribute("path"));
				}

				// Only advanced has all these properties
				if (configId != null && configId.equalsIgnoreCase("advanced"))
				{

					// Simple File Repository
					currentElement = getElement(configElement, "StellentLinuxFonts");
					if (currentElement != null)
					{
						config.setStellantLinuxFontsPath(currentElement.getAttribute("path"));
					}

					// Jackrabbit File Repository
					currentElement = getElement(configElement, "JackrabbitRepository");
					if (currentElement != null)
					{
						config.setJackRabbitRepPath(currentElement.getAttribute("path"));
					}

					// Extension Repository
					currentElement = getElement(configElement, "ExtensionRepository");
					if (currentElement != null)
					{
						config.setJackRabbitRepPath(currentElement.getAttribute("path"));
					}

					// Archive Store
					currentElement = getElement(configElement, "ArchiveStore");
					if (currentElement != null)
					{
						config.setArchiveStorePath(currentElement.getAttribute("path"));
					}

					// Cache Store
					currentElement = getElement(configElement, "CacheStore");
					if (currentElement != null)
					{
						config.setCacheStorePath(currentElement.getAttribute("path"));
					}

					// TODO
					// // Lucene Index
					// currentElement = getElement(configElement,
					// "LuceneIndex");
					// if (currentElement != null) {
					// config.setJackRabbitRepPath(currentElement
					// .getAttribute("path"));
					// }

				}

			}
		}

		return fileSystem;
	}

	/**
	 * <Database configName="MySQL_Default"> <!-- --> <!-- MySQL_Default --> <!-- --> <Config id="MySQL_Default" type="MySql"> <Resource
	 * for="icecore" driverClassName="com.mysql.jdbc.Driver" url=
	 * "jdbc:mysql://localhost:3306/sitescape?useUnicode=true&amp;characterEncoding=UTF-8" username="root" password="" /> </Config>
	 * 
	 * <!-- --> <!-- SQLServer_Default --> <!-- --> <Config id="SQLServer_Default" type="SQLServer"> <Resource for="icecore"
	 * driverClassName="net.sourceforge.jtds.jdbc.Driver" url="jdbc:jtds:sqlserver://localhost/sitescape;SelectMethod=cursor" username="sa"
	 * password="" /> </Config>
	 * 
	 * <!-- --> <!-- Oracle_Default --> <!-- --> <Config id="Oracle_Default" type="Oracle"> <Resource for="icecore"
	 * driverClassName="oracle.jdbc.driver.OracleDriver" url="jdbc:oracle:thin:@//localhost:1521/orcl" username="" password="" /> </Config>
	 * </Database>
	 * 
	 * @param document
	 * @return
	 */
	private Database getDatabaseData(Document document)
	{

		Database database = new Database();
		Element rootNode = getElement(document.getDocumentElement(), "Database");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return database;

		database.setConfigName(rootNode.getAttribute("configName"));

		// Get the Config nodes under the data base
		NodeList configNodeLists = rootNode.getElementsByTagName("Config");

		if (configNodeLists != null)
		{

			List<DatabaseConfig> configLists = new ArrayList<DatabaseConfig>();
			database.setConfig(configLists);

			// Go through each config nodes
			for (int i = 0; i < configNodeLists.getLength(); i++)
			{

				Element configElement = (Element) configNodeLists.item(i);

				DatabaseConfig config = new DatabaseConfig();
				configLists.add(config);

				// Config Id
				String configId = configElement.getAttribute("id");
				config.setId(configId);

				// Database Type
				String type = configElement.getAttribute("type");

				// Database types - default to MySql
				if (type == null || type.equalsIgnoreCase("MySql"))
				{
					config.setType(DatabaseType.MYSQL);
				}
				else if (type.equalsIgnoreCase("Oracle"))
				{
					config.setType(DatabaseType.ORACLE);
				}
				else if (type.equalsIgnoreCase("SQLServer"))
				{
					config.setType(DatabaseType.SQLSERVER);
				}

				// Resource Element
				Element currentElement = getElement(configElement, "Resource");

				if (currentElement != null)
				{
					config.setResourceFor(currentElement.getAttribute("for"));
					config.setResourceUrl(currentElement.getAttribute("url"));
					config.setResourceDriverClassName(currentElement.getAttribute("driverClassName"));
					config.setResourceUserName(currentElement.getAttribute("username"));
					config.setResourcePassword(currentElement.getAttribute("password"));
				}

			}
		}
		return database;
	}

	/**
	 * <Lucene luceneLocation="local"> <Resource lucene.index.hostname="localhost" lucene.max.booleans="10000"
	 * lucene.max.ha.search.nodes="100" lucene.merge.factor="10" lucene.rmi.port="1199" > <HASearchNode ha.service.name="node1"
	 * ha.service.title="This is node1" ha.service.hostname="xxx.xxx.xxx.xxx" ha.service.rmi.port="1199" /> <HASearchNode
	 * ha.service.name="node2" ha.service.title="This is node2" ha.service.hostname="yyy.yyy.yyy.yyy" ha.service.rmi.port="1199" />
	 * </Resource> </Lucene>
	 * 
	 * @param document
	 * @return
	 */
	private Lucene getLuceneData(Document document)
	{

		Lucene lucene = new Lucene();
		Element rootNode = getElement(document.getDocumentElement(), "Lucene");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return lucene;

		lucene.setLocation(rootNode.getAttribute("luceneLocation"));

		// Resource Element data
		Element resourceElement = getElement(rootNode, "Resource");

		if (resourceElement == null)
			return lucene;

		lucene.setIndexHostName(resourceElement.getAttribute("lucene.index.hostname"));
		lucene.setRmiPort(getIntegerValue(resourceElement.getAttribute("lucene.rmi.port")));
		lucene.setMaxBooleans(getIntegerValue(resourceElement.getAttribute("lucene.max.booleans")));
		lucene.setHighAvailabilitySearchNodes(getIntegerValue(resourceElement.getAttribute("lucene.max.ha.search.nodes")));
		lucene.setMergeFactor(getIntegerValue(resourceElement.getAttribute("lucene.merge.factor")));

		// Get the HASearchNodes
		NodeList configNodeLists = resourceElement.getElementsByTagName("HASearchNode");

		if (configNodeLists != null)
		{

			List<HASearchNode> configLists = new ArrayList<HASearchNode>();
			lucene.setSearchNodesList(configLists);

			// Go through each config nodes
			for (int i = 0; i < configNodeLists.getLength(); i++)
			{

				Element hsaElement = (Element) configNodeLists.item(i);

				HASearchNode config = new HASearchNode();
				configLists.add(config);

				config.setName(hsaElement.getAttribute("ha.service.name"));
				config.setHostName(hsaElement.getAttribute("ha.service.hostname"));
				config.setTitle(hsaElement.getAttribute("ha.service.title"));
				config.setRmiPort(getIntegerValue(hsaElement.getAttribute("ha.service.rmi.port")));

			}
		}
		return lucene;
	}

	/**
	 * <RSS enable="true"> <Feed max.elapseddays="31" max.inactivedays="7" /> </RSS>
	 * 
	 * @param document
	 * @return
	 */
	private RSS getRSSData(Document document)
	{

		RSS rss = new RSS();
		Element rootNode = getElement(document.getDocumentElement(), "RSS");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return rss;

		rss.setEnabled(getBooleanValue(rootNode.getAttribute("enable")));

		// Feed Element data
		Element feedElement = getElement(rootNode, "Feed");

		if (feedElement == null)
			return rss;

		rss.setMaxElapsedDays(getIntegerValue(feedElement.getAttribute("max.elapseddays")));
		rss.setMaxInactiveDays(getIntegerValue(feedElement.getAttribute("max.inactivedays")));

		return rss;
	}

	/**
	 * <EmailSettings> <InternalInboundSMTP enable="false" bindAddress="" port="2525" tls="true" />
	 * 
	 * <Outbound defaultTZ="America/New_York" allowSendToAllUsers="false" > <Resource mail.transport.protocol="smtp"
	 * 
	 * mail.smtp.host="mailhost.yourcompany.com" mail.smtp.user="vibe@yourcompany.com" mail.smtp.password="" mail.smtp.auth="false"
	 * mail.smtp.port="25" mail.smtp.sendpartial="true"
	 * 
	 * mail.smtps.host="mailhost.yourcompany.com" mail.smtps.user="vibe@yourcompany.com" mail.smtps.password="" mail.smtps.auth="false"
	 * mail.smtps.port="465" mail.smtps.sendpartial="true"
	 * 
	 * /> </Outbound> </EmailSettings>
	 * 
	 * @param document
	 * @return
	 */
	private EmailSettings getEmailSettingsData(Document document)
	{

		EmailSettings emailSettings = new EmailSettings();
		Element rootNode = getElement(document.getDocumentElement(), "EmailSettings");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return emailSettings;

		// InternalInboundSMTP Element data
		Element currentElement = getElement(rootNode, "InternalInboundSMTP");

		if (currentElement != null)
		{
			emailSettings.setInternalInboundSMTPEnabled(getBooleanValue(currentElement.getAttribute("enable")));
			emailSettings.setInternalInboundSMTPPort(getIntegerValue(currentElement.getAttribute("port")));
			emailSettings.setInternalInboundSMTPBindAddress(currentElement.getAttribute("bindAddress"));
			emailSettings.setInternalInboundSMTPTLSEnabld(getBooleanValue(currentElement.getAttribute("tls")));
		}

		currentElement = getElement(rootNode, "Outbound");

		if (currentElement != null)
		{
			// TimeZone and AllowSendTo all users
			emailSettings.setDefaultTZ(currentElement.getAttribute("defaultTZ"));
			emailSettings.setAllowSendToAllUsers(getBooleanValue(currentElement.getAttribute("allowSendToAllUsers")));

			currentElement = getElement(currentElement, "Resource");

			// Outbound
			if (currentElement != null)
			{

				String transportProtocol = currentElement.getAttribute("mail.transport.protocol");
				if (transportProtocol == null || transportProtocol.equals("smtp"))
					emailSettings.setTransportProtocol(EmailProtocol.SMTP);
				else
					emailSettings.setTransportProtocol(EmailProtocol.SMTPS);

				// Resource SMTP Settings
				emailSettings.setSmtpHost(currentElement.getAttribute("mail.smtp.host"));
				emailSettings.setSmtpUser(currentElement.getAttribute("mail.smtp.user"));
				emailSettings.setSmtpAuthEnabled(getBooleanValue(currentElement.getAttribute("mail.smtp.auth")));
				emailSettings.setSmtpPort(getIntegerValue(currentElement.getAttribute("mail.smtp.port")));
				emailSettings.setSmtpPassword(currentElement.getAttribute("mail.smtp.password"));
				emailSettings.setSmtpSendPartial(getBooleanValue(currentElement.getAttribute("mail.smtp.sendpartial")));

				// Resource SMTPS Settings
				emailSettings.setSmtpsHost(currentElement.getAttribute("mail.smtps.host"));
				emailSettings.setSmtpsUser(currentElement.getAttribute("mail.smtps.user"));
				emailSettings.setSmtpsAuthEnabled(getBooleanValue(currentElement.getAttribute("mail.smtps.auth")));
				emailSettings.setSmtpsPort(getIntegerValue(currentElement.getAttribute("mail.smtps.port")));
				emailSettings.setSmtpsPassword(currentElement.getAttribute("mail.smtps.password"));
				emailSettings.setSmtpsSendPartial(getBooleanValue(currentElement.getAttribute("mail.smtps.sendpartial")));

			}

		}

		return emailSettings;
	}

	/**
	 ** <Presence> <Resource presence.service.enable="false" presence.service.server.address="" presence.service.server.port="8300"
	 * presence.service.server.cert="" presence.service.user.dn="" presence.service.user.password=""/> </Presence>
	 * 
	 * @param document
	 * @return
	 */
	private Presence getPresenceData(Document document)
	{

		Presence presence = new Presence();
		Element rootNode = getElement(document.getDocumentElement(), "Presence");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return presence;

		// Resource Element data
		Element resourceElement = getElement(rootNode, "Resource");

		if (resourceElement == null)
			return presence;

		presence.setResourceEnabled(getBooleanValue(resourceElement.getAttribute("presence.service.enable")));
		presence.setResourceAddress(resourceElement.getAttribute("presence.service.server.address"));
		presence.setResourcePort(getIntegerValue(resourceElement.getAttribute("presence.service.server.port")));
		presence.setResourceCert(resourceElement.getAttribute("presence.service.server.cert"));
		presence.setResourceUserDn(resourceElement.getAttribute("presence.service.user.dn"));
		presence.setResourceUserPassword(resourceElement.getAttribute("presence.service.user.password"));

		return presence;
	}

	/**
	 * <MirroredFolders> <MirroredFolder enabled="false" type="file" id="rd1" title="Shared Files 1" rootPath="k:/somedir" readonly="true"
	 * zoneId=""> <AllowedUsers idList="admin;u1;u2;u3" /> <AllowedGroups idList="g1;g2;g3" /> </MirroredFolder>
	 * 
	 * <MirroredFolder enabled="false" type="file" id="rd2" title="Shared Files 2" rootPath="/sharedFiles/someDirectory" readonly="true"
	 * zoneId=""> <AllowedUsers idList="admin;u1;u2;u3" /> <AllowedGroups idList="g1;g2;g3" /> </MirroredFolder>
	 * 
	 * <MirroredFolder enabled="false" type="webdav" id="rd3" title="WebDAV 1" rootPath="/Shared Documents/cool-dir" readonly="true"
	 * zoneId=""> <WebDAVContext hostUrl="http://hostname" user="accessId" password="" /> <AllowedUsers idList="admin;u1;u2;u3" />
	 * <AllowedGroups idList="g1;g2;g3" /> </MirroredFolder> </MirroredFolders>
	 * 
	 * @param document
	 * @return
	 */
	private List<MirroredFolder> getMirroredFolders(Document document)
	{
		List<MirroredFolder> mirroredFolderList = new ArrayList<MirroredFolder>();

		Element rootNode = getElement(document.getDocumentElement(), "MirroredFolders");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return mirroredFolderList;

		// Get the Config nodes under the data base
		NodeList configNodeLists = rootNode.getElementsByTagName("MirroredFolder");

		if (configNodeLists != null)
		{

			// Go through each config nodes
			for (int i = 0; i < configNodeLists.getLength(); i++)
			{

				Element mirrorElement = (Element) configNodeLists.item(i);

				MirroredFolder config = new MirroredFolder();
				mirroredFolderList.add(config);

				config.setEnabled(getBooleanValue(mirrorElement.getAttribute("enabled")));
				config.setType(mirrorElement.getAttribute("type"));
				config.setId(mirrorElement.getAttribute("id"));
				config.setTitle(mirrorElement.getAttribute("title"));
				config.setZoneId(mirrorElement.getAttribute("zoneId"));
				config.setRootPath(mirrorElement.getAttribute("rootPath"));
				config.setReadOnly(getBooleanValue(mirrorElement.getAttribute("readonly")));

				// Allowed Users Element
				Element currentElement = getElement(mirrorElement, "AllowedUsers");

				if (currentElement != null)
				{
					config.setAllowedUsersList(currentElement.getAttribute("idList"));
				}

				// Allowed Groups Element
				currentElement = getElement(mirrorElement, "AllowedGroups");

				if (currentElement != null)
				{
					config.setAllowedGroupsList(currentElement.getAttribute("idList"));
				}

				// Allowed Groups Element
				currentElement = getElement(mirrorElement, "WebDAVContext");

				if (currentElement != null)
				{
					config.setWebDAVHostUrl(currentElement.getAttribute("hostUrl"));
					config.setWebDAVHostUrl(currentElement.getAttribute("user"));
					config.setWebDAVPassword(currentElement.getAttribute("password"));
				}
			}
		}
		return mirroredFolderList;
	}

	private Clustered getClusteredData(Document document)
	{
		Clustered clustered = new Clustered();
		Element rootNode = getElement(document.getDocumentElement(), "Clustered");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return clustered;

		clustered.setEnabled(getBooleanValue(rootNode.getAttribute("enable")));
		clustered.setCachingProvider(rootNode.getAttribute("cachingProvider"));
		clustered.setCacheService(rootNode.getAttribute("cacheService"));
		clustered.setCacheGroupAddress(rootNode.getAttribute("cacheGroupAddress"));
		clustered.setCacheGroupPort(getIntegerValue(rootNode.getAttribute("cacheGroupPort")));
		clustered.setMemCachedAddress(rootNode.getAttribute("memcachedAddresses"));
		clustered.setJvmRoute(rootNode.getAttribute("jvmRoute"));

		return clustered;
	}

	private SSO getSSOData(Document document)
	{
		SSO sso = new SSO();
		Element rootNode = getElement(document.getDocumentElement(), "SSO");

		// Nothing to parse if the node is null, return
		if (rootNode == null)
			return sso;

		Element iChainElement = getElement(rootNode, "iChain");

		if (iChainElement == null)
			return sso;

		{
			sso.setiChainEnabled(getBooleanValue(iChainElement.getAttribute("enable")));

			// Log Off URL
			Element logOffElement = getElement(iChainElement, "Logoff");
			if (logOffElement != null)
			{
				sso.setiChainLogoffUrl(logOffElement.getAttribute("url"));
			}

			// Web Dav Proxy
			Element webDavProxyElement = getElement(iChainElement, "WebDAVProxy");
			if (webDavProxyElement != null)
			{
				sso.setiChainWebDAVProxyEnabled(getBooleanValue(webDavProxyElement.getAttribute("enable")));
				sso.setiChainWebDAVProxyHost(webDavProxyElement.getAttribute("host"));
			}

			// Proxy
			Element proxyElement = getElement(iChainElement, "Proxy");
			if (proxyElement != null)
			{
				sso.setiChainProxyAddr(proxyElement.getAttribute("ipaddr"));
			}
		}

		// WinAuth
		{
			Element winAuthElement = getElement(rootNode, "WinAuth");

			if (winAuthElement == null)
				return sso;

			sso.setWinAuthEnabled(getBooleanValue(winAuthElement.getAttribute("enable")));

			// Log Off URL
			Element logOffElement = getElement(winAuthElement, "Logoff");
			if (logOffElement != null)
			{
				sso.setWinAuthLogoffUrl(logOffElement.getAttribute("url"));
			}

			Element proxyElement = getElement(winAuthElement, "Proxy");
			if (proxyElement != null)
			{
				sso.setWinAuthProxyAddr(proxyElement.getAttribute("ipaddr"));
			}
		}
		return sso;
	}

	/**
	 * Helper method to get the element
	 * 
	 * @param parentElement
	 *            - parent element
	 * @param tagName
	 *            - tag name
	 * @return - first element inside the parent element matching the tag name
	 */
	private Element getElement(Element parentElement, String tagName)
	{
		NodeList nodeList = parentElement.getElementsByTagName(tagName);
		if (nodeList != null && nodeList.getLength() > 0)
		{
			return (Element) nodeList.item(0);
		}
		return null;
	}

	@Override
	public void saveConfiguration(InstallerConfig config) throws ConfigurationSaveException
	{
		Document document = null;
		try
		{
			document = getDocument();
		}
		catch (IOException e)
		{
			throw new ConfigurationSaveException("Unable to get installer.xml");
		}
		ProductInfo productInfo = getProductInfo();

		// Save each sections
		{
			saveDatabaseConfiguration(config, document);

			// TODO: For lucene configuration, we need to update the changes to
			// the lucene server
			saveLuceneConfiguration(config, document);

			saveMemoryConfiguration(config, document);

			saveWebDavConfiguration(config, document);

			saveNetworkConfiguration(config, document);

			saveWebServiceConfiguration(config, document);

			saveClusteredConfiguration(config, document);

			saveSSOConfiguration(config, document);

			saveEmailSettingsConfiguration(config, document);

			saveReqAndConnectionsConfiguration(config, document);
		}

		// Save the changes to installer.xml
		if (productInfo.getType().equals(ProductType.NOVELL_FILR))
		{

			try
			{
				DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
				DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");

				LSOutput lsOutput = impl.createLSOutput();
				lsOutput.setEncoding("UTF-8");

				// Temporary, so that we can all the work from a Windows Box
				String osName = System.getProperty("os.name");
				if (osName.startsWith("Win"))
				{
					String str = getServletContext().getRealPath("/WEB-INF/installer.xml");
					lsOutput.setByteStream(new FileOutputStream(str));
				}
				else
				{
					lsOutput.setByteStream(new FileOutputStream("/filrinstall/installer.xml"));
				}
				LSSerializer serializer = impl.createLSSerializer();

				serializer.write(document, lsOutput);
			}
			catch (IOException e)
			{
				logger.debug("Error saving installer.xml, IO Exception");
				throw new ConfigurationSaveException();
			}
			catch (ClassCastException e)
			{
				logger.debug("Error saving installer.xml, Class Cast Exception");
				throw new ConfigurationSaveException();
			}
			catch (ClassNotFoundException e)
			{
				logger.debug("Error saving installer.xml, Class Not Found Exception");
				throw new ConfigurationSaveException();
			}
			catch (InstantiationException e)
			{
				logger.debug("Error saving installer.xml,Instantion Exception");
				throw new ConfigurationSaveException();
			}
			catch (IllegalAccessException e)
			{
				logger.debug("Error saving installer.xml, Illegal  Exception");
				throw new ConfigurationSaveException();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void store(Properties props, File file) throws FileNotFoundException
	{

		PrintWriter pw = new PrintWriter(file);

		for (Enumeration e = props.propertyNames(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			pw.println(key + "=" + props.getProperty(key));
		}
		pw.close();
	}

	private void saveDatabaseConfiguration(InstallerConfig installerConfig, Document document)
	{
		Database db = installerConfig.getDatabase();

		if (db == null)
			return;

		Element dbElement = getElement(document.getDocumentElement(), "Database");

		if (db.getConfigName() != null)
		{
			DatabaseConfig config = null;

			for (DatabaseConfig dbConfig : db.getConfig())
			{
				if (dbConfig.getId().equals(db.getConfigName()))
				{
					config = dbConfig;
					break;
				}
			}

			NodeList configList = dbElement.getElementsByTagName("Config");

			if (configList != null)
			{
				for (int i = 0; i < configList.getLength(); i++)
				{
					Element configElement = (Element) configList.item(i);

					if (configElement.getAttribute("id").equals(db.getConfigName()))
					{
						Element resourceElement = getElement(configElement, "Resource");

						String dbType = "MySql";
						if (config.getType().equals(DatabaseType.ORACLE))
							dbType = "Oracle";
						else if (config.getType().equals(DatabaseType.SQLSERVER))
							dbType = "SQLServer";
						configElement.setAttribute("type", dbType);

						resourceElement.setAttribute("username", config.getResourceUserName());

						if (config.getResourcePassword() != null && !config.getResourcePassword().isEmpty())
							resourceElement.setAttribute("password", config.getResourcePassword());
						resourceElement.setAttribute("url", config.getResourceUrl());
						resourceElement.setAttribute("for", config.getResourceFor());
						resourceElement.setAttribute("driverClassName", config.getResourceDriverClassName());
					}
				}
			}
		}
	}

	private void saveLuceneConfiguration(InstallerConfig config, Document document)
	{
		Lucene lucene = config.getLucene();
		if (lucene == null)
			return;

		Element luceneElement = getElement(document.getDocumentElement(), "Lucene");
		Element resourceElement = getElement(luceneElement, "Resource");

		if (lucene.getLocation() != null)
			luceneElement.setAttribute("luceneLocation", lucene.getLocation());

		if (lucene.getMaxBooleans() > 0)
		{
			resourceElement.setAttribute("lucene.max.booleans", String.valueOf(lucene.getMaxBooleans()));
		}

		if (lucene.getMergeFactor() > 0)
		{
			resourceElement.setAttribute("lucene.merge.factor", String.valueOf(lucene.getMergeFactor()));
		}

		if (lucene.getRmiPort() > 0)
		{
			resourceElement.setAttribute("lucene.rmi.port", String.valueOf(lucene.getRmiPort()));
		}

		if (lucene.getHighAvailabilitySearchNodes() > 0)
		{
			resourceElement.setAttribute("lucene.max.ha.search.nodes", String.valueOf(lucene.getHighAvailabilitySearchNodes()));
		}

		if (!lucene.getIndexHostName().isEmpty())
		{
			resourceElement.setAttribute("lucene.index.hostname", lucene.getIndexHostName());
		}

		// TODO: Handle high availability nodes
	}

	private void saveMemoryConfiguration(InstallerConfig config, Document document)
	{
		if (config.getJvmMemory() == null)
			return;

		Element memoryElement = getElement(document.getDocumentElement(), "Memory");
		Element jvmElement = getElement(memoryElement, "JavaVirtualMachine");

		jvmElement.setAttribute("mx", config.getJvmMemory());
	}

	private void saveWebDavConfiguration(InstallerConfig config, Document document)
	{
		if (config.getWebDav() == null)
			return;

		Element webDavElement = getElement(document.getDocumentElement(), "WebDAV");

		String value = "basic";
		if (config.getWebDav().equals(WebDAV.DIGEST))
			value = "digest";
		webDavElement.setAttribute("method", value);
	}

	private void saveNetworkConfiguration(InstallerConfig config, Document document)
	{
		Network network = config.getNetwork();

		if (network == null)
			return;

		Element webDavElement = getElement(document.getDocumentElement(), "Network");
		Element hostElement = getElement(webDavElement, "Host");

		if (hostElement != null)
		{
			hostElement.setAttribute("name", network.getHost());
			hostElement.setAttribute("port", String.valueOf(network.getPort()));
			hostElement.setAttribute("listenPort", String.valueOf(network.getListenPort()));
			hostElement.setAttribute("securePort", String.valueOf(network.getSecurePort()));
			hostElement.setAttribute("secureListenPort", String.valueOf(network.getSecureListenPort()));
			hostElement.setAttribute("shutdownPort", String.valueOf(network.getShutdownPort()));
			hostElement.setAttribute("ajpPort", String.valueOf(network.getAjpPort()));
			hostElement.setAttribute("keystoreFile", network.getKeystoreFile());
		}
	}

	private void saveWebServiceConfiguration(InstallerConfig config, Document document)
	{
		Network network = config.getNetwork();
		if (network == null)
			return;

		WebService webService = network.getWebService();

		if (webService == null)
			return;

		Element networkElement = getElement(document.getDocumentElement(), "Network");

		// WebServices
		Element webServicesElement = getElement(networkElement, "WebServices");
		if (webServicesElement != null)
		{
			webServicesElement.setAttribute("enable", String.valueOf(webService.isEnabled()));
		}

		// Basic WebServices
		Element basicWSElement = getElement(networkElement, "WebServicesBasic");
		if (basicWSElement != null)
		{
			basicWSElement.setAttribute("enable", String.valueOf(webService.isBasicEnabled()));
		}

		// Token WebServices
		Element tokenWSElement = getElement(networkElement, "WebServicesToken");
		if (tokenWSElement != null)
		{
			tokenWSElement.setAttribute("enable", String.valueOf(webService.isTokenEnabled()));
		}

		// Anonymous WebServices
		Element anonymousWSElement = getElement(networkElement, "WebServicesAnonymous");
		if (anonymousWSElement != null)
		{
			anonymousWSElement.setAttribute("enable", String.valueOf(webService.isAnonymousEnabled()));
		}

		// Session Timeout
		Element sessionElement = getElement(networkElement, "Session");
		if (sessionElement != null)
		{
			sessionElement.setAttribute("sessionTimeoutMinutes", String.valueOf(network.getSessionTimeoutMinutes()));
		}
	}

	private void saveClusteredConfiguration(InstallerConfig config, Document document)
	{
		Clustered clustered = config.getClustered();

		if (clustered == null)
			return;

		Element webDavElement = getElement(document.getDocumentElement(), "Clustered");

		if (webDavElement != null)
		{
			webDavElement.setAttribute("enable", String.valueOf(clustered.isEnabled()));
			webDavElement.setAttribute("cachingProvider", clustered.getCachingProvider());
			webDavElement.setAttribute("cacheService", clustered.getCacheService());
			webDavElement.setAttribute("cacheGroupAddress", clustered.getCacheGroupAddress());
			webDavElement.setAttribute("cacheGroupPort", String.valueOf(clustered.getCacheGroupPort()));
			webDavElement.setAttribute("memcachedAddresses", clustered.getMemCachedAddress());
			webDavElement.setAttribute("jvmRoute", clustered.getJvmRoute());
		}
	}

	private void saveSSOConfiguration(InstallerConfig config, Document document)
	{
		SSO sso = config.getSso();

		if (sso == null)
			return;

		Element ssoElement = getElement(document.getDocumentElement(), "SSO");

		if (ssoElement == null)
			return;

		{
			Element iChainElement = getElement(ssoElement, "iChain");
			if (iChainElement != null)
			{
				iChainElement.setAttribute("enable", String.valueOf(sso.isiChainEnabled()));
			}

			Element logoffUrlElement = getElement(iChainElement, "Logoff");
			if (logoffUrlElement != null)
			{
				logoffUrlElement.setAttribute("url", sso.getiChainLogoffUrl());
			}
		}

		{
			Element webDavProxyElement = getElement(ssoElement, "iChain");
			if (webDavProxyElement != null)
			{
				webDavProxyElement.setAttribute("enable", String.valueOf(sso.isiChainWebDAVProxyEnabled()));
				webDavProxyElement.setAttribute("host", sso.getiChainWebDAVProxyHost());
			}
		}
	}

	private void saveReqAndConnectionsConfiguration(InstallerConfig config, Document document)
	{
		RequestsAndConnections req = config.getRequestsAndConnections();

		if (req == null)
			return;

		Element reqElement = getElement(document.getDocumentElement(), "RequestsAndConnections");

		if (reqElement == null)
			return;

		reqElement.setAttribute("maxThreads", String.valueOf(req.getMaxThreads()));
		reqElement.setAttribute("maxActive", String.valueOf(req.getMaxActive()));
		reqElement.setAttribute("maxIdle", String.valueOf(req.getMaxIdle()));
	}

	private void saveEmailSettingsConfiguration(InstallerConfig config, Document document)
	{
		EmailSettings emailSettings = config.getEmailSettings();

		if (emailSettings == null)
			return;

		// Email Element
		Element emailElement = getElement(document.getDocumentElement(), "EmailSettings");
		if (emailElement == null)
			return;

		// Outbound Element
		Element outboundElement = getElement(emailElement, "Outbound");
		if (outboundElement == null)
			return;

		// Save TimeZone
		outboundElement.setAttribute("defaultTZ", emailSettings.getDefaultTZ());

		// Save allowSendToAllUsers
		outboundElement.setAttribute("allowSendToAllUsers", String.valueOf(emailSettings.isAllowSendToAllUsers()));

		// Resource Element
		Element resourceElement = getElement(outboundElement, "Resource");
		if (resourceElement == null)
			return;

		EmailProtocol protocol = emailSettings.getTransportProtocol();

		// Protocol
		String protocolStr = "smtp";
		if (protocol.equals(EmailProtocol.SMTPS))
			protocolStr = "smtps";
		resourceElement.setAttribute("mail.transport.protocol", protocolStr);

		// Outbound settings
		if (protocol.equals(EmailProtocol.SMTP))
		{
			resourceElement.setAttribute("mail.smtp.host", emailSettings.getSmtpHost());
			resourceElement.setAttribute("mail.smtp.auth", String.valueOf(emailSettings.isSmtpAuthEnabled()));
			resourceElement.setAttribute("mail.smtp.port", String.valueOf(emailSettings.getSmtpPort()));
			resourceElement.setAttribute("mail.smtp.sendpartial", String.valueOf(emailSettings.isSmtpSendPartial()));
			resourceElement.setAttribute("mail.smtp.user", emailSettings.getSmtpUser());

			if (emailSettings.getSmtpPassword() != null && !emailSettings.getSmtpPassword().isEmpty())
			{
				resourceElement.setAttribute("mail.smtp.password", emailSettings.getSmtpPassword());
			}
		}
		else
		{
			resourceElement.setAttribute("mail.smtps.host", emailSettings.getSmtpsHost());
			resourceElement.setAttribute("mail.smtps.auth", String.valueOf(emailSettings.isSmtpsAuthEnabled()));
			resourceElement.setAttribute("mail.smtps.port", String.valueOf(emailSettings.getSmtpsPort()));
			resourceElement.setAttribute("mail.smtps.sendpartial", String.valueOf(emailSettings.isSmtpsSendPartial()));
			resourceElement.setAttribute("mail.smtps.user", emailSettings.getSmtpsUser());

			if (emailSettings.getSmtpsPassword() != null && !emailSettings.getSmtpsPassword().isEmpty())
			{
				resourceElement.setAttribute("mail.smtps.password", emailSettings.getSmtpsPassword());
			}
		}

		// Inbound Settings
		Element inboundElement = getElement(emailElement, "InternalInboundSMTP");
		if (inboundElement == null)
			return;

		inboundElement.setAttribute("enable", String.valueOf(emailSettings.isInternalInboundSMTPEnabled()));
		if (emailSettings.getInternalInboundSMTPBindAddress() != null)
		{
			inboundElement.setAttribute("bindAddress", emailSettings.getInternalInboundSMTPBindAddress());
		}
		inboundElement.setAttribute("port", String.valueOf(emailSettings.getInternalInboundSMTPPort()));
		inboundElement.setAttribute("tls", String.valueOf(emailSettings.isInternalInboundSMTPTLSEnabld()));
	}

	/**
	 * Convert the string into integer
	 * 
	 * @param strValue
	 * @return
	 */
	private int getIntegerValue(String strValue)
	{

		// If null, return default value
		if (strValue == null || strValue.equals(""))
			return 0;

		try
		{
			return Integer.valueOf(strValue);
		}
		catch (NumberFormatException e)
		{
			logger.warn("Unable to convert " + strValue + " to integer value");
		}
		return 0;
	}

	/**
	 * Convert the string into boolean
	 * 
	 * @param strValue
	 * @return
	 */
	private boolean getBooleanValue(String strValue)
	{

		// If null, return default value
		if (strValue == null)
			return false;

		try
		{
			return Boolean.valueOf(strValue);
		}
		catch (NumberFormatException e)
		{
			logger.warn("Unable to convert " + strValue + " to boolean value");
		}
		return false;
	}

	@Override
	public ProductInfo getProductInfo()
	{
		// Look for the license key and figure out the product information
		ProductInfo productInfo = new ProductInfo();
		productInfo.setType(ProductType.NOVELL_FILR);

		productInfo.setProductVersion("Beta");

		productInfo.setCopyRight("� Copyright 1993-2012 Novell, Inc. All rights reserved.");

		File file = new File("/filrinstall/configured");
		productInfo.setConfigured(file.exists());

		String ipAddr = null;
		if (isUnix())
		{
			try
			{
				ipAddr = getLocalIpAddr();
			}
			catch (IOException e)
			{
				// Ignore..
			}
		}
		if (ipAddr != null)
			productInfo.setLocalIpAddress(ipAddr);
		else
		{
			InetAddress ip;
			try
			{
				ip = InetAddress.getLocalHost();
				productInfo.setLocalIpAddress(ip.getHostAddress());

			}
			catch (UnknownHostException e)
			{
				logger.warn("Unable to get local ip address");
			}
		}

		return productInfo;
	}

	private String getLocalIpAddr() throws IOException
	{
		String ipAddr = null;
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader("/proc/net/route"));
			String line;
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				String[] tokens = line.split("\\t");
				if (tokens.length > 1 && tokens[1].equals("00000000"))
				{
					String iface = tokens[0];
					NetworkInterface nif = NetworkInterface.getByName(iface);
					Enumeration<?> addrs = nif.getInetAddresses();
					while (addrs.hasMoreElements())
					{
						Object obj = addrs.nextElement();
						if (obj instanceof Inet4Address)
						{
							ipAddr = obj.toString();
							if (ipAddr.startsWith("/"))
								ipAddr = ipAddr.substring(1);
							return ipAddr;
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		return null;
	}

	public static boolean isUnix()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public ShellCommandInfo executeCommand(String command)
	{
		ShellCommandInfo commandInfo = new ShellCommandInfo();
		int tryCount = 0;
		int exitValue = -1;
		boolean waitingForLock = true;
		ShellCommand cmd = null;
		while (waitingForLock && (tryCount < MAX_TRIES))
		{
			// Don't sleep before the first try
			if (++tryCount > 1)
			{
				logger.info("waiting for RPM lock (try " + tryCount + " of " + MAX_TRIES + ")");
				// Sleep for a few seconds to allow RPM lock to free up
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}
			waitingForLock = false;
			try
			{
				cmd = new ShellCommand("sh");
				logger.info(command);
				cmd.stdin.write(command);
				cmd.stdin.close();
				cmd.waitFor();
				exitValue = cmd.exitValue();
				String output = null;
				if (exitValue == 0)
				{
					List<String> cmdOutput = new ArrayList<String>();
					while ((output = cmd.stdout.readLine()) != null)
					{
						cmdOutput.add(output);
						logger.info(output); // log stdout
					}
					commandInfo.setOutput(cmdOutput);
				}
				else
				{
					while ((output = cmd.stderr.readLine()) != null)
					{
						logger.info(output); // log stderr
						if (!waitingForLock)
						{
							// waitingForLock is cleared for each try and is
							// only
							// reset if the command fails and the stderr
							// contains
							// "cannot get exclusive lock". Once it is set we
							// stop checking for this message, as there will
							// normally
							// be additional lines of text such as:
							waitingForLock = (output.indexOf("cannot get exclusive lock") != -1);
						}
					}
				}
			}
			catch (IOException e)
			{
				logger.error("Exception thrown for command: " + command);
				e.printStackTrace();
				exitValue = -1;
			}
			logger.info(command + ": exitValue = " + exitValue);
		}
		if ((exitValue != 0) && waitingForLock && (tryCount >= MAX_TRIES))
		{
			logger.info("timed out waiting for lock");
		}
		commandInfo.setExitValue(exitValue);
		return commandInfo;
	}

	@Override
	public void createDatabase(Database database) throws ConfigurationSaveException
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{

			// Update the mysql-liquibase.properties
			DatabaseConfig dbConfig = database.getDatabaseConfig("Installed");

			// Check to see if database exists
			String resourceName = "root";
			String resourcePassword = "root";
			String resourceHost = "localhost";
			
			if (dbConfig.getResourceHost() != null)
				resourceHost = dbConfig.getResourceHost();
			
			if (dbConfig.getResourceUserName() != null)
				resourceName = dbConfig.getResourceUserName();
			
			if (dbConfig.getResourcePassword() != null)
				resourcePassword = dbConfig.getResourcePassword();
			
			if (checkDBExists("sitescape", dbConfig.getResourceUrl(), resourceName, resourcePassword))
				return;

			logger.info("Database does not exist, updating mysql-liquibase.properties");
			// Create database
			if (dbConfig != null)
			{

				// Update mysql-liquibase.properties
				File file = new File("/filrinstall/db/mysql-liquibase.properties");
				if (file.exists())
				{
					Properties prop = new Properties();
					try
					{
						prop.load(new FileInputStream(file));

						prop.setProperty("url", dbConfig.getResourceUrl());
						prop.setProperty("password", resourcePassword);
						prop.setProperty("username", resourceName);

						prop.setProperty("referenceUrl", dbConfig.getResourceUrl());
						prop.setProperty("referencePassword", resourcePassword);
						prop.setProperty("referenceUsername", resourceName);

						// Java Properties store escapes colon. We need to store
						// this natively.
						store(prop, file);
					}
					catch (IOException e)
					{
						logger.debug("Error saving properties file " + e.getMessage());
						throw new ConfigurationSaveException();
					}
				}

				// Create the database if needed
				int result = executeCommand(
						"mysql -h " + resourceHost + " -u" + resourceName + " -p"
								+ resourcePassword + " < /filrinstall/db/scripts/sql/mysql-create-empty-database.sql")
						.getExitValue();

				// We got an error ( 0 for success, 1 for database exists)
				if (!(result == 0 || result == 1))
				{
					logger.debug("Error creating database,Error code " + result);
					throw new ConfigurationSaveException();
				}
			}
		}

	}

	@Override
	public void updateDatabase(Database database) throws ConfigurationSaveException
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			// Update the database
			int result = executeCommand("cd /filrinstall/db; pwd; sh manage-database.sh mysql updateDatabase").getExitValue();

			// We got an error ( 0 for success, 107 for database exists)
			if (result != 0)
			{
				logger.debug("Error updating database,Error code " + result);
				throw new ConfigurationSaveException();
			}
		}

	}

	@Override
	public void reconfigure(boolean restartServer) throws ConfigurationSaveException
	{
		// Stop the server
		stopFilrServer();

		// Do the reconfigure which takes the changes from installer.xml and
		// reconfigures
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{

			// Run the reconfigure
			int result = executeCommand("cd /filrinstall; pwd; ./installer-filr.linux --silent --reconfigure").getExitValue();

			if (result != 0)
			{
				logger.debug("Error reconfiguring installer in silent mode,Error code " + result);
				throw new ConfigurationSaveException();
			}
			

			// Wizard configuration is done, put a temp file there
			File file = new File("/filrinstall/configured");
			// If it exists, ignore
			if (!System.getProperty("os.name").startsWith("Win") && !file.exists())
			{
				try
				{
					file.createNewFile();
				}
				catch (Exception e)
				{
					logger.debug("Error creating /filrinstall/configured file");
				}
			}
		}

		if (restartServer)
			startFilrServer();
	}

	public void stopFilrServer()
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			executeCommand("/sbin/rcfilr stop");

			int tries = 30;

			while (tries > 0)
			{
				try
				{
					Thread.sleep(1000);
					if (!isFilrServerRunning())
						return;
				}
				catch (MalformedURLException e)
				{
					logger.debug("Url is not valid for the filr server");
					return;
				}
				catch (IOException e)
				{
					logger.debug("Filr server is busy");
					return;
				}
				catch (InterruptedException e)
				{
					logger.debug("Waiting for filr servr to stop");
				}
				tries--;
			}
		}
	}

	@Override
	public void startFilrServer()
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			executeCommand("/sbin/rcfilr restart");

			int tries = 30;

			while (tries > 0)
			{
				try
				{
					Thread.sleep(1000);
					if (isFilrServerRunning())
						return;
				}
				catch (MalformedURLException e)
				{
					logger.debug("Url is not valid for the filr server");
					// TODO: We cannot handle this
					// Get out
					return;
				}
				catch (IOException e)
				{
					logger.debug("Filr server is busy");
					// Ignore
				}
				catch (InterruptedException e)
				{
					logger.debug("Waiting for filr servr to start");
				}
				tries--;
			}
		}
	}

	private boolean isFilrServerRunning() throws MalformedURLException, IOException
	{
		logger.debug("Check to see if Filr server is running");
		try
		{
			URL myURL = new URL(FILR_SERVER_URL);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.connect();
		}
		catch (MalformedURLException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		logger.debug("Filr server is running");
		return true;
	}

	@Override
	public void authenticateDbCredentials(String url, String userName, String password) throws LoginException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			logger.info("authenticating database credentials " + url);
			Connection conn = DriverManager.getConnection(url, userName, password); // Open a connection

			conn.close();
		}
		catch (Exception e)
		{
			logger.debug("Exception authenticating database credentials" + e.getLocalizedMessage());
			throw new LoginException();
		}
	}

	public boolean checkDBExists(String dbName, String url, String userName, String password)
	{

		logger.debug("Check if database exists");
		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			Connection conn = DriverManager.getConnection(url, userName, password); // Open a connection

			ResultSet resultSet = conn.getMetaData().getCatalogs();

			while (resultSet.next())
			{
				String databaseName = resultSet.getString(1);
				logger.debug("Database found =" + databaseName);
				if (databaseName.equals(dbName))
				{
					return true;
				}
			}
			resultSet.close();
			conn.close();

		}
		catch (Exception e)
		{
			logger.debug("Exception trying to check if database exists");
		}
		logger.debug("Database Not found");
		return false;
	}

	@Override
	public Boolean isLuceneServerValid(String host, long port) throws LuceneConnectException
	{
		logger.debug("Check if lucene server is valid");
		HttpURLConnection connection = null;
		try
		{
			URL u = new URL("http://" + host);
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("HEAD");
			int code = connection.getResponseCode();

			// We will get 403 for now
			logger.debug("Response code from lucene server " + code);
			return true;
		}
		catch (MalformedURLException e)
		{
			logger.info("Exception checking lucene server - Malformed Exception" + e.getMessage());
			throw new LuceneConnectException();
		}
		catch (IOException e)
		{
			logger.info("Exception checking lucene server - IO Exception" + e.getMessage());
			throw new LuceneConnectException();
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	@Override
	public Map<String, String> getTimeZones()
	{
		TreeMap<String, String> timeZoneMap = TimeZoneHelper.getTimeZoneIdDisplayStrings();
		return sortMapByValues(timeZoneMap);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValues(final Map<K, V> mapToSort)
	{
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(mapToSort.size());

		entries.addAll(mapToSort.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2)
			{
				return entry1.getValue().compareTo(entry2.getValue());
			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries)
		{
			sortedMap.put(entry.getKey(), entry.getValue());

		}

		return sortedMap;

	}
}
