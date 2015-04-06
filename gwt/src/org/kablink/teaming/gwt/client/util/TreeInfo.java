/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate workspace tree information between the
 * client (i.e., the WorkspaceTreeControl) and the server (i.e.,
 * GwtRpcServiceImpl.)
 * 
 * @author drfoster@novell.com
 */
public class TreeInfo implements IsSerializable, VibeRpcResponseData {
	private List<TreeInfo>	m_collectionsList;
	private List<TreeInfo>	m_childBindersList = new ArrayList<TreeInfo>();
	private BinderIcons		m_binderIcons      = new BinderIcons();
	private BinderInfo		m_binderInfo       = new BinderInfo();
	private boolean			m_binderBorderTop;
	private boolean			m_binderExpanded;
	private int				m_binderChildren;
	private int				m_binderCollections;
	private String			m_binderHover = "";
	private String			m_binderHoverImage;
	private String			m_binderTitle          = "";
	private String			m_binderPermalink      = "";
	private String			m_binderTrashPermalink = "";

	// The following are only used for TreeInfo's associated with
	// activity streams.
	private ActivityStreamInfo	m_activityStreamInfo;
	private boolean				m_activityStream;
	private TeamingEvents		m_activityStreamEvent = TeamingEvents.UNDEFINED;
	
	// The following is only used for TreeInfo's that represent a
	// bucket of Binder's.
	private BucketInfo m_bucketInfo;

	// Used on the client side only by the binder bread crumb tree to
	// facilitate styling the different nodes along the binder's path.
	private transient boolean m_rootTail;
	
	// Used on the client side only by the sidebar tree to cache the
	// Image object used for the binder.  It uses this to hold a
	// Binder's original image while it displays a spinning wheel
	// during a context load.
	private transient Object m_binderUIImg;

	// Used on the client side only by the sidebar tree to track the
	// grid and row this TreeInfo is rendered at.
	private transient Object m_renderedGrid;
	private transient int    m_renderedGridRow;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TreeInfo() {
		// Initialize the super class.
		super();
	}

	/**
	 * Stores a TreeInfo to the child Binder's contained in
	 * this TreeInfo.
	 * 
	 * @param childBinder
	 */
	public void addChildBinder(TreeInfo childBinder) {
		if (null == m_childBindersList) {
			m_childBindersList = new ArrayList<TreeInfo>();
		}
		m_childBindersList.add(childBinder);
		updateChildBindersCount();
	}

	/**
	 * Stores a TreeInfo in the collection Binder's contained in
	 * this TreeInfo.
	 * 
	 * @param collection
	 */
	public void addCollection(TreeInfo collection) {
		if (null == m_collectionsList) {
			m_collectionsList = new ArrayList<TreeInfo>();
		}
		m_collectionsList.add(collection);
		updateCollectionsCount();
	}

	/**
	 * Clears the binder icons being tracked in this TreeInfo.
	 */
	public void clearBinderIcons() {
		m_binderIcons.clearBinderIcons();
	}
	
	/**
	 * Clears the list of children for this TreeInfo's Binder.
	 */
	public void clearChildBindersList() {
		// If we're tracking any child Binders in this TreeInfo...
		List<TreeInfo> childBindersList = getChildBindersList();
		int children = ((null == childBindersList) ? 0 : childBindersList.size());
		if (0 < children) {
			// ...forget about them.
			children = getBinderChildren();
			setChildBindersList(new ArrayList<TreeInfo>());
			setBinderChildren(children);
		}
	}
	
	/**
	 * Creates a copy TreeInfo with the base information from this
	 * TreeInfo.
	 * 
	 * @return
	 */
	public TreeInfo copyBaseTI() {
		// Create the target TreeInfo...
		TreeInfo reply = new TreeInfo();

		// ...copy the information from this TreeInfo...
		reply.setActivityStream(      isActivityStream()                                         );
		reply.setActivityStreamEvent( getActivityStreamEvent(), getActivityStreamInfo()          );
		reply.setBinderBorderTop(     isBinderBorderTop()                                        );
		reply.setBinderExpanded(      isBinderExpanded()                                         );
		reply.setBinderHover(         getBinderHover()                                           );
		reply.setBinderIcon(          getBinderIcon(BinderIconSize.SMALL),  BinderIconSize.SMALL );
		reply.setBinderIcon(          getBinderIcon(BinderIconSize.MEDIUM), BinderIconSize.MEDIUM);
		reply.setBinderIcon(          getBinderIcon(BinderIconSize.LARGE),  BinderIconSize.LARGE );
		reply.setBinderInfo(          getBinderInfo().copyBinderInfo()                           );
		reply.setBinderPermalink(     getBinderPermalink()                                       );
		reply.setBinderTitle(         getBinderTitle()                                           );
		reply.setBinderTrashPermalink(getBinderTrashPermalink()                                  );
		
		// ...and return it.
		return reply;
	}

