package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.ValueRequiredBasedOnBoolValidator;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.InstallerConfig;
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
import com.google.gwt.user.client.ui.Label;
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
	private VibeTextBox hostTextBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		Label label = new Label(RBUNDLE.reverseProxyHostNameDesc());
		label.addStyleName("configPageTitleDescLabel");
		fPanel.add(label);

		FlexTable portTable = new FlexTable();
		portTable.addStyleName("reverProxyPortTable");
		fPanel.add(portTable);

		int row = 0;
		{

			// Host Name
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostColon());
			portTable.setWidget(row, 0, keyLabel);
			portTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostTextBox = new VibeTextBox();
			hostTextBox.setValidator(new ValueRequiredValidator(hostTextBox));
			portTable.setWidget(row, 1, hostTextBox);
			portTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Http Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.reverseProxyHttpPortColon());
			portTable.setWidget(row, 0, keyLabel);
			portTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			FlowPanel httpPortPanel = new FlowPanel();
			httpSpinner = new GwValueSpinner(8080, 80, 9999, null);
			httpPortPanel.add(httpSpinner);

			portTable.setWidget(row, 1, httpPortPanel);
			portTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Secure HTTP Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.reverseProxySecureHttpPortColon());
			portTable.setWidget(row, 0, keyLabel);
			portTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			httpSecureSpinner = new GwValueSpinner(8443, 443, 9999, null);
			portTable.setWidget(row, 1, httpSecureSpinner);
			portTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		Label nameTitleLabel = new Label(RBUNDLE.reverseProxyIntegration());
		nameTitleLabel.addStyleName("namContentPanelHeader");
		fPanel.add(nameTitleLabel);

		FlowPanel namContentPanel = new FlowPanel();
		namContentPanel.addStyleName("namContentPanel");
		fPanel.add(namContentPanel);

		HTML titleDescLabel = new HTML(RBUNDLE.reverseProxyPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		namContentPanel.add(titleDescLabel);

		HTML descLabel = new HTML(RBUNDLE.reverseProxyPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		namContentPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		namContentPanel.add(contentPanel);

		enableAccessGatewayCheckBox = new CheckBox(RBUNDLE.enableAccessGateway());
		enableAccessGatewayCheckBox.addStyleName("enableNamCheckBox");
		enableAccessGatewayCheckBox.addClickHandler(this);
		namContentPanel.add(enableAccessGatewayCheckBox);

		FlexTable table = new FlexTable();
		table.addStyleName("inboundEmailTable");
		namContentPanel.add(table);

		row = 0;
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

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{

		// Host Name is required
		if (!(hostTextBox.isValid()))
		{
			setErrorMessage(RBUNDLE.requiredField());
			return null;
		}

		// If access gateway is enabled, we need to have the address and log off url
		if (enableAccessGatewayCheckBox.getValue())
		{
			if (!(accessGatewayAddrTextBox.isValid() & logoutUrlTextBox.isValid()))
			{
				setErrorMessage(RBUNDLE.accessGatewayEnabledWithInValidData());
				return null;
			}
		}

		// Save the changes
		SSO sso = config.getSso();

		if (sso != null)
		{
			sso.setiChainEnabled(enableAccessGatewayCheckBox.getValue());
			sso.setiChainProxyAddr(accessGatewayAddrTextBox.getText());
			sso.setiChainLogoffUrl(logoutUrlTextBox.getText());

		}

		// Save HTTP Port info
		Network network = config.getNetwork();
		network.setSecurePort(httpSecureSpinner.getValueAsInt());

		if (network.isListenPortEnabled())
		{
			network.setPort(httpSpinner.getValueAsInt());
		}
		else
		{
			network.setPortDisabled(httpSpinner.getValueAsInt());
			network.setPort(0);
		}

		network.setHost(hostTextBox.getText());
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

		// Initialize the UI with the data
		if (network != null)
		{
            if (network.getPort() != 0)
			    httpSpinner.setValue(network.getPort());
            else if (network.getPortDisabled() != 0)
                httpSpinner.setValue(network.getPortDisabled());

			httpSpinner.setEnabled(network.isListenPortEnabled());
			httpSecureSpinner.setValue(network.getSecurePort());

			hostTextBox.setText(network.getHost());
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
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData = new HelpData();
		helpData.setPageId("access_manager");

		return helpData;
	}

	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.NOVELL_ACCESS_MANAGER);
		sectionsToUpdate.add(LeftNavItemType.NETWORK);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate, saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}

}
