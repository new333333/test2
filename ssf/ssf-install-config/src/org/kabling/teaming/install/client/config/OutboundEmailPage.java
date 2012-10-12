package org.kabling.teaming.install.client.config;

import java.util.Map;
import java.util.Map.Entry;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class OutboundEmailPage extends ConfigPageDlgBox
{
	private ListBox protocolListBox;
	private GwTextBox hostTextBox;
	private GwValueSpinner portSpinner;
	private ListBox timezoneListBox;
	private GwTextBox usernameTextBox;
	private PasswordTextBox passwordTextBox;
	private CheckBox authRequiredCheckBox;
	private CheckBox allowSendEmailUsersCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.outboundPageTitleDesc());
		titleDescLabel.addStyleName("inboundEmailTitleDescLabel");
		fPanel.add(titleDescLabel);

		HTML descLabel = new HTML(RBUNDLE.outboundPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);
		
		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("outboundPageContent");

		FlexTable table = new FlexTable();
		table.addStyleName("outboundEmailTable");
		contentPanel.add(table);

		int row = 0;
		{

			// Protocol
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.protocolColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			protocolListBox = new ListBox(false);
			table.setWidget(row, 1, protocolListBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Host
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostTextBox = new GwTextBox();
			table.setWidget(row, 1, hostTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.portColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			portSpinner = new GwValueSpinner(25, 1, 9999, null);
			table.setWidget(row, 1, portSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Time Zone
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.timeZoneColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			timezoneListBox = new ListBox(false);
			table.setWidget(row, 1, timezoneListBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// User Name
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.userNameColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			usernameTextBox = new GwTextBox();
			table.setWidget(row, 1, usernameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Password
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.passwordColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			passwordTextBox = new PasswordTextBox();
			table.setWidget(row, 1, passwordTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Auth Required
			authRequiredCheckBox = new CheckBox(RBUNDLE.authRequired());
			table.setWidget(row, 1, authRequiredCheckBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Allow sending e-mail to all users
			allowSendEmailUsersCheckBox = new CheckBox(RBUNDLE.allowSendEmailToAllUsers());
			table.setWidget(row, 1, allowSendEmailUsersCheckBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		AppUtil.getInstallService().getTimeZones(new AsyncCallback<Map<String,String>>()
		{
			
			@Override
			public void onSuccess(Map<String, String> result)
			{
				for (Entry<String,String> entry : result.entrySet())
				{
					timezoneListBox.addItem(entry.getValue(),entry.getKey());
				}
			}
			
			@Override
			public void onFailure(Throwable caught)
			{
				//TODO
			}
		});

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public void initUIWithData()
	{

	}

}
