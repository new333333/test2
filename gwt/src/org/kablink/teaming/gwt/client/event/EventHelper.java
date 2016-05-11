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
package org.kablink.teaming.gwt.client.event;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderInfoHelper;
import org.kablink.teaming.gwt.client.util.BinderInfoHelper.BinderInfoCallback;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Helper methods to facilitate dealing with client side GWT events.
 * 
 * @author drfoster@novell.com
 */
public class EventHelper {
	/*
	 * Constructor method.
	 */
	private EventHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Given an event that requires no parameters, returns a
	 * VibeEventBase<?> of it.
	 * 
	 * @param eventEnum
	 * 
	 * @return
	 */
	public static VibeEventBase<?> createSimpleEvent(TeamingEvents eventEnum) {
		VibeEventBase<?> reply;
		
		switch (eventEnum) {
		case ADMINISTRATION:                    	reply = new AdministrationEvent();                break;
		case ADMINISTRATION_EXIT:               	reply = new AdministrationExitEvent();            break;
		case ADMINISTRATION_UPGRADE_CHECK:      	reply = new AdministrationUpgradeCheckEvent();    break;
		case BROWSE_HIERARCHY_EXIT:             	reply = new BrowseHierarchyExitEvent();           break;
		case CONTENT_CHANGED:						reply = new ContentChangedEvent();                break;
		case EDIT_CURRENT_BINDER_BRANDING:          reply = new EditCurrentBinderBrandingEvent();     break;
		case EDIT_LANDING_PAGE_PROPERTIES:			reply = new EditLandingPagePropertiesEvent();	  break;
		case EDIT_PERSONAL_PREFERENCES:         	reply = new EditPersonalPreferencesEvent();       break;
		case EDIT_SITE_BRANDING:                	reply = new EditSiteBrandingEvent();              break;
		case EDIT_MOBILE_SITE_BRANDING:             reply = new EditMobileSiteBrandingEvent();        break;
		case EDIT_DESKTOP_SITE_BRANDING:            reply = new EditDesktopSiteBrandingEvent();       break;
		case FULL_UI_RELOAD:                    	reply = new FullUIReloadEvent();                  break;
		case GOTO_MY_WORKSPACE:                 	reply = new GotoMyWorkspaceEvent();               break;
		case HIDE_MANAGE_MENU:                 	    reply = new HideManageMenuEvent();                break;
		case INVOKE_ABOUT:							reply = new InvokeAboutEvent();                   break;
		case INVOKE_ADD_NEW_PROXY_IDENTITITY:		reply = new InvokeAddNewProxyIdentityEvent();     break;
		case INVOKE_CLIPBOARD:						reply = new InvokeClipboardEvent();               break;
		case INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG:	reply = new InvokeConfigureAdhocFoldersDlgEvent();break;
		case INVOKE_CONFIGURE_ANTIVIRUS_DLG:		reply = new InvokeConfigureAntiVirusDlgEvent();   break;
		case INVOKE_CONFIGURE_COLUMNS:				reply = new InvokeConfigureColumnsEvent();        break;
		case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:	reply = new InvokeConfigureFileSyncAppDlgEvent(); break;
		case INVOKE_CONFIGURE_MOBILE_APPS_DLG:		reply = new InvokeConfigureMobileAppsDlgEvent();  break;
		case INVOKE_CONFIGURE_PASSWORD_POLICY_DLG:	reply = new InvokeConfigurePasswordPolicyDlgEvent();break;
		case INVOKE_CONFIGURE_SHARE_SETTINGS_DLG:	reply = new InvokeConfigureShareSettingsDlgEvent();break;
		case INVOKE_CONFIGURE_TELEMETRY_DLG:		reply = new InvokeConfigureTelemetryDlgEvent();   break;
		case INVOKE_CONFIGURE_UPDATE_LOGS_DLG:	    reply = new InvokeConfigureUpdateLogsDlgEvent();  break;
		case INVOKE_CONFIGURE_USER_ACCESS_DLG:		reply = new InvokeConfigureUserAccessDlgEvent();  break;
		case INVOKE_DEFAULT_USER_SETTINGS_DLG:		reply = new InvokeDefaultUserSettingsDlgEvent();  break;
		case INVOKE_DOWNLOAD_DESKTOP_APP:           reply = new InvokeDownloadDesktopAppEvent();      break;
		case INVOKE_EMAIL_NOTIFICATION:         	reply = new InvokeEmailNotificationEvent();       break;
		case INVOKE_HELP:                       	reply = new InvokeHelpEvent();                    break;
		case INVOKE_IDEAS_PORTAL:					reply = new InvokeIdeasPortalEvent();			  break;
		case INVOKE_IMPORT_PROFILES_DLG:			reply = new InvokeImportProfilesDlgEvent();		  break;
		case INVOKE_LIMIT_USER_VISIBILITY_DLG:		reply = new InvokeLimitUserVisibilityDlgEvent();  break;
		case INVOKE_NET_FOLDER_GLOBAL_SETTINGS_DLG:	reply = new InvokeNetFolderGlobalSettingsDlgEvent();break;
		case INVOKE_LDAP_SYNC_RESULTS_DLG:			reply = new InvokeLdapSyncResultsDlgEvent();	  break;
		case INVOKE_MANAGE_DATABASE_PRUNE_DLG:		reply = new InvokeManageDatabasePruneDlgEvent();  break;
		case INVOKE_MANAGE_EMAIL_TEMPLATES_DLG:		reply = new InvokeManageEmailTemplatesDlgEvent(); break;
		case INVOKE_MANAGE_NET_FOLDERS_DLG:			reply = new InvokeManageNetFoldersDlgEvent();	  break;
		case INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG:	reply = new InvokeManageNetFolderRootsDlgEvent(); break;
		case INVOKE_MANAGE_GROUPS_DLG:				reply = new InvokeManageGroupsDlgEvent();		  break;
		case INVOKE_MANAGE_MOBILE_DEVICES_DLG:		reply = new InvokeManageMobileDevicesDlgEvent();  break;
		case INVOKE_MANAGE_PROXY_IDENTITIES_DLG:	reply = new InvokeManageProxyIdentitiesDlgEvent();break;
		case INVOKE_NAME_COMPLETION_SETTINGS_DLG:	reply = new InvokeNameCompletionSettingsDlgEvent();break;
		case INVOKE_RENAME_ENTITY:				    reply = new InvokeRenameEntityEvent();		      break;
		case INVOKE_RUN_A_REPORT_DLG:				reply = new InvokeRunAReportDlgEvent();		      break;
		case LOGIN:                             	reply = new LoginEvent();                         break;
		case PRE_LOGOUT:                        	reply = new PreLogoutEvent();                     break;
		case PREVIEW_LANDING_PAGE:					reply = new PreviewLandingPageEvent();			  break;
		case MANAGE_DEFAULT_USER_SETTINGS:          reply = new ManageDefaultUserSettingsEvent();     break;
		case MANAGE_USER_VISIBILITY:                reply = new ManageUserVisibilityEvent();          break;
		case MASTHEAD_HIDE:                     	reply = new MastheadHideEvent();                  break;
		case MASTHEAD_SHOW:                     	reply = new MastheadShowEvent();                  break;
		case MASTHEAD_UNHIGHLIGHT_ALL_ACTIONS:     	reply = new MastheadUnhighlightAllActionsEvent(); break;
		case MENU_HIDE:								reply = new MenuHideEvent();					  break;
		case MENU_SHOW:								reply = new MenuShowEvent();					  break;
		case REFRESH_SIDEBAR_TREE:					reply = new RefreshSidebarTreeEvent();			  break;
		case REROOT_SIDEBAR_TREE:					reply = new RerootSidebarTreeEvent();			  break;
		case SEARCH_ADVANCED:                   	reply = new SearchAdvancedEvent();                break;
		case SHOW_CONTENT_CONTROL:                 	reply = new ShowContentControlEvent();            break;
		case SIDEBAR_HIDE:                      	reply = new SidebarHideEvent();                   break;
		case SIDEBAR_SHOW:                      	reply = new SidebarShowEvent();                   break;
		case SIZE_CHANGED:                      	reply = new SizeChangedEvent();                   break;
		case SHOW_VIEW_PERMALINKS:                  reply = new ShowViewPermalinksEvent();            break;
		case TASK_DELETE:                       	reply = new TaskDeleteEvent();                    break;
		case TASK_HIERARCHY_DISABLED:				reply = new TaskHierarchyDisabledEvent();         break;
		case TASK_MOVE_DOWN:                    	reply = new TaskMoveDownEvent();                  break;
		case TASK_MOVE_LEFT:                    	reply = new TaskMoveLeftEvent();                  break;
		case TASK_MOVE_RIGHT:                   	reply = new TaskMoveRightEvent();                 break;
		case TASK_MOVE_UP:                      	reply = new TaskMoveUpEvent();                    break;
		case TASK_NEW_TASK:                     	reply = new TaskNewTaskEvent();                   break;
		case TASK_PURGE:                        	reply = new TaskPurgeEvent();                     break;
		case TASK_SET_PERCENT_DONE:             	reply = new TaskSetPercentDoneEvent();            break;
		case TASK_SET_PRIORITY:                 	reply = new TaskSetPriorityEvent();               break;
		case TASK_SET_STATUS:                   	reply = new TaskSetStatusEvent();                 break;
		case TASK_VIEW:                         	reply = new TaskViewEvent();                      break;
		case TRACK_CURRENT_BINDER:              	reply = new TrackCurrentBinderEvent();            break;
		case UNTRACK_CURRENT_BINDER:            	reply = new UntrackCurrentBinderEvent();          break;
		case UNTRACK_CURRENT_PERSON:            	reply = new UntrackCurrentPersonEvent();          break;
		case VIEW_ALL_ENTRIES:                  	reply = new ViewAllEntriesEvent();                break;
		case VIEW_CURRENT_BINDER_TEAM_MEMBERS:  	reply = new ViewCurrentBinderTeamMembersEvent();  break;
		case VIEW_RESOURCE_LIBRARY:             	reply = new ViewResourceLibraryEvent();           break;
		case VIEW_TEAMING_FEED:                 	reply = new ViewTeamingFeedEvent();               break;
		case VIEW_UNREAD_ENTRIES:               	reply = new ViewUnreadEntriesEvent();             break;
		case VIEW_WHATS_NEW_IN_BINDER:				reply = new ViewWhatsNewInBinderEvent();          break;
		case VIEW_WHATS_UNSEEN_IN_BINDER:			reply = new ViewWhatsUnseenInBinderEvent();       break;
			
		default:
		case UNDEFINED:
			GwtClientHelper.deferredAlert(GwtTeaming.getMessages().eventHandling_NonSimpleEvent(eventEnum.name(), EventHelper.class.getName()));
			reply = null;
			break;
		}
		
		return reply;
	}
	
	/**
	 * Asynchronously fires a ChangeContextEvent.
	 * 
	 * @param binderId
	 * @param binderPermalink
	 * @param instigator
	 */
	public static void fireChangeContextEventAsync(final String binderId, final String binderPermalink, final Instigator instigator) {
		ScheduledCommand doChangeContext = new ScheduledCommand() {
			@Override
			public void execute() {
				fireChangeContextEventNow(binderId, binderPermalink, instigator);
			}
		};
		Scheduler.get().scheduleDeferred(doChangeContext);
	}
	
