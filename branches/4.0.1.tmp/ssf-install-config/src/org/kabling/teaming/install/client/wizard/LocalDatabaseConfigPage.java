package org.kabling.teaming.install.client.wizard;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.DatabaseConfig.DatabaseType;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

public class LocalDatabaseConfigPage implements IWizardPage<InstallerConfig>
{
	private PasswordTextBox userPwdTextBox;
	private InstallerConfig config;
	private FlowPanel content;

	private ConfigWizard wizard;

	private AppResource RBUNDLE = AppUtil.getAppResource();
	private PasswordTextBox userPwdConfirmTextBox;

	private static final String DB_USER = "root";
	private static final String DB_NAME = "filr";

	public LocalDatabaseConfigPage(ConfigWizard wizard, InstallerConfig config)
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
		if (content == null)
		{
			content = new FlowPanel();
			content.addStyleName("wizardPage");

			Label descLabel = new Label(RBUNDLE.wizLocalDbPageTitleDesc());
			descLabel.addStyleName("wizardPageDesc");
			content.add(descLabel);

			FlexTable table = new FlexTable();
			content.add(table);

			int row = 0;

			{
				row++;
				// Database User
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.userNameColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				InlineLabel valueLabel = new InlineLabel(DB_USER);
				table.setWidget(row, 1, valueLabel);
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
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
			
			{
				row++;
				// Confirm Database Password
				InlineLabel keyLabel = new InlineLabel(RBUNDLE.confirmPasswordColon());
				table.setWidget(row, 0, keyLabel);
				table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

				userPwdConfirmTextBox = new PasswordTextBox();
				table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
				table.setWidget(row, 1, userPwdConfirmTextBox);
				
				userPwdConfirmTextBox.addKeyUpHandler(new KeyUpHandler()
				{
					
					@Override
					public void onKeyUp(KeyUpEvent event)
					{
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
						{
							if (!userPwdConfirmTextBox.getText().equals("") && !userPwdTextBox.getText().equals(""))
								wizard.nextPage();
						}
					}
				});
			}

			initUIWithData();
		}
		return content;
	}

	@Override
	public boolean isValid()
	{
		String password = userPwdTextBox.getText();
		String confirmPassword = userPwdConfirmTextBox.getText();
		
		// All fields are required, return if any field is empty
		if (password.isEmpty() || confirmPassword.isEmpty())
		{
			wizard.setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}

		if (!password.equals(confirmPassword))
		{
			wizard.setErrorMessage(RBUNDLE.passwordNotMatching());
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

	}

	@Override
	public void save()
	{
		Database db = config.getDatabase();

		// We should never get there
		if (db == null)
		{
			db = new Database();

			List<DatabaseConfig> configList = db.getConfig();
			DatabaseConfig config = new DatabaseConfig();
			configList.add(config);

			config.setId("Installed");
			config.setResourcePassword(userPwdTextBox.getText());
			config.setResourceUserName(DB_USER);
			config.setResourceDatabase(DB_NAME);
			config.setType(DatabaseType.MYSQL);
			config.setResourceFor("icecore");
			config.setResourceDriverClassName("com.mysql.jdbc.Driver");
			config.setResourceHost("localhost");
			config.setResourceUrl("jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&amp;characterEncoding=UTF-8");
		}
		else
		{
			// Save it back to the object
			List<DatabaseConfig> configList = db.getConfig();

			if (configList != null)
			{
				boolean found = false;
				for (DatabaseConfig config : configList)
				{
					
					if (config.getId().equals("Installed"))
					{
						found = true;
						config.setResourcePassword(userPwdTextBox.getText());
						config.setType(DatabaseType.MYSQL);
						config.setResourceUserName(DB_USER);
						config.setResourceHost("localhost");
						config.setResourceDatabase(DB_NAME);
						config.setResourceUrl("jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&amp;characterEncoding=UTF-8");
						break;
					}
				}
				
				if (!found)
				{
					DatabaseConfig newDbConfig = new DatabaseConfig();
					newDbConfig.setId("Installed");
					newDbConfig.setResourcePassword(userPwdTextBox.getText());
					newDbConfig.setResourceUserName(DB_USER);
					newDbConfig.setResourceHost("localhost");
					newDbConfig.setResourceDatabase(DB_NAME);
					newDbConfig.setResourceUrl("jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&amp;characterEncoding=UTF-8");
				
					
					configList.add(newDbConfig);
				}
				
			}
		}

	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage()
	{
		return wizard.configPage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage()
	{
		return wizard.localePage;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		return userPwdTextBox;
	}
}
