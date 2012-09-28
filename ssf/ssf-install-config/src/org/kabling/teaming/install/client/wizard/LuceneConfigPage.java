package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.widgets.GwTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.Lucene;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LuceneConfigPage implements IWizardPage<InstallerConfig>
{

	private InstallerConfig config;
	private GwValueSpinner bufferingRAMSpinner;
	private GwValueSpinner maxBooleanSpinner;
	private GwValueSpinner mergeFactorSpinner;
	private GwTextBox networkInterfaceAddrTextBox;
	private GwTextBox luceneAddrTextBox;
	private GwValueSpinner rmiPortSpinner;

	public LuceneConfigPage(InstallerConfig config)
	{
		this.config = config;
	}

	@Override
	public String isValid()
	{
		return null;
	}

	@Override
	public Widget getWizardUI()
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("wizardPage");

		HTML descLabel = new HTML("You can run lucene as a server on the same machine where the FILR sofware is installed or on a remote machine. ");
		descLabel.addStyleName("wizardPageDesc");
		fPanel.add(descLabel);

		FlexTable table = new FlexTable();
		fPanel.add(table);

		int row = 0;
		FlexTable hostTable = new FlexTable();
		fPanel.add(hostTable);
		{
			row = 0;
			// Merge Factor
			InlineLabel keyLabel = new InlineLabel("Lucene Server Address:");
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");
			
		
			luceneAddrTextBox = new GwTextBox();
			hostTable.setWidget(row, 1, luceneAddrTextBox);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// RMI Port
			InlineLabel keyLabel = new InlineLabel("RMI Port:");
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");
			
		
			rmiPortSpinner = new GwValueSpinner(1199, 1024, 9999, null);
			hostTable.setWidget(row, 1, rmiPortSpinner);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		initUIWithData();
		return fPanel;
	}

	@Override
	public String getPageTitle()
	{
		return "Lucene";
	}

	@Override
	public boolean canFinish()
	{
		return true;
	}

	@Override
	public void save()
	{
	}

	public void initUIWithData()
	{
		Lucene lucene = config.getLucene();
		
		if (lucene != null)
		{
			maxBooleanSpinner.setValue(lucene.getMaxBooleans());
			//TODO: RAM for buffering
			mergeFactorSpinner.setValue(lucene.getMergeFactor());
			
			luceneAddrTextBox.setText(lucene.getIndexHostName());
			rmiPortSpinner.setValue(lucene.getRmiPort());
		}
	}

}
