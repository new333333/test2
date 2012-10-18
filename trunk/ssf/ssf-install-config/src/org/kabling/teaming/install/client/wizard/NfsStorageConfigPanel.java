package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.widgets.VibeTextBox;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;

public class NfsStorageConfigPanel extends Composite
{
	private VibeTextBox hostNameTextBox;
	private VibeTextBox remoteDirTextBox;
	private CheckBox nfsv4CheckBox;
	private VibeTextBox domainNameTextBox;

	public NfsStorageConfigPanel()
	{
		FlowPanel content = new FlowPanel();
		initWidget(content);

		HTML descLabel = new HTML(
				"Fill in the NFS Server Hostname and Exported Remote Directory for the Appliance to use.");
		descLabel.addStyleName("storageDetailsDescLabel");
		content.add(descLabel);

		FlowPanel partitionInfoPanel = new FlowPanel();
		partitionInfoPanel.addStyleName("partitionInfoPanel");
		content.add(partitionInfoPanel);

		FlexTable table = new FlexTable();
		table.addStyleName("storageDetailsTable");
		partitionInfoPanel.add(table);

		int row = 0;
		{
			// Host Name
			InlineLabel keyLabel = new InlineLabel("Host Name:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostNameTextBox = new VibeTextBox();
			table.setWidget(row, 1, hostNameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Remote Directory
			InlineLabel keyLabel = new InlineLabel("Remote Directory:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			remoteDirTextBox = new VibeTextBox();
			table.setWidget(row, 1, remoteDirTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// NFSv4 share
			nfsv4CheckBox = new CheckBox("NFSv4 Share");
			table.setWidget(row, 0, nfsv4CheckBox);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");
		}
		
		{
			row++;
			// Domain Name
			InlineLabel keyLabel = new InlineLabel("Domain Name:");
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			domainNameTextBox = new VibeTextBox();
			table.setWidget(row, 1, domainNameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
	}
}
