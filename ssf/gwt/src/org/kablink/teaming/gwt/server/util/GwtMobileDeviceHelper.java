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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesViewSpec;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteMobileDevicesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.rpc.shared.ManageMobileDevicesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.module.mobiledevice.MobileDeviceModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code in dealing with mobile
 * devices.
 *
 * @author drfoster@novell.com
 */
public class GwtMobileDeviceHelper {
	protected static Log m_logger = LogFactory.getLog(GwtMobileDeviceHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtMobileDeviceHelper() {
		// Nothing to do.
	}
	
	/**
	 * Creates the requested number of dummy mobile devices for the
	 * given user.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * @param count
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData createDummyMobileDevices(AllModulesInjected bs, HttpServletRequest request, Long userId, int count) throws GwtTeamingException {
		try {
			MobileDeviceModule mdm          = bs.getMobileDeviceModule();
			ProfileModule      pm           = bs.getProfileModule();
			User               user         = ((User) pm.getEntry(userId));
			long               daysInMS     = (1000 * 60 * 60 * 24);
			Date               threeDaysAgo = new Date(System.currentTimeMillis() - (3 * daysInMS));
			for (int i = 0; i < count; i += 1) {
				if (0 < i) {
    				Thread.sleep(100);	// So the times change by at least 100ms.
				}
				Date         now    = new Date();
				String       nowStr = String.valueOf(now.getTime());
				MobileDevice md     = new MobileDevice(userId, nowStr);
				md.setWipeScheduled((0 == (i % 2))	);	// Marks every other one as having a wipe scheduled.
				if (0 == (i % 3)) {						// Every third one...
					md.setLastWipe(threeDaysAgo);		// ...has their last wipe set to 3 days ago.
				}
				md.setLastLogin(    now                                     );
				md.setDescription(  user.getTitle() + ":" + nowStr + ":" + i);
				mdm.addMobileDevice(md);
			}
				
			return new BooleanRpcResponseData(true);
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtMobileDeviceHelper.createDummyMobileDevices( SOURCE EXCEPTION ):  ");
		}		
	}

	/**
	 * Deletes the specified mobile devices.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DeleteMobileDevicesRpcResponseData deleteMobileDevices(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		DeleteMobileDevicesRpcResponseData reply = new DeleteMobileDevicesRpcResponseData(new ArrayList<ErrorInfo>());
		deleteMobileDevicesImpl(bs, request, entityIds, reply);
		return reply;
	}
	
	private static void deleteMobileDevicesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteMobileDevicesRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMobileDeviceHelper.deleteMobileDevicesImpl()");
		try {
			// Were we given any mobile devices to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them...
				MobileDeviceModule mdm = bs.getMobileDeviceModule();
				for (EntityId eid:  entityIds) {
					// ...deleting each.
					String mid    = eid.getMobileDeviceId();
					Long   userId = eid.getEntityId();
					mdm.deleteMobileDevice(userId, mid);
				}
				reply.setSuccessfulDeletes(entityIds);
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtMobileDeviceHelper.deleteMobileDevicesImpl( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a ManageMobileDevicesInfoRpcResponseData object
	 * containing the information for managing mobile devices.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageMobileDevicesInfoRpcResponseData getManageMobileDevicesInfo(AllModulesInjected bs, HttpServletRequest request, Long userId) throws GwtTeamingException {
		try {
			// Construct the ManageDevicesInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, bs.getProfileModule().getProfileBinderId());
			if (!(bi.getWorkspaceType().isProfileRoot())) {
				GwtLogHelper.error(m_logger, "GwtMobileDeviceHelper.getManageMobileDevicesInformation():  The workspace type of the profile root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.PROFILE_ROOT.name());
			}
			MobileDevicesViewSpec mdvSpec;
			if (null == userId)
			     mdvSpec = new MobileDevicesViewSpec(MobileDevicesViewSpec.Mode.SYSTEM      );
			else mdvSpec = new MobileDevicesViewSpec(MobileDevicesViewSpec.Mode.USER, userId);
			bi.setWorkspaceType(        WorkspaceType.MOBILE_DEVICES);
			bi.setMobileDevicesViewSpec(mdvSpec                     );
			ManageMobileDevicesInfoRpcResponseData reply = new ManageMobileDevicesInfoRpcResponseData(bi);

			// If we get here, reply refers to the
			// ManageMobileDevicesInfoRpcResponseData object
			// containing the information about managing mobile
			// devices.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtMobileDeviceHelper.getManageMobileDevicesInfo( SOURCE EXCEPTION ):  ");
		}		
	}

	/**
	 * Returns the MobileDevice identified by the given EntityId.
	 * 
	 * @param bs
	 * @param eid
	 * 
	 * @return
	 */
	public static MobileDevice getMobileDevice(AllModulesInjected bs, EntityId eid) {
		return bs.getMobileDeviceModule().getMobileDevice(GwtServerHelper.getCurrentUserId(), eid.getMobileDeviceId());
	}
	
	/**
	 * Returns the rows for the given mobile device set.
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
	public static FolderRowsRpcResponseData getMobileDeviceRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMobileDeviceHelper.getMobileDeviceRows()");
		try {
			// If we were given a quick filter...
			if (MiscUtil.hasString(quickFilter)) {
				// ...add it to the options as that's were the DB query
				// ...expects to find it.
				options.put(ObjectKeys.SEARCH_QUICK_FILTER, quickFilter);
			}

			// Where are we starting the read from? 
			int startIndex = GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET, 0);
			
			// Are we being asked for system wide or a per user mobile
			// device rows?
			MobileDeviceModule    mdm     = bs.getMobileDeviceModule();
			MobileDevicesViewSpec mdvSpec = bi.getMobileDevicesViewSpec();
			Map mdMap;
			if (mdvSpec.isSystem())
			     mdMap = mdm.getMobileDevices(                     options);	// System wide!
			else mdMap = mdm.getMobileDevices(mdvSpec.getUserId(), options);	// Per user!
			
			// Did we get any MobileDevice's?
			List<MobileDevice> mdList = (MiscUtil.hasItems(mdMap) ? ((List<MobileDevice>) mdMap.get(ObjectKeys.SEARCH_ENTRIES)) : null);
			if (!(MiscUtil.hasItems(mdList))) {
				// No!  Return an empty row set.
				return GwtViewHelper.buildEmptyFolderRows(binder);
			}
			Long mdTotal = ((Long) mdMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));

			// Resolve the user's we have devices for...
			List<Long> userIds = new ArrayList<Long>();
			for (MobileDevice md:  mdList) {
				ListUtil.addLongToListLongIfUnique(userIds, md.getUserId());
			}
			List<Principal> pList = ResolveIds.getPrincipals(userIds);
			
			// ...and track them by ID.
			Map<Long, User> userMap = new HashMap<Long, User>();
			for (Principal p:  pList) {
				userMap.put(p.getId(), ((User) p));
			}
			
			// Scan the user device map.
			List<FolderRow> deviceRows = new ArrayList<FolderRow>();
			for (MobileDevice md:  mdList) {
				// Create the FolderRow for this device and add
				// it to the list. 
				EntityId  eid = new EntityId(binder.getId(), md.getUserId(), EntityId.MOBILE_DEVICE, md.getDeviceId());
				FolderRow fr  = new FolderRow(eid, folderColumns);
				fr.setServerMobileDevice(md);
				deviceRows.add(fr);

				// Scan the columns.
				for (FolderColumn fc:  folderColumns) {
					// What mobile device column is this?
					String cName = fc.getColumnName();
					if (FolderColumn.isColumnDeviceDescription(cName)) {
						// Description!  Generate a
						// DescriptionHtml for it.
						String desc = md.getDescription();
						if (MiscUtil.hasString(desc)) {
							fr.setColumnValue(
								fc,
								new DescriptionHtml(
									desc,
									false));	// false -> Description is not HTML.
						}
					}
					
					else if (FolderColumn.isColumnDeviceLastLogin(cName)) {
						// Last login!  Generate a date/time
						// string for it.
						Date lastLogin = md.getLastLogin();
						if (null != lastLogin) {
							fr.setColumnValue(
								fc,
								GwtServerHelper.getDateTimeString(
									lastLogin,
									DateFormat.MEDIUM,
									DateFormat.SHORT));
						}
					}
					
					else if (FolderColumn.isColumnDeviceUser(cName)) {
						// User!  Generate a PrincipalInfo for
						// it...
						User user = userMap.get(md.getUserId());
						PrincipalInfo pi = GwtViewHelper.getPIFromUser(bs, request, user);
						
						// ...setup an appropriate GwtPresenceInfo...
						GwtPresenceInfo presenceInfo;
						if (GwtServerHelper.isPresenceEnabled())
						     presenceInfo = GwtServerHelper.getPresenceInfo(user);
						else presenceInfo = null;
						if (null == presenceInfo) {
							presenceInfo = GwtServerHelper.getPresenceInfoDefault();
						}
						if (null != presenceInfo) {
							pi.setPresence(presenceInfo);
							pi.setPresenceDude(GwtServerHelper.getPresenceDude(presenceInfo));
						}
						
						// ...and store it in the row.
						fr.setColumnValue(fc, pi);
					}
					
					else if (FolderColumn.isColumnDeviceWipeDate(cName)) {
						// Wipe date!  Generate a date/time
						// string for it.
						Date wipeDate = md.getLastWipe();
						if (null != wipeDate) {
							fr.setColumnValue(
								fc,
								GwtServerHelper.getDateTimeString(
									wipeDate,
									DateFormat.MEDIUM,
									DateFormat.SHORT));
						}
					}
					
					else if (FolderColumn.isColumnDeviceWipeScheduled(cName)) {
						// Wipe scheduled!  Generate a Boolean
						// wipe scheduled flag for it.
						Boolean wipeScheduled = md.getWipeScheduled();
						if (null == wipeScheduled) {
							wipeScheduled = Boolean.FALSE;
						}
						fr.setColumnWipeScheduled(
							fc,
							wipeScheduled);
					}
				}
			}

			// Return a FolderRowsRpcResponseData containing the row
			// data.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					deviceRows,				// FolderRows.
					startIndex,				// Start index.
					mdTotal.intValue(),		// Total count.
					TotalCountType.EXACT,	// How the total count should be interpreted.
					new ArrayList<Long>());	// Contributor IDs.
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// requested binder.  Return it.
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
	 * Sets the wipe scheduled state on a collection of devices.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * @param wipeScheduled
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveMobileDevicesWipeScheduledState(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, boolean wipeScheduled) throws GwtTeamingException {
		try {
			// Were we given any users/mobile devices to set the wipe
			// scheduled state on?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				MobileDeviceModule mdm = bs.getMobileDeviceModule();
				for (EntityId eid:  entityIds) {
					// Can we access this MobileDevice?
					String       mid    = eid.getMobileDeviceId();
					Long         userId = eid.getEntityId();
					MobileDevice md     = mdm.getMobileDevice(userId, mid);
					if (null != md) {
						// Yes!  Set it wipe scheduled flag...
						md.setWipeScheduled(wipeScheduled);
						
						// ...and write out the change.
						mdm.modifyMobileDevice(md);
					}
				}
			}
			
			// If we get here, we successfully set the wipe scheduled
			// state on the devices requested.  Return true.
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtMobileDeviceHelper.saveMobileDevicesWipeScheduledState( SOURCE EXCEPTION ):  ");
		}
	}
}
