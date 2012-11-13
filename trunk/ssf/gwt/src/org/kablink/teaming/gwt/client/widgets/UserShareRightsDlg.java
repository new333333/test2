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
import org.kablink.teaming.gwt.client.rpc.shared.SetUserSharingRightsInfoCmd.CombinedPerUserShareRightsInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PerUserShareRightsInfo;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Implements the user share rights dialog.
 *  
 * @author drfoster@novell.com
 */
public class UserShareRightsDlg extends DlgBox implements EditSuccessfulHandler {
	@SuppressWarnings("unused")
	private BinderInfo								m_binderInfo;			// The profiles root workspace the dialog is running against.
	private GwtTeamingMessages						m_messages;				// Access to Vibe's messages.
	private InlineLabel								m_progressIndicator;	// Text under the progress bar that displays what's going on.
	private int										m_totalDone;			// Tracks a running count of users whose sharing rights we've set.
	private List<Long>								m_userIds;				// The List<Long> of user IDs whose sharing rights are being set.
	private PerUserShareRightsInfo					m_singleUserRights;		// If the sharing rights are being set for a single user, this contains their current rights setting when the dialog is invoked. 
	private ProgressBar								m_progressBar;			// Progress bar displayed while saving the share rights.
	private UserSharingRightsInfoRpcResponseData	m_rightsInfo;			// Information about sharing rights available and to be set.
	private VibeFlowPanel							m_progressPanel;		// Panel containing the progress bar.
	private VibeVerticalPanel						m_vp;					// The panel holding the dialog's content.

	// Indexes of the various data columns.
	private final static int COLUMN_HEADER		= 0;
	private final static int COLUMN_INTERNAL	= 1;
	private final static int COLUMN_EXTERNAL	= 2;
	private final static int COLUMN_PUBLIC		= 3;
	private final static int COLUMN_FORWARDING	= 4;
	
	// Indexes of the various data rows.
	private final static int ROW_HEADER		= 0;
	private final static int ROW_SET		= 1;
	private final static int ROW_CLEAR		= 2;
	private final static int ROW_UNCHANGED	= 3;
	
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
		super(false, true);

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
	 * Adds a rights clear cell to the table.
	 */
	private void addRadioCell(FlexTable ft, FlexCellFormatter fcf, int row, int column, boolean checked) {
		String rbGroup = (RBID_BASE + ":" + column);
		String rbId    = (rbGroup   + ":" + row);
		RadioButton rb = new RadioButton(rbGroup);
		rb.getElement().setId(rbId);
		rb.addStyleName(              "vibe-userShareRightsDlg-radio"    );
		fcf.addStyleName(row, column, "vibe-userShareRightsDlg-radioCell");
		ft.setWidget(    row, column, rb);
		rb.setValue(checked);
	}
	
	/*
	 * Adds a rights column header cell to the table.
	 */
	private void addRightsColumnHeaderCell(FlexTable ft, FlexCellFormatter fcf, int column, String text) {
		InlineLabel il = new InlineLabel(text);
		il.addStyleName(                     "vibe-userShareRightsDlg-header"    );
		fcf.addStyleName(ROW_HEADER, column, "vibe-userShareRightsDlg-headerCell");
		ft.setWidget(    ROW_HEADER, column, il);
	}
	
