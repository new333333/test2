/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate information about a FOLDER_ENTRY view
 * through GWT RPC requests.
 *  
 * @author drfoster
 */
public class ViewFolderEntryInfo implements IsSerializable {
	private BinderIcons			m_entryIcons;			// The icons related to the entry in various sizes.
	private boolean				m_descIsHtml;			// true -> The entry's description is in HTML.  false -> It's in plain text.
	private CommentsInfo		m_comments;				// Information about the comments on the entry.
	private EntityId			m_entityId;				// The folder entry to view.
	private GwtPresenceInfo		m_creatorPresence;		// Presence information for the entry's creator.
	private int					m_x, m_y, m_cx, m_cy;	// The position and size of the dialog.  Only used when m_viewStyle is iframe.
	private List<ToolbarItem>	m_toolbarItems;			// List<ToolbarItem> of the toolbar items for entry entry view's menu.
	private String				m_creationDate;			// The date/time stamp of when the entry was created.
	private String				m_creatorAvatar;		// The entry creator's avatar.
	private String				m_creatorPresenceDude;	// The entry creator's current presence state.
	private String				m_creatorTitle;			// The entry creator's title.
	private String				m_desc;					// The entry description.
	private String				m_descTxt;				// Plain text version of m_desc.
	private String				m_family;				// The definition family of the folder entry.
	private String				m_modificationDate;		// The date/time stamp of when the entry was last modified.
	private String				m_path;					// The full path to the entry.
	private String				m_title;				// The title of the folder entry.
	private String				m_viewStyle;			// The style to run the entry viewer.

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public ViewFolderEntryInfo() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_entryIcons = new BinderIcons();
		m_x = m_y = m_cx = m_cy = (-1);	// (-1) -> Default size, centered.
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 */
	public ViewFolderEntryInfo(EntityId entityId) {
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
	public ViewFolderEntryInfo(Long binderId, Long entryId) {
		// Always use the alternate form of the constructor.
		this(new EntityId(binderId, entryId, EntityId.FOLDER_ENTRY));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean           isContentView()                             {return "newpage".equalsIgnoreCase(getViewStyle());}
	public boolean           isDescHtml()                                {return m_descIsHtml;                              }
	public boolean           isDialogView()                              {return (!(isContentView()));                      }
	public CommentsInfo      getComments()                               {return m_comments;                                }
	public EntityId          getEntityId()                               {return m_entityId;                                }
	public GwtPresenceInfo   getCreatorPresence()                        {return m_creatorPresence;                         }
	public int               getCX()                                     {return m_cx;                                      }
	public int               getCY()                                     {return m_cy;                                      }
	public int               getX()                                      {return m_x;                                       }
	public int               getY()                                      {return m_y;                                       }
	public List<ToolbarItem> getToolbarItems()                           {return m_toolbarItems;                            }
	public String            getCreationDate()                           {return m_creationDate;                            }
	public String            getCreatorAvatar()                          {return m_creatorAvatar;                           }
	public String            getCreatorPresenceDude()                    {return m_creatorPresenceDude;                     }
	public String            getCreatorTitle()                           {return m_creatorTitle;                            }
	public String            getDesc()                                   {return m_desc;                                    }
	public String            getDescTxt()                                {return m_descTxt;                                 }
	public String            getEntryIcon(BinderIconSize entityIconSize) {return m_entryIcons.getBinderIcon(entityIconSize);}
	public String            getFamily()                                 {return m_family;                                  }
	public String            getModificationDate()                       {return m_modificationDate;                        }
	public String            getPath()                                   {return m_path;                                    }
	public String            getTitle()                                  {return m_title;                                   }
	public String            getViewStyle()                              {return m_viewStyle;                               }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDescIsHtml(         boolean           descIsHtml)                                {m_descIsHtml          = descIsHtml;                    }
	public void setComments(           CommentsInfo      comments)                                  {m_comments            = comments;                      }
	public void setEntityId(           EntityId          entityId)                                  {m_entityId            = entityId;                      }
	public void setCreatorPresence(    GwtPresenceInfo   creatorPresence)                           {m_creatorPresence     = creatorPresence;               }
	public void setCX(                 int               cx)                                        {m_cx                  = cx;                            }
	public void setCY(                 int               cy)                                        {m_cy                  = cy;                            }
	public void setX(                  int               x)                                         {m_x                   = x;                             }
	public void setY(                  int               y)                                         {m_y                   = y;                             }
	public void setToolbarItems(       List<ToolbarItem> toolbarItems)                              {m_toolbarItems        = toolbarItems;                  }
	public void setCreationDate(       String            creationDate)                              {m_creationDate        = creationDate;                  }
	public void setCreatorAvatar(      String            creatorAvatar)                             {m_creatorAvatar       = creatorAvatar;                 }
	public void setCreatorPresenceDude(String            creatorPresenceDude)                       {m_creatorPresenceDude = creatorPresenceDude;           }
	public void setCreatorTitle(       String            creatorTitle)                              {m_creatorTitle        = creatorTitle;                  }
	public void setDesc(               String            desc)                                      {m_desc                = desc;                          }
	public void setDescTxt(            String            descTxt)                                   {m_descTxt             = descTxt;                       }
	public void setEntryIcon(          String            entityIcon, BinderIconSize entityIconSize) {m_entryIcons.setBinderIcon(entityIcon, entityIconSize);}
	public void setFamily(             String            family)                                    {m_family              = family;                        }
	public void setModificationDate(   String            modificationDate)                          {m_modificationDate    = modificationDate;              }
	public void setPath(               String            path)                                      {m_path                = path;                          }
	public void setTitle(              String            title)                                     {m_title               = title;                         }
	public void setViewStyle(          String            viewStyle)                                 {m_viewStyle           = viewStyle;                     }

	/**
	 * Returns true if the folder entry is a file entry and false otherwise.
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
}
