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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetSelectionDetailsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper class for obtaining a SelectionDetails object.
 *
 * @author drfoster@novell.com
 */
public class SelectionDetailsHelper {
	/**
	 * Callback interface to return a SelectionDetails object. 
	 */
	public interface SelectionDetailsCallback {
		public void onFailure();
		public void onSuccess(SelectionDetails selectionDetails);
	}
	
	/*
	 * Constructor method. 
	 */
	private SelectionDetailsHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Uses the callback to return a SelectionDetails for the given
	 * List<EntityId>.
	 * 
	 * @param entityIds
	 * @param sdCallback
	 */
	public static void getSelectionDetails(final List<EntityId> entityIds, final SelectionDetailsCallback sdCallback) {
		// Can we get the SelectionDetails for the entities?
		GwtClientHelper.executeCommand(
				new GetSelectionDetailsCmd(entityIds),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem and call the
				// failure callback.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetSelectionDetails());
				sdCallback.onFailure();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes, we've got the SelectionDetails for the given
				// entities!  Return it through the callback.
				sdCallback.onSuccess((SelectionDetails) response.getResponseData());
			}
		});
	}
	
	public static void getSelectionDetails(EntityId entityId, SelectionDetailsCallback callback) {
		// Always use the initial form of the method.
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		getSelectionDetails(entityIds, callback);
	}
}
