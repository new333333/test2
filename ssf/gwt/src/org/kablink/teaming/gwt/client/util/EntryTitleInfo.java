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
 * Class used to bundle information regarding an entry's title through
 * GWT RPC requests.
 *  
 * @author drfoster
 */
public class EntryTitleInfo implements IsSerializable {
	private boolean m_trash;		//
	private boolean	m_seen;			//
	private Long	m_entryId;		//
	private String	m_description;	//
	private String  m_entityType;	//
	private String  m_title;		//
	
	// The following is only used on the client side to push an Image
	// through to the data table for binders.
	private transient Object m_clientBinderImage; 

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public EntryTitleInfo() {
		// Nothing to do.
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean getSeen()              {return m_seen;             }
	public boolean getTrash()             {return m_trash;            }
	public Long    getEntryId()           {return m_entryId;          }
	public String  getDescription()       {return m_description;      }
	public String  getEntityType()        {return m_entityType;       }
	public String  getTitle()             {return m_title;            }
	public Object  getClientBinderImage() {return m_clientBinderImage;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSeen(             boolean seen)              {m_seen              = seen;             }
	public void setTrash(            boolean trash)             {m_trash             = trash;            }
	public void setEntryId(          Long    entryId)           {m_entryId           = entryId;          }
	public void setDescription(      String  description)       {m_description       = description;      }
	public void setEntityType(       String  entityType)        {m_entityType        = entityType;       }
	public void setTitle(            String  title)             {m_title             = title;            }
	public void setClientBinderImage(Object  clientBinderImage) {m_clientBinderImage = clientBinderImage;}
	
	/**
	 * Constructs an EntryTitleInfo from the parameters.
	 *
	 * @param seen
	 * @param trash
	 * @param entityType
	 * @param title
	 * @param description
	 * 
	 * @return
	 */
	public static EntryTitleInfo construct(Long entryId, boolean seen, boolean trash, String entityType, String title, String description) {
		EntryTitleInfo reply = new EntryTitleInfo();
		
		reply.setEntryId(    entryId    );
		reply.setSeen(       seen       );
		reply.setTrash(      trash      );
		reply.setEntityType( entityType );
		reply.setTitle(      title      );
		reply.setDescription(description);
		
		return reply;
	}
}
