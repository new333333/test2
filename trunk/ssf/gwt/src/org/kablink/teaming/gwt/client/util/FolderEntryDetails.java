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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate information about a folder entry through
 * GWT RPC requests.
 *  
 * @author drfoster
 */
public class FolderEntryDetails implements IsSerializable {
	private BinderIcons					m_entryIcons;			// The icons related to the entry in various sizes.
	private boolean						m_contentIsImage;		// true -> The content of the file can be viewed as an image.  false -> It can't.
	private boolean						m_descIsHtml;			// true -> The entry's description is in HTML.  false -> It's in plain text.
	private boolean						m_lockedByLoggedInUser;	// true -> The entry is locked by the logged in user.  false -> It's not locked, or locked by somebody else.
	private boolean						m_modifierIsCreator;	// true -> The creator and modifier are the same user.  false -> They're not.
	private boolean						m_seen;					// true -> The entry is  currently  marked as having been seen by the user.  false -> It isn't.
	private boolean						m_seenPrevious;			// true -> The entry was previously marked as having been seen by the user.  false -> It wasn't.
	private boolean						m_top;					// true -> The entry is a top level entry.  false -> It's a comment.
	private CommentsInfo				m_comments;				// Information about the comments on the entry.
	private EntityId					m_entityId;				// The folder entry to view.
	private int							m_contentImageHeight;	// If m_contentIsImage is true, the height   of the images.
	private int							m_contentImageRotation;	// If m_contentIsImage is true, the rotation of the images.
	private int							m_contentImageWidth;	// If m_contentIsImage is true, the width    of the images.
	private List<ShareInfo>				m_sharedByItems;		// List<ShareInfo> of who the shared this entry with the current user.
	private List<ShareInfo>				m_sharedWithItems;		// List<ShareInfo> of who the current user shared this entry with.
	private List<ToolbarItem>			m_toolbarItems;			// List<ToolbarItem> of the toolbar items for entry entry view's menu.
	private List<ViewFolderEntryInfo>	m_commentBreadCrumbs;	// List<ViewFolderEntryInfo> of the break crumb links for a comment entry.
	private String						m_desc;					// The entry description.
	private String						m_descTxt;				// Plain text version of m_desc.
	private String						m_family;				// The definition family of the folder entry.
	private String                      m_fileSizeDisplay;		// For file entries, the display string for the size of the file.
	private String						m_downloadUrl;			// The URL for downloading the file.
	private String						m_path;					// The full path to the entry.
	private String						m_title;				// The title of the folder entry.
	private String[]					m_contributors;			// The IDs of the contributors to this entry.
	private UserInfo					m_creator;				// The creator of the entry.
	private UserInfo					m_locker;				// The user that has the entry locked.  null -> The entry is not locked.
	private UserInfo					m_modifier;				// The last modifier of the entry.
	private ViewFileInfo				m_htmlView;				// If the entry is a file entry that can be viewed as HTML, contains the information for viewing it.  null otherwise.

	/**
	 * Inner class used to track information about how an entry is
	 * shared (either with the current user or by the current user.)
	 */
	public static class ShareInfo implements IsSerializable {
		private boolean			m_expired;		// true -> This share is expired.  false -> It's not.
		private AssignmentInfo	m_user;			// By:  The user that shared the item.  With:  The user the item is shared with.
		private ShareRights		m_rights;		// The rights granted as part of the share.
		private String			m_comment;		// The comment associated with the share.
		private String			m_expiresDate;	// If the share can expires, the time/date stamp when it does.
		private String			m_shareDate;	// The time/date stamp of when the share was created.
		private String			m_title;		// By:  The title of who shared the item.  With:  The title of who the item was shared with.
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		public ShareInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public AssignmentInfo getUser()        {return m_user;       }
		public boolean        isExpired()      {return m_expired;    }
		public ShareRights    getRights()      {return m_rights;     }
		public String         getComment()     {return m_comment;    }
		public String         getExpiresDate() {return m_expiresDate;}
		public String         getShareDate()   {return m_shareDate;  }
		public String         getTitle()       {return m_title;      }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setUser(       AssignmentInfo user)        {m_user        = user;       }
		public void setExpired(    boolean        expired)     {m_expired     = expired;    }
		public void setRights(     ShareRights    rights)      {m_rights      = rights;     }
		public void setComment(    String         comment)     {m_comment     = comment;    }
		public void setExpiresDate(String         expiresDate) {m_expiresDate = expiresDate;}
		public void setShareDate(  String         shareDate)   {m_shareDate   = shareDate;  }
		public void setTitle(      String         title)       {m_title       = title;      }
	}
	
