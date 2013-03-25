package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.ValueRequiredBasedOnBoolValidator;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.Clustered;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.Network;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * UI for setting up clustering
 * 
 */
public class ClusteringPage extends ConfigPageDlgBox implements ClickHandler, ChangeHandler
{
	private VibeTextBox jvmRouteTextBox;
	private ListBox cacheProviderListBox;
	private VibeTextBox hostNameTextBox;
	private VibeTextBox multicastGroupAddrTextBox;
	private GwValueSpinner multicastGroupPortSpinner;
	private CheckBox enableClusteredCheckBox;
	private ValueRequiredBasedOnBoolValidator jvmRouteValidator;
	private ValueRequiredBasedOnBoolValidator multicastHostValidator;
	private FlowPanel contentPanel;
	private VibeTextBox memcachedAddressesTextBox;
	private FlexTable ehcacheTable;
	private FlexTable memcacheTable;
	private ValueRequiredBasedOnBoolValidator memcachedAddrValidator;
	private VibeTextBox hostTextBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		// Title
		HTML titleDescLabel = new HTML(RBUNDLE.clusteringPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("clusteringPageContent");

		// Enable clustering environment
		enableClusteredCheckBox = new CheckBox(RBUNDLE.enableClusteredEnvironment());
		enableClusteredCheckBox.addClickHandler(this);
		contentPanel.add(enableClusteredCheckBox);

		// All the other content goes inside a table
		FlexTable table = new FlexTable();
		table.addStyleName("clusteringTable");
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
			cacheProviderListBox.addItem("memcached");
			cacheProviderListBox.setEnabled(false);
			cacheProviderListBox.addChangeHandler(this);
			table.setWidget(row, 1, cacheProviderListBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		if (enableClusteredCheckBox.getValue())
		{
			//Host Name is required
			if (!(hostTextBox.isValid() & jvmRouteTextBox.isValid()))
			{
				setErrorMessage(RBUNDLE.requiredField());
				return null;
			}
			
			if (cacheProviderListBox.getSelectedIndex() <= 0 && !(hostNameTextBox.isValid() & multicastGroupAddrTextBox.isValid()))
			{
				setErrorMessage(RBUNDLE.allFieldsRequired());
				return null;
			}
			else if (cacheProviderListBox.getSelectedIndex() == 1)
			{
				if (!(memcachedAddressesTextBox.isValid()))
				{
					setErrorMessage(RBUNDLE.allFieldsRequired());
					return null;
				}
			}
		}
		
		//Save the settings
		Clustered clustered = config.getClustered();
		
		clustered.setEnabled(enableClusteredCheckBox.getValue());
		clustered.setJvmRoute(jvmRouteTextBox.getText());
		
		//Save type
		if (cacheProviderListBox.getSelectedIndex() == 0)
		{
			clustered.setCachingProvider("ehcache");
			clustered.setCacheService(hostNameTextBox.getText());
			clustered.setCacheGroupAddress(multicastGroupAddrTextBox.getText());
			clustered.setCacheGroupPort(multicastGroupPortSpinner.getValueAsInt());
		}
		else
		{
			clustered.setCachingProvider("memcache");
			clustered.setMemCachedAddress(memcachedAddressesTextBox.getText());
		}
		
		Network network = config.getNetwork();
		network.setHost(hostTextBox.getText());
		
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

			// Set the validator state based on if clustering is enabled
			jvmRouteValidator.setRequired(enableClusteredCheckBox.getValue());
			jvmRouteTextBox.setText(clustered.getJvmRoute());

			if (clustered.getCachingProvider().equals("ehcache"))
			{
				cacheProviderListBox.setSelectedIndex(0);
				contentPanel.add(createEhCacheUI());
				hostNameTextBox.setText(clustered.getCacheService());
				multicastHostValidator.setRequired(enableClusteredCheckBox.getValue());
				multicastGroupAddrTextBox.setText(clustered.getCacheGroupAddress());
				multicastGroupPortSpinner.setValue(clustered.getCacheGroupPort());
			}
			else
			{
				cacheProviderListBox.setSelectedIndex(1);
				contentPanel.add(createMemCacheUI());
				memcachedAddressesTextBox.setText(clustered.getMemCachedAddress());
				memcachedAddrValidator.setRequired(enableClusteredCheckBox.getValue());
			}
		}
		
		Network network = config.getNetwork();
		if (network != null)
			hostTextBox.setText(network.getHost());
	}

