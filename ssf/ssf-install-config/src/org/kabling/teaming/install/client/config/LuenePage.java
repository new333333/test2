package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.Lucene;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * UI for setting up Lucene
 * 
 */
public class LuenePage extends ConfigPageDlgBox implements ClickHandler, ChangeHandler
{
	private VibeTextBox luceneAddrTextBox;
	private GwValueSpinner rmiPortSpinner;
	private GwValueSpinner maxBoolsSpinner;
	private GwValueSpinner mergeFactorSpinner;
	private ListBox configTypeListBox;
	private LuceneHighAvailabilityPanel haPanel;

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
		contentPanel.addStyleName("lucenePageContent");

		int row = 0;
		FlexTable hostTable = new FlexTable();
		contentPanel.add(hostTable);

		{
			// Max Threads
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxBooleansColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			maxBoolsSpinner = new GwValueSpinner(10000, 10000, 12000, RBUNDLE.default10000());
			maxBoolsSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			hostTable.setWidget(row, 1, maxBoolsSpinner);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Max Active
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxActiveColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			mergeFactorSpinner = new GwValueSpinner(10, 10, 50, RBUNDLE.default10());
			mergeFactorSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			hostTable.setWidget(row, 1, mergeFactorSpinner);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Configuration type
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.configurationTypeColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			configTypeListBox = new ListBox(false);
			configTypeListBox.addChangeHandler(this);
			configTypeListBox.addItem(RBUNDLE.local(),"local");
			configTypeListBox.addItem(RBUNDLE.server(),"server");
			configTypeListBox.addItem(RBUNDLE.highAvailablity(),"ha");
			hostTable.setWidget(row, 1, configTypeListBox);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			// Host Name or IP Address
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			luceneAddrTextBox = new VibeTextBox();
			hostTable.setWidget(row, 1, luceneAddrTextBox);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// RMI Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.rmiPortColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			rmiPortSpinner = new GwValueSpinner(1199, 1024, 9999, RBUNDLE.defaultIs1199());
			rmiPortSpinner.addStyleName("luceneConfigWizRmiSpinnerLabel");
			hostTable.setWidget(row, 1, rmiPortSpinner);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;

			haPanel = new LuceneHighAvailabilityPanel();
			haPanel.setVisible(false);
			hostTable.setWidget(row, 1, haPanel);
			hostTable.getFlexCellFormatter().addStyleName(row, 1, "table-value");
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
		return null;
	}

	@Override
	public void initUIWithData()
	{
		Lucene lucene = config.getLucene();
		
		if (lucene != null)
		{
			maxBoolsSpinner.setValue(lucene.getMaxBooleans());
			mergeFactorSpinner.setValue(lucene.getMergeFactor());
			if (lucene.getLocation().equals("::luceneMode_Local") || lucene.getLocation().equals("local"))
			{
				showUIForLocalMode();
			}
			else if (lucene.getLocation().equals("server"))
			{
				luceneAddrTextBox.setText(lucene.getIndexHostName());
				rmiPortSpinner.setValue(lucene.getRmiPort());
				showUIForServerMode();
			}
			else
			{
				showUIForHighAvailabilityMode();
			}
		}
	}

	

	@Override
	public void onChange(ChangeEvent event)
	{
		if (event.getSource() == configTypeListBox)
		{
			int index = configTypeListBox.getSelectedIndex();
			
			if (index == 0) //Local
			{
				showUIForLocalMode();
			}
			else if (index == 1) //Server
			{
				showUIForServerMode();
			}
			else
			{
				showUIForHighAvailabilityMode();
			}
		}
	}
	
	private void showUIForLocalMode()
	{
		luceneAddrTextBox.setText("localhost");
		luceneAddrTextBox.setEnabled(false);
		
		rmiPortSpinner.setValue(1199);
		rmiPortSpinner.setEnabled(false);
		
		haPanel.setVisible(false);
	}
	
	private void showUIForServerMode()
	{
		luceneAddrTextBox.setText(config.getLucene().getIndexHostName());
		rmiPortSpinner.setValue(config.getLucene().getRmiPort());
		
		luceneAddrTextBox.setEnabled(true);
		rmiPortSpinner.setEnabled(true);
		
		haPanel.setVisible(false);
	}
	
	private void showUIForHighAvailabilityMode()
	{
		luceneAddrTextBox.setText(config.getLucene().getIndexHostName());
		rmiPortSpinner.setValue(config.getLucene().getRmiPort());
		
		haPanel.setVisible(true);
		haPanel.updateUI(config);
	}
}
