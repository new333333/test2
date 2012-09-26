package org.kabling.teaming.install.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.kabling.teaming.install.client.InstallService;
import org.kabling.teaming.install.shared.Clustered;
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
import org.kabling.teaming.install.shared.MirroredFolder;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.Presence;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;
import org.kabling.teaming.install.shared.RSS;
import org.kabling.teaming.install.shared.RequestsAndConnections;
import org.kabling.teaming.install.shared.WebService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstallServiceImpl extends RemoteServiceServlet implements InstallService
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server");

	@Override
	public LoginInfo login(String userName, String password) throws LoginException
	{
		LoginInfo loginInfo = new LoginInfo();

		// Login to the database?

		// Look for license information and figure out if they have a valid license

		return loginInfo;
	}

	public InstallerConfig getConfiguration()
	{
		InstallerConfig config = null;
		// TODO Get the authentication information
		// TODO How are we going to get the database connection information?
		// TODO Get the installer.xml data from the database

		Document document = getDocument();

		if (document != null)
			config = getInstallerConfig(document);

		// TODO Get the data from Lucene server and other servers and make sure we
		// update it again with those data
		return config;
	}

	private Document getDocument()
	{
		try
		{

			// create a DOM parser
			DOMParser parser = new DOMParser();

			File file = new File("/filrinstall/installer.xml");
			InputStream is = null;
			if (file.exists())
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
			return parser.getDocument();
		}
		catch (IOException e)
		{
		}
		catch (SAXException e)
		{
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
				config.setJvmMemory(getIntegerValue(jvmElement.getAttribute("mx")));
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

		// Cluster
		config.setClustered(getClusteredData(document));
		return config;
	}

	/**
	 * Parse through the installer.xml "Network" section and return a java object
	 * 
	 * <Network> 
	 * 		<Host name="localhost" port="8080" listenPort="8080" securePort="8443" secureListenPort="8443" shutdownPort="8005"
	 * ajpPort="8009" keystoreFile="" /> 
	 * 		<WebServices enable="true" /> 
	 * 		<WebServicesBasic enable="true" />
	 * 		<WebServicesToken enable="true" />
	 * 		<WebServicesAnonymous enable="false" />
	 * 		 <Session sessionTimeoutMinutes="240" /> 
	 * </Network>
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
			webService.setEnabled(getBooleanValue(webServiceNode.getAttribute("enabled")));
		}

		// Web Service Basic
		Element basicNode = getElement(networkNode, "WebServicesBasic");
		if (basicNode != null)
		{
			webService.setBasicEnabled(getBooleanValue(basicNode.getAttribute("enabled")));
		}

		// Web Service Token
		Element tokenNode = getElement(networkNode, "WebServicesToken");
		if (tokenNode != null)
		{
			webService.setTokenEnabled(getBooleanValue(tokenNode.getAttribute("enabled")));
		}

		// Web Service Anonymous
		Element anonymousNode = getElement(networkNode, "WebServicesAnonymous");
		if (anonymousNode != null)
		{
			webService.setAnonymousEnabled(getBooleanValue(anonymousNode.getAttribute("enabled")));
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
	 * <!-- Where does the Kablink software reside? -->
	 * <SoftwareLocation path="" />
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
				env.setJdkHome(currentElement.getAttribute("JDK_HOME"));
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
	 * <RequestsAndConnections> 
	 * 		<maxThreads value="200" /> 
	 * 		<maxActive value="50" /> 
	 * 		<maxIdle value="20" /> 
	 * </RequestsAndConnections>
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

		// Max Threads
		Element currentElement = getElement(rootNode, "maxThreads");

		if (currentElement != null)
		{
			requestAndConnections.setMaxThreads(getIntegerValue(currentElement.getAttribute("value")));
		}

		// Max Active
		currentElement = getElement(rootNode, "maxActive");

		if (currentElement != null)
		{
			requestAndConnections.setMaxActive(getIntegerValue(currentElement.getAttribute("value")));
		}

		// Max Idle
		currentElement = getElement(rootNode, "maxIdle");

		if (currentElement != null)
		{
			requestAndConnections.setMaxIdle(getIntegerValue(currentElement.getAttribute("value")));
		}

		return requestAndConnections;
	}

	/**
	 * Parse through the installer.xml "RequestsAndConnections" section and return a java object
	 * 
	 * <RequestsAndConnections> 
	 * 		<maxThreads value="200" /> 
	 * 		<maxActive value="50" /> 
	 * 		<maxIdle value="20" /> 
	 * </RequestsAndConnections>
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
	 * <Database configName="MySQL_Default">
	 *   <!--                                                       -->
	 *   <!--                  MySQL_Default                        -->
	 *   <!--                                                       -->
	 *   <Config id="MySQL_Default" type="MySql">
	 *       <Resource for="icecore"
	 *           driverClassName="com.mysql.jdbc.Driver"
	 *           url="jdbc:mysql://localhost:3306/sitescape?useUnicode=true&amp;characterEncoding=UTF-8"
	 *           username="root"
	 *           password=""
	 *       />
	 *   </Config>
	 *
	 *   <!--                                                       -->
	 *    <!--                 SQLServer_Default                     -->
	 *    <!--                                                       -->
	 *    <Config id="SQLServer_Default" type="SQLServer">
	 *       <Resource for="icecore"
	 *           driverClassName="net.sourceforge.jtds.jdbc.Driver"
	 *           url="jdbc:jtds:sqlserver://localhost/sitescape;SelectMethod=cursor"
	 *           username="sa"
	 *           password=""
	 *        />
	 *   </Config>
	 *
	 *  <!--                                                       -->
	 *  <!--                 Oracle_Default                        -->
	 *   <!--                                                       -->
	 *   <Config id="Oracle_Default" type="Oracle">
	 *       <Resource for="icecore"
	 *           driverClassName="oracle.jdbc.driver.OracleDriver"
	 *           url="jdbc:oracle:thin:@//localhost:1521/orcl"
	 *           username=""
	 *           password=""
	 *        />
	 *   </Config>
	 *</Database>  
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
	 *   <Lucene luceneLocation="local">
	 *   <Resource 
	 *       lucene.index.hostname="localhost"
	 *		lucene.max.booleans="10000"
	 *	    lucene.max.ha.search.nodes="100"
	 *		lucene.merge.factor="10"
	 *		lucene.rmi.port="1199"
	 *  >
	 *   	<HASearchNode
	 *  		ha.service.name="node1"
	 *  		ha.service.title="This is node1"
	 *  		ha.service.hostname="xxx.xxx.xxx.xxx"
	 *  		ha.service.rmi.port="1199"
	 * 	/>
	 *  	<HASearchNode
	 * 		ha.service.name="node2"
	 * 		ha.service.title="This is node2"
	 * 		ha.service.hostname="yyy.yyy.yyy.yyy"
	 * 		ha.service.rmi.port="1199"
	 *	/>
	 *  </Resource>
	 *</Lucene>
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
		lucene.setHighAvailabilitySearchNodes(getIntegerValue(resourceElement
				.getAttribute("lucene.max.ha.search.nodes")));
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
	 * <RSS enable="true">
	 * 		<Feed max.elapseddays="31" max.inactivedays="7" />
	 * </RSS>

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
	 *  <EmailSettings>
	 *   	<InternalInboundSMTP enable="false" bindAddress="" port="2525" tls="true" />
	 *
	 *   	<Outbound
	 *                           defaultTZ="America/New_York"
	 *                           allowSendToAllUsers="false"
	 *          >
	 *   <Resource
	 *          mail.transport.protocol="smtp"
	 *
	 *         mail.smtp.host="mailhost.yourcompany.com"
	 *         mail.smtp.user="vibe@yourcompany.com"
	 *         mail.smtp.password=""
	 *         mail.smtp.auth="false"
	 *         mail.smtp.port="25"
	 *          mail.smtp.sendpartial="true"
	 *
	 *         mail.smtps.host="mailhost.yourcompany.com"
	 *         mail.smtps.user="vibe@yourcompany.com"
	 *          mail.smtps.password=""
	 *         mail.smtps.auth="false"
	 *         mail.smtps.port="465"
	 *         mail.smtps.sendpartial="true"
	 *
	 *     />
	 *  </Outbound>
	 *  </EmailSettings>
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
			emailSettings.setDefaultTZ(currentElement.getAttribute("defaultTZ"));
			emailSettings.setAllowSendToAllUsers(getBooleanValue(currentElement.getAttribute("allowSendToAllUsers")));

			currentElement = getElement(currentElement, "Resource");

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
				emailSettings
						.setSmtpsSendPartial(getBooleanValue(currentElement.getAttribute("mail.smtps.sendpartial")));

			}

		}

		return emailSettings;
	}

	/**
	  ** <Presence>
	  *  	<Resource 
	  *      	 presence.service.enable="false"
	  *     	 presence.service.server.address=""
	  *     	 presence.service.server.port="8300"
	  *     	 presence.service.server.cert=""
	  *     	 presence.service.user.dn=""
	  *    		 presence.service.user.password=""/>
	  * </Presence>
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
	 * <MirroredFolders>
	 *	<MirroredFolder enabled="false" type="file" 
	 *	                id="rd1" title="Shared Files 1"
	 *	                rootPath="k:/somedir" readonly="true" zoneId="">
	 *		<AllowedUsers idList="admin;u1;u2;u3" />
	 *		<AllowedGroups idList="g1;g2;g3" />
	 *	</MirroredFolder>
	 *
	 *	<MirroredFolder enabled="false" type="file" 
	 *	                id="rd2" title="Shared Files 2"
	 *	                rootPath="/sharedFiles/someDirectory" readonly="true" zoneId="">
	 *		<AllowedUsers idList="admin;u1;u2;u3" />
	 *		<AllowedGroups idList="g1;g2;g3" />
	 *	</MirroredFolder>
	 *
	 *	<MirroredFolder enabled="false" type="webdav"
	 *	                id="rd3" title="WebDAV 1" 
	 *	                rootPath="/Shared Documents/cool-dir" readonly="true" zoneId="">
	 *	    <WebDAVContext hostUrl="http://hostname" user="accessId" password="" />
	 *		<AllowedUsers idList="admin;u1;u2;u3" />
	 *	 	<AllowedGroups idList="g1;g2;g3" />
	 *	</MirroredFolder>
	 * </MirroredFolders>
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
	public void saveConfiguration(InstallerConfig config)
	{
		Document document = getDocument();
		ProductInfo productInfo = getProductInfo();
		// We need to save it locally
		if (productInfo.getType().equals(ProductType.NOVELL_FILR) && !productInfo.isConfigured())
		{
			saveLuceneConfiguration(config, document);
			saveDatabaseConfiguration(config, document);
		}
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

						configElement.setAttribute("type", config.getType().toString());

						resourceElement.setAttribute("username", config.getResourceUserName());
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
			resourceElement.setAttribute("lucene.max.ha.search.nodes",
					String.valueOf(lucene.getHighAvailabilitySearchNodes()));
		}

		if (!lucene.getIndexHostName().isEmpty())
		{
			resourceElement.setAttribute("lucene.index.hostname",
					String.valueOf(lucene.getHighAvailabilitySearchNodes()));
		}

		// TODO: Handle high availability nodes
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
		if (strValue == null)
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
		
		File file = new File("/filrinstall/installer.xml");
		productInfo.setConfigured(file.exists());

		return productInfo;
	}
}