	/**
	 * Returns the TreeInfo from another TreeInfo that references a
	 * specific activity stream.
	 * 
	 * @param ti
	 * @param asi
	 * 
	 * @return
	 */
	public static TreeInfo findActivityStreamTI(TreeInfo ti, ActivityStreamInfo asi) {
		// Is this an activity stream TreeInfo?
		if (ti.isActivityStream()) {
			// Yes!  Is it for the activity stream in question?
			if (asi.isEqual(ti.getActivityStreamInfo())) {
				// Yes!  Return.
				return ti;
			}
			
			// Otherwise, if the TreeInfo has child Binder's...
			List<TreeInfo> childBindersList = ti.getChildBindersList();
			if ((null != childBindersList) && (0 < childBindersList.size())) {
				// ...scan them...
				for (TreeInfo childTI: childBindersList) {
					// ...and if one of them references the activity stream
					// ...in question...
					TreeInfo reply = findActivityStreamTI(childTI, asi);
					if (null != reply) {
						// ...return it.
						return reply;
					}
				}
			}
		}

		// If we get here, the activity stream was nowhere to be found
		// in the TreeInfo.  Return null.
		return null;
	}
	
	/**
	 * Returns the first TreeInfo from another TreeInfo that references
	 * a specific activity stream type.
	 * 
	 * @param ti
	 * @param as
	 * 
	 * @return
	 */
	public static TreeInfo findFirstActivityStreamTI(TreeInfo ti, ActivityStream as) {
		// Is this an activity stream TreeInfo?
		if (ti.isActivityStream()) {
			// Yes!  Is it for the activity stream in question?
			ActivityStreamInfo tiASI = ti.getActivityStreamInfo();
			ActivityStream     tiAS  = (null == tiASI ? ActivityStream.UNKNOWN : tiASI.getActivityStream());
			if (as.equals(tiAS)) {
				// Yes!  Return.
				return ti;
			}
			
			// Otherwise, if the TreeInfo has child Binder's...
			List<TreeInfo> childBindersList = ti.getChildBindersList();
			if ((null != childBindersList) && (0 < childBindersList.size())) {
				// ...scan them...
				for (TreeInfo childTI: childBindersList) {
					// ...and if one of them references the activity stream
					// ...in question...
					TreeInfo reply = findFirstActivityStreamTI(childTI, as);
					if (null != reply) {
						// ...return it.
						return reply;
					}
				}
			}
		}

		// If we get here, the activity stream was nowhere to be found
		// in the TreeInfo.  Return null.
		return null;
	}
	
	/**
	 * Returns the TreeInfo from another TreeInfo that references a
	 * collection type.
	 * 
	 * @param ti
	 * @param ct
	 * 
	 * @return
	 */
	public static TreeInfo findCollectionTI(TreeInfo ti, CollectionType ct) {
		// If the TreeInfo is in activity stream mode...
		if (ti.isActivityStream()) {
			// ...we can never find the binder in question.
			return null;
		}
		
		// If this TreeInfo is for the binder in question...
		if (ti.isBinderCollection() && ct.equals(ti.getBinderInfo().getCollectionType())) {
			// ...return it.
			return ti;
		}

		// Otherwise, search the TreeInfo's collections list.
		return findCollectionTI(ti.getCollectionsList(), ct);
	}
	
	/**
	 * Returns the TreeInfo from a List<TreeInfo> that references a
	 * collection type.
	 * 
	 * @param collectionsList
	 * @param ct
	 * 
	 * @return
	 */
	public static TreeInfo findCollectionTI(List<TreeInfo> collectionsList, CollectionType ct) {
		// Do we have a collections list to search?
		if ((null != collectionsList) && (0 < collectionsList.size())) {
			// Yes!  Scan them...
			for (TreeInfo collectionTI: collectionsList) {
				// ...and if one of them references the collection in
				// ...question...
				TreeInfo reply = findCollectionTI(collectionTI, ct);
				if (null != reply) {
					// ...return it.
					return reply;
				}
			}
		}
		
		// If we get here, we couldn't find the collection in question.
		// Return null.
		return null;
	}

	/**
	 * Returns the TreeInfo from another TreeInfo that references a
	 * specific Binder ID.
	 * 
	 * @param ti
	 * @param binderId
	 * 
	 * @return
	 */
	public static TreeInfo findBinderTI(TreeInfo ti, String binderId) {
		// If the TreeInfo is in activity stream mode...
		if (ti.isActivityStream()) {
			// ...we can never find the binder in question.
			return null;
		}
		
		// If this TreeInfo is for the binder in question...
		if (ti.getBinderInfo().getBinderId().equals(binderId)) {
			// ...return it.
			return ti;
		}

		// Otherwise, if the TreeInfo has child Binder's...
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if ((null != childBindersList) && (0 < childBindersList.size())) {
			// ...scan them...
			for (TreeInfo childTI: childBindersList) {
				// ...and if one of them references the Binder ID in
				// ...question...
				TreeInfo reply = findBinderTI(childTI, binderId);
				if (null != reply) {
					// ...return it.
					return reply;
				}
			}
		}

		// If we get here, the binder ID was nowhere to be found in the
		// TreeInfo.  Return null.
		return null;
	}

