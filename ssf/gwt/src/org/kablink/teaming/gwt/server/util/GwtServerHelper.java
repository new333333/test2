/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.TopRankedInfo.TopRankedType;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.CreateGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.MarkupStringReplacementCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ReplyToEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmdType;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.license.LicenseModule.LicenseOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.Tabs.TabEntry;
import org.kablink.util.PropertyNotFoundException;
import org.kablink.util.search.Constants;


/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
public class GwtServerHelper {
	protected static Log m_logger = LogFactory.getLog(GwtServerHelper.class);

	// The following are used to classify various binders based on
	// their default view definition.  See getFolderType() and
	// getWorkspaceType().
	private static final String VIEW_FOLDER_GUESTBOOK      = "_guestbookFolder";
	private static final String VIEW_FOLDER_MIRRORED_FILE  = "_mirroredFileFolder";	
	private static final String VIEW_WORKSPACE_DISCUSSIONS = "_discussions";
	private static final String VIEW_WORKSPACE_PROJECT     = "_projectWorkspace";
	private static final String VIEW_WORKSPACE_TEAM        = "_team_workspace";
	private static final String VIEW_WORKSPACE_USER        = "_userWorkspace";
	private static final String VIEW_WORKSPACE_WELCOME     = "_welcomeWorkspace";
	private static final String VIEW_WORKSPACE_GENERIC     = "_workspace";

	/*
	 * Inner class used assist in the construction of the
	 * List<TreeInfo> for buckets workspace trees.
	 */
	private static class BinderData {
		private BucketInfo	m_bucketInfo;	//
		private Long		m_binderId;		//
		
		/**
		 * Class constructor.
		 */
		public BinderData() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		private BucketInfo getBucketInfo() {return m_bucketInfo;}
		private Long       getBinderId()   {return m_binderId;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		private void setBucketInfo( BucketInfo bucketInfo) {m_bucketInfo  = bucketInfo;}
		private void setBinderId(   Long       binderId)   {m_binderId    = binderId;  }
	}
	   
	/*
	 * Inner class used compare two GwtAdminAction objects.
	 */
	private static class GwtAdminActionComparator implements Comparator<GwtAdminAction>
	{
		private Collator m_collator;
		
		/**
		 * Class constructor.
		 */
		public GwtAdminActionComparator()
		{
			m_collator = Collator.getInstance();
			m_collator.setStrength( Collator.IDENTICAL );
		}// end GwtAdminActionComparator()

	      
		/**
		 * Implements the Comparator.compare() method on two GwtAdminAction objects.
		 *
		 * Returns:
		 *    -1 if adminAction1 <  adminAction2;
		 *     0 if adminAction1 == adminAction2; and
		 *     1 if adminAction1 >  adminAction2.
		 */
		public int compare( GwtAdminAction adminAction1, GwtAdminAction adminAction2 )
		{
			String s1, s2;

			s1 = adminAction1.getLocalizedName();
			if ( s1 == null )
				s1 = "";

			s2 = adminAction2.getLocalizedName();
			if ( s2 == null )
				s2 = "";

			return 	m_collator.compare( s1, s2 );
		}// end compare()
	}// end GwtAdminActionComparator

	/**
	 * Inner class used within the GWT server code to dump profiling
	 * information to the system log.
	 */
	public static class GwtServerProfiler {
		private boolean m_debugEnabled;	// true m_debugLogger has debugging enabled.  false -> It doesn't.
		private Log     m_debugLogger;	// The Log that profiling information is written to.
		private long    m_debugBegin;	// If m_debugEnabled is true, contains the system time in MS that start() was called.
		private String  m_debugFrom;	// Contains information about where the profile is being used from.

		/*
		 * Class constructor.
		 */
		private GwtServerProfiler(Log logger) {
			m_debugLogger  = logger;
			m_debugEnabled = m_debugLogger.isDebugEnabled();
		}
		
		/**
		 * If the logger has debugging enabled, dumps a message
		 * to the log regarding how long an operation took.
		 */
		public void end() {			
			if (m_debugEnabled) {
				long diff = System.currentTimeMillis() - m_debugBegin;
				m_debugLogger.debug(m_debugFrom + ":  Ended in " + diff + "ms");
			}
			
			SimpleProfiler.stop(m_debugFrom);
		}
		
		/**
		 * Creates a GwtServerProfiler object based on a Log and called
		 * from string.  If the logger has debugging enabled, dumps a
		 * message to the log regarding the profiling being started.
		 * 
		 * @param logger
		 * @param from
		 * 
		 * @return
		 */
		public static GwtServerProfiler start(Log logger, String from) {			
			GwtServerProfiler reply = new GwtServerProfiler(logger);
			reply.m_debugFrom = from;
			
			if (reply.m_debugEnabled) {
				reply.m_debugBegin = System.currentTimeMillis();
				
				reply.m_debugLogger.debug(from + ":  Starting...");
			}
			
			SimpleProfiler.start(reply.m_debugFrom);
			return reply;
		}
		
		public static GwtServerProfiler start(String from) {
			// Always use the initial form of the method.
			return start(m_logger, from);
		}
	}
	
	/**
	 * Inner class used compare two SavedSearchInfo objects.
	 */
	private static class SavedSearchInfoComparator implements Comparator<SavedSearchInfo> {
		/**
		 * Class constructor.
		 */
		public SavedSearchInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * SavedSearchInfo's.
		 *
		 * Returns:
		 *    -1 if ssi1 <  ssi2;
		 *     0 if ssi1 == ssi2; and
		 *     1 if ssi1 >  ssi2.
		 *     
		 * @param ssi1
		 * @param ssi2
		 * 
		 * @return
		 */
		public int compare(SavedSearchInfo ssi1, SavedSearchInfo ssi2) {
			return MiscUtil.safeSColatedCompare(ssi1.getName(), ssi2.getName());
		}
	}	   
	   
	/**
	 * Inner class used compare two TeamInfo objects.
	 */
	private static class TeamInfoComparator implements Comparator<TeamInfo> {
		/**
		 * Class constructor.
		 */
		public TeamInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * TeamInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		public int compare(TeamInfo ti1, TeamInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getTitle(), ti2.getTitle());
		}
	}	   
	   
