package org.kabling.teaming.install.client.wizard;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.DatabaseConfig.DatabaseType;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseConfigPage implements IWizardPage<InstallerConfig>
{
	private TextBox hostTextBox;
	private GwValueSpinner portSpinner;
	private TextBox userTextBox;
	private PasswordTextBox userPwdTextBox;
	private TextBox dbNameTextBox;
	private ListBox dbTypeListBox;
	private InstallerConfig config;
	private FlowPanel content;

	private ConfigWizard wizard;
	private boolean validatedCredentials;

	private AppResource RBUNDLE = AppUtil.getAppResource();

	public DatabaseConfigPage(ConfigWizard wizard, InstallerConfig config)
	{
		this.wizard = wizard;
		this.config = config;
	}

	@Override
	public String getPageTitle()
	{
		return RBUNDLE.database();
	}

	@Override
	public Widget getWizardUI()
	{
		validatedCredentials = false;
		if (content == null)
		{
			content = new FlowPanel();
			content.addStyleName("wizardPage");

			Label descLabel = new Label(RBUNDLE.wizDbPageTitleDesc());
			descLabel.addStyleName("wizardPageDesc");
			content.add(descLabel);

			FlexTable table = new FlexTable();
			content.add(table);

			int row = 0;

			{

				// Database Type
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.dbTypeColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				if (!AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
				{
					dbTypeListBox = new ListBox(false);
					table.setWidget(row, 1, dbTypeListBox);

					dbTypeListBox.addItem("MySql", "MySql");
					dbTypeListBox.addItem("Oracle", "Oracle");
					dbTypeListBox.setSelectedIndex(0);
				}
				// For Filr, only mysql is supported
				else
				{
					InlineLabel valueLabel = new InlineLabel("MySQL");
					table.setWidget(row, 1, valueLabel);
				}
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			}

			{

				row++;
				// Host
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				hostTextBox = new TextBox();
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, hostTextBox);
			}

			{
				row++;
				// Port
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.portColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				portSpinner = new GwValueSpinner(3306, 1024, 10000, null);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, portSpinner);
			}

			{
				row++;
				// Database Name
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.dbNameColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				dbNameTextBox = new TextBox();
				dbNameTextBox.setText("filr");
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, dbNameTextBox);
			}

			{
				row++;
				// Database User
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.userNameColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				userTextBox = new TextBox();
				userTextBox.setText("filr");
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, userTextBox);
			}

			{
				row++;
				// Database Password
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.dbAdminPasswordColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				userPwdTextBox = new PasswordTextBox();
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, userPwdTextBox);
			}

			HTML footerLabel = new HTML(
					"The MySQL database can be:<br> - The integrated database running in the Filr virtual appliance (localhost)<br>"
							+ "- The MySQL virtual appliance that is included with Filr, running separately.<br>"
							+ "- A MySQL database server running in a virtual or physical machine on your network.");
			footerLabel.addStyleName("configWizFooterLabel");
			content.add(footerLabel);

			initUIWithData();
		}
		return content;
	}

	@Override
	public boolean isValid()
	{
		String userName = userTextBox.getText();
		String password = userPwdTextBox.getText();
		String hostName = hostTextBox.getText();
		String dbName = dbNameTextBox.getText();
		long port = portSpinner.getValueAsInt();

		// All fields are required, return if any field is empty
		if (userName.isEmpty() || password.isEmpty() || hostName.isEmpty() || dbName.isEmpty() || port == 0)
		{
			wizard.setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}
		
//		if (Navigator.getPlatform().startsWith("Win") || Navigator.getPlatform().startsWith("Mac"))
//		{
//			return true;
//		}
		
		if (!validatedCredentials)
		{
			wizard.showStatusIndicator(RBUNDLE.validatingCredentials());
			//Note: We don't have the database name here as part of the url (otherwise, we would fail if the database does not exist)
			String url = "jdbc:mysql://" + hostTextBox.getText() + ":" + portSpinner.getValue()
					+ "?useUnicode=true&amp;characterEncoding=UTF-8";
			AppUtil.getInstallService().authenticateDbCredentials(url, userName, password, new AuthDbCallback());
			return false;
		}

		return true;
	}

	@Override
	public boolean canFinish()
	{
		return false;
	}

	public void initUIWithData()
	{
		Database db = config.getDatabase();
		DatabaseConfig installConfig = db.getDatabaseConfig("Installed");
		
		if (installConfig == null)
			addInstalledConfigFromMySqlDefault();
		
		if (db != null)
		{
			List<DatabaseConfig> configList = db.getConfig();

			if (configList != null)
			{
				// We only need to go through the data in "Installed" configuration
				for (DatabaseConfig config : configList)
				{
					if (config.getId().equals("Installed"))
					{
						//userTextBox.setText(config.getResourceUserName());
						//For filr large deployment default to filr, don't read from installer.xml
						userTextBox.setText("filr");

						String portStr = getHostPort(config.getResourceUrl());
						if (portStr != null)
						{
							try
							{
								int port = Integer.valueOf(portStr);
								portSpinner.setValue(port);
							}
							catch (NumberFormatException e)
							{

							}
						}

					}
				}
			}
		}
	}

	private void addInstalledConfigFromMySqlDefault()
	{
		Database db = config.getDatabase();
		DatabaseConfig installedConfig = db.getDatabaseConfig("Installed");
		
		if (installedConfig == null)
		{
			DatabaseConfig mysqlConfig = db.getDatabaseConfig("MySQL_Default");
			installedConfig = new DatabaseConfig();
			
			installedConfig.setId("Installed");
			installedConfig.setResourceDatabase(mysqlConfig.getResourceDatabase());
			installedConfig.setResourceDriverClassName(mysqlConfig.getResourceDriverClassName());
			installedConfig.setResourceFor(mysqlConfig.getResourceFor());
			installedConfig.setResourceHost(mysqlConfig.getResourceHost());
			installedConfig.setResourceUrl(mysqlConfig.getResourceUrl());
			installedConfig.setResourceUserName(mysqlConfig.getResourceUserName());
			installedConfig.setType(DatabaseType.MYSQL);
			db.getConfig().add(installedConfig);
			
			//Set the default to be Installed in the Database
			db.setConfigName("Installed");
		}
	}
	@Override
	public void save()
	{
		Database db = config.getDatabase();

		// We should never get there
		if (db == null)
		{
			db = new Database();
			db.setConfigName("Installed");
			
			List<DatabaseConfig> configList = db.getConfig();
			DatabaseConfig config = new DatabaseConfig();
			configList.add(config);
			
			config.setId("Installed");
			config.setResourcePassword(userPwdTextBox.getText());
			config.setResourceUserName(userTextBox.getText());
			config.setResourceDatabase(dbNameTextBox.getText());
			config.setType(DatabaseType.MYSQL);
			config.setResourceFor("icecore");
			config.setResourceDriverClassName("com.mysql.jdbc.Driver");
			config.setResourceHost(hostTextBox.getText());
			config.setResourceUrl("jdbc:mysql://" + hostTextBox.getText() + ":" + portSpinner.getValue()
					+ "/"+dbNameTextBox.getText() +"?useUnicode=true&amp;characterEncoding=UTF-8");
		}
		else
		{
			// Save it back to the object
			List<DatabaseConfig> configList = db.getConfig();

			if (configList != null)
			{
				for (DatabaseConfig config : configList)
				{
					if (config.getId().equals("Installed"))
					{
						config.setResourcePassword(userPwdTextBox.getText());
						config.setResourceUserName(userTextBox.getText());
						config.setResourceHost(hostTextBox.getText());
						config.setResourceDatabase(dbNameTextBox.getText());
						config.setResourceUrl("jdbc:mysql://" + hostTextBox.getText() + ":" + portSpinner.getValue()
								+ "/"+dbNameTextBox.getText() + "?useUnicode=true&amp;characterEncoding=UTF-8");
						break;
					}
				}
			}
		}

	}

	private String getHostPort(String url)
	{
		// url example jdbc:jtds:sqlserver://localhost/filr;SelectMethod=cursor
		// Note SQL server does not have a port
		String pattern = "//";
		if (url != null)
		{
			if (url != null)
			{
				int startIndex = url.indexOf(pattern) + pattern.length();
				int endIndex = url.indexOf(":", startIndex);

				// We do have the port
				// MySql and Oracle have the port in them
				if (url.charAt(endIndex) == ':')
				{
					int portIndex = url.indexOf("/", endIndex + 1);
					return url.substring(endIndex + 1, portIndex);
				}
			}
		}
		return url;
	}

	class AuthDbCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// Credentials are not valid
			// Display an error and stay in the same page
			wizard.hideStatusIndicator();
			wizard.setErrorMessage(RBUNDLE.unableToConnectToDbServer());
		}

		@Override
		public void onSuccess(Void coid)
		{
			// The credentials are valid
			validatedCredentials = true;

			// We can go to the next page
			wizard.hideStatusIndicator();
			wizard.nextPage();
		}
	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage() {
		return wizard.configPage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage() {
		return wizard.lucenePage;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		return hostTextBox;
	}
}
