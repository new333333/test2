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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.domain.MobileAppsConfig.MobileOpenInSetting;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileLinkAction;
import org.kablink.teaming.util.PrincipalDesktopAppsConfig;
import org.kablink.teaming.util.PrincipalMobileAppsConfig;
import org.kablink.teaming.util.Utils;

/**
 * Helper class dealing with various administrative functions.
 * 
 * @author drfoster@novell.com
 */
public class AdminHelper {
	protected static Log m_logger = LogFactory.getLog(AdminHelper.class);

	/**
	 * Enumeration used to specify where a file link is being used.
	 * 
	 * See the implementation of getEffectiveFileLinkAction() below.
	 */
	public enum FileLinkLocation {
		SEARCH_RESULTS,
		OTHER;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isSearchResults() {return SEARCH_RESULTS.equals(this);}
		public boolean isOther()         {return OTHER.equals(         this);}
	}
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private AdminHelper() {
		// Nothing to do.
	}

	/*
	 * Applies any settings from one PrincipalDesktopAppsConfig to
	 * another.
	 */
	private static void addPrincipalDACToPrincipalDAC(PrincipalDesktopAppsConfig target, PrincipalDesktopAppsConfig source) {
		if ((null != target) && (null != source) && (!(source.getUseDefaultSettings()))) {
			target.setUseDefaultSettings(false);
			target.setAllowCachePwd(       target.getAllowCachePwd()        || source.getAllowCachePwd()       );
			target.setIsFileSyncAppEnabled(target.getIsFileSyncAppEnabled() || source.getIsFileSyncAppEnabled());
		}
	}

	/*
	 * Applies any settings from one PrincipalMobileAppsConfig to
	 * another.
	 */
	private static void addPrincipalMACToPrincipalMAC(PrincipalMobileAppsConfig target, PrincipalMobileAppsConfig source) {
		if ((null != target) && (null != source) && (!(source.getUseDefaultSettings()))) {
			target.setUseDefaultSettings(false);
			target.setAllowCacheContent(     target.getAllowCacheContent()      || source.getAllowCacheContent()     );
			target.setAllowCachePwd(         target.getAllowCachePwd()          || source.getAllowCachePwd()         );
			target.setAllowPlayWithOtherApps(target.getAllowPlayWithOtherApps() || source.getAllowPlayWithOtherApps());
			target.setMobileAppsEnabled(     target.getMobileAppsEnabled()      || source.getMobileAppsEnabled()     );

			// Mobile Application Management (MAM) settings.
			target.setMobileCutCopyEnabled(                    target.getMobileCutCopyEnabled()                     || source.getMobileCutCopyEnabled()                    );
			target.setMobileAndroidScreenCaptureEnabled(       target.getMobileAndroidScreenCaptureEnabled()        || source.getMobileAndroidScreenCaptureEnabled()       );
			target.setMobileDisableOnRootedOrJailBrokenDevices(target.getMobileDisableOnRootedOrJailBrokenDevices() || source.getMobileDisableOnRootedOrJailBrokenDevices());
			
			MobileOpenInSetting targetMOI = target.getMobileOpenIn(); if (null == targetMOI) targetMOI = MobileOpenInSetting.ALL_APPLICATIONS;
			MobileOpenInSetting sourceMOI = source.getMobileOpenIn(); if (null == sourceMOI) sourceMOI = MobileOpenInSetting.ALL_APPLICATIONS;
			switch (targetMOI) {
			default:
			case ALL_APPLICATIONS:
				// 'All Applications' is is the highest level and we
				// stay with it.  Note the assignment here is to
				// cover the default case so that we use a KNOWN,
				// specific value.
				targetMOI = MobileOpenInSetting.ALL_APPLICATIONS;
				break;
				
			case DISABLED:
				switch (sourceMOI) {
				default:
				case ALL_APPLICATIONS:
					// Upgrade the 'Disabled' target to 'All
					// Applications'.
					targetMOI = MobileOpenInSetting.ALL_APPLICATIONS;
					break;
					
				case DISABLED:
					// Target stays 'Disabled'.
					break;
					
				case WHITE_LIST:
					// Upgrade the 'Disabled' target to 'White List',
					// using the white lists from the source.
					targetMOI = MobileOpenInSetting.WHITE_LIST;
					target.setAndroidApplications(source.getAndroidApplications());
					target.setIosApplications(    source.getIosApplications()    );
					break;
				}
				break;
				
			case WHITE_LIST:
				switch (sourceMOI) {
				default:
				case ALL_APPLICATIONS:
					// Upgrade the 'White List' target to 'All
					// Applications'.
					targetMOI = MobileOpenInSetting.ALL_APPLICATIONS;
					break;
					
				case DISABLED:
					// Target stays 'White List'.
					break;
					
				case WHITE_LIST:
					// Target stays 'White List', but we merge the
					// source's white lists into the target's.  First,
					// merge the Android applications...
					List<String> targetAppList = target.getAndroidApplications();
					if (null == targetAppList) {
						targetAppList = new ArrayList<String>();
						target.setAndroidApplications(targetAppList);
					}
					List<String> sourceAppList = source.getAndroidApplications();
					if (MiscUtil.hasItems(sourceAppList)) {
						for (String sourceApp:  sourceAppList) {
							ListUtil.addStringToListStringIfUniqueIgnoreCase(targetAppList, sourceApp);
						}
					}

					// ...then merge the iOS applications.
					targetAppList = target.getIosApplications();
					if (null == targetAppList) {
						targetAppList = new ArrayList<String>();
						target.setIosApplications(targetAppList);
					}
					sourceAppList = source.getIosApplications();
					if (MiscUtil.hasItems(sourceAppList)) {
						for (String sourceApp:  sourceAppList) {
							ListUtil.addStringToListStringIfUniqueIgnoreCase(targetAppList, sourceApp);
						}
					}
					break;
				}
				break;
			}
			
			// Finally, make sure the target contains the correct
			// MobileOpenInSetting. 
			target.setMobileOpenIn(targetMOI);
		}
	}

