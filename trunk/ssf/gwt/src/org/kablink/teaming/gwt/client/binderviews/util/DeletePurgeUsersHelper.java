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
	private List<String>			m_collectedErrors;		// Collects errors that occur while processing the operation on the users. 
	private Map<StringIds, String>	m_strMap;				// Initialized with a map of the strings used to run the operation.
	
	// The following is used to manage the strings displayed by a
	// delete/purge operations.  A map is loaded with the appropriate
	// strings from the resource bundle for each ID for each operation
	// performed.
	private enum StringIds{
		ERROR_OP_FAILURE,
		ERROR_RPC_FAILURE,
		PROGRESS_CAPTION,
		PROGRESS_MESSAGE,
	}
	
	/*
	 * Constructor method.
	 * 
	 * Private to inhibits the class from being instantiated from
	 * outside it.
	 */
	private DeletePurgeUsersHelper(Map<StringIds, String> strMap) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_strMap = strMap;
		
		// ...and initialize everything else.
		m_collectedErrors = new ArrayList<String>();
	}

	/*
	 * Get'er methods.
	 */
	private boolean isOperationCanceled() {return m_operationCanceled;}
	
	/*
	 * Set'er methods.
	 */
	private void setOperationCanceled(boolean operationCanceled) {m_operationCanceled = operationCanceled;}

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

				final DeleteUserWorkspacesCmd	deleteCmd       = new DeleteUserWorkspacesCmd(sourceUserIds);
				final DeletePurgeUsersHelper	dpuHelper       = new DeletePurgeUsersHelper(strMap);
				final int						totalUserCount  = sourceUserIds.size();
				
				if (pDlg.needProgressDialog(totalUserCount)) {
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							// ...and perform the delete.
							dpuHelper.dpuOpAsync(
								pDlg,				// Progress dialog to use.
								deleteCmd,			// Command for the operation.
								totalUserCount,		// Total number of users being operated on.
								sourceUserIds);		// Initial list of user IDs.
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.setOperationCanceled(true);
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We managing completion
							// via how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					totalUserCount);
				}
				
				else {
					// ...and perform the delete.
					dpuHelper.dpuOpAsync(
						null,				// null -> No progress dialog required.
						deleteCmd,			// Command for the operation.
						totalUserCount,		// Total number of users being operated on.
						sourceUserIds);		// Initial list of user IDs.
				}
			}
		});
	}

	/*
	 * Asynchronously performs an operation on a collection of users.
	 */
	private void dpuOpAsync(
			final ProgressDlg				pDlg,
			final DeletePurgeUsersCmdBase	cmd,
			final int						totalUserCount,
			final List<Long>				sourceUserIds) {
		// If the user canceled the operation... 
		if (isOperationCanceled()) {
			// bail.
			return;
		}
		
		ScheduledCommand doOp = new ScheduledCommand() {
			@Override
			public void execute() {
				dpuOpNow(
					pDlg,
					cmd,
					totalUserCount,
					sourceUserIds);
			}
		};
		Scheduler.get().scheduleDeferred(doOp);
	}
	
	/*
	 * Synchronously performs an operation on a collection of users.
	 */
	private void dpuOpNow(
			final ProgressDlg				pDlg,
			final DeletePurgeUsersCmdBase	cmd,
			final int						totalUserCount,
			final List<Long>				sourceUserIds) {
		// If the user canceled the operation... 
		if (isOperationCanceled()) {
			// bail.
			return;
		}
		
		// Do we need to process things in chunks?
		boolean cmdIsChunkList = (cmd.getUserIds() != sourceUserIds);
		if (cmdIsChunkList || ((null != pDlg) && pDlg.needProgressDialog(totalUserCount))) {
			// Yes!  Make sure we're using a separate list for the
			// chunks vs. the source list that we're operating on.
			List<Long> chunkList;
			if (cmdIsChunkList) {
				chunkList = cmd.getUserIds();
				chunkList.clear();
			}
			else {
				chunkList = new ArrayList<Long>();
				cmd.setUserIds(chunkList);
			}
			
			// Scan the user IDs to be operated on...
			while (!(isOperationCanceled())) {
				// ...moving each user ID from the source list into
				// ...the chunk list.
				chunkList.add(sourceUserIds.get(0));
				sourceUserIds.remove(0);
				
				// Was that the user to be operated on?
				if (sourceUserIds.isEmpty()) {
					// Yes!  Break out of the loop and let the chunk
					// get handled as if we weren't sending by chunks.
					break;
				}
				
				// Have we reached the size we chunk things at?
				if (ProgressDlg.CHUNK_SIZE == chunkList.size()) {
					// Yes!  Send this chunk.  Note that this is a
					// recursive call and will come back through this
					// method for the next chunk.
					dpuOpImpl(
						null,				// null -> No busy spinner.
						pDlg,				//
						cmd,				//
						totalUserCount,		//
						chunkList,			//
						true);				// true -> More remaining.
					
					return;
				}
			}
		}

		// Do we have any users to be operated on?
		if ((!(isOperationCanceled())) && (!(cmd.getUserIds().isEmpty()))) {
			// Yes!  If we're doing things without using chunks...
			SpinnerPopup busy;
			if (cmd.getUserIds() == sourceUserIds) {
				// ...create a busy spinner for the operation...
				busy = new SpinnerPopup();
				busy.center();
			}
			else {
				busy = null;
			}

			// ...and perform the final step of the operation.
			dpuOpImpl(
				busy,				//
				pDlg,				//
				cmd,				//
				totalUserCount,		//
				sourceUserIds,		//
				false);				// false -> No more remaining.
		}
	}

	/*
	 * Performs an operation on a collection of users.
	 */
	private void dpuOpImpl(
			final SpinnerPopup				busy,
			final ProgressDlg				pDlg,
			final DeletePurgeUsersCmdBase	cmd,
			final int						totalUserCount,
			final List<Long>				sourceUserIds,
			final boolean					moreRemaining) {
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Hide any busy/progress dialogs...
				if (null != busy) busy.hide();
				if (null != pDlg) pDlg.hide();

				// ...mark the operation as having been canceled...
				setOperationCanceled(true);

				// ...and tell the user about the RPC failure.
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_strMap.get(
						StringIds.ERROR_RPC_FAILURE));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != busy) && (!moreRemaining)) busy.hide();
				if  (null != pDlg)                      ProgressDlg.updateProgress(pDlg, sourceUserIds.size());
				
				// Did everything we ask get done?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> chunkErrors = responseData.getErrorList();
				int chunkErrorCount = ((null == chunkErrors) ? 0 : chunkErrors.size());
				if (0 < chunkErrorCount) {
					// No!  Copy the errors into the List<String> we're
					// collecting them in.
					for (String chunkError:  chunkErrors) {
						m_collectedErrors.add(chunkError);
					}
				}
				
				// Did we just do a part of what we need to do?
				if (moreRemaining) {
					// Yes!  Request that the next chunk be sent.
					dpuOpAsync(
						pDlg,
						cmd,
						totalUserCount,
						sourceUserIds);
				}
				
				else {
					// No, we didn't just do part of it, but everything
					// remaining!  Did we collect any errors during the
					// process?
					int totalErrorCount = m_collectedErrors.size();
					if (0 < totalErrorCount) {
						// Yes!  Tell the user about the problem(s).
						GwtClientHelper.displayMultipleErrors(
							m_strMap.get(StringIds.ERROR_OP_FAILURE),
							m_collectedErrors);
					}
					
					// If anything was done...
					if (totalErrorCount != totalUserCount) {
						// ...force the content to refresh just in case its
						// ...got something displayed that depends on it.
						FullUIReloadEvent.fireOne();
					}
				}
			}
		});
	}
	
	/**
	 * Asynchronously purges the users and their workspaces.
	 * 
	 * @param sourceUserIds
	 */
	public static void purgeUsersAsync(final List<Long> sourceUserIds) {
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

				final PurgeUsersCmd				purgeCmd        = new PurgeUsersCmd(sourceUserIds);
				final DeletePurgeUsersHelper	dpuHelper       = new DeletePurgeUsersHelper(strMap);
				final int						totalUserCount  = sourceUserIds.size();
				
				if (pDlg.needProgressDialog(totalUserCount)) {
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							// ...and perform the purge.
							dpuHelper.dpuOpAsync(
								pDlg,				// Progress dialog to use.
								purgeCmd,			// Command for the operation.
								totalUserCount,		// Total number of users being operated on.
								sourceUserIds);		// Initial list of user IDs.
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.setOperationCanceled(true);
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We managing completion
							// via how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					totalUserCount);
				}
				
				else {
					// ...and perform the purge.
					dpuHelper.dpuOpAsync(
						null,				// null -> No progress dialog required.
						purgeCmd,			// Command for the operation.
						totalUserCount,		// Total number of users being operated on.
						sourceUserIds);		// Initial list of user IDs.
				}
			}
		});
	}
	
	/**
	 * Asynchronously purges the user's workspaces.
	 * 
	 * @param sourceUserIds
	 */
	public static void purgeUserWorkspacesAsync(final List<Long> sourceUserIds) {
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

				final PurgeUserWorkspacesCmd	purgeCmd        = new PurgeUserWorkspacesCmd(sourceUserIds);
				final DeletePurgeUsersHelper	dpuHelper       = new DeletePurgeUsersHelper(strMap);
				final int						totalUserCount  = sourceUserIds.size();
				
				if (pDlg.needProgressDialog(totalUserCount)) {
					ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
						@Override
						public void dialogReady() {
							// ...and perform the purge.
							dpuHelper.dpuOpAsync(
								pDlg,				// Progress dialog to use.
								purgeCmd,			// Command for the operation.
								totalUserCount,		// Total number of users being operated on.
								sourceUserIds);		// Initial list of user IDs.
						}
						
						@Override
						public void operationCanceled() {
							// Simply mark the global indicating we've
							// been canceled.  The operation will stop
							// on the next chunk.
							dpuHelper.setOperationCanceled(true);
						}

						@Override
						public void operationComplete() {
							// Nothing to do.  We managing completion
							// via how we handle the items in the list.
						}
					},
					true,	// true -> Operation can be canceled.
					strMap.get(StringIds.PROGRESS_CAPTION),
					strMap.get(StringIds.PROGRESS_MESSAGE),
					totalUserCount);
				}
				
				else {
					// ...and perform the purge.
					dpuHelper.dpuOpAsync(
						null,				// null -> No progress dialog required.
						purgeCmd,			// Command for the operation.
						totalUserCount,		// Total number of users being operated on.
						sourceUserIds);		// Initial list of user IDs.
				}
			}
		});
	}
}
