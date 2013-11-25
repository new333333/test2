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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate information about a tag between the
 * client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getBinderTags().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TagInfo implements IsSerializable {
	private String  m_tagEntity;					//
	private String  m_tagOwnerEntity;				//
	private String  m_tagId;						//
	private String  m_tagName;						//
	private TagType m_tagType = TagType.UNKNOWN;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TagInfo() {
		// Nothing to do.
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String  getTagEntity()      {return m_tagEntity;     }
	public String  getTagId()          {return m_tagId;         }
	public String  getTagName()        {return m_tagName;       }
	public String  getTagOwnerEntity() {return m_tagOwnerEntity;}
	public TagType getTagType()        {return m_tagType;       }
	
	/**
	 * Returns true of this tag defines a community tag and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isCommunityTag() {
		return (TagType.COMMUNITY == m_tagType);
	}

	/**
	 * Returns true of this tag defines a personal tag and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPersonalTag() {
		return (TagType.PERSONAL == m_tagType);
	}

	/**
	 * Stores the entity of a tag.
	 * 
	 * @param tagEntity
	 */
	public void setTagEntity(String tagEntity) {
		m_tagEntity = tagEntity;
	}
	
	/**
	 * Stores the ID of a tag.
	 * 
	 * @param tagId
	 */
	public void setTagId(String tagId) {
		m_tagId = tagId;
	}
	
	/**
	 * Stores the name of a tag.
	 * 
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		m_tagName = tagName;
	}
	
	/**
	 * Stores the owner entity of a tag.
	 * 
	 * @param tagOwnerEntity
	 */
	public void setTagOwnerEntity(String tagOwnerEntity) {
		m_tagOwnerEntity = tagOwnerEntity;
	}
	
	/**
	 * Stores the type of the tag.
	 * 
	 * @param tagType
	 */
	public void setTagType(TagType tagType) {
		m_tagType = tagType;
	}
}
