/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SetUserSharingRightsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserSharingRightsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UserSharingRightsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.CombinedPerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.ProgressDlg;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Implements the user share rights dialog.
 *  
 * @author drfoster@novell.com
 */
public class UserShareRightsDlg extends DlgBox implements EditSuccessfulHandler {
	private GwtTeamingMessages						m_messages;				// Access to Vibe's messages.
	private InlineLabel								m_progressIndicator;	// Text under the progress bar that displays what's going on.
	private int										m_totalDone;			// Tracks a running count of users whose sharing rights we've set.
	private List<Long>								m_userIds;				// The List<Long> of user IDs whose sharing rights are being set.
	private PerEntityShareRightsInfo				m_singleUserRights;		// If the sharing rights are being set for a single user, this contains their current rights setting when the dialog is invoked. 
	private ProgressBar								m_progressBar;			// Progress bar displayed while saving the share rights.
	private UIObject								m_showRelativeTo;		// UIObject to show the dialog relative to.  null -> Center the dialog.
	private UserSharingRightsInfoRpcResponseData	m_rightsInfo;			// Information about sharing rights available and to be set.
	private VibeFlowPanel							m_progressPanel;		// Panel containing the progress bar.
	private VibeVerticalPanel						m_vp;					// The panel holding the dialog's content.

	// Indexes of the various table columns.
	private final static int COLUMN_HEADER		= 0;
	private final static int COLUMN_ALLOW		= 1;
	private final static int COLUMN_CLEAR		= 2;
	private final static int COLUMN_NO_CHANGE	= 3;
	
	// Indexes of the various table rows.
	private final static int ROW_HEADER_1		= 0;
	private final static int ROW_INTERNAL		= 1;
	private final static int ROW_EXTERNAL		= 2;
	private final static int ROW_PUBLIC			= 3;
	private final static int ROW_PUBLIC_LINKS	= 4;
	private final static int ROW_SPACER			= 5;
	private final static int ROW_HEADER_2		= 6;
	private final static int ROW_FORWARDING		= 7;

	// The following is used to generate the IDs used for the various
	// radio buttons contained in the dialog.
	private final static String RBID_BASE	= "rb";
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserShareRightsDlg() {
		// Initialize the superclass...
		super(true, false);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// The dialog's caption is set when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Adds a column header cell to the table.
	 */
	private void addColumnHeaderCell(FlexTable ft, FlexCellFormatter fcf, int row, int column, String text) {
		InlineLabel	il = new InlineLabel();
		if (null != text) {
			il.getElement().setInnerText(text);
		}
		il.addStyleName(                   "vibe-shareRightsDlg-header"          );
		fcf.addStyleName(     row, column, "vibe-shareRightsDlg-headerCell"      );
		if (COLUMN_HEADER != column)
		     fcf.addStyleName(row, column, "vibe-shareRightsDlg-headerCellCenter");
		else fcf.addStyleName(row, column, "vibe-shareRightsDlg-headerCellLeft"  );
		if (ROW_HEADER_2 == row) {
		     fcf.addStyleName(row, column, "vibe-shareRightsDlg-headerCell2"     );
		}
		else if (ROW_SPACER == row) {
		     fcf.addStyleName(row, column, "vibe-shareRightsDlg-headerCellSpacer");
		}
		ft.setWidget(         row, column, il);
	}
	
	private void addColumnHeaderCell(FlexTable ft, FlexCellFormatter fcf, int row, int column) {
		// Always use the initial form of the method.
		addColumnHeaderCell(ft, fcf, row, column, null);
	}
	
	/*
	 * Adds a rights clear cell to the table.
	 */
	private void addRadioCell(FlexTable ft, FlexCellFormatter fcf, int row, int column, boolean checked) {
		String		rbGroup = getRBGroup(      row   );
		String		rbId    = getRBId(rbGroup, column);
		RadioButton	rb      = new RadioButton(rbGroup);
		rb.getElement().setId(rbId);
		rb.addStyleName(              "vibe-shareRightsDlg-radio"    );
		fcf.addStyleName(row, column, "vibe-shareRightsDlg-radioCell");
		ft.setWidget(    row, column, rb);
		rb.setValue(checked);
	}
	
	/*
	 * Adds a row header cell to the table.
	 */
	private void addRowHeaderCell(FlexTable ft, FlexCellFormatter fcf, int row, int column, String text) {
		InlineLabel il = new InlineLabel(text);
		il.addStyleName(                  "vibe-shareRightsDlg-row"           );
		fcf.addStyleName(    row, column, "vibe-shareRightsDlg-rowCell"       );
		if ((ROW_FORWARDING == row) && (COLUMN_HEADER == column)) {
			fcf.addStyleName(row, column, "vibe-shareRightsDlg-rowCellForward");
		}
		ft.setWidget(        row, column, il);
	}
	
	/*
	 * Returns the string to use in a header cell for a rights
	 * assignment.
	 */
	private String buildHeaderCellString(String baseHeader, boolean isZoneEnabled) {
		String header = baseHeader;
		if (!isZoneEnabled) {
			header = ("* " + header);
		}
		return header;
	}
	
	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage(String style) {
		Image reply = GwtClientHelper.buildImage(GwtTeaming.getImageBundle().spinner16());
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
		}
		return reply;
	}