	/*
	 * Adds a rights row header cell to the table.
	 */
	private void addRightsRowHeaderCell(FlexTable ft, FlexCellFormatter fcf, int row, String text) {
		InlineLabel il = new InlineLabel(text);
		il.addStyleName(                     "vibe-userShareRightsDlg-row"    );
		fcf.addStyleName(row, COLUMN_HEADER, "vibe-userShareRightsDlg-rowCell");
		ft.setWidget(    row, COLUMN_HEADER, il);
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
	 * Returns a CombinedPerUserShareRightsInfo object that reflects
	 * the current selections in the dialog.
	 */
	private CombinedPerUserShareRightsInfo collectSharingRights() {
		// Create objects for the set and value flags.
		PerUserShareRightsInfo setFlags   = new PerUserShareRightsInfo();
		PerUserShareRightsInfo valueFlags = new PerUserShareRightsInfo();

		// Do we have an external column?
		boolean hasUnchanged = (null == m_singleUserRights);
		boolean isRBChecked;
		if (m_rightsInfo.isExternalEnabled()) {
			// Yes!  Initialize its set and value flags.
			isRBChecked = isRBChecked(ROW_SET, COLUMN_EXTERNAL);
			valueFlags.setAllowExternal(isRBChecked);
			if (hasUnchanged)
			     setFlags.setAllowExternal(!(isRBChecked(ROW_UNCHANGED, COLUMN_EXTERNAL)));
			else setFlags.setAllowExternal(isRBChecked != m_singleUserRights.isAllowExternal());
		}
		
		// Do we have a forwarding column?
		if (m_rightsInfo.isForwardingEnabled()) {
			// Yes!  Initialize its set and value flags.
			isRBChecked = isRBChecked(ROW_SET, COLUMN_FORWARDING);
			valueFlags.setAllowForwarding(isRBChecked);
			if (hasUnchanged)
			     setFlags.setAllowForwarding(!(isRBChecked(ROW_UNCHANGED, COLUMN_FORWARDING)));
			else setFlags.setAllowForwarding(isRBChecked != m_singleUserRights.isAllowForwarding());
		}
		
		// Do we have an internal column?
		if (m_rightsInfo.isInternalEnabled()) {
			// Yes!  Initialize its set and value flags.
			isRBChecked = isRBChecked(ROW_SET, COLUMN_INTERNAL);
			valueFlags.setAllowInternal(isRBChecked);
			if (hasUnchanged)
			     setFlags.setAllowInternal(!(isRBChecked(ROW_UNCHANGED, COLUMN_INTERNAL)));
			else setFlags.setAllowInternal(isRBChecked != m_singleUserRights.isAllowInternal());
		}
		
		// Do we have a public column?
		if (m_rightsInfo.isPublicEnabled()) {
			// Yes!  Initialize its set and value flags.
			isRBChecked = isRBChecked(ROW_SET, COLUMN_PUBLIC);
			valueFlags.setAllowPublic(isRBChecked);
			if (hasUnchanged)
			     setFlags.setAllowPublic(!(isRBChecked(ROW_UNCHANGED, COLUMN_PUBLIC)));
			else setFlags.setAllowPublic(isRBChecked != m_singleUserRights.isAllowPublic());
		}

		// Finally, return a CombinderPerUserShareRightsInfo object
		// with the set and value flags.
		return
			new CombinedPerUserShareRightsInfo(
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
		m_vp.addStyleName("vibe-userShareRightsDlg-panel");
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
		CombinedPerUserShareRightsInfo sharingRights = collectSharingRights();
		
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
	 * Returns true if a radio button is checked and false otherwise.
	 */
	private boolean isRBChecked(int row, int column) {
		String			rbGroup = (RBID_BASE + ":" + column);
		String			rbId    = (rbGroup   + ":" + row);
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
				// Yes!  Store it and populate the dialog.
				m_rightsInfo = ((UserSharingRightsInfoRpcResponseData) result.getResponseData());
				if (1 == m_userIds.size()) {
					m_singleUserRights = m_rightsInfo.getUserRights(m_userIds.get(0));
					if (null == m_singleUserRights) {
						GwtClientHelper.deferredAlert(m_messages.userShareRightsDlgErrorNoWorkspace());
						return;
					}
				}
				else {
					m_singleUserRights = null;
				}
				
				populateDlgAsync();
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
		// Clear the panel that holds the content...
		m_vp.clear();

		// ...create the table containing the dialog's content... 
		FlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-userShareRightsDlg-table");
		m_vp.add(ft);
		FlexCellFormatter fcf = ft.getFlexCellFormatter();
		ft.setCellPadding(2);
		ft.setCellSpacing(2);

		// ...define the column header cells...
		if (m_rightsInfo.isInternalEnabled())   addRightsColumnHeaderCell(ft, fcf, COLUMN_INTERNAL,   m_messages.userShareRightsDlgRight_AllowInternal()  );
		if (m_rightsInfo.isExternalEnabled())   addRightsColumnHeaderCell(ft, fcf, COLUMN_EXTERNAL,   m_messages.userShareRightsDlgRight_AllowExternal()  );
		if (m_rightsInfo.isPublicEnabled())     addRightsColumnHeaderCell(ft, fcf, COLUMN_PUBLIC,     m_messages.userShareRightsDlgRight_AllowPublic()    );
		if (m_rightsInfo.isForwardingEnabled()) addRightsColumnHeaderCell(ft, fcf, COLUMN_FORWARDING, m_messages.userShareRightsDlgRight_AllowForwarding());
		
		// ...define the row header cells...
		addRightsRowHeaderCell(    ft, fcf, ROW_SET,       m_messages.userShareRightsDlgLabel_Set()      );
		addRightsRowHeaderCell(    ft, fcf, ROW_CLEAR,     m_messages.userShareRightsDlgLabel_Clear()    );
		if (null == m_singleUserRights) {
			addRightsRowHeaderCell(ft, fcf, ROW_UNCHANGED, m_messages.userShareRightsDlgLabel_Unchanged());
		}
		
		// ...define the rights setting cells...
		if (m_rightsInfo.isInternalEnabled())   addRadioCell(ft, fcf, ROW_SET, COLUMN_INTERNAL,   ((null != m_singleUserRights) && m_singleUserRights.isAllowInternal())  );
		if (m_rightsInfo.isExternalEnabled())   addRadioCell(ft, fcf, ROW_SET, COLUMN_EXTERNAL,   ((null != m_singleUserRights) && m_singleUserRights.isAllowExternal())  );
		if (m_rightsInfo.isPublicEnabled())     addRadioCell(ft, fcf, ROW_SET, COLUMN_PUBLIC,     ((null != m_singleUserRights) && m_singleUserRights.isAllowPublic())    );
		if (m_rightsInfo.isForwardingEnabled()) addRadioCell(ft, fcf, ROW_SET, COLUMN_FORWARDING, ((null != m_singleUserRights) && m_singleUserRights.isAllowForwarding()));
		
		// ...define the rights clearing cells...
		if (m_rightsInfo.isInternalEnabled())   addRadioCell(ft, fcf, ROW_CLEAR, COLUMN_INTERNAL,   ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowInternal())))  );
		if (m_rightsInfo.isExternalEnabled())   addRadioCell(ft, fcf, ROW_CLEAR, COLUMN_EXTERNAL,   ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowExternal())))  );
		if (m_rightsInfo.isPublicEnabled())     addRadioCell(ft, fcf, ROW_CLEAR, COLUMN_PUBLIC,     ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowPublic())))    );
		if (m_rightsInfo.isForwardingEnabled()) addRadioCell(ft, fcf, ROW_CLEAR, COLUMN_FORWARDING, ((null != m_singleUserRights) && (!(m_singleUserRights.isAllowForwarding()))));

		// ...if we were given multiple users...
		if (null == m_singleUserRights) {
			// ...define the rights unchanged cells...
			if (m_rightsInfo.isInternalEnabled())   addRadioCell(ft, fcf, ROW_UNCHANGED, COLUMN_INTERNAL,   true);
			if (m_rightsInfo.isExternalEnabled())   addRadioCell(ft, fcf, ROW_UNCHANGED, COLUMN_EXTERNAL,   true);
			if (m_rightsInfo.isPublicEnabled())     addRadioCell(ft, fcf, ROW_UNCHANGED, COLUMN_PUBLIC,     true);
			if (m_rightsInfo.isForwardingEnabled()) addRadioCell(ft, fcf, ROW_UNCHANGED, COLUMN_FORWARDING, true);
		}
		
		// ...add a progress bar...
		m_progressBar = new ProgressBar(0, m_userIds.size());
		m_progressBar.addStyleName("vibe-userShareRightsDlg-progressBar");
		m_vp.add(m_progressBar);
		m_progressBar.setVisible(false);
		
		// ...and a panel for displaying progress, when needed...
		m_progressPanel = new VibeFlowPanel();
		m_progressPanel.addStyleName("vibe-userShareRightsDlg-progressPanel");
		m_vp.add(m_progressPanel);
		m_progressPanel.setVisible(false);
		m_progressPanel.add(buildSpinnerImage("vibe-userShareRightsDlg-progressSpinner"));
		m_progressIndicator = new InlineLabel("");
		m_progressIndicator.addStyleName("vibe-userShareRightsDlg-progressLabel");
		m_progressPanel.add(m_progressIndicator);

		// ...and finally, make sure the buttons are enabled and show
		// ...the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private static void runDlgAsync(final UserShareRightsDlg usrDlg, final BinderInfo bi, final List<Long> userIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				usrDlg.runDlgNow(bi, userIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the user share rights
	 * dialog.
	 */
	private void runDlgNow(BinderInfo bi, List<Long> userIds) {
		// Store the parameters...
		m_binderInfo = bi;
		m_userIds    = userIds;
		
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
									m_messages.userShareRightsDlgErrorSetFailures(),
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
			final BinderInfo			bi,
			final List<Long>			userIds) {
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
					runDlgAsync(usrDlg, bi, userIds);
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
	 * @param bi
	 * @param userIds
	 */
	public static void initAndShow(UserShareRightsDlg usrDlg, BinderInfo bi, List<Long> userIds) {
		doAsyncOperation(null, usrDlg, bi, userIds);
	}
}