	/*
	 * Synchronously fires a ChangeContextEvent.
	 */
	private static void fireChangeContextEventNow(final String binderId, final String binderPermalink, final Instigator instigator) {
		BinderInfoHelper.getBinderInfo(binderId, new BinderInfoCallback() {
			@Override
			public void onFailure() {
				// Nothing to do!  The user will already have been
				// told about the problem.
			}

			@Override
			public void onSuccess(BinderInfo binderInfo) {
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
					binderInfo,
					binderPermalink,
					instigator);
				if (GwtClientHelper.validateOSBI(osbInfo)) {
					GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
				}
			}
		});
	}
	
	/**
	 * Asynchronously fires a ContextChangedEvent.
	 * 
	 * @param contextBinderId
	 * @param binderPermalink
	 * @param instigator
	 */
	public static void fireContextChangedEventAsync(final String contextBinderId, final String binderPermalink, final Instigator instigator) {
		ScheduledCommand doContextChanged = new ScheduledCommand() {
			@Override
			public void execute() {
				fireContextChangedEventNow(contextBinderId, binderPermalink, instigator);
			}
		};
		Scheduler.get().scheduleDeferred(doContextChanged);
	}
	
	/*
	 * Synchronously fires a ContextChangedEvent.
	 */
	private static void fireContextChangedEventNow(final String binderId, final String binderPermalink, final Instigator instigator) {
		BinderInfoHelper.getBinderInfo(binderId, new BinderInfoCallback() {
			@Override
			public void onFailure() {
				// Nothing to do!  The user will already have been
				// told about the problem.
			}

			@Override
			public void onSuccess(BinderInfo binderInfo) {
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
					binderInfo,
					binderPermalink,
					instigator);
				if (GwtClientHelper.validateOSBI(osbInfo)) {
					GwtTeaming.fireEvent(new ContextChangedEvent(osbInfo));
				}
			}
		});
	}
	
	/*
	 * Returns true of an event is an an array of events and false
	 * otherwise.
	 */
	private static boolean isTEInA(TeamingEvents event, TeamingEvents[] events) {
		int c = ((null == events) ? 0 : events.length);
		for (int i = 0; i < c; i += 1) {
			if (event.ordinal() == events[i].ordinal()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Given an event that requires no parameters, fires one.
	 * 
	 * @param eventEnum
	 */
	public static void fireSimpleEvent(TeamingEvents eventEnum) {
		VibeEventBase<?> event = createSimpleEvent(eventEnum);
		if (null != event) {
			GwtTeaming.fireEvent(event);
		}
	}
	
	/**
	 * Registers event handlers on the event bus
	 * 
	 * Note:  This method must be maintained to support ALL the events
	 *    defined in TeamingEvent.java.
	 * 
	 * @param eventBus
	 * @param registerdEvents
	 * @param eventHandler
	 * @param registeredEventHandlers	Optional.  May be null.
	 * @param doValidation
	 */
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler, List<HandlerRegistration> registeredEventHandlers, boolean doValidation) {
		// If we supposed to validate the handlers...
		if (doValidation) {
			// ...validate what's being asked for vs. what the object
			// ...is defined to support.
			validateEvents(eventsToBeRegistered, eventHandler);
		}
		
		// Scan the events we were given to register.
		boolean returnRegisteredEventHandlers = (null != registeredEventHandlers);
		int events = ((null == eventsToBeRegistered) ? 0 : eventsToBeRegistered.length);
		for (int i = 0; i < events; i += 1) {
			// Which event is this?
			boolean handlerNotDefined = true;
			HandlerRegistration registrationHandler = null;
			TeamingEvents te = eventsToBeRegistered[i];
			switch (te) {
			case ACCESS_TO_ITEM_DENIED:
				// An AccessToItemDeniedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof AccessToItemDeniedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = AccessToItemDeniedEvent.registerEvent( eventBus, ((AccessToItemDeniedEvent.Handler) eventHandler) );
				}
				break;
			
			case ACTIVITY_STREAM:
				// An ActivityStreamEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ActivityStreamEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamEvent.registerEvent(eventBus, ((ActivityStreamEvent.Handler) eventHandler));
				}
				break;
				
			case ACTIVITY_STREAM_ENTER:
				// An ActivityStreamEnterEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ActivityStreamEnterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamEnterEvent.registerEvent(eventBus, ((ActivityStreamEnterEvent.Handler) eventHandler));
				}
				break;
				
			case ACTIVITY_STREAM_EXIT:
				// An ActivityStreamExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ActivityStreamExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamExitEvent.registerEvent(eventBus, ((ActivityStreamExitEvent.Handler) eventHandler));
				}
				break;
				
			case ACTIVITY_STREAM_COMMENT_DELETED:
				// An ActivityStreamCommentDeletedEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ActivityStreamCommentDeletedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamCommentDeletedEvent.registerEvent(eventBus, ((ActivityStreamCommentDeletedEvent.Handler) eventHandler));
				}
				break;
				
			case ADD_PRINCIPAL_ADMIN_RIGHTS:
				// An AddPrincipalAdminRightsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof AddPrincipalAdminRightsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AddPrincipalAdminRightsEvent.registerEvent(eventBus, ((AddPrincipalAdminRightsEvent.Handler) eventHandler));
				}
				break;
				
			case ADMINISTRATION:
				// An AdministrationEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof AdministrationEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationEvent.registerEvent(eventBus, ((AdministrationEvent.Handler) eventHandler));
				}
				break;
				
			case ADMINISTRATION_ACTION:
				// An AdministrationActionEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof AdministrationActionEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationActionEvent.registerEvent(eventBus, ((AdministrationActionEvent.Handler) eventHandler));
				}
				break;
				
			case ADMINISTRATION_EXIT:
				// An AdministrationExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof AdministrationExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationExitEvent.registerEvent(eventBus, ((AdministrationExitEvent.Handler) eventHandler));
				}
				break;
			
			case ADMINISTRATION_UPGRADE_CHECK:
				// An AdministrationUpgradeCheckEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof AdministrationUpgradeCheckEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationUpgradeCheckEvent.registerEvent(eventBus, ((AdministrationUpgradeCheckEvent.Handler) eventHandler));
				}
				break;
			
			case BLOG_ARCHIVE_FOLDER_SELECTED:
				// A BlogArchiveFolderSelectedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof BlogArchiveFolderSelectedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = BlogArchiveFolderSelectedEvent.registerEvent( eventBus, ((BlogArchiveFolderSelectedEvent.Handler) eventHandler) );
				}
				break;
			
			case BLOG_ARCHIVE_MONTH_SELECTED:
				// A BlogArchiveMonthSelectedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof BlogArchiveMonthSelectedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = BlogArchiveMonthSelectedEvent.registerEvent( eventBus, ((BlogArchiveMonthSelectedEvent.Handler) eventHandler) );
				}
				break;
			
			case BLOG_GLOBAL_TAG_SELECTED:
				// A BlogGlobalTagSelectedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof BlogGlobalTagSelectedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = BlogGlobalTagSelectedEvent.registerEvent( eventBus, ((BlogGlobalTagSelectedEvent.Handler) eventHandler) );
				}
				break;
			
			case BLOG_PAGE_CREATED:
				// A BlogPageCreatedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof BlogPageCreatedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = BlogPageCreatedEvent.registerEvent( eventBus, ((BlogPageCreatedEvent.Handler) eventHandler) );
				}
				break;
			
			case BLOG_PAGE_SELECTED:
				// A BlogPageSelectedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof BlogPageSelectedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = BlogPageSelectedEvent.registerEvent( eventBus, ((BlogPageSelectedEvent.Handler) eventHandler) );
				}
				break;
			
			case BROWSE_HIERARCHY:
				// An BrowseHierarchyEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof BrowseHierarchyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyEvent.registerEvent(eventBus, ((BrowseHierarchyEvent.Handler) eventHandler));
				}
				break;
			
			case BROWSE_HIERARCHY_EXIT:
				// An BrowseHierarchyExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof BrowseHierarchyExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyExitEvent.registerEvent(eventBus, ((BrowseHierarchyExitEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_CHANGED:
				// A CalendarChangedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarChangedEvent.registerEvent(eventBus, ((CalendarChangedEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_GOTO_DATE:
				// A CalendarGotoDateEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarGotoDateEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarGotoDateEvent.registerEvent(eventBus, ((CalendarGotoDateEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_HOURS:
				// A CalendarHoursEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof CalendarHoursEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarHoursEvent.registerEvent(eventBus, ((CalendarHoursEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_NEXT_PERIOD:
				// A CalendarNextPeriodEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarNextPeriodEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarNextPeriodEvent.registerEvent(eventBus, ((CalendarNextPeriodEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_PREVIOUS_PERIOD:
				// A CalendarPreviousPeriodEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarPreviousPeriodEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarPreviousPeriodEvent.registerEvent(eventBus, ((CalendarPreviousPeriodEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_SETTINGS:
				// A CalendarSettingsEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof CalendarSettingsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarSettingsEvent.registerEvent(eventBus, ((CalendarSettingsEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_SHOW:
				// A CalendarShowEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof CalendarShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarShowEvent.registerEvent(eventBus, ((CalendarShowEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_SHOW_HINT:
				// A CalendarShowHintEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarShowHintEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarShowHintEvent.registerEvent(eventBus, ((CalendarShowHintEvent.Handler) eventHandler));
				}
				break;
			
			case CALENDAR_VIEW_DAYS:
				// A CalendarViewDaysEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof CalendarViewDaysEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CalendarViewDaysEvent.registerEvent(eventBus, ((CalendarViewDaysEvent.Handler) eventHandler));
				}
				break;
			
			case CHANGE_CONTEXT:
				// A ChangeContextEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof ChangeContextEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ChangeContextEvent.registerEvent(eventBus, ((ChangeContextEvent.Handler) eventHandler));
				}
				break;
			
			case CHANGE_ENTRY_TYPE_SELECTED_ENTITIES:
				// A ChangeEntryTypeSelectedEntitiesEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof ChangeEntryTypeSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ChangeEntryTypeSelectedEntitiesEvent.registerEvent(eventBus, ((ChangeEntryTypeSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case CHANGE_FAVORITE_STATE:
				// A ChangeFavoriteStateEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ChangeFavoriteStateEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ChangeFavoriteStateEvent.registerEvent(eventBus, ((ChangeFavoriteStateEvent.Handler) eventHandler));
				}
				break;
			
			case CHECK_MANAGE_DLG_ACTIVE:
				// An CheckManageDlgActiveEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof CheckManageDlgActiveEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CheckManageDlgActiveEvent.registerEvent(eventBus, ((CheckManageDlgActiveEvent.Handler) eventHandler));
				}
				break;
				
			case CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES:
				// A ClearScheduledWipeSelectedMobileDevicesEvent!  Can
				// the event handler we were given handle that?
				if (eventHandler instanceof ClearScheduledWipeSelectedMobileDevicesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ClearScheduledWipeSelectedMobileDevicesEvent.registerEvent(eventBus, ((ClearScheduledWipeSelectedMobileDevicesEvent.Handler) eventHandler));
				}
				break;
			
			case CLEAR_SELECTED_USERS_ADHOC_FOLDERS:
				// A ClearSelectedUsersAdHocFoldersEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof ClearSelectedUsersAdHocFoldersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ClearSelectedUsersAdHocFoldersEvent.registerEvent(eventBus, ((ClearSelectedUsersAdHocFoldersEvent.Handler) eventHandler));
				}
				break;
			
			case CLEAR_SELECTED_USERS_DOWNLOAD:
				// A ClearSelectedUsersDownloadEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ClearSelectedUsersDownloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ClearSelectedUsersDownloadEvent.registerEvent(eventBus, ((ClearSelectedUsersDownloadEvent.Handler) eventHandler));
				}
				break;
			
			case CLEAR_SELECTED_USERS_WEBACCESS:
				// A ClearSelectedUsersWebAccessEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ClearSelectedUsersWebAccessEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ClearSelectedUsersWebAccessEvent.registerEvent(eventBus, ((ClearSelectedUsersWebAccessEvent.Handler) eventHandler));
				}
				break;
			
			case CONTENT_CHANGED:
				// A ContentChangedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ContentChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContentChangedEvent.registerEvent(eventBus, ((ContentChangedEvent.Handler) eventHandler));
				}
				break;
			
			case CONTEXT_CHANGED:
				// A ContextChangedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ContextChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContextChangedEvent.registerEvent(eventBus, ((ContextChangedEvent.Handler) eventHandler));
				}
				break;
			
			case CONTEXT_CHANGING:
				// A ContextChangingEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ContextChangingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContextChangingEvent.registerEvent(eventBus, ((ContextChangingEvent.Handler) eventHandler));
				}
				break;
			
			case CONTRIBUTOR_IDS_REPLY:
				// A ContributorIdsReplyEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ContributorIdsReplyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContributorIdsReplyEvent.registerEvent(eventBus, ((ContributorIdsReplyEvent.Handler) eventHandler));
				}
				break;
			
			case CONTRIBUTOR_IDS_REQUEST:
				// A ContributorIdsRequestEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ContributorIdsRequestEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContributorIdsRequestEvent.registerEvent(eventBus, ((ContributorIdsRequestEvent.Handler) eventHandler));
				}
				break;
			
			case COPY_PUBLIC_LINK_SELECTED_ENTITIES:
				// A CopyPublicLinkSelectedEntitiesEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof CopyPublicLinkSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CopyPublicLinkSelectedEntitiesEvent.registerEvent(eventBus, ((CopyPublicLinkSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case COPY_SELECTED_ENTITIES:
				// A CopySelectedEntitiesEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof CopySelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = CopySelectedEntitiesEvent.registerEvent(eventBus, ((CopySelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case DELETE_ACTIVITY_STREAM_UI_ENTRY:
				// A DeleteActivityStreamUIEntryEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DeleteActivityStreamUIEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteActivityStreamUIEntryEvent.registerEvent(eventBus, ((DeleteActivityStreamUIEntryEvent.Handler) eventHandler));
				}
				break;
				
			case DELETE_SELECTED_CUSTOMIZED_EMAIL_TEMPLATES:
				// A DeleteSelectedCustomizedEmailTemplatesEvent!  Can
				// the event handler we were given handle that?
				if (eventHandler instanceof DeleteSelectedCustomizedEmailTemplatesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteSelectedCustomizedEmailTemplatesEvent.registerEvent(eventBus, ((DeleteSelectedCustomizedEmailTemplatesEvent.Handler) eventHandler));
				}
				break;
			
			case DELETE_SELECTED_ENTITIES:
				// A DeleteSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DeleteSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteSelectedEntitiesEvent.registerEvent(eventBus, ((DeleteSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case DELETE_SELECTED_MOBILE_DEVICES:
				// A DeleteSelectedMobileDevicesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DeleteSelectedMobileDevicesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteSelectedMobileDevicesEvent.registerEvent(eventBus, ((DeleteSelectedMobileDevicesEvent.Handler) eventHandler));
				}
				break;
			
			case DELETE_SELECTED_PROXY_IDENTITIES:
				// A DeleteSelectedProxyIdentitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DeleteSelectedProxyIdentitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteSelectedProxyIdentitiesEvent.registerEvent(eventBus, ((DeleteSelectedProxyIdentitiesEvent.Handler) eventHandler));
				}
				break;
			
			case DELETE_SELECTED_USERS:
				// A DeleteSelectedUsersEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof DeleteSelectedUsersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DeleteSelectedUsersEvent.registerEvent(eventBus, ((DeleteSelectedUsersEvent.Handler) eventHandler));
				}
				break;
			
			case DIALOG_CLOSED:
				// A DialogClosedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof DialogClosedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DialogClosedEvent.registerEvent(eventBus, ((DialogClosedEvent.Handler) eventHandler));
				}
				break;
			
			case DISABLE_SELECTED_USERS:
				// A DisableSelectedUsersEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof DisableSelectedUsersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DisableSelectedUsersEvent.registerEvent(eventBus, ((DisableSelectedUsersEvent.Handler) eventHandler));
				}
				break;
			
			case DISABLE_SELECTED_USERS_ADHOC_FOLDERS:
				// A DisableSelectedUsersAdHocFoldersEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof DisableSelectedUsersAdHocFoldersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DisableSelectedUsersAdHocFoldersEvent.registerEvent(eventBus, ((DisableSelectedUsersAdHocFoldersEvent.Handler) eventHandler));
				}
				break;
			
			case DISABLE_SELECTED_USERS_DOWNLOAD:
				// A DisableSelectedUsersDownloadEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DisableSelectedUsersDownloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DisableSelectedUsersDownloadEvent.registerEvent(eventBus, ((DisableSelectedUsersDownloadEvent.Handler) eventHandler));
				}
				break;
			
			case DISABLE_SELECTED_USERS_WEBACCESS:
				// A DisableSelectedUsersWebAccessEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DisableSelectedUsersWebAccessEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DisableSelectedUsersWebAccessEvent.registerEvent(eventBus, ((DisableSelectedUsersWebAccessEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_CURRENT_BINDER_BRANDING:
				// A EditCurrentBinderBrandingEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditCurrentBinderBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditCurrentBinderBrandingEvent.registerEvent(eventBus, ((EditCurrentBinderBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_LANDING_PAGE_PROPERTIES:
				// A EditLandingPagePropertiesEvent.  Can the event handler we were given handle that?
				if ( eventHandler instanceof EditLandingPagePropertiesEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = EditLandingPagePropertiesEvent.registerEvent( eventBus, ((EditLandingPagePropertiesEvent.Handler) eventHandler) );
				}
				break;
			
			case EDIT_PERSONAL_PREFERENCES:
				// A EditPersonalPreferencesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditPersonalPreferencesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditPersonalPreferencesEvent.registerEvent(eventBus, ((EditPersonalPreferencesEvent.Handler) eventHandler));
				}
				break;
			
			case DOWNLOAD_FOLDER_AS_CSV_FILE:
				// A DownloadFolderAsCSVFileEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof DownloadFolderAsCSVFileEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = DownloadFolderAsCSVFileEvent.registerEvent(eventBus, ((DownloadFolderAsCSVFileEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_ACTIVITY_STREAM_UI_ENTRY:
				// A EditActivityStreamUIEntryEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditActivityStreamUIEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditActivityStreamUIEntryEvent.registerEvent(eventBus, ((EditActivityStreamUIEntryEvent.Handler) eventHandler));
				}
				break;
				
			case EDIT_PUBLIC_LINK_SELECTED_ENTITIES:
				// A EditPublicLinkSelectedEntitiesEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof EditPublicLinkSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditPublicLinkSelectedEntitiesEvent.registerEvent(eventBus, ((EditPublicLinkSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_SITE_BRANDING:
				// A EditSiteBrandingEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof EditSiteBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditSiteBrandingEvent.registerEvent(eventBus, ((EditSiteBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_MOBILE_SITE_BRANDING:
				// A EditMobileSiteBrandingEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditMobileSiteBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditMobileSiteBrandingEvent.registerEvent(eventBus, ((EditMobileSiteBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_DESKTOP_SITE_BRANDING:
				// A EditDesktopSiteBrandingEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditDesktopSiteBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditDesktopSiteBrandingEvent.registerEvent(eventBus, ((EditDesktopSiteBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case EMAIL_PUBLIC_LINK_SELECTED_ENTITIES:
				// A EmailPublicLinkSelectedEntitiesEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof EmailPublicLinkSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EmailPublicLinkSelectedEntitiesEvent.registerEvent(eventBus, ((EmailPublicLinkSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case ENABLE_SELECTED_USERS:
				// A EnableSelectedUsersEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof EnableSelectedUsersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EnableSelectedUsersEvent.registerEvent(eventBus, ((EnableSelectedUsersEvent.Handler) eventHandler));
				}
				break;
			
			case ENABLE_SELECTED_USERS_ADHOC_FOLDERS:
				// A EnableSelectedUsersAdHocFoldersEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof EnableSelectedUsersAdHocFoldersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EnableSelectedUsersAdHocFoldersEvent.registerEvent(eventBus, ((EnableSelectedUsersAdHocFoldersEvent.Handler) eventHandler));
				}
				break;
			
			case ENABLE_SELECTED_USERS_DOWNLOAD:
				// A EnableSelectedUsersDownloadEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EnableSelectedUsersDownloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EnableSelectedUsersDownloadEvent.registerEvent(eventBus, ((EnableSelectedUsersDownloadEvent.Handler) eventHandler));
				}
				break;
			
			case ENABLE_SELECTED_USERS_WEBACCESS:
				// A EnableSelectedUsersWebAccessEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EnableSelectedUsersWebAccessEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EnableSelectedUsersWebAccessEvent.registerEvent(eventBus, ((EnableSelectedUsersWebAccessEvent.Handler) eventHandler));
				}
				break;
			
			case FILES_DROPPED:
				// An FilesDroppedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof FilesDroppedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = FilesDroppedEvent.registerEvent(eventBus, ((FilesDroppedEvent.Handler) eventHandler));
				}
				break;
				
			case FIND_CONTROL_BROWSE:
				// An FindControlBrowseEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof FindControlBrowseEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = FindControlBrowseEvent.registerEvent(eventBus, ((FindControlBrowseEvent.Handler) eventHandler));
				}
				break;
				
			case FOLDER_ENTRY_ACTION_COMPLETE:
				// An FolderEntryActionCompleteEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof FolderEntryActionCompleteEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = FolderEntryActionCompleteEvent.registerEvent(eventBus, ((FolderEntryActionCompleteEvent.Handler) eventHandler));
				}
				break;
				
			case FORCE_FILES_UNLOCK:
				// An ForceFilesUnlockEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ForceFilesUnlockEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ForceFilesUnlockEvent.registerEvent(eventBus, ((ForceFilesUnlockEvent.Handler) eventHandler));
				}
				break;
				
			case FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD:
				// An ForceSelectedUsersToChangePasswordEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof ForceSelectedUsersToChangePasswordEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ForceSelectedUsersToChangePasswordEvent.registerEvent(eventBus, ((ForceSelectedUsersToChangePasswordEvent.Handler) eventHandler));
				}
				break;
				
			case FULL_UI_RELOAD:
				// An FullUIReloadEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof FullUIReloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = FullUIReloadEvent.registerEvent(eventBus, ((FullUIReloadEvent.Handler) eventHandler));
				}
				break;
				
			case GET_CURRENT_VIEW_INFO:
				// An GetCurrentViewInfoEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof GetCurrentViewInfoEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GetCurrentViewInfoEvent.registerEvent(eventBus, ((GetCurrentViewInfoEvent.Handler) eventHandler));
				}
				break;
				
			case GET_MANAGE_MENU_POPUP:
				// An GetManageMenuPopupEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof GetManageMenuPopupEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GetManageMenuPopupEvent.registerEvent(eventBus, ((GetManageMenuPopupEvent.Handler) eventHandler));
				}
				break;
				
			case GET_MANAGE_TITLE:
				// An GetManageTitleEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GetManageTitleEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GetManageTitleEvent.registerEvent(eventBus, ((GetManageTitleEvent.Handler) eventHandler));
				}
				break;
				
			case GET_MASTHEAD_LEFT_EDGE:
				// An GetMastHeadLeftEdgeEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof GetMastHeadLeftEdgeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GetMastHeadLeftEdgeEvent.registerEvent(eventBus, ((GetMastHeadLeftEdgeEvent.Handler) eventHandler));
				}
				break;
				
			case GET_SIDEBAR_COLLECTION:
				// An GetSidebarCollectionEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof GetSidebarCollectionEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GetSidebarCollectionEvent.registerEvent(eventBus, ((GetSidebarCollectionEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_CONTENT_URL:
				// An GotoContentUrlEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoContentUrlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoContentUrlEvent.registerEvent(eventBus, ((GotoContentUrlEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_URL:
				// An GotoUrlEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof GotoUrlEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GotoUrlEvent.registerEvent( eventBus, ((GotoUrlEvent.Handler) eventHandler) );
				}
				break;
				
			case GOTO_MY_WORKSPACE:
				// An GotoMyWorkspaceEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoMyWorkspaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoMyWorkspaceEvent.registerEvent(eventBus, ((GotoMyWorkspaceEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_PERMALINK_URL:
				// An GotoPermalinkUrlEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoPermalinkUrlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoPermalinkUrlEvent.registerEvent(eventBus, ((GotoPermalinkUrlEvent.Handler) eventHandler));
				}
				break;
				
			case GROUP_CREATED:
				// A GroupCreatedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupCreatedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupCreatedEvent.registerEvent(
																	eventBus,
																	((GroupCreatedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MEMBERSHIP_MODIFICATION_FAILED:
				// A GroupMembershipModificationFailedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupMembershipModificationFailedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupMembershipModificationFailedEvent.registerEvent(
																			eventBus,
																			((GroupMembershipModificationFailedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MEMBERSHIP_MODIFICATION_STARTED:
				// A GroupMembershipModificationStartedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupMembershipModificationStartedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupMembershipModificationStartedEvent.registerEvent(
																			eventBus,
																			((GroupMembershipModificationStartedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MEMBERSHIP_MODIFIED:
				// A GroupMembershipModifiedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupMembershipModifiedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupMembershipModifiedEvent.registerEvent(
																			eventBus,
																			((GroupMembershipModifiedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFICATION_FAILED:
				// A GroupModificationFailedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModificationFailedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModificationFailedEvent.registerEvent(
																			eventBus,
																			((GroupModificationFailedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFICATION_STARTED:
				// A GroupModificationStartedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModificationStartedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModificationStartedEvent.registerEvent(
																			eventBus,
																			((GroupModificationStartedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFIED:
				// A GroupModifiedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModifiedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModifiedEvent.registerEvent( eventBus, ((GroupModifiedEvent.Handler) eventHandler ) );
				}
				break;
				
			case HIDE_ACCESSORIES:
				// A HideAccessoriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof HideAccessoriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = HideAccessoriesEvent.registerEvent(eventBus, ((HideAccessoriesEvent.Handler) eventHandler) );
				}
				break;
			
			case HIDE_HTML_ELEMENT:
				// A HideHtmlElementEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof HideHtmlElementEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = HideHtmlElementEvent.registerEvent(eventBus, ((HideHtmlElementEvent.Handler) eventHandler) );
				}
				break;
			
			case HIDE_MANAGE_MENU:
				// A HideManageMenuEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof HideManageMenuEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = HideManageMenuEvent.registerEvent(eventBus, ((HideManageMenuEvent.Handler) eventHandler) );
				}
				break;
			
			case HIDE_SELECTED_SHARES:
				// A HideSelectedSharesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof HideSelectedSharesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = HideSelectedSharesEvent.registerEvent(eventBus, ((HideSelectedSharesEvent.Handler) eventHandler));
				}
				break;
			
			case HIDE_USER_LIST:
				// A HideUserListEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof HideUserListEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = HideUserListEvent.registerEvent(eventBus, ((HideUserListEvent.Handler) eventHandler) );
				}
				break;
			
			case INVOKE_ABOUT:
				// An InvokeAboutEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeAboutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeAboutEvent.registerEvent(eventBus, ((InvokeAboutEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_ADD_NEW_FOLDER:
				// An InvokeAddNewFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeAddNewFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeAddNewFolderEvent.registerEvent(eventBus, ((InvokeAddNewFolderEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_ADD_NEW_PROXY_IDENTITITY:
				// An InvokeAddNewProxyIdentityEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeAddNewProxyIdentityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeAddNewProxyIdentityEvent.registerEvent(eventBus, ((InvokeAddNewProxyIdentityEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_BINDER_SHARE_RIGHTS_DLG:
				// An InvokeBinderShareRightsDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeBinderShareRightsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeBinderShareRightsDlgEvent.registerEvent(eventBus, ((InvokeBinderShareRightsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CHANGE_PASSWORD_DLG:
				// An InvokeChangePasswordDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeChangePasswordDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeChangePasswordDlgEvent.registerEvent( eventBus, ((InvokeChangePasswordDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CLIPBOARD:
				// An InvokeClipboardEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof InvokeClipboardEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeClipboardEvent.registerEvent(eventBus, ((InvokeClipboardEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_COLUMN_RESIZER:
				// An InvokeColumnResizerEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeColumnResizerEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeColumnResizerEvent.registerEvent(eventBus, ((InvokeColumnResizerEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG:
				// An InvokeConfigureAdhocFoldersDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureAdhocFoldersDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureAdhocFoldersDlgEvent.registerEvent( eventBus, ((InvokeConfigureAdhocFoldersDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_ANTIVIRUS_DLG:
				// An InvokeConfigureAntiVirusDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeConfigureAntiVirusDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureAntiVirusDlgEvent.registerEvent(eventBus, ((InvokeConfigureAntiVirusDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_COLUMNS:
				// An InvokeConfigureColumnsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeConfigureColumnsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureColumnsEvent.registerEvent(eventBus, ((InvokeConfigureColumnsEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:
				// An InvokeConfigureFileSyncAppDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureFileSyncAppDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureFileSyncAppDlgEvent.registerEvent( eventBus, ((InvokeConfigureFileSyncAppDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_MOBILE_APPS_DLG:
				// An InvokeConfigureMobileAppsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureMobileAppsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureMobileAppsDlgEvent.registerEvent( eventBus, ((InvokeConfigureMobileAppsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_PASSWORD_POLICY_DLG:
				// An InvokeConfigurePasswordPolicyDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokeConfigurePasswordPolicyDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeConfigurePasswordPolicyDlgEvent.registerEvent(eventBus, ((InvokeConfigurePasswordPolicyDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_SHARE_SETTINGS_DLG:
				// An InvokeConfigureShareSettingsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureShareSettingsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureShareSettingsDlgEvent.registerEvent( eventBus, ((InvokeConfigureShareSettingsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_TELEMETRY_DLG:
				// An InvokeConfigureTelemetryDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeConfigureTelemetryDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureTelemetryDlgEvent.registerEvent(eventBus, ((InvokeConfigureTelemetryDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_UPDATE_LOGS_DLG:
				// An InvokeConfigureUpdateLogsDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeConfigureUpdateLogsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureUpdateLogsDlgEvent.registerEvent(eventBus, ((InvokeConfigureUpdateLogsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_CONFIGURE_USER_ACCESS_DLG:
				// An InvokeConfigureUserAccessDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureUserAccessDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureUserAccessDlgEvent.registerEvent( eventBus, ((InvokeConfigureUserAccessDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_COPY_FILTERS_DLG:
				// An InvokeCopyFiltersDlgEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeCopyFiltersDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeCopyFiltersDlgEvent.registerEvent(eventBus, ((InvokeCopyFiltersDlgEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_DEFAULT_USER_SETTINGS_DLG:
				// An InvokeDefaultUserSettingsDlgEvent!  Can the event
				// handler we were given handle that?
				if ( eventHandler instanceof InvokeDefaultUserSettingsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeDefaultUserSettingsDlgEvent.registerEvent( eventBus, ((InvokeDefaultUserSettingsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_DOWNLOAD_DESKTOP_APP:
				// An InvokeDownloadDesktopAppEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeDownloadDesktopAppEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeDownloadDesktopAppEvent.registerEvent(eventBus, ((InvokeDownloadDesktopAppEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_DROPBOX:
				// An InvokeDropBoxEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof InvokeDropBoxEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeDropBoxEvent.registerEvent(eventBus, ((InvokeDropBoxEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_EDIT_IN_PLACE:
				// An InvokeEditInPlaceEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof InvokeEditInPlaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeEditInPlaceEvent.registerEvent(eventBus, ((InvokeEditInPlaceEvent.Handler) eventHandler));
				}
				break;

			case INVOKE_EDIT_KEYSHIELD_CONFIG_DLG:
				// An InvokeEditKeyShieldConfigDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditKeyShieldConfigDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditKeyShieldConfigDlgEvent.registerEvent(
																				eventBus,
																				((InvokeEditKeyShieldConfigDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_EDIT_LDAP_CONFIG_DLG:
				// An InvokeEditLdapConfigDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditLdapConfigDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditLdapConfigDlgEvent.registerEvent(
																				eventBus,
																				((InvokeEditLdapConfigDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_EDIT_NET_FOLDER_DLG:
				// An InvokeEditNetFolderDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditNetFolderDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditNetFolderDlgEvent.registerEvent(
																				eventBus,
																				((InvokeEditNetFolderDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_EDIT_NET_FOLDER_RIGHTS_DLG:
				// An InvokeEditNetFolderRightsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditNetFolderRightsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditNetFolderRightsDlgEvent.registerEvent(
																					eventBus,
																					((InvokeEditNetFolderRightsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_EDIT_SHARE_RIGHTS_DLG:
				// An InvokeEditShareRightsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditShareRightsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditShareRightsDlgEvent.registerEvent(
																					eventBus,
																					((InvokeEditShareRightsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_EDIT_USER_ZONE_SHARE_RIGHTS_DLG:
				// An InvokeEditUserZoneShareRightsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeEditUserZoneShareRightsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeEditUserZoneShareRightsDlgEvent.registerEvent(
																					eventBus,
																					((InvokeEditUserZoneShareRightsDlgEvent.Handler) eventHandler));
				}
				break;
		
			case INVOKE_EMAIL_NOTIFICATION:
				// An InvokeEmailNotificationEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeEmailNotificationEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeEmailNotificationEvent.registerEvent(eventBus, ((InvokeEmailNotificationEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_HELP:
				// An InvokeHelpEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeHelpEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeHelpEvent.registerEvent(eventBus, ((InvokeHelpEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_IDEAS_PORTAL:
				// An InvokeIdeasPortalEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeIdeasPortalEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeIdeasPortalEvent.registerEvent(eventBus, ((InvokeIdeasPortalEvent.Handler) eventHandler));
				}
				break;				
				
			case INVOKE_IMPORT_ICAL_FILE:
				// An InvokeImportIcalFileEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeImportIcalFileEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeImportIcalFileEvent.registerEvent(eventBus, ((InvokeImportIcalFileEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_IMPORT_ICAL_URL:
				// An InvokeImportIcalUrlEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeImportIcalUrlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeImportIcalUrlEvent.registerEvent(eventBus, ((InvokeImportIcalUrlEvent.Handler) eventHandler));
				}
				break;

			case INVOKE_IMPORT_PROFILES_DLG:
				// An InvokeImportProfilesDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeImportProfilesDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeImportProfilesDlgEvent.registerEvent(eventBus, ((InvokeImportProfilesDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_LIMIT_USER_VISIBILITY_DLG:
				// An InvokeLimitUserVisibilityDlgEvent!  Can the event
				// handler we were given handle that?
				if ( eventHandler instanceof InvokeLimitUserVisibilityDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeLimitUserVisibilityDlgEvent.registerEvent( eventBus, ((InvokeLimitUserVisibilityDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_NET_FOLDER_GLOBAL_SETTINGS_DLG:
				// An InvokeNetFolderGlobalSettingsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeNetFolderGlobalSettingsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeNetFolderGlobalSettingsDlgEvent.registerEvent(
																					eventBus,
																					((InvokeNetFolderGlobalSettingsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_LDAP_SYNC_RESULTS_DLG:
				// An InvokeLdapSyncResultsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeLdapSyncResultsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeLdapSyncResultsDlgEvent.registerEvent(
																					eventBus,
																					((InvokeLdapSyncResultsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_ADMINISTRATORS_DLG:
				// An InvokeManageAdministratorsDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokeManageAdministratorsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageAdministratorsDlgEvent.registerEvent( eventBus, ((InvokeManageAdministratorsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_DATABASE_PRUNE_DLG:
				// An InvokeManageDatabasePruneDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeManageDatabasePruneDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeManageDatabasePruneDlgEvent.registerEvent( eventBus, ((InvokeManageDatabasePruneDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_EMAIL_TEMPLATES_DLG:
				// An InvokeManageEmailTemplatesDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokeManageEmailTemplatesDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageEmailTemplatesDlgEvent.registerEvent(eventBus, ((InvokeManageEmailTemplatesDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_NET_FOLDERS_DLG:
				// An InvokeManageNetFoldersDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeManageNetFoldersDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeManageNetFoldersDlgEvent.registerEvent(
																					eventBus,
																					((InvokeManageNetFoldersDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG:
				// An InvokeManageNetFolderRootsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeManageNetFolderRootsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeManageNetFolderRootsDlgEvent.registerEvent(
																						eventBus,
																						((InvokeManageNetFolderRootsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_GROUPS_DLG:
				// An InvokeManageGroupsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeManageGroupsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeManageGroupsDlgEvent.registerEvent( eventBus, ((InvokeManageGroupsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_TEAMS_DLG:
				// An InvokeManageTeamsDlgEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeManageTeamsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageTeamsDlgEvent.registerEvent( eventBus, ((InvokeManageTeamsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_USERS_DLG:
				// An InvokeManageUsersDlgEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeManageUsersDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageUsersDlgEvent.registerEvent(eventBus, ((InvokeManageUsersDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_MOBILE_DEVICES_DLG:
				// An InvokeManageMobileDevicesDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeManageMobileDevicesDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageMobileDevicesDlgEvent.registerEvent(eventBus, ((InvokeManageMobileDevicesDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_PROXY_IDENTITIES_DLG:
				// An InvokeManageProxyIdentitiesDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokeManageProxyIdentitiesDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeManageProxyIdentitiesDlgEvent.registerEvent(eventBus, ((InvokeManageProxyIdentitiesDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_NAME_COMPLETION_SETTINGS_DLG:
				// An InvokeNameCompletionSettingsDlgEvent!  Can the event handler
				// we were given handle that?
				if ( eventHandler instanceof InvokeNameCompletionSettingsDlgEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = InvokeNameCompletionSettingsDlgEvent.registerEvent( eventBus, ((InvokeNameCompletionSettingsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_PRINCIPAL_DESKTOP_SETTINGS_DLG:
				// An InvokePrincipalDesktopSettingsDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokePrincipalDesktopSettingsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokePrincipalDesktopSettingsDlgEvent.registerEvent(eventBus, ((InvokePrincipalDesktopSettingsDlgEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_PRINCIPAL_MOBILE_SETTINGS_DLG:
				// An InvokePrincipalMobileSettingsDlgEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof InvokePrincipalMobileSettingsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokePrincipalMobileSettingsDlgEvent.registerEvent(eventBus, ((InvokePrincipalMobileSettingsDlgEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_RENAME_ENTITY:
				// An InvokeRenameEntityEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeRenameEntityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeRenameEntityEvent.registerEvent(eventBus, ((InvokeRenameEntityEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_REPLY:
				// An InvokeReplyEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeReplyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeReplyEvent.registerEvent(eventBus, ((InvokeReplyEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_RUN_A_REPORT_DLG:
				// An InvokeRunAReportDlgEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeRunAReportDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeRunAReportDlgEvent.registerEvent(eventBus, ((InvokeRunAReportDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_SEND_EMAIL_TO_TEAM:
				// An InvokeSendEmailToTeamEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeSendEmailToTeamEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSendEmailToTeamEvent.registerEvent(eventBus, ((InvokeSendEmailToTeamEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SEND_TO_FRIEND:
				// An InvokeSendToFriendEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeSendToFriendEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = InvokeSendToFriendEvent.registerEvent( eventBus, ((InvokeSendToFriendEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SHARE:
				// An InvokeShareEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeShareEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeShareEvent.registerEvent(eventBus, ((InvokeShareEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SHARE_BINDER:
				// An InvokeShareBinderEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeShareBinderEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = InvokeShareBinderEvent.registerEvent( eventBus, ((InvokeShareBinderEvent.Handler) eventHandler) );
				}
				break;
				
			case INVOKE_SIGN_GUESTBOOK:
				// An InvokeSignGuestbookEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeSignGuestbookEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSignGuestbookEvent.registerEvent(eventBus, ((InvokeSignGuestbookEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SIMPLE_PROFILE:
				// An InvokeSimpleProfileEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeSimpleProfileEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSimpleProfileEvent.registerEvent(eventBus, ((InvokeSimpleProfileEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SUBSCRIBE:
				// An InvokeSubscribeEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof InvokeSubscribeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSubscribeEvent.registerEvent(eventBus, ((InvokeSubscribeEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_TAG:
				// An InvokeTagEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeTagEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeTagEvent.registerEvent(eventBus, ((InvokeTagEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_USER_PROPERTIES_DLG:
				// An InvokeUserPropertiesDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeUserPropertiesDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeUserPropertiesDlgEvent.registerEvent(eventBus, ((InvokeUserPropertiesDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_USER_SHARE_RIGHTS_DLG:
				// An InvokeUserShareRightsDlgEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeUserShareRightsDlgEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeUserShareRightsDlgEvent.registerEvent(eventBus, ((InvokeUserShareRightsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_WORKSPACE_SHARE_RIGHTS:
				// An InvokeWorkspaceShareRightsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof InvokeWorkspaceShareRightsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeWorkspaceShareRightsEvent.registerEvent(eventBus, ((InvokeWorkspaceShareRightsEvent.Handler) eventHandler));
				}
				break;
			
			case JSP_LAYOUT_CHANGED:
				// An JspLayoutChangedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof JspLayoutChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = JspLayoutChangedEvent.registerEvent(eventBus, ((JspLayoutChangedEvent.Handler) eventHandler));
				}
				break;
				
			case LDAP_SYNC_STATUS:
				// A LdapSyncStatusEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof LdapSyncStatusEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = LdapSyncStatusEvent.registerEvent(
																			eventBus,
																			((LdapSyncStatusEvent.Handler) eventHandler ) );
				}
				break;
				
			case LOCK_SELECTED_ENTITIES:
				// A LockSelectedEntitiesEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof LockSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LockSelectedEntitiesEvent.registerEvent(eventBus, ((LockSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case LOGIN:
				// An LoginEvent!  Can the event handler we were given
				// handle that?
				if (eventHandler instanceof LoginEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LoginEvent.registerEvent(eventBus, ((LoginEvent.Handler) eventHandler));
				}
				break;
				
			case LOGOUT:
				// An LogoutEvent!  Can the event handler we were given
				// handle that?
				if (eventHandler instanceof LogoutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LogoutEvent.registerEvent(eventBus, ((LogoutEvent.Handler) eventHandler));
				}
				break;
				
			case NET_FOLDER_CREATED:
				// A NetFolderCreatedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof NetFolderCreatedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = NetFolderCreatedEvent.registerEvent(
																			eventBus,
																			((NetFolderCreatedEvent.Handler) eventHandler ) );
				}
				break;
				
			case NET_FOLDER_ROOT_CREATED:
				// A NetFolderRootCreatedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof NetFolderRootCreatedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = NetFolderRootCreatedEvent.registerEvent(
																			eventBus,
																			((NetFolderRootCreatedEvent.Handler) eventHandler ) );
				}
				break;
				
			case NET_FOLDER_MODIFIED:
				// A NetFolderModifiedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof NetFolderModifiedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = NetFolderModifiedEvent.registerEvent(
																			eventBus,
																			((NetFolderModifiedEvent.Handler) eventHandler ) );
				}
				break;
				
			case NET_FOLDER_ROOT_MODIFIED:
				// A NetFolderRootModifiedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof NetFolderRootModifiedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = NetFolderRootModifiedEvent.registerEvent(
																			eventBus,
																			((NetFolderRootModifiedEvent.Handler) eventHandler ) );
				}
				break;
				
			case PRE_LOGOUT:
				// A PreLogoutEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof PreLogoutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = PreLogoutEvent.registerEvent(eventBus, ((PreLogoutEvent.Handler) eventHandler));
				}
				break;
				
			case PREVIEW_LANDING_PAGE:
				// A PreviewLandingPageEvent.  Can the event handler we were given handle that?
				if ( eventHandler instanceof PreviewLandingPageEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = PreviewLandingPageEvent.registerEvent( eventBus, ((PreviewLandingPageEvent.Handler) eventHandler) );
				}
				break;
			
			case PUBLIC_COLLECTION_STATE_CHANGED:
				// A PublicCollectionStateChangedEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof PublicCollectionStateChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = PublicCollectionStateChangedEvent.registerEvent(eventBus, ((PublicCollectionStateChangedEvent.Handler) eventHandler));
				}
				break;
			
			case RELOAD_DIALOG_CONTENT:
				// A ReloadDialogContentEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ReloadDialogContentEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ReloadDialogContentEvent.registerEvent(eventBus, ((ReloadDialogContentEvent.Handler) eventHandler));
				}
				break;
			
			case RESET_ENTRY_MENU:
				// A ResetEntryMenuEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ResetEntryMenuEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ResetEntryMenuEvent.registerEvent(eventBus, ((ResetEntryMenuEvent.Handler) eventHandler));
				}
				break;
			
			case RESET_VELOCITY_ENGINE:
				// A ResetVelocityEngineEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ResetVelocityEngineEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ResetVelocityEngineEvent.registerEvent(eventBus, ((ResetVelocityEngineEvent.Handler) eventHandler));
				}
				break;
			
			case MAILTO_PUBLIC_LINK_ENTITY:
				// A MailToPublicLinkEntityEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof MailToPublicLinkEntityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MailToPublicLinkEntityEvent.registerEvent(eventBus, ((MailToPublicLinkEntityEvent.Handler) eventHandler));
				}
				break;
			
			case MANAGE_DEFAULT_USER_SETTINGS:
				// An ManageDefaultUserSettingsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ManageDefaultUserSettingsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ManageDefaultUserSettingsEvent.registerEvent(eventBus, ((ManageDefaultUserSettingsEvent.Handler) eventHandler));
				}
				break;
				
			case MANAGE_SHARES_SELECTED_ENTITIES:
				// A ManageSharesSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ManageSharesSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ManageSharesSelectedEntitiesEvent.registerEvent(eventBus, ((ManageSharesSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case MANAGE_USERS_FILTER:
				// An ManageUsersFilterEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ManageUsersFilterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ManageUsersFilterEvent.registerEvent(eventBus, ((ManageUsersFilterEvent.Handler) eventHandler));
				}
				break;
				
			case MANAGE_USER_VISIBILITY:
				// An ManageUserVisibilityEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ManageUserVisibilityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ManageUserVisibilityEvent.registerEvent(eventBus, ((ManageUserVisibilityEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_ENTRY_READ:
				// An MarkEntryReadEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof MarkEntryReadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkEntryReadEvent.registerEvent(eventBus, ((MarkEntryReadEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_ENTRY_UNREAD:
				// An MarkEntryUnreadEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof MarkEntryUnreadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkEntryUnreadEvent.registerEvent(eventBus, ((MarkEntryUnreadEvent.Handler) eventHandler));
				}
				break;

			case MARK_FOLDER_CONTENTS_READ:
				// An MarkFolderContentsReadEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof MarkFolderContentsReadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkFolderContentsReadEvent.registerEvent(eventBus, ((MarkFolderContentsReadEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_FOLDER_CONTENTS_UNREAD:
				// An MarkFolderContentsUnreadEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof MarkFolderContentsUnreadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkFolderContentsUnreadEvent.registerEvent(eventBus, ((MarkFolderContentsUnreadEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_READ_SELECTED_ENTITIES:
				// A MarkReadSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof MarkReadSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkReadSelectedEntitiesEvent.registerEvent(eventBus, ((MarkReadSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case MARK_UNREAD_SELECTED_ENTITIES:
				// A MarkUnreadSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof MarkUnreadSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkUnreadSelectedEntitiesEvent.registerEvent(eventBus, ((MarkUnreadSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case MASTHEAD_HIDE:
				// A MastheadHideEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MastheadHideEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MastheadHideEvent.registerEvent(eventBus, ((MastheadHideEvent.Handler) eventHandler));
				}
				break;
			
			case MASTHEAD_SHOW:
				// A MastheadShowEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MastheadShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MastheadShowEvent.registerEvent(eventBus, ((MastheadShowEvent.Handler) eventHandler));
				}
				break;
			
			case MASTHEAD_UNHIGHLIGHT_ALL_ACTIONS:
				// A MastheadUnhighlightAllActionsEvent!  Can the event handler we were
				// given handle that?
				if ( eventHandler instanceof MastheadUnhighlightAllActionsEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = MastheadUnhighlightAllActionsEvent.registerEvent( eventBus, ((MastheadUnhighlightAllActionsEvent.Handler) eventHandler) );
				}
				break;
			
			case MENU_HIDE:
				// A MenuHideEvent.  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MenuHideEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MenuHideEvent.registerEvent(eventBus, ((MenuHideEvent.Handler) eventHandler));
				}
				break;
			
			case MENU_LOADED:
				// A MenuLoadedEvent.  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MenuLoadedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MenuLoadedEvent.registerEvent(eventBus, ((MenuLoadedEvent.Handler) eventHandler));
				}
				break;
			
			case MENU_SHOW:
				// A MenuShowEvent.  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MenuShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MenuShowEvent.registerEvent(eventBus, ((MenuShowEvent.Handler) eventHandler));
				}
				break;
			
			case MOBILE_DEVICE_WIPE_SCHEDULE_CHANGED:
				// A MobileDeviceWipeScheduleStateChangedEvent!  Can
				// the event handler we were given handle that?
				if (eventHandler instanceof MobileDeviceWipeScheduleStateChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MobileDeviceWipeScheduleStateChangedEvent.registerEvent(eventBus, ((MobileDeviceWipeScheduleStateChangedEvent.Handler) eventHandler));
				}
				break;
			
			case MOVE_SELECTED_ENTITIES:
				// A MoveSelectedEntitiesEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof MoveSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MoveSelectedEntitiesEvent.registerEvent(eventBus, ((MoveSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case QUICK_FILTER:
				// A QuickFilterEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof QuickFilterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = QuickFilterEvent.registerEvent(eventBus, ((QuickFilterEvent.Handler) eventHandler));
				}
				break;
			
			case REFRESH_SIDEBAR_TREE:
				// A RefreshSidebarTreeEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof RefreshSidebarTreeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = RefreshSidebarTreeEvent.registerEvent(eventBus, ((RefreshSidebarTreeEvent.Handler) eventHandler));
				}
				break;
			
			case REROOT_SIDEBAR_TREE:
				// A RerootSidebarTreeEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof RerootSidebarTreeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = RerootSidebarTreeEvent.registerEvent(eventBus, ((RerootSidebarTreeEvent.Handler) eventHandler));
				}
				break;
			
			case SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES:
				// A ScheduleWipeSelectedMobileDevicesEvent!  Can
				// the event handler we were given handle that?
				if (eventHandler instanceof ScheduleWipeSelectedMobileDevicesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ScheduleWipeSelectedMobileDevicesEvent.registerEvent(eventBus, ((ScheduleWipeSelectedMobileDevicesEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_ADVANCED:
				// A SearchAdvancedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchAdvancedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchAdvancedEvent.registerEvent(eventBus, ((SearchAdvancedEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_FIND_RESULTS:
				// A SearchFindResultsEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchFindResultsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchFindResultsEvent.registerEvent(eventBus, ((SearchFindResultsEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_RECENT_PLACE:
				// A SearchRecentPlaceEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchRecentPlaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchRecentPlaceEvent.registerEvent(eventBus, ((SearchRecentPlaceEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_SAVED:
				// A SearchSavedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchSavedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchSavedEvent.registerEvent(eventBus, ((SearchSavedEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_SIMPLE:
				// A SearchSimpleEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchSimpleEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchSimpleEvent.registerEvent(eventBus, ((SearchSimpleEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_TAG:
				// A SearchTagEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchTagEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchTagEvent.registerEvent(eventBus, ((SearchTagEvent.Handler) eventHandler));
				}
				break;
				
			case SET_DESKTOP_DOWNLOAD_APP_CONTROL_VISIBILITY:
				// A SetDesktopDownloadAppControlVisibilityEvent!  Can
				// the event handler we were given handle that?
				if (eventHandler instanceof SetDesktopDownloadAppControlVisibilityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetDesktopDownloadAppControlVisibilityEvent.registerEvent(eventBus, ((SetDesktopDownloadAppControlVisibilityEvent.Handler) eventHandler));
				}
				break;
				
			case SET_FILR_ACTION_FROM_COLLECTION_TYPE:
				// A SetFilrActionFromCollectionTypeEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof SetFilrActionFromCollectionTypeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetFilrActionFromCollectionTypeEvent.registerEvent(eventBus, ((SetFilrActionFromCollectionTypeEvent.Handler) eventHandler));
				}
				break;
				
			case SET_FOLDER_SORT:
				// A SetFolderSortEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SetFolderSortEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetFolderSortEvent.registerEvent(eventBus, ((SetFolderSortEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_BINDER_SHARE_RIGHTS:
				// An SetSelectedBinderShareRightsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof SetSelectedBinderShareRightsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedBinderShareRightsEvent.registerEvent(eventBus, ((SetSelectedBinderShareRightsEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS:
				// An SetSelectedPrincipalsAdminRightsEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof SetSelectedPrincipalsAdminRightsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedPrincipalsAdminRightsEvent.registerEvent(eventBus, ((SetSelectedPrincipalsAdminRightsEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY:
				// An SetSelectedPrincipalsLimitedUserVisibilityEvent!
				// Can the event handler we were given handle that?
				if (eventHandler instanceof SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedPrincipalsLimitedUserVisibilityEvent.registerEvent(eventBus, ((SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_USER_DESKTOP_SETTINGS:
				// An SetSelectedUserDesktopSettingsEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof SetSelectedUserDesktopSettingsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedUserDesktopSettingsEvent.registerEvent(eventBus, ((SetSelectedUserDesktopSettingsEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_USER_MOBILE_SETTINGS:
				// An SetSelectedUserMobileSettingsEvent!  Can the
				// event handler we were given handle that?
				if (eventHandler instanceof SetSelectedUserMobileSettingsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedUserMobileSettingsEvent.registerEvent(eventBus, ((SetSelectedUserMobileSettingsEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SELECTED_USER_SHARE_RIGHTS:
				// An SetSelectedUserShareRightsEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof SetSelectedUserShareRightsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SetSelectedUserShareRightsEvent.registerEvent(eventBus, ((SetSelectedUserShareRightsEvent.Handler) eventHandler));
				}
				break;
				
			case SET_SHARE_RIGHTS:
				// A SetShareRightsEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof SetShareRightsEvent.Handler ) 
				{
					handlerNotDefined = false;
					registrationHandler = SetShareRightsEvent.registerEvent( eventBus, ((SetShareRightsEvent.Handler) eventHandler) );
				}
				break;
				
			case SHARE_EXPIRATION_VALUE_CHANGED:
				// An ShareExpirationValueChangedEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShareExpirationValueChangedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShareExpirationValueChangedEvent.registerEvent( eventBus, ((ShareExpirationValueChangedEvent.Handler) eventHandler));
				}
				break;
				
			case SHARE_SELECTED_ENTITIES:
				// A ShareSelectedEntitiesEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShareSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShareSelectedEntitiesEvent.registerEvent(eventBus, ((ShareSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case SHARED_VIEW_FILTER:
				// An SharedViewFilterEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SharedViewFilterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SharedViewFilterEvent.registerEvent(eventBus, ((SharedViewFilterEvent.Handler) eventHandler));
				}
				break;
				
			case SHOW_ACCESSORIES:
				// A ShowAccessoriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowAccessoriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowAccessoriesEvent.registerEvent(eventBus, ((ShowAccessoriesEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_BLOG_FOLDER:
				// A ShowBlogFolderEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowBlogFolderEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowBlogFolderEvent.registerEvent(
																		eventBus,
																		((ShowBlogFolderEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_CALENDAR_FOLDER:
				// A ShowCalendarFolderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowCalendarFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowCalendarFolderEvent.registerEvent(eventBus, ((ShowCalendarFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_COLLECTION:
				// A ShowCollectionEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowCollectionEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowCollectionEvent.registerEvent(
																		eventBus,
																		((ShowCollectionEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_COLLECTION_VIEW:
				// A ShowCollectionViewEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowCollectionViewEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowCollectionViewEvent.registerEvent(eventBus, ((ShowCollectionViewEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_CONTENT_CONTROL:
				// A ShowContentControlEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowContentControlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowContentControlEvent.registerEvent(eventBus, ((ShowContentControlEvent.Handler) eventHandler));
				}
				break;

			case SHOW_CUSTOM_BINDER_VIEW:
				// A ShowCustomBinderViewEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowCustomBinderViewEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowCustomBinderViewEvent.registerEvent(eventBus, ((ShowCustomBinderViewEvent.Handler) eventHandler));
				}
				break;

			case SHOW_DISCUSSION_FOLDER:
				// A ShowDiscussionFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowDiscussionFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowDiscussionFolderEvent.registerEvent(eventBus, ((ShowDiscussionFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_DISCUSSION_WORKSPACE:
				// A ShowDiscussionWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowDiscussionWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowDiscussionWSEvent.registerEvent( eventBus, ((ShowDiscussionWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_FILE_FOLDER:
				// A ShowFileFolderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowFileFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowFileFolderEvent.registerEvent(eventBus, ((ShowFileFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_FOLDER_ENTRY:
				// A ShowFolderEntryEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowFolderEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowFolderEntryEvent.registerEvent(eventBus, ((ShowFolderEntryEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_GENERIC_WORKSPACE:
				// A ShowGenericWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowGenericWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowGenericWSEvent.registerEvent( eventBus, ((ShowGenericWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_GLOBAL_WORKSPACE:
				// A ShowGlobalWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowGlobalWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowGlobalWSEvent.registerEvent( eventBus, ((ShowGlobalWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_GUESTBOOK_FOLDER:
				// A ShowGuestbookFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowGuestbookFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowGuestbookFolderEvent.registerEvent(eventBus, ((ShowGuestbookFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_HOME_WORKSPACE:
				// A ShowHomeWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowHomeWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowHomeWSEvent.registerEvent( eventBus, ((ShowHomeWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_HTML_ELEMENT:
				// A ShowHtmlElementEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowHtmlElementEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowHtmlElementEvent.registerEvent(eventBus, ((ShowHtmlElementEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_LANDING_PAGE:
				// A ShowLandingPageEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowLandingPageEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowLandingPageEvent.registerEvent(eventBus, ((ShowLandingPageEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_MICRO_BLOG_FOLDER:
				// A ShowMicroBlogFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowMicroBlogFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowMicroBlogFolderEvent.registerEvent(eventBus, ((ShowMicroBlogFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_MILESTONE_FOLDER:
				// A ShowMilestoneFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowMilestoneFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowMilestoneFolderEvent.registerEvent(eventBus, ((ShowMilestoneFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_MIRRORED_FILE_FOLDER:
				// A ShowMirroredFileFolderEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ShowMirroredFileFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowMirroredFileFolderEvent.registerEvent(eventBus, ((ShowMirroredFileFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_NET_FOLDERS_WORKSPACE:
				// A ShowNetFoldersWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowNetFoldersWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowNetFoldersWSEvent.registerEvent( eventBus, ((ShowNetFoldersWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_PERSONAL_WORKSPACE:
				// A ShowPersonalWorkspaceEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowPersonalWorkspaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowPersonalWorkspaceEvent.registerEvent(eventBus, ((ShowPersonalWorkspaceEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_PERSONAL_WORKSPACES:
				// A ShowPersonalWorkspacesEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowPersonalWorkspacesEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowPersonalWorkspacesEvent.registerEvent( eventBus, ((ShowPersonalWorkspacesEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_PHOTO_ALBUM_FOLDER:
				// A ShowPhotoAlbumFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ShowPhotoAlbumFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowPhotoAlbumFolderEvent.registerEvent(eventBus, ((ShowPhotoAlbumFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_PROJECT_MANAGEMENT_WORKSPACE:
				// A ShowProjectManagementWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowProjectManagementWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowProjectManagementWSEvent.registerEvent( eventBus, ((ShowProjectManagementWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_SELECTED_SHARES:
				// A ShowSelectedSharesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowSelectedSharesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowSelectedSharesEvent.registerEvent(eventBus, ((ShowSelectedSharesEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_SURVEY_FOLDER:
				// A ShowSurveyFolderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowSurveyFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowSurveyFolderEvent.registerEvent(eventBus, ((ShowSurveyFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_TASK_FOLDER:
				// A ShowTaskFolderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowTaskFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowTaskFolderEvent.registerEvent(eventBus, ((ShowTaskFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_TEAM_ROOT_WORKSPACE:
				// A ShowTeamRootWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowTeamRootWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowTeamRootWSEvent.registerEvent( eventBus, ((ShowTeamRootWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_TEAM_WORKSPACE:
				// A ShowTeamWSEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof ShowTeamWSEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = ShowTeamWSEvent.registerEvent( eventBus, ((ShowTeamWSEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_TRASH:
				// A ShowTrashEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof ShowTrashEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowTrashEvent.registerEvent(eventBus, ((ShowTrashEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_USER_LIST:
				// A ShowUserListEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof ShowUserListEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowUserListEvent.registerEvent(eventBus, ((ShowUserListEvent.Handler) eventHandler) );
				}
				break;
			
			case SHOW_VIEW_PERMALINKS:
				// A ShowViewPermalinksEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowViewPermalinksEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowViewPermalinksEvent.registerEvent(eventBus, ((ShowViewPermalinksEvent.Handler) eventHandler));
				}
				break;
			
			case SHOW_WIKI_FOLDER:
				// A ShowWikiFolderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ShowWikiFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ShowWikiFolderEvent.registerEvent(eventBus, ((ShowWikiFolderEvent.Handler) eventHandler));
				}
				break;
			
			case SIDEBAR_HIDE:
				// A SidebarHideEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SidebarHideEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SidebarHideEvent.registerEvent(eventBus, ((SidebarHideEvent.Handler) eventHandler));
				}
				break;
			
			case SIDEBAR_SHOW:
				// A SidebarShowEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SidebarShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SidebarShowEvent.registerEvent(eventBus, ((SidebarShowEvent.Handler) eventHandler));
				}
				break;
			
			case SIZE_CHANGED:
				// A SizeChangedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SizeChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SizeChangedEvent.registerEvent(eventBus, ((SizeChangedEvent.Handler) eventHandler));
				}
				break;
			
			case SUBSCRIBE_SELECTED_ENTITIES:
				// A SubscribeSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof SubscribeSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SubscribeSelectedEntitiesEvent.registerEvent(eventBus, ((SubscribeSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_DELETE:
				// A TaskDeleteEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskDeleteEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskDeleteEvent.registerEvent(eventBus, ((TaskDeleteEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_HIERARCHY_DISABLED:
				// A TaskHierarchyDisabledEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof TaskHierarchyDisabledEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskHierarchyDisabledEvent.registerEvent(eventBus, ((TaskHierarchyDisabledEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_LIST_READY:
				// A TaskListReadyEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskListReadyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskListReadyEvent.registerEvent(eventBus, ((TaskListReadyEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_DOWN:
				// A TaskMoveDownEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveDownEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveDownEvent.registerEvent(eventBus, ((TaskMoveDownEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_LEFT:
				// A TaskMoveLeftEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveLeftEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveLeftEvent.registerEvent(eventBus, ((TaskMoveLeftEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_RIGHT:
				// A TaskMoveRightEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveRightEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveRightEvent.registerEvent(eventBus, ((TaskMoveRightEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_UP:
				// A TaskMoveUpEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveUpEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveUpEvent.registerEvent(eventBus, ((TaskMoveUpEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_NEW_TASK:
				// A TaskNewTaskEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskNewTaskEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskNewTaskEvent.registerEvent(eventBus, ((TaskNewTaskEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_PICK_DATE:
				// A TaskPickDateEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskPickDateEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskPickDateEvent.registerEvent(eventBus, ((TaskPickDateEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_PURGE:
				// A TaskPurgeEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskPurgeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskPurgeEvent.registerEvent(eventBus, ((TaskPurgeEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_PERCENT_DONE:
				// A TaskSetPercentDoneEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TaskSetPercentDoneEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetPercentDoneEvent.registerEvent(eventBus, ((TaskSetPercentDoneEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_PRIORITY:
				// A TaskSetPriorityEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TaskSetPriorityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetPriorityEvent.registerEvent(eventBus, ((TaskSetPriorityEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_STATUS:
				// A TaskSetStatusEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskSetStatusEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetStatusEvent.registerEvent(eventBus, ((TaskSetStatusEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_VIEW:
				// A TaskViewEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskViewEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskViewEvent.registerEvent(eventBus, ((TaskViewEvent.Handler) eventHandler));
				}
				break;
			
			case TOGGLE_SHARED_VIEW:
				// A ToggleSharedViewEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ToggleSharedViewEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ToggleSharedViewEvent.registerEvent(eventBus, ((ToggleSharedViewEvent.Handler) eventHandler));
				}
				break;
			
			case TRACK_CURRENT_BINDER:
				// A TrackCurrentBinderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TrackCurrentBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrackCurrentBinderEvent.registerEvent(eventBus, ((TrackCurrentBinderEvent.Handler) eventHandler));
				}
				break;
			
			case TRASH_PURGE_ALL:
				// A TrashPurgeAllEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TrashPurgeAllEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrashPurgeAllEvent.registerEvent(eventBus, ((TrashPurgeAllEvent.Handler) eventHandler));
				}
				break;
			
			case TRASH_PURGE_SELECTED_ENTITIES:
				// A TrashPurgeSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof TrashPurgeSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrashPurgeSelectedEntitiesEvent.registerEvent(eventBus, ((TrashPurgeSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case TRASH_RESTORE_ALL:
				// A TrashRestoreAllEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TrashRestoreAllEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrashRestoreAllEvent.registerEvent(eventBus, ((TrashRestoreAllEvent.Handler) eventHandler));
				}
				break;
			
			case TRASH_RESTORE_SELECTED_ENTITIES:
				// A TrashRestoreSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof TrashRestoreSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrashRestoreSelectedEntitiesEvent.registerEvent(eventBus, ((TrashRestoreSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case TREE_NODE_COLLAPSED:
				// A TreeNodeCollapsedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TreeNodeCollapsedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TreeNodeCollapsedEvent.registerEvent(eventBus, ((TreeNodeCollapsedEvent.Handler) eventHandler));
				}
				break;
			
			case TREE_NODE_EXPANDED:
				// A TreeNodeExpandedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TreeNodeExpandedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TreeNodeExpandedEvent.registerEvent(eventBus, ((TreeNodeExpandedEvent.Handler) eventHandler));
				}
				break;
			
			case UNTRACK_CURRENT_BINDER:
				// A UntrackCurrentBinderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof UntrackCurrentBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = UntrackCurrentBinderEvent.registerEvent(eventBus, ((UntrackCurrentBinderEvent.Handler) eventHandler));
				}
				break;
			
			case UNLOCK_SELECTED_ENTITIES:
				// A UnlockSelectedEntitiesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof UnlockSelectedEntitiesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = UnlockSelectedEntitiesEvent.registerEvent(eventBus, ((UnlockSelectedEntitiesEvent.Handler) eventHandler));
				}
				break;
			
			case UNTRACK_CURRENT_PERSON:
				// A UntrackCurrentPersonEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof UntrackCurrentPersonEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = UntrackCurrentPersonEvent.registerEvent(eventBus, ((UntrackCurrentPersonEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_ALL_ENTRIES:
				// A ViewAllEntriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewAllEntriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewAllEntriesEvent.registerEvent(eventBus, ((ViewAllEntriesEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_CURRENT_BINDER_TEAM_MEMBERS:
				// A ViewCurrentBinderTeamMembersEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ViewCurrentBinderTeamMembersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewCurrentBinderTeamMembersEvent.registerEvent(eventBus, ((ViewCurrentBinderTeamMembersEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_FORUM_ENTRY:
				// A ViewForumEntryEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewForumEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewForumEntryEvent.registerEvent(eventBus, ((ViewForumEntryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_PINNED_ENTRIES:
				// A ViewPinnedEntriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewPinnedEntriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewPinnedEntriesEvent.registerEvent(eventBus, ((ViewPinnedEntriesEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_RESOURCE_LIBRARY:
				// A ViewResourceLibraryEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ViewResourceLibraryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewResourceLibraryEvent.registerEvent(eventBus, ((ViewResourceLibraryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_SELECTED_ENTRY:
				// A ViewSelectedEntryEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewSelectedEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewSelectedEntryEvent.registerEvent(eventBus, ((ViewSelectedEntryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_TEAMING_FEED:
				// A ViewTeamingFeedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewTeamingFeedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewTeamingFeedEvent.registerEvent(eventBus, ((ViewTeamingFeedEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_UNREAD_ENTRIES:
				// A ViewUnreadEntriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewUnreadEntriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewUnreadEntriesEvent.registerEvent(eventBus, ((ViewUnreadEntriesEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_WHATS_NEW_IN_BINDER:
				// A ViewWhatsNewInBinderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ViewWhatsNewInBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewWhatsNewInBinderEvent.registerEvent(eventBus, ((ViewWhatsNewInBinderEvent.Handler) eventHandler));
				}				
				break;
			
			case VIEW_WHATS_UNSEEN_IN_BINDER:
				// A ViewWhatsUnseenInBinderEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ViewWhatsUnseenInBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewWhatsUnseenInBinderEvent.registerEvent(eventBus, ((ViewWhatsUnseenInBinderEvent.Handler) eventHandler));
				}				
				break;
			
			case VIEW_WHO_HAS_ACCESS:
				// A ViewWhoHasAccessEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewWhoHasAccessEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewWhoHasAccessEvent.registerEvent(eventBus, ((ViewWhoHasAccessEvent.Handler) eventHandler));
				}
				break;
			
			case WINDOW_TITLE_SET:
				// A WindowTitleSetEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof WindowTitleSetEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = WindowTitleSetEvent.registerEvent(eventBus, ((WindowTitleSetEvent.Handler) eventHandler));
				}
				break;
			
			case ZIP_AND_DOWNLOAD_FOLDER:
				// A ZipAndDownloadFolderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ZipAndDownloadFolderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ZipAndDownloadFolderEvent.registerEvent(eventBus, ((ZipAndDownloadFolderEvent.Handler) eventHandler));
				}
				break;
			
			case ZIP_AND_DOWNLOAD_SELECTED_FILES:
				// A ZipAndDownloadSelectedFilesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ZipAndDownloadSelectedFilesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ZipAndDownloadSelectedFilesEvent.registerEvent(eventBus, ((ZipAndDownloadSelectedFilesEvent.Handler) eventHandler));
				}
				break;
			
			default:
			case UNDEFINED:
				// Whatever it is, we can't handle it!  Tell the user
				// about the problem.
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().eventHandling_UnhandledEvent(te.name(), EventHelper.class.getName()));
				handlerNotDefined = false;
				registrationHandler = null;
				break;
			}

			// Was the event handler we were given able to handle this
			// event?
			if (handlerNotDefined) {
				// No!  Tell the user about the problem.
				GwtClientHelper.deferredAlert(
					GwtTeaming.getMessages().eventHandling_UnhandledEvent(
						te.name(),
						eventHandler.getClass().getName()));
			}
			
			// Yes, the event handler we were given was able to handle
			// this event!  We're we able to register it and is the
			// caller wanting to keep track of them?
			else if ((null != registrationHandler) && returnRegisteredEventHandlers) {
				// Yes!  Add the HandlerRegistration to the caller's
				// list of them.
				final HandlerRegistration finalRegistrationHandler = registrationHandler;
				registeredEventHandlers.add(new HandlerRegistration() {
					@Override
					public void removeHandler() {
						finalRegistrationHandler.removeHandler();
					}
				});
			}
		}
	}
	
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler, List<HandlerRegistration> registeredEventHandlers) {
		// Always use the initial form of the method.
		registerEventHandlers(
			eventBus,
			eventsToBeRegistered,
			eventHandler,
			registeredEventHandlers,
			true);
	}
	
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler, boolean doValidation) {
		// Always use the initial form of the method.
		registerEventHandlers(
			eventBus,
			eventsToBeRegistered,
			eventHandler,
			null,
			doValidation);
	}
	
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler) {
		// Always use the initial form of the method.
		registerEventHandlers(
			eventBus,
			eventsToBeRegistered,
			eventHandler,
			null,
			true);
	}

	/**
	 * Unregisters the HandlerRegistration's from a list of them.
	 * 
	 * @param registeredEventHandlers
	 */
	public static void unregisterEventHandlers(List<HandlerRegistration> registeredEventHandlers) {
		// If we were given a list of HandlerRegistration's...
		if ((null != registeredEventHandlers) && (!(registeredEventHandlers.isEmpty()))) {
			// ...scan them...
			for (HandlerRegistration hr: registeredEventHandlers) {
				// ...removing the handler for each...
				hr.removeHandler();
			}
			
			// ...and emptying the list.
			registeredEventHandlers.clear();
		}
	}

	/*
	 * Validates that the event handlers implemented by eventHandler
	 * are include in a TeamingEvents[].
	 * 
	 * The code actually only perform validation checking if the UI is
	 * in debug mode.  I decided to take this tact rather than always
	 * enumerate the enumeration values in the registerEventHandlers()
	 * for performance reasons.  I didn't want each class that
	 * registers events to pay the price of checking whether perhaps
	 * 100's of events were implemented.
	 * 
	 * This should NEVER be done in shipping code !!!
	 */
	private static void validateEvents(TeamingEvents[] eventsToCheck, Object eventHandler) {
		// If we're not validating things...
		if (!GwtClientHelper.isDebugUI()) {
			// ...bail.
			return;
		}

		// If we don't have any events to validate or a handler to
		// validate them against...
		int events = ((null == eventsToCheck) ? 0 : eventsToCheck.length);
		if ((0 == events) || (null == eventHandler)) {
			// ...bail.
			return;
		}

		// Scan the defined TeamingEvents looking for what's defined
		// and what should be defined.
		for (TeamingEvents te:  TeamingEvents.values() ) {
			boolean needsHandler = isTEInA(te, eventsToCheck);
			boolean hasHandler   = false;
			switch (te) {
			case ACCESS_TO_ITEM_DENIED:				       	   hasHandler = (eventHandler instanceof AccessToItemDeniedEvent.Handler);		           	   break;
			case ACTIVITY_STREAM:                   	       hasHandler = (eventHandler instanceof ActivityStreamEvent.Handler);                         break;
			case ACTIVITY_STREAM_ENTER:             	       hasHandler = (eventHandler instanceof ActivityStreamEnterEvent.Handler);                    break;
			case ACTIVITY_STREAM_EXIT:              	       hasHandler = (eventHandler instanceof ActivityStreamExitEvent.Handler);                     break;
			case ACTIVITY_STREAM_COMMENT_DELETED:              hasHandler = (eventHandler instanceof ActivityStreamCommentDeletedEvent.Handler);           break;
			
			case ADMINISTRATION:                    	       hasHandler = (eventHandler instanceof AdministrationEvent.Handler);                         break;
			case ADMINISTRATION_ACTION:                    	   hasHandler = (eventHandler instanceof AdministrationActionEvent.Handler);                   break;
			case ADMINISTRATION_EXIT:               	       hasHandler = (eventHandler instanceof AdministrationExitEvent.Handler);                     break;
			case ADMINISTRATION_UPGRADE_CHECK:      	       hasHandler = (eventHandler instanceof AdministrationUpgradeCheckEvent.Handler);             break;

			case BLOG_ARCHIVE_FOLDER_SELECTED:                 hasHandler = (eventHandler instanceof BlogArchiveFolderSelectedEvent.Handler);              break;
			case BLOG_ARCHIVE_MONTH_SELECTED:                  hasHandler = (eventHandler instanceof BlogArchiveMonthSelectedEvent.Handler);               break;
			case BLOG_GLOBAL_TAG_SELECTED:           	       hasHandler = (eventHandler instanceof BlogGlobalTagSelectedEvent.Handler);                  break;
			case BLOG_PAGE_CREATED:           			       hasHandler = (eventHandler instanceof BlogPageCreatedEvent.Handler);         	           break;
			case BLOG_PAGE_SELECTED:           			       hasHandler = (eventHandler instanceof BlogPageSelectedEvent.Handler);         	           break;

			case BROWSE_HIERARCHY:                  	       hasHandler = (eventHandler instanceof BrowseHierarchyEvent.Handler);                        break;
			case BROWSE_HIERARCHY_EXIT:             	       hasHandler = (eventHandler instanceof BrowseHierarchyExitEvent.Handler);                    break;
			
			case CALENDAR_CHANGED:                             hasHandler = (eventHandler instanceof CalendarChangedEvent.Handler);                        break;
			case CALENDAR_GOTO_DATE:                           hasHandler = (eventHandler instanceof CalendarGotoDateEvent.Handler);                       break;
			case CALENDAR_HOURS:                               hasHandler = (eventHandler instanceof CalendarHoursEvent.Handler);                          break;
			case CALENDAR_NEXT_PERIOD:                         hasHandler = (eventHandler instanceof CalendarNextPeriodEvent.Handler);                     break;
			case CALENDAR_PREVIOUS_PERIOD:                     hasHandler = (eventHandler instanceof CalendarPreviousPeriodEvent.Handler);                 break;
			case CALENDAR_SETTINGS:                            hasHandler = (eventHandler instanceof CalendarSettingsEvent.Handler);                       break;
			case CALENDAR_SHOW:                                hasHandler = (eventHandler instanceof CalendarShowEvent.Handler);                           break;
			case CALENDAR_SHOW_HINT:                           hasHandler = (eventHandler instanceof CalendarShowHintEvent.Handler);                       break;
			case CALENDAR_VIEW_DAYS:                           hasHandler = (eventHandler instanceof CalendarViewDaysEvent.Handler);                       break;
			
			case CHANGE_CONTEXT:                    	       hasHandler = (eventHandler instanceof ChangeContextEvent.Handler);                          break;
			case CONTENT_CHANGED:                   	       hasHandler = (eventHandler instanceof ContentChangedEvent.Handler);                         break;
			case CONTEXT_CHANGED:                   	       hasHandler = (eventHandler instanceof ContextChangedEvent.Handler);                         break;
			case CONTEXT_CHANGING:                  	       hasHandler = (eventHandler instanceof ContextChangingEvent.Handler);                        break;
			
			case CONTRIBUTOR_IDS_REPLY:                        hasHandler = (eventHandler instanceof ContributorIdsReplyEvent.Handler);                    break;
			case CONTRIBUTOR_IDS_REQUEST:                      hasHandler = (eventHandler instanceof ContributorIdsRequestEvent.Handler);                  break;
			
			case DELETE_ACTIVITY_STREAM_UI_ENTRY:              hasHandler = (eventHandler instanceof DeleteActivityStreamUIEntryEvent.Handler);            break;
			case EDIT_ACTIVITY_STREAM_UI_ENTRY:                hasHandler = (eventHandler instanceof EditActivityStreamUIEntryEvent.Handler);              break;

			case EDIT_CURRENT_BINDER_BRANDING:      	       hasHandler = (eventHandler instanceof EditCurrentBinderBrandingEvent.Handler);              break;
			case EDIT_PERSONAL_PREFERENCES:         	       hasHandler = (eventHandler instanceof EditPersonalPreferencesEvent.Handler);                break;
			case EDIT_SITE_BRANDING:                	       hasHandler = (eventHandler instanceof EditSiteBrandingEvent.Handler);                       break;
			case EDIT_MOBILE_SITE_BRANDING:                	   hasHandler = (eventHandler instanceof EditMobileSiteBrandingEvent.Handler);                 break;
			case EDIT_DESKTOP_SITE_BRANDING:                   hasHandler = (eventHandler instanceof EditDesktopSiteBrandingEvent.Handler);                break;
			
			case EDIT_LANDING_PAGE_PROPERTIES:      	       hasHandler = (eventHandler instanceof EditLandingPagePropertiesEvent.Handler);              break;
			
			case MASTHEAD_HIDE:                     	       hasHandler = (eventHandler instanceof MastheadHideEvent.Handler);                           break;
			case MASTHEAD_SHOW:                     	       hasHandler = (eventHandler instanceof MastheadShowEvent.Handler);                           break;
			case MASTHEAD_UNHIGHLIGHT_ALL_ACTIONS:     	       hasHandler = (eventHandler instanceof MastheadUnhighlightAllActionsEvent.Handler);          break;
			
			case GET_MANAGE_MENU_POPUP:                        hasHandler = (eventHandler instanceof GetManageMenuPopupEvent.Handler);                     break;
			case HIDE_MANAGE_MENU:						       hasHandler = (eventHandler instanceof HideManageMenuEvent.Handler);		                   break;
			case MANAGE_USERS_FILTER:                     	   hasHandler = (eventHandler instanceof ManageUsersFilterEvent.Handler);                 	   break;
			case MENU_HIDE:                     		       hasHandler = (eventHandler instanceof MenuHideEvent.Handler);                 	           break;
			case MENU_LOADED:                     		       hasHandler = (eventHandler instanceof MenuLoadedEvent.Handler);                 	           break;
			case MENU_SHOW:                     		       hasHandler = (eventHandler instanceof MenuShowEvent.Handler);                               break;
			case SHARED_VIEW_FILTER:                     	   hasHandler = (eventHandler instanceof SharedViewFilterEvent.Handler);                 	   break;

			case FILES_DROPPED:                    	           hasHandler = (eventHandler instanceof FilesDroppedEvent.Handler);                           break;
			case FULL_UI_RELOAD:                    	       hasHandler = (eventHandler instanceof FullUIReloadEvent.Handler);                           break;
			
			case GOTO_CONTENT_URL:                  	       hasHandler = (eventHandler instanceof GotoContentUrlEvent.Handler);                         break;
			case GOTO_MY_WORKSPACE:                 	       hasHandler = (eventHandler instanceof GotoMyWorkspaceEvent.Handler);                        break;
			case GOTO_PERMALINK_URL:                	       hasHandler = (eventHandler instanceof GotoPermalinkUrlEvent.Handler);                       break;
			case GOTO_URL:                  			       hasHandler = (eventHandler instanceof GotoUrlEvent.Handler);                	               break;
						
			case GROUP_CREATED:                			       hasHandler = (eventHandler instanceof GroupCreatedEvent.Handler);             	           break;
			case GROUP_MEMBERSHIP_MODIFICATION_FAILED:	       hasHandler = (eventHandler instanceof GroupMembershipModificationFailedEvent.Handler);      break;
			case GROUP_MEMBERSHIP_MODIFICATION_STARTED:	       hasHandler = (eventHandler instanceof GroupMembershipModificationStartedEvent.Handler);     break;
			case GROUP_MEMBERSHIP_MODIFIED:	       			   hasHandler = (eventHandler instanceof GroupMembershipModifiedEvent.Handler);     		   break;
			case GROUP_MODIFICATION_FAILED:       		       hasHandler = (eventHandler instanceof GroupModificationFailedEvent.Handler);                break;
			case GROUP_MODIFICATION_STARTED:       		       hasHandler = (eventHandler instanceof GroupModificationStartedEvent.Handler);               break;
			case GROUP_MODIFIED:                		       hasHandler = (eventHandler instanceof GroupModifiedEvent.Handler);             	           break;

			case INVOKE_ABOUT:							       hasHandler = (eventHandler instanceof InvokeAboutEvent.Handler);                            break;
			case INVOKE_ADD_NEW_FOLDER:					       hasHandler = (eventHandler instanceof InvokeAddNewFolderEvent.Handler);                     break;
			case INVOKE_ADD_NEW_PROXY_IDENTITITY:			   hasHandler = (eventHandler instanceof InvokeAddNewProxyIdentityEvent.Handler);              break;
			case INVOKE_BINDER_SHARE_RIGHTS_DLG:			   hasHandler = (eventHandler instanceof InvokeBinderShareRightsDlgEvent.Handler);		       break;
			case INVOKE_CHANGE_PASSWORD_DLG:			       hasHandler = (eventHandler instanceof InvokeChangePasswordDlgEvent.Handler);                break;
			case INVOKE_CLIPBOARD:						       hasHandler = (eventHandler instanceof InvokeClipboardEvent.Handler);                        break;
			case INVOKE_COLUMN_RESIZER:				           hasHandler = (eventHandler instanceof InvokeColumnResizerEvent.Handler);                    break;
			case INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG:	       hasHandler = (eventHandler instanceof InvokeConfigureAdhocFoldersDlgEvent.Handler);         break;
			case INVOKE_CONFIGURE_ANTIVIRUS_DLG:	           hasHandler = (eventHandler instanceof InvokeConfigureAntiVirusDlgEvent.Handler);            break;
			case INVOKE_CONFIGURE_COLUMNS:				       hasHandler = (eventHandler instanceof InvokeConfigureColumnsEvent.Handler);                 break;
			case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:	       hasHandler = (eventHandler instanceof InvokeConfigureFileSyncAppDlgEvent.Handler);          break;
			case INVOKE_CONFIGURE_MOBILE_APPS_DLG:	       	   hasHandler = (eventHandler instanceof InvokeConfigureMobileAppsDlgEvent.Handler);           break;
			case INVOKE_CONFIGURE_PASSWORD_POLICY_DLG:		   hasHandler = (eventHandler instanceof InvokeConfigurePasswordPolicyDlgEvent.Handler);       break;
			case INVOKE_CONFIGURE_SHARE_SETTINGS_DLG:	       hasHandler = (eventHandler instanceof InvokeConfigureShareSettingsDlgEvent.Handler);        break;
			case INVOKE_CONFIGURE_TELEMETRY_DLG:	           hasHandler = (eventHandler instanceof InvokeConfigureTelemetryDlgEvent.Handler);            break;
			case INVOKE_CONFIGURE_UPDATE_LOGS_DLG:		       hasHandler = (eventHandler instanceof InvokeConfigureUpdateLogsDlgEvent.Handler);           break;
			case INVOKE_CONFIGURE_USER_ACCESS_DLG:		       hasHandler = (eventHandler instanceof InvokeConfigureUserAccessDlgEvent.Handler);           break;
			case INVOKE_COPY_FILTERS_DLG:                      hasHandler = (eventHandler instanceof InvokeCopyFiltersDlgEvent.Handler);                   break;
			case INVOKE_DEFAULT_USER_SETTINGS_DLG:		       hasHandler = (eventHandler instanceof InvokeDefaultUserSettingsDlgEvent.Handler);           break;
			case INVOKE_DOWNLOAD_DESKTOP_APP:                  hasHandler = (eventHandler instanceof InvokeDownloadDesktopAppEvent.Handler);               break;
			case INVOKE_DROPBOX:						       hasHandler = (eventHandler instanceof InvokeDropBoxEvent.Handler);                          break;
			case INVOKE_EDIT_IN_PLACE:					       hasHandler = (eventHandler instanceof InvokeEditInPlaceEvent.Handler);                      break;
			case INVOKE_EDIT_KEYSHIELD_CONFIG_DLG:		   	   hasHandler = (eventHandler instanceof InvokeEditKeyShieldConfigDlgEvent.Handler); 	       break;
			case INVOKE_EDIT_LDAP_CONFIG_DLG:			   	   hasHandler = (eventHandler instanceof InvokeEditLdapConfigDlgEvent.Handler); 	       	   break;
			case INVOKE_EDIT_NET_FOLDER_DLG:			   	   hasHandler = (eventHandler instanceof InvokeEditNetFolderDlgEvent.Handler); 	       		   break;
			case INVOKE_EDIT_NET_FOLDER_RIGHTS_DLG:			   hasHandler = (eventHandler instanceof InvokeEditNetFolderRightsDlgEvent.Handler); 	       break;
			case INVOKE_EDIT_SHARE_RIGHTS_DLG:			   	   hasHandler = (eventHandler instanceof InvokeEditShareRightsDlgEvent.Handler); 	       	   break;
			case INVOKE_EDIT_USER_ZONE_SHARE_RIGHTS_DLG:	   hasHandler = (eventHandler instanceof InvokeEditUserZoneShareRightsDlgEvent.Handler); 	   break;
			case INVOKE_EMAIL_NOTIFICATION:         	       hasHandler = (eventHandler instanceof InvokeEmailNotificationEvent.Handler);                break;
			case INVOKE_HELP:                       	       hasHandler = (eventHandler instanceof InvokeHelpEvent.Handler);                             break;
			case INVOKE_IDEAS_PORTAL:                          hasHandler = (eventHandler instanceof InvokeIdeasPortalEvent.Handler);                      break;
			case INVOKE_IMPORT_ICAL_FILE:           	       hasHandler = (eventHandler instanceof InvokeImportIcalFileEvent.Handler);                   break;
			case INVOKE_IMPORT_ICAL_URL:            	       hasHandler = (eventHandler instanceof InvokeImportIcalUrlEvent.Handler);                    break;
			case INVOKE_IMPORT_PROFILES_DLG:				   hasHandler = (eventHandler instanceof InvokeImportProfilesDlgEvent.Handler);		           break;
			case INVOKE_LIMIT_USER_VISIBILITY_DLG:		       hasHandler = (eventHandler instanceof InvokeLimitUserVisibilityDlgEvent.Handler);           break;
			case INVOKE_NET_FOLDER_GLOBAL_SETTINGS_DLG:		   hasHandler = (eventHandler instanceof InvokeNetFolderGlobalSettingsDlgEvent.Handler); 	   break;
			case INVOKE_LDAP_SYNC_RESULTS_DLG:			       hasHandler = (eventHandler instanceof InvokeLdapSyncResultsDlgEvent.Handler); 	           break;
			case INVOKE_MANAGE_ADMINISTRATORS_DLG:			   hasHandler = (eventHandler instanceof InvokeManageAdministratorsDlgEvent.Handler);		   break;
			case INVOKE_MANAGE_DATABASE_PRUNE_DLG:			   hasHandler = (eventHandler instanceof InvokeManageDatabasePruneDlgEvent.Handler); 	       break;
			case INVOKE_MANAGE_EMAIL_TEMPLATES_DLG:			   hasHandler = (eventHandler instanceof InvokeManageEmailTemplatesDlgEvent.Handler);		   break;
			case INVOKE_MANAGE_NET_FOLDERS_DLG:			       hasHandler = (eventHandler instanceof InvokeManageNetFoldersDlgEvent.Handler); 	           break;
			case INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG:	       hasHandler = (eventHandler instanceof InvokeManageNetFolderRootsDlgEvent.Handler);          break;
			case INVOKE_MANAGE_GROUPS_DLG:				       hasHandler = (eventHandler instanceof InvokeManageGroupsDlgEvent.Handler);		           break;
			case INVOKE_MANAGE_MOBILE_DEVICES_DLG:			   hasHandler = (eventHandler instanceof InvokeManageMobileDevicesDlgEvent.Handler);		   break;
			case INVOKE_MANAGE_PROXY_IDENTITIES_DLG:		   hasHandler = (eventHandler instanceof InvokeManageProxyIdentitiesDlgEvent.Handler);		   break;
			case INVOKE_MANAGE_TEAMS_DLG:				       hasHandler = (eventHandler instanceof InvokeManageTeamsDlgEvent.Handler);		           break;
			case INVOKE_MANAGE_USERS_DLG:				       hasHandler = (eventHandler instanceof InvokeManageUsersDlgEvent.Handler);		           break;
			case INVOKE_NAME_COMPLETION_SETTINGS_DLG:	       hasHandler = (eventHandler instanceof InvokeNameCompletionSettingsDlgEvent.Handler);		   break;
			case INVOKE_PRINCIPAL_DESKTOP_SETTINGS_DLG:        hasHandler = (eventHandler instanceof InvokePrincipalDesktopSettingsDlgEvent.Handler);      break;
			case INVOKE_PRINCIPAL_MOBILE_SETTINGS_DLG:         hasHandler = (eventHandler instanceof InvokePrincipalMobileSettingsDlgEvent.Handler);       break;
			case INVOKE_RENAME_ENTITY:					       hasHandler = (eventHandler instanceof InvokeRenameEntityEvent.Handler);                     break;
			case INVOKE_REPLY:                      	       hasHandler = (eventHandler instanceof InvokeReplyEvent.Handler);                            break;
			case INVOKE_RUN_A_REPORT_DLG:				       hasHandler = (eventHandler instanceof InvokeRunAReportDlgEvent.Handler);		               break;
			case INVOKE_SEND_EMAIL_TO_TEAM:                    hasHandler = (eventHandler instanceof InvokeSendEmailToTeamEvent.Handler);                  break;
			case INVOKE_SEND_TO_FRIEND:					       hasHandler = (eventHandler instanceof InvokeSendToFriendEvent.Handler);			           break;
			case INVOKE_SHARE:                      	       hasHandler = (eventHandler instanceof InvokeShareEvent.Handler);                            break;
			case INVOKE_SHARE_BINDER:					       hasHandler = (eventHandler instanceof InvokeShareBinderEvent.Handler);			           break;
			case INVOKE_SIGN_GUESTBOOK:					       hasHandler = (eventHandler instanceof InvokeSignGuestbookEvent.Handler);                    break;
			case INVOKE_SIMPLE_PROFILE:             	       hasHandler = (eventHandler instanceof InvokeSimpleProfileEvent.Handler);                    break;
			case INVOKE_SUBSCRIBE:                  	       hasHandler = (eventHandler instanceof InvokeSubscribeEvent.Handler);                        break;
			case INVOKE_TAG:                        	       hasHandler = (eventHandler instanceof InvokeTagEvent.Handler);                              break;
			case INVOKE_USER_PROPERTIES_DLG:				   hasHandler = (eventHandler instanceof InvokeUserPropertiesDlgEvent.Handler);		           break;
			case INVOKE_USER_SHARE_RIGHTS_DLG:				   hasHandler = (eventHandler instanceof InvokeUserShareRightsDlgEvent.Handler);		       break;
			case INVOKE_WORKSPACE_SHARE_RIGHTS:			       hasHandler = (eventHandler instanceof InvokeWorkspaceShareRightsEvent.Handler);		       break;
			
			case JSP_LAYOUT_CHANGED:                   	       hasHandler = (eventHandler instanceof JspLayoutChangedEvent.Handler);                       break;

			case LDAP_SYNC_STATUS:        			       	   hasHandler = (eventHandler instanceof LdapSyncStatusEvent.Handler);          	           break;

			case LOGIN:                             	       hasHandler = (eventHandler instanceof LoginEvent.Handler);                                  break;
			case LOGOUT:                            	       hasHandler = (eventHandler instanceof LogoutEvent.Handler);                                 break;
			case PRE_LOGOUT:                        	       hasHandler = (eventHandler instanceof PreLogoutEvent.Handler);                              break;

			case PREVIEW_LANDING_PAGE:      			       hasHandler = (eventHandler instanceof PreviewLandingPageEvent.Handler);     	               break;

			case MANAGE_DEFAULT_USER_SETTINGS:                 hasHandler = (eventHandler instanceof ManageDefaultUserSettingsEvent.Handler);              break;
			case MANAGE_SHARES_SELECTED_ENTITIES:              hasHandler = (eventHandler instanceof ManageSharesSelectedEntitiesEvent.Handler);           break;
			case MANAGE_USER_VISIBILITY:                       hasHandler = (eventHandler instanceof ManageUserVisibilityEvent.Handler);                   break;
			case MARK_ENTRY_READ:                   	       hasHandler = (eventHandler instanceof MarkEntryReadEvent.Handler);                          break;
			case MARK_ENTRY_UNREAD:                 	       hasHandler = (eventHandler instanceof MarkEntryUnreadEvent.Handler);                        break;
			case MARK_FOLDER_CONTENTS_READ:                    hasHandler = (eventHandler instanceof MarkFolderContentsReadEvent.Handler);                 break;
			case MARK_FOLDER_CONTENTS_UNREAD:                  hasHandler = (eventHandler instanceof MarkFolderContentsUnreadEvent.Handler);               break;
			
			case NET_FOLDER_CREATED:        			       hasHandler = (eventHandler instanceof NetFolderCreatedEvent.Handler);          	           break;
			case NET_FOLDER_MODIFIED:        			       hasHandler = (eventHandler instanceof NetFolderModifiedEvent.Handler);         	           break;
			case NET_FOLDER_ROOT_CREATED:        		       hasHandler = (eventHandler instanceof NetFolderRootCreatedEvent.Handler);                   break;
			case NET_FOLDER_ROOT_MODIFIED:        		       hasHandler = (eventHandler instanceof NetFolderRootModifiedEvent.Handler);                  break;

			case QUICK_FILTER:                 	               hasHandler = (eventHandler instanceof QuickFilterEvent.Handler);                            break;
			
			case RELOAD_DIALOG_CONTENT:                   	   hasHandler = (eventHandler instanceof ReloadDialogContentEvent.Handler);                    break;
			case RESET_ENTRY_MENU:                   	       hasHandler = (eventHandler instanceof ResetEntryMenuEvent.Handler);                         break;
			
			case SEARCH_ADVANCED:						       hasHandler = (eventHandler instanceof SearchAdvancedEvent.Handler);                         break;
			case SEARCH_FIND_RESULTS:               	       hasHandler = (eventHandler instanceof SearchFindResultsEvent.Handler);                      break;
			case SEARCH_RECENT_PLACE:               	       hasHandler = (eventHandler instanceof SearchRecentPlaceEvent.Handler);                      break;
			case SEARCH_SAVED:                      	       hasHandler = (eventHandler instanceof SearchSavedEvent.Handler);                            break;
			case SEARCH_SIMPLE:                     	       hasHandler = (eventHandler instanceof SearchSimpleEvent.Handler);                           break;
			case SEARCH_TAG:                        	       hasHandler = (eventHandler instanceof SearchTagEvent.Handler);                              break;

			case SET_SHARE_RIGHTS:						       hasHandler = (eventHandler instanceof SetShareRightsEvent.Handler);                         break;

			case SHARE_EXPIRATION_VALUE_CHANGED:           	   hasHandler = (eventHandler instanceof ShareExpirationValueChangedEvent.Handler);            break;

			case SHOW_BLOG_FOLDER:						       hasHandler = (eventHandler instanceof ShowBlogFolderEvent.Handler);		   		           break;
			case SHOW_CALENDAR_FOLDER:					       hasHandler = (eventHandler instanceof ShowCalendarFolderEvent.Handler);		               break;
			case SHOW_COLLECTION:						       hasHandler = (eventHandler instanceof ShowCollectionEvent.Handler);				           break;
			case SHOW_COLLECTION_VIEW:				           hasHandler = (eventHandler instanceof ShowCollectionViewEvent.Handler);		               break;
			case SHOW_CONTENT_CONTROL:                         hasHandler = (eventHandler instanceof ShowContentControlEvent.Handler);                     break;
			case SHOW_DISCUSSION_FOLDER:				       hasHandler = (eventHandler instanceof ShowDiscussionFolderEvent.Handler);		           break;
			case SHOW_DISCUSSION_WORKSPACE:				       hasHandler = (eventHandler instanceof ShowDiscussionWSEvent.Handler);			           break;
			case SHOW_FILE_FOLDER:						       hasHandler = (eventHandler instanceof ShowFileFolderEvent.Handler);		                   break;
			case SHOW_FOLDER_ENTRY:						       hasHandler = (eventHandler instanceof ShowFolderEntryEvent.Handler);		                   break;
			case SHOW_GENERIC_WORKSPACE:				       hasHandler = (eventHandler instanceof ShowGenericWSEvent.Handler);			   	           break;
			case SHOW_GLOBAL_WORKSPACE:					       hasHandler = (eventHandler instanceof ShowGlobalWSEvent.Handler);			   	           break;
			case SHOW_GUESTBOOK_FOLDER:				           hasHandler = (eventHandler instanceof ShowGuestbookFolderEvent.Handler);		               break;
			case SHOW_HOME_WORKSPACE:					       hasHandler = (eventHandler instanceof ShowHomeWSEvent.Handler);			   	   	           break;
			case SHOW_LANDING_PAGE:						       hasHandler = (eventHandler instanceof ShowLandingPageEvent.Handler);			               break;
			case SHOW_MICRO_BLOG_FOLDER:				       hasHandler = (eventHandler instanceof ShowMicroBlogFolderEvent.Handler);		               break;
			case SHOW_MILESTONE_FOLDER:				           hasHandler = (eventHandler instanceof ShowMilestoneFolderEvent.Handler);		               break;
			case SHOW_MIRRORED_FILE_FOLDER:				       hasHandler = (eventHandler instanceof ShowMirroredFileFolderEvent.Handler);		           break;
			case SHOW_NET_FOLDERS_WORKSPACE:			       hasHandler = (eventHandler instanceof ShowNetFoldersWSEvent.Handler);			           break;
			case SHOW_PERSONAL_WORKSPACE:				       hasHandler = (eventHandler instanceof ShowPersonalWorkspaceEvent.Handler);		           break;
			case SHOW_PERSONAL_WORKSPACES:				       hasHandler = (eventHandler instanceof ShowPersonalWorkspacesEvent.Handler);		           break;
			case SHOW_PHOTO_ALBUM_FOLDER:					   hasHandler = (eventHandler instanceof ShowPhotoAlbumFolderEvent.Handler);		           break;
			case SHOW_PROJECT_MANAGEMENT_WORKSPACE:		       hasHandler = (eventHandler instanceof ShowProjectManagementWSEvent.Handler);	               break;
			case SHOW_SURVEY_FOLDER:				           hasHandler = (eventHandler instanceof ShowSurveyFolderEvent.Handler);		               break;
			case SHOW_TASK_FOLDER:						       hasHandler = (eventHandler instanceof ShowTaskFolderEvent.Handler);		                   break;
			case SHOW_TEAM_ROOT_WORKSPACE:				       hasHandler = (eventHandler instanceof ShowTeamRootWSEvent.Handler);			   	           break;
			case SHOW_TEAM_WORKSPACE:					       hasHandler = (eventHandler instanceof ShowTeamWSEvent.Handler);			   		           break;
			case SHOW_TRASH:						           hasHandler = (eventHandler instanceof ShowTrashEvent.Handler);		                       break;
			case SHOW_VIEW_PERMALINKS:						   hasHandler = (eventHandler instanceof ShowViewPermalinksEvent.Handler);		               break;
			case SHOW_WIKI_FOLDER:					           hasHandler = (eventHandler instanceof ShowWikiFolderEvent.Handler);		                   break;
			
			case HIDE_ACCESSORIES:						       hasHandler = (eventHandler instanceof HideAccessoriesEvent.Handler);		   	               break;
			case HIDE_HTML_ELEMENT:						       hasHandler = (eventHandler instanceof HideHtmlElementEvent.Handler);		   	               break;
			case HIDE_USER_LIST:						       hasHandler = (eventHandler instanceof HideUserListEvent.Handler);		   	               break;
			case SHOW_ACCESSORIES:						       hasHandler = (eventHandler instanceof ShowAccessoriesEvent.Handler);		   	               break;
			case SHOW_HTML_ELEMENT:						       hasHandler = (eventHandler instanceof ShowHtmlElementEvent.Handler);		   	               break;
			case SHOW_USER_LIST:						       hasHandler = (eventHandler instanceof ShowUserListEvent.Handler);		   	               break;
			
			case GET_CURRENT_VIEW_INFO:                        hasHandler = (eventHandler instanceof GetCurrentViewInfoEvent.Handler);                     break;
			case GET_SIDEBAR_COLLECTION:                       hasHandler = (eventHandler instanceof GetSidebarCollectionEvent.Handler);                   break;
			case REFRESH_SIDEBAR_TREE:                 	       hasHandler = (eventHandler instanceof RefreshSidebarTreeEvent.Handler);                     break;
			case REROOT_SIDEBAR_TREE:                 	       hasHandler = (eventHandler instanceof RerootSidebarTreeEvent.Handler);                      break;
			case SET_FILR_ACTION_FROM_COLLECTION_TYPE:         hasHandler = (eventHandler instanceof SetFilrActionFromCollectionTypeEvent.Handler);        break;
			case SIDEBAR_HIDE:                      	       hasHandler = (eventHandler instanceof SidebarHideEvent.Handler);                            break;
			case SIDEBAR_SHOW:                      	       hasHandler = (eventHandler instanceof SidebarShowEvent.Handler);                            break;
			
			case SIZE_CHANGED:                      	       hasHandler = (eventHandler instanceof SizeChangedEvent.Handler);                            break;
			
			case TASK_DELETE:                       	       hasHandler = (eventHandler instanceof TaskDeleteEvent.Handler);                             break;
			case TASK_HIERARCHY_DISABLED:           	       hasHandler = (eventHandler instanceof TaskHierarchyDisabledEvent.Handler);                  break;
			case TASK_LIST_READY:                              hasHandler = (eventHandler instanceof TaskListReadyEvent.Handler);                          break;
			case TASK_MOVE_DOWN:                    	       hasHandler = (eventHandler instanceof TaskMoveDownEvent.Handler);                           break;
			case TASK_MOVE_LEFT:                    	       hasHandler = (eventHandler instanceof TaskMoveLeftEvent.Handler);                           break;
			case TASK_MOVE_RIGHT:                   	       hasHandler = (eventHandler instanceof TaskMoveRightEvent.Handler);                          break;
			case TASK_MOVE_UP:                      	       hasHandler = (eventHandler instanceof TaskMoveUpEvent.Handler);                             break;
			case TASK_NEW_TASK:                     	       hasHandler = (eventHandler instanceof TaskNewTaskEvent.Handler);                            break;
			case TASK_PICK_DATE:                    	       hasHandler = (eventHandler instanceof TaskPickDateEvent.Handler);                           break;
			case TASK_PURGE:                        	       hasHandler = (eventHandler instanceof TaskPurgeEvent.Handler);                              break;
			case TASK_SET_PERCENT_DONE:             	       hasHandler = (eventHandler instanceof TaskSetPercentDoneEvent.Handler);                     break;
			case TASK_SET_PRIORITY:                 	       hasHandler = (eventHandler instanceof TaskSetPriorityEvent.Handler);                        break;
			case TASK_SET_STATUS:                   	       hasHandler = (eventHandler instanceof TaskSetStatusEvent.Handler);                          break;
			case TASK_VIEW:                         	       hasHandler = (eventHandler instanceof TaskViewEvent.Handler);                               break;
			
			case TRASH_PURGE_ALL:                              hasHandler = (eventHandler instanceof TrashPurgeAllEvent.Handler);                          break;
			case TRASH_PURGE_SELECTED_ENTITIES:                hasHandler = (eventHandler instanceof TrashPurgeSelectedEntitiesEvent.Handler);             break;
			case TRASH_RESTORE_ALL:                            hasHandler = (eventHandler instanceof TrashRestoreAllEvent.Handler);                        break;
			case TRASH_RESTORE_SELECTED_ENTITIES:              hasHandler = (eventHandler instanceof TrashRestoreSelectedEntitiesEvent.Handler);           break;
			
			case TREE_NODE_COLLAPSED:                          hasHandler = (eventHandler instanceof TreeNodeCollapsedEvent.Handler);                      break;
			case TREE_NODE_EXPANDED:                           hasHandler = (eventHandler instanceof TreeNodeExpandedEvent.Handler);                       break;
			
			case TRACK_CURRENT_BINDER:              	       hasHandler = (eventHandler instanceof TrackCurrentBinderEvent.Handler);                     break;
			case UNTRACK_CURRENT_BINDER:            	       hasHandler = (eventHandler instanceof UntrackCurrentBinderEvent.Handler);                   break;
			case UNTRACK_CURRENT_PERSON:            	       hasHandler = (eventHandler instanceof UntrackCurrentPersonEvent.Handler);                   break;
			
			case VIEW_ALL_ENTRIES:                  	       hasHandler = (eventHandler instanceof ViewAllEntriesEvent.Handler);                         break;
			case VIEW_CURRENT_BINDER_TEAM_MEMBERS:  	       hasHandler = (eventHandler instanceof ViewCurrentBinderTeamMembersEvent.Handler);           break;
			case VIEW_FORUM_ENTRY:                  	       hasHandler = (eventHandler instanceof ViewForumEntryEvent.Handler);                         break;
			case VIEW_PINNED_ENTRIES:                  	       hasHandler = (eventHandler instanceof ViewPinnedEntriesEvent.Handler);                      break;
			case VIEW_RESOURCE_LIBRARY:             	       hasHandler = (eventHandler instanceof ViewResourceLibraryEvent.Handler);                    break;
			case VIEW_SELECTED_ENTRY:                  	       hasHandler = (eventHandler instanceof ViewSelectedEntryEvent.Handler);                      break;
			case VIEW_TEAMING_FEED:                 	       hasHandler = (eventHandler instanceof ViewTeamingFeedEvent.Handler);                        break;
			case VIEW_UNREAD_ENTRIES:               	       hasHandler = (eventHandler instanceof ViewUnreadEntriesEvent.Handler);                      break;
			case VIEW_WHATS_NEW_IN_BINDER:				       hasHandler = (eventHandler instanceof ViewWhatsNewInBinderEvent.Handler);                   break;
			case VIEW_WHATS_UNSEEN_IN_BINDER:			       hasHandler = (eventHandler instanceof ViewWhatsUnseenInBinderEvent.Handler);                break;
			case VIEW_WHO_HAS_ACCESS:                  	       hasHandler = (eventHandler instanceof ViewWhoHasAccessEvent.Handler);                       break;
			
			case ADD_PRINCIPAL_ADMIN_RIGHTS:                   hasHandler = (eventHandler instanceof AddPrincipalAdminRightsEvent.Handler);                break;
			case CHANGE_ENTRY_TYPE_SELECTED_ENTITIES:          hasHandler = (eventHandler instanceof ChangeEntryTypeSelectedEntitiesEvent.Handler);        break;
			case CHANGE_FAVORITE_STATE:                        hasHandler = (eventHandler instanceof ChangeFavoriteStateEvent.Handler);                    break;
			case CHECK_MANAGE_DLG_ACTIVE:                      hasHandler = (eventHandler instanceof CheckManageDlgActiveEvent.Handler);                   break;
			case CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES: hasHandler = (eventHandler instanceof ClearScheduledWipeSelectedMobileDevicesEvent.Handler);break;
			case CLEAR_SELECTED_USERS_ADHOC_FOLDERS:           hasHandler = (eventHandler instanceof ClearSelectedUsersAdHocFoldersEvent.Handler);         break;
			case CLEAR_SELECTED_USERS_DOWNLOAD:                hasHandler = (eventHandler instanceof ClearSelectedUsersDownloadEvent.Handler);             break;
			case CLEAR_SELECTED_USERS_WEBACCESS:               hasHandler = (eventHandler instanceof ClearSelectedUsersWebAccessEvent.Handler);            break;
			case COPY_PUBLIC_LINK_SELECTED_ENTITIES:           hasHandler = (eventHandler instanceof CopyPublicLinkSelectedEntitiesEvent.Handler);         break;
			case COPY_SELECTED_ENTITIES:                       hasHandler = (eventHandler instanceof CopySelectedEntitiesEvent.Handler);                   break;
			case DELETE_SELECTED_CUSTOMIZED_EMAIL_TEMPLATES:   hasHandler = (eventHandler instanceof DeleteSelectedCustomizedEmailTemplatesEvent.Handler); break;
			case DELETE_SELECTED_ENTITIES:                     hasHandler = (eventHandler instanceof DeleteSelectedEntitiesEvent.Handler);                 break;
			case DELETE_SELECTED_MOBILE_DEVICES:               hasHandler = (eventHandler instanceof DeleteSelectedMobileDevicesEvent.Handler);            break;
			case DELETE_SELECTED_PROXY_IDENTITIES:             hasHandler = (eventHandler instanceof DeleteSelectedProxyIdentitiesEvent.Handler);          break;
			case DELETE_SELECTED_USERS:                        hasHandler = (eventHandler instanceof DeleteSelectedUsersEvent.Handler);                    break;
			case DIALOG_CLOSED:                                hasHandler = (eventHandler instanceof DialogClosedEvent.Handler);                           break;
			case DISABLE_SELECTED_USERS:                       hasHandler = (eventHandler instanceof DisableSelectedUsersEvent.Handler);                   break;
			case DISABLE_SELECTED_USERS_ADHOC_FOLDERS:         hasHandler = (eventHandler instanceof DisableSelectedUsersAdHocFoldersEvent.Handler);       break;
			case DISABLE_SELECTED_USERS_DOWNLOAD:              hasHandler = (eventHandler instanceof DisableSelectedUsersDownloadEvent.Handler);           break;
			case DISABLE_SELECTED_USERS_WEBACCESS:             hasHandler = (eventHandler instanceof DisableSelectedUsersWebAccessEvent.Handler);          break;
			case DOWNLOAD_FOLDER_AS_CSV_FILE:                  hasHandler = (eventHandler instanceof DownloadFolderAsCSVFileEvent.Handler);                break;
			case EDIT_PUBLIC_LINK_SELECTED_ENTITIES:           hasHandler = (eventHandler instanceof EditPublicLinkSelectedEntitiesEvent.Handler);         break;
			case EMAIL_PUBLIC_LINK_SELECTED_ENTITIES:          hasHandler = (eventHandler instanceof EmailPublicLinkSelectedEntitiesEvent.Handler);        break;
			case ENABLE_SELECTED_USERS:                        hasHandler = (eventHandler instanceof EnableSelectedUsersEvent.Handler);                    break;
			case ENABLE_SELECTED_USERS_ADHOC_FOLDERS:          hasHandler = (eventHandler instanceof EnableSelectedUsersAdHocFoldersEvent.Handler);        break;
			case ENABLE_SELECTED_USERS_DOWNLOAD:               hasHandler = (eventHandler instanceof EnableSelectedUsersDownloadEvent.Handler);            break;
			case ENABLE_SELECTED_USERS_WEBACCESS:              hasHandler = (eventHandler instanceof EnableSelectedUsersWebAccessEvent.Handler);           break;
			case FIND_CONTROL_BROWSE:                          hasHandler = (eventHandler instanceof FindControlBrowseEvent.Handler);                      break;
			case FOLDER_ENTRY_ACTION_COMPLETE:                 hasHandler = (eventHandler instanceof FolderEntryActionCompleteEvent.Handler);              break;
			case FORCE_FILES_UNLOCK:                           hasHandler = (eventHandler instanceof ForceFilesUnlockEvent.Handler);                       break;
			case FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD:      hasHandler = (eventHandler instanceof ForceSelectedUsersToChangePasswordEvent.Handler);     break;
			case GET_MANAGE_TITLE:                             hasHandler = (eventHandler instanceof GetManageTitleEvent.Handler);                         break;
			case GET_MASTHEAD_LEFT_EDGE:                       hasHandler = (eventHandler instanceof GetMastHeadLeftEdgeEvent.Handler);                    break;
			case HIDE_SELECTED_SHARES:                         hasHandler = (eventHandler instanceof HideSelectedSharesEvent.Handler);                     break;
			case LOCK_SELECTED_ENTITIES:                       hasHandler = (eventHandler instanceof LockSelectedEntitiesEvent.Handler);                   break;
			case MAILTO_PUBLIC_LINK_ENTITY:                    hasHandler = (eventHandler instanceof MailToPublicLinkEntityEvent.Handler);                 break;
			case MARK_READ_SELECTED_ENTITIES:                  hasHandler = (eventHandler instanceof MarkReadSelectedEntitiesEvent.Handler);               break;
			case MARK_UNREAD_SELECTED_ENTITIES:                hasHandler = (eventHandler instanceof MarkUnreadSelectedEntitiesEvent.Handler);             break;
			case MOBILE_DEVICE_WIPE_SCHEDULE_CHANGED:          hasHandler = (eventHandler instanceof MobileDeviceWipeScheduleStateChangedEvent.Handler);   break;
			case MOVE_SELECTED_ENTITIES:                       hasHandler = (eventHandler instanceof MoveSelectedEntitiesEvent.Handler);                   break;
			case PUBLIC_COLLECTION_STATE_CHANGED:              hasHandler = (eventHandler instanceof PublicCollectionStateChangedEvent.Handler);           break;
			case RESET_VELOCITY_ENGINE:                        hasHandler = (eventHandler instanceof ResetVelocityEngineEvent.Handler);                    break;
			case SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES:        hasHandler = (eventHandler instanceof ScheduleWipeSelectedMobileDevicesEvent.Handler);      break;
			case SET_DESKTOP_DOWNLOAD_APP_CONTROL_VISIBILITY:  hasHandler = (eventHandler instanceof SetDesktopDownloadAppControlVisibilityEvent.Handler); break;
			case SET_FOLDER_SORT:                              hasHandler = (eventHandler instanceof SetFolderSortEvent.Handler);                          break;
			case SET_SELECTED_BINDER_SHARE_RIGHTS:             hasHandler = (eventHandler instanceof SetSelectedBinderShareRightsEvent.Handler);           break;
			case SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS:         hasHandler = (eventHandler instanceof SetSelectedPrincipalsAdminRightsEvent.Handler);       break;
			case SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY: hasHandler = (eventHandler instanceof SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler); break;
			case SET_SELECTED_USER_DESKTOP_SETTINGS:           hasHandler = (eventHandler instanceof SetSelectedUserDesktopSettingsEvent.Handler);         break;
			case SET_SELECTED_USER_MOBILE_SETTINGS:            hasHandler = (eventHandler instanceof SetSelectedUserMobileSettingsEvent.Handler);          break;
			case SET_SELECTED_USER_SHARE_RIGHTS:               hasHandler = (eventHandler instanceof SetSelectedUserShareRightsEvent.Handler);             break;
			case SHARE_SELECTED_ENTITIES:                      hasHandler = (eventHandler instanceof ShareSelectedEntitiesEvent.Handler);                  break;
			case SHOW_SELECTED_SHARES:                         hasHandler = (eventHandler instanceof ShowSelectedSharesEvent.Handler);                     break;
			case SUBSCRIBE_SELECTED_ENTITIES:                  hasHandler = (eventHandler instanceof SubscribeSelectedEntitiesEvent.Handler);              break;
			case TOGGLE_SHARED_VIEW:                  	       hasHandler = (eventHandler instanceof ToggleSharedViewEvent.Handler);                       break;
			case UNLOCK_SELECTED_ENTITIES:                     hasHandler = (eventHandler instanceof UnlockSelectedEntitiesEvent.Handler);                 break;
			case WINDOW_TITLE_SET:                             hasHandler = (eventHandler instanceof WindowTitleSetEvent.Handler);                         break;
			case ZIP_AND_DOWNLOAD_FOLDER:                      hasHandler = (eventHandler instanceof ZipAndDownloadFolderEvent.Handler);                   break;
			case ZIP_AND_DOWNLOAD_SELECTED_FILES:              hasHandler = (eventHandler instanceof ZipAndDownloadSelectedFilesEvent.Handler);            break;
			
			case UNDEFINED:
				// Ignore.
				continue;
				
			default:
				// Somebody forget to add a validation handler for
				// this!
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().eventHandling_Validation_NoValidator(te.name()));
				continue;
			}

			// Is something wrong with this (i.e., we need a handler
			// but don't have one or we have a handler that wasn't in
			// the list?
			if (needsHandler != hasHandler) {
				// Yes!  Tell the user about the problem.
				String teName = te.name();
				String className = eventHandler.getClass().getName();
				String error = "*???*";
				if      (needsHandler) error = GwtTeaming.getMessages().eventHandling_Validation_NoHandler(teName, className);
				else if (hasHandler)   error = GwtTeaming.getMessages().eventHandling_Validation_NotListed(teName, className);
				GwtClientHelper.deferredAlert(error);
			}
		}
	}
}
