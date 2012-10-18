package org.kabling.teaming.install.client.wizard;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class DeviceStorageConfigPanel extends Composite
{
	public DeviceStorageConfigPanel()
	{
		FlowPanel content = new FlowPanel();
		initWidget(content);
		

		HTML descLabel = new HTML("Select the Hard Disk and partition for the Appliance to use as remote storage:");
		descLabel.addStyleName("storageDetailsDescLabel");
		content.add(descLabel);
		
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName("deviceStorageHeaderPanel");
		content.add(headerPanel);
		
		InlineLabel hardDiskLabel = new InlineLabel("Select Hard Disk:");
		headerPanel.add(hardDiskLabel);
		
		ListBox hardDiskListBox = new ListBox(false);
		headerPanel.add(hardDiskListBox);
		
		InlineLabel partitionLabel = new InlineLabel("Select Partition:");
		partitionLabel.addStyleName("partitionSelectLabel");
		headerPanel.add(partitionLabel);
		
		ListBox partitionListBox = new ListBox(false);
		headerPanel.add(partitionListBox);
		
		FlowPanel partitionInfoPanel = new FlowPanel();
		partitionInfoPanel.addStyleName("partitionInfoPanel");
		headerPanel.add(partitionInfoPanel);
		

		FlexTable table = new FlexTable();
		table.addStyleName("storageDetailsTable");
		partitionInfoPanel.add(table);

		int row = 0;
		{
			// File System
			InlineLabel keyLabel = new InlineLabel("File System:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			InlineLabel valueLabel = new InlineLabel("EXT3");
			table.setWidget(row, 1, valueLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// File System
			InlineLabel keyLabel = new InlineLabel("Capacity");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			InlineLabel valueLabel = new InlineLabel("100GB");
			table.setWidget(row, 1, valueLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Free Space
			InlineLabel keyLabel = new InlineLabel("Free Space");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			InlineLabel valueLabel = new InlineLabel("30GB");
			table.setWidget(row, 1, valueLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
	}
}
