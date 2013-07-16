package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.EditCanceledHandler;
import org.kabling.teaming.install.client.EditSuccessfulHandler;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.images.CellTableResource;
import org.kabling.teaming.install.client.widgets.AnchorCell;
import org.kabling.teaming.install.shared.HASearchNode;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class LuceneHighAvailabilityPanel extends Composite implements Handler, ClickHandler, EditSuccessfulHandler, EditCanceledHandler
{
	private MultiSelectionModel<HASearchNode> selectionModel;
	private CellTable<HASearchNode> table;
	private FlowPanel content;
	private Button deleteButton;
	private Button newButton;
	private ListDataProvider<HASearchNode> dataProvider;
	protected AppResource RBUNDLE = AppUtil.getAppResource();

	// newCreation flag helps to keep track of whether we are editing an existing node
	// or creating a new one as we are using one dialog for creation and editing.
	// When editSuccessful get's called, we can use this flag to determine and update the
	// list provider accordingly
	private boolean newCreation;

	public LuceneHighAvailabilityPanel()
	{
		content = new FlowPanel();
		initWidget(content);

		FlowPanel actionsPanel = new FlowPanel();
		content.add(actionsPanel);

		// Add buttons to add/remove search nodes
		newButton = new Button(RBUNDLE.add());
		newButton.addClickHandler(this);
		newButton.addStyleName("luceneHAButton");
		actionsPanel.add(newButton);

		deleteButton = new Button(RBUNDLE.remove());
		deleteButton.addClickHandler(this);
		deleteButton.setEnabled(false);
		deleteButton.addStyleName("luceneHAButton");
		actionsPanel.add(deleteButton);
	}

	public void updateUI(InstallerConfig config)
	{
		if (table == null)
		{
			CellTable.Resources res = GWT.create(CellTableResource.class);
			table = new CellTable<HASearchNode>(5, res);
			table.setWidth("400px");
			content.add(table);

			// Name Column
			AnchorCell anchorCell = new AnchorCell();
			Column<HASearchNode, String> nameColumn = new Column<HASearchNode, String>(anchorCell)
			{
				@Override
				public String getValue(HASearchNode haSearchNode)
				{
					return haSearchNode.getName();
				}
			};

			nameColumn.setFieldUpdater(new FieldUpdater<HASearchNode, String>()
			{

				@Override
				public void update(int index, HASearchNode object, String value)
				{
					// editing an existing one
					newCreation = false;

					// Show the dialog to edit
					NewLuceneHANodeDialog dlg = new NewLuceneHANodeDialog(object, dataProvider.getList());
					dlg.createAllDlgContent(RBUNDLE.newSearchNode(), LuceneHighAvailabilityPanel.this, null, null);
					dlg.show(true);
				}
			});

			// Host Name Column
			TextColumn<HASearchNode> hostNameColumn = new TextColumn<HASearchNode>()
			{
				@Override
				public String getValue(HASearchNode haSearchNode)
				{
					return haSearchNode.getHostName();
				}
			};

			// Port Column
			TextColumn<HASearchNode> portColumn = new TextColumn<HASearchNode>()
			{
				@Override
				public String getValue(HASearchNode haSearchNode)
				{
					return String.valueOf(haSearchNode.getRmiPort());
				}
			};

			// Add the columns.
			table.addColumn(nameColumn, RBUNDLE.name());
			table.addColumn(hostNameColumn, RBUNDLE.hostName());
			table.addColumn(portColumn, RBUNDLE.rmiPort());

			// Create a data provider.
			dataProvider = new ListDataProvider<HASearchNode>();

			// Connect the table to the data provider.
			dataProvider.addDataDisplay(table);

			selectionModel = new MultiSelectionModel<HASearchNode>();
			table.setSelectionModel(selectionModel);
			selectionModel.addSelectionChangeHandler(this);
		}

		dataProvider.setList(new ArrayList<HASearchNode>());
		
		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<HASearchNode> list = dataProvider.getList();

		List<HASearchNode> searchNodesList = config.getLucene().getSearchNodesList();
		if (searchNodesList != null && searchNodesList.size() > 0)
		{
			for (HASearchNode contact : searchNodesList)
			{
				// Ignore the dummy elements
				// Default installer.xml contains this nodes..
				if (contact.getHostName().startsWith("xxx.") || contact.getHostName().startsWith("yyy."))
				{
					continue;
				}
				list.add(contact);
			}

			// We don't have anything to display
			if (list.size() == 0)
			{
				table.setEmptyTableWidget(new Label(RBUNDLE.noAvailabilityNodesExists()));
			}
		}
		// We don't have anything to display
		else
		{
			table.setEmptyTableWidget(new Label(RBUNDLE.noAvailabilityNodesExists()));
		}

	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		// Enable/Disable delete button based on selection
		if (selectionModel instanceof MultiSelectionModel)
		{

			Set<HASearchNode> selectedSet = ((MultiSelectionModel<HASearchNode>) selectionModel).getSelectedSet();
			if (selectedSet.size() > 0)
			{
				deleteButton.setEnabled(true);
			}
			else
			{
				deleteButton.setEnabled(false);
			}
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		Set<HASearchNode> selectedSet = ((MultiSelectionModel<HASearchNode>) selectionModel).getSelectedSet();
		if (event.getSource() == deleteButton)
		{
			// Delete the selected nodes
			for (HASearchNode node : selectedSet)
			{
				dataProvider.getList().remove(node);
			}
			int currentSize = dataProvider.getList().size();
			table.setRowCount(currentSize);
			if (currentSize == 0)
			{
				table.setEmptyTableWidget(new Label(RBUNDLE.noAvailabilityNodesExists()));
			}
		}
		else if (event.getSource() == newButton)
		{
			// Set the newCreation flag
			newCreation = true;
			NewLuceneHANodeDialog dlg = new NewLuceneHANodeDialog(null, dataProvider.getList());
			dlg.createAllDlgContent(RBUNDLE.newSearchNode(), this, null, null);
			dlg.show(true);
		}
	}

	@Override
	public boolean editSuccessful(Object obj)
	{

		if (obj != null)
		{
			// If we are creating a new one, add it to the list provider
			if (newCreation)
			{
				HASearchNode node = (HASearchNode) obj;
				dataProvider.getList().add(node);
				// Reset the flag
				newCreation = false;
			}
			table.redraw();
		}
		return true;
	}

	@Override
	public boolean editCanceled()
	{
		newCreation = false;
		return true;
	}

	public List<HASearchNode> getAvailableNodes()
	{
		List<HASearchNode> returnList = new ArrayList<HASearchNode>();

		// Copy the data from list provider as the dataProvider.getList() has few decorating
		// wrappers GWT serialization does not like
		if (dataProvider.getList() != null)
		{
			for (HASearchNode node : dataProvider.getList())
			{
				returnList.add(node);
			}
		}
		return returnList;
	}

}
