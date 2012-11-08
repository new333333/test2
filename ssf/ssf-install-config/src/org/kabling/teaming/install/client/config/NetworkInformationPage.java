package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.Network;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class NetworkInformationPage extends ConfigPageDlgBox
{
	private VibeTextBox hostTextBox;
	private GwValueSpinner httpSpinner;
	private GwValueSpinner listenSpinner;
	private GwValueSpinner secureListenSpinner;
	private GwValueSpinner shutDownPortSpinner;
	private GwValueSpinner ajpPortSpinner;
	private GwValueSpinner sessionTimeOutSpinner;
	private VibeTextBox keyStoreFileTextBox;
	private GwValueSpinner httpSecureSpinner;

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

			// Host Name
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
			// Http Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.httpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			httpSpinner = new GwValueSpinner(80, 80, 9999, null);
			table.setWidget(row, 1, httpSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Secure HTTP Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.secureHttpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			httpSecureSpinner = new GwValueSpinner(443, 80, 9999, null);
			table.setWidget(row, 1, httpSecureSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Listen Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.listenPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			listenSpinner = new GwValueSpinner(443, 80, 9999, null);
			table.setWidget(row, 1, listenSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Secure Listen Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.secureListenPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			secureListenSpinner = new GwValueSpinner(443, 80, 9999, null);
			table.setWidget(row, 1, secureListenSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Shutdown port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.shutdownPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			shutDownPortSpinner = new GwValueSpinner(8005, 80, 9999, null);
			table.setWidget(row, 1, shutDownPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// AJP port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.ajpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			ajpPortSpinner = new GwValueSpinner(8009, 80, 9999, null);
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
		//Host Name is required
		if (!hostTextBox.isValid())
		{
			setErrorMessage(RBUNDLE.requiredField());
			return null;
		}

		//Save the configuration
		Network network = config.getNetwork();
		network.setHost(hostTextBox.getText());
		network.setPort(httpSpinner.getValueAsInt());
		network.setSecurePort(httpSecureSpinner.getValueAsInt());
		network.setListenPort(listenSpinner.getValueAsInt());
		network.setSecureListenPort(secureListenSpinner.getValueAsInt());
		network.setShutdownPort(shutDownPortSpinner.getValueAsInt());
		network.setAjpPort(ajpPortSpinner.getValueAsInt());
		network.setSessionTimeoutMinutes(sessionTimeOutSpinner.getValueAsInt());
		network.setKeystoreFile(keyStoreFileTextBox.getText());

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
		Network network = config.getNetwork();

		//Initialize the UI with the data
		if (network != null)
		{
			hostTextBox.setText(network.getHost());
			httpSpinner.setValue(network.getPort());
			httpSecureSpinner.setValue(network.getSecurePort());
			listenSpinner.setValue(network.getListenPort());
			secureListenSpinner.setValue(network.getSecureListenPort());
			shutDownPortSpinner.setValue(network.getShutdownPort());
			ajpPortSpinner.setValue(network.getAjpPort());
			sessionTimeOutSpinner.setValue(network.getSessionTimeoutMinutes());
			keyStoreFileTextBox.setText(network.getKeystoreFile());
		}
	}

}
