package org.kabling.teaming.install.client.config;

import java.util.Map;
import java.util.Map.Entry;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibePasswordTextBox;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.EmailSettings;
import org.kabling.teaming.install.shared.EmailSettings.EmailProtocol;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

public class OutboundEmailPage extends ConfigPageDlgBox
{
	private ListBox protocolListBox;
	private VibeTextBox hostTextBox;
	private GwValueSpinner portSpinner;
	private ListBox timezoneListBox;
	private VibeTextBox usernameTextBox;
	private VibePasswordTextBox passwordTextBox;
	private CheckBox authRequiredCheckBox;
	private CheckBox allowSendEmailUsersCheckBox;
	private static Map<String, String> timezones;

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
			protocolListBox.addItem("smtp", "smtp");
			protocolListBox.addItem("smtps", "smtps");
			table.setWidget(row, 1, protocolListBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Host
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostTextBox = new VibeTextBox();
			hostTextBox.setValidator(new ValueRequiredValidator(hostTextBox));
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

			usernameTextBox = new VibeTextBox();
			usernameTextBox.setValidator(new ValueRequiredValidator(usernameTextBox));
			table.setWidget(row, 1, usernameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Password
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.passwordColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			passwordTextBox = new VibePasswordTextBox();
			passwordTextBox.setValidator(new ValueRequiredValidator(passwordTextBox));
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

		if (timezones == null)
		{
			AppUtil.getInstallService().getTimeZones(new AsyncCallback<Map<String, String>>()
			{

				@Override
				public void onSuccess(Map<String, String> result)
				{
					timezones = result;

					// Update the time zone list box
					if (result != null)
					{
						for (Entry<String, String> entry : result.entrySet())
						{
							timezoneListBox.addItem(entry.getKey(), entry.getValue());
						}

						// If this call comes back later than initUI, then we may need to set the value here
						// We will also initialize the value in initUIWithData() method
						EmailSettings emailSettings = config.getEmailSettings();

						if (emailSettings != null && emailSettings.getDefaultTZ() != null)
						{
							selectTimeZone(emailSettings.getDefaultTZ());
						}
					}
				}

				@Override
				public void onFailure(Throwable caught)
				{
					// TODO
				}
			});
		}
		else
		{
			for (Entry<String, String> entry : timezones.entrySet())
			{
				timezoneListBox.addItem(entry.getKey(), entry.getValue());
			}
		}

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{

		if (!(hostTextBox.isValid() & usernameTextBox.isValid() & passwordTextBox.isValid()))
		{
			setErrorMessage(RBUNDLE.allFieldsRequired());
			return null;
		}

		// TODO: What is required
		EmailProtocol protocol = protocolListBox.getSelectedIndex() == 0 ? EmailProtocol.SMTP : EmailProtocol.SMTPS;
		EmailSettings emailSettings = config.getEmailSettings();
		emailSettings.setTransportProtocol(protocol);

		// SMTP
		if (protocol.equals(EmailProtocol.SMTP))
		{
			emailSettings.setSmtpHost(hostTextBox.getText());
			emailSettings.setSmtpAuthEnabled(authRequiredCheckBox.getValue());
			emailSettings.setSmtpUser(usernameTextBox.getText());
			emailSettings.setSmtpPassword(passwordTextBox.getText());
			emailSettings.setSmtpPort(portSpinner.getValueAsInt());
			//emailSettings.setSmtpSendPartial(allowSendEmailUsersCheckBox.getValue());
		}
		// SMPTS
		else
		{
			emailSettings.setSmtpsHost(hostTextBox.getText());
			emailSettings.setSmtpsAuthEnabled(authRequiredCheckBox.getValue());
			emailSettings.setSmtpsUser(usernameTextBox.getText());
			emailSettings.setSmtpsPassword(passwordTextBox.getText());
			emailSettings.setSmtpsPort(portSpinner.getValueAsInt());
			//emailSettings.setSmtpsSendPartial(allowSendEmailUsersCheckBox.getValue());
		}

		// Time Zone
		int selectedIndex = timezoneListBox.getSelectedIndex();
		emailSettings.setDefaultTZ(timezoneListBox.getValue(selectedIndex));
		
		// Time Zone
		emailSettings.setAllowSendToAllUsers(allowSendEmailUsersCheckBox.getValue());
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return hostTextBox;
	}

	@Override
	public void initUIWithData()
	{
		EmailSettings emailSettings = config.getEmailSettings();

		if (emailSettings != null)
		{
			EmailProtocol protocol = emailSettings.getTransportProtocol();

			if (protocol.equals(EmailProtocol.SMTP))
				protocolListBox.setSelectedIndex(0);
			else
				protocolListBox.setSelectedIndex(1);
			
			if (protocol.equals(EmailProtocol.SMTP))
			{
				hostTextBox.setText(emailSettings.getSmtpHost());
				usernameTextBox.setText(emailSettings.getSmtpUser());
				authRequiredCheckBox.setValue(emailSettings.isSmtpAuthEnabled());
				portSpinner.setValue(emailSettings.getSmtpPort());
			}
			else
			{
				hostTextBox.setText(emailSettings.getSmtpsHost());
				usernameTextBox.setText(emailSettings.getSmtpsUser());
				authRequiredCheckBox.setValue(emailSettings.isSmtpsAuthEnabled());
				portSpinner.setValue(emailSettings.getSmtpsPort());
			}

			String tzString = emailSettings.getDefaultTZ();
			if (tzString != null && timezoneListBox.getItemCount() > 0)
			{
				selectTimeZone(tzString);
			}
			
			allowSendEmailUsersCheckBox.setValue(emailSettings.isAllowSendToAllUsers());
		}
	}

	private void selectTimeZone(String id)
	{
		for (int i = 0; i < timezoneListBox.getItemCount(); i++)
		{
			String value = timezoneListBox.getValue(i);
			if (value.equals(id))
			{
				timezoneListBox.setSelectedIndex(i);
				return;
			}
		}
	}
}
