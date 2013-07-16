/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.datatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.rpc.shared.SaveColumnWidthsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.SliderBar;
import org.kablink.teaming.gwt.client.widgets.Spinner;
import org.kablink.teaming.gwt.client.widgets.SpinnerListener;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements Vibe's data table column sizing dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class SizeColumnsDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private ApplyColumnWidths				m_acw;					// Interface to apply column width changes.
	private BinderInfo						m_folderInfo;			// The folder the dialog is running against.
	private boolean							m_warnOnUnitMix;		// true -> Warn the user about mixing %/pixel values.  false -> Don't.
	private ColumnWidth						m_defaultColumnWidth;	// The default column width for the data table.
	private FlexCellFormatter				m_dataTableCF;			//
	private FlexTable						m_dataTable;			//
	private GwtTeamingDataTableImageBundle	m_images;				// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;				// Access to Vibe's messages.
	private int								m_dtAbsTop;				// The absolute top of the data table whose columns are being sized.  Used to position the dialog.
	private List<FolderColumn> 				m_fcList;				// The columns the dialog is sizing.
	private Map<String, ColumnWidth>		m_columnWidths;			// The current widths of the columns.
	private Map<String, ColumnWidth>		m_defaultColumnWidths;	// The default widths of the columns in the data table.
	private Map<String, ColumnWidth>		m_initialColumnWidths;	// The widths of the columns passed in when the dialog was invoked.
	private RowFormatter					m_dataTableRF;			//
	private ScrollPanel						m_sp;					// ScrollPanel with the dialog's contents.

	private final static int DATA_TABLE_ROW_HEIGHT	= 32;	// Height of a row in a data table.
	private final static int PADDING_FOR_DLG_HEADER	= 25;	// Spacing to allow for the dialog header.

	// The following define the column indexes into the FlexTable used
	// to edit the column sizes.
	private final static int COL_DEFAULT_CB	= 0;
	private final static int COL_COLUMN		= 1;
	private final static int COL_RESIZE		= 2;
	private final static int COL_SIZE		= 3;
	private final static int COL_UNIT		= 4;
	
	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-sizeColumnsDlg-label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private SizeColumnsDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.sizeColumnsDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
	}

	/*
	 * Changes a column width, both in the data table and the data
	 * structures used to persist the changes.
	 */
	private void adjustColumnWidth(String cName, SliderBar vSizeSlider, ValueSpinner vSizeSpinner, ColumnWidth cw) {
		// Adjust the columns...
		if (null == cw)
		     m_columnWidths.remove(cName);
		else m_columnWidths.put(cName, cw);
		
		// ...adjust the widgets to reflect the new width...
		double value = cw.getWidth();
		vSizeSpinner.getTextBox().setValue(String.valueOf(value));
		vSizeSpinner.getSpinner().setValue(value, false);	// false -> Don't fire...
		vSizeSlider.setCurrentValue(       value, false);	// ...any change events. 
		
		// ...and table to show the width.
		m_acw.applyColumnWidths(m_fcList, m_columnWidths, m_defaultColumnWidth);
	}
	
	/*
	 * Changes a column width based on a unit change.
	 */
	private void adjustColumnWidthByUnits(String cName, SliderBar vSizeSlider, ValueSpinner vSizeSpinner, Unit units) {
		int max = getUnitMax(units);
		vSizeSlider.setMaxValue(max);
		Spinner sizeSpinner = vSizeSpinner.getSpinner();
		double value = sizeSpinner.getValue();
		sizeSpinner.setMax(max);
		if (max < value) {
			sizeSpinner.setValue(max, false);
			value = max;
		}
		adjustColumnWidth(cName, vSizeSlider, vSizeSpinner, new ColumnWidth(value, units));
	}

	/*
	 * Connect the appropriate column listeners to the various sizing
	 * widgets for a column.
	 */
	private void connectColumnListeners(final String cName, final CheckBox useDefaultCB, final SliderBar vSizeSlider, final ValueSpinner vSizeSpinner, final RadioButton pctRB, final RadioButton pxRB) {
		// Connect a listener to the default checkbox.
		useDefaultCB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this checkbox is being checked, adjust the column
				// widths accordingly and enable/disable the sizer
				// widgets as appropriate.
				if (useDefaultCB.getValue()) {
					ColumnWidth defaultCW = m_defaultColumnWidths.get(cName);
					if (null == defaultCW) {
						defaultCW = m_defaultColumnWidth;
					}
					adjustColumnWidth(cName, vSizeSlider, vSizeSpinner, defaultCW);
				}
				
				else {
					adjustColumnWidth(
						cName,
						vSizeSlider,
						vSizeSpinner,
						new ColumnWidth(
							vSizeSpinner.getSpinner().getValue(),
							(pctRB.getValue() ?
								Unit.PCT      :
								Unit.PX)));
				}
			}
		});
		
		// Connect a listener to the size slider.
		vSizeSlider.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Adjust the column width with the value from the
				// slider...
				adjustColumnWidth(
					cName,
					vSizeSlider,
					vSizeSpinner,
					new ColumnWidth(
						event.getValue(),
						(pctRB.getValue() ?
							Unit.PCT      :
							Unit.PX)));
				
				// ...and make sure the checkbox is unchecked.
				useDefaultCB.setValue(false);
			}
		});
		
		// Connect a listener to the size spinner.
		vSizeSpinner.getSpinner().addSpinnerListener(new SpinnerListener() {
			@Override
			public void onSpinning(double value) {
				// Adjust the column width with the value from the
				// spinner...
				adjustColumnWidth(
					cName,
					vSizeSlider,
					vSizeSpinner,
					new ColumnWidth(
						value,
						(pctRB.getValue() ?
							Unit.PCT      :
							Unit.PX)));
				
				// ...and make sure the checkbox is unchecked.
				useDefaultCB.setValue(false);
			}
		});

		// Connect a listener to the '%' radio button.
		pctRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pctRB.getValue()) {
					// ...adjust the column widths accordingly...
					adjustColumnWidthByUnits(cName, vSizeSlider, vSizeSpinner, Unit.PCT);
				}
				
				// ...and make sure the checkbox is unchecked.
				useDefaultCB.setValue(false);
			}
		});

		// Connect a listener to the 'px' radio button.
		pxRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pxRB.getValue()) {
					// ...adjust the column widths accordingly...
					adjustColumnWidthByUnits(cName, vSizeSlider, vSizeSpinner, Unit.PX);
					
					// ...and make sure the checkbox is unchecked.
					useDefaultCB.setValue(false);

					// Do we need to warn the user about mixing pixel
					// widths with percentage widths?
					if (m_warnOnUnitMix && ColumnWidth.hasPCTWidths(m_fcList, m_columnWidths, m_defaultColumnWidth)) {
						// Yes!  Warn them.
						m_warnOnUnitMix = false;
						Window.alert(m_messages.sizeColumnsDlgWarnPercents());
					}
				}
			}
		});
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		FlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-sizeColumnsDlg-content");
		
		Label sliderHint = new Label(m_messages.sizeColumnsDlgSliderHint());
		sliderHint.addStyleName("vibe-sizeColumnsDlg-sizeSliderHint");
		fp.add(sliderHint);
		
		// Create a panel to hold the dialog's content...
		m_sp = new ScrollPanel();
		m_sp.addStyleName("vibe-sizeColumnsDlg-scrollPanel");
		fp.add(m_sp);
		
		// ...and return the Panel that holds the dialog's contents.
		return fp;
	}

	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		// Restore the column widths to what we were initially given...
		m_columnWidths.clear();
		for (String cName:  m_initialColumnWidths.keySet()) {
			m_columnWidths.put(cName, m_initialColumnWidths.get(cName));
		}

		// ...restore the widths in the data table as well...
		m_acw.applyColumnWidths(m_fcList, m_columnWidths, m_defaultColumnWidth);
		
		// ...and return true to close the dialog.
		return true;
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Start saving the contents of the dialog and return false.
		// We'll keep the dialog open until the save is successful, at
		// which point, we'll close it. 
		persistColumnWidthsAsync();
		return false;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Returns the maximum width constraint for a given unit of
	 * measure.
	 */
	private int getUnitMax(Unit units) {
		int reply;
		if (Unit.PCT == units)
		     reply = 100;						// Maximum percentage:  100%.
		else reply = Window.getClientWidth();	// Maximum pixels:      Width of the window.
		return reply;
	}
	
	/*
	 * Asynchronously saves the contents of the dialog.
	 */
	private void persistColumnWidthsAsync() {
		ScheduledCommand doPersist = new ScheduledCommand() {
			@Override
			public void execute() {
				persistColumnWidthsNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPersist);
	}
	
	/*
	 * Synchronously saves the contents of the dialog.
	 */
	private void persistColumnWidthsNow() {
		// Create a save command with the contents of the dialog.
		Map<String, String> saveableColumnWidths = new HashMap<String, String>();
		for (String cName:  m_columnWidths.keySet()) {
			ColumnWidth cw         = m_columnWidths.get(      cName);
			ColumnWidth defaultCW = m_defaultColumnWidths.get(cName);
			if (!(cw.equals(defaultCW))) {
				saveableColumnWidths.put(cName, cw.getWidthStyle());
			}
		}
		SaveColumnWidthsCmd saveCmd = new SaveColumnWidthsCmd(
			m_folderInfo,
			(saveableColumnWidths.isEmpty() ?
				null                        :
				saveableColumnWidths));

		// Can we perform the save?
		GwtClientHelper.executeCommand(
				saveCmd,
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveColumnWidths());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes, the save was successful.  Simply close the
				// dialog.
				hide();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Reset the contents of the scroll panel...
		m_sp.clear();

		// ...create a table to hold the sizing widgets...
		m_dataTable = new VibeFlexTable();
		m_dataTable.addStyleName("vibe-sizeColumnsDlg-dataTable");
		m_dataTable.setCellPadding(0);
		m_dataTable.setCellSpacing(0);
		m_sp.add(m_dataTable);

		// ...get the formatters for laying out the table...
		m_dataTableCF = m_dataTable.getFlexCellFormatter();
		m_dataTableRF = m_dataTable.getRowFormatter();

		// ...render the table's header...
		renderHeaderRow();
		
		// ...render the rows for the column data...
		int rows = 0;
		for (FolderColumn fc:  m_fcList) {
			renderColumnRow(
				fc,
				++rows,
				("vibe-sizeColumnsDlg-" + ((0 == (rows % 2)) ?
					"row-even"                               :
					"row-odd")));
		}
		
		// ...and adjust the scroll panel's styles as appropriate.
		if (5 < rows)
		     m_sp.addStyleName(   "vibe-sizeColumnsDlg-scrollLimit");
		else m_sp.removeStyleName("vibe-sizeColumnsDlg-scrollLimit");
		
		// Finally, show the dialog, positioned so that column sizing
		// adjustments can be seen in the window below the dialog.
		setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// Calculate the left...
				int wDiff = (Window.getClientWidth() - offsetWidth);
				int left  = ((0 >= wDiff) ? 0 : (wDiff / 2));

				// ...and top dialog positions...
				int top = (m_dtAbsTop + PADDING_FOR_DLG_HEADER + (2 * DATA_TABLE_ROW_HEIGHT));
				
				// ...and put the dialog there.
				setPopupPosition(left, top);
			}
		});
	}
	
	/*
	 * Renders a column sizer row into data table.
	 */
	private void renderColumnRow(FolderColumn fc, int rowIndex, String rowStyle) {
		// Create the 'use default' checkbox for the column.
		String      cName     = fc.getColumnName();
		ColumnWidth cw        = m_columnWidths.get(       cName);
		ColumnWidth defaultCW = m_defaultColumnWidths.get(cName);
		boolean isDefault = ((null == cw) || (cw.equals(defaultCW)));	// No column width or the default for the column.
		CheckBox useDefaultCB = new CheckBox();
		useDefaultCB.setValue(isDefault);
		m_dataTable.setWidget(     rowIndex, COL_DEFAULT_CB, useDefaultCB);
		m_dataTableCF.addStyleName(rowIndex, COL_DEFAULT_CB, "vibe-sizeColumnsDlg-rowCell vibe-sizeColumnsDlg-rowCellCB");
		
		// Create a caption for the column.
		Widget captionWidget;
		if (cName.equals(FolderColumn.COLUMN_PIN)) {
			Image columnCaption = new Image(m_images.grayPin());
			columnCaption.addStyleName("vibe-sizeColumnsDlg-colCaptionImg");
			columnCaption.setTitle(m_messages.vibeDataTable_Alt_PinHeader());
			captionWidget = columnCaption;
		}
		else {
			DlgLabel columnCaption = new DlgLabel(fc.getColumnTitle());
			columnCaption.addStyleName("vibe-sizeColumnsDlg-colCaptionTxt");
			captionWidget = columnCaption;
		}
		m_dataTable.setWidget(     rowIndex, COL_COLUMN, captionWidget);
		m_dataTableCF.addStyleName(rowIndex, COL_COLUMN, "vibe-sizeColumnsDlg-rowCell");

		// Create a resizing slider for the column.
		if (null == cw) {
			cw = m_defaultColumnWidth;
		}
		int unitMax = getUnitMax(cw.getUnits());
		SliderBar vSizeSlider = new SliderBar(0, unitMax);
		vSizeSlider.addStyleName("vibe-sizeColumnsDlg-sizeSlider");
		vSizeSlider.setStepSize(1);
		vSizeSlider.setCurrentValue(cw.getWidth());
		m_dataTable.setWidget(     rowIndex, COL_RESIZE, vSizeSlider);
		m_dataTableCF.addStyleName(rowIndex, COL_RESIZE, "vibe-sizeColumnsDlg-rowCell");

		// Create the widgets for specifying a fixed width for the
		// column...
		ValueSpinner vSizeSpinner = new ValueSpinner(cw.getWidth(), 0, unitMax);
		vSizeSpinner.addStyleName("vibe-sizeColumnsDlg-sizeSpinner");
		m_dataTable.setWidget(     rowIndex, COL_SIZE, vSizeSpinner);
		m_dataTableCF.addStyleName(rowIndex, COL_SIZE, "vibe-sizeColumnsDlg-rowCell");
		vSizeSpinner.getTextBox().setVisibleLength(5);
		
		// ...that can either be a pixel value...
		HorizontalPanel sizerPanel = new VibeHorizontalPanel();
		sizerPanel.addStyleName("vibe-sizeColumnsDlg-sizePanel");
		m_dataTable.setWidget(     rowIndex, COL_UNIT, sizerPanel);
		m_dataTableCF.addStyleName(rowIndex, COL_UNIT, "vibe-sizeColumnsDlg-rowCell");
		String unitRBGroup = (cName + "_units");
		RadioButton pxRB  = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPixelRB()  );
		pxRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pxRB);
		pxRB.setWordWrap(false);
		pxRB.setValue(Unit.PX == cw.getUnits());
		
		// ...or a percentage.
		RadioButton pctRB = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPercentRB());
		pctRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pctRB);
		pctRB.setWordWrap(false);
		pctRB.setValue(Unit.PCT == cw.getUnits());

		// Finally, add the style (odd vs. even) to the row...
		m_dataTableRF.setStyleName(rowIndex, rowStyle);
		
		// ...and connect the appropriate column listeners.
		connectColumnListeners(cName, useDefaultCB, vSizeSlider, vSizeSpinner, pctRB, pxRB);
	}

	/*
	 * Renders the header row into the data table.
	 */
	private void renderHeaderRow() {
		// Define the column headers for the data table...
		DlgLabel il = new DlgLabel(m_messages.sizeColumnsDlgColColumn());
		il.addStyleName("vibe-sizeColumnsDlg-headerCell");
		m_dataTable.setWidget(     0, COL_COLUMN, il);
		m_dataTableCF.addStyleName(0, COL_COLUMN, "paddingRight8px");
		
		il = new DlgLabel(m_messages.sizeColumnsDlgColDefault());
		il.addStyleName("vibe-sizeColumnsDlg-headerCell");
		m_dataTable.setWidget(     0, COL_DEFAULT_CB, il);
		m_dataTableCF.addStyleName(0, COL_DEFAULT_CB, "paddingRight8px");
		
		il = new DlgLabel(m_messages.sizeColumnsDlgColResize());
		il.addStyleName("vibe-sizeColumnsDlg-headerCell");
		m_dataTable.setWidget(     0, COL_RESIZE, il);
		m_dataTableCF.addStyleName(0, COL_RESIZE, "paddingRight8px");
		
		il = new DlgLabel(m_messages.sizeColumnsDlgColSize());
		il.addStyleName("vibe-sizeColumnsDlg-headerCell");
		m_dataTable.setWidget(     0, COL_SIZE, il);
		m_dataTableCF.addStyleName(0, COL_SIZE, "paddingRight8px");
		
		il = new DlgLabel(m_messages.sizeColumnsDlgColUnit());
		il.addStyleName("vibe-sizeColumnsDlg-headerCell");
		m_dataTable.setWidget(0, COL_UNIT, il);

		// ...and set the header row's style.
		m_dataTableRF.setStyleName(0, "vibe-sizeColumnsDlg-headerRow");
	}
	
	/*
	 * Asynchronously runs the given instance of the size columns
	 * dialog.
	 */
	private static void runDlgAsync(final SizeColumnsDlg cbDlg, final BinderInfo fi, final List<FolderColumn> fcList, final Map<String, ColumnWidth> columnWidths, final ColumnWidth defaultColumnWidth, final Map<String, ColumnWidth> defaultColumnWidths, final ApplyColumnWidths acw, final int dtAbsTop, final boolean fixedLayout) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cbDlg.runDlgNow(
					fi,
					fcList,
					columnWidths,
					defaultColumnWidth,
					defaultColumnWidths,
					acw,
					dtAbsTop,
					fixedLayout);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the size columns
	 * dialog.
	 */
	private void runDlgNow(BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth, Map<String, ColumnWidth> defaultColumnWidths, ApplyColumnWidths acw, int dtAbsTop, boolean fixedLayout) {
		// Store the parameters...
		m_folderInfo          = fi;
		m_fcList              = fcList;
		m_columnWidths        = columnWidths;
		m_defaultColumnWidth  = defaultColumnWidth;
		m_defaultColumnWidths = defaultColumnWidths;
		m_acw                 = acw;
		m_dtAbsTop            = dtAbsTop;
		
		// ...initialize any other data members...
		m_warnOnUnitMix = (!fixedLayout);	// We need to warn the user about mixing %/pixel widths when not using a fixed table layout.
		
		// ...clone the column widths so we can restore them if the
		// ...dialog is canceled...
		m_initialColumnWidths = ColumnWidth.copyColumnWidths(m_columnWidths);

		// ...and populate dialog.
		populateDlgAsync();
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the size columns dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the size columns dialog
	 * asynchronously after it loads. 
	 */
	public interface SizeColumnsDlgClient {
		void onSuccess(SizeColumnsDlg scDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the SizeColumnsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final SizeColumnsDlgClient scDlgClient,
			
			// initAndShow parameters,
			final SizeColumnsDlg			scDlg,
			final BinderInfo				fi,
			final List<FolderColumn>		fcList,
			final Map<String, ColumnWidth>	columnWidths,
			final ColumnWidth				defaultColumnWidth,
			final Map<String, ColumnWidth>	defaultColumnWidths,
			final ApplyColumnWidths			acw,
			final int						dtAbsTop,
			final boolean					fixedLayout) {
		GWT.runAsync(SizeColumnsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_SizeColumnsDlg());
				if (null != scDlgClient) {
					scDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != scDlgClient) {
					// Yes!  Create it and return it via the callback.
					SizeColumnsDlg cbDlg = new SizeColumnsDlg();
					scDlgClient.onSuccess(cbDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(
						scDlg,
						fi,
						fcList,
						columnWidths,
						defaultColumnWidth,
						defaultColumnWidths,
						acw,
						dtAbsTop,
						fixedLayout);
				}
			}
		});
	}
	
	/**
	 * Loads the SizeColumnsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cbDlgClient
	 */
	public static void createAsync(SizeColumnsDlgClient cbDlgClient) {
		doAsyncOperation(cbDlgClient, null, null, null, null, null, null, null, (-1), false);
	}
	
	/**
	 * Initializes and shows the size columns dialog.
	 * 
	 * @param cbDlg
	 * @param fi
	 * @param fcList
	 * @param columnWidths
	 * @param defaultColumnWidth
	 * @param defaultColumnWidths
	 * @param acw
	 * @param dtAbsTop
	 * @param fixedLayout
	 */
	public static void initAndShow(SizeColumnsDlg cbDlg, BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth, Map<String, ColumnWidth> defaultColumnWidths, ApplyColumnWidths acw, int dtAbsTop, boolean fixedLayout) {
		doAsyncOperation(
			null,
			cbDlg,
			fi,
			fcList,
			columnWidths,
			defaultColumnWidth,
			defaultColumnWidths,
			acw,
			dtAbsTop,
			fixedLayout);
	}
}