	/**
	 * Return the 'AdHoc folder' setting from the given user or group.
	 * (i.e., a UserPrinciapl object.)
	 * 
	 * @param bs
	 * @param upId
	 * 
	 * @return
	 */
	public static Boolean getAdhocFolderSettingFromUserOrGroup(AllModulesInjected bs, Long upId) {
		// Are we running Filr?
		if (Utils.checkIfFilr()) {
			// Yes!  If we have an ID...
			if (null != upId) {
				// ...read the 'allow AdHoc folder' setting from the
				// ...UserPrincipal. Did we find it?
				ProfileModule pm = bs.getProfileModule();
				Boolean reply = pm.getAdHocFoldersEnabled(upId);
				if (null == reply) {
					try {
						// No!  Assume upId is that of a user and look
						// in their properties.  Can we find it there?
						UserProperties userProperties = bs.getProfileModule().getUserProperties(upId);
						Object value = userProperties.getProperty(ObjectKeys.USER_PROPERTY_ALLOW_ADHOC_FOLDERS_DEPRECATED);
						if ((null != value) && (value instanceof String)) {
							// Yes!  Return that...
							reply = new Boolean((String) value);
							
							// ...store the value in the
							// ...UserPrinciapl...
							pm.setAdHocFoldersEnabled(upId, reply);
							
							// ...and remove it from the their
							// ...properties.
							pm.setUserProperty(upId, ObjectKeys.USER_PROPERTY_ALLOW_ADHOC_FOLDERS_DEPRECATED, null);
						}
					}
					catch (Exception e) {
						// Ignore.
					}
				}
				return reply;
			}
			return null;
		}
		
		else {
			// No, we're not running Filr!  AdHoc folders are always
			// allowed in Vibe.
			return Boolean.TRUE;
		}
	}

	/**
	 * Return the 'AdHoc folder' setting from the zone.
	 * 
	 * @param ami,
	 * 
	 * @return
	 */
	public static Boolean getAdhocFolderSettingFromZone(AllModulesInjected ami) {
		// If we're running Filr, we check the zone setting.
		// Otherwise, we simply return true.
		Boolean reply;
		if (Utils.checkIfFilr())
		     reply = new Boolean(ami.getAdminModule().isAdHocFoldersEnabled());
		else reply = Boolean.TRUE;
		return reply;
	}