	/**
	 * Inner class used to track information about who acted upon an
	 * entry. 
	 */
	public static class UserInfo implements IsSerializable {
		private PrincipalInfo	m_pi;	//
		private String			m_date;	// The date/time stamp of when the user acted upon the entry.
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		public UserInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public PrincipalInfo getPrincipalInfo() {return m_pi;  }
		public String        getDate()          {return m_date;}

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setPrincipalInfo(PrincipalInfo pi)   {m_pi   = pi;  }
		public void setDate(         String        date) {m_date = date;}
		
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public FolderEntryDetails() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything that requires it.
		m_entryIcons      = new BinderIcons();
		m_sharedByItems   = new ArrayList<ShareInfo>();
		m_sharedWithItems = new ArrayList<ShareInfo>();
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 */
	public FolderEntryDetails(EntityId entityId) {
		// Initialize this object...
		this();

		// ...and store the parameter.
		setEntityId(entityId);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entryId
	 */
	public FolderEntryDetails(Long binderId, Long entryId) {
		// Always use the alternate form of the constructor.
		this(new EntityId(binderId, entryId, EntityId.FOLDER_ENTRY));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                   isContentImage()          {return m_contentIsImage;      }
	public boolean                   isDescHtml()              {return m_descIsHtml;          }
	public boolean                   isLocked()                {return (null != m_locker);    }
	public boolean                   isLockedByLoggedInUser()  {return m_lockedByLoggedInUser;}
	public boolean                   isModifierCreator()       {return m_modifierIsCreator;   }
	public boolean                   isSeen()                  {return m_seen;                }
	public boolean                   isSeenPrevious()          {return m_seenPrevious;        }
	public boolean                   isTop()                   {return m_top;                 }
	public boolean                   isHtmlViewable()          {return (null != m_htmlView);  }
	public CommentsInfo              getComments()             {return m_comments;            }
	public EntityId                  getEntityId()             {return m_entityId;            }
	public int                       getContentImageHeight()   {return m_contentImageHeight;  }
	public int                       getContentImageRotation() {return m_contentImageRotation;}
	public int                       getContentImageWidth()    {return m_contentImageWidth;   }
	public List<ShareInfo>           getSharedByItems()        {return m_sharedByItems;       }
	public List<ShareInfo>           getSharedWithItems()      {return m_sharedWithItems;     }
	public List<ToolbarItem>         getToolbarItems()         {return m_toolbarItems;        }
	public List<ViewFolderEntryInfo> getCommentBreadCrumbs()   {return m_commentBreadCrumbs;  }
	public String                    getDesc()                 {return m_desc;                }
	public String                    getDescTxt()              {return m_descTxt;             }
	public String                    getDownloadUrl()          {return m_downloadUrl;         }
	public String                    getFamily()               {return m_family;              }
	public String                    getFileSizeDisplay()      {return m_fileSizeDisplay;     }
	public String                    getPath()                 {return m_path;                }
	public String                    getTitle()                {return m_title;               }
	public String[]                  getContributors()         {return m_contributors;        }
	public UserInfo                  getCreator()              {return m_creator;             }
	public UserInfo                  getLocker()               {return m_locker;              }
	public UserInfo                  getModifier()             {return m_modifier;            }
	public ViewFileInfo              getHtmlView()             {return m_htmlView;            }                            
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setContentIsImage(      boolean                   contentIsImage)         {m_contentIsImage       = contentIsImage;        }
	public void setDescIsHtml(          boolean                   descIsHtml)             {m_descIsHtml           = descIsHtml;            }
	public void setLockedByLoggedInUser(boolean                   lockedByLoggedInUser)   {m_lockedByLoggedInUser = lockedByLoggedInUser;  }
	public void setModifierIsCreator(   boolean                   modifierIsCreator)      {m_modifierIsCreator    = modifierIsCreator;     }
	public void setSeen(                boolean                   seen)                   {m_seen                 = seen;                  }
	public void setSeenPrevious(        boolean                   seenPrevious)           {m_seenPrevious         = seenPrevious;          }
	public void setTop(                 boolean                   top)                    {m_top                  = top;                   }
	public void setComments(            CommentsInfo              comments)               {m_comments             = comments;              }
	public void setEntityId(            EntityId                  entityId)               {m_entityId             = entityId;              }
	public void setContentImageHeight(  int                       contentImageHeight)     {m_contentImageHeight   = contentImageHeight;    }
	public void setContentImageRotation(int                       contentImageRotation)   {m_contentImageRotation = contentImageRotation;  }
	public void setContentImageWidth(   int                       contentImageWidth)      {m_contentImageWidth    = contentImageWidth;     }
	public void setSharedByItems(       List<ShareInfo>           sharedByItems)          {m_sharedByItems        = sharedByItems;         }
	public void setSharedWithItems(     List<ShareInfo>           sharedWithItems)        {m_sharedWithItems      = sharedWithItems;       }
	public void setToolbarItems(        List<ToolbarItem>         toolbarItems)           {m_toolbarItems         = toolbarItems;          }
	public void setCommentBreadCrumbs(  List<ViewFolderEntryInfo> commentBreadCrumbItems) {m_commentBreadCrumbs   = commentBreadCrumbItems;}
	public void setDesc(                String                    desc)                   {m_desc                 = desc;                  }
	public void setDescTxt(             String                    descTxt)                {m_descTxt              = descTxt;               }
	public void setDownloadUrl(         String                    downloadUrl)            {m_downloadUrl          = downloadUrl;           }
	public void setFamily(              String                    family)                 {m_family               = family;                }
	public void setFileSizeDisplay(     String                    fileSizeDisplay)        {m_fileSizeDisplay      = fileSizeDisplay;       }
	public void setPath(                String                    path)                   {m_path                 = path;                  }
	public void setTitle(               String                    title)                  {m_title                = title;                 }
	public void setContributors(        String[]                  contributors)           {m_contributors         = contributors;          }
	public void setCreator(             UserInfo                  creator)                {m_creator              = creator;               }
	public void setLocker(              UserInfo                  locker)                 {m_locker               = locker;                }
	public void setModifier(            UserInfo                  modifier)               {m_modifier             = modifier;              }
	public void setHtmlView(            ViewFileInfo              htmlView)               {m_htmlView             = htmlView;              }

	/**
	 * Add'er methods.
	 * 
	 * @param shareItem
	 */
	public void addSharedByItem(  ShareInfo shareItem) {m_sharedByItems.add(  shareItem);}
	public void addSharedWithItem(ShareInfo shareItem) {m_sharedWithItems.add(shareItem);}
	
	/**
	 * Returns the icon for the entry in a particular size, if
	 * available.
	 * 
	 * @param entityIconSize
	 * 
	 * @return
	 */
	public String getEntryIcon(BinderIconSize entityIconSize) {
		return m_entryIcons.getBinderIcon(entityIconSize);
	}

	/**
	 * Returns true if the folder entry has a description and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean hasDescripion() {
		String hDesc = (null == m_desc)    ? "" : m_desc.trim();
		String pDesc = (null == m_descTxt) ? "" : m_descTxt.trim();
		return ((0 < hDesc.length()) || (0 < pDesc.length()));
	}

	/**
	 * Returns true if there are any items in the 'Shared by'
	 * List<ShareInfo> and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasShareBys() {
		return ((null != m_sharedByItems) && (!(m_sharedByItems.isEmpty())));
	}

	/**
	 * Returns true if there are any 'Share by/with' ShareItem's or
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean hasShares() {
		return (hasShareBys() || hasShareWiths());
	}
	
	/**
	 * Returns true if there are any items in the 'Shared with'
	 * List<ShareInfo> and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasShareWiths() {
		return ((null != m_sharedWithItems) && (!(m_sharedWithItems.isEmpty())));
	}
	
	/**
	 * Returns true if the folder entry is a file entry and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isFamilyFile() {
		boolean reply = ((null != m_family) && (0 < m_family.length()));
		if (reply) {
			m_family.equals("file");
		}
		return reply;
	}
	
	/**
	 * Returns true if the modification and creation are the same and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isModifiedAtCreation() {
		return (m_modifierIsCreator && m_modifier.getDate().equals(m_creator.getDate()));
	}
	
	/**
	 * Stores an icon of a particular size for the entry.
	 * 
	 * @param entityIcon
	 * @param entityIconSize
	 */
	public void setEntryIcon(String entityIcon, BinderIconSize entityIconSize) {
		m_entryIcons.setBinderIcon(entityIcon, entityIconSize);
	}
}
