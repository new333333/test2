package org.kabling.teaming.install.client.config;

import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.DlgBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.HASearchNode;
import org.kabling.teaming.install.shared.Lucene;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

public class NewLuceneHANodeDialog extends DlgBox
{
	protected AppResource RBUNDLE = AppUtil.getAppResource();
	private VibeTextBox nameTextBox;
	private VibeTextBox hostTextBox;
	private GwValueSpinner rmiPortSpinner;
	private Lucene lucene;
	private TextArea descriptionTextArea;

	public NewLuceneHANodeDialog(Lucene lucene)
	{
		super(false, true, DlgButtonMode.OkCancel);
		this.lucene = lucene;
	}

	public boolean isValid()
	{
		if (!(nameTextBox.isValid() & hostTextBox.isValid()))
		{
			return false;
		}

		List<HASearchNode> nodes = lucene.getSearchNodesList();

		// Make sure we a unique name
		for (HASearchNode node : nodes)
		{
			if (node.getName().equals(nameTextBox.getText()))
			{
				return false;
			}
		}
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
		return content;
	}

	@Override
	public Object getDataFromDlg()
	{
		if (!isValid())
			return null;

		HASearchNode node = new HASearchNode();
		node.setName(nameTextBox.getText());
		node.setHostName(hostTextBox.getText());
		node.setTitle(descriptionTextArea.getText());
		node.setRmiPort(rmiPortSpinner.getValueAsInt());
		lucene.getSearchNodesList().add(node);

		return lucene;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return nameTextBox;
	}
}
