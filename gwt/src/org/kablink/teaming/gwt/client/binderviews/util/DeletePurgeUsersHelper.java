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

package org.kablink.teaming.gwt.client.binderviews.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.DeletePurgeUsersCmdBase;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteUserWorkspacesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.PurgeUserWorkspacesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PurgeUsersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ProgressDlg;
import org.kablink.teaming.gwt.client.util.ProgressDlg.ProgressCallback;
import org.kablink.teaming.gwt.client.util.ProgressDlg.ProgressDlgClient;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper methods for deleting and/or purging users and/or their
 * workspaces.
 *
 * @author drfoster@novell.com
 */
public class DeletePurgeUsersHelper {
	private boolean					m_operationCanceled;	// Set true if the operation gets canceled.
	private DeletePurgeUsersCmdBase	m_dpuCmd;				// The delete/purge users command to perform.
	private int						m_totalUserCount;		// Count of items in m_sourceUserIds.
	private List<ErrorInfo>			m_collectedErrors;		// Collects errors that occur while processing an operation on the list of users. 
	private List<Long>				m_sourceUserIds;		// The user IDs being operated on.
	private Map<StringIds, String>	m_strMap;				// Initialized with a map of the strings used to run the operation.
	
	// The following are used to manage the strings displayed by a
	// delete/purge operation.  A map is loaded with the appropriate
	// strings from the resource bundle for each of these IDs for each
	// operation performed.
	private enum StringIds{
		ERROR_OP_FAILURE,
		ERROR_RPC_FAILURE,
		PROGRESS_CAPTION,
		PROGRESS_MESSAGE,
	}
	
	/*
	 * Constructor method.
	 * 
	 * Private to inhibit the class from being instantiated from
	 * outside of it.
	 */
	private DeletePurgeUsersHelper(List<Long> sourceUserIds, Map<StringIds, String> strMap, DeletePurgeUsersCmdBase dpuCmd) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_strMap         = strMap;
		m_sourceUserIds  = sourceUserIds;
		m_totalUserCount = m_sourceUserIds.size();
		m_dpuCmd         = dpuCmd;
		
