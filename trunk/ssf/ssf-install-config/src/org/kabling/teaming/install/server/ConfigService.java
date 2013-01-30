package org.kabling.teaming.install.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.kabling.teaming.install.shared.Clustered;
import org.kabling.teaming.install.shared.ConfigurationSaveException;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.Environment;
import org.kabling.teaming.install.shared.FileConfig;
import org.kabling.teaming.install.shared.FileSystem;
import org.kabling.teaming.install.shared.HASearchNode;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.LoginException;
import org.kabling.teaming.install.shared.Lucene;
import org.kabling.teaming.install.shared.LuceneConnectException;
import org.kabling.teaming.install.shared.MirroredFolder;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.Presence;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.RSS;
import org.kabling.teaming.install.shared.RequestsAndConnections;
import org.kabling.teaming.install.shared.SSO;
import org.kabling.teaming.install.shared.ShellCommandInfo;
import org.kabling.teaming.install.shared.WebService;
import org.kabling.teaming.install.shared.DatabaseConfig.DatabaseType;
import org.kabling.teaming.install.shared.EmailSettings.EmailProtocol;
import org.kabling.teaming.install.shared.InstallerConfig.WebDAV;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ConfigService
{
	static Logger logger = Logger.getLogger("org.kabling.teaming.install.server.ConfigService");
	private static final int MAX_TRIES = 2;
	private static final String FILR_SERVER_URL = "http://localhost:8443";
	static final Pattern IPv4_ADDR = Pattern.compile("\\binet addr:\\s*([\\d\\.]+)\\b", Pattern.MULTILINE);

	public static InstallerConfig getConfiguration()
	{
		return getConfiguration("/filrinstall/installer.xml");
	}

	public static InstallerConfig getConfiguration(String installerXmlPath)
	{
		InstallerConfig config = null;
		Document document = null;
		try
		{
			document = getDocument(installerXmlPath);
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

	private static Document getDocument(String filePath) throws IOException
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
				file = new File(filePath);
			}

			if (file != null && file.exists())
			{
				is = new FileInputStream(file);
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
	private static InstallerConfig getInstallerConfig(Document document)
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
	private static Network getNetworkData(Document document)
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
			network.setPortRedirect(getBooleanValue(hostNode.getAttribute("portRedirect")));

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
	private static Environment getEnvironmentData(Document document)
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
	private static RequestsAndConnections getRequestsAndConnections(Document document)
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
	private static FileSystem getFileSystemData(Document document)
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
	private static Database getDatabaseData(Document document)
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
	private static Lucene getLuceneData(Document document)
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

		lucene.setServerLogin(resourceElement.getAttribute("lucene.server.login"));
		lucene.setServerPassword(resourceElement.getAttribute("lucene.server.password"));

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

				config.setServerLogin(hsaElement.getAttribute("lucene.server.login"));
				config.setServerPassword(hsaElement.getAttribute("lucene.server.password"));

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
	private static RSS getRSSData(Document document)
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
	private static EmailSettings getEmailSettingsData(Document document)
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

				emailSettings.setSmtpConnectionTimeout(getIntegerValue(currentElement.getAttribute("mail.smtp.connectiontimeout")));

				// Resource SMTPS Settings
				emailSettings.setSmtpsHost(currentElement.getAttribute("mail.smtps.host"));
				emailSettings.setSmtpsUser(currentElement.getAttribute("mail.smtps.user"));
				emailSettings.setSmtpsAuthEnabled(getBooleanValue(currentElement.getAttribute("mail.smtps.auth")));
				emailSettings.setSmtpsPort(getIntegerValue(currentElement.getAttribute("mail.smtps.port")));
				emailSettings.setSmtpsPassword(currentElement.getAttribute("mail.smtps.password"));
				emailSettings.setSmtpsSendPartial(getBooleanValue(currentElement.getAttribute("mail.smtps.sendpartial")));
				emailSettings.setSmtpsConnectionTimeout(getIntegerValue(currentElement.getAttribute("mail.smtps.connectiontimeout")));

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
	private static Presence getPresenceData(Document document)
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
	private static List<MirroredFolder> getMirroredFolders(Document document)
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

	private static Clustered getClusteredData(Document document)
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

	private static SSO getSSOData(Document document)
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
	private static Element getElement(Element parentElement, String tagName)
	{
		NodeList nodeList = parentElement.getElementsByTagName(tagName);
		if (nodeList != null && nodeList.getLength() > 0)
		{
			return (Element) nodeList.item(0);
		}
		return null;
	}

	public static void saveConfiguration(InstallerConfig config) throws ConfigurationSaveException
	{
		// Make a copy of the original installer.xml so that we can revert if we need to
		try
		{
			File srcFile = new File("/filrinstall/installer.xml");
			File destFile = new File("/filrinstall/installer.xml.orig");

			// If the file already exists, user has been making changes before applying the reconfigure action
			if (!destFile.exists())
				FileUtils.copyFile(srcFile, new File("/filrinstall/installer.xml.orig"), true);
		}
		catch (IOException ioe)
		{
			// We can ignore if the copy does not work
		}

		Document document = null;
		try
		{
			document = getDocument("/filrinstall/installer.xml");
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

				lsOutput.setByteStream(new FileOutputStream("/filrinstall/installer.xml"));
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
	public static void store(Properties props, File file) throws FileNotFoundException
	{

		PrintWriter pw = new PrintWriter(file);

		for (Enumeration e = props.propertyNames(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			pw.println(key + "=" + props.getProperty(key));
		}
		pw.close();
	}

	private static void saveDatabaseConfiguration(InstallerConfig installerConfig, Document document)
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

	private static void saveLuceneConfiguration(InstallerConfig config, Document document)
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

		if (!lucene.getServerLogin().isEmpty())
		{
			resourceElement.setAttribute("lucene.server.login", lucene.getServerLogin());
		}

		if (!lucene.getServerPassword().isEmpty())
		{
			resourceElement.setAttribute("lucene.server.password", lucene.getServerPassword());
		}

		if (lucene.getLocation().equals("ha"))
		{
			// Delete high availability nodes
			NodeList nodeList = resourceElement.getElementsByTagName("HASearchNode");
			if (nodeList != null)
			{
				while (nodeList.item(0) != null)
				{
					Node currentNode = nodeList.item(0);
					Node parent = currentNode.getParentNode();
					parent.removeChild(currentNode);

				}
			}

			for (int i = 0; i < lucene.getSearchNodesList().size(); i++)
			{
				HASearchNode searchNode = lucene.getSearchNodesList().get(i);

				Element node = document.createElement("HASearchNode");
				node.setAttribute("ha.service.name", searchNode.getName());
				node.setAttribute("ha.service.title", searchNode.getTitle());
				node.setAttribute("ha.service.hostname", searchNode.getHostName());
				node.setAttribute("ha.service.rmi.port", String.valueOf(searchNode.getRmiPort()));

				if (!searchNode.getServerLogin().isEmpty())
				{
					node.setAttribute("lucene.server.login", searchNode.getServerLogin());
				}

				if (!searchNode.getServerPassword().isEmpty())
				{
					node.setAttribute("lucene.server.password", searchNode.getServerPassword());
				}

				resourceElement.appendChild(node);
			}
		}

	}

	private static void saveMemoryConfiguration(InstallerConfig config, Document document)
	{
		if (config.getJvmMemory() == null)
			return;

		Element memoryElement = getElement(document.getDocumentElement(), "Memory");
		Element jvmElement = getElement(memoryElement, "JavaVirtualMachine");

		jvmElement.setAttribute("mx", config.getJvmMemory());
	}

	private static void saveWebDavConfiguration(InstallerConfig config, Document document)
	{
		if (config.getWebDav() == null)
			return;

		Element webDavElement = getElement(document.getDocumentElement(), "WebDAV");

		String value = "basic";
		if (config.getWebDav().equals(WebDAV.DIGEST))
			value = "digest";
		webDavElement.setAttribute("method", value);
	}

	private static void saveNetworkConfiguration(InstallerConfig config, Document document)
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
			hostElement.setAttribute("portRedirect", String.valueOf(network.isPortRedirect()));
		}
	}

	private static void saveWebServiceConfiguration(InstallerConfig config, Document document)
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

	private static void saveClusteredConfiguration(InstallerConfig config, Document document)
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

	private static void saveSSOConfiguration(InstallerConfig config, Document document)
	{
		SSO sso = config.getSso();

		if (sso == null)
			return;

		Element ssoElement = getElement(document.getDocumentElement(), "SSO");

		if (ssoElement == null)
			return;

		Element iChainElement = getElement(ssoElement, "iChain");
		{
			if (iChainElement != null)
			{
				iChainElement.setAttribute("enable", String.valueOf(sso.isiChainEnabled()));
			}

			Element proxyElement = getElement(iChainElement, "Proxy");
			if (proxyElement != null)
			{
				proxyElement.setAttribute("ipaddr", sso.getiChainProxyAddr());
			}

			Element logoffUrlElement = getElement(iChainElement, "Logoff");
			if (logoffUrlElement != null)
			{
				logoffUrlElement.setAttribute("url", sso.getiChainLogoffUrl());
			}

			Element webDavProxyElement = getElement(iChainElement, "WebDAVProxy");
			if (webDavProxyElement != null)
			{
				webDavProxyElement.setAttribute("enable", String.valueOf(sso.isiChainWebDAVProxyEnabled()));
				webDavProxyElement.setAttribute("host", sso.getiChainWebDAVProxyHost());
			}
		}
	}

	private static void saveReqAndConnectionsConfiguration(InstallerConfig config, Document document)
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

	private static void saveEmailSettingsConfiguration(InstallerConfig config, Document document)
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
			resourceElement.setAttribute("mail.smtp.connectiontimeout", String.valueOf(emailSettings.getSmtpConnectionTimeout()));

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
			resourceElement.setAttribute("mail.smtps.connectiontimeout", String.valueOf(emailSettings.getSmtpsConnectionTimeout()));

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
	private static int getIntegerValue(String strValue)
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
	private static boolean getBooleanValue(String strValue)
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

	public static ProductInfo getProductInfo()
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
			ipAddr = getLocalIpAddr();
			logger.debug("Local IP Adress " + ipAddr);
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

	private static String getLocalIpAddr()
	{

		ShellCommandInfo status = executeCommand("/sbin/ifconfig eth0", true);

		if (status.getExitValue() == 0)
		{
			String outputStr = status.getOutputAsString();

			if (outputStr != null)
			{
				Matcher m = IPv4_ADDR.matcher(outputStr);
				if (m.find() && m.groupCount() > 0)
				{
					return m.group(1);
				}

			}
		}

		return null;
	}

	public static boolean isUnix()
	{

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public static ShellCommandInfo executeCommand(String command, boolean displayToConsole)
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
				if (displayToConsole)
				{
					logger.info("Displaying to Console " + command);
				}
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
				if (displayToConsole)
					logger.error("Exception thrown for command: " + command);
				else
					logger.error("Exception thrown for command, not displayed");
				e.printStackTrace();
				exitValue = -1;
			}
			if (displayToConsole)
				logger.info(command + ": exitValue = " + exitValue);
		}
		if ((exitValue != 0) && waitingForLock && (tryCount >= MAX_TRIES))
		{
			logger.info("timed out waiting for lock");
		}
		commandInfo.setExitValue(exitValue);
		return commandInfo;
	}

	public static void updateMySqlLiquiBaseProperties(Database database) throws ConfigurationSaveException
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{

			// Update the mysql-liquibase.properties
			DatabaseConfig dbConfig = database.getDatabaseConfig("Installed");

			String resourceName = "root";
			String resourcePassword = "root";
			String resourceDatabase = "filr";

			if (dbConfig.getResourceUserName() != null)
				resourceName = dbConfig.getResourceUserName();

			if (dbConfig.getResourcePassword() != null)
				resourcePassword = dbConfig.getResourcePassword();

			if (dbConfig.getResourceDatabase() != null)
				resourceDatabase = dbConfig.getResourceDatabase();

			if (checkDBExists(resourceDatabase, dbConfig.getResourceUrl(), resourceName, resourcePassword))
			{
				logger.info("Database exists, but still updating mysql-liquibase.properties");
			}

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
			}
		}

	}

	public static void createDatabase(Database database) throws ConfigurationSaveException
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			updateMySqlLiquiBaseProperties(database);

			// Update the mysql-liquibase.properties
			DatabaseConfig dbConfig = database.getDatabaseConfig("Installed");

			// Check to see if database exists
			String resourceName = "root";
			String resourcePassword = "root";
			String resourceHost = "localhost";
			String databaseName = "filr";

			if (dbConfig.getResourceHost() != null)
				resourceHost = dbConfig.getResourceHost();

			if (dbConfig.getResourceUserName() != null)
				resourceName = dbConfig.getResourceUserName();

			if (dbConfig.getResourcePassword() != null)
				resourcePassword = dbConfig.getResourcePassword();

			if (dbConfig.getResourceDatabase() != null)
				databaseName = dbConfig.getResourceDatabase();

			if (checkDBExists(databaseName, dbConfig.getResourceUrl(), resourceName, resourcePassword))
				return;

			// Update mysql-create-empty-database.sql with the database name
			updateCreateScriptWithDatabaseName(databaseName);

			// Create the database if needed
			int result = executeCommand(
					"sudo mysql -h " + resourceHost + " -u" + resourceName + " -p'" + resourcePassword + "'"
							+ " < /filrinstall/db/scripts/sql/mysql-create-empty-database.sql", false).getExitValue();

			// We got an error ( 0 for success, 1 for database exists)
			if (!(result == 0 || result == 1))
			{
				logger.debug("Error creating database,Error code " + result);
				throw new ConfigurationSaveException();
			}
		}

	}

	private static void updateCreateScriptWithDatabaseName(String dbName)
	{
		try
		{
			FileInputStream fstream = new FileInputStream("/filrinstall/db/scripts/sql/mysql-create-empty-database.sql");
			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			StringBuilder updatedLine = new StringBuilder();

			while ((strLine = br.readLine()) != null)
			{
				// If it starts with create database, we will update the database name
				// 3rd token is the database name
				if (strLine.startsWith("create database"))
				{
					String tokens[] = strLine.split(" ");
					StringBuilder lineStr = new StringBuilder();
					for (int i = 0; i < tokens.length; i++)
					{
						if (i == 2)
							lineStr.append(dbName);
						else
							lineStr.append(tokens[i]);

						lineStr.append(" ");
					}
					updatedLine.append(lineStr.toString());
					updatedLine.append("\n");
				}
				else
				{
					updatedLine.append(strLine);
					updatedLine.append("\n");
				}
			}
			in.close();

			// Put the changes back
			FileWriter fileWriter = new FileWriter("/filrinstall/db/scripts/sql/mysql-create-empty-database.sql");
			BufferedWriter out = new BufferedWriter(fileWriter);
			out.write(updatedLine.toString());
			out.close();
		}
		catch (Exception e)
		{
			logger.debug("Error updating mysql-create-empty-database.sql file");
			throw new ConfigurationSaveException();
		}
	}

	public static void updateDatabase(Database database) throws ConfigurationSaveException
	{
		updateMySqlLiquiBaseProperties(database);
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			// Update the database
			int result = executeCommand("cd /filrinstall/db; pwd; sudo sh manage-database.sh mysql updateDatabase", true).getExitValue();

			// We got an error ( 0 for success, 107 for database exists)
			if (result != 0)
			{
				logger.debug("Error updating database,Error code " + result);
				throw new ConfigurationSaveException();
			}
		}

	}

	public static void reconfigure(boolean restartServer) throws ConfigurationSaveException
	{
		// Stop the server
		stopFilrServer();

		// Do the reconfigure which takes the changes from installer.xml and
		// reconfigures
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			// Run the reconfigure
			ShellCommandInfo info = executeCommand("cd /filrinstall;sudo ./installer-filr.linux --silent --reconfigure", true);

			if (info.getExitValue() != 0)
			{
				if (info.getOutput() != null)
				{
					for (String debugStr : info.getOutput())
					{
						logger.info(debugStr);
					}
				}
				logger.debug("Error reconfiguring installer in silent mode,Error code " + info.getExitValue());
				throw new ConfigurationSaveException();
			}

			// Setup Encoding on hostname information in installer.xml
			info = executeCommand("sudo hostname -f", true);
			List<String> outputList = info.getOutput();
			if (info.getExitValue() == 0 && outputList != null && outputList.get(0) != null)
			{
				String hostName = info.getOutput().get(0);
				info = executeCommand("sudo sed -i -e's/@@HOSTNAME@@/" + hostName + "/' /filrinstall/installer.xml", true);
				if (info.getExitValue() != 0)
				{
					logger.debug("Unable to set hostName " + info.getExitValue());
				}
			}

			// Setup Encoding on /etc/init.d/teaming file
			info = executeCommand("sudo sed -i -e's/ISO8859-1/UTF-8/' /etc/init.d/teaming", true);
			if (info.getExitValue() != 0)
			{
				logger.debug("Error setting up UTF-8 Encoding " + info.getExitValue());
				throw new ConfigurationSaveException();
			}

			// Replace Novell Vibe with Novell Filr on ssf-ext.properties
			info = executeCommand(
					"sudo sed -i \"s/Novell Vibe/Novell Filr/g\" /opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties",
					true);
			if (info.getExitValue() != 0)
			{
				logger.debug("Error setting up Novell Filr string on ssf-ext.properties" + info.getExitValue());
				throw new ConfigurationSaveException();
			}

			// Setup luceneindex.root.dir on ssf-ext.properties
			info = executeCommand(
					"sudo sed -i  -e 's|data.luceneindex.root.dir=/vastorage/filr|data.luceneindex.root.dir=/vastorage/search|' /opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties",
					true);
			if (info.getExitValue() != 0)
			{
				logger.debug("Error setting up data.luceneindex.root.dir=/vastorage/search" + info.getExitValue());
				throw new ConfigurationSaveException();
			}

			// If the configuration is not done, add this to restart filr after reboot
			File file = new File("/filrinstall/configured");
			// If it exists, ignore
			if (!file.exists())
			{
				// Setup chkconfig so that teaming starts always
				info = executeCommand("sudo chkconfig teaming on", true);
			}
		}

		// Delete the backup copy
		File srcFile = new File("/filrinstall/installer.xml.orig");
		if (srcFile.exists())
		{
			boolean deleteOrigStatus = srcFile.delete();
			logger.debug("Deleted Backup /filrinstall/installer.xml.orig " + deleteOrigStatus);
		}
		else
		{
			logger.debug("Does not exists for deletion /filrinstall/installer.xml.orig ");
		}
		if (restartServer)
			startFilrServer();
	}

	public static void stopFilrServer()
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			executeCommand("sudo /sbin/rcfilr stop", true);

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

	public static void startFilrServer()
	{
		if (getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			// Update security constraints based on network page settings
			updateSecurityBasedOnNetworkPageSettings();

			executeCommand("sudo /sbin/rcfilr restart", true);

			int tries = 2;

			while (tries > 0)
			{
				try
				{
					// Sleeping for minute and 20 seconds
					// This is temporary workaround
					Thread.sleep(80000);
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

	private static void updateSecurityBasedOnNetworkPageSettings()
	{
		InstallerConfig config = getConfiguration();
		Network network = config.getNetwork();

		String WEB_XML1 = "/opt/novell/filr/apache-tomcat/webapps/ROOT/WEB-INF/web.xml";
		String WEB_XML2 = "/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/web.xml";
		String WEB_XML3 = "/opt/novell/filr/apache-tomcat/webapps/ssr/WEB-INF/web.xml";
		String WEB_XML4 = "/opt/novell/filr/apache-tomcat/webapps/rest/WEB-INF/web.xml";

		String ipAddr = getLocalIpAddr();

		// Http 8080 disabled
		if (network.getListenPort() == 0)
		{
			// Call addSecurityConstraint.py pointing to web.xml, we need to update all the 4 web.xml
			executeCommand("sudo python /opt/novell/filr_config/addSecurityConstraint.py " + WEB_XML1, true);
			executeCommand("sudo python /opt/novell/filr_config/addSecurityConstraint.py " + WEB_XML2, true);
			executeCommand("sudo python /opt/novell/filr_config/addSecurityConstraint.py " + WEB_XML3, true);
			executeCommand("sudo python /opt/novell/filr_config/addSecurityConstraint.py " + WEB_XML4, true);
		}
		else
		{
			// Call addSecurityConstraint.py pointing to web.xml, we need to update all the 4 web.xml
			executeCommand("sudo python /opt/novell/filr_config/removeSecurityConstraint.py " + WEB_XML1, true);
			executeCommand("sudo python /opt/novell/filr_config/removeSecurityConstraint.py " + WEB_XML2, true);
			executeCommand("sudo python /opt/novell/filr_config/removeSecurityConstraint.py " + WEB_XML3, true);
			executeCommand("sudo python /opt/novell/filr_config/removeSecurityConstraint.py " + WEB_XML4, true);

			// Enable the firewall for this port
			executeCommand("sudo SuSEfirewall2 open EXT TCP " + network.getListenPort(), true);
		}

		if (network.isPortRedirect())
		{
			executeCommand("sudo python /opt/novell/filr_config/updateFirewallRedirect.py " + ipAddr + " " + network.getSecureListenPort()
					+ " " + network.getListenPort(), true);

			// Enable the firewall for this port 80 443
			executeCommand("sudo SuSEfirewall2 open EXT TCP 80 443", true);

			// We need to open up both 8080 and 8443 for port redirection to work
			executeCommand("sudo SuSEfirewall2 open EXT TCP " + network.getListenPort() + " " + network.getSecureListenPort(), true);
		}
		else
		{
			executeCommand("sudo python /opt/novell/filr_config/updateFirewallRedirect.py " + ipAddr + " 443 80", true);

			// Enable the firewall for this port 80 443
			executeCommand("sudo SuSEfirewall2 close EXT TCP 80 443", true);
		}

		// Restart the firewall after the changes
		executeCommand("sudo SuSEfirewall2 stop", true);
		executeCommand("sudo SuSEfirewall2 start", true);
	}

	private static boolean isFilrServerRunning() throws MalformedURLException, IOException
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

	public static void authenticateDbCredentials(String url, String userName, String password) throws LoginException
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

	public static boolean checkDBExists(String dbName, String url, String userName, String password)
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

	public static Boolean isLuceneServerValid(String host, long port) throws LuceneConnectException
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

	public static Map<String, String> getTimeZones()
	{
		TreeMap<String, String> timeZoneMap = TimeZoneHelper.getTimeZoneIdDisplayStrings();
		return timeZoneMap;
	}

	public static void markConfigurationDone(String configType)
	{
		// Wizard configuration is done, put a temp file there
		File file = new File("/filrinstall/configured");
		// If it exists, ignore
		if (!System.getProperty("os.name").startsWith("Win") && !file.exists())
		{
			try
			{
				// Create configured file
				file.createNewFile();

				// Also create configurationDetails.properties file to store if this is small or large deployment
				File configurationDetailsFile = new File("/filrinstall/configurationDetails.properties");

				// During upgrade process, this file may exists, in that case, we don't want to overwrite
				if (!configurationDetailsFile.exists())
				{
					// Save the property of the type of configuration
					Properties prop = new Properties();
					prop.setProperty("type", configType);

					// Store the properties
					store(prop, configurationDetailsFile);
				}
				else
				{
					Properties prop = new Properties();
					prop.load(new FileInputStream(configurationDetailsFile));
					configType = prop.getProperty("type");
				}

				// For large deployment, we need to disable sql and lucene
				if (configType.equals("large"))
				{
					disableMySqlAndLucene();
				}

			}
			catch (Exception e)
			{
				logger.debug("Error creating /filrinstall/configured file");
			}
		}
	}

	private static void disableMySqlAndLucene()
	{
		// Disable filrsearch and mysql for large deployment
		executeCommand("sudo mv /etc/opt/novell/ganglia/monitor/conf.d/filrsearch.pyconf "
				+ " /etc/opt/novell/ganglia/monitor/conf.d/filrsearch.pyconf.disabled", true);

		executeCommand("sudo mv /etc/opt/novell/ganglia/monitor/conf.d/mysql.pyconf "
				+ " /etc/opt/novell/ganglia/monitor/conf.d/mysql.pyconf.disabled", true);

		executeCommand("sudo rcmysql stop", true);
	}

	public static void reverConfiguration() throws IOException
	{
		File srcFile = new File("/filrinstall/installer.xml.orig");
		if (srcFile.exists())
			FileUtils.copyFile(srcFile, new File("/filrinstall/installer.xml"), true);

		srcFile.delete();
	}

	public static boolean isUnsavedConfigurationExists()
	{
		File srcFile = new File("/filrinstall/installer.xml.orig");
		return srcFile.exists();
	}

	public static void setupLocalMySqlUserPassword(Database db)
	{
		DatabaseConfig config = db.getDatabaseConfig("Installed");
		ShellCommandInfo info = executeCommand("mysqladmin -uroot -proot password '" + config.getResourcePassword() + "'", false);

		logger.debug("mysqladmin setting up admin password exit Value" + info.getExitValue());
		if (info.getExitValue() != 0)
		{
			throw new ConfigurationSaveException();
		}
	}

	public static LicenseInformation getLicenseInformation()
	{
		return getLicenseInformation("/filrinstall/license-key.xml");
	}

	public static LicenseInformation getLicenseInformation(String licensePath)
	{
		LicenseInformation licenseInfo = new LicenseInformation();
		try
		{
			Document document = getDocument(licensePath);

			Element keyInfoElement = getElement(document.getDocumentElement(), "KeyInfo");

			if (keyInfoElement != null)
			{
				licenseInfo.setKeyVersion(keyInfoElement.getAttribute("keyversion"));
				licenseInfo.setIssuedBy(keyInfoElement.getAttribute("by"));
				licenseInfo.setIssuedDate(keyInfoElement.getAttribute("issued"));
			}

			Element datesElement = getElement(document.getDocumentElement(), "Dates");
			if (datesElement != null)
			{
				licenseInfo.setExpirationDate(datesElement.getAttribute("expiration"));
				licenseInfo.setDatesEffective(datesElement.getAttribute("Effective"));
			}

			Element productElement = getElement(document.getDocumentElement(), "Product");
			if (productElement != null)
			{
				licenseInfo.setProductId(productElement.getAttribute("id"));
				licenseInfo.setProductVersion(productElement.getAttribute("version"));
				licenseInfo.setProductTitle(productElement.getAttribute("title"));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licenseInfo;
	}
}