	@Override
	public void onClick(ClickEvent event)
	{
		super.onClick(event);

		if (event.getSource() == enableClusteredCheckBox)
		{
			jvmRouteValidator.setRequired(enableClusteredCheckBox.getValue());
			if (multicastHostValidator != null)
				multicastHostValidator.setRequired(enableClusteredCheckBox.getValue());
			
			if (memcachedAddrValidator != null)
				memcachedAddrValidator.setRequired(enableClusteredCheckBox.getValue());
		}
	}

	private FlexTable createEhCacheUI()
	{

		// All the other content goes inside a table
		ehcacheTable = new FlexTable();

		int row = 0;
		{
			row++;
			// Description about network interface
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.networkInterfaceForCacheDesc());
			keyLabel.addStyleName("networkInterfaceForCacheDescLabel");
			ehcacheTable.setWidget(row, 0, keyLabel);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");
			ehcacheTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		}

		{
			row++;
			// Host Name
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
			ehcacheTable.setWidget(row, 0, keyLabel);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostNameTextBox = new VibeTextBox();
			hostNameTextBox.setWatermark(RBUNDLE.optional());
			ehcacheTable.setWidget(row, 1, hostNameTextBox);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Mulitcast Group Address (Ip Address)
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.multicastGroupAddrColon());
			ehcacheTable.setWidget(row, 0, keyLabel);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			multicastGroupAddrTextBox = new VibeTextBox();
			multicastHostValidator = new ValueRequiredBasedOnBoolValidator(true, multicastGroupAddrTextBox);
			multicastGroupAddrTextBox.setValidator(multicastHostValidator);
			ehcacheTable.setWidget(row, 1, multicastGroupAddrTextBox);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Mulitcast Group Address (Ip Address)
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.multicastGroupPortColon());
			ehcacheTable.setWidget(row, 0, keyLabel);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			multicastGroupPortSpinner = new GwValueSpinner(4446, 1024, 9999, null);
			ehcacheTable.setWidget(row, 1, multicastGroupPortSpinner);
			ehcacheTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		return ehcacheTable;
	}

	private FlexTable createMemCacheUI()
	{
		memcacheTable = new FlexTable();

		int row = 0;
		{
			row++;
			// Description about network interface
			HTML keyLabel = new HTML(RBUNDLE.memcacheAddressDesc());
			keyLabel.addStyleName("networkInterfaceForCacheDescLabel");
			memcacheTable.setWidget(row, 0, keyLabel);
			memcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");
			memcacheTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		}

		{
			row++;
			// Memcached addresses
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.serverAddressColon());
			memcacheTable.setWidget(row, 0, keyLabel);
			memcacheTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			memcachedAddressesTextBox = new VibeTextBox();
			memcachedAddrValidator = new ValueRequiredBasedOnBoolValidator(true, memcachedAddressesTextBox);
			memcacheTable.setWidget(row, 1, memcachedAddressesTextBox);
			memcacheTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		return memcacheTable;
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		if (event.getSource() == cacheProviderListBox)
		{
			// Ehcache
			if (cacheProviderListBox.getSelectedIndex() == 0)
			{
				if (ehcacheTable == null)
					contentPanel.add(createEhCacheUI());
				ehcacheTable.setVisible(true);
				if (memcacheTable != null)
					memcacheTable.setVisible(false);
			}
			// Memcache
			else
			{
				if (memcacheTable == null)
					contentPanel.add(createMemCacheUI());
				memcacheTable.setVisible(true);
				if (ehcacheTable != null)
					ehcacheTable.setVisible(false);
			}
		}
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("clustering");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.CLUSTERING);
		
		//As we need to update host name
		sectionsToUpdate.add(LeftNavItemType.NETWORK);
		
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}

}