	/*
	 * Returns a List<Long> clone of the original list.
	 */
	private List<Long> cloneUserIds(List<Long> userIds) {
		List<Long> reply = new ArrayList<Long>();
		for (Long userId:  userIds) {
			reply.add(userId);
		}
		return reply;
	}

	/*
	 * Returns a CombinedPerEntityShareRightsInfo object that reflects
	 * the current selections in the dialog.
	 */
	private CombinedPerEntityShareRightsInfo collectSharingRights() {
		// Create objects for the set and value flags.
		PerEntityShareRightsInfo setFlags   = new PerEntityShareRightsInfo();
		PerEntityShareRightsInfo valueFlags = new PerEntityShareRightsInfo();

		// Initialize the external set and value flags.
		boolean hasUnchanged = (null == m_singleUserRights);
		boolean isRBChecked  = isRBChecked(ROW_EXTERNAL, COLUMN_ALLOW);
		valueFlags.setAllowExternal(isRBChecked);
		if (hasUnchanged)
		     setFlags.setAllowExternal(!(isRBChecked(ROW_EXTERNAL, COLUMN_NO_CHANGE)));
		else setFlags.setAllowExternal(isRBChecked != m_singleUserRights.isAllowExternal());
		
		// Initialize the forwarding set and value flags.
		isRBChecked = isRBChecked(ROW_FORWARDING, COLUMN_ALLOW);
		valueFlags.setAllowForwarding(isRBChecked);
		if (hasUnchanged)
		     setFlags.setAllowForwarding(!(isRBChecked(ROW_FORWARDING, COLUMN_NO_CHANGE)));
		else setFlags.setAllowForwarding(isRBChecked != m_singleUserRights.isAllowForwarding());
		
		// Initialize the internal set and value flags.
		isRBChecked = isRBChecked(ROW_INTERNAL, COLUMN_ALLOW);
		valueFlags.setAllowInternal(isRBChecked);
		if (hasUnchanged)
		     setFlags.setAllowInternal(!(isRBChecked(ROW_INTERNAL, COLUMN_NO_CHANGE)));
		else setFlags.setAllowInternal(isRBChecked != m_singleUserRights.isAllowInternal());
		
		// Initialize the public set and value flags.
		isRBChecked = isRBChecked(ROW_PUBLIC, COLUMN_ALLOW);
		valueFlags.setAllowPublic(isRBChecked);
		if (hasUnchanged)
		     setFlags.setAllowPublic(!(isRBChecked(ROW_PUBLIC, COLUMN_NO_CHANGE)));
		else setFlags.setAllowPublic(isRBChecked != m_singleUserRights.isAllowPublic());

		// Initialize the public links set and value flags.
		isRBChecked = isRBChecked(ROW_PUBLIC_LINKS, COLUMN_ALLOW);
		valueFlags.setAllowPublicLinks(isRBChecked);
		if (hasUnchanged)
		     setFlags.setAllowPublicLinks(!(isRBChecked(ROW_PUBLIC_LINKS, COLUMN_NO_CHANGE)));
		else setFlags.setAllowPublicLinks(isRBChecked != m_singleUserRights.isAllowPublicLinks());

		// Finally, return a CombinderPerEntityShareRightsInfo object
		// with the set and value flags.
		return
			new CombinedPerEntityShareRightsInfo(
				setFlags,
				valueFlags);
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
		// Create and return a panel to hold the dialog's content.
		m_vp = new VibeVerticalPanel(null, null);
		m_vp.addStyleName("vibe-shareRightsDlg-panel");
		return m_vp;
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
		// Collect the sharing rights to be set from the dialog...
		CombinedPerEntityShareRightsInfo sharingRights = collectSharingRights();
		
		// ...and start the operation.
		List<Long>		sourceUserIds   = cloneUserIds(m_userIds);	// We use a clone because we manipulate the list's contents during the operation.
		int				totalUserCount  = sourceUserIds.size();		// Total number of entities that we're starting with.
		List<ErrorInfo>	collectedErrors = new ArrayList<ErrorInfo>();
		
		m_totalDone = 0;
		setOkEnabled(false);
		setUserSharingRightsAsync(
			new SetUserSharingRightsInfoCmd(
				sourceUserIds,
				sharingRights),
			sourceUserIds,
			totalUserCount,
			collectedErrors);
		
		// Return false.  We'll close the dialog manually if/when the
		// operation completes.
		return false;
	}