		// ...and initialize everything else.
		m_collectedErrors = new ArrayList<ErrorInfo>();
	}

	/**
	 * Asynchronously deletes the user's workspaces.
	 * 
	 * @param sourceUserIds
	 */
	public static void deleteUserWorkspacesAsync(final List<Long> sourceUserIds) {
		ProgressDlg.createAsync(new ProgressDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(final ProgressDlg pDlg) {
				// Load the strings...
				Map<StringIds, String> strMap = new HashMap<StringIds, String>();
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().deleteUserWorkspacesError()              );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_DeleteUserWorkspaces()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsDeleteUserWorkspacesCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsDeleteUserWorkspacesProgress());

				// ...create the helper...
				final DeletePurgeUsersHelper dpuHelper = new DeletePurgeUsersHelper(
					sourceUserIds,
					strMap,
					new DeleteUserWorkspacesCmd(
						sourceUserIds));
				
				// ...and perform the delete...
				if (ProgressDlg.needsChunking(dpuHelper.m_totalUserCount)) {
					// ...chunking as necessary.
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							dpuHelper.dpuOpAsync(pDlg);
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.m_operationCanceled = true;
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We manage completion via
							// how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					dpuHelper.m_totalUserCount);
				}
				
				else {
					// ...without chunking.
					dpuHelper.dpuOpAsync(null);
				}
			}
		});
	}

	/*
	 * Asynchronously performs an operation on a collection of users.
	 */
	private void dpuOpAsync(final ProgressDlg pDlg) {
		// If the user canceled the operation... 
		if (m_operationCanceled) {
			// bail.
			return;
		}
		
		ScheduledCommand doOp = new ScheduledCommand() {
			@Override
			public void execute() {
				dpuOpNow(pDlg);
			}
		};
		Scheduler.get().scheduleDeferred(doOp);
	}

	/*
	 * Asynchronously completes the operation sequence.
	 */
	private void dpuOpCompleteAsync() {
		ScheduledCommand doOpDone = new ScheduledCommand() {
			@Override
			public void execute() {
				dpuOpCompleteNow();
			}
		};
		Scheduler.get().scheduleDeferred(doOpDone);
	}
	
	/*
	 * Synchronously completes the operation sequence.
	 */
	private void dpuOpCompleteNow() {
		// Did we collect any errors during the process?
		int totalErrorCount = m_collectedErrors.size();
		if (0 < totalErrorCount) {
			// Yes!  Tell the user about the problem(s).
			GwtClientHelper.displayMultipleErrors(
				m_strMap.get(StringIds.ERROR_OP_FAILURE),
				m_collectedErrors,
				500);	// Delay to allow a progress dialog time to reflect any final updates.
		}
		
		// If anything was done...
		if (totalErrorCount != m_totalUserCount) {
			// ...force the content to refresh just in case its got
			// ...something displayed that depends on it.
			FullUIReloadEvent.fireOne();
		}
	}
	
	/*
	 * Synchronously performs an operation on a collection of users.
	 */
	private void dpuOpNow(final ProgressDlg pDlg) {
		// If the user canceled the operation... 
		if (m_operationCanceled) {
			// bail.
			return;
		}
		
		// Do we need to process things in chunks?
		boolean cmdIsChunkList = (m_dpuCmd.getUserIds() != m_sourceUserIds);
		if (cmdIsChunkList || ((null != pDlg) && ProgressDlg.needsChunking(m_totalUserCount))) {
			// Yes!  Make sure we're using a separate list for the
			// chunks vs. the source list that we're operating on.
			List<Long> chunkList;
			if (cmdIsChunkList) {
				chunkList = m_dpuCmd.getUserIds();
				chunkList.clear();
			}
			else {
				chunkList = new ArrayList<Long>();
				m_dpuCmd.setUserIds(chunkList);
			}
			
			// Scan the user IDs to be operated on...
			while (!m_operationCanceled) {
				// ...moving each user ID from the source list into
				// ...the chunk list.
				chunkList.add(m_sourceUserIds.get(0));
				m_sourceUserIds.remove(0);
				
				// Was that the user to be operated on?
				if (m_sourceUserIds.isEmpty()) {
					// Yes!  Break out of the loop and let the chunk
					// get handled as if we weren't sending by chunks.
					break;
				}
				
				// Have we reached the size we chunk things at?
				if (ProgressDlg.isChunkFull(chunkList.size())) {
					// Yes!  Send this chunk.  Note that this is a
					// recursive call and will come back through this
					// method for the next chunk.
					dpuOpImpl(null, pDlg, true);
					return;
				}
			}
		}

		// Do we have any users to be operated on?
		if ((!m_operationCanceled) && (!(m_dpuCmd.getUserIds().isEmpty()))) {
			// Yes!  If we're doing things without using chunks...
			SpinnerPopup busy;
			if (m_dpuCmd.getUserIds() == m_sourceUserIds) {
				// ...create a busy spinner for the operation...
				busy = new SpinnerPopup();
				busy.center();
			}
			else {
				busy = null;
			}

			// ...and perform the final step of the operation.
			dpuOpImpl(busy, pDlg, false);
		}
	}

	/*
	 * Performs an operation on a collection of users.
	 */
	private void dpuOpImpl(final SpinnerPopup busy, final ProgressDlg pDlg, final boolean moreRemaining) {
		GwtClientHelper.executeCommand(m_dpuCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Hide any busy/progress dialogs...
				if (null != busy) busy.hide();
				if (null != pDlg) pDlg.hide();

				// ...mark the operation as having been canceled...
				m_operationCanceled = true;

				// ...and tell the user about the RPC failure.
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_strMap.get(
						StringIds.ERROR_RPC_FAILURE));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != busy) && (!moreRemaining)) busy.hide();
				if  (null != pDlg)                      pDlg.updateProgress(m_dpuCmd.getUserIds().size());
				
				// Did everything we ask get done?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<ErrorInfo> chunkErrors = responseData.getErrorList();
				int chunkErrorCount = ((null == chunkErrors) ? 0 : chunkErrors.size());
				if (0 < chunkErrorCount) {
					// No!  Copy the errors into the List<ErrorInfo>
					// we're collecting them in.
					for (ErrorInfo chunkError:  chunkErrors) {
						m_collectedErrors.add(chunkError);
					}
				}
				
				// Did we just do a part of what we need to do?
				if (moreRemaining) {
					// Yes!  Request that the next chunk be sent.
					dpuOpAsync(pDlg);
				}
				
				else {
					// No, we didn't just do part of it, but everything
					// remaining!  Hide the progress dialog and wrap
					// things up.
					if (null != pDlg) {
						pDlg.hide();
					}
					dpuOpCompleteAsync();
				}
			}
		});
	}
	
	/**
	 * Asynchronously purges the users and their workspaces.
	 * 
	 * @param sourceUserIds
	 */
	public static void purgeUsersAsync(final List<Long> sourceUserIds, final boolean purgeMirrored) {
		ProgressDlg.createAsync(new ProgressDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(final ProgressDlg pDlg) {
				// Load the strings...
				Map<StringIds, String> strMap = new HashMap<StringIds, String>();
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().purgeUsersError()              );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_PurgeUsers()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsPurgeUsersCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsPurgeUsersProgress());

				// ...create the helper...
				final DeletePurgeUsersHelper dpuHelper = new DeletePurgeUsersHelper(
					sourceUserIds,
					strMap,
					new PurgeUsersCmd(
						sourceUserIds,
						purgeMirrored));
				
				// ...and perform the purge...
				if (ProgressDlg.needsChunking(dpuHelper.m_totalUserCount)) {
					// ...chunking as necessary.
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							dpuHelper.dpuOpAsync(pDlg);
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.m_operationCanceled = true;
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We manage completion via
							// how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					dpuHelper.m_totalUserCount);
				}
				
				else {
					// ...without chunking.
					dpuHelper.dpuOpAsync(null);
				}
			}
		});
	}
	
	/**
	 * Asynchronously purges the user's workspaces.
	 * 
	 * @param sourceUserIds
	 */
	public static void purgeUserWorkspacesAsync(final List<Long> sourceUserIds, final boolean purgeMirrored) {
		ProgressDlg.createAsync(new ProgressDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(final ProgressDlg pDlg) {
				// Load the strings...
				Map<StringIds, String> strMap = new HashMap<StringIds, String>();
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().purgeUserWorkspacesError()              );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_PurgeUserWorkspaces()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsPurgeUserWorkspacesCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsPurgeUserWorkspacesProgress());

				// ...create the helper...
				final DeletePurgeUsersHelper dpuHelper = new DeletePurgeUsersHelper(
					sourceUserIds,
					strMap,
					new PurgeUserWorkspacesCmd(
						sourceUserIds,
						purgeMirrored));
				
				// ...and perform the purge...
				if (ProgressDlg.needsChunking(dpuHelper.m_totalUserCount)) {
					// ...chunking as necessary.
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							dpuHelper.dpuOpAsync(pDlg);
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.m_operationCanceled = true;
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We manage completion via
							// how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					dpuHelper.m_totalUserCount);
				}
				
				else {
					// ...without chunking.
					dpuHelper.dpuOpAsync(null);
				}
			}
		});
	}
}
