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

import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
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
	private AbstractCellTable<FolderRow>	m_dt;			// The data table containing the columns.
	private BinderInfo						m_folderInfo;	// The folder the dialog is running against.
	private GwtTeamingMainMenuImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;		// Access to Vibe's messages.
	private List<FolderColumn> 				m_fcList;		// The columns the dialog is sizing.
	private Map<String, ColumnWidth>		m_columnWidths;	// The current widths of the columns.
	private ScrollPanel						m_sp;			// ScrollPanel with the dialog's contents.

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
		m_images   = GwtTeaming.getMainMenuImageBundle();
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
	 * structure used to persist the changes.
	 */
	private void adjustColumnWidth(Column<FolderRow, ?> column, double value, Unit units) {
		// Adjust the table to show the width...
		m_dt.setColumnWidth(column, value, units);
		
//!		...this needs to be implemented...
		// ...and persist the ColumnWidth value.
	}
	
	private void adjustColumnWidth(String cName, double value, Unit units) {
		// Always use the initial form of the method.
		adjustColumnWidth(m_dt.getColumn(getColumnIndex(cName)), value, units);
	}

	/*
	 * Changes a column width based on a unit change.
	 */
	private void adjustColumnWidthByUnits(String cName, Spinner unitSpinner, Unit units) {
		double value = unitSpinner.getValue();
		int max = getWidthMax(units);
		unitSpinner.setMax(max);
		if (max > value) {
			unitSpinner.setValue(max, false);
			value = max;
		}
		adjustColumnWidth(cName, value, units);
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
		// Simply return true to allow the dialog to close.
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
		// Unused.
		return true;
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
	
	/*
	 * Returns the maximum width constraint for a given unit of
	 * measure.
	 */
	private int getWidthMax(Unit units) {
		int reply;
		if (Unit.PCT == units)
		     reply = 100;
		else reply = 1000;
		return reply;
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
		DlgLabel columnCaption = new DlgLabel(fc.getColumnTitle());
		columnCaption.addStyleName("vibe-sizeColumnsDlg-colCaption");
		ft.setWidget(    ROW_CAPTION, 0, columnCaption);
		ftFmt.setColSpan(ROW_CAPTION, 0, 4            );
		
		// Create a radio button to allow this column's width to flow.
		final String cName = fc.getColumnName();
		ColumnWidth cw = m_columnWidths.get(cName);
		boolean isFlow = (null == cw);	// No column width -> Flow.
		String rbGroup = (cName + "_value");
		final RadioButton flowRB = new RadioButton(rbGroup);
		flowRB.addStyleName("vibe-sizeColumnsDlg-radio");
		ft.setWidget(ROW_DEFAULT_RB, 0, flowRB);
		flowRB.setValue(isFlow);
		flowRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (flowRB.getValue()) {
					// ...adjust the column widths accordingly.
//!					...this needs to be implemented...
				}
			}
		});
		
		DlgLabel flowLabel = new DlgLabel(m_messages.sizeColumnsDlgFlowRB());
		flowLabel.addStyleName("vibe-sizeColumnsDlg-radioLabel");
		ft.setWidget(    ROW_DEFAULT_RB, 1, flowLabel);
		ftFmt.setColSpan(ROW_DEFAULT_RB, 1, 3        );

		// Create a radio button to specify a fixed width for this
		// column.
		final RadioButton fixedRB = new RadioButton(rbGroup);
		fixedRB.addStyleName("vibe-sizeColumnsDlg-radio");
		ft.setWidget(ROW_FIXED_RB, 0, fixedRB);
		fixedRB.setValue(!isFlow);	// Checked if column width.
		fixedRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (fixedRB.getValue()) {
					// ...adjust the column widths accordingly.
//!					...this needs to be implemented...
				}
			}
		});
		
		DlgLabel fixedLabel = new DlgLabel(m_messages.sizeColumnsDlgFixedRB());
		fixedLabel.addStyleName("vibe-sizeColumnsDlg-radioLabel");
		ft.setWidget(    ROW_FIXED_RB, 1, fixedLabel);
		ftFmt.setColSpan(ROW_FIXED_RB, 1, 3         );

		// Create the widgets for specifying a fixed width for a
		// column...
		String unitRBGroup = (cName + "_units");
		final RadioButton pctRB = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPercentRB());
		final RadioButton pxRB  = new RadioButton(unitRBGroup, m_messages.sizeColumnsDlgUnitPixelRB()  );
		
		int  size  = (isFlow ? 50      : cw.getWidth());
		Unit units = (isFlow ? Unit.PX : cw.getUnits());
		ValueSpinner vSpinner = new ValueSpinner(size, 0, getWidthMax(units));
		final Spinner spinner = vSpinner.getSpinner();
		vSpinner.addStyleName("vibe-sizeColumnsDlg-sizeSpinner");
		ft.setWidget(ROW_SIZER, 1, vSpinner);
		vSpinner.getTextBox().setVisibleLength(5);
		spinner.addSpinnerListener(new SpinnerListener() {
			@Override
			public void onSpinning(long value) {
				// Adjust the column width with the value from the
				// spinner.
				adjustColumnWidth(cName, value, (pctRB.getValue() ? Unit.PCT : Unit.PX));
			}
		});

		// ...that can either be a percentage...
		VerticalPanel sizerPanel = new VibeVerticalPanel();
		sizerPanel.addStyleName("vibe-sizeColumnsDlg-sizePanel");
		ft.setWidget(ROW_SIZER, 2, sizerPanel);
		pctRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pctRB);
		pctRB.setWordWrap(false);
		pctRB.setValue(Unit.PCT == units);
		pctRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pctRB.getValue()) {
					// ...adjust the column widths accordingly.
					adjustColumnWidthByUnits(cName, spinner, Unit.PCT);
				}
			}
		});

		// ...or a pixel value.
		pxRB.addStyleName("vibe-sizeColumnsDlg-sizeRB");
		sizerPanel.add(pxRB);
		pxRB.setWordWrap(false);
		pxRB.setValue(Unit.PX == units);
		pxRB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If this radio button is being checked...
				if (pxRB.getValue()) {
					// ...adjust the column widths accordingly.
					adjustColumnWidthByUnits(cName, spinner, Unit.PX);
				}
			}
		});
	}
	
	/*
	 * Asynchronously runs the given instance of the size columns
	 * dialog.
	 */
	private static void runDlgAsync(final SizeColumnsDlg cbDlg, final BinderInfo fi, final List<FolderColumn> fcList, final Map<String, ColumnWidth> columnWidths, final AbstractCellTable<FolderRow> dt) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cbDlg.runDlgNow(fi, fcList, columnWidths, dt);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the size columns
	 * dialog.
	 */
	private void runDlgNow(BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, AbstractCellTable<FolderRow> dt) {
		// Store the parameters...
		m_folderInfo   = fi;
		m_fcList       = fcList;
		m_columnWidths = columnWidths;
		m_dt           = dt;

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
			final SizeColumnsDlg					scDlg,
			final BinderInfo						fi,
			final List<FolderColumn>				fcList,
			final Map<String, ColumnWidth>			columnWidths,
			final AbstractCellTable<FolderRow>		dt) {
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
					runDlgAsync(scDlg, fi, fcList, columnWidths, dt);
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
		doAsyncOperation(cbDlgClient, null, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the size columns dialog.
	 * 
	 * @param cbDlg
	 * @param fi
	 * @param fcList
	 * @param dt
	 */
	public static void initAndShow(SizeColumnsDlg cbDlg, BinderInfo fi, List<FolderColumn> fcList, Map<String, ColumnWidth> columnWidths, AbstractCellTable<FolderRow> dt) {
		doAsyncOperation(null, cbDlg, fi, fcList, columnWidths, dt);
	}
}
