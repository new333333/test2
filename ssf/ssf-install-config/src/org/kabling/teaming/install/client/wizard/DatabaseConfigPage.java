package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.shared.InstallerConfig;

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

	@Override
	public String isValid()
	{
		if (!userTextBox.getText().isEmpty() & !userPwdTextBox.getText().isEmpty() & portTextBox.getText().isEmpty() & !hostTextBox.getText().isEmpty())
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

		Label descLabel = new Label("Explain what options to select here, Describe what they are configuring here. ");
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

			dbTypeListBox = new ListBox(false);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			table.setWidget(row, 1, dbTypeListBox);
			
			dbTypeListBox.addItem("MySql", "MySql");
			dbTypeListBox.addItem("Oracle", "Oracle");
			
			dbTypeListBox.setSelectedIndex(0);
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
			userTextBox.setText("admin");
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

	@Override
	public void initUIWithData(InstallerConfig object)
	{
		// TODO Auto-generated method stub
		
	}

}
