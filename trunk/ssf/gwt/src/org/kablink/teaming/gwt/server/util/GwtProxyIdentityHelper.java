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
import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteProxyIdentitiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageProxyIdentitiesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProxyIdentityRpcResponseData;
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
	 * @param gwtPI
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ProxyIdentityRpcResponseData addNewProxyIdentity(AllModulesInjected bs, HttpServletRequest request, GwtProxyIdentity gwtPI) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtProxyIdentityHelper.addNewProxyIdentity()");
		try {
			ProxyIdentityRpcResponseData reply = new ProxyIdentityRpcResponseData();
			try {
				// Can we create the proxy identity?
				ProxyIdentity pi = convertGwtPIToPI(gwtPI);
				pi.setId(null);	// An add requires a null ID.
				bs.getProxyIdentityModule().addProxyIdentity(pi);
			}
			
			catch (Exception ex) {
				// No!  Add an error to the error list and log it.
				reply.addError(NLT.get("addNewProxyIdentityError.Exception", new String[]{gwtPI.getTitle(), ex.getMessage()}));
				GwtLogHelper.error(m_logger, "GwtProxyIdentityHelper.addNewProxyIdentity( Name:  '" + gwtPI.getTitle() + "', EXCEPTION ):  ", ex);
			}
			
			// If we get here, reply refers to a 
			// ProxyIdentityRpcResponseData containing any errors
			// we encountered.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Converts a domain ProxyIdentity to a GwtProxyIdentity.
	 */
	private static GwtProxyIdentity convertPIToGwtPI(ProxyIdentity pi) {
		GwtProxyIdentity reply = new GwtProxyIdentity();
		reply.setId(       pi.getId()       );
//		reply.setPassword( pi.getPassword() );
		reply.setProxyName(pi.getProxyName());
		reply.setTitle(    pi.getTitle()    );
		return reply;
	}
	
	/*
	 * Converts a GwtProxyIdentity to a domain ProxyIdentity.
	 */
	private static ProxyIdentity convertGwtPIToPI(GwtProxyIdentity gwtPI) {
		ProxyIdentity reply = new ProxyIdentity(gwtPI.getPassword(), gwtPI.getProxyName(), gwtPI.getTitle());
		reply.setId(gwtPI.getId());
		return reply;
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
	@SuppressWarnings("unchecked")
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
			
			// Are there any proxy identities to show?
			Map piMap = bs.getProxyIdentityModule().getProxyIdentities(options);
			List<ProxyIdentity> piList = (MiscUtil.hasItems(piMap) ? ((List<ProxyIdentity>) piMap.get(ObjectKeys.SEARCH_ENTRIES)) : null);
			if (!(MiscUtil.hasItems(piList))) {
				// No!  Return an empty row set.
				return GwtViewHelper.buildEmptyFolderRows(binder);
			}
			Long piTotal = ((Long) piMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));

			// Scan the proxy identity map.
			List<FolderRow> piRows = new ArrayList<FolderRow>();
			for (ProxyIdentity pi:  piList) {
				// Create the FolderRow for this proxy identity and add
				// it to the list. 
				EntityId  eid = new EntityId(binder.getId(), pi.getId(), EntityId.PROXY_IDENTITY);
				FolderRow fr  = new FolderRow(eid, folderColumns);
				piRows.add(fr);

				// Scan the columns.
				for (FolderColumn fc:  folderColumns) {
					// What proxy identity column is this?
					String cName = fc.getColumnName();
					if (FolderColumn.isColumnProxyName(cName)) {
						// Proxy Name!  Simply store the name.
						fr.setColumnValue(fc, pi.getProxyName());
					}
					
					else if (FolderColumn.isColumnProxyTitle(cName)) {
						// Proxy Title!  Convert the domain
						// ProxyIdentity to a GwtProxyIdentity and
						// store that.
						fr.setColumnValue(fc, convertPIToGwtPI(pi));
					}
				}
			}

			// Return a FolderRowsRpcResponseData containing the row
			// data.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					piRows,					// FolderRows.
					startIndex,				// Start index.
					piTotal.intValue(),		// Total count.
					TotalCountType.EXACT,	// How the total count should be interpreted.
					new ArrayList<Long>());	// Contributor IDs.
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// proxy identities.  Return it.
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
	 * Modifies an existing proxy identity.
	 *
	 * @param bs
	 * @param request
	 * @param gwtPI
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ProxyIdentityRpcResponseData modifyProxyIdentity(AllModulesInjected bs, HttpServletRequest request, GwtProxyIdentity gwtPI) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtProxyIdentityHelper.modifyProxyIdentity()");
		try {
			ProxyIdentityRpcResponseData reply = new ProxyIdentityRpcResponseData();
			try {
				// If the GwtProxyIdentity doesn't contain an ID...
				Long id = gwtPI.getId();
				if (null == id) {
					// ...tell the user about the problem and bail.
					reply.addError(NLT.get("modifyProxyIdentityError.MissingID", new String[]{gwtPI.getTitle()}));
					return reply;
				}

				// If we can't find an existing ProxyIdentity with
				// that ID...
				ProxyIdentityModule pim = bs.getProxyIdentityModule();
				ProxyIdentity pi = pim.getProxyIdentity(id);
				if (null == pi) {
					// ...tell the user about the problem and bail.
					reply.addError(NLT.get("modifyProxyIdentityError.NotFound", new String[]{String.valueOf(id)}));
					return reply;
				}
				
				// Can we modify the proxy identity?
				pi = convertGwtPIToPI(gwtPI);
				bs.getProxyIdentityModule().modifyProxyIdentity(pi);
			}
			
			catch (Exception ex) {
				// No!  Add an error to the error list and log it.
				reply.addError(NLT.get("modifyProxyIdentityError.Exception", new String[]{gwtPI.getTitle(), ex.getMessage()}));
				GwtLogHelper.error(m_logger, "GwtProxyIdentityHelper.modifyProxyIdentity( Name:  '" + gwtPI.getTitle() + "', EXCEPTION ):  ", ex);
			}
			
			// If we get here, reply refers to a 
			// ProxyIdentityRpcResponseData containing any errors
			// we encountered.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
}
