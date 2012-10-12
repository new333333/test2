package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwTextBox;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class ReverseProxyPage extends ConfigPageDlgBox
{

	private GwTextBox accessGatewayAddrTextBox;
	private GwTextBox logoutUrlTextBox;
	private GwTextBox webDavGatewayAddrTextBox;
	private CheckBox useAccessGatewayWebDavCheckBox;
	private CheckBox enableAccessGatewayCheckBox;

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

			accessGatewayAddrTextBox = new GwTextBox();
			table.setWidget(row, 1, accessGatewayAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Logout URL
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.logoutUrlColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			logoutUrlTextBox = new GwTextBox();
			table.setWidget(row, 1, logoutUrlTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Use Access Gateway for WebDav configuration
			useAccessGatewayWebDavCheckBox = new CheckBox(RBUNDLE.useAccessGatewayForWebDav());
			useAccessGatewayWebDavCheckBox.addStyleName("useAccessGatewayWebDavCheckBox");
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

			webDavGatewayAddrTextBox = new GwTextBox();
			table.setWidget(row, 1, webDavGatewayAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
	
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
		//NONE
		return null;
	}

	@Override
	public void initUIWithData()
	{
		// TODO Auto-generated method stub
		
	}

}