	/**
	 * Returns the TreeInfo of the first trash binder found in another
	 * TreeInfo.
	 * 
	 * @param ti
	 * 
	 * @return
	 */
	public static TreeInfo findBinderTrash(TreeInfo ti) {
		// If the TreeInfo is in activity stream mode...
		if (ti.isActivityStream()) {
			// ...we can never find the binder trash in question.
			return null;
		}
		
		// If this TreeInfo is a trash binder...
		if (ti.isBinderTrash()) {
			// ...return it.
			return ti;
		}
		
		// Otherwise, if the TreeInfo has child Binder's...
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if ((null != childBindersList) && (0 < childBindersList.size())) {
			// ...scan them...
			for (TreeInfo childTI: childBindersList) {
				// ...and if one of them is a trash binder...
				TreeInfo reply = findBinderTrash(childTI);
				if (null != reply) {
					// ...return it.
					return reply;
				}
			}
		}
		
		// If we get here, we could not find a trash binder in the
		// TreeInfo.  Return null.
		return null;
	}

	/**
	 * Returns the teaming event associated with the activity stream
	 * associated with this TreeInfo object.
	 *  
	 * @return
	 */
	public TeamingEvents getActivityStreamEvent() {
		TeamingEvents reply;
		
		if (isActivityStream())
			 reply = m_activityStreamEvent;
		else reply = TeamingEvents.UNDEFINED;
		
		return reply;
	}
	
	/**
	 * Returns the ActivityStreamInfo object associated with this
	 * TreeInfo object.
	 *  
	 * @return
	 */
	public ActivityStreamInfo getActivityStreamInfo() {
		ActivityStreamInfo reply;
		
		if (isActivityStream())
			 reply = m_activityStreamInfo;
		else reply = null;
		
		return reply;
	}
	
	/**
	 * Returns the number of children in the Binder corresponding to
	 * this TreeInfo object.
	 * 
	 * @return
	 */
	public int getBinderChildren() {
		return m_binderChildren;
	}

	/**
	 * Returns the number of collections in the Binder corresponding to
	 * this TreeInfo object.
	 * 
	 * @return
	 */
	public int getBinderCollections() {
		return m_binderCollections;
	}

	/**
	 * Returns the hover text, if any of the Binder corresponding to
	 * this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderHover() {
	     return m_binderHover;
	}

	/**
	 * Returns the string to use for the hover text over the binder's
	 * image.
	 * 
	 * @return
	 */
	public String getBinderHoverImage() {
		// Do we have the hover text for the binder image yet?
		if (!(GwtClientHelper.hasString(m_binderHoverImage))) {
			// Yes!  Is this tree info for a bucket?
			GwtTeamingMessages messages = GwtTeaming.getMessages();
			if (isBucket()) {
				// Yes!  Use the specific text for that.
				m_binderHoverImage = messages.hoverBucket();
			}
			
			else {
				// No, this tree info is not for a bucket!  Use the
				// appropriate text for the binder type.
				switch (m_binderInfo.getBinderType()) {
				case FOLDER:
					switch (m_binderInfo.getFolderType()) {
					default:
					case OTHER:        m_binderHoverImage = messages.hoverFolder();              break;
					case BLOG:         m_binderHoverImage = messages.hoverFolderBlog();          break;
					case CALENDAR:     m_binderHoverImage = messages.hoverFolderCalendar();      break;
					case DISCUSSION:   m_binderHoverImage = messages.hoverFolderDiscussion();    break;
					case FILE:         m_binderHoverImage = messages.hoverFolderFile();          break;
					case GUESTBOOK:    m_binderHoverImage = messages.hoverFolderGuestbook();     break;
					case MILESTONE:    m_binderHoverImage = messages.hoverFolderMilestones();    break;
					case MINIBLOG:     m_binderHoverImage = messages.hoverFolderMiniBlog();      break;
					case MIRROREDFILE: m_binderHoverImage = messages.hoverFolderMirroredFiles(); break;
					case PHOTOALBUM:   m_binderHoverImage = messages.hoverFolderPhotoAlbum();    break;
					case SURVEY:       m_binderHoverImage = messages.hoverFolderSurvey();        break;
					case TASK:         m_binderHoverImage = messages.hoverFolderTask();          break;
					case TRASH:        m_binderHoverImage = messages.hoverFolderTrash();         break;
					case WIKI:         m_binderHoverImage = messages.hoverFolderWiki();          break;
					}
					
					break;
					
				case WORKSPACE:
					switch (m_binderInfo.getWorkspaceType()) {
					default:
					case OTHER:                    m_binderHoverImage = messages.hoverWorkspace();                  break;
					case DISCUSSIONS:              m_binderHoverImage = messages.hoverWorkspaceDiscussions();       break;
					case GLOBAL_ROOT:              m_binderHoverImage = messages.hoverWorkspaceGlobalRoot();        break;
					case LANDING_PAGE:             m_binderHoverImage = messages.hoverWorkspaceLandingPage();       break;
					case PROFILE_ROOT:             m_binderHoverImage = messages.hoverWorkspaceProfileRoot();       break;
					case PROFILE_ROOT_MANAGEMENT:  m_binderHoverImage = messages.hoverWorkspaceProfileRoot();       break;
					case PROJECT_MANAGEMENT:       m_binderHoverImage = messages.hoverWorkspaceProjectManagement(); break;
					case TEAM:                     m_binderHoverImage = messages.hoverWorkspaceTeam();              break;
					case TEAM_ROOT:                m_binderHoverImage = messages.hoverWorkspaceTeamRoot();          break;
					case TOP:                      m_binderHoverImage = messages.hoverWorkspaceTop();               break;
					case TRASH:                    m_binderHoverImage = messages.hoverWorkspaceTrash();             break;
					case USER:                     m_binderHoverImage = messages.hoverWorkspacePersonal();          break;
					case WORKSPACE:                m_binderHoverImage = messages.hoverWorkspace();                  break;
					}
					
					break;
				}
			}
		}

		// If we get here, m_binderHoverImage refers to the appropriate
		// text for this tree info's image.  Return it.
		return m_binderHoverImage;
	}

