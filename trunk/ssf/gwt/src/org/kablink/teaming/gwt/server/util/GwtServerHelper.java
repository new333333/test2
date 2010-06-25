/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.TopRankedInfo.TopRankedType;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.license.LicenseModule.LicenseOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.Tabs.TabEntry;
import org.kablink.util.PropertyNotFoundException;


/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
public class GwtServerHelper {
	protected static Log m_logger = LogFactory.getLog(GwtServerHelper.class);
	
	/**
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
	   
	/**
	 * Inner class used compare two TreeInfo objects.
	 */
	private static class TreeInfoComparator implements Comparator<TreeInfo> {
		/**
		 * Class constructor.
		 */
		public TreeInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * TreeInfo's.
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
		public int compare(TreeInfo ti1, TreeInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getBinderTitle(), ti2.getBinderTitle());
		}
	}	   
	   
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtServerHelper() {
		// Nothing to do.
	}

	/**
	 * Adds a tag to those defined on a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param binderTag
	 * 
	 * @return
	 */
	public static TagInfo addBinderTag(AllModulesInjected bs, String binderIdS, TagInfo binderTag) {
		// Define the new tag.
		boolean community = binderTag.isCommunityTag();
		BinderModule bm = bs.getBinderModule();
		Long binderId = Long.parseLong(binderIdS);
		String binderTagName = binderTag.getTagName();
		bm.setTag(binderId, binderTagName, community);

		// Find the new tag in those attached to the binder...
		Map<String, SortedSet<Tag>> tagsMap = TagUtil.uniqueTags(bm.getTags(bm.getBinder(binderId)));
		Set<Tag> tags = tagsMap.get(community ? ObjectKeys.COMMUNITY_ENTITY_TAGS : ObjectKeys.PERSONAL_ENTITY_TAGS);
		for (Iterator<Tag> tagsIT = tags.iterator(); tagsIT.hasNext(); ) {
			Tag tag = tagsIT.next();
			if (tag.getName().equalsIgnoreCase(binderTagName)) {
				// ...and return a new TagInfo for it. 
				return buildTIFromTag(binderTag.getTagType(), tag);
			}
		}

		// If we get here, something about creating the tag failed.
		return null;
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
		return buildTreeInfoFromBinderImpl(request, bs, binder, expandedBindersList, (null != expandedBindersList), 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinderImpl(request, bs, binder, null, false, 0);
	}
	
	/*
	 * Builds a TreeInfo object for a given Binder.
	 */
	private static TreeInfo buildTreeInfoFromBinderImpl(HttpServletRequest request, AllModulesInjected bs, Binder binder, List<Long> expandedBindersList, boolean mergeUsersExpansions, int depth) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderInfo(getBinderInfo(binder));
		reply.setBinderTitle(binder.getTitle());
		reply.setBinderChildren(binder.getBinderCount());
		String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
		reply.setBinderPermalink(binderPermalink);
		reply.setBinderTrashPermalink(GwtUIHelper.getTrashPermalink(binderPermalink));
		if (binder instanceof Folder) {
			reply.setBinderIconName(binder.getIconName());
		}

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
			// Yes!  Scan the Binder's children...
			List<TreeInfo> childTIList = reply.getChildBindersList(); 
			List<Binder> childBinderList = getVisibleBinderDecendents(bs, binder);
			for (Iterator<Binder> biIT = childBinderList.iterator(); biIT.hasNext(); ) {
				// ...creating a TreeInfo for each...
				Binder subBinder = biIT.next();
				TreeInfo subWsTI = buildTreeInfoFromBinderImpl(request, bs, subBinder, expandedBindersList, false, (depth + 1));
				childTIList.add(subWsTI);
			}
			
			// ...and update the count of Binder children as it may
			// ...have changed based on visibility.
			reply.setBinderChildren(childBinderList.size());
		}

		// If there's more than one child being tracked for this
		// TreeInfo...
		List<TreeInfo> childBindersList = reply.getChildBindersList();
		if (1 < childBindersList.size()) {
			// ...sort them.
			Collections.sort(childBindersList, new TreeInfoComparator());
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
				reply.add(buildTreeInfoFromBinderImpl(request, bs, binder, null, false, (-1)));
			}
		}
		return reply;
	}

	/*
	 * Scans the tags in newTags and any that aren't defined in oldTags
	 * are defined on the binder.
	 */
	private static void defineNewTags(BinderModule bm, Long binderId, List<TagInfo> newTags, Set<Tag> oldTags, boolean community) {
		// Scan the new tags...
		for (Iterator<TagInfo> newTagsIT = newTags.iterator(); newTagsIT.hasNext(); ) {
			// ...scan the old tags looking for this new tag... 
			TagInfo ti = newTagsIT.next();
			boolean found = false;
			for (Iterator<Tag> oldTagsIT = oldTags.iterator(); oldTagsIT.hasNext(); ) {
				Tag tag = oldTagsIT.next();
				if (tag.getName().equalsIgnoreCase(ti.getTagName())) {
					// ...and break out of the old tag scan loop if we
					// ...find the new tag.
					found = true;
					break;
				}
			}

			// If we didn't find the new tag in the old tag list...
			if (!found) {
				// ...define it.
				bm.setTag(binderId, ti.getTagName(), community);				
			}
		}
	}
	
	/*
	 * Scans the tags in oldTags and any that aren't defined in newTags
	 * are deleted from the binder.
	 */
	private static void deleteMissingTags(BinderModule bm, Long binderId, List<TagInfo> newTags, Set<Tag> oldTags) {
		// Scan the old tags...
		for (Iterator<Tag> oldTagsIT = oldTags.iterator(); oldTagsIT.hasNext(); ) {
			// ...scan the new tags looking for this old tag...
			Tag tag = oldTagsIT.next();
			boolean found = false;
			for (Iterator<TagInfo> newTagsIT = newTags.iterator(); newTagsIT.hasNext(); ) {
				TagInfo ti = newTagsIT.next();
				if (tag.getName().equalsIgnoreCase(ti.getTagName())) {
					// ...and break out of the new tag scan loop if we
					// ...find the old tag.
					found = true;
					break;
				}
			}

			// If we didn't find the old tag in the new tag list...
			if (!found) {
				// ...delete it.
				bm.deleteTag(binderId, tag.getId());
			}
		}
	}

	/*
	 * Constructs a FavoriteInfo object from a JSONObject.
	 */
	private static FavoriteInfo fiFromJSON(JSONObject jso) {
		FavoriteInfo reply = new FavoriteInfo();
		
		reply.setAction(  getSFromJSO(jso, "action"  ));
		reply.setCategory(getSFromJSO(jso, "category"));
		reply.setEletype( getSFromJSO(jso, "eletype" ));
		reply.setHover(   getSFromJSO(jso, "hover"   ));
		reply.setId(      getSFromJSO(jso, "id"      ));
		reply.setName(    getSFromJSO(jso, "name"    ));
		reply.setType(    getSFromJSO(jso, "type"    ));
		reply.setValue(   getSFromJSO(jso, "value"   ));
		
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
			
			// Does the user have rights to "Add user"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.addEntry ) )
				{
					List defaultEntryDefinitions;

					defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
					
					if ( !defaultEntryDefinitions.isEmpty() )
					{
						Definition def = (Definition) defaultEntryDefinitions.get( 0 );
						String[] nltArgs = new String[] {NLT.getDef(def.getTitle())};

						title = NLT.get( "toolbar.new_with_arg", nltArgs );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", true );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
						adaptedUrl.setParameter( WebKeys.URL_BINDER_ID, profilesBinder.getId().toString() );
						adaptedUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
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
			if ( definitionModule.testAccess(binder, Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition) ||
				 definitionModule.testAccess(binder, Definition.WORKFLOW, DefinitionOperation.manageDefinition))
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

			// Does the user have rights to "configure hope page"?
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

			// Does the user have rights to "Import profiles"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					// Yes
					title = NLT.get( "administration.import.profiles" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_PROFILES_IMPORT );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.IMPORT_PROFILES );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

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
				
				brandingSourceBinderId = brandingSourceBinder.getId().toString();
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
	
				brandingData.setBranding( branding );
				
				// Get the additional branding information.
				{
					String xmlStr;
					
					brandingExt = new GwtBrandingDataExt();
					
					// Is there old-style branding?
					if ( branding != null && branding.length() > 0 )
					{
						// Yes
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
					}
					else
					{
						// No
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
					}

					// Get the xml that represents the branding data.  The following is an example of what the xml should look like.
					// 	<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced">
					// 		<background color="" imgName="" />
					// 	</brandingData>
					xmlStr = brandingSourceBinder.getBrandingExt();
					
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
		catch (AccessControlException ace) {
			m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (AccessControlException) ):  '" + String.valueOf(binderId) + "'");
			reply = null;
		}

		// Do we have a Binder that's in the trash...
		if ((null != reply) && isBinderPreDeleted(reply)) {
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
		Binder binder = getBinderSafely(bs.getBinderModule(), binderId);
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
		reply.setBinderId(binder.getId());
		                                    reply.setEntityType(   getBinderEntityType(binder));
		                                    reply.setBinderType(   getBinderType(      binder));
		if      (reply.isBinderFolder())    reply.setFolderType(   getFolderType(      binder));
		else if (reply.isBinderWorkspace()) reply.setWorkspaceType(getWorkspaceType(   binder));
		return reply;
	}
	
	/**
	 * Returns s Binder from it's ID guarding against any exceptions.
	 * If an exception is caught, null is returned.
	 * 
	 * @param bm
	 * @param binderId
	 * 
	 * @return
	 */
	public static Binder getBinderSafely(BinderModule bm, String binderId) {
		return getBinderSafely(bm, Long.parseLong(binderId));
	}
	
	public static Binder getBinderSafely(BinderModule bm, Long binderId) {
		Binder reply;
		try
		{
			reply = bm.getBinder(binderId);
		}
		catch (Exception e)
		{
			m_logger.debug("GwtServerHelper.getBinderSafely(Binder could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
			reply = null;
		}
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
	public static List<TagInfo> getBinderTags(AllModulesInjected bs, String binderId) {
		BinderModule bm = bs.getBinderModule();
		return getBinderTags(bm, bm.getBinder(Long.parseLong(binderId)));
	}
	
	public static List<TagInfo> getBinderTags(BinderModule bm, Binder binder) {
		// Allocate an ArrayList to return the TagInfo's in...
		List<TagInfo> reply = new ArrayList<TagInfo>();

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
			reply.add(fiFromJSON((JSONObject) flIT.next()));
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
		FolderType reply;
		if (binder instanceof Folder) {
			reply = FolderType.OTHER;
			Element familyProperty = ((Element) binder.getDefaultViewDef().getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']"));
			if (familyProperty != null) {
				String dFamily = familyProperty.attributeValue("value", "");
				if (null != dFamily) {
					dFamily = dFamily.toLowerCase();
					if      (dFamily.equals("blog"))       reply = FolderType.BLOG;
					else if (dFamily.equals("calendar"))   reply = FolderType.CALENDAR;
					else if (dFamily.equals("discussion")) reply = FolderType.DISCUSSION;
					else if (dFamily.equals("file"))       reply = FolderType.FILE;
					else if (dFamily.equals("guestbook"))  reply = FolderType.GUESTBOOK;
					else if (dFamily.equals("milestone"))  reply = FolderType.MILESTONE;
					else if (dFamily.equals("miniblog"))   reply = FolderType.MINIBLOG;
					else if (dFamily.equals("photo"))      reply = FolderType.PHOTOALBUM;
					else if (dFamily.equals("task"))       reply = FolderType.TASK;
					else if (dFamily.equals("survey"))     reply = FolderType.SURVEY;
					else if (dFamily.equals("wiki"))       reply = FolderType.WIKI;
				}
			}
		}
		else {
			reply = FolderType.NOT_A_FOLDER;
		}
		
		if (FolderType.OTHER == reply) {
			m_logger.debug("GwtServerHelper.getFolderType( 'Could not determine folder type' ):  " + binder.getPathName());
		}
		return reply;
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
		Tabs tabs = ((Tabs) hSession.getAttribute(GwtUIHelper.CACHED_TABS_KEY));
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
				Long binderId = tab.getBinderId();
				rpi.setBinderId(String.valueOf(binderId));
				rpi.setEntityPath(((String) tab.getData().get("path")));
				rpi.setEntryId(String.valueOf(tab.getEntryId()));
				rpi.setPermalink(PermaLinkUtil.getPermalink(request, bs.getBinderModule().getBinder(binderId)));
				
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
		
		// If there's more than one team being returned...
		if (1 < reply.size()) {
			// ...sort them.
			Collections.sort(reply, new TeamInfoComparator());
		}
		
		// If we get here, reply refers to the ArrayList<TeamInfo> of
		// the teams the current user is a member of.  Return it.
		return reply;
	}
	
	/*
	 * Returns a List<Binder> of the child Binder's of binder that are
	 * visible to the user.
	 * 
	 * Note:  For a way to do this using the search index, start with
	 *    TrashHelper.containsVisibleBinders().
	 */
	@SuppressWarnings("unchecked")
	private static List<Binder> getVisibleBinderDecendents(AllModulesInjected bs, Binder binder) {
		ArrayList<Binder> reply = new ArrayList<Binder>();
		
		List allSubBinders = binder.getBinders();
		for (Iterator bi = allSubBinders.iterator(); bi.hasNext(); ) {
			Binder subBinder = ((Binder) bi.next());
			if ((!(isBinderPreDeleted(subBinder))) && 
					(bs.getBinderModule().testAccess(subBinder, BinderOperation.readEntries) || 
					bs.getBinderModule().testAccess(subBinder, BinderOperation.viewBinderTitle))) {
				reply.add(0, subBinder);
			}
		}
		
		return reply;
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
		List<Map> peopleList = ((List<Map>) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PEOPLE_KEY)); int people = ((null == peopleList) ? 0 : peopleList.size());
		List<Map> placesList = ((List<Map>) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PLACES_KEY)); int places = ((null == placesList) ? 0 : placesList.size());
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
		WorkspaceType reply;
		if (binder instanceof Workspace) {
			reply = WorkspaceType.OTHER;
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				if (ws.getInternalId().equals(ObjectKeys.TOP_WORKSPACE_INTERNALID)) reply = WorkspaceType.TOP;
				if (ws.getInternalId().equals(ObjectKeys.TEAM_ROOT_INTERNALID))     reply = WorkspaceType.TEAM_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.GLOBAL_ROOT_INTERNALID))   reply = WorkspaceType.GLOBAL_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.PROFILE_ROOT_INTERNALID))  reply = WorkspaceType.PROFILE_ROOT;
			}
			else {
				if      (BinderHelper.isBinderUserWorkspace(binder) ) reply = WorkspaceType.USER;
				else if (BinderHelper.isBinderTeamWorkspace(binder) ) reply = WorkspaceType.TEAM;
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
	
	/*
	 * Returns true if a Binder is preDeleted and false otherwise. 
	 */
	private static boolean isBinderPreDeleted(Binder binder) {
		// If we have a Binder...
		boolean reply = false;
		if (null != binder) {
			// ...check it if it's a Folder or Workspace.
			if (binder instanceof Folder) {
				reply = ((Folder) binder).isPreDeleted();
			}
			else if (binder instanceof Workspace) {
				reply = ((Workspace) binder).isPreDeleted();
			}
		}
		
		// If we get here, reply is true if the Binder is
		// preDeleted and false otherwise.
		return reply;
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
	 * Removes a tag from those defined on a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param binderTag
	 * 
	 * @return
	 */
	public static Boolean removeBinderTag(AllModulesInjected bs, String binderId, TagInfo binderTag) {
		bs.getBinderModule().deleteTag(Long.parseLong(binderId), binderTag.getTagId());
		return Boolean.TRUE;
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
		Tabs tabs = ((Tabs) hSession.getAttribute(GwtUIHelper.CACHED_TABS_KEY));
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
	
	/*
	 * Stores the expanded Binder's List in a UserProperties.
	 */
	private static void setExpandedBindersList(AllModulesInjected bs, List<Long> expandedBindersList) {
		bs.getProfileModule().setUserProperty(
			null,
			ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST,
			((null == expandedBindersList) ?
				new ArrayList<Long>()      :
				expandedBindersList));
	}
	
	/**
	 * Updates the tags defined on a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param binderTags
	 * 
	 * @return
	 */
	public static Boolean updateBinderTags(AllModulesInjected bs, String binderId, List<TagInfo> binderTags) {
		BinderModule bm = bs.getBinderModule();
		return updateBinderTags(bm, bm.getBinder(Long.parseLong(binderId)), binderTags);
	}
	
	public static Boolean updateBinderTags(BinderModule bm, Binder binder, List<TagInfo> binderTags) {
		// Split the tags between community tags and personal tags.
		List<TagInfo> newCommunityTags = new ArrayList<TagInfo>();
		List<TagInfo> newPersonalTags  = new ArrayList<TagInfo>();
		for (Iterator<TagInfo> tagsIT = binderTags.iterator(); tagsIT.hasNext(); ) {
			TagInfo ti = tagsIT.next();
			if (ti.isCommunityTag()) newCommunityTags.add(ti);
			else                     newPersonalTags.add(ti);
		}

		// Read the currently defined tags from the binder.
		Map<String, SortedSet<Tag>> oldTags = TagUtil.uniqueTags(bm.getTags(binder));
		Set<Tag> oldCommunityTags = oldTags.get(ObjectKeys.COMMUNITY_ENTITY_TAGS);
		Set<Tag> oldPersonalTags  = oldTags.get(ObjectKeys.PERSONAL_ENTITY_TAGS);

		// Delete any existing tags that aren't part of the new list...
		Long binderId = binder.getId();
		deleteMissingTags(bm, binderId, newCommunityTags, oldCommunityTags);
		deleteMissingTags(bm, binderId, newPersonalTags,  oldPersonalTags);

		// ...and define and new tags that aren't part of the existing
		// ...list.
		defineNewTags(bm, binderId, newCommunityTags, oldCommunityTags, true);
		defineNewTags(bm, binderId, newPersonalTags,  oldPersonalTags,  false);

		// If we get here, the update was successful.  Return true.
		return Boolean.TRUE;
	}
}
