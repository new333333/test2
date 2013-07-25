/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.rpc.shared.DeletePurgeFolderEntriesCmdBase;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteSelectionsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteTasksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.PurgeTasksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.DeleteSelectionsMode;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ProgressDlg;
import org.kablink.teaming.gwt.client.util.ProgressDlg.ProgressCallback;
import org.kablink.teaming.gwt.client.util.ProgressDlg.ProgressDlgClient;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper methods for deleting and/or purging entries and tasks.
 *
 * @author drfoster@novell.com
 */
public class DeletePurgeEntriesHelper {
	private boolean							m_operationCanceled;	// Set true if the operation gets canceled.
	private DeletePurgeEntriesCallback		m_dpeCallback;			// Callback interface used to inform callers about what happens.
	private DeletePurgeFolderEntriesCmdBase	m_dpeCmd;				// The delete/purge folder entries command to perform.
	private int								m_totalEntityCount;		// Count of items in m_sourceEntityIds.
	private List<EntityId>					m_sourceEntityIds;		// The entity IDs being operated on.
	private List<ErrorInfo>					m_collectedErrors;		// Collects errors that occur while processing an operation on the list of entries.
	private Map<StringIds, String>			m_strMap;				// Initialized with a map of the strings used to run the operation.
	
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
	
	/**
	 * Interface used by the helper to inform the caller about what
	 * happened. 
	 */
	public interface DeletePurgeEntriesCallback {
		public void operationCanceled();
		public void operationComplete();
		public void operationFailed();
	}
	
	/*
	 * Constructor method.
	 * 
	 * Private to inhibit the class from being instantiated from
	 * outside of it.
	 */
	private DeletePurgeEntriesHelper(List<EntityId> sourceEntityIds, Map<StringIds, String> strMap, DeletePurgeFolderEntriesCmdBase dpeCmd, DeletePurgeEntriesCallback dpeCallback) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_sourceEntityIds  = sourceEntityIds;
		m_totalEntityCount = m_sourceEntityIds.size();
		m_strMap           = strMap;
		m_dpeCmd           = dpeCmd;
		m_dpeCallback      = dpeCallback;
		
