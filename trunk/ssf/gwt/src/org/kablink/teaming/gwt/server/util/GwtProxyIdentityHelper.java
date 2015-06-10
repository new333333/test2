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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.rpc.shared.CreateProxyIdentityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteProxyIdentitiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageProxyIdentitiesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.module.proxyidentity.ProxyIdentityModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code in dealing with proxy
 * identities.
 *
 * @author drfoster@novell.com
 */
public class GwtProxyIdentityHelper {
	protected static Log m_logger = LogFactory.getLog(GwtProxyIdentityHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtProxyIdentityHelper() {
		// Nothing to do.
	}
	
	/**
	 * Adds a new proxy identity.
	 *
	 * @param bs
	 * @param request
	 * @param title
	 * @param proxyName
	 * @param password
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CreateProxyIdentityRpcResponseData addNewProxyIdentity(AllModulesInjected bs, HttpServletRequest request, String title, String proxyName, String password) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtProxyIdentityHelper.addNewProxyIdentity()");
		try {
			CreateProxyIdentityRpcResponseData reply = new CreateProxyIdentityRpcResponseData();
			try {
				// Can we create the proxy identity?
				bs.getProxyIdentityModule().addProxyIdentity(new ProxyIdentity(password, proxyName, title));
			}
			
			catch (Exception ex) {
				// No!  Add an error to the error list and log it.
				reply.addError(NLT.get("addNewProxyIdentityError.Exception", new String[]{proxyName, ex.getMessage()}));
				GwtLogHelper.error(m_logger, "GwtProxyIdentityHelper.addNewProxyIdentity( Name:  '" + proxyName + "', EXCEPTION ):  ", ex);
			}
			
			// If we get here, reply refers to a
			// CreateProxyIdentityRpcResponseData containing any errors
			// we encountered.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Deletes the specified proxy identities.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DeleteProxyIdentitiesRpcResponseData deleteProxyIdentities(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		DeleteProxyIdentitiesRpcResponseData reply = new DeleteProxyIdentitiesRpcResponseData(new ArrayList<ErrorInfo>());
		deleteProxyIdentitiesImpl(bs, request, entityIds, reply);
		return reply;
	}
	
	private static void deleteProxyIdentitiesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteProxyIdentitiesRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtProxyIdentityHelper.deleteProxyIdentitiesImpl()");
		try {
			// Were we given any proxy identities to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them...
				ProxyIdentityModule pim = bs.getProxyIdentityModule();
				for (EntityId eid:  entityIds) {
					// ...deleting each.
					pim.deleteProxyIdentity(eid.getEntityId());
				}
				reply.setSuccessfulDeletes(entityIds);
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtProxyIdentityHelper.deleteProxyIdentitiesImpl( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a ManageProxyIdentitiesInfoRpcResponseData object
	 * containing the information for managing proxy identities.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageProxyIdentitiesInfoRpcResponseData getManageProxyIdentitiesInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageDevicesInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, bs.getWorkspaceModule().getTopWorkspaceId());
			if ((!(bi.getWorkspaceType().isTopWS())) && (!(bi.getWorkspaceType().isLandingPage()))) {
				GwtLogHelper.error(m_logger, "GwtProxyIdentityHelper.getManageProxyIdentitiesInformation():  The workspace type of the top workspace was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.TOP.name());
			}
			bi.setWorkspaceType(WorkspaceType.PROXY_IDENTITIES);
			ManageProxyIdentitiesInfoRpcResponseData reply = new ManageProxyIdentitiesInfoRpcResponseData(bi);

			// If we get here, reply refers to the
			// ManageProxyIdentitiesInfoRpcResponseData object
			// containing the information about managing proxy
			// identities.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtProxyIdentityHelper.getManageProxyIdentitiesInfo( SOURCE EXCEPTION ):  ");
		}		
	}

	/**
	 * Returns the ProxyIdentity identified by the given ID.
	 * 
	 * @param bs
	 * @param id
	 * 
	 * @return
	 */
	public static ProxyIdentity getProxyIdentity(AllModulesInjected bs, Long id) {
		return bs.getProxyIdentityModule().getProxyIdentity(id);
	}
	
	/**
	 * Returns the rows for the given proxy identities set.
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
	@SuppressWarnings({"unchecked", "unused"})
	public static FolderRowsRpcResponseData getProxyIdentityRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtProxyIdentityHelper.getProxyIdentityRows()");
		try {
			// If we were given a quick filter...
			if (MiscUtil.hasString(quickFilter)) {
				// ...add it to the options as that's were the DB query
				// ...expects to find it.
				options.put(ObjectKeys.SEARCH_QUICK_FILTER, quickFilter);
			}

			// Where are we starting the read from? 
			int startIndex = GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET, 0);
			
//!			...this needs to be implemented...
			return GwtViewHelper.buildEmptyFolderRows(binder);
		}
		
		finally {
			gsp.stop();
		}
	}
}
