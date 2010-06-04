package org.kablink.teaming.gwt.client.profile.widgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ComponentTable extends Composite {

	private final Object[][] rowData = {
			{ new DeleteButton("Delete"), "A", "B", "C", "D" },
			{ new DeleteButton("Delete"), "E", "F", "G", "H" },
			{ new DeleteButton("Delete"), "I", "J", "K", "L" }, };

	private final ComponentFlexTable table;

	public ComponentTable() {
		
		FlowPanel panel = new FlowPanel();
		table = new ComponentFlexTable(rowData);
		table.addColumn("");
		table.addColumn("Column 1");
		table.addColumn("Column 2");
		table.addColumn("Column 3");
		table.addColumn("Column 4");

		table.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, final int row,
					final int cell) {
				ComponentTable.this.onCellClicked(sender, row, cell);
			}
		});
		
		panel.add(table);

		initWidget(panel);
	}
	
	private void onCellClicked(SourcesTableEvents sender, final int row,
			final int cell) {
		if (row == 0) {
			return;
		}
		final Widget widget = table.getWidget(row, cell);

		if (widget instanceof Label == false) {
			return;
		}
		final Label label = (Label) widget;
		final TextBox textBox = new TextBox();
		textBox.setText(table.getText(row, cell));
		table.setWidget(row, cell, textBox);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				textBox.setFocus(true);
				textBox.selectAll();
			}
		});

		textBox.addKeyboardListener(new KeyboardListenerAdapter() {
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				if (keyCode == KeyboardListener.KEY_ENTER) {
					label.setText(textBox.getText());
					table.setWidget(row, cell, label);
				} else if (keyCode == KeyboardListener.KEY_ESCAPE) {
					table.setWidget(row, cell, label);
				}
			}
		});
		textBox.addFocusListener(new FocusListenerAdapter() {
			public void onLostFocus(Widget sender) {
				label.setText(textBox.getText());
				table.setWidget(row, cell, label);
			}
		});
	}

	public void onModuleLoad() {
		table.addColumn("");
		table.addColumn("Column 1");
		table.addColumn("Column 2");
		table.addColumn("Column 3");
		table.addColumn("Column 4");

		RootPanel.get().add(table);

		table.addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, final int row,
					final int cell) {
				ComponentTable.this.onCellClicked(sender, row, cell);
			}
		});
	}

	private class DeleteButton extends Button {
		public DeleteButton(String text) {
			super(text);
			addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					table.removeRow(getThisRow());
					table.updateRowStyle();
				}
			});
		}

		private int getThisRow() {
			for (int i = 1; i < table.getRowCount(); ++i) {
				if (table.getWidget(i, 0) == this) {
					return i;
				}
			}
			return -1;
		}
	}
}

class ComponentFlexTable extends FlexTable {
	protected static final int HEADER_ROW = 0;

	private int rowIndex = 1;
	private Object[][] rowData = null;

	public ComponentFlexTable(Object[][] rowData) {
		insertRow(HEADER_ROW);
		getRowFormatter().addStyleName(HEADER_ROW, "ComponentFlexTable-Header");
		setCellSpacing(0);
		addStyleName("ComponentFlexTable");
		this.rowData = rowData;
		createRows(0);
		updateRowStyle();
	}

	public void createRows(int rowIndex) {
		if (rowData == null)
			return;

		for (int row = rowIndex; row < rowData.length; row++) {
			addRow(rowData[row]);
		}
	}

	private void addRow(Object[] cellObjects) {
		for (int i = 0; i < cellObjects.length; i++) {
			addCell(rowIndex, i, cellObjects[i]);
		}
		rowIndex++;
	}

	public void addCell(int row, int cell, Object cellObject) {
		if (cellObject instanceof Widget)
			setWidget(row, cell, (Widget) cellObject);
		else
			setWidget(row, cell, new Label(cellObject.toString()));
		getCellFormatter().addStyleName(row, cell, "ComponentFlexTable-Cell");
	}

	public void updateRowStyle() {
		HTMLTable.RowFormatter rf = getRowFormatter();
		for (int row = 1; row < getRowCount(); ++row) {
			if ((row % 2) != 0) {
				rf.removeStyleName(row, "ComponentFlexTable-EvenRow");
				rf.addStyleName(row, "ComponentFlexTable-OddRow");
			} else {
				rf.removeStyleName(row, "ComponentFlexTable-OddRow");
				rf.addStyleName(row, "ComponentFlexTable-EvenRow");
			}
		}
	}

	public void addColumn(Object columnHeading) {
		Widget widget = new Label(columnHeading.toString());
		int columnIndex = getColumnCount();
		widget.setWidth("100%");
		widget.addStyleName("ComponentFlexTable-ColumnLabel");

		setWidget(HEADER_ROW, columnIndex, widget);

		getCellFormatter().addStyleName(HEADER_ROW, columnIndex,
				"ComponentFlexTable-ColumnLabelCell");
	}

	public int getColumnCount() {
		return getCellCount(HEADER_ROW);
	}
}
