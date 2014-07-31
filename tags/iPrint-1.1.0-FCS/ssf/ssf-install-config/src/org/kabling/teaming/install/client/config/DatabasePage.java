package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class DatabasePage extends ConfigPageDlgBox
{
	private TextBox hostTextBox;
	private GwValueSpinner portSpinner;
	private TextBox userTextBox;
	private PasswordTextBox userPwdTextBox;
	private ListBox dbTypeListBox;
	private FlowPanel content;
	private String dbName;

	private AppResource RBUNDLE = AppUtil.getAppResource();

	@Override
	public Panel createContent(Object propertiesObj)
	{
		content = new FlowPanel();
		content.addStyleName("configPage");

		Label descLabel = new Label(RBUNDLE.dbConfigPageTitleDesc());
		descLabel.addStyleName("configPageTitleDescLabel");
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
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameOrIpAddrColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostTextBox = new TextBox();
			hostTextBox.setText("localhost");
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
			// Database User
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.userNameColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			userTextBox = new TextBox();
			userTextBox.setText("root");
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

		return content;
	}

	public boolean isValid()
	{
		String userName = userTextBox.getText();
		String password = userPwdTextBox.getText();
		String hostName = hostTextBox.getText();
		long port = portSpinner.getValueAsInt();

		// All fields are required, return if any field is empty
		if (userName.isEmpty() || password.isEmpty() || hostName.isEmpty() || port == 0)
		{
			setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}

		return true;
	}

	public void initUIWithData()
	{
		Database db = config.getDatabase();
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
						userTextBox.setText(config.getResourceUserName());

						String hostName = getHostName(config.getResourceUrl());
						hostTextBox.setText(hostName);

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
						dbName = getDbName(config.getResourceUrl());

					}
				}
			}
		}
	}

	@Override
	public Object getDataFromDlg()
	{
		if (!isValid())
			return null;

		Database db = config.getDatabase();

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
					config.setResourceUrl("jdbc:mysql://" + hostTextBox.getText() + ":" + portSpinner.getValue() + "/" + dbName
							+ "?useUnicode=true&amp;characterEncoding=UTF-8");
					break;
				}
			}
		}
		return config;
	}

	private String getHostName(String url)
	{
		// url example jdbc:jtds:sqlserver://localhost/sitescape;SelectMethod=cursor
		String pattern = "//";

		if (url != null)
		{
			int startIndex = url.indexOf(pattern) + pattern.length();
			int endIndex = url.indexOf(":", startIndex);
			url = url.substring(startIndex, endIndex);
			return url;
		}
		return url;
	}

	private String getDbName(String url)
	{
		// url example jdbc:jtds:sqlserver://localhost/sitescape;SelectMethod=cursor
		String pattern = "//";
		int startIndex = url.indexOf(pattern) + pattern.length();
		
		//Should give the url from localhost/
		url = url.substring(startIndex);
		
		//Get the starting from the end of address
		startIndex = url.indexOf("/") +1;
		
		//Ends with ?
		int endIndex = url.indexOf("?", startIndex);
	
		url = url.substring(startIndex, endIndex);
		return url;
	}

	private String getHostPort(String url)
	{
		// url example jdbc:jtds:sqlserver://localhost/sitescape;SelectMethod=cursor
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

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("database");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.DATABASE);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}
}