	/**
	 * Returns the edited List<FavoriteInfo>.
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
	 * Generates the IDs used for the various radio buttons.
	 */
	private String getRBId(String rbGroup, int column) {return (rbGroup   + ":" + column);}
	private String getRBGroup(             int row)    {return (RBID_BASE + ":" + row   );}
	
	/*
	 * Returns true if a radio button is checked and false otherwise.
	 */
	private boolean isRBChecked(int row, int column) {
		String			rbGroup = getRBGroup(      row   );
		String			rbId    = getRBId(rbGroup, column);
		InputElement	rb      = Document.get().getElementById(rbId).getFirstChildElement().cast();
		return rb.isChecked();								
	}
	
	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(new GetUserSharingRightsInfoCmd(m_userIds), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetUserSharingRightsInfo());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it...
				m_rightsInfo = ((UserSharingRightsInfoRpcResponseData) result.getResponseData());
				if (1 == m_userIds.size()) {
					m_singleUserRights = m_rightsInfo.getUserRights(m_userIds.get(0));
					if (null == m_singleUserRights) {
						GwtClientHelper.deferredAlert(m_messages.userShareRightsDlgError_NoWorkspace());
						return;
					}
				}
				
				else {
					m_singleUserRights = null;
				}
				
				// ...and populate the dialog.
				populateDlgAsync();
			}
		});
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
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
		// Clear the panel that holds the content...
		m_vp.clear();

		// ...create the table containing the dialog's content... 
		FlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-shareRightsDlg-table");
		m_vp.add(ft);
		FlexCellFormatter fcf = ft.getFlexCellFormatter();
		ft.setCellPadding(4);
		ft.setCellSpacing(0);

		// ...define the column header cells...
		addColumnHeaderCell(    ft, fcf, ROW_HEADER_1, COLUMN_HEADER,    m_messages.userShareRightsDlgLabel_AllowSharingWith());
		addColumnHeaderCell(    ft, fcf, ROW_HEADER_1, COLUMN_ALLOW,     m_messages.userShareRightsDlgLabel_Allow()           );
		addColumnHeaderCell(    ft, fcf, ROW_HEADER_1, COLUMN_CLEAR,     m_messages.userShareRightsDlgLabel_Clear()           );
		if (null == m_singleUserRights) {
			addColumnHeaderCell(ft, fcf, ROW_HEADER_1, COLUMN_NO_CHANGE, m_messages.userShareRightsDlgLabel_NoChange()        );
		}

		// ...define the share internal cells...
		int noZoneSettings = 0;
		boolean hasZoneSetting = m_rightsInfo.isInternalEnabled(); 
		if (!hasZoneSetting) {
			noZoneSettings += 1;
		}
		addRowHeaderCell(ft, fcf, ROW_INTERNAL, COLUMN_HEADER, buildHeaderCellString(m_messages.userShareRightsDlgLabel_InternalUsers(), hasZoneSetting));
		addRadioCell(    ft, fcf, ROW_INTERNAL, COLUMN_ALLOW, ((null != m_singleUserRights) &&    m_singleUserRights.isAllowInternal()));
		addRadioCell(    ft, fcf, ROW_INTERNAL, COLUMN_CLEAR, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowInternal()))));
		if (null == m_singleUserRights) {
			addRadioCell(ft, fcf, ROW_INTERNAL, COLUMN_NO_CHANGE, true);
		}
		
		// ...define the share external cells...
		hasZoneSetting = m_rightsInfo.isExternalEnabled(); 
		if (!hasZoneSetting) {
			noZoneSettings += 1;
		}
		addRowHeaderCell(ft, fcf, ROW_EXTERNAL, COLUMN_HEADER, buildHeaderCellString(m_messages.userShareRightsDlgLabel_ExternalUsers(), hasZoneSetting));
		addRadioCell(    ft, fcf, ROW_EXTERNAL, COLUMN_ALLOW, ((null != m_singleUserRights) &&    m_singleUserRights.isAllowExternal())  );
		addRadioCell(    ft, fcf, ROW_EXTERNAL, COLUMN_CLEAR, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowExternal()))));
		if (null == m_singleUserRights) {
			addRadioCell(ft, fcf, ROW_EXTERNAL, COLUMN_NO_CHANGE, true);
		}
		
		// ...define the share public cells...
		hasZoneSetting = m_rightsInfo.isPublicEnabled(); 
		if (!hasZoneSetting) {
			noZoneSettings += 1;
		}
		addRowHeaderCell(ft, fcf, ROW_PUBLIC, COLUMN_HEADER, buildHeaderCellString(m_messages.userShareRightsDlgLabel_Public(), hasZoneSetting));
		addRadioCell(    ft, fcf, ROW_PUBLIC, COLUMN_ALLOW, ((null != m_singleUserRights) &&    m_singleUserRights.isAllowPublic())  );
		addRadioCell(    ft, fcf, ROW_PUBLIC, COLUMN_CLEAR, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowPublic()))));
		if (null == m_singleUserRights) {
			addRadioCell(ft, fcf, ROW_PUBLIC, COLUMN_NO_CHANGE, true);
		}

		// ...define the share public links cells...
		hasZoneSetting = m_rightsInfo.isPublicLinksEnabled(); 
		if (!hasZoneSetting) {
			noZoneSettings += 1;
		}
		addRowHeaderCell(ft, fcf, ROW_PUBLIC_LINKS, COLUMN_HEADER, buildHeaderCellString(m_messages.userShareRightsDlgLabel_PublicLinks(), hasZoneSetting));
		addRadioCell(    ft, fcf, ROW_PUBLIC_LINKS, COLUMN_ALLOW, ((null != m_singleUserRights) &&    m_singleUserRights.isAllowPublicLinks())  );
		addRadioCell(    ft, fcf, ROW_PUBLIC_LINKS, COLUMN_CLEAR, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowPublicLinks()))));
		if (null == m_singleUserRights) {
			addRadioCell(ft, fcf, ROW_PUBLIC_LINKS, COLUMN_NO_CHANGE, true);
		}

		// ...define the spacer above the share forwarding cells...
		addColumnHeaderCell(    ft, fcf, ROW_SPACER, COLUMN_HEADER   );
		addColumnHeaderCell(    ft, fcf, ROW_SPACER, COLUMN_ALLOW    );
		addColumnHeaderCell(    ft, fcf, ROW_SPACER, COLUMN_CLEAR    );
		if (null == m_singleUserRights) {
			addColumnHeaderCell(ft, fcf, ROW_SPACER, COLUMN_NO_CHANGE);
		}
	
		// ...define the share forwarding header cells...
		addColumnHeaderCell(    ft, fcf, ROW_HEADER_2, COLUMN_ALLOW,     m_messages.userShareRightsDlgLabel_Allow());
		addColumnHeaderCell(    ft, fcf, ROW_HEADER_2, COLUMN_CLEAR,     m_messages.userShareRightsDlgLabel_Clear());
		if (null == m_singleUserRights) {
			addColumnHeaderCell(ft, fcf, ROW_HEADER_2, COLUMN_NO_CHANGE, m_messages.userShareRightsDlgLabel_NoChange());
		}
		
		// ...define the share forwarding cells...
		hasZoneSetting = m_rightsInfo.isForwardingEnabled(); 
		if (!hasZoneSetting) {
			noZoneSettings += 1;
		}
		addRowHeaderCell(   ft, fcf, ROW_FORWARDING, COLUMN_HEADER, buildHeaderCellString(m_messages.userShareRightsDlgLabel_AllowForwarding(), hasZoneSetting));
		addRadioCell(       ft, fcf, ROW_FORWARDING, COLUMN_ALLOW, ((null != m_singleUserRights) &&    m_singleUserRights.isAllowForwarding())  );
		addRadioCell(       ft, fcf, ROW_FORWARDING, COLUMN_CLEAR, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowForwarding()))));
		if (null == m_singleUserRights) {
			addRadioCell(   ft, fcf, ROW_FORWARDING, COLUMN_NO_CHANGE, true);
		}
		
		// ...if some of the rights do not have settings at the zone
		// ...level...
		if (0 < noZoneSettings) {
			// ...add a note telling the user.
			Label l = new Label("* " + m_messages.userShareRightsDlgLabel_NoZoneSettings());
			l.addStyleName("vibe-shareRightsDlg-noZoneSettings");
			m_vp.add(l);
		}
		
		// ...define a progress bar...
		m_progressBar = new ProgressBar(0, m_userIds.size());
		m_progressBar.addStyleName("vibe-shareRightsDlg-progressBar");
		m_vp.add(m_progressBar);
		m_progressBar.setVisible(false);
		
		m_progressPanel = new VibeFlowPanel();
		m_progressPanel.addStyleName("vibe-shareRightsDlg-progressPanel");
		m_vp.add(m_progressPanel);
		m_progressPanel.setVisible(false);
		m_progressPanel.add(buildSpinnerImage("vibe-shareRightsDlg-progressSpinner"));
		m_progressIndicator = new InlineLabel("");
		m_progressIndicator.addStyleName("vibe-shareRightsDlg-progressLabel");
		m_progressPanel.add(m_progressIndicator);

		// ...and finally, make sure the buttons are enabled and show
		// ...the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		if (null == m_showRelativeTo)
		     center();
		else showRelativeTo(m_showRelativeTo);
	}
	
	/*
	 * Asynchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private static void runDlgAsync(final UserShareRightsDlg usrDlg, final List<Long> userIds, final UIObject showRelativeTo) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				usrDlg.runDlgNow(userIds, showRelativeTo);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private void runDlgNow(List<Long> userIds, UIObject showRelativeTo) {
		// Store the parameter...
		m_userIds        = userIds;
		m_showRelativeTo = showRelativeTo;
		
		// ...update the dialog's caption...
		setCaption(m_messages.userShareRightsDlgHeader(m_userIds.size()));

		// ...and populate the dialog.
		loadPart1Async();
	}
	
	/*
	 * Asynchronously sets the sharing rights on a list of users.
	 */
	private void setUserSharingRightsAsync(
			final SetUserSharingRightsInfoCmd	cmd,
			final List<Long>					sourceUserIds,
			final int							totalUserCount,
			final List<ErrorInfo>				collectedErrors) {
		ScheduledCommand doSetUserSharingRights = new ScheduledCommand() {
			@Override
			public void execute() {
				setUserSharingRightsNow(
					cmd,
					sourceUserIds,
					totalUserCount,
					collectedErrors);
			}
		};
		Scheduler.get().scheduleDeferred(doSetUserSharingRights);
	}
	
	/*
	 * Synchronously sets the sharing rights on a list of users.
	 */
	private void setUserSharingRightsNow(
			final SetUserSharingRightsInfoCmd	cmd,
			final List<Long>					sourceUserIds,
			final int							totalUserCount,
			final List<ErrorInfo>				collectedErrors) {
		// Do we need to send the request to set the sharing rights in
		// chunks?  (We do if we've already been sending chunks or the
		// source list contains more items than our threshold.)
		boolean cmdIsChunkList = (cmd.getUserIds() != sourceUserIds);
		if (cmdIsChunkList || ProgressDlg.needsChunking(sourceUserIds.size())) {
			// Yes!  If we're not showing the progress bar or panel
			// yet...
			if ((!(m_progressPanel.isVisible())) || (!(m_progressBar.isVisible()))) {
				// ...show them now.
				m_progressBar.setVisible(  true);
				m_progressPanel.setVisible(true);
				updateProgress(0, totalUserCount);
			}
			
			// Make sure we're using a separate list for the chunks
			// vs. the source list that we're saving the rights for.
			List<Long> chunkList;
			if (cmdIsChunkList) {
				chunkList = cmd.getUserIds();
				chunkList.clear();
			}
			else {
				chunkList = new ArrayList<Long>();
				cmd.setUserIds(chunkList);
			}
			
			// Scan the user IDs whose sharing rights are to be set...
			while(true) {
				// ...moving each user ID from the source list into
				// ...the chunk list.
				chunkList.add(sourceUserIds.get(0));
				sourceUserIds.remove(0);
				
				// Was that the last user whose rights are to be set?
				if (sourceUserIds.isEmpty()) {
					// Yes!  Break out of the loop and let the chunk
					// get handled as if we weren't sending by chunks.
					break;
				}
				
				// Have we reached the size we chunk things at?
				if (ProgressDlg.isChunkFull(chunkList.size())) {
					// Yes!  Send this chunk.  Note that this is a
					// recursive call and will come back through this
					// method for the next chunk.
					setUserSharingRightsImpl(
						cmd,
						sourceUserIds,
						totalUserCount,
						collectedErrors,
						true);	// true -> This is a one of multiple chunks of users whose rights are to be set.
					
					return;
				}
			}
		}

		// Do we have any user whose sharing rights are to be set?
		if (!(cmd.getUserIds().isEmpty())) {
			// Yes!  Perform the final set.
			setUserSharingRightsImpl(
				cmd,
				sourceUserIds,
				totalUserCount,
				collectedErrors,
				false);	// false -> This is the last set of user whose rights are to be set.
		}
	}

	/*
	 * Sends an RPC request with a set user sharing rights command that
	 * either sets the rights for everything or simply the next chunk
	 * in a sequence of chunks.
	 */
	private void setUserSharingRightsImpl(
			final SetUserSharingRightsInfoCmd	cmd,
			final List<Long>					sourceUserIds,
			final int							totalUserCount,
			final List<ErrorInfo>				collectedErrors,
			final boolean						moreRemaining) {
		// Send the request to set the sharing rights for the users.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SetUserSharingRightsInfo());
			}

			@Override
			public void onSuccess(final VibeRpcResponse response) {
				// Handle the response in a scheduled command so that
				// the AJAX request gets released ASAP.
				ScheduledCommand doHandleUserSharingRights = new ScheduledCommand() {
					@Override
					public void execute() {
						// Did everybody we asked to get their rights
						// set?
						ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
						List<ErrorInfo> chunkErrors = responseData.getErrorList();
						int chunkErrorCount = ((null == chunkErrors) ? 0 : chunkErrors.size());
						if (0 < chunkErrorCount) {
							// No!  Copy the errors into the
							// List<ErrorInfo> that we're collecting
							// them in.
							for (ErrorInfo chunkError:  chunkErrors) {
								collectedErrors.add(chunkError);
							}
						}
		
						// Did we just set a part of the users that we
						// need to set the rights for?
						if (moreRemaining) {
							// Yes!  Clear the user ID list in the
							// command and request that the next chunk
							// be sent.
							updateProgress(cmd.getUserIds().size(), totalUserCount);
							setUserSharingRightsAsync(
								cmd,
								sourceUserIds,
								totalUserCount,
								collectedErrors);
						}
						
						else {
							// No, we didn't just set the rights for
							// part of the users, but all that were
							// remaining!  Did we collect any errors
							// during the process?
							updateProgress(cmd.getUserIds().size(), totalUserCount);
							int totalErrorCount = collectedErrors.size();
							if (0 < totalErrorCount) {
								// Yes!  Tell the user about the
								// problem(s).
								GwtClientHelper.displayMultipleErrors(
									m_messages.userShareRightsDlgError_SetFailures(),
									collectedErrors);
							}
							
							// Finally, close the dialog, we're done!
							hide();
						}
					}
				};
				Scheduler.get().scheduleDeferred(doHandleUserSharingRights);
			}
		});
	}
	
	/*
	 * Called up update the progress indicator in the dialog.
	 */
	private void updateProgress(int justCompleted, int totalUserCount) {
		// If we're done...
		m_totalDone += justCompleted;
		if (m_totalDone == totalUserCount) {
			// ...hide the progress bar and panel.
			m_progressBar.setVisible(  false);
			m_progressPanel.setVisible(false);
		}
		else {
			// ...otherwise, set the number we've completed.
			m_progressBar.setMaxProgress(totalUserCount);
			m_progressBar.setProgress(   m_totalDone   );
			m_progressIndicator.setText(
				m_messages.userShareRightsDlgProgress(
					m_totalDone,
					totalUserCount));
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the user share rights dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the user share rights dialog
	 * asynchronously after it loads. 
	 */
	public interface UserShareRightsDlgClient {
		void onSuccess(UserShareRightsDlg usrDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserShareRightsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters to create an instance of the dialog.
			final UserShareRightsDlgClient usrDlgClient,
			
			// Parameters to initialize and show the dialog.
			final UserShareRightsDlg	usrDlg,
			final List<Long>			userIds,
			final UIObject				showRelativeTo) {
		GWT.runAsync(UserShareRightsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserShareRightsDlg());
				if (null != usrDlgClient) {
					usrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != usrDlgClient) {
					// Yes!  Create it and return it via the callback.
					UserShareRightsDlg usrDlg = new UserShareRightsDlg();
					usrDlgClient.onSuccess(usrDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(usrDlg, userIds, showRelativeTo);
				}
			}
		});
	}
	
	/**
	 * Loads the UserShareRightsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param usrDlgClient
	 */
	public static void createAsync(UserShareRightsDlgClient usrDlgClient) {
		doAsyncOperation(usrDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the user share rights dialog.
	 * 
	 * @param usrDlg
	 * @param userIds
	 * @param showRelativeTo
	 */
	public static void initAndShow(UserShareRightsDlg usrDlg, List<Long> userIds, UIObject showRelativeTo) {
		doAsyncOperation(null, usrDlg, userIds, showRelativeTo);
	}
	
	/**
	 * Initializes and shows the user share rights dialog.
	 * 
	 * @param usrDlg
	 * @param userIds
	 */
	public static void initAndShow(UserShareRightsDlg usrDlg, List<Long> userIds) {
		// Always use the initial form of the method.
		initAndShow(usrDlg, userIds, null);
	}
}
