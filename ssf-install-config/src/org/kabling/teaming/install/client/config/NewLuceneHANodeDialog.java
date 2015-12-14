package org.kabling.teaming.install.client.config;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.DlgBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.HASearchNode;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

public class NewLuceneHANodeDialog extends DlgBox
{
	protected AppResource RBUNDLE = AppUtil.getAppResource();
	private VibeTextBox nameTextBox;
	private VibeTextBox hostTextBox;
	private GwValueSpinner rmiPortSpinner;
	private TextArea descriptionTextArea;
	private Label errorLabel;
	private List<HASearchNode> currentNodes;
	private HASearchNode searchNode;

	public NewLuceneHANodeDialog(HASearchNode node,List<HASearchNode> currentNodes)
	{
		super(false, true, DlgButtonMode.OkCancel);
		this.currentNodes = currentNodes;
		
		//Save the node, this helps to determine if we are creating or editing existing node
		this.searchNode = node;
	}

	public boolean isValid()
	{
		//Set up the error label
		if (errorLabel == null)
		{
			errorLabel = new Label();
			errorLabel.setStyleName("errorLabel");
			getErrorPanel().add(errorLabel);
		}
		
		//We need to have the name and host name
		if (!(nameTextBox.isValid() & hostTextBox.isValid()))
		{
			errorLabel.setText(RBUNDLE.requiredField());
			getErrorPanel().setVisible(true);
			return false;
		}


		// Make sure we a unique name
		if (searchNode == null && currentNodes != null)
		{
			for (HASearchNode node : currentNodes)
			{
				if (node.getName().equals(nameTextBox.getText()))
				{
					errorLabel.setText(RBUNDLE.searchNodeUnique());
					getErrorPanel().setVisible(true);
					return false;
				}
			}
		}
		getErrorPanel().setVisible(false);
		return true;
	}

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel content = new FlowPanel();

		FlexTable table = new FlexTable();
		content.add(table);

		int row = 0;
		{
			// Name
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.nameColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			nameTextBox = new VibeTextBox();
			nameTextBox.setValidator(new ValueRequiredValidator(nameTextBox));
			table.setWidget(row, 1, nameTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Description
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.descriptionColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			descriptionTextArea = new TextArea();
			table.setWidget(row, 1, descriptionTextArea);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Host
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostTextBox = new VibeTextBox();
			hostTextBox.setValidator(new ValueRequiredValidator(hostTextBox));
			table.setWidget(row, 1, hostTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// RMI Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.rmiPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			rmiPortSpinner = new GwValueSpinner(1199, 1024, 9999, RBUNDLE.defaultIs1199());
			rmiPortSpinner.addStyleName("luceneConfigWizRmiSpinnerLabel");
			table.setWidget(row, 1, rmiPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		if (searchNode != null)
		{
			hostTextBox.setText(searchNode.getHostName());
			nameTextBox.setText(searchNode.getName());
			rmiPortSpinner.setValue(searchNode.getRmiPort());
			descriptionTextArea.setText(searchNode.getTitle());
		}
		
		return content;
	}

	@Override
	public Object getDataFromDlg()
	{
		if (!isValid())
			return null;

		//If the search node is null, create it
		if (searchNode == null)
		{
			searchNode = new HASearchNode();
		}
		searchNode.setName(nameTextBox.getText());
		searchNode.setHostName(hostTextBox.getText());
		searchNode.setTitle(descriptionTextArea.getText());
		searchNode.setRmiPort(rmiPortSpinner.getValueAsInt());

		return searchNode;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return nameTextBox;
	}

	@Override
	public HelpData getHelpData()
	{
		return null;
	}
}
