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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about a tag via a GWT RPC
 * command.
 * 
 * @author drfoster@novell.com
 */
public class TagInfo implements IsSerializable {
	private String  m_tagEntity;					//
	private String  m_tagOwnerEntity;				//
	private String  m_tagId;						//
	private String  m_tagName;						//
	private TagType m_tagType = TagType.UNKNOWN;	//
	
	// The following data members are only valid when the TagInfo object was created via
	// a request to get archive information.
	private int		m_searchResultsCnt;		//
	private int		m_searchResultsRating;	//
	private String	m_searchText;			//
	
	// The following are used as the names of the personal tags added
	// to a shared entity to indicate that the user has it hidden.
    // Must match values in ObjectKeys.java
	private final static String	HIDDEN_SHARED_BY_TAG	= "sharedByHidden";
	private final static String	HIDDEN_SHARED_WITH_TAG	= "sharedWithHidden";

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TagInfo() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isCommunityTag()            {return (TagType.COMMUNITY == m_tagType);}
	public boolean isPersonalTag()             {return (TagType.PERSONAL  == m_tagType);}
	public int     getTagSearchResultsCount()  {return m_searchResultsCnt;              }
	public int     getTagSearchResultsRating() {return m_searchResultsRating;           }
	public String  getTagSearchText()          {return m_searchText;                    }
	public String  getTagEntity()              {return m_tagEntity;                     }
	public String  getTagId()                  {return m_tagId;                         }
	public String  getTagName()                {return m_tagName;                       }
	public String  getTagOwnerEntity()         {return m_tagOwnerEntity;                }
	public TagType getTagType()                {return m_tagType;                       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTagSearchResultsCount( int     cnt)            {m_searchResultsCnt    = cnt;           }
	public void setTagSearchResultsRating(int     rating)         {m_searchResultsRating = rating;        }
	public void setTagSearchText(         String  text)           {m_searchText          = text;          }
	public void setTagEntity(             String  tagEntity)      {m_tagEntity           = tagEntity;     }
	public void setTagId(                 String  tagId)          {m_tagId               = tagId;         }
	public void setTagName(               String  tagName)        {m_tagName             = tagName;       }
	public void setTagOwnerEntity(        String  tagOwnerEntity) {m_tagOwnerEntity      = tagOwnerEntity;}
	public void setTagType(               TagType tagType)        {m_tagType             = tagType;       }

	/**
	 * Constructs and returns a TagInfo used as the tag to indicate a
	 * hidden Shared By Me share.
	 * 
	 * @return
	 */
	public static TagInfo buildHiddenSharedByTag() {
		TagInfo reply = new TagInfo();
		reply.setTagName(TagInfo.HIDDEN_SHARED_BY_TAG);
		reply.setTagType(TagType.PERSONAL            );
		return reply;
	}
	
	/**
	 * Constructs and returns a TagInfo used as the tag to indicate a
	 * hidden Shared By Me share.
	 * 
	 * @return
	 */
	public static TagInfo buildHiddenSharedWithTag() {
		TagInfo reply = new TagInfo();
		reply.setTagName(TagInfo.HIDDEN_SHARED_WITH_TAG);
		reply.setTagType(TagType.PERSONAL              );
		return reply;
	}
	
	/**
	 * Returns true if this is a tag used to indicate a hidden Shared
	 * By Me share and false otherwise.
	 * 
	 * @return
	 */
	public boolean isHiddenSharedByTag() {
		return (isPersonalTag() && TagInfo.HIDDEN_SHARED_BY_TAG.equals(getTagName()));
	}
	
	/**
	 * Returns true if this is a tag used to indicate a hidden Shared
	 * With Me share and false otherwise.
	 * 
	 * @return
	 */
	public boolean isHiddenSharedWithTag() {
		return (isPersonalTag() && TagInfo.HIDDEN_SHARED_WITH_TAG.equals(getTagName()));
	}
}
