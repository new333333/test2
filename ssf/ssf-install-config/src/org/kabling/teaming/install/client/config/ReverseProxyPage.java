package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredBasedOnBoolValidator;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
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
	private VibeTextBox webDavGatewayAddrTextBox;
	private CheckBox useAccessGatewayWebDavCheckBox;
	private CheckBox enableAccessGatewayCheckBox;
	private ValueRequiredBasedOnBoolValidator accessGatewayAddrValidator;
	private ValueRequiredBasedOnBoolValidator accessGatewayLogOffValidator;
	private ValueRequiredBasedOnBoolValidator webDavValidator;

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

		{
			row++;
			// Use Access Gateway for WebDav configuration
			useAccessGatewayWebDavCheckBox = new CheckBox(RBUNDLE.useAccessGatewayForWebDav());
			useAccessGatewayWebDavCheckBox.addStyleName("useAccessGatewayWebDavCheckBox");
			useAccessGatewayWebDavCheckBox.addClickHandler(this);
			table.setWidget(row, 0, useAccessGatewayWebDavCheckBox);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-value");
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
		}

		{
			row++;
			// WebDav access gateway address
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.webDavAccessGatewayAddrColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			webDavGatewayAddrTextBox = new VibeTextBox();
			webDavValidator = new ValueRequiredBasedOnBoolValidator(true, webDavGatewayAddrTextBox);
			webDavGatewayAddrTextBox.setValidator(webDavValidator);
			table.setWidget(row, 1, webDavGatewayAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
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
		
		//If we are using for WebDAV, we need to have the WebDAV Address
		if (useAccessGatewayWebDavCheckBox.getValue() && !webDavGatewayAddrTextBox.isValid())
		{
			setErrorMessage(RBUNDLE.gatewayWebDavEnabledWithInvalidData());
			return null;
		}

		//Save the changes
		SSO sso = config.getSso();

		if (sso != null)
		{
			sso.setiChainEnabled(enableAccessGatewayCheckBox.getValue());
			sso.setiChainProxyAddr(accessGatewayAddrTextBox.getText());
			sso.setiChainLogoffUrl(logoutUrlTextBox.getText());

			sso.setiChainWebDAVProxyEnabled(useAccessGatewayWebDavCheckBox.getValue());
			sso.setiChainWebDAVProxyHost(webDavGatewayAddrTextBox.getText());
		}
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
			webDavValidator.setRequired(useAccessGatewayWebDavCheckBox.getValue());
			
			logoutUrlTextBox.setText(sso.getWinAuthLogoffUrl());

			useAccessGatewayWebDavCheckBox.setValue(sso.isiChainWebDAVProxyEnabled());
			webDavGatewayAddrTextBox.setText(sso.getiChainWebDAVProxyHost());
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
		else if (event.getSource() == useAccessGatewayWebDavCheckBox)
		{
			webDavValidator.setRequired(useAccessGatewayWebDavCheckBox.getValue());
			if (!useAccessGatewayWebDavCheckBox.getValue())
			{
				webDavGatewayAddrTextBox.clearError();
			}
		}

	}

}