	private static class GroupInfoComparator implements Comparator<GroupInfo> {
		/**
		 * Class constructor.
		 */
		public GroupInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * GroupInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		public int compare(GroupInfo ti1, GroupInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getTitle(), ti2.getTitle());
		}
	}	   
	   
	/*
	 * Inner class used to compare two TreeInfo's.
	 */
	private static class TreeInfoComparator implements Comparator<TreeInfo> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TreeInfoComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TreeInfo's by their assignee's name.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		@Override
		public int compare(TreeInfo ti1, TreeInfo ti2) {
			BucketInfo bi1 = ti1.getBucketInfo();
			BucketInfo bi2 = ti2.getBucketInfo();
			
			String title1 = ((null == bi1) ? ti1.getBinderTitle() : bi1.getBucketPageTuple());
			String title2 = ((null == bi2) ? ti2.getBinderTitle() : bi2.getBucketPageTuple());

			int reply;
			if (m_ascending)
			     reply = MiscUtil.safeSColatedCompare(title1, title2);
			else reply = MiscUtil.safeSColatedCompare(title2, title1);
			return reply;
		}
	}

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtServerHelper() {
		// Nothing to do.
	}

	/**
	 * Add a tag to the given binder.
	 */
	public static void addBinderTag( BinderModule bm, Binder binder, TagInfo tagInfo )
	{
		boolean community;
		Long binderId;
		String tagName;

		// Define the new tag.
		community = tagInfo.isCommunityTag();
		binderId = binder.getId();
		tagName = tagInfo.getTagName();
		bm.setTag( binderId, tagName, community );
	}
	
	/**
	 * Add a tag to the given entry
	 */
	public static void addEntryTag( FolderModule fm, Long entryId, TagInfo tagInfo )
	{
		String tagName;
		TagType tagType;
		boolean community;
		
		tagName = tagInfo.getTagName();
		tagType = tagInfo.getTagType();
		community = false;
		if ( tagType == TagType.COMMUNITY )
			community = true;
		
		fm.setTag( null, entryId, tagName, community );
	}
	

	/**
	 * Add a reply to the given entry.
	 */
	@SuppressWarnings("unchecked")
	public static FolderEntry addReply( AllModulesInjected bs, String entryId, String title, String desc )
		throws WriteEntryDataException, WriteFilesException
	{
		FolderModule folderModule;
		FolderEntry entry;
		Long entryIdL;
		Long binderIdL;
		String replyDefId;
		Map fileMap;
		Map<String, String> inputMap;
		MapInputData inputData;

		folderModule = bs.getFolderModule();
		entryIdL = new Long( entryId );
		
		// Get the id of the binder the given entry lives in.
		entry = folderModule.getEntry( null, entryIdL );
		binderIdL = entry.getParentBinder().getId();
		
		// Get the entry's reply definition id.
		{
			Document entryDefDoc = null;

			replyDefId = null;
			entryDefDoc = entry.getEntryDefDoc();
			if ( entryDefDoc != null )
			{
				List replyStyles = null;

				// Get the reply styles for this entry.
				replyStyles = DefinitionUtils.getPropertyValueList( entryDefDoc.getRootElement(), "replyStyle" );

				// Do we have any reply styles?
				if ( replyStyles != null && replyStyles.isEmpty() == false )
				{
					int i;

					// Yes, find the one whose name is "_comment"
					for (i = 0; i < replyStyles.size() && replyDefId == null; i++)
					{
						String replyStyleId;

						replyStyleId = (String)replyStyles.get(i);
						
						try
						{
							Definition replyDef;
							String replyName;

							replyDef = bs.getDefinitionModule().getDefinition( replyStyleId );
							replyName = replyDef.getName();
							if ( replyName != null && replyName.equalsIgnoreCase( "_comment" ) )
								replyDefId = replyStyleId;
						}
						catch ( NoDefinitionByTheIdException e )
						{
							continue;
						}
					}
					
					if ( replyDefId == null )
					{
						// use the first one.
						replyDefId = (String) replyStyles.get( 0 );
					}
				}
			}

			if ( replyDefId == null )
				replyDefId = entry.getEntryDefId();
		}
		
		// Create an empty file map.
		fileMap = new HashMap();

		inputMap = new HashMap<String, String>();
		inputMap.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_HTML ) );
		inputData = new MapInputData( inputMap );

    	return folderModule.addReply( binderIdL, entryIdL, replyDefId, inputData, fileMap, null );
	}
	
	
	/**
	 * Adds a Trash folder to the TreeInfo.
	 * 
	 * Note:  At the point this gets called, it is assumed that the
	 *        caller has done whatever checks need to be done to
	 *        validate the user's access to the trash on this folder.
	 * 
	 * @param bs
	 * @param ti
	 * @param binder
	 */
	public static void addTrashFolder(AllModulesInjected bs, TreeInfo ti, Binder binder) {
		// Find the TreeInfo in question and copy it so we can make a
		// trash TreeInfo out of it.
		TreeInfo binderTI = TreeInfo.findBinderTI(ti, String.valueOf(binder.getId()));
		TreeInfo trashTI = binderTI.copyBaseTI();
		BinderInfo trashBI = trashTI.getBinderInfo();
		
		// Change the copy to a trash TreeInfo.
		if (BinderType.FOLDER == trashBI.getBinderType())
			 trashBI.setFolderType(   FolderType.TRASH   );
		else trashBI.setWorkspaceType(WorkspaceType.TRASH);
		trashTI.setBinderExpanded(false);
		trashTI.setBinderIconName(null);
		trashTI.setBinderTitle(NLT.get("profile.abv.element.trash"));
		String trashUrl = GwtUIHelper.getTrashPermalink(trashTI.getBinderPermalink());
		trashTI.setBinderPermalink(     trashUrl);
		trashTI.setBinderTrashPermalink(trashUrl);

		// Finally, add the trash TreeInfo to the base TreeInfo's list
		// of children.
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if (null == childBindersList) {
			childBindersList = new ArrayList<TreeInfo>();
		}
		childBindersList.add(trashTI);
		ti.setChildBindersList(childBindersList);
	}

	/*
	 * Constructs a TagInfo from a Tag.
	 */
	private static TagInfo buildTIFromTag(TagType tagType, Tag tag) {
		TagInfo reply = new TagInfo();
		
		reply.setTagType(tagType);
		reply.setTagId(tag.getId());
		reply.setTagName(tag.getName());
		
		EntityIdentifier ei = tag.getEntityIdentifier();
		if (null != ei) {
			reply.setTagEntity(ei.toString());
		}
		ei = tag.getOwnerIdentifier();
		if (null != ei) {
			reply.setTagOwnerEntity(ei.toString());
		}
		
		return reply;
	}

	/*
	 * Builds a TreeInfo object for a BucketInfo.
	 */
	private static void buildChildBucketTI(HttpServletRequest request, AllModulesInjected bs, List<TreeInfo> childTIList, BucketInfo bi, int depth) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildBucketTI()");
		try {
			TreeInfo bucketTI = new TreeInfo();
			bucketTI.setBucketInfo(bi);
			childTIList.add(bucketTI);
		}
		finally {
			gsp.end();
		}
	}
	
	/*
	 * Builds the TreeInfo objects for a list of child Binder IDs.
	 */
	private static void buildChildTIs(HttpServletRequest request, AllModulesInjected bs, List<TreeInfo> childTIList, List<Long> childBinderList, List<Long> expandedBindersList, int depth) {
		SortedSet<Binder> binders = null;
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( READ )");
		try {
			try {
				binders = bs.getBinderModule().getBinders(childBinderList);
			} catch(AccessControlException ace) {
			} catch(NoBinderByTheIdException nbe) {}
		}
		finally {
			gsp.end();
		}
		
		gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( PROCESS )");
		try {
			if (null != binders) {
				for (Long subBinderId:  childBinderList) {
					long sbi = subBinderId.longValue();
					for (Binder subBinder:  binders) {
						if (subBinder.getId().longValue() == sbi) {
							try {
								TreeInfo subWsTI = buildTreeInfoFromBinder(request, bs, subBinder, expandedBindersList, false, depth);
								childTIList.add(subWsTI);
							} catch(AccessControlException ace) {
							} catch(NoBinderByTheIdException nbe) {}
							
							break;
						}
					}
				}				
				
				if (!(childTIList.isEmpty())) {
					Collections.sort(childTIList, new TreeInfoComparator(true));
				}
			}
		}
		finally {
			gsp.end();
		}
	}
	
	/**
	 * Builds a TreeInfo object for a given Binder.
	 *
	 * @param bs
	 * @param binder
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder, List<Long> expandedBindersList) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, binder, expandedBindersList, (null != expandedBindersList), 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, binder, null, false, 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder, List<Long> expandedBindersList, boolean mergeUsersExpansions, int depth) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderInfo(getBinderInfo(binder));
		reply.setBinderTitle(GwtUIHelper.getTreeBinderTitle(binder));
		reply.setBinderChildren(binder.getBinderCount());
		String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
		reply.setBinderPermalink(binderPermalink);
		reply.setBinderTrashPermalink(GwtUIHelper.getTrashPermalink(binderPermalink));
		reply.setBinderIconName(binder.getIconName());

		// When requested to do so...
		if (mergeUsersExpansions) {
			// ...merge any User Binder expansions with those we were
			// ...given.
			mergeBinderExpansions(bs, expandedBindersList);
		}

		// Should this Binder should be expanded?
		boolean expandBinder = ((0 == depth) || isLongInList(binder.getId(), expandedBindersList));
		reply.setBinderExpanded(expandBinder);
		if (expandBinder) {
			// Yes!  Get the sort list of the binder's children.
			List<Long> childBinderIds = new ArrayList<Long>();
			List<TreeInfo> childTIList = reply.getChildBindersList(); 
			List<BinderData> childBinderData = getChildBinderData(bs, binder);
			for (BinderData bd:  childBinderData) {			
				// Do we need to display the list in buckets?
				BucketInfo bi = bd.getBucketInfo(); 
				if (null != bi) {
					// Yes!  Build the TreeInfo objects for putting the
					// Binder's children into buckets.
					buildChildBucketTI(
						request,
						bs,
						childTIList,
						bi,
						(depth + 1));
				}
				else {
					childBinderIds.add(bd.getBinderId());
				}
			}

			// Do we have any non-bucketed binders?
			if (!(childBinderIds.isEmpty())) {
				// Yes!  Generate the TreeInfo's for them.
				buildChildTIs(
					request,
					bs,
					childTIList,
					childBinderIds,
					expandedBindersList,
					(depth + 1));
			}
			
			// Update the count of Binder children as it may have
			// changed based on moving into buckets, ...
			reply.setBinderChildren(childTIList.size());
		}

		// If we get here, reply refers to the TreeInfo object for this
		// Binder.  Return it.
		return reply;
	}

	/**
	 * Builds a TreeInfo object for a given Binder, using a List<Long>
	 * of Binder IDs for its children.
	 *
	 * @param bs
	 * @param bindersList
	 * 
	 * @return
	 */
	public static List<TreeInfo> buildTreeInfoFromBinderList(HttpServletRequest request, AllModulesInjected bs, List<Long> bindersList) {
		ArrayList<TreeInfo> reply = new ArrayList<TreeInfo>();
		for (Iterator<Long> lIT = bindersList.iterator(); lIT.hasNext(); ) {
			Binder binder = getBinderForWorkspaceTree(bs, lIT.next());
			if (null != binder) {
				reply.add(buildTreeInfoFromBinder(request, bs, binder, null, false, (-1)));
			}
		}
		return reply;
	}

	/**
	 * See if the user has rights to manage personal tags on the given binder.
	 */
	public static Boolean canManagePersonalBinderTags( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			
			binder = bs.getBinderModule().getBinder( Long.parseLong( binderId ) );
			return new Boolean( bs.getBinderModule().testAccess( binder, BinderOperation.modifyBinder ) );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage personal tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage personal tags on the given entry.
	 */
	public static Boolean canManagePersonalEntryTags( AllModulesInjected bs, String entryId )
	{
		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			// Check to see if the user can manage personal tags on this entry.
			folderModule.checkAccess( folderEntry, FolderOperation.readEntry );

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage personal tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given binder.
	 */
	public static Boolean canManagePublicBinderTags( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			
			binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			return new Boolean( bs.getBinderModule().testAccess( binder, BinderOperation.manageTag ) );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage public tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given entry.
	 */
	public static Boolean canManagePublicEntryTags( AllModulesInjected bs, String entryId )
	{
		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			// Check to see if the user can manage public tags on this entry.
			folderModule.checkAccess( folderEntry, FolderOperation.manageTag );

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage public tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to modify the given binder.
	 */
	public static Boolean canModifyBinder( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			boolean results;
			
			binder = bs.getBinderModule().getBinder( Long.parseLong( binderId ) );
			results = bs.getBinderModule().testAccess( binder, BinderOperation.modifyBinder );
			return new Boolean( results );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to modify the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * Create a group from the given information
	 * @throws WriteEntryDataException 
	 * @throws WriteFilesException 
	 * @throws AccessControlException 
	 */
	public static Group createGroup(
								AllModulesInjected ami,
								String name,
								String title,
								String desc,
								boolean isMembershipDynamic,
								List<GwtTeamingItem> membership,
								GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		MapInputData inputData;
		HashMap<String, Object> inputMap;
		HashMap<String, Object> fileMap;
		Group newGroup = null;
		String ldapQuery = null;
		HashSet<Long> membershipIds;
		SortedSet<Principal> principals;

		// Create an empty file map.
		fileMap = new HashMap<String, Object>();
		
		membershipIds = new HashSet<Long>();
		
		inputMap = new HashMap<String, Object>();
		inputMap.put( ObjectKeys.FIELD_PRINCIPAL_NAME, name );
		inputMap.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
		inputMap.put( ObjectKeys.FIELD_GROUP_DYNAMIC, isMembershipDynamic );
	
		// Is group membership dynamic?
		if ( isMembershipDynamic == false )
		{
			// No
			// Get a list of all the membership ids
			if ( membership != null )
			{
				for (GwtTeamingItem nextMember : membership)
				{
					Long id;
	
					id = null;
					if ( nextMember instanceof GwtUser )
						id = Long.valueOf( ((GwtUser) nextMember).getUserId() );
					else if ( nextMember instanceof GwtGroup )
						id = Long.valueOf( ((GwtGroup) nextMember).getId() );
								
					if ( id != null )
						membershipIds.add( id );
				}
			}
		}
		else
		{
			// Yes
			if ( membershipCriteria != null )
			{
				// Execute the ldap query and get the list of members from the results.
				try
				{
					int maxCount;
					HashSet<Long> potentialMemberIds;
					
					potentialMemberIds = ami.getLdapModule().getDynamicGroupMembers(
																		membershipCriteria.getBaseDn(),
																		membershipCriteria.getLdapFilterWithoutCRLF(),
																		membershipCriteria.getSearchSubtree() );

					// Get the maximum number of users that can be in a group.
					maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
					
					// Is the number of potential members greater than the max number of group members?
					if ( potentialMemberIds.size() > maxCount )
					{
						int count;

						// Yes, only take the max number of users.
						count = 0;
						for (Long userId : potentialMemberIds)
						{
							membershipIds.add( userId );
							++count;
							
							if ( count >= maxCount )
							{
								break;
							}
						}
					}
					else
					{
						// No
						membershipIds = potentialMemberIds;
					}
				}
				catch (Exception ex)
				{
					//!!! What to do.
					membershipIds = new HashSet<Long>();
				}

				ldapQuery = membershipCriteria.getAsXml();
			}
		}
		
		principals = ami.getProfileModule().getPrincipals( membershipIds );
		inputMap.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );

		inputMap.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );

		inputData = new MapInputData( inputMap );

		try
		{
			// Create the new group
			newGroup = ami.getProfileModule().addGroup( null, inputData, fileMap, null );
			
			// Reindex all the members of this group.
			if ( principals != null )
			{
				Map<Long, Principal> principalsToIndex;
				
				// Create a list of the members of the group.
				principalsToIndex = new HashMap<Long,Principal>();
				for (Principal principal : principals)
				{
					principalsToIndex.put( principal.getId(), principal );
				}
				
				Utils.reIndexPrincipals( ami.getProfileModule(), principalsToIndex );
			}
		}
		catch( Exception ex)
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		return newGroup;
	}
	
	/**
	 * Delete the given tag from the given entry.
	 */
	public static void deleteEntryTag( FolderModule fm, Long entryId, TagInfo tagInfo )
	{
		String tagId;
		
		// Does this tag already exist?
		tagId = tagInfo.getTagId();
		if ( tagId != null && tagId.length() > 0 )
		{
			// Yes, delete it from the given entry.
			fm.deleteTag( null, entryId, tagId );
		}
	}
	
	/*
	 * Remove the given tag from the given binder.
	 * are deleted from the binder.
	 */
	private static void deleteBinderTag( BinderModule bm, Binder binder, TagInfo tag )
	{
		String tagId;
		
		tagId = tag.getTagId();
		if ( tagId != null && tagId.length() > 0 )
			bm.deleteTag( binder.getId(), tagId );
	}
	
	/**
	 * 
	 */
	public static Boolean deleteGroups( AllModulesInjected ami, ArrayList<GroupInfo> listOfGroups ) throws GwtTeamingException
	{
		if ( listOfGroups != null && listOfGroups.size() > 0 )
		{
	   		List<Long> grpIds;
	   		
	   		grpIds = new ArrayList<Long>();
	   		for (GroupInfo nextGroup : listOfGroups)
	   		{
	   			grpIds.add( nextGroup.getId() );
	   		}

       		// Remove each group's quota (by setting it to 0) before deleting it
			// This will fix up all of the user quotas that may have been influenced by this group
	   		ami.getProfileModule().setGroupDiskQuotas( grpIds, new Long( 0L ) );
	   		ami.getProfileModule().setGroupFileSizeLimits( grpIds, null );
	       		
			// Delete each groups
	   		for (Long nextGroupId : grpIds)
	   		{
	   			try
	   			{
	   				ami.getProfileModule().deleteEntry( nextGroupId, null );
	   			}
	   			catch ( Exception ex )
	   			{
	   				throw GwtServerHelper.getGwtTeamingException( ex );
	   			} 
	   		}
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a List<Long> of Binder IDs
	 * (i.e., a bucket list.)
	 * 
	 * @param request
	 * @param bs
	 * @param bucketInfo
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo expandBucket(HttpServletRequest request, AllModulesInjected bs, BucketInfo bi, List<Long> expandedBindersList) {
		TreeInfo reply = new TreeInfo();
		List<TreeInfo> childTIList = reply.getChildBindersList(); 
		List<BinderData> childBinderData = getChildBinderData(bs, bi);
		List<Long> childBinderIds = new ArrayList<Long>();
		for (BinderData bd:  childBinderData) {			
			// Do we need to display the list in buckets?
			BucketInfo childBI = bd.getBucketInfo(); 
			if (null != childBI) {
				// Yes!  Build the TreeInfo objects for putting the
				// Binder's children into buckets.
				buildChildBucketTI(
					request,
					bs,
					childTIList,
					childBI,
					(-1));
			}
			else {
				childBinderIds.add(bd.getBinderId());
			}
		}

		// Do we have any non-bucketed binders?
		if (!(childBinderIds.isEmpty())) {
			// Yes!  Generate the TreeInfo's for them.
			buildChildTIs(
				request,
				bs,
				childTIList,
				childBinderIds,
				expandedBindersList,
				(-1));
		}
		
		// Update the count of Binder children as it may have
		// changed based on moving into buckets, ...
		reply.setBinderChildren(childTIList.size());
		
		// If we get here, reply refers to the TreeInfo for the
		// expanded bucket list.  Return it.
		return reply;
	}

	/*
	 * Constructs a FavoriteInfo object from a JSONObject.
	 */
	private static FavoriteInfo fiFromJSON(AllModulesInjected bs, JSONObject jso) {
		// Construct a FavoriteInfo based on the JSONObject.
		FavoriteInfo reply = new FavoriteInfo();
		
		reply.setAction(  getSFromJSO(jso, "action"  ));
		reply.setCategory(getSFromJSO(jso, "category"));
		reply.setEletype( getSFromJSO(jso, "eletype" ));
		reply.setHover(   getSFromJSO(jso, "hover"   ));
		reply.setId(      getSFromJSO(jso, "id"      ));
		reply.setName(    getSFromJSO(jso, "name"    ));
		reply.setType(    getSFromJSO(jso, "type"    ));
		reply.setValue(   getSFromJSO(jso, "value"   ));

		// Is this favorite a binder?  (I'm not sure if it's every
		// anything else.)
		if (reply.getType().equalsIgnoreCase("binder")) {
			// Yes!  Does it have a binder ID?
			String idS = reply.getValue();
			if (MiscUtil.hasString(idS)) {
				// Yes!  Can access the binder?
				Long id = Long.parseLong(idS);
				Binder binder;
				try {
					binder = bs.getBinderModule().getBinder(id);
				}
				catch (Exception e) {
					binder = null;
				}
				if (binder == null) {
					// No!  Ignore the favorite.
					reply = null;
				}
				else {
					// Yes, we can access the binder!  Is it in the
					// trash?
					if (GwtUIHelper.isBinderPreDeleted(binder)) {
						// Yes!  Ignore the favorite.
						reply = null;
					}
					else {
						// No, it's not in the trash!  Valid the string
						// we use for the mouse hover.  If this binder
						// got moved, what's there may be wrong.
						reply.setHover(binder.getPathName());
					}
				}
			}
		}

		// If we get here, reply refers to the FavoriteInfo for the
		// JSONObject or is null.  Return it.
		return reply;
	}
	
	/**
	 * Return a list of administration actions the user has rights to perform. 
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<GwtAdminCategory> getAdminActions( HttpServletRequest request, Binder binder, AbstractAllModulesInjected allModules )
	{
		ArrayList<GwtAdminCategory> adminCategories;
		GwtAdminCategory managementCategory;
		GwtAdminCategory systemCategory;
		GwtAdminCategory reportsCategory;
		GwtAdminAction adminAction;
		String title;
		String url;
		AdaptedPortletURL adaptedUrl;
		User user;
 		Workspace top = null;
		ProfileBinder profilesBinder;
		DefinitionModule definitionModule;
		LdapModule ldapModule;
		AdminModule adminModule;
		ProfileModule profileModule;
		WorkspaceModule workspaceModule;
		BinderModule binderModule;
		LicenseModule licenseModule;
		ZoneModule zoneModule;
		
		definitionModule = allModules.getDefinitionModule();
		ldapModule = allModules.getLdapModule();
		adminModule = allModules.getAdminModule();
		profileModule = allModules.getProfileModule();
		workspaceModule = allModules.getWorkspaceModule();
		binderModule = allModules.getBinderModule();
		licenseModule = allModules.getLicenseModule();
		zoneModule = allModules.getZoneModule();
		
		user = GwtServerHelper.getCurrentUser();
		
 		try
 		{
			top = workspaceModule.getTopWorkspace();
 		}
 		catch( Exception e )
 		{}
 		
 		profilesBinder = profileModule.getProfileBinder();
		
		// Create an ArrayList that will hold the GwtAdminCategory objects.
		adminCategories = new ArrayList<GwtAdminCategory>();
		
		// Create a "Management" category
		{
	 		managementCategory = new GwtAdminCategory();
	 		managementCategory.setLocalizedName( NLT.get( "administration.category.management" ) );
	 		managementCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.MANAGEMENT );
			adminCategories.add( managementCategory );
			
			// Does the user have rights to "Manage User Accounts"?
			//   This function covers: Add Account, Disable/Delete Accounts, Import Profiles.
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					List defaultEntryDefinitions;

					defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
					
					if ( !defaultEntryDefinitions.isEmpty() )
					{
						Definition def = (Definition) defaultEntryDefinitions.get( 0 );
						title = NLT.get( "administration.manage.userAccounts" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", true );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
						adaptedUrl.setParameter( WebKeys.URL_BINDER_ID, profilesBinder.getId().toString() );
						adaptedUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
						adaptedUrl.setParameter(WebKeys.URL_CONTEXT, "adminMenu");
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.ADD_USER );
						
						// Add this action to the "Management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
			}
			catch( AccessControlException e )
			{}

			// Does the user have rights to "Manage the search index"?
			if ( top != null )
			{
				if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) && 
					  binderModule.testAccess( top, BinderOperation.indexBinder ) )
				{
					// Yes
					if ( adminModule.retrieveIndexNodes() != null )
					{
						GwtAdminCategory manageSearchIndexCategory;
						
						// Create a "manage search index category.
				 		manageSearchIndexCategory = new GwtAdminCategory();
				 		manageSearchIndexCategory.setLocalizedName( NLT.get( "administration.configure_search_index" ) );
				 		manageSearchIndexCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.MANAGE_SEARCH_INDEX );
						adminCategories.add( manageSearchIndexCategory );
						
						// Add a "Folder Index" action
						{
							title = NLT.get( "administration.search.title.index" );
	
							adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
							adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE );
							url = adaptedUrl.toString();
							
							adminAction = new GwtAdminAction();
							adminAction.init( title, url, AdminAction.CONFIGURE_FOLDER_INDEX );
							
							// Add this action to the "manage search index" category
							manageSearchIndexCategory.addAdminOption( adminAction );
						}

						// Add a "Folder Search Nodes" action
						{
							title = NLT.get( "administration.search.title.nodes" );
	
							adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
							adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_SEARCH_NODES_CONFIGURE );
							url = adaptedUrl.toString();
							
							adminAction = new GwtAdminAction();
							adminAction.init( title, url, AdminAction.CONFIGURE_FOLDER_SEARCH_NODES );
							
							// Add this action to the "manage search index" category
							manageSearchIndexCategory.addAdminOption( adminAction );
						}
					}
					else
					{
						// Add a "Manage the search index" action.
						title = NLT.get( "administration.configure_search_index" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.CONFIGURE_SEARCH_INDEX );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
			}

			// Does the user have rights to "Manage groups"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					// Yes
					title = NLT.get( "administration.manage.groups" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_GROUPS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_GROUPS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch( AccessControlException e ) {}

			// Does the user have rights to "Manage quotas"?
			try
			{
				if ( adminModule.testAccess( AdminOperation.manageFunction ) )
				{
					// Yes
					title = NLT.get( "administration.manage.quotas" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_QUOTAS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_QUOTAS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to "Manage workspace and folder templates"?
			if ( adminModule.testAccess( AdminOperation.manageTemplate ) )
			{
				// Yes
				title = NLT.get( "administration.configure_configurations" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.MANAGE_WORKSPACE_AND_FOLDER_TEMPLATES );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Manage license"?
			if( ReleaseInfo.isLicenseRequiredEdition() )
			{
				if ( licenseModule.testAccess( LicenseOperation.manageLicense ) )
				{
					// Yes
					title = NLT.get( "administration.manage.license" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_LICENSE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_LICENSE );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}

			// Does the user have rights to "Manage zones"?
			if ( LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.module.zone.MultiZone" ) &&
					zoneModule.testAccess() )
			{
				// Yes
				title = NLT.get( "administration.manage.zones" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_ZONES );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.MANAGE_ZONES );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Manage applications"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					// Yes
					title = NLT.get( "administration.manage.applications" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATIONS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_APPLICATIONS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to "Manage application groups"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					// Yes
					title = NLT.get( "administration.manage.application.groups" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATION_GROUPS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_APPLICATION_GROUPS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to "Manage Extensions"?
			if ( adminModule.testAccess( AdminOperation.manageExtensions ) )
			{
				// Yes
				title = NLT.get( "administration.manage.extensions" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_EXTENSIONS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.MANAGE_EXTENSIONS );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}
		}
		
		// Create a "Reports" category
		reportsCategory = new GwtAdminCategory();
		reportsCategory.setLocalizedName( NLT.get( "administration.category.reports" ) );
		reportsCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.REPORTS );
		adminCategories.add( reportsCategory );

		// Does the user have rights to run reports?
		if ( adminModule.testAccess( AdminOperation.report ) )
		{
			// Yes
			// Create an "email report"
			{
				title = NLT.get( "administration.report.title.email" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_EMAIL_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_EMAIL );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Create a "login report"
			{
				title = NLT.get( "administration.report.title.login" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LOGIN_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_LOGIN );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "License Report"
			//License report
			if( ReleaseInfo.isLicenseRequiredEdition() )
			{
				title = NLT.get( "administration.report.title.license" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LICENSE_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_LICENSE );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Activity by User"
			{
				title = NLT.get( "administration.report.title.activityByUser" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT_BY_USER );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_ACTIVITY_BY_USER );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Disk usage report"
			{
				title = NLT.get( "administration.report.title.quota" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DISK_USAGE );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Data quota exceeded report"
			{
				title = NLT.get( "administration.report.title.disk_quota_exceeded" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_EXCEEDED_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DATA_QUOTA_EXCEEDED );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Data quota highwater exceeded report"
			{
				title = NLT.get( "administration.report.title.highwater_exceeded" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_HIGHWATER_EXCEEDED_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "User access report"
			{
				title = NLT.get( "administration.report.title.user_access" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_USER_ACCESS_REPORT  );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_USER_ACCESS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "XSS report"
			{
				title = NLT.get( "administration.report.title.xss", "XSS Report" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_XSS_REPORT  );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_XSS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
		}

		// Add reports that everyone can run.
		{
			// Add a "Credits report" action.
			{
				title = NLT.get( "administration.credits" );
	
				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_CREDITS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_VIEW_CREDITS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
		}

		// Does the user have rights to run "Content Modification Log Report"?
		if ( adminModule.testAccess( AdminOperation.manageFunction ) )
		{
			// Yes
			title = NLT.get( "administration.view_change_log" );
			
			adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
			adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG );
			url = adaptedUrl.toString();
			
			adminAction = new GwtAdminAction();
			adminAction.init( title, url, AdminAction.REPORT_VIEW_CHANGELOG );
			
			// Add this action to the "reports" category
			reportsCategory.addAdminOption( adminAction );
		}

		// Does the user have rights to run the "System error logs" report?
		if ( adminModule.testAccess( AdminOperation.manageErrorLogs ) )
		{
			// Yes
			title = NLT.get( "administration.system_error_logs" );
			
			adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
			adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_GET_LOG_FILES );
			url = adaptedUrl.toString();
			
			adminAction = new GwtAdminAction();
			adminAction.init( title, url, AdminAction.REPORT_VIEW_SYSTEM_ERROR_LOG );
			
			// Add this action to the "reports" category
			reportsCategory.addAdminOption( adminAction );
		}
		
		// Create a "System" category
		{
			systemCategory = new GwtAdminCategory();
			systemCategory.setLocalizedName( NLT.get( "administration.category.system" ) );
			systemCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.SYSTEM );
			adminCategories.add( systemCategory );

			// Does the user have rights to "Form/View Designers"?
			if ( ( null != top ) &&
			     definitionModule.testAccess( top, Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition ) ||
				 definitionModule.testAccess( top, Definition.WORKFLOW,     DefinitionOperation.manageDefinition ) )
			{
				// Yes
				title = NLT.get( "administration.definition_builder_designers" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.FORM_VIEW_DESIGNER );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			// Does the user have the rights to "ldap configuration"?
			if ( ldapModule.testAccess(LdapOperation.manageLdap ) )
			{
				// Yes
				if ( ldapModule.getLdapSchedule() != null )
				{
					title = NLT.get( "administration.configure_ldap" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.LDAP_CONFIG );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}
			
			// Does the user have rights to "configure guest access"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				if ( ReleaseInfo.isLicenseRequiredEdition() )
					title = NLT.get( "administration.configure_userAccessOnly", NLT.get( "administration.configure_userAccess" ) );
				else
					title = NLT.get( "administration.configure_userAccess" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_USER_ACCESS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_GUEST_ACCESS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "configure mobile access"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				title = NLT.get( "administration.configure_mobileAccess" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_MOBILE_ACCESS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_MOBILE_ACCESS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "configure home page"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				title = NLT.get( "administration.configure_homePage" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_HOME_PAGE );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_HOME_PAGE );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Configure Role Definitions"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				title = NLT.get( "administration.configure_roles" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_ROLE_DEFINITIONS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			// Does the user have rights to "Configure Weekends and Holidays"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				title = NLT.get( "administration.configure.schedule.action" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_SCHEDULE );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_SCHEDULE );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			// Does the user have rights to "Configure E-Mail"?
			if ( adminModule.testAccess( AdminOperation.manageMail ) )
			{
				// Yes
				title = NLT.get( "administration.configure_mail" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_POSTINGJOB_CONFIGURE );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_EMAIL );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Configure File Version Aging"?
			if ( adminModule.testAccess( AdminOperation.manageFileVersionAging ) )
			{
				// Yes
				title = NLT.get( "administration.configure_file_version_aging" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FILE_VERSION_AGING_JOB_CONFIGURE);
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_FILE_VERSION_AGING );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Access Control for Zone Administration functions"?
			if ( adminModule.testAccess( AdminOperation.manageFunctionMembership ) )
			{
				ZoneConfig zoneConfig;
				
				// Yes
				zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
				title = NLT.get( "administration.manage.accessControl" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL );
				adaptedUrl.setParameter( WebKeys.URL_WORKAREA_ID, zoneConfig.getWorkAreaId().toString() );
				adaptedUrl.setParameter( WebKeys.URL_WORKAREA_TYPE, zoneConfig.getWorkAreaType() );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.ACCESS_CONTROL_FOR_ZONE_ADMIN_FUNCTIONS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Site Branding"
			if ( top != null && binderModule.testAccess( top, BinderOperation.modifyBinder ) )
			{
				// Yes
				title = NLT.get( "administration.modifySiteBranding" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.SITE_BRANDING );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			// Are we running the Enterprise version of Teaming?
			if ( ReleaseInfo.isLicenseRequiredEdition() == true )
			{
				// Yes
				// Does the user have rights to "Configure File Sync App"?
				if ( adminModule.testAccess( AdminOperation.manageFileSynchApp ) )
				{
					// Yes
					title = NLT.get( "administration.configureVibeDesktop" );
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, "", AdminAction.CONFIGURE_FILE_SYNC_APP );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}
		}
		
		// Sort the administration actions in each category
		GwtAdminActionComparator comparator;
		comparator = new GwtAdminActionComparator();
		for ( GwtAdminCategory category : adminCategories )
		{
			ArrayList<GwtAdminAction> adminActions;
			
			// Do we have more than 1 administration action in this category?
			adminActions = category.getActions();
			if ( adminActions != null && adminActions.size() > 1 )
			{
				// Yes, sort them.
				Collections.sort( adminActions, comparator );
			}
		}
		
		return adminCategories;
	}// end getAdminActions()
	
	
	/**
	 * Return a list of all the groups in Vibe
	 */
	public static List<GroupInfo> getAllGroups( AllModulesInjected ami ) throws GwtTeamingException
	{
		ArrayList<GroupInfo> reply;
		
		reply = new ArrayList<GroupInfo>();
		
		try
		{
			Map options;
			Map searchResults;
			List groups;
			
			options = new HashMap();
			options.put( ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD );
			options.put( ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE );
			options.put( ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1 );
			
			// Exclude allUsers from the search
			{
				Document searchFilter;
				Element rootElement;
				Element field;
		    	Element child;
	
				searchFilter = DocumentHelper.createDocument();
				rootElement = searchFilter.addElement( Constants.NOT_ELEMENT );
				field = rootElement.addElement( Constants.FIELD_ELEMENT );
		    	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.GROUPNAME_FIELD );
		    	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
		    	child.setText( "allUsers" );
		    	options.put( ObjectKeys.SEARCH_FILTER_AND, searchFilter );
			}
	
			// Get the list of all the groups.
			searchResults = ami.getProfileModule().getGroups( options );
	
			groups = (List) searchResults.get( ObjectKeys.SEARCH_ENTRIES );
			
			if ( groups != null )
			{
				int i;
				
				for (i = 0; i < groups.size(); ++i)
				{
					HashMap nextMap;
					GroupInfo grpInfo;
					Long id;
					
					if ( groups.get( i ) instanceof HashMap )
					{
						Object value;
						
						nextMap = (HashMap) groups.get( i );
					
						grpInfo = new GroupInfo();
						id = Long.valueOf( (String) nextMap.get( "_docId" ) );
						grpInfo.setId( id );
						grpInfo.setTitle( (String) nextMap.get( "title" ) );
						grpInfo.setName( (String) nextMap.get( "_groupName" ) );
						
						value = nextMap.get( "_desc" );
						if ( value != null && value instanceof String )
							grpInfo.setDesc( (String) value );
						
						reply.add( grpInfo );
					}
				}
			}
		}
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		} 
		
		return reply;
	}
	
	
	/**
	 * Return the branding data for the given binder.
	 */
	public static GwtBrandingData getBinderBrandingData( AbstractAllModulesInjected allModules, String binderId, HttpServletRequest request ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Binder binder;
		Long binderIdL;
		GwtBrandingData brandingData;
		
		brandingData = new GwtBrandingData();
		
		try
		{
			binderModule = allModules.getBinderModule();
	
			binderIdL = new Long( binderId );
			
			// Get the binder object.
			if ( binderIdL != null )
			{
				String branding;
				GwtBrandingDataExt brandingExt;
				Binder brandingSourceBinder;
				String brandingSourceBinderId;
				
				binder = binderModule.getBinder( binderIdL );
				
				// Get the binder where branding comes from.
				brandingSourceBinder = binder.getBrandingSource();
				
				// Does the user have rights to the binder where the branding is coming from?
				if ( !binderModule.testAccess( brandingSourceBinder, BinderOperation.readEntries ) )
				{
					// No, don't use inherited branding.
					brandingSourceBinderId = binderId;
					brandingSourceBinder = binder;
				}
				else
				{
					brandingSourceBinderId = brandingSourceBinder.getId().toString();
				}
				
				brandingData.setBinderId( brandingSourceBinderId );

				// Get the branding that should be applied for this binder.
				branding = brandingSourceBinder.getBranding();
				
				// For some unknown reason, if there is no branding in the db the string we get back
				// will contain only a \n.  We don't want that.
				if ( branding != null && branding.length() == 1 && branding.charAt( 0 ) == '\n' )
					branding = "";
				
				// Parse the branding and replace any markup with the appropriate url.  For example,
				// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
				branding = MarkupUtil.markupStringReplacement( null, null, request, null, brandingSourceBinder, branding, "view" );
				
				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );
	
				brandingData.setBranding( branding );
				
				// Get the additional branding information.
				{
					String xmlStr;
					
					brandingExt = new GwtBrandingDataExt();
					
					// Get the xml that represents the branding data.  The following is an example of what the xml should look like.
					// 	<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced">
					// 		<background color="" imgName="" />
					// 	</brandingData>
					xmlStr = brandingSourceBinder.getBrandingExt();
					
					// Is there old-style branding?
					if ( branding != null && branding.length() > 0 )
					{
						// Yes
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
						
						// Is there additional branding data?
						if ( xmlStr == null || xmlStr.length() == 0 )
						{
							// Yes, We are dealing with branding that was created before the Durango release.
							// In order to have existing branding still look good even though we aren't
							// using the old background image, we will set the background color to white
							// and the font color to black.
							brandingExt.setBackgroundColor( "white" );
							brandingExt.setFontColor( "black" );
						}
					}
					else
					{
						// No
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
					}

					if ( xmlStr != null )
					{
						try
			    		{
			    			Document doc;
			    			Node node;
			    			Node attrNode;
		        			String imgName;
							String fileUrl;
							String webPath;
							
							webPath = WebUrlUtil.getServletRootURL( request );

							// Parse the xml string into an xml document.
							doc = DocumentHelper.parseText( xmlStr );
			    			
			    			// Get the root element.
			    			node = doc.getRootElement();
			    			
			    			// Get the font color.
			    			attrNode = node.selectSingleNode( "@fontColor" );
			    			if ( attrNode != null )
			    			{
			        			String fontColor;
			
			        			fontColor = attrNode.getText();
			        			brandingExt.setFontColor( fontColor );
			    			}
			    			
			    			// Get the name of the branding image
			    			attrNode = node.selectSingleNode( "@brandingImgName" );
			    			if ( attrNode != null )
			    			{
			        			imgName = attrNode.getText();

			    				if ( imgName != null && imgName.length() > 0 )
			    				{
			    					brandingExt.setBrandingImgName( imgName );

			    					// Is the image name "__no image__" or "__default teaming image__"?
			    					// These are special names that don't represent a real image file name.
			    					if ( !imgName.equalsIgnoreCase( "__no image__" ) && !imgName.equalsIgnoreCase( "__default teaming image__" ) )
			    					{
			    						// No, Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBrandingImgUrl( fileUrl );
			    					}
			    				}
			    			}
			    			
			    			// Get the type of branding, "advanced" or "image"
			    			attrNode = node.selectSingleNode( "@brandingType" );
			    			if ( attrNode != null )
			    			{
			    				String type;
			    				
			    				type = attrNode.getText();
			    				if ( type != null && type.equalsIgnoreCase( GwtBrandingDataExt.BRANDING_TYPE_IMAGE ) )
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
			    				else
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
			    			}
			    			
			    			// Get the branding rule.
			    			attrNode = node.selectSingleNode( "@brandingRule" );
			    			if ( attrNode != null )
			    			{
			    				String ruleName;
			    				
			    				ruleName = attrNode.getText();
			    				if ( ruleName != null )
			    				{
			    					if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY );
			    				}
			    			}
			    			
			    			// Get the <background color="" imgName="" stretchImg="" /> node
			    			node = node.selectSingleNode( "background" );
			    			if ( node != null )
			    			{
			    				// Get the background color.
			    				attrNode = node.selectSingleNode( "@color" );
			    				if ( attrNode != null )
			    				{
			        				String bgColor;
			
			        				bgColor = attrNode.getText();
			        				brandingExt.setBackgroundColor( bgColor );
			    				}
			    				
			    				// Get the name of the background image.
			    				attrNode = node.selectSingleNode( "@imgName" );
			    				if ( attrNode != null )
			    				{
			        				imgName = attrNode.getText();

				    				if ( imgName != null && imgName.length() > 0 )
				    				{
				    					// Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBackgroundImgUrl( fileUrl );
				    					
				    					brandingExt.setBackgroundImgName( imgName );
				    				}
			    				}

			    				// Get the value of whether or not to stretch the background image.
	        					brandingExt.setBackgroundImgStretchValue( true );
			    				attrNode = node.selectSingleNode( "@stretchImg" );
			    				if ( attrNode != null )
			    				{
			        				String stretch;
			
			        				stretch = attrNode.getText();
			        				if ( stretch != null && stretch.equalsIgnoreCase( "false" ) )
			        					brandingExt.setBackgroundImgStretchValue( false );
			    				}
			    			}
			    		}
			    		catch(Exception e)
			    		{
			    			m_logger.warn( "Unable to parse branding ext " + xmlStr );
			    		}
					}
					
					brandingData.setBrandingExt( brandingExt );
				}
				
				// Are we dealing with site branding?
				{
					Binder topWorkspace;
					
					// Get the top workspace.
					topWorkspace = allModules.getWorkspaceModule().getTopWorkspace();				
					
					// Are we dealing with the site branding.
					if ( binderIdL.compareTo( topWorkspace.getId() ) == 0 )
					{
						// Yes
						brandingData.setIsSiteBranding( true );
					}
				}
			}
		}
		catch (NoBinderByTheIdException nbEx)
		{
			// Nothing to do
		}
		catch (AccessControlException acEx)
		{
			// Nothing to do
		}
		catch (Exception e)
		{
			// Nothing to do
		}
		
		return brandingData;
	}// end getBinderBrandingData()

	/**
	 * Returns the User object that's the creator of a Binder.
	 * 
	 * Note that the creator is not always the owner (i.e.,
	 * binder.getOwner() may return something different.)  This will
	 * happen, for instance, with the guest user workspace.  For that
	 * binder, binder.getOwner() returns the admin user where this
	 * method will return the guest user.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	public static User getBinderCreator(AllModulesInjected bs, Binder binder) {
		User         reply         = null;
		HistoryStamp creationStamp = ((null == binder)        ? null : binder.getCreation());
		Principal    owner         = ((null == creationStamp) ? null : creationStamp.getPrincipal());
		if (null != owner) {
			owner = Utils.fixProxy(owner);
			if (owner instanceof User) {
				reply = ((User) owner);
			}
		}
		return reply;
	}
	
	public static User getBinderCreator(AllModulesInjected bs, String binderId) {
		return getBinderCreator(bs, Long.parseLong(binderId));
	}
	
	public static User getBinderCreator(AllModulesInjected bs, Long binderId) {
		// Always use the initial form of the method.
		return getBinderCreator(bs, bs.getBinderModule().getBinder(binderId));
	}
	
	/**
	 * Returns the entity type of a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static String getBinderEntityType(AllModulesInjected bs, String binderId) {
		return getBinderEntityType(bs.getBinderModule().getBinder(Long.parseLong(binderId)));
	}
	
	public static String getBinderEntityType(Binder binder) {
		return binder.getEntityType().toString();
	}
	
	/**
	 * Returns an accessible Binder for a given ID.  If the Binder
	 * cannot be accessed for any reason, null is returned.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, String binderId) {
		return getBinderForWorkspaceTree(bs, binderId, false);
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, String binderId, boolean defaultToTop) {
		Binder reply;
		try {
			Long binderIdL = Long.parseLong(binderId);
			reply = getBinderForWorkspaceTree(bs, binderIdL, defaultToTop);
		}
		catch (NumberFormatException nfe) {
			m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (NumberFormatException) ):  '" + ((null == binderId) ? "<nul>" : binderId) + "'");
			reply = null;
		}
		
		// If we get here, reply refers to the Binder if it could be
		// accessed and null otherwise.  Return it.
		return reply;
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, Long binderId) {
		return getBinderForWorkspaceTree(bs, binderId, false);
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, Long binderId, boolean defaultToTop) {
		Binder reply;
		try {
			reply = bs.getBinderModule().getBinder(binderId);
		}
		catch (Exception e) {
			m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (AccessControlException) ):  '" + String.valueOf(binderId) + "'");
			reply = null;
		}

		// Do we have a Binder that's in the trash...
		if ((null != reply) && GwtUIHelper.isBinderPreDeleted(reply)) {
			// ...we want to ignore it.
			reply = null;
		}
		
		// If we couldn't access the binder and we're supposed to
		// default to the top workspace...
		if ((null == reply) && defaultToTop) {
			// ...default to it.
			try {
				reply = bs.getWorkspaceModule().getTopWorkspace();
			}
			catch (Exception e) {
				m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Default to Top Workspace ) ");
				reply = null;
			}
		}
		
		// If we get here, reply refers to the Binder if it could be
		// accessed and null otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns a BinderInfo describing a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderInfo getBinderInfo(AllModulesInjected bs, String binderId) {
		BinderInfo reply;
		Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
		if (null == binder) {
			reply = new BinderInfo();
			reply.setBinderId(binderId);
		}
		else {
			reply = getBinderInfo(binder);
		}
		return reply;
	}
	
	public static BinderInfo getBinderInfo(Binder binder) {
		BinderInfo reply = new BinderInfo();
		                                    reply.setBinderId(     binder.getId()             );
		                                    reply.setBinderTitle(  binder.getTitle()          );
		                                    reply.setEntityType(   getBinderEntityType(binder));
		                                    reply.setBinderType(   getBinderType(      binder));
		if      (reply.isBinderFolder())    reply.setFolderType(   getFolderType(      binder));
		else if (reply.isBinderWorkspace()) reply.setWorkspaceType(getWorkspaceType(   binder));
		return reply;
	}
	
	/**
	 * Returns a List<TagInfo> describing the tags defined on a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static ArrayList<TagInfo> getBinderTags(AllModulesInjected bs, String binderId) {
		BinderModule bm = bs.getBinderModule();
		return getBinderTags(bm, bm.getBinder(Long.parseLong(binderId)));
	}
	
	public static ArrayList<TagInfo> getBinderTags(BinderModule bm, Binder binder) {
		// Allocate an ArrayList to return the TagInfo's in...
		ArrayList<TagInfo> reply = new ArrayList<TagInfo>();

		// ...read the Tag's from the Binder...
		Map<String, SortedSet<Tag>> tagsMap = TagUtil.uniqueTags(bm.getTags(binder));
		Set<Tag> communityTagsSet = ((null == tagsMap) ? null : tagsMap.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
		Set<Tag> personalTagsSet  = ((null == tagsMap) ? null : tagsMap.get(ObjectKeys.PERSONAL_ENTITY_TAGS));

		// ...iterate through the community tags...
		Iterator<Tag> tagsIT;
		if (null != communityTagsSet) {
			for (tagsIT = communityTagsSet.iterator(); tagsIT.hasNext(); ) {
				// ...adding each to the reply list...
				reply.add(buildTIFromTag(TagType.COMMUNITY, tagsIT.next()));
			}
		}
		
		// ...iterate through the personal tags...
		if (null != personalTagsSet) {
			for (tagsIT = personalTagsSet.iterator(); tagsIT.hasNext(); ) {
				// ...adding each to the reply list...
				reply.add(buildTIFromTag(TagType.PERSONAL, tagsIT.next()));
			}
		}

		// ...and finally, return the List<TagInfo> of the tags defined
		// ...on the Binder.
		return reply;
	}

	/**
	 * Returns a BinderType describing a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderType getBinderType(AllModulesInjected bs, String binderId) {
		return getBinderType(bs.getBinderModule().getBinder(Long.parseLong(binderId)));
	}
	
	public static BinderType getBinderType(Binder binder) {
		BinderType reply;
		if      (binder instanceof Workspace) reply = BinderType.WORKSPACE;
		else if (binder instanceof Folder)    reply = BinderType.FOLDER;
		else                                  reply = BinderType.OTHER;

		if (BinderType.OTHER == reply) {
			m_logger.debug("GwtServerHelper.getBinderType( 'Could not determine binder type' ):  " + binder.getPathName());
		}
		return reply;
	}

	/**
	 * Returns the User object of the currently logged in user.
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		return RequestContextHolder.getRequestContext().getUser();
	}

	/**
	 * Returns the current HttpSession, if accessible.
	 * 
	 * @return
	 */
	public static HttpSession getCurrentHttpSession() {
		HttpSession reply = null;
		RequestContext rc = RequestContextHolder.getRequestContext();
		SessionContext sc = rc.getSessionContext();
		if (sc instanceof HttpSessionContext) {
			reply = ((HttpSessionContext) sc).getHttpSession();
		}
		return reply;
	}

	/**
	 * Return a GwtFileSyncAppConfiguration object that holds the File Sync App configuration data
	 * 
	 * @return
	 */
	public static GwtFileSyncAppConfiguration getFileSyncAppConfiguration( AllModulesInjected allModules )
	{
		GwtFileSyncAppConfiguration fileSyncAppConfiguration;
		ZoneConfig zoneConfig;
		ZoneModule zoneModule;
		
		zoneModule = allModules.getZoneModule();
		zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		
		fileSyncAppConfiguration = new GwtFileSyncAppConfiguration();
		
		// Get the whether the File Sync App is enabled.
		fileSyncAppConfiguration.setIsFileSyncAppEnabled( zoneConfig.getFsaEnabled() );
		
		// Get the File Sync App sync interval.
		fileSyncAppConfiguration.setSyncInterval( zoneConfig.getFsaSynchInterval() );
		
		// Get the auto-update url.
		fileSyncAppConfiguration.setAutoUpdateUrl( zoneConfig.getFsaAutoUpdateUrl() );
		
		return fileSyncAppConfiguration;
	}
	
	
	/**
	 * Return a list of tags associated with the given entry.
	 */
	public static ArrayList<TagInfo> getEntryTags( AllModulesInjected bs, String entryId )
	{
		FolderEntry entry;
		Long entryIdL;
		Map<String, SortedSet<Tag>> tagsMap;
		ArrayList<TagInfo> tags;
		
		tags = new ArrayList<TagInfo>();
		
		entryIdL = new Long( entryId );
		entry = bs.getFolderModule().getEntry( null, entryIdL );
		tagsMap = TagUtil.uniqueTags( bs.getFolderModule().getTags( entry ) );
		
		if ( tagsMap != null )
		{
			Set<Tag> communityTagsSet;
			Set<Tag> personalTagsSet;
			Iterator<Tag> tagsIT;

			communityTagsSet = tagsMap.get( ObjectKeys.COMMUNITY_ENTITY_TAGS );
			personalTagsSet  = tagsMap.get( ObjectKeys.PERSONAL_ENTITY_TAGS );

			// ...iterate through the community tags...
			if ( communityTagsSet != null )
			{
				for ( tagsIT = communityTagsSet.iterator(); tagsIT.hasNext(); )
				{
					// ...adding each to the reply list...
					tags.add( buildTIFromTag( TagType.COMMUNITY, tagsIT.next() ) );
				}
			}

			// ...iterate through the personal tags...
			if ( personalTagsSet != null )
			{
				for ( tagsIT = personalTagsSet.iterator(); tagsIT.hasNext(); )
				{
					// ...adding each to the reply list...
					tags.add( buildTIFromTag( TagType.PERSONAL, tagsIT.next() ) );
				}
			}
		}
		
		return tags;
	}
	
	
	/*
	 * Returns a cloned copy of the expanded Binder's list from the
	 * UserProperties.
	 */
	@SuppressWarnings("unchecked")
	private static List<Long> getExpandedBindersList(AllModulesInjected bs) {
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> reply= ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));
		return reply;
	}

	/**
	 * Returns the ID of the default view definition of a folder.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getDefaultFolderDefinitionId(AllModulesInjected bs, String binderIdS) {
		// Does the user have a default definition selected for this
		// binder?
		Long binderId = Long.valueOf(binderIdS);
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(getCurrentUser().getId(), binderId);
		String userSelectedDefinition = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));

		// If we can find the default definition for this binder...
		HashMap model = new HashMap();
		Binder binder = bs.getBinderModule().getBinder(binderId);
		DefinitionHelper.getDefinitions(binder, model, userSelectedDefinition);		
		Definition def = ((Definition) model.get(WebKeys.DEFAULT_FOLDER_DEFINITION));

		// ...return it's ID or if we can't find it, return an empty
		// ...string.
		String reply = ((null == def) ? "" : def.getId());
		m_logger.debug("GwtServerHelper.getDefaultFolderDefinitionId( binderId:  '" + binderIdS + "' ):  '" + reply);
		return reply;
	}
	
	/**
	 * Returns information about the current user's favorites.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<FavoriteInfo> getFavorites(AllModulesInjected bs) {
		// Allocate an ArrayList<FavoriteInfo> to hold the favorites.
		ArrayList<FavoriteInfo> reply = new ArrayList<FavoriteInfo>();

		// Read the user's favorites.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		Object userFavorites = userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites favorites;
		if (userFavorites instanceof Document) {
			favorites = new Favorites((Document)userFavorites);
			bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, favorites.toString());
		} else {		
			favorites = new Favorites((String)userFavorites);
		}
		
		// Scan the user's favorites...
		List favoritesList = favorites.getFavoritesList();
		for (Iterator flIT = favoritesList.iterator(); flIT.hasNext(); ) {
			// ...constructing a FavoriteInfor object for each.
			FavoriteInfo fi = fiFromJSON(bs, ((JSONObject) flIT.next()));
			if (null != fi) {
				reply.add(fi);
			}
		}

		// If we get here, reply refers to an ArrayList<FavoriteInfo>
		// of the user's defined favorites.  Return it.
		return reply;
	}

	/**
	 * Returns the FolderType of a folder.
	 *
	 * @param bs
	 * @param folderId
	 * 
	 * @return
	 */
	public static FolderType getFolderType(AllModulesInjected bs, String folderId) {
		return getFolderType(bs.getBinderModule().getBinder(Long.parseLong(folderId)));
	}
	
	public static FolderType getFolderType(Binder binder) {
		// Is the binder a folder?
		FolderType reply;
		if (binder instanceof Folder) {
			// Yes!  Can we find a family name in its default view?
			String family = BinderHelper.getBinderDefaultFamilyName(binder);
			reply = FolderType.OTHER;
			if (MiscUtil.hasString(family)) {
				// Yes!  Classify the folder based on it.
				family = family.toLowerCase();
				if      (family.equals("blog"))       reply = FolderType.BLOG;
				else if (family.equals("calendar"))   reply = FolderType.CALENDAR;
				else if (family.equals("discussion")) reply = FolderType.DISCUSSION;
				else if (family.equals("file" ))      reply = FolderType.FILE;
				else if (family.equals("guestbook"))  reply = FolderType.GUESTBOOK;
				else if (family.equals("milestone"))  reply = FolderType.MILESTONE;
				else if (family.equals("miniblog"))   reply = FolderType.MINIBLOG;
				else if (family.equals("photo"))      reply = FolderType.PHOTOALBUM;
				else if (family.equals("task"))       reply = FolderType.TASK;
				else if (family.equals("survey"))     reply = FolderType.SURVEY;
				else if (family.equals("wiki"))       reply = FolderType.WIKI;
			}

			// For certain folder types, we need to special case the
			// classification for one reason or another.  Is this one
			// of them?
			String view = BinderHelper.getBinderDefaultViewName(binder);
			switch (reply) {
			case OTHER:
				// We need to special case guest book folders
				// because its definition does not contain a family
				// name.
				if (MiscUtil.hasString(view) && view.equals(VIEW_FOLDER_GUESTBOOK)) {
					reply = FolderType.GUESTBOOK;
				}				
				else {
					m_logger.debug("GwtServerHelper.getFolderType( 'Could not determine folder type' ):  " + binder.getPathName());
				}				
				break;
			
			case FILE:
				// We need to special case files because both a
				// normal file folder and a mirrored file
				// folder use 'file' for their family name.
				if (MiscUtil.hasString(view) && view.equals(VIEW_FOLDER_MIRRORED_FILE)) {
					reply = FolderType.MIRROREDFILE;
				}				
				break;
			}
		}
		
		else {
			// No, the binder isn't a folder!
			reply = FolderType.NOT_A_FOLDER;
		}

		// If we get here, reply refers to the type of folder the
		// binder is.  Return it.
		return reply;
	}
	
	/**
	 * Return the groups ldap query
	 */
	public static GwtDynamicGroupMembershipCriteria getDynamicMembershipCriteria( AllModulesInjected ami, Long groupId )
	{
		Principal principal;
		GwtDynamicGroupMembershipCriteria membershipCriteria;
		
		membershipCriteria = new GwtDynamicGroupMembershipCriteria();
		
		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			String ldapQueryXml;

			// Get the xml that defines the membership criteria
			group = (Group) principal;
			ldapQueryXml = group.getLdapQuery();

			if ( ldapQueryXml != null && ldapQueryXml.length() > 0 )
			{
				try
	    		{
	    			Document doc;
	    			Node node;
	    			Node searchNode;
	    			Node attrNode;
	    			String value;
					
					// Parse the xml string into an xml document.
					doc = DocumentHelper.parseText( ldapQueryXml );
	    			
	    			// Get the root element.
	    			node = doc.getRootElement();
	    			
	    			// Get the "updateMembershipDuringLdapSync" attribute value.
	    			attrNode = node.selectSingleNode( "@updateMembershipDuringLdapSync" );
	    			if ( attrNode != null )
	    			{
	        			value = attrNode.getText();
	        			if ( value != null && value.equalsIgnoreCase( "true" ) )
	        				membershipCriteria.setUpdateDuringLdapSync( true );
	        			else
	        				membershipCriteria.setUpdateDuringLdapSync( false );
	    			}
	    			
	    			// Get the <search ...> element.
	    			searchNode = node.selectSingleNode( "search" );
	    			if ( searchNode != null )
	    			{
    					Node baseDnNode;
    					Node filterNode;
    					
	    				// Get the "searchSubtree" attribute.
	    				attrNode = searchNode.selectSingleNode( "@searchSubtree" );
	    				if ( attrNode != null )
	    				{
	    					value = attrNode.getText();
	    					if ( value != null && value.equalsIgnoreCase( "true" ) )
	    						membershipCriteria.setSearchSubtree( true );
	    					else
	    						membershipCriteria.setSearchSubtree( false );
	    				}
	    				
	    				// Get the <baseDn> element.
	    				baseDnNode = searchNode.selectSingleNode( "baseDn" );
	    				if ( baseDnNode != null )
	    				{
	    					value = baseDnNode.getText();
	    					membershipCriteria.setBaseDn( value );
	    				}
	    				
	    				// Get the <filter> element.
	    				filterNode = searchNode.selectSingleNode( "filter" );
	    				if ( filterNode != null )
	    				{
	    					value = filterNode.getText();
	    					membershipCriteria.setLdapFilter( value );
	    				}
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			m_logger.warn( "Unable to parse dynamic group membership criteria" + ldapQueryXml );
	    		}
			}
		}

		return membershipCriteria;
	}
	
	/**
	 * Returns a GwtTeamingException from a generic Exception.
	 * 
	 * Note:  The mappings between an instance of an exception and the
	 *    exception type of the GwtTeamingException returned was based
	 *    on the code originally constructing these in
	 *    GwtRpcServiceImpl.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	public static GwtTeamingException getGwtTeamingException(Exception ex) {
		// If we were given a GwtTeamingException...
		GwtTeamingException reply;
		if ((null != ex) && (ex instanceof GwtTeamingException)) {
			// ...simply return it.
			reply = ((GwtTeamingException) ex);
		}
		
		else {
			// Otherwise, construct an appropriate GwtTeamingException.
			reply = new GwtTeamingException();
			if (null != ex) {
				ExceptionType exType;
				
				if      (ex instanceof AccessControlException               ) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof NoBinderByTheIdException             ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoFolderEntryByTheIdException        ) exType = ExceptionType.NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoUserByTheIdException               ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof OperationAccessControlExceptionNoName) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if ( ex instanceof FavoritesLimitExceededException )
				{
					exType = ExceptionType.FAVORITES_LIMIT_EXCEEDED;
				}
				else if ( ex instanceof ExtensionDefinitionInUseException )
				{
					exType = ExceptionType.EXTENSION_DEFINITION_IN_USE;
				}
				else if ( ex instanceof GroupExistsException )
				{
					exType = ExceptionType.GROUP_ALREADY_EXISTS;
				}
				else                                                          exType = ExceptionType.UNKNOWN;
				
				reply.setExceptionType(exType);
			}
		}

		// If debug logging is enabled...
		if (m_logger.isDebugEnabled()) {
			// ...log the exception that got us here.
			if (null != ex)
			     m_logger.debug("GwtServerHelper.getGwtTeamingException( SOURCE EXCEPTION ):  ", ex   );
			else m_logger.debug("GwtServerHelper.getGwtTeamingException( GWT EXCEPTION ):  ",    reply);
		}

		// If we get here, reply refers to the GwtTeamingException that
		// was requested.  Return it.
		return reply;
	}
	
	public static GwtTeamingException getGwtTeamingException() {
		// Always use the initial form of the method.
		return getGwtTeamingException(null);
	}
	
	/**
	 * Return login information such as self registration and auto complete.
	 */
	public static GwtLoginInfo getLoginInfo( HttpServletRequest request, AllModulesInjected ami )
	{
		GwtLoginInfo loginInfo;
		GwtSelfRegistrationInfo selfRegInfo;
		boolean allowAutoComplete;
		
		loginInfo = new GwtLoginInfo();
		
		// Get self-registration info.
		selfRegInfo = getSelfRegistrationInfo( request, ami );
		loginInfo.setSelfRegistrationInfo( selfRegInfo );
		
		// Read the value of the enable.login.autocomplete key from ssf.properties
		allowAutoComplete = SPropsUtil.getBoolean( "enable.login.autocomplete", false );
		loginInfo.setAllowAutoComplete( allowAutoComplete );
		
		return loginInfo;
	}
	
	
	/**
	 * Returns information about the groups the current user is a member of.
	 * 
	 * @return
	 */
	public static List<GroupInfo> getMyGroups(HttpServletRequest request, AllModulesInjected bs) {
		User user = getCurrentUser();
		return getGroups(request, bs, user.getId());
	}

	/**
	 * Returns information about the teams the current user is a member of.
	 * 
	 * @return
	 */
	public static List<TeamInfo> getMyTeams(HttpServletRequest request, AllModulesInjected bs) {
		User user = getCurrentUser();
		return getTeams(request, bs, user.getId());
	}

	/**
	 * Returns a GwtPresenceInfo object for a User.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static GwtPresenceInfo getPresenceInfo(User user)
	{
		GwtPresenceInfo gwtPresence = new GwtPresenceInfo();
		
		try {
			// Guest user can't get presence information.
			User userAsking = GwtServerHelper.getCurrentUser();
			if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(userAsking.getInternalId()))) {
				PresenceManager presenceService = ((PresenceManager) SpringContextUtil.getBean("presenceService"));
				if (null != presenceService) {
					PresenceInfo pi = null;
					int userStatus = PresenceInfo.STATUS_UNKNOWN;

					CustomAttribute attr = user.getCustomAttribute("presenceID");
					String userID = ((null != attr) ? ((String) attr.getValue()) : null);
					  
					attr = userAsking.getCustomAttribute("presenceID");
					String userIDAsking = ((null != attr) ? ((String) attr.getValue()) : null);

					if (MiscUtil.hasString(userID) && MiscUtil.hasString(userIDAsking)) {
						pi = presenceService.getPresenceInfo(userIDAsking, userID);
						if (null != pi) {
							gwtPresence.setStatusText(pi.getStatusText());
							userStatus = pi.getStatus();
						}	
					}
					
					switch (userStatus) {
					case PresenceInfo.STATUS_AVAILABLE:  gwtPresence.setStatus(GwtPresenceInfo.STATUS_AVAILABLE); break;
					case PresenceInfo.STATUS_AWAY:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_AWAY);      break;
					case PresenceInfo.STATUS_IDLE:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_IDLE);      break;
					case PresenceInfo.STATUS_BUSY:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_BUSY);      break;
					case PresenceInfo.STATUS_OFFLINE:    gwtPresence.setStatus(GwtPresenceInfo.STATUS_OFFLINE);   break;
					default:                             gwtPresence.setStatus(GwtPresenceInfo.STATUS_UNKNOWN);   break;
					}
				}
			}
		}
		catch (Exception e) {}
		return gwtPresence;
	}

	/**
	 * Returns a default GwtPresence info object that contains a
	 * localized status text string.
	 * 
	 * @return
	 */
	public static GwtPresenceInfo getPresenceInfoDefault() {
		GwtPresenceInfo reply = new GwtPresenceInfo();		
		setPresenceText(reply);
		return reply;
	}

	/**
	 * Returns the image for the presence dude to display for a
	 * GwtPresenceInfo.
	 * 
	 * @param pi
	 * 
	 * @return
	 */
	public static String getPresenceDude(GwtPresenceInfo pi) {
		String dudeGif;
		switch (pi.getStatus()) {
		case PresenceInfo.STATUS_AVAILABLE:  dudeGif = "pics/sym_s_green_dude_14.png";  break;
		case PresenceInfo.STATUS_AWAY:       dudeGif = "pics/sym_s_yellow_dude_14.png"; break;
		case PresenceInfo.STATUS_IDLE:       dudeGif = "pics/sym_s_yellow_dude_14.png"; break;
		case PresenceInfo.STATUS_BUSY:       dudeGif = "pics/sym_s_red_dude_14.png";    break;
		case PresenceInfo.STATUS_OFFLINE:    dudeGif = "pics/sym_s_gray_dude_14.png";   break;
		default:                             dudeGif = "pics/sym_s_white_dude_14.png";  break;
		}
		return dudeGif;
	}

	/**
	 * Stores a localized status text in a GwtPresenceInfo base on its
	 * status.
	 * 
	 * @param pi
	 */
	public static void setPresenceText(GwtPresenceInfo pi) {
		String statusText;
		switch (pi.getStatus()) {
		case PresenceInfo.STATUS_AVAILABLE:  statusText = NLT.get("presence.online");  break;
		case PresenceInfo.STATUS_AWAY:       statusText = NLT.get("presence.away");    break;
		case PresenceInfo.STATUS_IDLE:       statusText = NLT.get("presence.idle");    break;
		case PresenceInfo.STATUS_BUSY:       statusText = NLT.get("presence.busy");    break;
		case PresenceInfo.STATUS_OFFLINE:    statusText = NLT.get("presence.offline"); break;
		default:                             statusText = NLT.get("presence.none");    break;
		}
		pi.setStatusText(statusText);
	}

	/**
	 * Returns information about the recent places the current user has
	 * visited.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<RecentPlaceInfo> getRecentPlaces(HttpServletRequest request, AllModulesInjected bs) {
		// Allocate an ArrayList to return the recent places in.
		ArrayList<RecentPlaceInfo> rpiList = new ArrayList<RecentPlaceInfo>();
		
		// If we can't access the HttpSession...
		HttpSession hSession = getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached tabs to build the recent
			// ...places list from.  Bail.
			m_logger.debug("GwtServerHelper.getRecentPlaces( 'Could not access the current HttpSession' )");
			return rpiList;
		}

		// If we can't access the cached tabs... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TABS_KEY));
		Tabs tabs = ((Tabs) tabsObj.getData());
		if (null == tabs) {
			// ...we can't build any recent place items.  Bail.
			m_logger.debug("GwtServerHelper.getRecentPlaces( 'Could not access any cached tabs' )");
			return rpiList;
		}
		
		// What's the maximum size for a place's title?
		int maxTitle = 30;
		try {
			maxTitle = SPropsUtil.getInt("history.max.title");
		} catch (PropertyNotFoundException e) {}

		// Scan the cached tabs...
		int count = 0;
		List tabList = tabs.getTabList();
		int maxItems = SPropsUtil.getInt("recent-places-depth", 10);
		for (Iterator tabIT = tabList.iterator(); tabIT.hasNext(); ) {
			// ...creating a RecentPlaceInfo object for each...
			TabEntry tab = ((TabEntry) tabIT.next());
			RecentPlaceInfo rpi = new RecentPlaceInfo();
			String title = ((String) tab.getData().get("title"));
			if (title.length() > maxTitle) {
				title = (title.substring(0, maxTitle) + "...");
			}
			rpi.setTitle(title);
			rpi.setId(String.valueOf(tab.getTabId()));
			rpi.setType(tab.getType());
			switch (rpi.getTypeEnum()) {
			case BINDER:
				// If the tab's binder is no longer accessible...
				Long binderId = tab.getBinderId();
				Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
				if ((null == binder) || GwtUIHelper.isBinderPreDeleted(binder)) {
					// ...skip it.
					continue;
				}
				rpi.setBinderId(String.valueOf(binderId));
				rpi.setEntityPath(((String) tab.getData().get("path")));
				rpi.setEntryId(String.valueOf(tab.getEntryId()));
				rpi.setPermalink(PermaLinkUtil.getPermalink(request, binder));
				
				break;
				
			case SEARCH:
				rpi.setSearchQuery(tab.getQuery());
				rpi.setSearchQuick(((Boolean) tab.getData().get("quickSearch")));
				
				break;

			default:
				continue;
			}
			
			// ...adding it to the list of them...
			rpiList.add(rpi);
			
			// ...and stopping when we hit our maximum.
			count += 1;
			if (maxItems == count) {
				break;
			}
		}

		// If we get here, rpiList refers to a List<RecentPlaceInfo> of
		// the user's recent places.  Return it.
		return rpiList;
	}
	
	/**
	 * Returns information about the saved searches the current user
	 * as defined.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<SavedSearchInfo> getSavedSearches(AllModulesInjected bs) {
		// Allocate an ArrayList to return the saved searches in.
		List<SavedSearchInfo> ssList = new ArrayList<SavedSearchInfo>();

		// Does the user have any saved searches defined?
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUser().getId());
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			// Yes!  Scan them...
			Map userQueries = ((Map) properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES));
			Set queryKeys = userQueries.keySet();
			for (Iterator qkIT = queryKeys.iterator(); qkIT.hasNext(); ) {
				// ...added a SavedSearchInfo for each to the list.
				String key = ((String) qkIT.next());
				SavedSearchInfo ssi = new SavedSearchInfo();
				ssi.setName(key);
				ssList.add(ssi);
			}
		}

		// If there are any saved searches being returned...
		if (!(ssList.isEmpty())) {
			// ...sort them.
			Collections.sort(ssList, new SavedSearchInfoComparator());
		}
		
		// If we get here, ssList refers to a List<SavedSearchInfo> of
		// the user's saved searches.  Return it.
		return ssList;
	}
	
	/**
	 * Return information about self registration.
	 */
	@SuppressWarnings("unchecked")
	public static GwtSelfRegistrationInfo getSelfRegistrationInfo( HttpServletRequest request, AllModulesInjected ami )
	{
		GwtSelfRegistrationInfo selfRegInfo;
		boolean selfRegAllowed;
		
		selfRegInfo = new GwtSelfRegistrationInfo();
		
		// Set the flag that indicates whether self registration is allowed.
		selfRegAllowed = MiscUtil.canDoSelfRegistration( ami );
		selfRegInfo.setSelfRegistrationAllowed( selfRegAllowed );
		if ( selfRegAllowed )
		{
			ProfileModule			profileModule;
			Map<String, Definition>	entryDefsMap;
			Definition				def;
			Collection<Definition>	entryDefsCollection;
			Iterator<Definition>	entryDefsIterator;

			profileModule = ami.getProfileModule();
			
			// There is only 1 entry definition for a Profile binder.  Get it.
			entryDefsMap = profileModule.getProfileBinderEntryDefsAsMap();
			entryDefsCollection = entryDefsMap.values();
			entryDefsIterator = entryDefsCollection.iterator();
			if ( entryDefsIterator.hasNext() )
			{
				AdaptedPortletURL	adapterUrl;

				def = (Definition) entryDefsIterator.next();

				// Create the url needed to invoke the "Add User" page.
				adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
				adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
				adapterUrl.setParameter( WebKeys.URL_BINDER_ID, profileModule.getProfileBinderId().toString() );
				adapterUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
				
				selfRegInfo.setCreateUserUrl( adapterUrl.toString() );
			}
		}
		
		return selfRegInfo;
	}// end getSelfRegistrationInfo()
	
	/*
	 * Extracts a non-null string from a JSONObject.
	 */
	private static String getSFromJSO(JSONObject jso, String key) {
		String reply;
		if (jso.has(key)) {
			reply = jso.getString(key);
			if (null == reply) {
				reply = "";
			}
		}
		else {
			reply = "";
		}
		return reply;
	}
	
	/**
	 * Return the membership of the given group.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<GwtTeamingItem> getGroupMembership( AllModulesInjected ami, String groupId ) throws GwtTeamingException
	{
		ArrayList<GwtTeamingItem> retList;

		try
		{
			Long groupIdL;
			Principal group;

			retList = new ArrayList<GwtTeamingItem>();
	
			// Get the group object.
			groupIdL = Long.valueOf(groupId);
			group = ami.getProfileModule().getEntry(groupIdL);
			if ( group != null && group instanceof Group )
			{
				Iterator<Principal> itMembers;
				List<Long> membership;
				List<Principal> memberList;
				
				// Get the members of the group.
				memberList = ((Group) group).getMembers();
				
				// Get a list of the ids of all the members of this group.
				membership = new ArrayList<Long>();
				itMembers = memberList.iterator();
				while (itMembers.hasNext())
				{
					Principal member;
					
					member = (Principal) itMembers.next();
					membership.add( member.getId() );
				}
				
				// Get all the memembers of the group.  We call ResolveIDs.getPrincipals()
				// because it handles deleted users and users the logged-in user has
				// rights to see.
				memberList = ResolveIds.getPrincipals( membership );

				// For each member of the group create a GwtUser or GwtGroup object.
				itMembers = memberList.iterator();
				while ( itMembers.hasNext() )
				{
					Principal member;
	
					member = (Principal) itMembers.next();
					if (member instanceof Group)
					{
						Group nextGroup;
						GwtGroup gwtGroup;
						
						nextGroup = (Group) member;
						
						gwtGroup = new GwtGroup();
						gwtGroup.setId( nextGroup.getId().toString() );
						gwtGroup.setName( nextGroup.getName() );
						gwtGroup.setTitle( nextGroup.getTitle() );
						
						retList.add( gwtGroup );
					}
					else if (member instanceof User)
					{
						User user;
						GwtUser gwtUser;
						
						user = (User) member;
	
						gwtUser = new GwtUser();
						gwtUser.setUserId( user.getId() );
						gwtUser.setName( user.getName() );
						gwtUser.setTitle( Utils.getUserTitle( user ) );
						gwtUser.setWorkspaceTitle( user.getWSTitle() );
	
						retList.add( gwtUser );
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		return retList;
	}

	/**
	 * Returns information about the teams of a specific user
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<GroupInfo> getGroups(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		// Allocate an ArrayList<GroupInfo> to hold the groups.
		ArrayList<GroupInfo> reply = new ArrayList<GroupInfo>();

		// Scan the groups the current user is a member of...
		ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		List users = ResolveIds.getPrincipals(userIds, true);
		if (!users.isEmpty()) {
			Principal p = (Principal)users.get(0);
			Set<Long> groupIds = profileDao.getAllGroupMembership(p.getId(), RequestContextHolder.getRequestContext().getZoneId());
			List<Group> groups = profileDao.loadGroups(groupIds, RequestContextHolder.getRequestContext().getZoneId());
			for (Group myGroup : groups) {
				// ...adding a GroupInfo for each to the reply list.
				GroupInfo gi = new GroupInfo();
				gi.setId(myGroup.getId());
				gi.setTitle(myGroup.getTitle());
				reply.add(gi);
			}
		}
		
		// If there any groups being returned...
		if (!(reply.isEmpty())) {
			// ...sort them.
			Collections.sort(reply, new GroupInfoComparator());
		}
		
		// If we get here, reply refers to the ArrayList<GroupInfo> of
		// the groups the current user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Return the number of members in the given group
	 */
	@SuppressWarnings("unchecked")
	public static int getNumberOfMembers( AllModulesInjected ami, Long groupIdL ) throws GwtTeamingException
	{
		int count = 0;
		
		try
		{
			Principal group;

			// Get the group object.
			group = ami.getProfileModule().getEntry( groupIdL );
			if ( group != null && group instanceof Group )
			{
				Iterator<Principal> itMembers;
				List<Long> membership;
				List<Principal> memberList;
				
				// Get the members of the group.
				memberList = ((Group) group).getMembers();
				
				// Get a list of the ids of all the members of this group.
				membership = new ArrayList<Long>();
				itMembers = memberList.iterator();
				while (itMembers.hasNext())
				{
					Principal member;
					
					member = (Principal) itMembers.next();
					membership.add( member.getId() );
				}
				
				// Get all the memembers of the group.  We call ResolveIDs.getPrincipals()
				// because it handles deleted users and users the logged-in user has
				// rights to see.
				memberList = ResolveIds.getPrincipals( membership );
				
				count = memberList.size();
			}
		}
		catch (Exception ex)
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		return count;
	}

	/**
	 * Return subscription data for the given entry id.
	 */
	public static SubscriptionData getSubscriptionData( AllModulesInjected bs, String entryId )
	{
		SubscriptionData subscriptionData;
		User user;
		String address;
		
		subscriptionData = new SubscriptionData();

		user = GwtServerHelper.getCurrentUser();
		
		// Get the user's primary email address.
		address = user.getEmailAddress( Principal.PRIMARY_EMAIL );
		subscriptionData.setPrimaryEmailAddress( address );
		
		// Get the user's mobile email address.
		address = user.getEmailAddress( Principal.MOBILE_EMAIL );
		subscriptionData.setMobileEmailAddress( address );
		
		// Get the user's text message address
		address = user.getEmailAddress( Principal.TEXT_EMAIL );
		subscriptionData.setTextMessagingAddress( address );

		// Get the user's subscription data for the given entry.
		{
			FolderEntry entry;
			Subscription sub;
			FolderModule folderModule;
			Long entryIdL;
			
			// Get the subscription data for the given entry.
			entryIdL = new Long( entryId );
			folderModule = bs.getFolderModule();
			entry = folderModule.getEntry( null, entryIdL );
			sub = folderModule.getSubscription( entry );			
			
			if ( sub != null )
			{
				Map<Integer,String[]> subMap;
				String[] values;

				// Get the map that holds the subscription data.  The following is an example of what the data may look like.
				// 2:_primary:3:_primary,_text:5:_primary,_text,_mobile:
				subMap = sub.getStyles();
				
				// Get the subscription values for which address should be sent an email.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION );
				subscriptionData.setSendEmailTo( values );
				
				// Get the subscription values for which address should be sent an email without an attachment.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION );
				subscriptionData.setSendEmailToWithoutAttachment( values );
				
				// Get the subscription values for which address should be sent a text.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION );
				subscriptionData.setSendTextTo( values );
			}
		}
		
		return subscriptionData;
	}
	
	
	/**
	 * Returns information about the teams of a specific user
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<TeamInfo> getTeams(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		// Allocate an ArrayList<TeamInfo> to hold the teams.
		ArrayList<TeamInfo> reply = new ArrayList<TeamInfo>();

		// Scan the teams the current user is a member of...
		List<Map> myTeams = bs.getBinderModule().getTeamMemberships( userId );
		for (Iterator<Map> myTeamsIT = myTeams.iterator(); myTeamsIT.hasNext(); ) {
			// ...adding a TeamInfo for each to the reply list.
			Map myTeam = myTeamsIT.next();
			TeamInfo ti = new TeamInfo();
			ti.setBinderId(   ((String) myTeam.get(      "_docId"        )));
			ti.setEntityPath( ((String) myTeam.get(      "_entityPath"   )));
			ti.setPermalink(  PermaLinkUtil.getPermalink( request, myTeam ));
			ti.setTitle(      ((String) myTeam.get(      "title"         )));
			reply.add(ti);
		}
		
		// If there any teams being returned...
		if (!(reply.isEmpty())) {
			// ...sort them.
			Collections.sort(reply, new TeamInfoComparator());
		}
		
		// If we get here, reply refers to the ArrayList<TeamInfo> of
		// the teams the current user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Returns a List<String> of the user ID's of the people the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public static List<String> getTrackedPeople(AllModulesInjected bs) {
		// Return the IDs of the people the current user is tracking.
		Long userId = GwtServerHelper.getCurrentUser().getId();
		return SearchUtils.getTrackedPeopleIds(bs, userId);
	}
	
	/**
	 * Returns a List<String> of the binder ID's of the places the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public static List<String> getTrackedPlaces(AllModulesInjected bs) {
		Long userId = GwtServerHelper.getCurrentUser().getId();
		return SearchUtils.getTrackedPlacesIds(bs, userId);
	}
	
	/*
	 * Using a search query, returns a List<BinderData> containing
	 * the sorted list the children of a given binder.
	 */
	@SuppressWarnings("unchecked")
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, Binder binder, BucketInfo bi) {
		String pageTuple;
		if (null == bi) {
			pageTuple = "";
		}
		else {
			pageTuple = (bi.getBucketPage() + "//" + bi.getBucketPageTuple());
			String binderIdText = bi.getBucketId();
			Long binderId;
			int i = binderIdText.indexOf(".");
			if (0 <= i) {
				binderId = Long.valueOf(binderIdText.substring(0, i));
			}
			else {
				binderId = Long.valueOf(binderIdText);
			}
			binder = bs.getBinderModule().getBinder(binderId);
		}
		
		List<BinderData> reply = new ArrayList<BinderData>();
		Document wsTree = bs.getBinderModule().getDomBinderTree(
			binder.getId(), 
			// Binder bottom, boolean checkChildren, AllModulesInjected bs, String key, String page
			new WsDomTreeBuilder(
				binder,	//
				true,	// true -> childChildren
				bs,		//
				"",		//   "" -> No key
				pageTuple),	//
			1);			//    1 -> Levels.
		
		Element wsRoot = wsTree.getRootElement();
		Iterator childIT = wsRoot.selectNodes("./" + DomTreeBuilder.NODE_CHILD).iterator();
		while (childIT.hasNext()) {
			BinderData bbd = new BinderData();
			Element childElement = ((Element) childIT.next());
			String type = childElement.attributeValue("type");
			if (type.equals("range")) {
				BucketInfo childBI = new BucketInfo();
				childBI.setBucketId(       childElement.attributeValue("id"       ));
				childBI.setBucketPage(     childElement.attributeValue("page"     ));
				childBI.setBucketPageTuple(childElement.attributeValue("pageTuple"));
				childBI.setBucketTitle(    childElement.attributeValue("title"    ));
				childBI.setBucketTuple1(   childElement.attributeValue("tuple1"   ));
				childBI.setBucketTuple2(   childElement.attributeValue("tuple2"   ));				
				bbd.setBucketInfo(childBI);
			}
			else {
				bbd.setBinderId(Long.parseLong(childElement.attributeValue("id")));
			}
			reply.add(bbd);
		}
		return reply;
	}
	
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, Binder binder) {
		return getChildBinderData(bs, binder, null);
	}
	
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, BucketInfo bi) {
		return getChildBinderData(bs, null, bi);
	}
	
	/**
	 * Returns a List<TopRankedInfo> of the top ranked items from the
	 * most recent search.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<TopRankedInfo> getTopRanked(HttpServletRequest request, AllModulesInjected bs)
	{
		// Allocate an ArrayList to return the top ranked items in.
		ArrayList<TopRankedInfo> triList = new ArrayList<TopRankedInfo>();
		
		// If we can't access the HttpSession...
		HttpSession hSession = getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached top ranked items to build
			// ...the list from.  Bail.
			m_logger.debug("GwtServerHelper.getTopRanked( 'Could not access the current HttpSession' )");
			return triList;
		}

		// If there aren't any cached top ranked items... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PEOPLE_KEY));
		List<Map> peopleList = ((List<Map>) tabsObj.getData());
		int people = ((null == peopleList) ? 0 : peopleList.size());
		
		tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PLACES_KEY));
		List<Map> placesList = ((List<Map>) tabsObj.getData());
		int places = ((null == placesList) ? 0 : placesList.size());
		
		if (0 == (people + places)) {
			// ...we can't build a list.  Bail.
			m_logger.debug("GwtServerHelper.getTopRanked( 'Could not access any cached items' )");
			return triList;
		}
		
		// Scan the top ranked people...
		TopRankedInfo tri;
		for (int i = 0; i < people; i += 1) {
			// ...extract the Principal and reference count from this
			// ...person... 
			Map person = peopleList.get(i);
			Principal user     = ((Principal) person.get(WebKeys.USER_PRINCIPAL));
			Integer   refCount = ((Integer)   person.get(WebKeys.SEARCH_RESULTS_COUNT));
			String    css      = ((String)    person.get(WebKeys.SEARCH_RESULTS_RATING_CSS));
			
			// ...use them to construct a TopRankedInfo object...
			tri = new TopRankedInfo();
			tri.setTopRankedType(TopRankedType.PERSON);
			tri.setTopRankedName(Utils.getUserTitle(user));
			tri.setTopRankedPermalinkUrl(PermaLinkUtil.getPermalink(request, user));
			tri.setTopRankedRefCount((null == refCount) ? 0 : refCount.intValue());
			tri.setTopRankedCSS(css);

			// ...and add it to the list.
			triList.add(tri);
		}
		
		// Scan the top ranked places...
		for (int i = 0; i < places; i += 1) {
			// ...extract the Binder and reference count from this
			// ...place... 
			Map place = placesList.get(i);
			Binder  binder   = ((Binder)  place.get(WebKeys.BINDER));
			Integer refCount = ((Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT));
			String  css      = ((String)  place.get(WebKeys.SEARCH_RESULTS_RATING_CSS));

			// ...use them to construct a TopRankedInfo object...
			tri = new TopRankedInfo();
			tri.setTopRankedType(TopRankedType.PLACE);
			tri.setTopRankedName(binder.getTitle());
			tri.setTopRankedHoverText(binder.getPathName());
			tri.setTopRankedPermalinkUrl(PermaLinkUtil.getPermalink(request, binder));
			tri.setTopRankedRefCount((null == refCount) ? 0 : refCount.intValue());
			tri.setTopRankedCSS(css);

			// ...and add it to the list.
			triList.add(tri);
		}

		// If we get here, triList refers to an List<TopRankedInfo> of
		// the top ranked items.  Return it.
		return triList;
	}
	
	/**
	 * Return the "show setting" (show all or show unread) for the "What's new" page.
	 */
	public static ShowSetting getWhatsNewShowSetting( UserProperties userProperties )
	{
		ShowSetting showSetting;
		
		showSetting = ShowSetting.SHOW_ALL;
		
		// Do we have a user properties?
		if ( userProperties != null )
		{
			Integer property;
			
			property = (Integer) userProperties.getProperty( ObjectKeys.USER_PROPERTY_WHATS_NEW_SHOW_SETTING );
			if ( property != null )
			{
				int value;
				
				value = property.intValue();
				
				if ( value == ShowSetting.SHOW_ALL.ordinal() )
					showSetting = ShowSetting.SHOW_ALL;
				else if ( value == ShowSetting.SHOW_UNREAD.ordinal() )
					showSetting = ShowSetting.SHOW_UNREAD;
			}
		}
		
		return showSetting;
	}

	/**
	 * Returns the WorkspaceType of a binder.
	 * 
	 * @param bs
	 * @param wsId
	 * 
	 * @return
	 */
	public static WorkspaceType getWorkspaceType(AllModulesInjected bs, String wsId) {
		return getWorkspaceType(bs. getBinderModule().getBinder(Long.parseLong(wsId)));
	}
	
	public static WorkspaceType getWorkspaceType(Binder binder) {
		// Is this binder a workspace?
		WorkspaceType reply;
		if (binder instanceof Workspace) {
			// Yes!  Is it a reserved workspace?
			reply = WorkspaceType.OTHER;
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				// Yes!  Then we can determine its type based on its
				// internal ID.
				if (ws.getInternalId().equals(ObjectKeys.TOP_WORKSPACE_INTERNALID)) reply = WorkspaceType.TOP;
				if (ws.getInternalId().equals(ObjectKeys.TEAM_ROOT_INTERNALID))     reply = WorkspaceType.TEAM_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.GLOBAL_ROOT_INTERNALID))   reply = WorkspaceType.GLOBAL_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.PROFILE_ROOT_INTERNALID))  reply = WorkspaceType.PROFILE_ROOT;
			}
			else {
				// No, it isn't a reserved workspace!  Is it a user workspace?
				if (BinderHelper.isBinderUserWorkspace(binder)) {
					// Yes!  Mark it as such.
					reply = WorkspaceType.USER;
				}

				else {
					// No, it isn't a user workspace either!  Can we
					// determine the name of its default view?
					String view = BinderHelper.getBinderDefaultViewName(binder);
					if (MiscUtil.hasString(view)) {
						// Yes!  Check for those that we know.
						m_logger.debug("GwtServerHelper.getWorkspaceType( " + binder.getTitle() + "'s:  'Workspace View' ):  " + view);
						if      (view.equals(VIEW_WORKSPACE_DISCUSSIONS)) reply = WorkspaceType.DISCUSSIONS;
						else if (view.equals(VIEW_WORKSPACE_PROJECT))     reply = WorkspaceType.PROJECT_MANAGEMENT;
						else if (view.equals(VIEW_WORKSPACE_TEAM))        reply = WorkspaceType.TEAM;
						else if (view.equals(VIEW_WORKSPACE_USER))        reply = WorkspaceType.USER;
						else if (view.equals(VIEW_WORKSPACE_WELCOME))     reply = WorkspaceType.LANDING_PAGE;
						else if (view.equals(VIEW_WORKSPACE_GENERIC))     reply = WorkspaceType.WORKSPACE;
					}					
				}
			}
		}
		else {
			reply = WorkspaceType.NOT_A_WORKSPACE;
		}
		
		if (WorkspaceType.OTHER == reply) {
			m_logger.debug("GwtServerHelper.getWorkspaceType( 'Could not determine workspace type' ):  " + binder.getPathName());
		}
		return reply;
	}
	
	/**
	 * Return whether dynamic group membership is allowed.  It is allowed if the ldap
	 * configuration has a value set for "LDAP attribute that uniquely identifies a user or group"
	 */
	public static boolean isDynamicGroupMembershipAllowed( AllModulesInjected ami )
	{
		return ami.getLdapModule().isGuidConfigured();
	}
	
	/**
	 * Return true if the given group's membership is dynamic.
	 */
	public static Boolean isGroupMembershipDynamic( AllModulesInjected ami, Long groupId )
	{
		Principal principal;
		
		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			
			group = (Group) principal;
			return group.isDynamic();
		}

		return Boolean.FALSE;
	}
	
	/*
	 * Returns true if a Long is in a List<Long> and false otherwise.
	 */
	private static boolean isLongInList(Long l, List<Long> lList) {
		if ((null != l) && (null != lList)) {
			long lv = l.longValue();
			for (Iterator<Long> lIT = lList.iterator(); lIT.hasNext(); ) {
				if (lv == lIT.next().longValue()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return true if the presence service is enabled and false
	 * otherwise.
	 * 
	 * @param
 	 */
	public static boolean isPresenceEnabled()
	{
		PresenceManager presenceService = ((PresenceManager) SpringContextUtil.getBean("presenceService"));
		boolean reply = ((null != presenceService) ? presenceService.isEnabled() : false);
		return reply;
	}

	/*
	 * Returns true if two List<Long>'s contain different values and
	 * false otherwise.
	 */
	private static boolean longListsDiffer(List<Long> list1, List<Long> list2) {
		// If the lists differ in size...
		int c1 = ((null == list1) ? 0 : list1.size());
		int c2 = ((null == list2) ? 0 : list2.size());
		if (c1 != c2) {
			// ...they're different.
			return true;
		}
		
		// If they're both empty...
		if (0 == c1) {
			// ...they're the same.
			return false;
		}

		// Scan the Long's in the first list.
		for (Iterator<Long> l1IT = list1.iterator(); l1IT.hasNext(); ) {
			long l1 = l1IT.next().longValue();
			boolean found1in2 = false;
			
			// Scan the Long's in the second list.
			for (Iterator<Long> l2IT = list2.iterator(); l2IT.hasNext(); ) {
				// If we find a Long from the first list in the second
				// list...
				long l2 = l2IT.next().longValue();
				found1in2 = (l1 == l2);
				if (found1in2) {
					// ...quit looking.
					break;
				}
			}
			
			// If we failed to find a Long from the first list in the
			// second list...
			if (!found1in2) {
				// ...the lists are different.
				return true;
			}
		}
		
		// If we get here, the lists are the same.  Return
		// false.
		return false;
	}
	
	/*
	 * Merges the expanded Binder list from the current User's
	 * preferences into those in expandedBindersList.  If the resultant
	 * lists differ, they are written back to the User's preferences
	 */
	private static void mergeBinderExpansions(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// Make sure we have two non-null lists to work with.
		if (null == expandedBindersList)      expandedBindersList      = new ArrayList<Long>();
		if (null == usersExpandedBindersList) usersExpandedBindersList = new ArrayList<Long>();

		// Scan the Binder ID's in the User's expanded Binder's list.
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this Binder ID in the list we were given?
			Long l = lIT.next();
			if (!(isLongInList(l, expandedBindersList))) {
				// No!  Added it to it and force the changes to be
				// written to the User's list.
				expandedBindersList.add(0, l);
			}
		}

		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (longListsDiffer(usersExpandedBindersList, expandedBindersList)) {
			// Yes!  Write it.
			setExpandedBindersList(bs, expandedBindersList);
		}
	}

	/**
	 * Modify the given group
	 */
	public static void modifyGroup( AllModulesInjected ami, Long groupId, String title, String desc, boolean isMembershipDynamic, List<GwtTeamingItem> membership, GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		Principal principal;
		GwtServerProfiler gsp;

		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			List<Principal> currentMembers;
			Set<Long> membershipIds;
			SortedSet<Principal> principals;
			String ldapQuery;
			Group group;

			group = (Group) principal;
			
			membershipIds = new HashSet<Long>();
			
			// Capture the current membership of the group before it gets modified
			{
				ArrayList<Principal> tmpMembers;
				
				// It seems that group.getMembers() changed and is returning a list of
				// UserPrincipal objects instead of a list of User and Group objects.
				// This is causing problems when we re-index.  See bug 768094.
				currentMembers = new ArrayList<Principal>();
				tmpMembers = new ArrayList<Principal>( group.getMembers() );
				for (Principal nextMember : tmpMembers)
				{
					currentMembers.add( ami.getProfileModule().getEntry( nextMember.getId() ) );
				}
				m_logger.debug( "GwtServerHelper.modifyGroup(), number of members in current group membership: " + String.valueOf( currentMembers.size() ) );
			}
			
			// Is the group membership dynamic?
			if ( isMembershipDynamic )
			{
				// Yes
				
				if ( membershipCriteria != null )
				{
					// Execute the ldap query and get the list of members from the results.
					gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - get list of dynamic group members" );
					try
					{
						int maxCount;
						HashSet<Long> potentialMemberIds;
						
						// Get a list of all the potential members.
						potentialMemberIds = ami.getLdapModule().getDynamicGroupMembers(
																			membershipCriteria.getBaseDn(),
																			membershipCriteria.getLdapFilterWithoutCRLF(),
																			membershipCriteria.getSearchSubtree() );
						
						// Get the maximum number of users that can be in a group.
						maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
						
						// Is the number of potential members greater than the max number of group members?
						if ( potentialMemberIds.size() > maxCount )
						{
							int count;

							// Yes, only take the max number of users.
							count = 0;
							for (Long userId : potentialMemberIds)
							{
								membershipIds.add( userId );
								++count;
								
								if ( count >= maxCount )
								{
									break;
								}
							}
						}
						else
						{
							// No
							membershipIds = potentialMemberIds;
						}
					}
					catch (Exception ex)
					{
						//!!! What to do.
						membershipIds = new HashSet<Long>();
					}
					finally
					{
						gsp.end();
					}
				}
				else
					membershipIds = new HashSet<Long>();
				
				ldapQuery = membershipCriteria.getAsXml();
			}
			else
			{
				// No
				ldapQuery = null;
				
				// Get a list of all the new membership ids.
				for (GwtTeamingItem nextMember : membership)
				{
					Long id;

					id = null;
					if ( nextMember instanceof GwtUser )
						id = Long.valueOf( ((GwtUser) nextMember).getUserId() );
					else if ( nextMember instanceof GwtGroup )
						id = Long.valueOf( ((GwtGroup) nextMember).getId() );
								
					if ( id != null )
						membershipIds.add( id );
				}
			}

			// Modify the group.
			{
				Map updates;

				principals = ami.getProfileModule().getPrincipals( membershipIds );

				m_logger.debug( "GwtServerHelper.modifyGroup(), number of members in new group membership: " + String.valueOf( principals.size() ) );
			
				updates = new HashMap();
				updates.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
				updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
				updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
				updates.put( ObjectKeys.FIELD_GROUP_DYNAMIC, isMembershipDynamic );
				updates.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );
				updates.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );
				
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - getProfileModule().modifyEntry()" );
				try
				{
					ami.getProfileModule().modifyEntry( groupId, new MapInputData( updates ) );
				}
	   			catch ( Exception ex )
	   			{
	   				throw GwtServerHelper.getGwtTeamingException( ex );
	   			}
	   			finally
	   			{
	   				gsp.end();
	   			}
			}
			
			// Now deal with everyone who was affected
			{
				ArrayList<Long> usersRemovedFromGroup;
				ArrayList<Long> groupsRemovedFromGroup;
				ArrayList<Long> usersAddedToGroup;
				ArrayList<Long> groupsAddedToGroup;
				Map<Long,Principal> changes;

				usersRemovedFromGroup = new ArrayList<Long>();
				usersAddedToGroup = new ArrayList<Long>();
				groupsRemovedFromGroup = new ArrayList<Long>();
				groupsAddedToGroup = new ArrayList<Long>();
				changes = new HashMap<Long,Principal>();

				// Get a list of the users and groups that were removed from the group.
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - create list of users removed from the group." );
				try
				{
					for (Principal p : currentMembers)
					{
						Long oldMemberId;
						boolean found;

						// See if the old member is in the list of new members
						oldMemberId = p.getId();
						found = false;
						for (Principal newMember : principals)
						{
							Long newMemberId;
							
							newMemberId = newMember.getId();
							if ( oldMemberId != null && oldMemberId.equals( newMemberId ) )
							{
								found = true;
								break;
							}
						}
						
						// This principal used to be a member of the group.  Is he still a member?
						if ( found == false )
						{
							// No
							if ( (p instanceof UserPrincipal) || (p instanceof User) )
								usersRemovedFromGroup.add( oldMemberId );
							else if ( (p instanceof GroupPrincipal) || (p instanceof Group) )
								groupsRemovedFromGroup.add( oldMemberId );
							
							changes.put( oldMemberId, p );
						}
					}
				}
				finally
				{
					gsp.end();
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of users removed from group: " + String.valueOf( usersRemovedFromGroup.size() ) );
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of groups removed from group: " + String.valueOf( groupsRemovedFromGroup.size() ) );
				}
				
				// Get a list of the users and groups that were added to the group.
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - create list of users added to the group." );
				try
				{
					for (Principal p : principals)
					{
						Long newMemberId;
						boolean found;
						
						// See if the new member was already a member of the group.
						newMemberId = p.getId();
						found = false;
						for (Principal oldMember : currentMembers)
						{
							Long oldMemberId;
							
							oldMemberId = oldMember.getId();
							if ( newMemberId != null && newMemberId.equals( oldMemberId ) )
							{
								found = true;
								break;
							}
						}
						
						// Was this principal already a member of the group?
						if ( found == false )
						{
							// No
							if ( (p instanceof UserPrincipal) || (p instanceof User) )
								usersAddedToGroup.add( newMemberId );
							else if ( (p instanceof GroupPrincipal) || (p instanceof Group) )
								groupsAddedToGroup.add( newMemberId );
							
							changes.put( newMemberId, p );
						}
					}
				}
				finally
				{
					gsp.end();
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of users added to group: " + String.valueOf( usersAddedToGroup.size() ) );
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of groups added to group: " + String.valueOf( groupsAddedToGroup.size() ) );
				}

				m_logger.debug( "GwtServerHelper.modifyGroup(), number of changes to the group: " + String.valueOf( changes.size() ) );

				// Update the disk quotas and file size limits for users that were added
				// or removed from the group.
				Utils.updateDiskQuotasAndFileSizeLimits(
														ami.getProfileModule(),
														group,
														usersAddedToGroup,
														usersRemovedFromGroup,
														groupsAddedToGroup,
														groupsRemovedFromGroup );

				// After changing the group membership, re-index any user that was added or deleted
				Utils.reIndexPrincipals( ami.getProfileModule(), changes );
			}
		}
	}
	
	/**
	 * Runs the XSS checker on the GWT RPC commands that require
	 * checking.
	 * 
	 * Note:  Although we don't check everything we checked in
	 *   Evergreen and earlier, we do check what matters.  Things
	 *   we aren't checking that we used to include IDs (e.g.,
	 *   binderId's, entryId's, ...)
	 * 
	 * @param cmd
	 */
	public static void performXSSCheckOnRpcCmd(VibeRpcCmd cmd) {
		// What RPC command are we XSS checking?
		VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
		switch (cmdEnum) {
		// The following commands require XSS checks.
		case CREATE_GROUP:
		{
			CreateGroupCmd cgCmd;
			String value;
			
			cgCmd = (CreateGroupCmd) cmd;
			value = cgCmd.getName();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setName( StringCheckUtil.check( value ) );
			
			value = cgCmd.getDesc();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setDesc( StringCheckUtil.check( value ) );
			
			value = cgCmd.getTitle();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setTitle( StringCheckUtil.check( value ) );
			
			break;
		}
			
		case MARKUP_STRING_REPLACEMENT:
		{
			MarkupStringReplacementCmd msrCmd = ((MarkupStringReplacementCmd) cmd);
			String html = msrCmd.getHtml();
			if (MiscUtil.hasString(html)) {
				msrCmd.setHtml(StringCheckUtil.check(html));
			}
			break;
		}
		
		case MODIFY_GROUP:
		{
			ModifyGroupCmd mgCmd;
			String value;
			
			mgCmd = (ModifyGroupCmd) cmd;
			value = mgCmd.getDesc();
			if ( MiscUtil.hasString( value ) )
				mgCmd.setDesc( StringCheckUtil.check( value ) );
			
			value = mgCmd.getTitle();
			if ( MiscUtil.hasString( value ) )
				mgCmd.setTitle( StringCheckUtil.check( value ) );
			
			break;
		}
			
		case SAVE_BRANDING:
		{
			SaveBrandingCmd sbCmd = ((SaveBrandingCmd) cmd);
			GwtBrandingData bd = sbCmd.getBrandingData(); 
			String html = bd.getBranding();
			if (MiscUtil.hasString(html)) {
				bd.setBranding(StringCheckUtil.check(html));
			}
			break;
		}
			
		case SAVE_USER_STATUS:
		{
			SaveUserStatusCmd susCmd = ((SaveUserStatusCmd) cmd);
			String status = susCmd.getStatus();
			if (MiscUtil.hasString(status)) {
				susCmd.setStatus(StringCheckUtil.check(status));
			}
			break;
		}
			
		case SHARE_ENTRY:
		{
			ShareEntryCmd seCmd = ((ShareEntryCmd) cmd);
			String comment = seCmd.getComment();
			if (MiscUtil.hasString(comment)) {
				seCmd.setComment(StringCheckUtil.check(comment));
			}
			break;
		}
			
		case REPLY_TO_ENTRY:
		{
			ReplyToEntryCmd rteCmd = ((ReplyToEntryCmd) cmd);
			String title = rteCmd.getTitle();
			if (MiscUtil.hasString(title)) {
				rteCmd.setTitle(StringCheckUtil.check(title));
			}
			String desc = rteCmd.getDescription();
			if (MiscUtil.hasString(desc)) {
				rteCmd.setDescription(StringCheckUtil.check(desc));
			}
			break;
		}
			
		// The following commands do not require XSS checks.
		case ADD_FAVORITE:
		case CAN_MODIFY_BINDER:
		case COLLAPSE_SUBTASKS:
		case DELETE_GROUPS:
		case DELETE_TASKS:
		case EXECUTE_SEARCH:
		case EXPAND_HORIZONTAL_BUCKET:
		case EXPAND_SUBTASKS:
		case EXPAND_VERTICAL_BUCKET:
		case GET_ACTIVITY_STREAM_DATA:
		case GET_ACTIVITY_STREAM_PARAMS:
		case GET_ADD_MEETING_URL:
		case GET_ADMIN_ACTIONS:
		case GET_ALL_GROUPS:
		case GET_BINDER_BRANDING:
		case GET_BINDER_INFO:
		case GET_BINDER_PERMALINK:
		case GET_BINDER_TAGS:
		case GET_DEFAULT_ACTIVITY_STREAM:
		case GET_DEFAULT_FOLDER_DEFINITION_ID:
		case GET_DOCUMENT_BASE_URL:
		case GET_DISK_USAGE_INFO:
		case GET_ENTRY:
		case GET_ENTRY_TAGS:
		case GET_EXTENSION_FILES:
		case GET_EXTENSION_INFO:
		case GET_FAVORITES:
		case GET_FILE_ATTACHMENTS:
		case GET_FILE_SYNC_APP_CONFIGURATION:
		case GET_FOLDER:
		case GET_GROUP_ASSIGNEE_MEMBERSHIP:
		case GET_DYNAMIC_MEMBERSHIP_CRITERIA:
		case GET_GROUP_MEMBERSHIP:
		case GET_GROUP_MEMBERSHIP_TYPE:
		case GET_GROUPS:
		case GET_HORIZONTAL_NODE:
		case GET_HORIZONTAL_TREE:
		case GET_IM_URL:
		case GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED:
		case GET_LOGGED_IN_USER_PERMALINK:
		case GET_LOGIN_INFO:
		case GET_MICRO_BLOG_URL:
		case GET_MODIFY_BINDER_URL:
		case GET_MY_TEAMS:
		case GET_NUMBER_OF_MEMBERS:
		case GET_PERSONAL_PREFERENCES:
		case GET_PRESENCE_INFO:
		case GET_PROFILE_AVATARS:
		case GET_PROFILE_INFO:
		case GET_PROFILE_STATS:
		case GET_QUICK_VIEW_INFO:
		case GET_RECENT_PLACES:
		case GET_ROOT_WORKSPACE_ID:
		case GET_SAVED_SEARCHES:
		case GET_SITE_ADMIN_URL:
		case GET_SITE_BRANDING:
		case GET_SUBSCRIPTION_DATA:
		case GET_TAG_RIGHTS_FOR_BINDER:
		case GET_TAG_RIGHTS_FOR_ENTRY:
		case GET_TAG_SORT_ORDER:
		case GET_TEAM_ASSIGNEE_MEMBERSHIP:
		case GET_TEAM_MANAGEMENT_INFO:
		case GET_TEAMS:
		case GET_TASK_BUNDLE:
		case GET_TASK_LINKAGE:
		case GET_TASK_LIST:
		case GET_TOOLBAR_ITEMS:
		case GET_TOP_RANKED:
		case GET_UPGRADE_INFO:
		case GET_USER_PERMALINK:
		case GET_USER_STATUS:
		case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
		case GET_VERTICAL_NODE:
		case GET_VERTICAL_TREE:
		case GET_VIEW_FOLDER_ENTRY_URL:
		case HAS_ACTIVITY_STREAM_CHANGED:
		case IS_ALL_USERS_GROUP:
		case IS_PERSON_TRACKED:
		case IS_SEEN:
		case PERSIST_ACTIVITY_STREAM_SELECTION:
		case PERSIST_NODE_COLLAPSE:
		case PERSIST_NODE_EXPAND:
		case PURGE_TASKS:
		case REMOVE_EXTENSION:
		case REMOVE_FAVORITE:
		case REMOVE_TASK_LINKAGE:
		case REMOVE_SAVED_SEARCH:
		case SAVE_FILE_SYNC_APP_CONFIGURATION:
		case SAVE_PERSONAL_PREFERENCES:
		case SAVE_SUBSCRIPTION_DATA:
		case SAVE_TASK_COMPLETED:
		case SAVE_TASK_DUE_DATE:
		case SAVE_TASK_LINKAGE:
		case SAVE_TASK_PRIORITY:
		case SAVE_TASK_SORT:
		case SAVE_TASK_STATUS:
		case SAVE_SEARCH:
		case SAVE_TAG_SORT_ORDER:
		case SAVE_WHATS_NEW_SETTINGS:
		case SET_SEEN:
		case SET_UNSEEN:
		case TEST_GROUP_MEMBERSHIP_LDAP_QUERY:
		case TRACK_BINDER:
		case UPDATE_BINDER_TAGS:
		case UPDATE_CALCULATED_DATES:
		case UPDATE_ENTRY_TAGS:
		case UPDATE_FAVORITES:
		case UNTRACK_BINDER:
		case UNTRACK_PERSON:
		case VALIDATE_ENTRY_EVENTS:
			break;
			
		default:
			// Log an error that we encountered an unhandled command.
			m_logger.error("GwtServerHelper.performXSSCheckOnRpcCmd( Unhandled Command ):  " + cmd.getClass().getName());
			break;
		}
	}
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 *
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeCollapse(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		if (null == usersExpandedBindersList) {
			// ...we don't have to do anything since we only store
			// ...the IDs of expanded binders.  Bail.
			return;
		}

		// Scan the User's expanded Binder's list.
		boolean updateUsersList = false;
		long binderIdVal = binderId.longValue();
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this the Binder in question?
			Long l = lIT.next();
			if (l.longValue() == binderIdVal) {
				// Yes, remove it from the list, force the list to be
				// written back out and quit looking.
				usersExpandedBindersList.remove(l);
				updateUsersList = true;
				break;
			}
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Write it.
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeExpand(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		boolean updateUsersList = (null == usersExpandedBindersList);
		if (updateUsersList) {
			// ...we need to create one...
			usersExpandedBindersList = new ArrayList<Long>();
		}
		
		else {
			// ...otherwise, we look for the Binder ID in that list.
			updateUsersList = (!(isLongInList(binderId, usersExpandedBindersList)));
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Add this Binder ID to it and write it.
			usersExpandedBindersList.add(0, binderId);
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}
	
	/**
	 * Removes a search based on its SavedSearchInfo.
	 *
	 * @param bs
	 * @param ssi
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Boolean removeSavedSearch(AllModulesInjected bs, SavedSearchInfo ssi) {
		// Does the user contain any saved searches?
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUser().getId());
		Map properties = userProperties.getProperties();		
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			// Yes!  Can we find a saved search by the given name?
			Map userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			String queryName = ssi.getName();
			if (userQueries.containsKey(queryName)) {
				// Yes!  Remove it and save the modified search saved
				// list.
				userQueries.remove(queryName);
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
			}
		}

		// If we get here, the removal was successful.  Return true.
		return Boolean.TRUE;
	}
	
	/**
	 * Save the given File Sync App configuration
	 */
	public static Boolean saveFileSyncAppConfiguration( AllModulesInjected allModules, GwtFileSyncAppConfiguration fsaConfiguration )
	{
		AdminModule adminModule;
		Boolean enabled;
		Integer interval;
		
		adminModule = allModules.getAdminModule();
		enabled = new Boolean( fsaConfiguration.getIsFileSyncAppEnabled() );
		interval = new Integer( fsaConfiguration.getSyncInterval() );
		adminModule.setFileSynchAppSettings( enabled, interval, fsaConfiguration.getAutoUpdateUrl() );

		return Boolean.TRUE;
	}
	
	
	/**
	 * Saves a search based on its tab ID and SavedSearchInfo.
	 *
	 * @param bs
	 * @param searchTabId
	 * @param ssi
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SavedSearchInfo saveSearch(AllModulesInjected bs, String searchTabId, SavedSearchInfo ssi) {
		// If we can't access the HttpSession...
		HttpSession hSession = getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached tabs to save the search
			// ...with.  Bail.
			m_logger.debug("GwtServerHelper.save( 'Could not access the current HttpSession' )");
			return null;
		}

		// If we can't access the cached tabs... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TABS_KEY));
		Tabs tabs = ((Tabs) tabsObj.getData());
		if (null == tabs) {
			// ...we can't save the search.  Bail.
			m_logger.debug("GwtServerHelper.saveSearch( 'Could not access any cached tabs' )");
			return null;
		}
		
		// If we can't find the tab to save...
		Integer tabId = Integer.parseInt(searchTabId);
		Tabs.TabEntry tab = tabs.findTab(Tabs.SEARCH, tabId);
		if (null == tab) {
			// ...we can't save anything.  Bail.
			return null;
		}

		// Get the user's currently defined saved searches...
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUser().getId());
		Map properties = userProperties.getProperties();
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

		// ...store the tab as a saved search into them...
		String queryName = ssi.getName();
		userQueries.put(queryName, tab.getQuery());
		bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
		Map tabOptions = tab.getData();
		tabOptions.put(Tabs.TITLE, queryName);
		tab.setData(tabOptions);

		// ...and return a SavedSearchInfo for the newly saved search.
		SavedSearchInfo reply = new SavedSearchInfo();
		reply.setName(ssi.getName());
		return reply;
	}

	/**
	 * Save the given subscription data for the given entry id.
	 */
	@SuppressWarnings("unused")
	public static Boolean saveSubscriptionData( AllModulesInjected bs, String entryId, SubscriptionData subscriptionData ) throws GwtTeamingException
	{
		try
		{
			FolderEntry entry;
			FolderModule folderModule;
			Long entryIdL;
			Map<Integer, String[]> subscriptionSettings;
			int sendEmailTo;
			int sendEmailToWithoutAttachment;
			int sendTextTo;
			
			entryIdL = new Long( entryId );
			folderModule = bs.getFolderModule();
			entry = folderModule.getEntry( null, entryIdL );
			
			subscriptionSettings = new HashMap<Integer, String[]>();
			
			// Get the 3 notification settings.
			sendEmailTo = subscriptionData.getSendEmailTo();
			sendEmailToWithoutAttachment = subscriptionData.getSendEmailToWithoutAttachment();
			sendTextTo = subscriptionData.getSendTextTo();
			
			// Are all notifications turned off?
			if ( sendEmailTo == SubscriptionData.SEND_TO_NONE && sendEmailToWithoutAttachment == SubscriptionData.SEND_TO_NONE && sendTextTo == SubscriptionData.SEND_TO_NONE )
			{
				// Yes
				subscriptionSettings.put( Subscription.DISABLE_ALL_NOTIFICATIONS, null );
			}
			else
			{
				String[] setting;
				
				// Get the setting for "send email to"
				setting = subscriptionData.getSendEmailToAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION, setting );
				
				// Get the setting for "send email to without an attachment"
				setting = subscriptionData.getSendEmailToWithoutAttachmentAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, setting );
				
				// Get the setting for "send text to"
				setting = subscriptionData.getSendTextToAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION, setting );
			}
			
			// Save the subscription settings to the db.
			folderModule.setSubscription( null, entryIdL, subscriptionSettings );
		}
		catch (Exception e)
		{
			if (e instanceof AccessControlException)
			     m_logger.warn( "GwtSeverHelper.saveSubscriptionData() AccessControlException" );
			else m_logger.warn( "GwtServerHelper.saveSubscriptionData() unknown exception"     );
			
			throw getGwtTeamingException( e );
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Save the show setting (show all or show unread) for the What's New page to
	 * the user's properties.
	 */
	public static Boolean saveWhatsNewShowSetting( AllModulesInjected bs, ShowSetting showSetting )
	{
		ProfileModule profileModule;
		Integer setting;
		
		// Save the show setting to the user's properties.
		profileModule = bs.getProfileModule();
		setting = new Integer( showSetting.ordinal() );
		profileModule.setUserProperty( null, ObjectKeys.USER_PROPERTY_WHATS_NEW_SHOW_SETTING , setting );
		
		return Boolean.TRUE;
	}
	
	/*
	 * Stores the expanded Binder's List in a UserProperties.
	 */
	private static void setExpandedBindersList(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Do we have an expanded binders list to save?
		if (null == expandedBindersList) {
			// No!  Simply save an empty list.
			expandedBindersList = new ArrayList<Long>();
		}
		
		else {
			// Yes, we have an expanded binders list to save!  Is there
			// a limit on how many entries that list may contain?
			int maxExpandedBinders = SPropsUtil.getInt("maxExpandedBinders", ObjectKeys.MAX_EXPANDED_BINDERS);
			if ((-1) != maxExpandedBinders) {
				// Yes!  If it's contains too many, prune it to the
				// maximum.
				int c = expandedBindersList.size();
				for (int i = (c - 1); i >= maxExpandedBinders; i -= 1) {
					expandedBindersList.remove(i);
				}
			}
		}		

		// Finally, store the expanded binders list in the user's
		// properties.
		bs.getProfileModule().setUserProperty(
			null,
			ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST,
			expandedBindersList);
	}

	/**
	 * Send an email notification to the given recipients for the given entry.
	 * This code was taken from RelevanceAjaxController.java, ajaxSaveShareThisBinder() and modified.
	 */
	@SuppressWarnings("unchecked")
	public static GwtShareEntryResults shareEntry( AllModulesInjected ami, String entryId, String addedComments, List<String> principalIds, List<String> teamIds )
		throws Exception
	{
		FolderModule folderModule;
		ProfileModule profileModule;
		FolderEntry entry;
		Long entryIdL;
		Set<Long> principalIdsL;
		Set<Long> teamIdsL;
		Map sendEmailStatus;
		List<User> noAccessPrincipals;
		GwtShareEntryResults results;
		
		folderModule = ami.getFolderModule();
		entryIdL = new Long( entryId );
		entry = folderModule.getEntry( null, entryIdL );
		
		principalIdsL = new HashSet<Long>();

		// Convert all of the user and groups ids to Longs
		if ( principalIds != null )
		{
			for ( String nextId : principalIds )
			{
				principalIdsL.add( Long.valueOf( nextId ) );
			}
		}

		teamIdsL = new HashSet<Long>();
		
		// Convert all of the team ids to Longs
		if ( teamIds != null )
		{
			for ( String nextId : teamIds )
			{
				teamIdsL.add( Long.valueOf( nextId ) );
			}
		}
		
		profileModule = ami.getProfileModule();
		profileModule.setShares( entry, principalIdsL, teamIdsL );

		// Send an email to the given recipients.
		{
			String title;
			String shortTitle;
			String desc;
			Description body;
	        User user;
			String mailTitle;
			Set<String> emailAddress;
			String bccEmailAddress;

	        user = RequestContextHolder.getRequestContext().getUser();

			title = entry.getTitle();
			shortTitle = title;
			
			if ( entry.getParentBinder() != null )
				title = entry.getParentBinder().getPathName() + "/" + title;

			// Do NOT use interactive context when constructing permalink for email. See Bug 536092.
			desc = "<a href=\"" + PermaLinkUtil.getPermalinkForEmail( entry ) + "\">" + title + "</a><br/><br/>" + addedComments;
			body = new Description( desc );

			mailTitle = NLT.get( "relevance.mailShared", new Object[]{Utils.getUserTitle( user )} );
			mailTitle += " (" + shortTitle +")";
			
			emailAddress = new HashSet();
			
			//See if this user wants to be BCC'd on all mail sent out
			bccEmailAddress = user.getBccEmailAddress();
			if ( bccEmailAddress != null && !bccEmailAddress.equals("") )
			{
				if ( !emailAddress.contains( bccEmailAddress.trim() ) )
				{
					//Add the user's chosen bcc email address
					emailAddress.add( bccEmailAddress.trim() );
				}
			}
			
			// Send the email notification
			sendEmailStatus = ami.getAdminModule().sendMail( principalIdsL, teamIdsL, emailAddress, null, null, mailTitle, body );
		}
			
		// Check to see if the recipients have rights to see the given entry.
		{
			Set<Long> totalIds;
			Set<Principal> totalUsers;

			totalIds = new HashSet<Long>();
			totalIds.addAll( principalIdsL );
			
			totalUsers = profileModule.getPrincipals( totalIds );
			noAccessPrincipals = new ArrayList<User>();
			for ( Principal p : totalUsers )
			{
				if ( p instanceof User )
				{
					try
					{
						AccessUtils.readCheck( (User)p, entry );
					}
					catch( AccessControlException e )
					{
						noAccessPrincipals.add( (User) p );
					}
				}
			}
		}

		results = new GwtShareEntryResults();
		
		// Package up the results.
		{
			List errors;

			// Do we have any users who were sent an email who don't have rights to read the entry?
			if ( noAccessPrincipals != null && noAccessPrincipals.size() > 0 )
			{
				// Yes, add them to the results
				for ( User user : noAccessPrincipals )
				{
					GwtUser gwtUser;
					
					// Add this user to the results.
					gwtUser = new GwtUser();
					gwtUser.setUserId( user.getId() );
					gwtUser.setName( user.getName() );
					gwtUser.setTitle( Utils.getUserTitle( user ) );
					gwtUser.setWorkspaceTitle( user.getWSTitle() );
					
					results.addUser( gwtUser );
				}
			}
			
			// Add any errors that happened to the results.
			errors = (List)sendEmailStatus.get( ObjectKeys.SENDMAIL_ERRORS );
			results.setErrors( (String[])errors.toArray( new String[0]) );
		}
			
		return results;
	}

	/**
	 * Execute the given ldap query and return the number of users/groups it found.
	 */
	public static Integer testGroupMembershipCriteria(
												AllModulesInjected ami,
												GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		LdapModule ldapModule;
		Integer count = null;
		
		ldapModule = ami.getLdapModule();
		
		// Is the guid attribute configured the the ldap configuration?
		if ( ldapModule.isGuidConfigured() )
		{
			// Yes
			try
			{
				count = ldapModule.testGroupMembershipCriteria(
															membershipCriteria.getBaseDn(),
															membershipCriteria.getLdapFilterWithoutCRLF(),
															membershipCriteria.getSearchSubtree() );
			}
			catch (Exception ex)
			{
				throw GwtServerHelper.getGwtTeamingException( ex );
			}
		}
		else
		{
			GwtTeamingException ex;
			
			// No, throw an exception
			ex = GwtServerHelper.getGwtTeamingException();
			ex.setExceptionType( ExceptionType.LDAP_GUID_NOT_CONFIGURED );
			throw ex;
		}
		
		return count;
	}
	
	/**
	 * Update the tags for the given binder.
	 */
	public static Boolean updateBinderTags( AllModulesInjected bs, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		BinderModule bm;
		Binder binder;

		bm = bs.getBinderModule();
		binder = bm.getBinder( Long.parseLong( binderId ) );
		
		// Go through the list of tags to be deleted and delete them.
		for ( TagInfo nextTag : tagsToBeDeleted )
		{
			deleteBinderTag( bm, binder, nextTag );
		}
		
		// Go through the list of tags to be added and add them.
		for ( TagInfo nextTag : tagsToBeAdded )
		{
			addBinderTag( bm, binder, nextTag );
		}
		
		// If we get here, everything worked.
		return Boolean.TRUE;
	}
	
	/**
	 * Update the tags for the given entry.
	 */
	public static Boolean updateEntryTags( AllModulesInjected bs, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		FolderModule fm;
		Long entryIdL;
		
		fm = bs.getFolderModule();
		entryIdL = Long.parseLong( entryId );
		
		// Go through the list of tags to be deleted and delete them.
		for ( TagInfo nextTag : tagsToBeDeleted )
		{
			deleteEntryTag( fm, entryIdL, nextTag );
		}
		
		// Go through the list of tags to be added and add them.
		for ( TagInfo nextTag : tagsToBeAdded )
		{
			addEntryTag( fm, entryIdL, nextTag );
		}

		// If we get here, everything worked.
		return Boolean.TRUE;
	}
	
	/**
	 * Validate the list of TeamingEvents to see if the user has rights to perform the events
	 */
	public static void validateEntryEvents( AllModulesInjected bs, HttpRequestInfo ri, List<EventValidation> eventValidations, String entryId )
	{
		// Initialize all events as invalid.
		for ( EventValidation nextValidation : eventValidations )
		{
			// Validate this event.
			nextValidation.setIsValid( false );
		}

		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			for ( EventValidation nextValidation : eventValidations )
			{
				TeamingEvents teamingEvent;
				
				// Validate the next event.
				try
				{
					teamingEvent = TeamingEvents.getEnum(nextValidation.getEventOrdinal());
					
					if ( teamingEvent.equals( TeamingEvents.INVOKE_REPLY ))
						folderModule.checkAccess( folderEntry, FolderOperation.addReply );
					else if ( teamingEvent.equals( TeamingEvents.INVOKE_TAG ))
					{
						// Tag is valid if the user can manage public tags or can read the entry.
						if ( canManagePublicEntryTags( bs, entryId ) == true )
						{
							// Nothing to do.
						}
						else if ( canManagePersonalEntryTags( bs, entryId ) )
						{
							// Nothing to do.
						}
						else
							throw new AccessControlException();
					}
					else if ( teamingEvent.equals( TeamingEvents.INVOKE_SHARE ))
						folderModule.checkAccess( folderEntry, FolderOperation.readEntry );
					else if ( teamingEvent.equals( TeamingEvents.INVOKE_SUBSCRIBE ))
						folderModule.checkAccess( folderEntry, FolderOperation.readEntry );

					// If we get here the action is valid.
					nextValidation.setIsValid( true );
				}
				catch (AccessControlException acEx)
				{
				}
			}
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
	}
}
