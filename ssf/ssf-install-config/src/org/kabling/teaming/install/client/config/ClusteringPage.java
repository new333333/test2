package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredBasedOnBoolValidator;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.Clustered;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 *  UI for setting up clustering
 *
 */
public class ClusteringPage extends ConfigPageDlgBox
{
	private VibeTextBox jvmRouteTextBox;
	private ListBox cacheProviderListBox;
	private VibeTextBox hostNameTextBox;
	private VibeTextBox multicastGroupAddrTextBox;
	private GwValueSpinner multicastGroupPortSpinner;
	private CheckBox enableClusteredCheckBox;
	private ValueRequiredBasedOnBoolValidator jvmRouteValidator;
	private ValueRequiredBasedOnBoolValidator multicastHostValidator;
	
	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		// Title
		HTML titleDescLabel = new HTML(RBUNDLE.clusteringPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("clusteringPageContent");

		// Enable clustering environment
		enableClusteredCheckBox = new CheckBox(RBUNDLE.enableClusteredEnvironment());
		contentPanel.add(enableClusteredCheckBox);

		// All the other content goes inside a table
		FlexTable table = new FlexTable();
		table.addStyleName("clusteringTable");
		contentPanel.add(table);

		int row = 0;
		{

			// JVM Route
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.jvmRouteColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			jvmRouteTextBox = new VibeTextBox();
			jvmRouteValidator = new ValueRequiredBasedOnBoolValidator(true, jvmRouteTextBox);
			jvmRouteTextBox.setValidator(jvmRouteValidator);
			table.setWidget(row, 1, jvmRouteTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Hibernate Caching provider
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hibernateCachingProviderColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			cacheProviderListBox = new ListBox(false);
			cacheProviderListBox.addItem("ehcache");
			cacheProviderListBox.addItem("memcache");
			table.setWidget(row, 1, cacheProviderListBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Description about network interface
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.networkInterfaceForCacheDesc());
			keyLabel.addStyleName("networkInterfaceForCacheDescLabel");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
		}

		{
			row++;
			// Host Name
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostNameTextBox = new VibeTextBox();
			hostNameTextBox.setWatermark(RBUNDLE.optional());
			table.setWidget(row, 1, hostNameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Mulitcast Group Address (Ip Address)
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.multicastGroupAddrColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			multicastGroupAddrTextBox = new VibeTextBox();
			multicastHostValidator = new ValueRequiredBasedOnBoolValidator(true, multicastGroupAddrTextBox);
			multicastGroupAddrTextBox.setValidator(multicastHostValidator);
			table.setWidget(row, 1, multicastGroupAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Mulitcast Group Address (Ip Address)
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.multicastGroupPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			multicastGroupPortSpinner = new GwValueSpinner(4446, 1024, 9999, null);
			table.setWidget(row, 1, multicastGroupPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		//TODO: Memcached addresses
		
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		if (enableClusteredCheckBox.getValue())
		{
			if (!(hostNameTextBox.isValid() & multicastGroupAddrTextBox.isValid()))
			{
				setErrorMessage(RBUNDLE.allFieldsRequired());
				return null;
			}
		}
		Clustered clustered = config.getClustered();

		clustered.setEnabled(enableClusteredCheckBox.getValue());
		clustered.setJvmRoute(jvmRouteTextBox.getText());
		clustered.setCacheService(hostNameTextBox.getText());
		clustered.setCacheGroupAddress(multicastGroupAddrTextBox.getText());
		clustered.setCacheGroupPort(multicastGroupPortSpinner.getValueAsInt());

		if (cacheProviderListBox.getSelectedIndex() == 0)
			clustered.setCachingProvider("ehcache");
		else
			clustered.setCachingProvider("memcache");
		
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return jvmRouteTextBox;
	}

	@Override
	public void initUIWithData()
	{
		Clustered clustered = config.getClustered();

		if (clustered != null)
		{
			enableClusteredCheckBox.setValue(clustered.isEnabled());
			hostNameTextBox.setText(clustered.getCacheService());
			jvmRouteTextBox.setText(clustered.getJvmRoute());

			if (clustered.getCachingProvider().equals("ehcache"))
				cacheProviderListBox.setSelectedIndex(0);
			else
				cacheProviderListBox.setSelectedIndex(1);

			multicastGroupAddrTextBox.setText(clustered.getCacheGroupAddress());
			multicastGroupPortSpinner.setValue(clustered.getCacheGroupPort());

			//TODO: Memcached addresses
		}
	}

}
