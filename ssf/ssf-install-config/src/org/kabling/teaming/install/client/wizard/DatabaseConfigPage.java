package org.kabling.teaming.install.client.wizard;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseConfigPage implements IWizardPage<InstallerConfig>
{

	private TextBox hostTextBox;
	private TextBox portTextBox;
	private TextBox userTextBox;
	private PasswordTextBox userPwdTextBox;
	private TextBox dbNameTextBox;
	private ListBox dbTypeListBox;
	private InstallerConfig config;

	public DatabaseConfigPage(InstallerConfig config)
	{
		this.config = config;
	}
	
	@Override
	public String isValid()
	{
		if (userTextBox != null && userTextBox.getText().isEmpty() || userPwdTextBox != null
				&& userPwdTextBox.getText().isEmpty() || portTextBox != null && portTextBox.getText().isEmpty())
			return "Required Fields cannot be empty";

		return null;
	}

	@Override
	public String getPageTitle()
	{
		return "Database";
	}

	@Override
	public Widget getWizardUI()
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("wizardPage");

		Label descLabel = new Label("Set the database options. ");
		descLabel.addStyleName("wizardPageDesc");
		fPanel.add(descLabel);

		FlexTable table = new FlexTable();
		fPanel.add(table);

		int row = 0;

		{

			// Database Type
			InlineLabel keyLabel = new InlineLabel("Database Type:");
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
			//For Filr, only mysql is supported
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
			InlineLabel keyLabel = new InlineLabel("Host Name:");
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
			InlineLabel keyLabel = new InlineLabel("Port:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			portTextBox = new TextBox();
			portTextBox.setText("3306");
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			table.setWidget(row, 1, portTextBox);
		}

		//We will use the default database name for filr
		if (!AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			row++;
			// Database Name
			InlineLabel keyLabel = new InlineLabel("Database Name:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			dbNameTextBox = new TextBox();
			dbNameTextBox.setText("sitescape");
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			table.setWidget(row, 1, dbNameTextBox);
		}

		{
			row++;
			// Database User
			InlineLabel keyLabel = new InlineLabel("User Name:");
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
			InlineLabel keyLabel = new InlineLabel("User Password:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			userPwdTextBox = new PasswordTextBox();
			userPwdTextBox.setText("test");
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			table.setWidget(row, 1, userPwdTextBox);
		}

		initUIWithData();
		return fPanel;
	}

	@Override
	public boolean canFinish()
	{
		return false;
	}

	@Override
	public void save()
	{
		// TODO Auto-generated method stub

	}

	public void initUIWithData()
	{
		Database db = config.getDatabase();
		if (db != null)
		{
			List<DatabaseConfig> configList = db.getConfig();
			
			if (configList != null)
			{
				for (DatabaseConfig config : configList)
				{
					if (config.getId().equals("Installed"))
					{
						userTextBox.setText(config.getResourceUserName());
						userPwdTextBox.setText(config.getResourcePassword());
						//TODO
						hostTextBox.setText(config.getResourceUrl());
					}
				}
			}
		}
	}

}
