package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class NetworkInformationPage extends ConfigPageDlgBox implements ClickHandler
{
	private GwValueSpinner listenSpinner;
	private GwValueSpinner secureListenSpinner;
	private GwValueSpinner shutDownPortSpinner;
	private GwValueSpinner ajpPortSpinner;
	private GwValueSpinner sessionTimeOutSpinner;
	private VibeTextBox keyStoreFileTextBox;
	private CheckBox httpEnabledCheckBox;
	private CheckBox forceSecureCheckBox;
	private CheckBox portRedirectCheckBox;
	private InlineLabel port443RedirectLabel;
	private Widget portRedirectReverseProxyLabel;
	private InlineLabel httpPortLabel;

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
			// Port Redirection
			portRedirectCheckBox = new CheckBox("Port Redirection");
			portRedirectCheckBox.addClickHandler(this);
			table.setWidget(row, 0, portRedirectCheckBox);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-value");
		}

		{
			row++;
			// Listen Port
			FlowPanel labelWrapper = new FlowPanel();

			httpPortLabel = new InlineLabel(RBUNDLE.httpPortColon());
			labelWrapper.add(httpPortLabel);

			table.setWidget(row, 0, labelWrapper);
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
			
			forceSecureCheckBox = new CheckBox(RBUNDLE.forceSecureConnection());
			forceSecureCheckBox.setEnabled(false);
			forceSecureCheckBox.addStyleName("networkPageHttpEnabledCheckBox");
			forceSecureCheckBox.addClickHandler(this);
			portEnablePanel.add(forceSecureCheckBox);
		}

		{
			row++;
			// Secure Listen Port

			FlowPanel labelWrapper = new FlowPanel();

			port443RedirectLabel = new InlineLabel("Port 443 -> ");
			labelWrapper.add(port443RedirectLabel);

			InlineLabel keyLabel1 = new InlineLabel(RBUNDLE.secureHttpPortColon());
			labelWrapper.add(keyLabel1);

			table.setWidget(row, 0, labelWrapper);
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
		
		portRedirectReverseProxyLabel = new Label(RBUNDLE.portRedirectionReverseProxyPortDesc());
		portRedirectReverseProxyLabel.addStyleName("networkPagePortRedirectProxyDescLabel");
		fPanel.add(portRedirectReverseProxyLabel);
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{

        int listenPort = listenSpinner.getValueAsInt();
        int secureListenPort = secureListenSpinner.getValueAsInt();

        if (listenPort == secureListenPort)
        {
            setErrorMessage(RBUNDLE.listenPortSecureNotEqual());
            return null;
        }
		// Save the configuration
		Network network = config.getNetwork();
		network.setSecureListenPort(secureListenPort);
		network.setPortRedirect(portRedirectCheckBox.getValue());

		if (shutDownPortSpinner != null)
			network.setShutdownPort(shutDownPortSpinner.getValueAsInt());

		if (ajpPortSpinner != null)
			network.setAjpPort(ajpPortSpinner.getValueAsInt());
		network.setSessionTimeoutMinutes(sessionTimeOutSpinner.getValueAsInt());
		network.setKeystoreFile(keyStoreFileTextBox.getText());

		network.setListenPort(httpEnabledCheckBox.getValue() ? listenPort : 0);
		network.setListenPortEnabled(httpEnabledCheckBox.getValue());
        network.setListenPortDisabled(listenPort);

        //Override this to be 0 if http is not enabled
        if (!httpEnabledCheckBox.getValue())
        {
            if (network.getPort() != 0)
                network.setPortDisabled(network.getPort());

		    network.setPort(0);
        }
		network.setForceSecure(forceSecureCheckBox.getValue());

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

		// Initialize the UI with the data
		if (network != null)
		{
			boolean portRedirect = network.isPortRedirect();
			portRedirectCheckBox.setValue(portRedirect);

			port443RedirectLabel.setVisible(portRedirect);
			portRedirectReverseProxyLabel.setVisible(portRedirect);

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
            else if (network.getListenPortDisabled() != 0)
            {
                listenSpinner.setValue(network.getListenPortDisabled());
            }
			
			if (network.getListenPort() == 0 || !network.isListenPortEnabled())
			{
				listenSpinner.setEnabled(false);
			}
			
			
			
			boolean httpEnabled = network.isListenPortEnabled();
			httpEnabledCheckBox.setValue(httpEnabled);
			httpEnabledCheckBox.setEnabled(!network.isForceSecure());
			
			forceSecureCheckBox.setValue(network.isForceSecure());
			forceSecureCheckBox.setEnabled(httpEnabled);
			updateHttpPortLabel();
		}
	}

	private void updateHttpPortLabel()
	{
		if (portRedirectCheckBox.getValue())
		{
			if (forceSecureCheckBox.getValue())
			{
				httpPortLabel.setText(RBUNDLE.port80ToSecureHttpPortLabel());
			}
			else if (httpEnabledCheckBox.getValue())
			{
				httpPortLabel.setText(RBUNDLE.port80ToHttpPortLabel());
			}
			else
			{
				httpPortLabel.setText(RBUNDLE.httpPortColon());
			}
		}
		else if (forceSecureCheckBox.getValue())
		{
			httpPortLabel.setText(RBUNDLE.httpPortToSecurePortLabel());
		}
		else
		{
			httpPortLabel.setText(RBUNDLE.httpPortColon());
		}
	}
	@Override
	public void onClick(ClickEvent event)
	{
		super.onClick(event);
		
		if (event.getSource() == httpEnabledCheckBox)
		{
			listenSpinner.setEnabled(httpEnabledCheckBox.getValue());
			forceSecureCheckBox.setEnabled(httpEnabledCheckBox.getValue());
			
			updateHttpPortLabel();
		}
		else if (event.getSource() == portRedirectCheckBox)
		{
			port443RedirectLabel.setVisible(portRedirectCheckBox.getValue());
			portRedirectReverseProxyLabel.setVisible(portRedirectCheckBox.getValue());
			
			updateHttpPortLabel();
		}
		else if (event.getSource() == forceSecureCheckBox)
		{
			httpEnabledCheckBox.setEnabled(!forceSecureCheckBox.getValue());
			listenSpinner.setEnabled(!forceSecureCheckBox.getValue());
			
			updateHttpPortLabel();
		}
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("network");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.NETWORK);
		//For session time out settings
		sectionsToUpdate.add(LeftNavItemType.WEB_SERVICES);
		
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}
}
