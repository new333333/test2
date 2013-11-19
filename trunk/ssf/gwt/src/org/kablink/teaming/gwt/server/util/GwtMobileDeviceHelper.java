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
package org.kablink.teaming.gwt.server.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.MobileDevices;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.MobileDevices.MobileDevice;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesView;
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesViewSpec;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageMobileDevicesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.AllModulesInjected;
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
	public static ErrorListRpcResponseData deleteMobileDevices(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		deleteMobileDevicesImpl(bs, request, entityIds, reply);
		return reply;
	}
	
	private static void deleteMobileDevicesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, ErrorListRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMobileDeviceHelper.deleteMobileDevicesImpl()");
		try {
			// Were we given any mobile devices to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				ProfileModule pm = bs.getProfileModule();
				for (EntityId eid:  entityIds) {
					// Can we access the MobileDevices for this user?
					String        mid    = eid.getMobileDeviceId();
					Long          userId = eid.getEntityId();
					MobileDevices mds    = pm.getMobileDevices(userId);
					if (null != mds) {
						// Yes!  Does it contain any MobileDevice's?
						List<MobileDevice> mdList = mds.getMobileDeviceList();
						if (MiscUtil.hasItems(mdList)) {
							// Yes!  Scan them.
							for (MobileDevice md:  mdList) {
								// Is this the device in question?
								if (md.getId().equalsIgnoreCase(mid)) {
									// Yes!  Remove it from the list...
									mdList.remove(md);
									
									// ...write out the change...
									mds.setMobileDevices(mdList);
									pm.setMobileDevices(userId, mds);
									
									// ...and break out of the loop.
									// ...We're done with this
									// ...user/mobile device.
									break;
								}
							}
						}
					}
				}
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

	/*
	 * Applies a quick filter to a List<FolderRow> of 'Mobile Device'
	 * rows.  A List<FolderRow> of the the FolderRow's from the input
	 * list that matches the filter is returned.
	 */
	public static List<FolderRow> filterMobileDeviceFolderRows(List<FolderColumn> folderColumns, List<FolderRow> folderRows, String quickFilter) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMobileDeviceHelper.filterMobileDeviceFolderRows()");
		try {
			// Do we have a string to filter with and some FolderRow's
			// to be filtered?
			if (null != quickFilter) {
				quickFilter = quickFilter.trim().toLowerCase();
			}
			if (MiscUtil.hasString(quickFilter) && MiscUtil.hasItems(folderRows)) {
				// Yes!  Yes!  Scan the rows.
				List<FolderRow> reply = new ArrayList<FolderRow>();
				for (FolderRow fr:  folderRows) {
					// Scan the columns.
					for (FolderColumn fc:  folderColumns) {
						// What column is this?
						String cName = fc.getColumnName();
						if (FolderColumn.isColumnDeviceUser(cName)) {
							// The user column!  If the user's title
							// contains the quick filter...
							PrincipalInfo pi = fr.getColumnValueAsPrincipalInfo(fc);
							if (null != pi) {
								if (GwtViewHelper.valueContainsQuickFilter(pi.getTitle(), quickFilter)) {
									// ...add it to the reply list.
									reply.add(fr);
									break;
								}
							}
						}
							
						else if (FolderColumn.isColumnDeviceDescription(cName)) {
							// A description column!  is there a value for it?
							DescriptionHtml dh = fr.getColumnValueAsDescriptionHtml(fc);
							if (null != dh) {
								if (GwtViewHelper.valueContainsQuickFilter(dh.getDescription(), quickFilter)) {
									// ...add it to the reply list.
									reply.add(fr);
									break;
								}
							}
						}
					}
				}
				folderRows = reply;
			}
			
			// If we get here, filterRows refers to the filtered list
			// of rows.  Return it. 
			return folderRows;
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
		// Can we get the MobileDevices for this user?
		MobileDevice reply = null;
		MobileDevices mds = bs.getProfileModule().getMobileDevices(eid.getEntityId());
		if (null != mds) {
			// Yes!  Does it contain any MobileDevice's?
			String mid = eid.getMobileDeviceId();
			List<MobileDevice> mdList = mds.getMobileDeviceList();
			if (MiscUtil.hasItems(mdList)) {
				// Yes!  Scan them.
				for (MobileDevice md:  mdList) {
					// Is this the mobile device in question?
					if (md.getId().equalsIgnoreCase(mid)) {
						// Yes!  Return it.
						reply = md;
						break;
					}
				}
			}
		}

		// If we get here, reply refers to the requested MobileDevice
		// if it was found and null otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns the rows for the given mobile device set.
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param quickFilter
	 * @param fdd
	 * @param folderColumns
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static FolderRowsRpcResponseData getMobileDeviceRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, FolderDisplayDataRpcResponseData fdd, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMobileDeviceHelper.getMobileDeviceRows()");
		try {
			// Are we being asked for system wide or a per user mobile
			// device rows?
			ProfileModule pm = bs.getProfileModule();
			Map<User, List<MobileDevice>> userDevicesMap = new HashMap<User, List<MobileDevice>>();
			MobileDevicesViewSpec mdvSpec = bi.getMobileDevicesViewSpec();
			if (mdvSpec.isSystem()) {
				// System wide!  Can we find any users with mobile
				// devices?
				Collection<User> usersWithDevices = pm.getAllUsersWithMobileDevices();
				if (MiscUtil.hasItems(usersWithDevices)) {
					// Yes!  Scan them.
					for (User user:  usersWithDevices) {
						// Does this user have a MobileDevices object?
						MobileDevices mds = user.getMobileDevices();
						if (null != mds) {
							// Yes!  Does that contain any mobile
							// devices?
							List<MobileDevice> mdList = mds.getMobileDeviceList();
							if (MiscUtil.hasItems(mdList)) {
								// Yes!  Add it to the user device map.
								userDevicesMap.put(user, mdList);
							}
							else {
								m_logger.error("GwtMobileDeviceHelper.getMobileDeviceRows( *Internal Error* ):  User '" + user.getTitle() + "' has no List<MobileDevice>.");
							}
						}
						else {
							m_logger.error("GwtMobileDeviceHelper.getMobileDeviceRows( *Internal Error* ):  User '" + user.getTitle() + "' has no MobileDevices.");
						}
					}
				}
			}
			
			else {
				// Per user!  Does this user have any devices defined?
				Long               userId    = mdvSpec.getUserId();
				User               user      = ((User) pm.getEntry(userId));
				MobileDevices      mds       = user.getMobileDevices();
				List<MobileDevice> mdList    = ((null == mds) ? null : mds.getMobileDeviceList());
				boolean            hasMDList = MiscUtil.hasItems(mdList);

				// If the user doesn't have any devices and we're set
				// to always show the user's mobile devices...
				if ((!hasMDList) && MobileDevicesView.ALWAYS_SHOW_MOBILE_DEVICES_USER) {
					// ...create a dummy one...
					Date         now    = new Date();
					String       nowStr = String.valueOf(now.getTime());
					MobileDevice md     = new MobileDevice();
					md.setWipeScheduled(true                          );
					md.setLastActivity( now                           );
					md.setLastLogin(    now                           );
					md.setId(           nowStr                        );
					md.setDescription(  user.getTitle() + ":" + nowStr);
					
					// ...add it to the MobileDevices object...
					if (null == mds) {
						mds = new MobileDevices();
					}
					mds.addMobileDevice(md);
					
					// ...store the MobileDevices object into the
					// ...User...
					pm.setMobileDevices(userId, mds);
					
					// ...and use the current list to populate the
					// ...rows.
					mdList    = mds.getMobileDeviceList();
					hasMDList = MiscUtil.hasItems(mdList);
				}

				// Do we have any devices for this user?
				if (hasMDList) {
					// Yes!  Track them in the user device map.
					userDevicesMap.put(user, mdList);
				}
				
				else {
					// No, we don't have any devices for this user!
					// Return an empty row set.
					return GwtViewHelper.buildEmptyFolderRows();
				}
			}
			
			// Scan the user device map.
			List<FolderRow> deviceRows = new ArrayList<FolderRow>();
			Set<User> users = userDevicesMap.keySet();
			for (User user:  users) {
				List<MobileDevice> mdList = userDevicesMap.get(user);
				if (MiscUtil.hasItems(mdList)) {
					for (MobileDevice md:  mdList) {
						// Create the FolderRow for this device and add
						// it to the list. 
						EntityId  eid = new EntityId(binder.getId(), user.getId(), EntityId.MOBILE_DEVICE, md.getId());
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
								Date wipeDate = md.getLastLogin();
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
								fr.setColumnWipeScheduled(
									fc,
									md.isWipeScheduled());
							}
						}
					}
				}
			}

			// If there are any rows and a quick filter...
			int devices = deviceRows.size();
			if ((0 < devices) && MiscUtil.hasString(quickFilter)) {
				// ...apply the filter.
				deviceRows = filterMobileDeviceFolderRows(
					folderColumns,
					deviceRows,
					quickFilter);
				devices = deviceRows.size();
			}
			
			// If there's more than one device...
			if (1 < devices) {
				// ...sort them using the defined criteria.
				Comparator<FolderRow> comparator =
					new FolderRowComparator(
						fdd.getFolderSortBy(),
						fdd.getFolderSortDescend(),
						folderColumns,
						FolderColumn.COLUMN_DEVICE_DESCRIPTION);
				
				Collections.sort(deviceRows, comparator);
			}
			
			// Return a FolderRowsRpcResponseData containing the row
			// data.
			return
				new FolderRowsRpcResponseData(
					deviceRows,				// FolderRows.
					0,						// Start index.
					devices,				// Total count.
					false,					// false -> Total is accurate.
					new ArrayList<Long>());	// Contributor IDs.
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
				ProfileModule pm = bs.getProfileModule();
				for (EntityId eid:  entityIds) {
					// Can we access the MobileDevices for this user?
					String        mid    = eid.getMobileDeviceId();
					Long          userId = eid.getEntityId();
					MobileDevices mds    = pm.getMobileDevices(userId);
					if (null != mds) {
						// Yes!  Does it contain any MobileDevice's?
						List<MobileDevice> mdList = mds.getMobileDeviceList();
						if (MiscUtil.hasItems(mdList)) {
							// Yes!  Scan them.
							for (MobileDevice md:  mdList) {
								// Is this the device in question?
								if (md.getId().equalsIgnoreCase(mid)) {
									// Yes!  Set it wipe scheduled flag...
									md.setWipeScheduled(wipeScheduled);
									
									// ...write out the change...
									mds.setMobileDevices(mdList);
									pm.setMobileDevices(userId, mds);
									
									// ...and break out of the loop.
									// ...We're done with this
									// ...user/mobile device.
									break;
								}
							}
						}
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
