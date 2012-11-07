package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kabling.teaming.install.client.AppUtil;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class LuceneHighAvailabilityPanel extends Composite implements Handler,ClickHandler,EditSuccessfulHandler
{
	private MultiSelectionModel<HASearchNode> selectionModel;
	private CellTable<HASearchNode> table;
	private FlowPanel content;
	private Button deleteButton;
	private Button newButton;
	private ListDataProvider<HASearchNode> dataProvider;
	protected AppResource RBUNDLE = AppUtil.getAppResource();
	private InstallerConfig config;
	public LuceneHighAvailabilityPanel()
	{
		content = new FlowPanel();
		initWidget(content);
		
		FlowPanel actionsPanel = new FlowPanel();
		content.add(actionsPanel);
		
		newButton = new Button(RBUNDLE.newEllipsis());
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
		this.config = config;
		if (table == null)
		{
			CellTable.Resources res =  GWT.create(CellTableResource.class);
			table = new CellTable<HASearchNode>(5,res);
			table.setWidth("400px");
			content.add(table);
		}

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
				Window.alert("HA Create Node");
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
		table.addColumn(nameColumn, "Name");
		table.addColumn(hostNameColumn, "Address");
		table.addColumn(portColumn, "RMI Port");

		// Create a data provider.
		dataProvider = new ListDataProvider<HASearchNode>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<HASearchNode> list = dataProvider.getList();

		List<HASearchNode> searchNodesList = config.getLucene().getSearchNodesList();
		if (searchNodesList != null && searchNodesList.size() > 0)
		{
			for (HASearchNode contact : searchNodesList)
			{
				//Ignore the dummy elements
				if (contact.getHostName().startsWith("xxx.") || contact.getHostName().startsWith("yyy."))
				{
					continue;
				}
				list.add(contact);
			}
			if (list.size() == 0)
			{
				table.setEmptyTableWidget(new Label("No high availability nodes found"));
			}
		}
		else
		{
			table.setEmptyTableWidget(new Label("No high availability nodes found"));
		}

		selectionModel = new MultiSelectionModel<HASearchNode>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(this);

//		PagerResource resource = GWT.create(PagerResource.class);
//		pager = new SimplePager(TextLocation.CENTER, resource, false, getPageSize(), true);
//		pager.setRangeLimited(true);
//		pager.getElement().setAttribute("align", "center");
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
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
			for (HASearchNode node: selectedSet)
			{
				dataProvider.getList().remove(node);
			}
			table.setRowCount(dataProvider.getList().size());
		}
		else if (event.getSource() == newButton)
		{
			NewLuceneHANodeDialog dlg = new NewLuceneHANodeDialog(dataProvider.getList());
			dlg.createAllDlgContent("New Search Node", this, null, null);
			dlg.show(true);
		}
	}

	@Override
	public boolean editSuccessful(Object obj) {
		if (obj != null)
			dataProvider.getList().add((HASearchNode)obj);
		return true;
	}

	public List<HASearchNode> getAvailableNodes() {
		List<HASearchNode> returnList = new ArrayList<HASearchNode>();
		
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
