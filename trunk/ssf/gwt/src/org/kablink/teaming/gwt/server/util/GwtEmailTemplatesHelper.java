/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteCustomizedEmailTemplatesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageEmailTemplatesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code in dealing with email
 * templates.
 *
 * @author drfoster@novell.com
 */
public class GwtEmailTemplatesHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEmailTemplatesHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtEmailTemplatesHelper() {
		// Nothing to do.
	}
	
	/**
	 * Deletes the specified customized email templates.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DeleteCustomizedEmailTemplatesRpcResponseData deleteCustomizedEmailTemplates(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		DeleteCustomizedEmailTemplatesRpcResponseData reply = new DeleteCustomizedEmailTemplatesRpcResponseData(new ArrayList<ErrorInfo>());
		deleteCustomizedEmailTemplatesImpl(bs, request, entityIds, reply);
		return reply;
	}

	@SuppressWarnings("unused")
	private static void deleteCustomizedEmailTemplatesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteCustomizedEmailTemplatesRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtEmailTemplatesHelper.deleteCustomizedEmailTemplatesImpl()");
		try {
			// Were we given any proxy identities to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				List<EntityId> successfulDeletes = new ArrayList<EntityId>();
				for (EntityId eid:  entityIds) {
//!					...this needs to be implemented...
				}
				reply.setSuccessfulDeletes(successfulDeletes);
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtEmailTemplatesHelper.deleteCustomizedEmailTemplatesImpl( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns the rows for the email templates view.
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param quickFilter
	 * @param options
	 * @param folderColumns
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getEmailTemplatesRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtEmailTemplatesHelper.getProxyIdentityRows()");
		try {
			// Return a FolderRowsRpcResponseData containing the row
			// data.
//!			...this needs to be implemented...
			FolderRowsRpcResponseData reply = GwtViewHelper.buildEmptyFolderRows(binder);
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// email templates.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtViewHelper.dumpFolderRowsRpcResponseData(m_logger, binder, reply);
			}
			
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a ManageEmailTemplatesInfoRpcResponseData object
	 * containing the information for managing email templates.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageEmailTemplatesInfoRpcResponseData getManageEmailTemplatesInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageDevicesInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, bs.getWorkspaceModule().getTopWorkspaceId());
			if ((!(bi.getWorkspaceType().isTopWS())) && (!(bi.getWorkspaceType().isLandingPage()))) {
				GwtLogHelper.error(m_logger, "GwtEmailTemplatesHelper.getManageEmailTemplatesInformation():  The workspace type of the top workspace was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.TOP.name());
			}
			bi.setWorkspaceType(WorkspaceType.EMAIL_TEMPLATES);
			ManageEmailTemplatesInfoRpcResponseData reply = new ManageEmailTemplatesInfoRpcResponseData(bi);

			// If we get here, reply refers to the
			// ManageEmailTemplatesInfoRpcResponseData object
			// containing the information about managing proxy
			// identities.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtEmailTemplatesHelper.getManageEmailTemplatesInfo( SOURCE EXCEPTION ):  ");
		}		
	}
}