	/**
	 * Return the effective 'AdHoc folder' setting from the given user.
	 * We will look in the user's properties first for a value.  If one
	 * is not found we will get the setting from the zone.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Boolean getEffectiveAdhocFolderSetting(AllModulesInjected bs, User user) {
		// Are we running Filr?
		Boolean reply;
		if (Utils.checkIfFilr()) {
			// Yes!  Do we have a user?  
			if (null !=  user) {
				// Yes!  Do they have an adHoc override?
				Long userId = user.getId();
				reply = getAdhocFolderSettingFromUserOrGroup(bs, userId);
				if (null == reply) {
					// No!  Is the user the member of any groups?
					List<Group> groups = GwtUIHelper.getGroups(userId);
					if (MiscUtil.hasItems(groups)) {
						// Yes!  Scan them.
						for (Group group:  groups) {
							// Does this group have an adHoc folder
							// override?
							Boolean gAdHoc = getAdhocFolderSettingFromUserOrGroup(bs, group.getId());
							if (null != gAdHoc) {
								// Yes!  Use it as the override and if
								// it's true...
								reply =  gAdHoc;
								if (reply) {
									// ...we're done looking.
									break;
								}
							}
						}
					}
				}
			}
			
			else {
				// No, we don't have a user!  There is no effective
				// setting.
				reply = null;
			}
		
			// Did we find a setting for the user?
			if (null == reply) {
				// No!  Read the global setting.
				reply = getAdhocFolderSettingFromZone(bs);
			}
		}
		
		else {
			// No, we aren't running Filr!  Vibe users always get adHoc
			// folders.
			reply = Boolean.TRUE;
		}

		// If we get here, reply contains true if AdHoc folders are
		// supported and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Return the effective 'Desktop Application Configuration'
	 * settings for the given user.  We will look in the User's
	 * properties first for a value.  If one is not found, we will look
	 * for the settings from the groups the user is a member of.  If
	 * one is still not found, we'll get the setting from the zone.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static PrincipalDesktopAppsConfig getEffectiveDesktopAppsConfigOverride(AllModulesInjected bs, User user) {
		PrincipalDesktopAppsConfig reply = null;
		
		// Does the user have a desktop applications override?
		Long                       userId = user.getId();
		ProfileModule              pm     = bs.getProfileModule();
		PrincipalDesktopAppsConfig pDAC   = pm.getPrincipalDesktopAppsConfig(userId);
		if ((null == pDAC) || pDAC.getUseDefaultSettings()) {
			// No!  Is the user the member of any groups?
			List<Group> groups = GwtUIHelper.getGroups(userId);
			if (MiscUtil.hasItems(groups)) {
				// Yes!  Scan them.
				for (Group group:  groups) {
					// Does this group have a desktop applications
					// override?
					pDAC = pm.getPrincipalDesktopAppsConfig(group.getId());
					if ((null != pDAC) && (!(pDAC.getUseDefaultSettings()))) {
						if (null == reply)
						     reply = pDAC;
						else addPrincipalDACToPrincipalDAC(reply, pDAC);
					}
				}
			}
		}
		
		else {
			// Yes, the user has a mobile applications override!
			// Factor it into the reply.
			reply = pDAC;
		}

		// If we don't have a PrincipalDesktopAppsConfig to return...
		if (null == reply) {
			// ...return one that indicates the system defaults are to
			// ...be used.
			reply = new PrincipalDesktopAppsConfig();
			reply.setUseDefaultSettings(true);
		}

		// If we get here, Refers to the effective PrincipalDesktopAppsConfig for
		// the user.  Return it.
		return reply;
	}
	
	/**
	 * Return the 'download' setting from the given user or group
	 * (i.e., a UserPrinciapl object.)
	 * 
	 * @param bs
	 * @param upId
	 * 
	 * @return
	 */
	public static Boolean getDownloadSettingFromUserOrGroup(final AllModulesInjected bs, final Long upId) {
		Boolean reply;
		if (Utils.checkIfFilr()) {
			// If we have a user ID...
			if (null != upId) {
				// ...read the 'download' setting from the
				// ...UserPrincipal object...
				return ((Boolean) RunasTemplate.runasAdmin(
					// Note that we run this as admin in case the
					// logged in user doesn't have rights to the group.
					new RunasCallback() {
						@Override
						public Object doAs() {
							return bs.getProfileModule().getDownloadEnabled(upId);
						}
					},
					RequestContextHolder.getRequestContext().getZoneName()));
			}
			reply = null;
		}
		else {
			reply = Boolean.TRUE;
		}
		return reply;
	}