	/**
	 * Returns the name of the icons for the Binder corresponding to
	 * this TreeInfo.
	 *
	 * @param iconSize
	 * 
	 * @return
	 */
	public String getBinderIcon(BinderIconSize iconSize) {
		return m_binderIcons.getBinderIcon(iconSize);
	}

	/**
	 * Returns the height to display the binder image for a given
	 * TreeInfo.
	 * 
	 * @param iconSize
	 * 
	 * @return
	 */
	public int getBinderIconHeight(BinderIconSize iconSize) {
		int reply;
		if (isActivityStream())
		     reply = BinderIconSize.AS_BINDER_HEIGHT_INT;
		else reply = iconSize.getBinderIconHeight();
		return reply;
	}
	
	/**
	 * Returns the width to display the binder image for a given
	 * TreeInfo.
	 * 
	 * @param iconSize
	 * 
	 * @return
	 */
	public int getBinderIconWidth(BinderIconSize iconSize) {
		int reply;
		if (isActivityStream())
		     reply  = BinderIconSize.AS_BINDER_WIDTH_INT;
		else reply = iconSize.getBinderIconWidth();
		return reply;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder.
	 *
	 * @param iconSize
	 * 
	 * @return
	 */
	public ImageResource getBinderImage(BinderIconSize iconSize) {
		ImageResource reply;
		switch (iconSize) {
		default:
		case SMALL:   reply = getBinderImageSmall();  break;
		case MEDIUM:  reply = getBinderImageMedium(); break;
		case LARGE:   reply = getBinderImageLarge();  break;
		}
		return reply;
	}
	
	/*
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder when a small image is requested.
	 */
	private ImageResource getBinderImageSmall() {
		ImageResource reply = null;
		GwtTeamingFilrImageBundle			filrImages   = GwtTeaming.getFilrImageBundle();
		GwtTeamingWorkspaceTreeImageBundle	wsTreeImages = GwtTeaming.getWorkspaceTreeImageBundle();
		if (isBucket()) {
			reply = wsTreeImages.bucket();
		}
		
		else {
			switch (m_binderInfo.getBinderType()) {
			case COLLECTION:
				switch (m_binderInfo.getCollectionType()) {
				case MY_FILES:        reply = filrImages.myFiles();      break;
				case NET_FOLDERS:     reply = filrImages.netFolder();   break;
				case SHARED_BY_ME:    reply = filrImages.sharedByMe();   break;
				case SHARED_WITH_ME:  reply = filrImages.sharedWithMe(); break;
				case SHARED_PUBLIC:   reply = filrImages.sharedPublic(); break;
				}
				
				if (null == reply) {
					reply = null;
				}
				
				break;
				
			case FOLDER:
				if (m_binderInfo.isFolderHome()) {
					reply = filrImages.folderHome();
				}
				
				else if (m_binderInfo.isFolderMyFilesStorage()) {
					reply = filrImages.myFilesStorage();
				}
				
				else {
					switch (m_binderInfo.getFolderType()) {
					case BLOG:         reply = wsTreeImages.folder_comment();   break;
					case CALENDAR:     reply = wsTreeImages.folder_calendar();  break;
					case DISCUSSION:   reply = wsTreeImages.folder_comment();   break;
					case FILE:         reply = wsTreeImages.folder_file();      break;
					case GUESTBOOK:    reply = wsTreeImages.folder_guestbook(); break;
					case MILESTONE:    reply = wsTreeImages.folder_milestone(); break;
					case MINIBLOG:     reply = wsTreeImages.folder_comment();   break;
					case MIRROREDFILE: reply = wsTreeImages.folder_file();      break;
					case PHOTOALBUM:   reply = wsTreeImages.folder_photo();     break;
					case SURVEY:       reply = wsTreeImages.folder_survey();    break;
					case TASK:         reply = wsTreeImages.folder_task();      break;
					case TRASH:        reply = wsTreeImages.folder_trash();     break;
					case WIKI:         reply = wsTreeImages.folder_wiki();      break;
					case OTHER:                                                 break;
					}
				
					if (null == reply) {
						reply = wsTreeImages.folder_generic();
					}
				}
				
				break;
				
			case WORKSPACE:
				switch (m_binderInfo.getWorkspaceType()) {
				case DISCUSSIONS:              reply = wsTreeImages.workspace_discussions();        break;
				case GLOBAL_ROOT:              reply = wsTreeImages.workspace_global_root();        break;
				case LANDING_PAGE:             reply = wsTreeImages.workspace_landing_page();       break;
				case PROFILE_ROOT:             reply = wsTreeImages.workspace_profile_root();       break;
				case PROFILE_ROOT_MANAGEMENT:  reply = wsTreeImages.workspace_profile_root();       break;
				case PROJECT_MANAGEMENT:       reply = wsTreeImages.workspace_project_management(); break;
				case TEAM:                     reply = wsTreeImages.workspace_team();               break;
				case TEAM_ROOT:                reply = wsTreeImages.workspace_team_root();          break;
				case TOP:                      reply = wsTreeImages.workspace_top();                break;
				case TRASH:                    reply = wsTreeImages.workspace_trash();              break;
				case USER:                     reply = wsTreeImages.workspace_personal();           break;
				case OTHER:                                                                         break;
				}
				
				if (null == reply) {
					reply = wsTreeImages.workspace_generic();
				}
				
				break;
			}
		}
		
		return reply;
	}
	
	/*
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder when a medium image is requested.
	 */
	private ImageResource getBinderImageMedium() {
		ImageResource reply = null;
		GwtTeamingFilrImageBundle			filrImages   = GwtTeaming.getFilrImageBundle();
		GwtTeamingWorkspaceTreeImageBundle	wsTreeImages = GwtTeaming.getWorkspaceTreeImageBundle();
		if (isBucket()) {
			reply = wsTreeImages.bucket();
		}
		
		else {
			switch (m_binderInfo.getBinderType()) {
			case COLLECTION:
				switch (m_binderInfo.getCollectionType()) {
				case MY_FILES:        reply = filrImages.myFiles_medium();      break;
				case NET_FOLDERS:     reply = filrImages.netFolder_medium();    break;
				case SHARED_BY_ME:    reply = filrImages.sharedByMe_medium();   break;
				case SHARED_WITH_ME:  reply = filrImages.sharedWithMe_medium(); break;
				case SHARED_PUBLIC:   reply = filrImages.sharedPublic_medium(); break;
				}
				
				if (null == reply) {
					reply = null;
				}
				
				break;
				
			case FOLDER:
				if (m_binderInfo.isFolderHome()) {
					reply = filrImages.folderHome_medium();
				}

				else if (m_binderInfo.isFolderMyFilesStorage()) {
					reply = filrImages.myFilesStorage_medium();
				}

				else {
					switch (m_binderInfo.getFolderType()) {
					case BLOG:         reply = wsTreeImages.folder_comment_medium();   break;
					case CALENDAR:     reply = wsTreeImages.folder_calendar_medium();  break;
					case DISCUSSION:   reply = wsTreeImages.folder_comment_medium();   break;
					case FILE:         reply = wsTreeImages.folder_file_medium();      break;
					case GUESTBOOK:    reply = wsTreeImages.folder_guestbook_medium(); break;
					case MILESTONE:    reply = wsTreeImages.folder_milestone_medium(); break;
					case MINIBLOG:     reply = wsTreeImages.folder_comment_medium();   break;
					case MIRROREDFILE: reply = wsTreeImages.folder_file_medium();      break;
					case PHOTOALBUM:   reply = wsTreeImages.folder_photo_medium();     break;
					case SURVEY:       reply = wsTreeImages.folder_survey_medium();    break;
					case TASK:         reply = wsTreeImages.folder_task_medium();      break;
					case TRASH:        reply = wsTreeImages.folder_trash_medium();     break;
					case WIKI:         reply = wsTreeImages.folder_wiki_medium();      break;
					case OTHER:                                                        break;
					}
				
					if (null == reply) {
						reply = wsTreeImages.folder_generic_medium();
					}
				}
				
				break;
				
			case WORKSPACE:
				switch (m_binderInfo.getWorkspaceType()) {
				case DISCUSSIONS:              reply = wsTreeImages.workspace_discussions_medium();        break;
				case GLOBAL_ROOT:              reply = wsTreeImages.workspace_global_root_medium();        break;
				case LANDING_PAGE:             reply = wsTreeImages.workspace_landing_page_medium();       break;
				case PROFILE_ROOT:             reply = wsTreeImages.workspace_profile_root_medium();       break;
				case PROFILE_ROOT_MANAGEMENT:  reply = wsTreeImages.workspace_profile_root_medium();       break;
				case PROJECT_MANAGEMENT:       reply = wsTreeImages.workspace_project_management_medium(); break;
				case TEAM:                     reply = wsTreeImages.workspace_team_medium();               break;
				case TEAM_ROOT:                reply = wsTreeImages.workspace_team_root_medium();          break;
				case TOP:                      reply = wsTreeImages.workspace_top_medium();                break;
				case TRASH:                    reply = wsTreeImages.workspace_trash_medium();              break;
				case USER:                     reply = wsTreeImages.workspace_personal_medium();           break;
				case OTHER:                                                                                break;
				}
				
				if (null == reply) {
					reply = wsTreeImages.workspace_generic_medium();
				}
				
				break;
			}
		}
		
		return reply;
	}
	
	/*
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder when a large image is requested.
	 */
	private ImageResource getBinderImageLarge() {
		ImageResource reply = null;
		GwtTeamingFilrImageBundle			filrImages   = GwtTeaming.getFilrImageBundle();
		GwtTeamingWorkspaceTreeImageBundle	wsTreeImages = GwtTeaming.getWorkspaceTreeImageBundle();
		if (isBucket()) {
			reply = wsTreeImages.bucket();
		}
		
		else {
			switch (m_binderInfo.getBinderType()) {
			case COLLECTION:
				switch (m_binderInfo.getCollectionType()) {
				case MY_FILES:        reply = filrImages.myFiles_large();      break;
				case NET_FOLDERS:     reply = filrImages.netFolder_large();    break;
				case SHARED_BY_ME:    reply = filrImages.sharedByMe_large();   break;
				case SHARED_WITH_ME:  reply = filrImages.sharedWithMe_large(); break;
				case SHARED_PUBLIC:   reply = filrImages.sharedPublic_large(); break;
				}
				
				if (null == reply) {
					reply = null;
				}
				
				break;
				
			case FOLDER:
				if (m_binderInfo.isFolderHome()) {
					reply = filrImages.folderHome_large();
				}

				else if (m_binderInfo.isFolderMyFilesStorage()) {
					reply = filrImages.myFilesStorage_large();
				}

				else {
					switch (m_binderInfo.getFolderType()) {
					case BLOG:         reply = wsTreeImages.folder_comment_large();   break;
					case CALENDAR:     reply = wsTreeImages.folder_calendar_large();  break;
					case DISCUSSION:   reply = wsTreeImages.folder_comment_large();   break;
					case FILE:         reply = wsTreeImages.folder_file_large();      break;
					case GUESTBOOK:    reply = wsTreeImages.folder_guestbook_large(); break;
					case MILESTONE:    reply = wsTreeImages.folder_milestone_large(); break;
					case MINIBLOG:     reply = wsTreeImages.folder_comment_large();   break;
					case MIRROREDFILE: reply = wsTreeImages.folder_file_large();      break;
					case PHOTOALBUM:   reply = wsTreeImages.folder_photo_large();     break;
					case SURVEY:       reply = wsTreeImages.folder_survey_large();    break;
					case TASK:         reply = wsTreeImages.folder_task_large();      break;
					case TRASH:        reply = wsTreeImages.folder_trash_large();     break;
					case WIKI:         reply = wsTreeImages.folder_wiki_large();      break;
					case OTHER:                                                       break;
					}
				
					if (null == reply) {
						reply = wsTreeImages.folder_generic_large();
					}
				}
				
				break;
				
			case WORKSPACE:
				switch (m_binderInfo.getWorkspaceType()) {
				case DISCUSSIONS:              reply = wsTreeImages.workspace_discussions_large();        break;
				case GLOBAL_ROOT:              reply = wsTreeImages.workspace_global_root_large();        break;
				case LANDING_PAGE:             reply = wsTreeImages.workspace_landing_page_large();       break;
				case PROFILE_ROOT:             reply = wsTreeImages.workspace_profile_root_large();       break;
				case PROFILE_ROOT_MANAGEMENT:  reply = wsTreeImages.workspace_profile_root_large();       break;
				case PROJECT_MANAGEMENT:       reply = wsTreeImages.workspace_project_management_large(); break;
				case TEAM:                     reply = wsTreeImages.workspace_team_large();               break;
				case TEAM_ROOT:                reply = wsTreeImages.workspace_team_root_large();          break;
				case TOP:                      reply = wsTreeImages.workspace_top_large();                break;
				case TRASH:                    reply = wsTreeImages.workspace_trash_large();              break;
				case USER:                     reply = wsTreeImages.workspace_personal_large();           break;
				case OTHER:                                                                               break;
				}
				
				if (null == reply) {
					reply = wsTreeImages.workspace_generic_large();
				}
				
				break;
			}
		}
		
		return reply;
	}
	
	/**
	 * Returns the BinderInfo this TreeInfo object refers to.
	 * 
	 * @return
	 */
	public BinderInfo getBinderInfo() {
		return m_binderInfo;
	}
	
	/**
	 * Returns the permalink to the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderPermalink() {
		return m_binderPermalink;
	}

	/**
	 * Returns the title of the Binder corresponding to this TreeInfo
	 * object.
	 * 
	 * @return
	 */
	public String getBinderTitle() {
		return m_binderTitle;
	}

	/**
	 * Returns the permalink to the trash for the Binder corresponding
	 * to this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderTrashPermalink() {
		return m_binderTrashPermalink;
	}

	/**
	 * Returns this TreeInfo's UI image.
	 * 
	 * @return
	 */
	public Object getBinderUIImage() {
		return m_binderUIImg;
	}
	
	/**
	 * Returns the BucketInfo of this bucket.
	 * 
	 * @return
	 */
	public BucketInfo getBucketInfo() {
		return m_bucketInfo;
	}
	
	/*
	 * Returns the part of a title to be used for constructing the
	 * display name for a bucket.
	 */
	private static String getBucketTitlePart(String title) {
		// If we don't have a name to get the display part from...
		String reply;
		if (null != title) {
			title = title.trim();
		}
		if (!(GwtClientHelper.hasString(title))) {
			// ...return what we were given.
			reply = title;
		}
		else {
			// Otherwise, look for places to split the name.
			int comma = title.indexOf(',');
			int dot   = title.indexOf('.');
			int nerp  = title.indexOf(')');
			int space = title.indexOf(' ');
			int split = Integer.MAX_VALUE;
			if  ((-1) < comma)                     split = comma;
			if (((-1) < dot)   && (dot   < split)) split = dot;
			if (((-1) < nerp)  && (nerp  < split)) split = nerp;
			if (((-1) < space) && (space < split)) split = space;

			// Can we split the name?
			if (Integer.MAX_VALUE == split) {
				// No!  Return the entire thing.
				reply = title;
			}
			else {
				// Yes!  Split it, maintaining the ')' if that's what
				// we're splitting on.
				reply = title.substring(0, ((split == nerp) ? (split + 1) : split));
			}
		}
		
		// If we get here, reply refers to the part of the name to
		// display.  Return it.
		return reply;
	}
	
	/**
	 * Returns the List<TreeInfo> of the Binder's contained in the
	 * Binder corresponding to this TreeInfo.
	 * 
	 * @return
	 */
	public List<TreeInfo> getChildBindersList() {
		return m_childBindersList;
	}
	
	/**
	 * Returns the List<TreeInfo> of the collection point Binder's.
	 * 
	 * @return
	 */
	public List<TreeInfo> getCollectionsList() {
		return m_collectionsList;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display for the
	 * expander next to the Binder.  If no expander should be shown,
	 * null is returned.
	 * 
	 * @return
	 */
	public ImageResource getExpanderImage() {
		ImageResource reply = null;
		
		if (isBucket() || (0 < getBinderChildren())) {
			GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
			if (isBinderExpanded())
			     reply = images.tree_closer();
			else reply = images.tree_opener();
		}

		return reply;
	}

	/**
	 * Returns the grid object this TreeInfo was rendered in.
	 * 
	 * Note:  This is client side only data!
	 * 
	 * @return
	 */
	public Object getRenderedGrid() {
		return m_renderedGrid;
	}

	/**
	 * Returns the row within the grid this TreeInfo was rendered in.
	 * 
	 * Note:  This is client side only data!
	 * 
	 * @return
	 */
	public int getRenderedGridRow() {
		return m_renderedGridRow;
	}
	
	/**
	 * Returns the initial part to construct a bucket title with.
	 * 
	 * @return
	 */
	public String getPreBucketTitle() {
		return getBucketTitlePart(getBucketInfo().getBucketTuple1());
	}
	
	/**
	 * Returns the final part to construct a bucket title with.
	 * 
	 * @return
	 */
	public String getPostBucketTitle() {
		return getBucketTitlePart(getBucketInfo().getBucketTuple2());
	}

	/**
	 * Returns true if this TreeInfo object represents an entity in
	 * an activity Stream and false otherwise.
	 * 
	 * @return
	 */
	public boolean isActivityStream() {
		return m_activityStream;
	}
	
	/**
	 * Returns true if the Binder is supposed to display a border along
	 * its top edge and false otherwise..
	 * 
	 * @return
	 */
	public boolean isBinderBorderTop() {
		return m_binderBorderTop;
	}

	/**
	 * Returns true if this TreeInfo represents a collection and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderCollection() {
		return ((null != m_binderInfo) && m_binderInfo.isBinderCollection());
	}

	/**
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be expanded and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderExpanded() {
		return m_binderExpanded;
	}

	/**
	 * Returns true if this TreeInfo represents a trash view and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderTrash() {
		return ((null != m_binderInfo) && m_binderInfo.isBinderTrash());
	}

	/**
	 * Returns true if this TreeInfo corresponds to a bucket list
	 * or false otherwise.
	 * 
	 * @return
	 */
	public boolean isBucket() {
		return (null != m_bucketInfo);
	}

	/**
	 * Returns true if this TreeInfo is a 'root tail' node and false
	 * otherwise.
	 * 
	 * See the comments by the definition of m_rootTail for how this is
	 * used.
	 * 
	 * @return
	 */
	public boolean isRootTail() {
		return m_rootTail;
	}
	
	/**
	 * Stores whether this TreeInfo object represents an Activity
	 * Stream or not.
	 * 
	 * @param activityStream
	 */
	public void setActivityStream(boolean activityStream) {
		m_activityStream = activityStream;
	}

	/**
	 * Stores a teaming event for an activity stream stored in the
	 * TreeInfo object.
	 * 
	 * @param te
	 */
	public void setActivityStreamEvent(TeamingEvents te, ActivityStreamInfo asi) {
		if (isActivityStream()) {
			m_activityStreamEvent = te;
			setActivityStreamInfo(asi);
		}
	}
	
	/**
	 * Stores an ActivityStreamInfo object in the TreeInfo object.
	 * 
	 * @param asi
	 */
	public void setActivityStreamInfo(ActivityStreamInfo asi) {
		if (isActivityStream()) {
			m_activityStreamInfo = asi;
		}
	}
	
	/**
	 * Store a count of the children of a Binder.
	 * 
	 * @param binderChildren
	 */
	public void setBinderChildren(int binderChildren) {
		m_binderChildren = binderChildren;
	}
	
	/**
	 * Store a count of the collections of a Binder.
	 * 
	 * @param binderCollections
	 */
	public void setBinderCollections(int binderCollections) {
		m_binderCollections = binderCollections;
	}
	
	/**
	 * Stores a Binder's hover text in this TreeInfo object.
	 * 
	 * @param binderHover
	 */
	public void setBinderHover(String binderHover) {
		m_binderHover = binderHover;
	}

	/**
	 * Stores the string to use for the hover text for a binder's
	 * image.
	 * 
	 * @param binderHoverImage
	 */
	public void setBinderHoverImage(String binderHoverImage) {
		m_binderHoverImage = binderHoverImage;
	}
	
	/**
	 * Stores the names of the icons for the Binder.
	 * 
	 * @param binderIcon
	 * @param iconSize
	 */
	public void setBinderIcon(String binderIcon, BinderIconSize iconSize) {
		m_binderIcons.setBinderIcon(binderIcon, iconSize);
	}

	/**
	 * Stores whether the Binder should display a border along its top
	 * edge.
	 * 
	 * @param binderBorderTop
	 */
	public void setBinderBorderTop(boolean binderBorderTop) {
		m_binderBorderTop = binderBorderTop;
	}

	/**
	 * Stores whether the Binder should be expanded.
	 * 
	 * @param binderExpanded
	 */
	public void setBinderExpanded(boolean binderExpanded) {
		m_binderExpanded = binderExpanded;
	}

	/**
	 * Stores a Binder's permalink in this TreeInfo object.
	 * 
	 * @param binderPermalink
	 */
	public void setBinderPermalink(String binderPermalink) {
		m_binderPermalink = binderPermalink;
	}

	/**
	 * Stores a Binder's title in this TreeInfo object.
	 * 
	 * @param binderTitle
	 */
	public void setBinderTitle(String binderTitle) {
		m_binderTitle = binderTitle;
	}

	/**
	 * Stores a Binder's trash permalink in this TreeInfo object.
	 * 
	 * @param binderTrashPermalink
	 */
	public void setBinderTrashPermalink(String binderTrashPermalink) {
		m_binderTrashPermalink = binderTrashPermalink;
	}

	/**
	 * Stores the BinderInfo about the Binder referenced by this
	 * TreeInfo object.
	 * 
	 * @param binderInfo
	 */
	public void setBinderInfo(BinderInfo binderInfo) {
		m_binderInfo = binderInfo;
	}

	/**
	 * Stores the TreeInfo's UI image.
	 * 
	 * @param binderUIImg
	 */
	public void setBinderUIImage(Object binderUIImg) {
		m_binderUIImg = binderUIImg;
	}
	
	/**
	 * Stores information about a bucket of Binders.
	 * 
	 * @param bucketInfo
	 */
	public void setBucketInfo(BucketInfo bucketInfo) {
		m_bucketInfo = bucketInfo;
	}
	
	/**
	 * Stores a List<TreeInfo> of the Binder's contained in the BInder
	 * corresponding to this TreeInfo.
	 * 
	 * @param childBindersList
	 */
	public void setChildBindersList(List<TreeInfo> childBindersList) {
		m_childBindersList = childBindersList;
		updateChildBindersCount();
	}

	/**
	 * Stores a List<TreeInfo> of the collection Binder's contained in
	 * this TreeInfo.
	 * 
	 * @param collectionsAL
	 */
	public void setCollectionsList(List<TreeInfo> collectionsAL) {
		m_collectionsList = collectionsAL;
		updateCollectionsCount();
	}

	/**
	 * Stores where a TreeInfo is rendered.
	 *
	 * Note:  This is client side only data!
	 * 
	 * @param grid
	 * @param gridRow
	 */
	public void setRenderedGrid(Object renderedGrid, int renderedGridRow) {
		m_renderedGrid    = renderedGrid;
		m_renderedGridRow = renderedGridRow;
	}
	
	/**
	 * Stores whether a TreeInfo is a root tail or not.
	 * 
	 * See the comments by the definition of m_rootTail for how this is
	 * used.
	 * 
	 * @param rootTail
	 */
	public void setRootTail(boolean rootTail) {
		m_rootTail = rootTail;
	}
	
	/**
	 * Updates the m_binderChildren data member based on what's
	 * currently contained in the m_childBindersList ArrayList.
	 */
	public void updateChildBindersCount() {
		m_binderChildren = ((null == m_childBindersList) ? 0 : m_childBindersList.size());
	}
	
	/**
	 * Updates the m_binderCollections data member based on what's
	 * currently contained in the m_collectionsList ArrayList.
	 */
	public void updateCollectionsCount() {
		m_binderCollections = ((null == m_collectionsList) ? 0 : m_collectionsList.size());
	}
}
