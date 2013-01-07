package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class NetworkInformationPage extends ConfigPageDlgBox implements ClickHandler
{
	private GwValueSpinner listenSpinner;
	private GwValueSpinner secureListenSpinner;
	private GwValueSpinner shutDownPortSpinner;
	private GwValueSpinner ajpPortSpinner;
	private GwValueSpinner sessionTimeOutSpinner;
	private VibeTextBox keyStoreFileTextBox;
	private CheckBox httpEnabledCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		// Title
		HTML titleDescLabel = new HTML(RBUNDLE.networkInfoPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		// Page Description
		HTML descLabel = new HTML(RBUNDLE.networkInfoPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("networkInfoPageContent");

		FlexTable table = new FlexTable();
		contentPanel.add(table);

		int row = 0;

		{
			// Listen Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.httpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			FlowPanel portEnablePanel = new FlowPanel();
			
			listenSpinner = new GwValueSpinner(8080, 1024, 9999, null);
			table.setWidget(row, 1, portEnablePanel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
			
			httpEnabledCheckBox = new CheckBox(RBUNDLE.enabled());
			httpEnabledCheckBox.addStyleName("networkPageHttpEnabledCheckBox");
			httpEnabledCheckBox.addClickHandler(this);
			
			portEnablePanel.add(listenSpinner);
			portEnablePanel.add(httpEnabledCheckBox);
		}

		{
			row++;
			// Secure Listen Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.secureHttpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			secureListenSpinner = new GwValueSpinner(8443, 1024, 9999, null);
			table.setWidget(row, 1, secureListenSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		if (!AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			row++;
			// Shutdown port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.shutdownPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			shutDownPortSpinner = new GwValueSpinner(8005, 1024, 9999, null);
			table.setWidget(row, 1, shutDownPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		if (!AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
		{
			row++;
			// AJP port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.ajpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			ajpPortSpinner = new GwValueSpinner(8009, 1024, 9999, null);
			table.setWidget(row, 1, ajpPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Session Time out
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.sessionTimeOutColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			sessionTimeOutSpinner = new GwValueSpinner(240, 20, 500, null);
			table.setWidget(row, 1, sessionTimeOutSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// KeyStore
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.keyStoreFileColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			keyStoreFileTextBox = new VibeTextBox();
			table.setWidget(row, 1, keyStoreFileTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		//Save the configuration
		Network network = config.getNetwork();
		network.setSecureListenPort(secureListenSpinner.getValueAsInt());
		
		if (shutDownPortSpinner != null)
			network.setShutdownPort(shutDownPortSpinner.getValueAsInt());
		
		if (ajpPortSpinner != null)
			network.setAjpPort(ajpPortSpinner.getValueAsInt());
		network.setSessionTimeoutMinutes(sessionTimeOutSpinner.getValueAsInt());
		network.setKeystoreFile(keyStoreFileTextBox.getText());

		if (httpEnabledCheckBox.getValue())
			network.setListenPort(listenSpinner.getValueAsInt());
		else
			network.setListenPort(0);
		
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
		Network network = config.getNetwork();

		//Initialize the UI with the data
		if (network != null)
		{
			secureListenSpinner.setValue(network.getSecureListenPort());
			
			if (shutDownPortSpinner != null)
				shutDownPortSpinner.setValue(network.getShutdownPort());
			
			if (ajpPortSpinner != null)
				ajpPortSpinner.setValue(network.getAjpPort());
			sessionTimeOutSpinner.setValue(network.getSessionTimeoutMinutes());
			keyStoreFileTextBox.setText(network.getKeystoreFile());
			
			if (network.getListenPort() != 0)
			{
				listenSpinner.setValue(network.getListenPort());
			}
			else
			{
				listenSpinner.setEnabled(false);
			}
			httpEnabledCheckBox.setValue(network.getListenPort() != 0);
		}
	}
	
	@Override
	public void onClick(ClickEvent event)
	{
		super.onClick(event);
		
		if (event.getSource() == httpEnabledCheckBox)
		{
			listenSpinner.setEnabled(httpEnabledCheckBox.getValue());
		}
	}
}
