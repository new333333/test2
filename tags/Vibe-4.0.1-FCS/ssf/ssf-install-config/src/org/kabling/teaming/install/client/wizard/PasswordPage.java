package org.kabling.teaming.install.client.wizard;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PasswordPage implements IWizardPage<InstallerConfig>
{
	private static final String DEFAULT_DB = "filr";
	private TextBox userTextBox;
	private PasswordTextBox userPwdTextBox;
	private InstallerConfig config;
	private FlowPanel content;

	private ConfigWizard wizard;

	private AppResource RBUNDLE = AppUtil.getAppResource();

	public PasswordPage(ConfigWizard wizard, InstallerConfig config)
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

			Label descLabel = new Label(RBUNDLE.wizDbPageTitleDesc());
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

			initUIWithData();
		}
		return content;
	}

	@Override
	public boolean isValid()
	{
		String userName = userTextBox.getText();
		String password = userPwdTextBox.getText();

		// All fields are required, return if any field is empty
		if (userName.isEmpty() || password.isEmpty())
		{
			wizard.setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}

		return true;
	}

	@Override
	public boolean canFinish()
	{
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
					}
				}
			}
		}
	}

	@Override
	public void save()
	{
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
					config.setResourceHost("localhost");
					config.setResourceUrl("jdbc:mysql://" + getHostName(config.getResourceUrl()) + ":" + getHostPort(config.getResourceUrl()) + "/"
							+ DEFAULT_DB + "?useUnicode=true&amp;characterEncoding=UTF-8");
					break;
				}
			}
		}

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
	public IWizardPage<InstallerConfig> getPreviousPage()
	{
		return wizard.configPage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage()
	{
		return null;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		return userTextBox;
	}
}
