package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigModifiedEvent;
import org.kabling.teaming.install.client.ConfigModifiedEvent.ConfigModifiedEventHandler;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.Clustered;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.DatabaseConfig.DatabaseType;
import org.kabling.teaming.install.shared.EmailSettings.EmailProtocol;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.InstallerConfig.WebDAV;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.RequestsAndConnections;
import org.kabling.teaming.install.shared.WebService;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ConfigSummaryPage extends Composite implements ConfigModifiedEventHandler
{
	private GetConfigInformationCallback getConfigCallback = new GetConfigInformationCallback();
	private FlowPanel content;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private InstallerConfig config;

	public ConfigSummaryPage()
	{
		content = new FlowPanel();
		initWidget(content);

		// Look for config modifications
		AppUtil.getEventBus().addHandler(ConfigModifiedEvent.TYPE, this);

		AppUtil.getInstallService().getConfiguration(getConfigCallback);
	}

	private void buildUI()
	{

		Label titleLabel = new Label(RBUNDLE.configurationSummary());
		titleLabel.addStyleName("configSummaryTitle");
		content.add(titleLabel);

		// Network Section
		content.add(buildNetworkSection());

		// Database Section
		content.add(buildDatabaseSection());

		// WebDav Section
		content.add(buildWebDavSection());

		// Java JDK Section
		content.add(buildJavaJDKSection());

		// Requests and Connections
		content.add(buildReqAndConnectionsSection());

		// Web Services
		content.add(buildWebServiceSection());

		// Outbound
		content.add(buildOutboundEmailSection());

		// Inbound
		content.add(buildInboundEmailSection());

		// Clustering
		content.add(buildClusteringSection());

		// Reverse Proxy
		// content.add(buildReverseProxySection());
	}

	private FlowPanel buildNetworkSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.network());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		Network network = config.getNetwork();

		if (config.getNetwork() != null)
		{
			int row = 0;
			{
				// Host Name
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(network.getHost());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Http Port
				keyLabel = new InlineLabel(RBUNDLE.httpPortColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(network.getPort()));
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			row++;
			{
				// Secure HTTP Port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.secureHttpPortColon());
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");
				table.setWidget(row, 0, keyLabel);

				InlineLabel valueLabel = new InlineLabel(String.valueOf(network.getSecurePort()));
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, valueLabel);

				// Listen Port
				keyLabel = new InlineLabel(RBUNDLE.listenPortColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(network.getListenPort()));
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
				table.setWidget(row, 3, valueLabel);

			}

			row++;
			{
				// Secure Listen Port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.secureListenPortColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(String.valueOf(network.getSecureListenPort()));
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, valueLabel);

				// Shutdown port
				keyLabel = new InlineLabel(RBUNDLE.shutdownPortColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(network.getShutdownPort()));
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
				table.setWidget(row, 3, valueLabel);
			}

			row++;
			{
				// AJP port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.ajpPortColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(String.valueOf(network.getAjpPort()));
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, valueLabel);

				// Session Time out
				keyLabel = new InlineLabel(RBUNDLE.sessionTimeOutColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(network.getSessionTimeoutMinutes()));
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
				table.setWidget(row, 3, valueLabel);
			}

			row++;
			{
				// KeyStore
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.keyStoreFileColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(network.getKeystoreFile());
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, valueLabel);
			}
		}
		return sectionPanel;
	}

	private FlowPanel buildDatabaseSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.database());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		Database db = config.getDatabase();

		if (db != null && db.getDatabaseConfig("Installed") != null)
		{
			DatabaseConfig dbConfig = db.getDatabaseConfig("Installed");
			int row = 0;
			{
				// Host Name
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(dbConfig.getResourceHost());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// User Name
				keyLabel = new InlineLabel(RBUNDLE.userNameColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(dbConfig.getResourceUserName()));
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}
			row++;
			{
				// Type
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.dbTypeColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				String dbType = RBUNDLE.mysql();
				if (dbConfig.getType().equals(DatabaseType.ORACLE))
					dbType = RBUNDLE.oracle();
				InlineLabel valueLabel = new InlineLabel(dbType);
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}
		}
		return sectionPanel;
	}

	private FlowPanel buildWebDavSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.webDavAuthentication());

		FlowPanel content = new FlowPanel();
		content.addStyleName("configSummaryDivContent");
		sectionPanel.add(content);

		InlineLabel authLabel = new InlineLabel(RBUNDLE.webDavPageAuthMethodColon());
		authLabel.addStyleName("configSummaryKeyLabel");
		content.add(authLabel);

		String webDav = RBUNDLE.basic();
		if (config.getWebDav().equals(WebDAV.DIGEST))
			webDav = RBUNDLE.digest();

		InlineLabel authValueLabel = new InlineLabel(webDav);
		authValueLabel.addStyleName("configSummaryValueLabel");
		content.add(authValueLabel);
		return sectionPanel;
	}

	private FlowPanel buildJavaJDKSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.javaJDK());

		FlowPanel content = new FlowPanel();
		content.addStyleName("configSummaryDivContent");
		sectionPanel.add(content);

		InlineLabel keyLabel = new InlineLabel(RBUNDLE.jvmHeapSizeColon());
		keyLabel.addStyleName("configSummaryKeyLabel");
		content.add(keyLabel);

		InlineLabel valueLabel = new InlineLabel(config.getJvmMemory());
		valueLabel.addStyleName("configSummaryValueLabel");
		content.add(valueLabel);
		return sectionPanel;
	}

	private FlowPanel buildReqAndConnectionsSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.requestsAndConnections());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		RequestsAndConnections reqConnections = config.getRequestsAndConnections();

		if (reqConnections != null)
		{
			int row = 0;
			{
				// Max Threads
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxThreadsColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(String.valueOf(reqConnections.getMaxThreads()));
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}

			row++;
			{
				// Max Active
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxActiveColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(String.valueOf(reqConnections.getMaxActive()));
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}

			row++;
			{
				// Max Idle
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxIdleColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(String.valueOf(reqConnections.getMaxIdle()));
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}
		}
		return sectionPanel;
	}

	private FlowPanel buildWebServiceSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.webServices());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		Network db = config.getNetwork();
		WebService webService = db.getWebService();

		if (webService != null)
		{
			int row = 0;
			{
				// Enabled
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.enabledColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				boolean value = webService.isEnabled();
				InlineLabel valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Basic Auth
				keyLabel = new InlineLabel(RBUNDLE.basicAuthColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				value = webService.isBasicEnabled();
				valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}
			row++;
			{
				// Token
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.tokenBasicAuthColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				boolean value = webService.isTokenEnabled();
				InlineLabel valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Anonymous
				keyLabel = new InlineLabel(RBUNDLE.anonymousAuthColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				value = webService.isAnonymousEnabled();
				valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}
		}
		return sectionPanel;
	}

	private FlowPanel buildOutboundEmailSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.outboundEmail());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		EmailSettings emailSettings = config.getEmailSettings();

		if (emailSettings != null)
		{
			EmailProtocol protocol = emailSettings.getTransportProtocol();
			boolean smtpTransport = protocol.equals(EmailProtocol.SMTP);

			int row = 0;
			{
				// Auth Required (based on what protocol is selected)
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.authRequired());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				boolean value = false;
				if (smtpTransport)
					value = emailSettings.isSmtpAuthEnabled();
				else
					value = emailSettings.isSmtpsAuthEnabled();
				InlineLabel valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Allow sending email to all users
				keyLabel = new InlineLabel(RBUNDLE.allowSendEmailToAllUsers());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				value = emailSettings.isAllowSendToAllUsers();
				valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			row++;
			{
				// Protocol
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.protocolColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(smtpTransport ? RBUNDLE.smtp() : RBUNDLE.smtps());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Host
				keyLabel = new InlineLabel(RBUNDLE.hostColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				String host = null;
				if (smtpTransport)
					host = emailSettings.getSmtpHost();
				else
					host = emailSettings.getSmtpsHost();

				valueLabel = new InlineLabel(host);
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			row++;
			{
				// Port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.portColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				int port = 0;
				if (smtpTransport)
					port = emailSettings.getSmtpPort();
				else
					port = emailSettings.getSmtpsPort();

				InlineLabel valueLabel = new InlineLabel(String.valueOf(port));
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// TimeZone
				keyLabel = new InlineLabel(RBUNDLE.timeZoneColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(emailSettings.getDefaultTZ());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			row++;
			{
				// User name
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.userNameColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				String user;
				if (smtpTransport)
					user = emailSettings.getSmtpUser();
				else
					user = emailSettings.getSmtpsUser();

				InlineLabel valueLabel = new InlineLabel(user);
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}
		}
		return sectionPanel;
	}

	private FlowPanel buildInboundEmailSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.outboundEmail());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		EmailSettings emailSettings = config.getEmailSettings();

		if (emailSettings != null)
		{
			int row = 0;
			{
				// Enabled
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.enabledColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				boolean value = emailSettings.isInternalInboundSMTPEnabled();
				InlineLabel valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// TLS Enabled
				keyLabel = new InlineLabel(RBUNDLE.tlsEnabledColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				value = emailSettings.isInternalInboundSMTPTLSEnabld();
				valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			{
				row++;
				{
					// Address
					InlineLabel keyLabel = new InlineLabel(RBUNDLE.smtpBindAddressColon());
					table.setWidget(row, 0, keyLabel);
					table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

					InlineLabel valueLabel = new InlineLabel(emailSettings.getInternalInboundSMTPBindAddress());
					table.setWidget(row, 1, valueLabel);
					table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

					// Port
					keyLabel = new InlineLabel(RBUNDLE.smtpPortColon());
					table.setWidget(row, 2, keyLabel);
					table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

					valueLabel = new InlineLabel(String.valueOf(emailSettings.getInternalInboundSMTPPort()));
					table.setWidget(row, 3, valueLabel);
					table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
				}
			}

		}
		return sectionPanel;
	}

	private FlowPanel buildClusteringSection()
	{
		FlowPanel sectionPanel = createSection(RBUNDLE.clustering());

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		Clustered clustered = config.getClustered();

		if (clustered != null)
		{
			int row = 0;
			{
				// Enabled
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.enabledColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				boolean value = clustered.isEnabled();
				InlineLabel valueLabel = new InlineLabel(value ? RBUNDLE.trueStr() : RBUNDLE.falseStr());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// JVM Route
				keyLabel = new InlineLabel(RBUNDLE.jvmRouteColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(clustered.getJvmRoute());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			{
				row++;
				// Caching Provider
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.hibernateCachingProviderColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(clustered.getCachingProvider());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// host name
				keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(clustered.getCacheService());
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

			{
				row++;
				// Group Address
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.multicastGroupAddrColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(clustered.getCacheGroupAddress());
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");

				// Group Port
				keyLabel = new InlineLabel(RBUNDLE.multicastGroupPortColon());
				table.setWidget(row, 2, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 2, "table-key");

				valueLabel = new InlineLabel(String.valueOf(clustered.getCacheGroupPort()));
				table.setWidget(row, 3, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 3, "table-value");
			}

		}
		return sectionPanel;
	}

	class GetConfigInformationCallback implements AsyncCallback<InstallerConfig>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// TODO:
		}

		@Override
		public void onSuccess(InstallerConfig result)
		{
			if (result != null)
			{
				config = result;
				content.clear();
				buildUI();
			}
			else
			{
				// TODO
			}
		}
	}

	private FlowPanel createSection(String header)
	{
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.addStyleName("section");

		if (header != null)
		{
			Label label = new Label(header);
			label.addStyleName("sectionHeader");
			flowPanel.add(label);
		}
		return flowPanel;
	}

	@Override
	public void onEvent(ConfigModifiedEvent event)
	{
		if (event.isModified())
		{
			AppUtil.getInstallService().getConfiguration(getConfigCallback);
		}
	}
}
