package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredBasedOnBoolValidator;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.SSO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class ReverseProxyPage extends ConfigPageDlgBox implements ClickHandler
{

	private VibeTextBox accessGatewayAddrTextBox;
	private VibeTextBox logoutUrlTextBox;
	private CheckBox enableAccessGatewayCheckBox;
	private ValueRequiredBasedOnBoolValidator accessGatewayAddrValidator;
	private ValueRequiredBasedOnBoolValidator accessGatewayLogOffValidator;
	private GwValueSpinner httpSpinner;
	private GwValueSpinner httpSecureSpinner;
	private CheckBox httpEnabledCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.reverseProxyPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		HTML descLabel = new HTML(RBUNDLE.reverseProxyPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);

		contentPanel.addStyleName("reverseProxyPageContent");

		enableAccessGatewayCheckBox = new CheckBox(RBUNDLE.enableAccessGateway());
		enableAccessGatewayCheckBox.addClickHandler(this);
		contentPanel.add(enableAccessGatewayCheckBox);

		FlexTable table = new FlexTable();
		table.addStyleName("inboundEmailTable");
		contentPanel.add(table);

		int row = 0;
		{

			// Access Gateway Addresseses
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.accessGatewayAddressesColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			accessGatewayAddrTextBox = new VibeTextBox();
			accessGatewayAddrValidator = new ValueRequiredBasedOnBoolValidator(true, accessGatewayAddrTextBox);
			accessGatewayAddrTextBox.setValidator(accessGatewayAddrValidator);
			table.setWidget(row, 1, accessGatewayAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Logout URL
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.logoutUrlColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			logoutUrlTextBox = new VibeTextBox();
			accessGatewayLogOffValidator = new ValueRequiredBasedOnBoolValidator(true, logoutUrlTextBox);
			logoutUrlTextBox.setValidator(accessGatewayLogOffValidator);
			table.setWidget(row, 1, logoutUrlTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		FlexTable portTable = new FlexTable();
		portTable.addStyleName("reverProxyPortTable");
		contentPanel.add(portTable);
		
		row = 0;
		{
			// Http Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.reverseProxyHttpPortColon());
			portTable.setWidget(row, 0, keyLabel);
			portTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			httpSpinner = new GwValueSpinner(8080, 1024, 9999, null);
			portTable.setWidget(row, 1, httpSpinner);
			portTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			
			httpEnabledCheckBox = new CheckBox(RBUNDLE.enabled());
			httpEnabledCheckBox.addClickHandler(this);
			portTable.setWidget(row, 2, httpEnabledCheckBox);
			portTable.getFlexCellFormatter().addStyleName(row, 2, "table-value");
		}

		{
			row++;
			// Secure HTTP Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.reverseProxySecureHttpPortColon());
			portTable.setWidget(row, 0, keyLabel);
			portTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			httpSecureSpinner = new GwValueSpinner(8443, 1024, 9999, null);
			portTable.setWidget(row, 1, httpSecureSpinner);
			portTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{

		//If access gateway is enabled, we need to have the address and log off url
		if (enableAccessGatewayCheckBox.getValue())
		{
			if (!(accessGatewayAddrTextBox.isValid() & logoutUrlTextBox.isValid()))
			{
				setErrorMessage(RBUNDLE.accessGatewayEnabledWithInValidData());
				return null;
			}
		}
		
		//Save the changes
		SSO sso = config.getSso();

		if (sso != null)
		{
			sso.setiChainEnabled(enableAccessGatewayCheckBox.getValue());
			sso.setiChainProxyAddr(accessGatewayAddrTextBox.getText());
			sso.setiChainLogoffUrl(logoutUrlTextBox.getText());

		}
		
		//Save HTTP Port info
		Network network = config.getNetwork();
		network.setPort(httpSpinner.getValueAsInt());
		network.setSecurePort(httpSecureSpinner.getValueAsInt());
		if (httpEnabledCheckBox.getValue())
			network.setPort(httpSpinner.getValueAsInt());
		else
			network.setPort(0);
		
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		// NONE
		return null;
	}

	@Override
	public void initUIWithData()
	{
		SSO sso = config.getSso();

		if (sso != null)
		{
			enableAccessGatewayCheckBox.setValue(sso.isiChainEnabled());
			
			accessGatewayAddrValidator.setRequired(enableAccessGatewayCheckBox.getValue());
			accessGatewayLogOffValidator.setRequired(enableAccessGatewayCheckBox.getValue());
			
			accessGatewayAddrTextBox.setText(sso.getiChainProxyAddr());
			
			logoutUrlTextBox.setText(sso.getiChainLogoffUrl());
		}
		
		Network network = config.getNetwork();

		//Initialize the UI with the data
		if (network != null)
		{
			if (network.getPort() != 0)
			{
				httpSpinner.setValue(network.getPort());
			}
			else
			{
				httpSpinner.setEnabled(false);
			}
			httpEnabledCheckBox.setValue(network.getPort() != 0);
			httpSecureSpinner.setValue(network.getSecurePort());
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		super.onClick(event);

		if (event.getSource() == enableAccessGatewayCheckBox)
		{
			accessGatewayAddrValidator.setRequired(enableAccessGatewayCheckBox.getValue());
			accessGatewayLogOffValidator.setRequired(enableAccessGatewayCheckBox.getValue());
			
			if (!enableAccessGatewayCheckBox.getValue())
			{
				accessGatewayAddrTextBox.clearError();
				logoutUrlTextBox.clearError();
			}
		}
		else if (event.getSource() == httpEnabledCheckBox)
		{
			httpSpinner.setEnabled(httpEnabledCheckBox.getValue());
		}

	}

}