		// ...and initialize everything else.
		m_collectedErrors = new ArrayList<ErrorInfo>();
	}

	/**
	 * Asynchronously deletes the selected entries.
	 * 
	 * @param sourceEntityIds
	 * @param dsMode
	 * @param dpeCallback
	 */
	public static void deleteSelectedEntriesAsync(final List<EntityId> sourceEntityIds, final DeleteSelectionsMode dsMode, final DeletePurgeEntriesCallback dpeCallback) {
		switch (dsMode) {
		case TRASH_ALL:                 trashSelectedEntitiesAsync(                sourceEntityIds, dpeCallback); break;
		case TRASH_ADHOC_PURGE_OTHERS:  trashAdHocPurgeRemoteSelectedEntitiesAsync(sourceEntityIds, dpeCallback); break;
		case PURGE_ALL:                 purgeSelectedEntitiesAsync(                sourceEntityIds, dpeCallback); break;
		}
	}
	
	public static void deleteSelectedEntriesAsync(final List<EntityId> sourceEntityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// Always use the initial form of the method.
		deleteSelectedEntriesAsync(sourceEntityIds, DeleteSelectionsMode.TRASH_ALL, dpeCallback);
	}
	
	/**
	 * Asynchronously deletes the selected tasks.
	 * 
	 * @param sourceTaskIds
	 * @param dpeCallback
	 */
	public static void deleteSelectedTasksAsync(final List<EntityId> sourceTaskIds, final DeletePurgeEntriesCallback dpeCallback) {
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
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().deleteTasksError()              );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_DeleteTasks()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsDeleteTasksCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsDeleteTasksProgress());

				// ...create the helper...
				DeletePurgeEntriesHelper dpeHelper = new DeletePurgeEntriesHelper(
					sourceTaskIds,
					strMap,
					new DeleteTasksCmd(sourceTaskIds),
					dpeCallback);
				
				// ...and perform the delete.
				dpeHelper.runOpNow(pDlg);
			}
		});
	}
	
	/*
	 * Asynchronously performs an operation on a collection of entries.
	 */
	private void dpeOpAsync(final ProgressDlg pDlg) {
		// If the user canceled the operation... 
		if (m_operationCanceled) {
			// bail.
			return;
		}
		
		ScheduledCommand doOp = new ScheduledCommand() {
			@Override
			public void execute() {
				dpeOpNow(pDlg);
			}
		};
		Scheduler.get().scheduleDeferred(doOp);
	}

	/*
	 * Asynchronously completes the operation sequence.
	 */
	private void dpeOpCompleteAsync() {
		ScheduledCommand doOpDone = new ScheduledCommand() {
			@Override
			public void execute() {
				dpeOpCompleteNow();
			}
		};
		Scheduler.get().scheduleDeferred(doOpDone);
	}
	
	/*
	 * Synchronously completes the operation sequence.
	 */
	private void dpeOpCompleteNow() {
		// Did we collect any errors during the process?
		int totalErrorCount = m_collectedErrors.size();
		if (0 < totalErrorCount) {
			// Yes!  Tell the user about the problem(s).
			GwtClientHelper.displayMultipleErrors(
				m_strMap.get(StringIds.ERROR_OP_FAILURE),
				m_collectedErrors,
				500);	// Delay to allow a progress dialog time to reflect any final updates.
		}
		
		// Finally, tell the caller that the operation has
		// completed.
		m_dpeCallback.operationComplete();
	}
	
	/*
	 * Synchronously performs an operation on a collection of entries.
	 */
	private void dpeOpNow(final ProgressDlg pDlg) {
		// If the user canceled the operation... 
		if (m_operationCanceled) {
			// bail.
			return;
		}
		
		// Do we need to process things in chunks?
		boolean cmdIsChunkList = (m_dpeCmd.getEntityIds() != m_sourceEntityIds);
		if (cmdIsChunkList || ((null != pDlg) && ProgressDlg.needsChunking(m_totalEntityCount))) {
			// Yes!  Make sure we're using a separate list for the
			// chunks vs. the source list that we're operating on.
			List<EntityId> chunkList;
			if (cmdIsChunkList) {
				chunkList = m_dpeCmd.getEntityIds();
				chunkList.clear();
			}
			else {
				chunkList = new ArrayList<EntityId>();
				m_dpeCmd.setEntityIds(chunkList);
			}
			
			// Scan the entity IDs to be operated on...
			while (!m_operationCanceled) {
				// ...moving each entity ID from the source list into
				// ...the chunk list.
				chunkList.add(m_sourceEntityIds.get(0));
				m_sourceEntityIds.remove(0);
				
				// Was that the entry to be operated on?
				if (m_sourceEntityIds.isEmpty()) {
					// Yes!  Break out of the loop and let the chunk
					// get handled as if we weren't sending by chunks.
					break;
				}
				
				// Have we reached the size we chunk things at?
				if (ProgressDlg.isChunkFull(chunkList.size())) {
					// Yes!  Send this chunk.  Note that this is a
					// recursive call and will come back through this
					// method for the next chunk.
					dpeOpImpl(null, pDlg, true);
					return;
				}
			}
		}

		// Do we have any entries to be operated on?
		if ((!m_operationCanceled) && (!(m_dpeCmd.getEntityIds().isEmpty()))) {
			// Yes!  If we're doing things without using chunks...
			SpinnerPopup busy;
			if (m_dpeCmd.getEntityIds() == m_sourceEntityIds) {
				// ...create a busy spinner for the operation...
				busy = new SpinnerPopup();
				busy.center();
			}
			else {
				busy = null;
			}

			// ...and perform the final step of the operation.
			dpeOpImpl(busy, pDlg, false);
		}
	}

	/*
	 * Performs an operation on a collection of entries.
	 */
	private void dpeOpImpl(final SpinnerPopup busy, final ProgressDlg pDlg, final boolean moreRemaining) {
		GwtClientHelper.executeCommand(m_dpeCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Hide any busy/progress dialogs...
				if (null != busy) busy.hide();
				if (null != pDlg) pDlg.hide();

				// ...mark the operation as having been canceled...
				m_operationCanceled = true;
				m_dpeCallback.operationFailed();

				// ...and tell the user about the RPC failure.
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_strMap.get(
						StringIds.ERROR_RPC_FAILURE));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != busy) && (!moreRemaining)) busy.hide();
				if  (null != pDlg)                      pDlg.updateProgress(m_dpeCmd.getEntityIds().size());
				
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
					dpeOpAsync(pDlg);
				}
				
				else {
					// No, we didn't just do part of it, but everything
					// remaining!  Hide the progress dialog and wrap
					// things up.
					if (null != pDlg) {
						pDlg.hide();
					}
					dpeOpCompleteAsync();
				}
			}
		});
	}
	
	/*
	 * Asynchronously purges the selected entries.
	 */
	private static void purgeSelectedEntitiesAsync(final List<EntityId> sourceEntityIds, final DeletePurgeEntriesCallback dpeCallback) {
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
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().deleteFolderEntriesError()           );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_DeleteSelections()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsDeleteSelectionsCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsDeleteSelectionsProgress());

				// ...create the helper...
				DeletePurgeEntriesHelper dpeHelper = new DeletePurgeEntriesHelper(
					sourceEntityIds,
					strMap,
					new DeleteSelectionsCmd(sourceEntityIds, DeleteSelectionsMode.PURGE_ALL),
					dpeCallback);
				
				// ...and perform the purge...
				dpeHelper.runOpNow(pDlg);
			}
		});
	}
	
	/**
	 * Asynchronously purges the selected tasks.
	 * 
	 * @param sourceTaskIds
	 * @param dpeCallback
	 */
	public static void purgeSelectedTasksAsync(final List<EntityId> sourceTaskIds, final DeletePurgeEntriesCallback dpeCallback) {
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
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().purgeTasksError()              );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_PurgeTasks()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsPurgeTasksCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsPurgeTasksProgress());

				// ...create the helper...
				DeletePurgeEntriesHelper dpeHelper = new DeletePurgeEntriesHelper(
					sourceTaskIds,
					strMap,
					new PurgeTasksCmd(sourceTaskIds),
					dpeCallback);
				
				// ...and perform the purge...
				dpeHelper.runOpNow(pDlg);
			}
		});
	}
	
	/*
	 * Runs the delete/purge operation via the progress dialog.
	 */
	private void runOpNow(final ProgressDlg pDlg) {
		// Perform the operation...
		if (ProgressDlg.needsChunking(m_totalEntityCount)) {
			// ...chunking as necessary.
			ProgressDlg.initAndShow(pDlg, new ProgressCallback() {
				@Override
				public void dialogReady() {
					dpeOpAsync(pDlg);
				}
				
				@Override
				public void operationCanceled() {
					// Mark the global indicating we've been canceled
					// (the operation will stop on the next chunk.)
					m_operationCanceled = true;
					m_dpeCallback.operationCanceled();
				}

				@Override
				public void operationComplete() {
					// Nothing to do.  We manage completion via
					// how we handle the items in the list.
				}
			},
			true,	// true -> Operation can be canceled.
			m_strMap.get(StringIds.PROGRESS_CAPTION),
			m_strMap.get(StringIds.PROGRESS_MESSAGE),
			m_totalEntityCount);
		}
		
		else {
			// ...without chunking.
			dpeOpAsync(null);
		}
	}
	
	/*
	 * Asynchronously deletes the selected entries.
	 */
	private static void trashAdHocPurgeRemoteSelectedEntitiesAsync(final List<EntityId> sourceEntityIds, final DeletePurgeEntriesCallback dpeCallback) {
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
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().deleteFolderEntriesError()           );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_DeleteSelections()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsDeleteSelectionsCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsDeleteSelectionsProgress());

				// ...create the helper...
				DeletePurgeEntriesHelper dpeHelper = new DeletePurgeEntriesHelper(
					sourceEntityIds,
					strMap,
					new DeleteSelectionsCmd(sourceEntityIds, DeleteSelectionsMode.TRASH_ADHOC_PURGE_OTHERS),
					dpeCallback);
				
				// ...and perform the delete.
				dpeHelper.runOpNow(pDlg);
			}
		});
	}
	
	/*
	 * Asynchronously deletes the selected entries.
	 */
	private static void trashSelectedEntitiesAsync(final List<EntityId> sourceEntityIds, final DeletePurgeEntriesCallback dpeCallback) {
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
				strMap.put(StringIds.ERROR_OP_FAILURE,  GwtTeaming.getMessages().deleteFolderEntriesError()           );
				strMap.put(StringIds.ERROR_RPC_FAILURE, GwtTeaming.getMessages().rpcFailure_DeleteSelections()        );
				strMap.put(StringIds.PROGRESS_CAPTION,  GwtTeaming.getMessages().binderViewsDeleteSelectionsCaption() );
				strMap.put(StringIds.PROGRESS_MESSAGE,  GwtTeaming.getMessages().binderViewsDeleteSelectionsProgress());

				// ...create the helper...
				DeletePurgeEntriesHelper dpeHelper = new DeletePurgeEntriesHelper(
					sourceEntityIds,
					strMap,
					new DeleteSelectionsCmd(sourceEntityIds, DeleteSelectionsMode.TRASH_ALL),
					dpeCallback);
				
				// ...and perform the delete.
				dpeHelper.runOpNow(pDlg);
			}
		});
	}
}
