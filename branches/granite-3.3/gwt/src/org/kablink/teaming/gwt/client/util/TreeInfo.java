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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate workspace tree information between the
 * client (i.e., the WorkspaceTreeControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTreeInfo().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeInfo implements IsSerializable, VibeRpcResponseData {
	private List<TreeInfo>	m_childBindersAL = new ArrayList<TreeInfo>();
	private BinderInfo		m_binderInfo     = new BinderInfo();
	private boolean			m_binderExpanded;
	private int				m_binderChildren;
	private String			m_binderHover = "";
	private String			m_binderHoverImage;
	private String			m_binderIconName;
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

	// Used on the client side only by the sidebar tree to cache the
	// Image object used for the binder.  It uses this to hold a
	// Binder's original image while it displays a spinning wheel
	// during a context load.
	private transient Object m_binderUIImg;
		
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TreeInfo() {
		// Nothing to do.
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
		reply.setActivityStream(      isActivityStream()                               );
		reply.setActivityStreamEvent( getActivityStreamEvent(), getActivityStreamInfo());
		reply.setBinderExpanded(      isBinderExpanded()                               );
		reply.setBinderHover(         getBinderHover()                                 );
		reply.setBinderIconName(      getBinderIconName()                              );
		reply.setBinderInfo(          getBinderInfo().copyBinderInfo()                 );
		reply.setBinderPermalink(     getBinderPermalink()                             );
		reply.setBinderTitle(         getBinderTitle()                                 );
		reply.setBinderTrashPermalink(getBinderTrashPermalink()                        );
		
		// ...store an empty child Binder's List<TreeInfo>...
		reply.setChildBindersList(new ArrayList<TreeInfo>());

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
		// If the TreeInfo is not in activity stream mode...
		if (!(ti.isActivityStream())) {
			// ...we can never find the activity stream in question.
			return null;
		}
		
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
		if (ti.getBinderInfo().isBinderTrash()) {
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
					case OTHER:               m_binderHoverImage = messages.hoverWorkspace();                  break;
					case DISCUSSIONS:         m_binderHoverImage = messages.hoverWorkspaceDiscussions();       break;
					case GLOBAL_ROOT:         m_binderHoverImage = messages.hoverWorkspaceGlobalRoot();        break;
					case LANDING_PAGE:        m_binderHoverImage = messages.hoverWorkspaceLandingPage();       break;
					case PROFILE_ROOT:        m_binderHoverImage = messages.hoverWorkspaceProfileRoot();       break;
					case PROJECT_MANAGEMENT:  m_binderHoverImage = messages.hoverWorkspaceProjectManagement(); break;
					case TEAM:                m_binderHoverImage = messages.hoverWorkspaceTeam();              break;
					case TEAM_ROOT:           m_binderHoverImage = messages.hoverWorkspaceTeamRoot();          break;
					case TOP:                 m_binderHoverImage = messages.hoverWorkspaceTop();               break;
					case TRASH:               m_binderHoverImage = messages.hoverWorkspaceTrash();             break;
					case USER:                m_binderHoverImage = messages.hoverWorkspacePersonal();          break;
					case WORKSPACE:           m_binderHoverImage = messages.hoverWorkspace();                  break;
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
	 * Returns the name of the Binder icon for the Binder corresponding
	 * to this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderIconName() {
		return m_binderIconName;
	}

	/**
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder.
	 * 
	 * @return
	 */
	public ImageResource getBinderImage() {
		ImageResource reply = null;
		GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
		if (isBucket()) {
			reply = images.bucket();
		}
		
		else {
			switch (m_binderInfo.getBinderType()) {
			case FOLDER:
				switch (m_binderInfo.getFolderType()) {
				case BLOG:         reply = images.folder_comment();   break;
				case CALENDAR:     reply = images.folder_calendar();  break;
				case DISCUSSION:   reply = images.folder_comment();   break;
				case FILE:         reply = images.folder_file();      break;
				case GUESTBOOK:    reply = images.folder_guestbook(); break;
				case MILESTONE:    reply = images.folder_milestone(); break;
				case MINIBLOG:     reply = images.folder_comment();   break;
				case MIRROREDFILE: reply = images.folder_file();      break;
				case PHOTOALBUM:   reply = images.folder_photo();     break;
				case SURVEY:       reply = images.folder_survey();    break;
				case TASK:         reply = images.folder_task();      break;
				case TRASH:        reply = images.folder_trash();     break;
				case WIKI:         reply = images.folder_wiki();      break;
				case OTHER:                                           break;
				}
				
				if (null == reply) {
					reply = images.folder_generic();
				}
				
				break;
				
			case WORKSPACE:
				switch (m_binderInfo.getWorkspaceType()) {
				case DISCUSSIONS:         reply = images.workspace_discussions();        break;
				case GLOBAL_ROOT:         reply = images.workspace_global_root();        break;
				case LANDING_PAGE:        reply = images.workspace_landing_page();       break;
				case PROFILE_ROOT:        reply = images.workspace_profile_root();       break;
				case PROJECT_MANAGEMENT:  reply = images.workspace_project_management(); break;
				case TEAM:                reply = images.workspace_team();               break;
				case TEAM_ROOT:           reply = images.workspace_team_root();          break;
				case TOP:                 reply = images.workspace_top();                break;
				case TRASH:               reply = images.workspace_trash();              break;
				case USER:                reply = images.workspace_personal();           break;
				case OTHER:                                                                                                break;
				}
				
				if (null == reply) {
					reply = images.workspace_generic();
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
		return m_childBindersAL;
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
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be expanded and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderExpanded() {
		return m_binderExpanded;
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
	 * Stores the name of the icon for the Binder.
	 * 
	 * @param binderIconName
	 */
	public void setBinderIconName(String binderIconName) {
		m_binderIconName = binderIconName;
		
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
	 * Stores an ArrayList<TreeInfo> of the Binder's contained in the
	 * Binder corresponding to this TreeInfo.
	 */
	public void setChildBindersList(List<TreeInfo> childBindersList) {
		m_childBindersAL = childBindersList;
		updateChildBindersCount();
	}

	/**
	 * Updates the m_binderChildren data member based on what's
	 * currently contained in the m_childBindersAL ArrayList.
	 */
	public void updateChildBindersCount() {
		m_binderChildren = ((null == m_childBindersAL) ? 0 : m_childBindersAL.size());
	}
}
