/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.widgets.Spinner;
import org.kablink.teaming.gwt.client.widgets.SpinnerListener;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Image;
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
	private AbstractCellTable<FolderRow>	m_dt;					// The data table containing the columns.
	private BinderInfo						m_folderInfo;			// The folder the dialog is running against.
	private boolean							m_warnOnUnitMix;		// true -> Warn the user about mixing %/pixel values.  false -> Don't.
	private ColumnWidth						m_defaultColumnWidth;	// The default column width for the data table.
	private GwtTeamingDataTableImageBundle	m_images;				// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;				// Access to Vibe's messages.
	private List<FolderColumn> 				m_fcList;				// The columns the dialog is sizing.
	private Map<String, ColumnWidth>		m_columnWidths;			// The current widths of the columns.
	private Map<String, ColumnWidth>		m_defaultColumnWidths;	// The default widths of the columns in the data table.
	private Map<String, ColumnWidth>		m_initialColumnWidths;	// The widths of the columns passed in when the dialog was invoked.
	private ScrollPanel						m_sp;					// ScrollPanel with the dialog's contents.

	// The following defines the amount of padded that will be inserted
	// between the bottom of the dialog and the top of the data table.
	private final static int PADDING_BEWTEEN_DLG_AND_DT	= 20;

	// The following define the row indexes into the FlexTable used to
	// edit the size of an individual column.
	private final static int ROW_CAPTION	= 0;
	private final static int ROW_DEFAULT_RB	= 1;
	private final static int ROW_FIXED_RB	= 2;
	private final static int ROW_SIZER		= 3;
	
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
	private void adjustColumnWidth(String cName, Column<FolderRow, ?> column, ColumnWidth cw) {
		// Adjust the table to show the width...
		m_dt.setColumnWidth(column, ColumnWidth.getWidthStyle(cw));
		
		// ...and persist the ColumnWidth value.
		if (null == cw)
		     m_columnWidths.remove(cName);
		else m_columnWidths.put(cName, cw);
	}
	
	private void adjustColumnWidth(String cName, ColumnWidth cw) {
		// Always use the initial form of the method.
		adjustColumnWidth(cName, m_dt.getColumn(getColumnIndex(cName)), cw);
	}

	/*
	 * Changes a column width based on a unit change.
	 */
	private void adjustColumnWidthByUnits(String cName, ValueSpinner vUnitSpinner, Unit units) {
		Spinner unitSpinner = vUnitSpinner.getSpinner();
		double value = unitSpinner.getValue();
		int max = getUnitMax(units);
		unitSpinner.setMax(max);
		if (max < value) {
			vUnitSpinner.getTextBox().setValue(String.valueOf(max));
			unitSpinner.setValue(max, false);
			value = max;
		}
		adjustColumnWidth(cName, new ColumnWidth(value, units));
	}

	/*
	 * Connect the appropriate column listeners to the various sizing
	 * widgets for a column.
	 */
	private void connectColumnListeners(final String cName, final RadioButton flowRB, final RadioButton fixedRB, final ValueSpinner vSizeSpinner, final RadioButton pctRB, final RadioButton pxRB) {
		// Connect a listener to the flow radio button.
		flowRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (flowRB.getValue()) {
					// ...adjust the column widths accordingly and
					// ...disable the sizer widgets.
					ColumnWidth defaultCW = m_defaultColumnWidths.get(cName);
					if (null == defaultCW) {
						defaultCW = m_defaultColumnWidth;
					}
					adjustColumnWidth(cName, defaultCW);
					setSizeWidgetsEnabled(false, vSizeSpinner, pctRB, pxRB);
				}
			}
		});
		
		// Connect a listener to the fixed radio button.
		fixedRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (fixedRB.getValue()) {
					// ...adjust the column widths accordingly and
					// ...enable the sizer widgets.
					adjustColumnWidth(cName, new ColumnWidth(vSizeSpinner.getSpinner().getValue(), (pctRB.getValue() ? Unit.PCT : Unit.PX)));
					setSizeWidgetsEnabled(true, vSizeSpinner, pctRB, pxRB);
				}
			}
		});

		// Connect a listener to the size spinner.
		vSizeSpinner.getSpinner().addSpinnerListener(new SpinnerListener() {
			@Override
			public void onSpinning(double value) {
				// Adjust the column width with the value from the
				// spinner.
				adjustColumnWidth(cName, new ColumnWidth(value, (pctRB.getValue() ? Unit.PCT : Unit.PX)));
			}
		});

		// Connect a listener to the '%' radio button.
		pctRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pctRB.getValue()) {
					// ...adjust the column widths accordingly.
					adjustColumnWidthByUnits(cName, vSizeSpinner, Unit.PCT);
				}
			}
		});

		// Connect a listener to the 'px' radio button.
		pxRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pxRB.getValue()) {
					// ...adjust the column widths accordingly.
					adjustColumnWidthByUnits(cName, vSizeSpinner, Unit.PX);

					// Do we need to warn the user about mixing pixel
					// widths with percentage widths?
					if (m_warnOnUnitMix && ColumnWidth.hasPercentWidths(m_columnWidths)) {
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
		// Create a panel to hold the dialog's content...
		m_sp = new ScrollPanel();
		m_sp.addStyleName("vibe-sizeColumnsDlg-scrollPanel");
		
		// ...and return the Panel that holds the dialog's contents.
		return m_sp;
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
	public boolean editCanceled() {
		// Restore the column widths to what we were initially given...
		m_columnWidths.clear();
		for (String cName:  m_initialColumnWidths.keySet()) {
			m_columnWidths.put(cName, m_initialColumnWidths.get(cName));
		}

		// ...restore the widths in the data table as well...
		for (FolderColumn fc:  m_fcList) {
			String cName = fc.getColumnName();
			ColumnWidth cw = m_initialColumnWidths.get(cName);
			if (null == cw) {
				cw = m_defaultColumnWidths.get(cName);
				if (null == cw) {
					cw = m_defaultColumnWidth;
				}
			}
			Column<FolderRow, ?> column = m_dt.getColumn(getColumnIndex(cName));
			m_dt.setColumnWidth(column, ColumnWidth.getWidthStyle(cw));
		}
		
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
	public boolean editSuccessful(Object callbackData) {
		// Start saving the contents of the dialog and return false.
		// We'll keep the dialog open until the save is successful, at
		// which point, we'll close it. 
		persistColumnWidthsAsync();
		return false;
	}

	/*
	 * Returns the index of a named FolderColumn from the global
	 * List<FolderColumn>.
	 */
	private int getColumnIndex(String cName) {
		// Scan the List<FolderColumn>...
		int reply = 0;
		for (FolderColumn fc:  m_fcList) {
			// ...is this the column in question?
			if (fc.getColumnName().equals(cName)) {
				// Yes!  Return its index.
				return reply;
			}
			reply += 1;
		}
		
		// If we get here, we couldn't find the column in question. 
		// Return -1.
		return (-1);
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
			m_folderInfo.getBinderIdAsLong(),
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
		HorizontalPanel hp = new VibeHorizontalPanel();
		hp.addStyleName("vibe-sizeColumnsDlg-horizPanel");
		m_sp.add(hp);

		// ...render the column data...
		int cols = 0;
		for (FolderColumn fc:  m_fcList) {
			renderColumn(hp, fc, ((0 == (cols++ % 2)) ? "vibe-sizeColumnsDlg-cell-even" : "cell-odd"));
		}
		
		// ...and adjust the scroll panel's styles as appropriate.
		if (4 < cols)
		     m_sp.addStyleName(   "vibe-sizeColumnsDlg-scrollLimit");
		else m_sp.removeStyleName("vibe-sizeColumnsDlg-scrollLimit");
		
		// Finally, show the dialog, positioned so that column sizing
		// adjustments can be seen in the window below the dialog.
		setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				// Calculate the left...
				int wDiff = (Window.getClientWidth() - offsetWidth);
				int left  = ((0 >= wDiff) ? 0 : (wDiff / 2));

				// ...and top dialog positions...
				int hDiff = (m_dt.getAbsoluteTop() - (offsetHeight + PADDING_BEWTEEN_DLG_AND_DT));
				int top   = ((0 >= hDiff) ? 0 : hDiff);
				
				// ...and put the dialog there.
				setPopupPosition(left, top);
			}
		});
	}
	
	/*
	 * Renders a column sizer into a HorizontalPanel.
	 */
	private void renderColumn(HorizontalPanel hp, FolderColumn fc, String cellStyle) {
		// Create a FlexTable to hold the widgets to size this column.
		FlexTable ft = new FlexTable();
		ft.addStyleName("vibe-sizeColumnsDlg-colPanel " + cellStyle);
		hp.add(ft);
		FlexCellFormatter ftFmt = ft.getFlexCellFormatter();

		// Create a caption for this column's sizer.
		Widget captionWidget;
		String cName = fc.getColumnName();
		if (cName.equals(ColumnWidth.COLUMN_PIN)) {
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
		ft.setWidget(    ROW_CAPTION, 0, captionWidget);
		ftFmt.setColSpan(ROW_CAPTION, 0, 4            );
		
		// Create a radio button to allow this column's width to flow.
		ColumnWidth cw        = m_columnWidths.get(       cName);
		ColumnWidth defaultCW = m_defaultColumnWidths.get(cName);
		boolean isFlow = ((null == cw) || (cw.equals(defaultCW)));	// No column width or the default for the column -> Flow.
		if (isFlow) {
			cw = defaultCW;
			if (null == defaultCW) {
				cw = m_defaultColumnWidth;
				if (null == cw) {
					cw = new ColumnWidth(
						1,
						(ColumnWidth.hasPercentWidths(m_columnWidths) ?
							Unit.PCT                                  :
							Unit.PX));
				}
			}
		}
		String rbGroup = (cName + "_value");
		RadioButton flowRB = new RadioButton(rbGroup);
		flowRB.addStyleName("vibe-sizeColumnsDlg-radio");
		ft.setWidget(ROW_DEFAULT_RB, 0, flowRB);
		flowRB.setValue(isFlow);
		if (null == defaultCW) {
			defaultCW = m_defaultColumnWidth;
		}
		String flowText = ((null == defaultCW) ? m_messages.sizeColumnsDlgFlowRB() : m_messages.sizeColumnsDlgDefaultRB(defaultCW.getWidth() + defaultCW.getUnits().getType()));
		DlgLabel flowLabel = new DlgLabel(flowText);
		flowLabel.addStyleName("vibe-sizeColumnsDlg-radioLabel");
		ft.setWidget(    ROW_DEFAULT_RB, 1, flowLabel);
		ftFmt.setColSpan(ROW_DEFAULT_RB, 1, 3        );

		// Create a radio button to specify a fixed width for this
		// column.
		RadioButton fixedRB = new RadioButton(rbGroup);
		fixedRB.addStyleName("vibe-sizeColumnsDlg-radio");
		ft.setWidget(ROW_FIXED_RB, 0, fixedRB);
		fixedRB.setValue(!isFlow);
		
		DlgLabel fixedLabel = new DlgLabel(m_messages.sizeColumnsDlgFixedRB());
		fixedLabel.addStyleName("vibe-sizeColumnsDlg-radioLabel");
		ft.setWidget(    ROW_FIXED_RB, 1, fixedLabel);
		ftFmt.setColSpan(ROW_FIXED_RB, 1, 3         );

		// Create the widgets for specifying a fixed width for a
		// column...
		ValueSpinner vSizeSpinner = new ValueSpinner(cw.getWidth(), 0, getUnitMax(cw.getUnits()));
		vSizeSpinner.addStyleName("vibe-sizeColumnsDlg-sizeSpinner");
		ft.setWidget(ROW_SIZER, 1, vSizeSpinner);
		vSizeSpinner.getTextBox().setVisibleLength(5);

		// ...that can either be a percentage...
		VerticalPanel sizerPanel = new VibeVerticalPanel();
		sizerPanel.addStyleName("vibe-sizeColumnsDlg-sizePanel");
		ft.setWidget(ROW_SIZER, 2, sizerPanel);
		String unitRBGroup = (cName + "_units");
		RadioButton pctRB = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPercentRB());
		pctRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pctRB);
		pctRB.setWordWrap(false);
		pctRB.setValue(Unit.PCT == cw.getUnits());

		// ...or a pixel value.
		RadioButton pxRB  = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPixelRB()  );
		pxRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pxRB);
		pxRB.setWordWrap(false);
		pxRB.setValue(Unit.PX == cw.getUnits());

		// Finally, in flow mode... 
		if (isFlow) {
			// ...disable the sizer widgets...
			setSizeWidgetsEnabled(false, vSizeSpinner, pctRB, pxRB);
		}
		// ...and connect the appropriate column listeners.
		connectColumnListeners(cName, flowRB, fixedRB, vSizeSpinner, pctRB, pxRB);
	}
	
	/*
	 * Asynchronously runs the given instance of the size columns
	 * dialog.
	 */
	private static void runDlgAsync(final SizeColumnsDlg cbDlg, final BinderInfo fi, final List<FolderColumn> fcList, final Map<String, ColumnWidth> columnWidths, final ColumnWidth defaultColumnWidth, final Map<String, ColumnWidth> defaultColumnWidths, final AbstractCellTable<FolderRow> dt, final boolean fixedLayout) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cbDlg.runDlgNow(
					fi,
					fcList,
					columnWidths,
					defaultColumnWidth,
					defaultColumnWidths,
					dt,
					fixedLayout);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the size columns
	 * dialog.
	 */
	private void runDlgNow(BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth, Map<String, ColumnWidth> defaultColumnWidths, AbstractCellTable<FolderRow> dt, boolean fixedLayout) {
		// Store the parameters...
		m_folderInfo          = fi;
		m_fcList              = fcList;
		m_columnWidths        = columnWidths;
		m_defaultColumnWidth  = defaultColumnWidth;
		m_defaultColumnWidths = defaultColumnWidths;
		m_dt                  = dt;
		
		// ...initialize any other data members...
		m_warnOnUnitMix = (!fixedLayout);	// We need to warn the user about mixing %/pixel widths when not using a fixed table layout.
		
		// ...clone the column widths so we can restore them if the
		// ...dialog is canceled...
		m_initialColumnWidths = ColumnWidth.copyColumnWidths(m_columnWidths);

		// ...and populate dialog.
		populateDlgAsync();
	}

	/*
	 * Enabled/disables the sizer widgets.
	 */
	private void setSizeWidgetsEnabled(boolean enabled, ValueSpinner vSizeSpinner, RadioButton pctRB, RadioButton pxRB) {
		vSizeSpinner.setEnabled(enabled);
		pctRB.setEnabled(       enabled);
		pxRB.setEnabled(        enabled);
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
			final SizeColumnsDlg					scDlg,
			final BinderInfo						fi,
			final List<FolderColumn>				fcList,
			final Map<String, ColumnWidth>			columnWidths,
			final ColumnWidth						defaultColumnWidth,
			final Map<String, ColumnWidth>			defaultColumnWidths,
			final AbstractCellTable<FolderRow>		dt,
			final boolean							fixedLayout) {
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
						dt,
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
		doAsyncOperation(cbDlgClient, null, null, null, null, null, null, null, false);
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
	 * @param dt
	 * @param fixedLayout
	 */
	public static void initAndShow(SizeColumnsDlg cbDlg, BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth, Map<String, ColumnWidth> defaultColumnWidths, AbstractCellTable<FolderRow> dt, boolean fixedLayout) {
		doAsyncOperation(
			null,
			cbDlg,
			fi,
			fcList,
			columnWidths,
			defaultColumnWidth,
			defaultColumnWidths,
			dt,
			fixedLayout);
	}
}