	/**
	 * Return the 'Download' setting from the zone.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static Boolean getDownloadSettingFromZone(AllModulesInjected bs) {
		Boolean reply;
		if (Utils.checkIfFilr())
	         reply = new Boolean(bs.getAdminModule().isDownloadEnabled());
		else reply = Boolean.TRUE;
		return reply;
	}

	/**
	 * Return the effective 'Download' setting for the given user.
	 * We will look in the User object first for a value.  If one
	 * is not found we will or the settings from the groups the user
	 * is a member of.  If one is still not found, we'll get the
	 * setting from the zone.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Boolean getEffectiveDownloadSetting(AllModulesInjected bs, User user) {
		// Are we running Filr?
		Boolean reply;
		if (Utils.checkIfFilr()) {
			// Yes!  Do we have a user?  
			if (null !=  user) {
				// Yes!  Do they have a download override?
				Long userId = user.getId();
				reply = getDownloadSettingFromUserOrGroup(bs, user.getId());
				if (null == reply) {
					// No!  Is the user the member of any groups?
					List<Group> groups = GwtUIHelper.getGroups(userId);
					if (MiscUtil.hasItems(groups)) {
						// Yes!  Scan them.
						for (Group group:  groups) {
							// Does this group have a download
							// override?
							Boolean gDownload = getDownloadSettingFromUserOrGroup(bs, group.getId());
							if (null != gDownload) {
								// Yes!  Use it as the override and if
								// it's true...
								reply =  gDownload;
								if (reply) {
									// ...we're done looking.
									break;
								}
							}
						}
					}
			     }
			}
			
			else {
				// No, we don't have a user!  There is no effective
				// setting.
				reply = null;
			}
		
			// Did we find a setting for the user?
			if (null == reply) {
				// No!  Read the global setting.
				reply = getDownloadSettingFromZone(bs);
			}
		}
		
		else {
			// No, we aren't running Filr!  Vibe users can always
			// download.
			reply = Boolean.TRUE;
		}

		// If we get here, reply contains true if downloads are
		// enabled and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Return the effective 'File Link Action' setting from the given
	 * user.
	 * 
	 * If the user has a value stored in their preferences, that value
	 * will be validated against their effective download setting an the
	 * appropriate enumeration will be returned.
	 * 
	 * If the user does NOT have a value stored in their preferences,
	 * an appropriate default enumeration will be returned.
	 * 
	 * @param bs
	 * @param user
	 * @param fll
	 * 
	 * @return
	 */
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs, User user, FileLinkLocation fll) {
		// Is this Filr?
		FileLinkAction reply;
		if (Utils.checkIfFilr()) {
			// Yes!
			boolean        canDownload   = getEffectiveDownloadSetting(bs, user);
			FileLinkAction defaultFLA    = (canDownload ? FileLinkAction.DOWNLOAD : FileLinkAction.VIEW_HTML_ELSE_DETAILS);
			FileLinkAction calculatedFLA = null;
			if (!(user.isShared())) {
				UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
				String flaS = ((String) userProperties.getProperty(ObjectKeys.FILE_LINK_ACTION));
				if (!(MiscUtil.hasString(flaS))) {
					flaS = String.valueOf(FileLinkAction.DOWNLOAD.ordinal());
				}
				try {
					int flaI      = Integer.parseInt(      flaS);
					calculatedFLA = FileLinkAction.getEnum(flaI);
					if (!canDownload) {
						switch (calculatedFLA) {
						case DOWNLOAD:
						case VIEW_HTML_ELSE_DOWNLOAD:
							calculatedFLA = FileLinkAction.VIEW_HTML_ELSE_DETAILS;
							break;
							
						default:
							break;
						}
					}
				}
				catch (NumberFormatException nfe) {
					m_logger.warn("AdminHelper.getEffectiveFileLinkAction():  file link action is not an integer.", nfe);
				}
			}
			reply = ((null == calculatedFLA) ? defaultFLA : calculatedFLA);
		}
		
		else {
			// No, this isn't Filr!  For Vibe, we download from the
			// search results and view details from everywhere else.
			reply = 
				(fll.isSearchResults()      ?
					FileLinkAction.DOWNLOAD :
					FileLinkAction.VIEW_DETAILS);
		}

		// If we get here, reply contains the user's effective
		// FileLinkAction.  Return it.
		return reply;
	}
	
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs, User user) {
		// Always use the initial form of the method.
		return getEffectiveFileLinkAction(bs, user, FileLinkLocation.OTHER);
	}
	
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs, Long userId, FileLinkLocation fll) {
		// Always use the initial form of the method.
		return getEffectiveFileLinkAction(bs, GwtUIHelper.getUserSafely(bs.getProfileModule(), userId), fll);
	}
	
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs, Long userId) {
		// Always use the initial form of the method.
		return getEffectiveFileLinkAction(bs, GwtUIHelper.getUserSafely(bs.getProfileModule(), userId), FileLinkLocation.OTHER);
	}
	
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs, FileLinkLocation fll) {
		// Always use the initial form of the method.
		return getEffectiveFileLinkAction(bs, RequestContextHolder.getRequestContext().getUser(), fll);
	}
	
	public static FileLinkAction getEffectiveFileLinkAction(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getEffectiveFileLinkAction(bs, RequestContextHolder.getRequestContext().getUser(), FileLinkLocation.OTHER);
	}
	
	/**
	 * Return the effective 'Mobile Application Configuration' settings
	 * for the given user.  We will look in the User's properties first
	 * for a value.  If one is not found, we will look for the settings
	 * from the groups the user is a member of.  If one is still not
	 * found, we'll get the setting from the zone.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static PrincipalMobileAppsConfig getEffectiveMobileAppsConfigOverride(AllModulesInjected bs, User user) {
		PrincipalMobileAppsConfig reply = null;
		
		// Does the user have a mobile applications override?
		Long                      userId = user.getId();
		ProfileModule             pm     = bs.getProfileModule();
		PrincipalMobileAppsConfig pMAC   = pm.getPrincipalMobileAppsConfig(userId);
		if ((null == pMAC) || pMAC.getUseDefaultSettings()) {
			// No!  Is the user the member of any groups?
			List<Group> groups = GwtUIHelper.getGroups(userId);
			if (MiscUtil.hasItems(groups)) {
				// Yes!  Scan them.
				for (Group group:  groups) {
					// Does this group have a mobile applications
					// override?
					pMAC = pm.getPrincipalMobileAppsConfig(group.getId());
					if ((null != pMAC) && (!(pMAC.getUseDefaultSettings()))) {
						if (null == reply)
						     reply = pMAC;
						else addPrincipalMACToPrincipalMAC(reply, pMAC);
					}
				}
			}
		}
		
		else {
			// Yes, the user has a mobile applications override!
			// Factor it into the reply.
			reply = pMAC;
		}

		// If we don't have a PrincipalMobileAppsConfig to return...
		if (null == reply) {
			// ...return one that indicates the system defaults are to
			// ...be used.
			reply = new PrincipalMobileAppsConfig();
			reply.setUseDefaultSettings(true);
		}

		// If we get here, Refers to the effective PrincipalMobileAppsConfig for
		// the user.  Return it.
		return reply;
	}
	
	/**
	 * Return the effective 'Public Collection' setting for the given
	 * user.  We will look in the User object first for a value.  If
	 * one is not found we will or the settings from the groups the
	 * user is a member of.  If one is still not found, we'll get the
	 * setting from the zone.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Boolean getEffectivePublicCollectionSetting(AllModulesInjected bs, User user) {
		// Do we have a user?
		Boolean reply;
		if (null !=  user) {
			// Yes!  Is it Guest?
			if (user.isShared()) {
				// Yes!  Guest ALWAYS has a public collection.
				reply = Boolean.TRUE;
			}

			// No, it isn't Guest! Is it an external user?
			else if (!(user.getIdentityInfo().isInternal())) {
				// Yes!  External users NEVER have a public collection.
				reply = Boolean.FALSE;
			}
			
			else {
				// No!  Are there any public shares active?
				reply = bs.getSharingModule().arePublicSharesActive();
				if (reply) {
					// Yes!  Check whether the user has hidden their
					// public collection in their preferences.
					UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
					Boolean value = ((Boolean) userProperties.getProperty(ObjectKeys.HIDE_PUBLIC_COLLECTION));
					reply = ((null == value) || (!value));
				}
			}
		}
		
		else {
			// No, we don't have a user!  There is no effective
			// setting.
			reply = Boolean.FALSE;
		}
	
		// If we get here, reply contains true if the user see's a
		// public collection false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Return the effective 'WebAccess' setting for the given user.
	 * We will look in the User object first for a value.  If one
	 * is not found we will or the settings from the groups the user
	 * is a member of.  If one is still not found, we'll get the
	 * setting from the zone.
	 * 
	 * @param am
	 * @param pm
	 * @param user
	 * 
	 * @return
	 */
	public static Boolean getEffectiveWebAccessSetting(AdminModule am, ProfileModule pm, User user) {
		// Do we have a user?
		Boolean reply;
		if (null !=  user) {
			// Yes!  Is it the system admin?
			if (user.isSuper()) {
				// Yes!  The admin is ALWAYS allowed to use the web
				// access client.
				reply = Boolean.TRUE;
			}
			
			else {
				// No!  The user isn't the admin.  Does the user have a
				// web access override?
				Long userId = user.getId();
				reply = getWebAccessSettingFromUserOrGroup(pm, userId);
				if (null == reply) {
					// No!  Is the user the member of any groups?
					List<Group> groups = GwtUIHelper.getGroups(userId);
					if (MiscUtil.hasItems(groups)) {
						// Yes!  Scan them.
						for (Group group:  groups) {
							// Does this group have a web access
							// override?
							Boolean gAccess = getWebAccessSettingFromUserOrGroup(pm, group.getId());
							if (null != gAccess) {
								// Yes!  Use it as the override and if
								// it's true...
								reply =  gAccess;
								if (reply) {
									// ...we're done looking.
									break;
								}
							}
						}
					}
				}
			}
		}
		
		else {
			// No, we don't have a user!  There is no effective
			// setting.
			reply = null;
		}
	
		// Did we find a setting for the user?
		if (null == reply) {
			// No!  Read the global setting.
			reply = getWebAccessSettingFromZone(am);
		}

		// If we get here, reply contains true if web access is
		// enabled and false otherwise.  Return it.
		return reply;
	}
	
	public static Boolean getEffectiveWebAccessSetting(AllModulesInjected bs, User user) {
		// Always use the initial form of the method.
		return getEffectiveWebAccessSetting(bs.getAdminModule(), bs.getProfileModule(), user);
	}
	
	/**
	 * Return the 'web access' setting from the given user or group
	 * (i.e., UserPrincipal object.)
	 * 
	 * @param pm
	 * @param upId
	 * 
	 * @return
	 */
	public static Boolean getWebAccessSettingFromUserOrGroup(final ProfileModule pm, final Long upId) {
		// If we have a user ID...
		if (null != upId) {
			// ...read the 'web access' setting from the
			// ...UserPrincipal object...
			return ((Boolean) RunasTemplate.runasAdmin(
				// Note that we run this as admin in case the logged in
				// user doesn't have rights to the group.
				new RunasCallback() {
					@Override
					public Object doAs() {
						return pm.getWebAccessEnabled(upId);
					}
				},
				RequestContextHolder.getRequestContext().getZoneName()));
		}
		return null;
	}
	
	public static Boolean getWebAccessSettingFromUserOrGroup(AllModulesInjected bs, Long upId) {
		// Always use the initial form of the method.
		return getWebAccessSettingFromUserOrGroup(bs.getProfileModule(), upId);
	}

	/**
	 * Return the 'WebAccess' setting from the zone.
	 * 
	 * @param am
	 * 
	 * @return
	 */
	public static Boolean getWebAccessSettingFromZone(AdminModule am) {
	    return new Boolean(am.isWebAccessEnabled());
	}
	
	public static Boolean getWebAccessSettingFromZone(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getWebAccessSettingFromZone(bs.getAdminModule());
	}
}
