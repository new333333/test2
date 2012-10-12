package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwTextBox;
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

public class ClusteringPage extends ConfigPageDlgBox
{
	private GwTextBox jvmRouteTextBox;
	private ListBox cacheProviderListBox;
	private GwTextBox hostNameTextBox;
	private GwTextBox multicastGroupAddrTextBox;
	private GwValueSpinner multicastGroupPortSpinner;
	private CheckBox enableClusteredCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.clusteringPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("clusteringPageContent");

		enableClusteredCheckBox = new CheckBox(RBUNDLE.enableClusteredEnvironment());
		contentPanel.add(enableClusteredCheckBox);
		
		FlexTable table = new FlexTable();
		table.addStyleName("clusteringTable");
		contentPanel.add(table);

		int row = 0;
		{

			// JVM Route
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.jvmRouteColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			jvmRouteTextBox = new GwTextBox();
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

			hostNameTextBox = new GwTextBox();
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

			multicastGroupAddrTextBox = new GwTextBox();
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
			
		}
	}

}
