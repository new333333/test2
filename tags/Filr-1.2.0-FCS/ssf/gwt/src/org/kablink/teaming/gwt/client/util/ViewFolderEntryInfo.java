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
 * Class used to encapsulate information about a FOLDER_ENTRY view
 * through GWT RPC requests.
 *  
 * @author drfoster
 */
public class ViewFolderEntryInfo implements IsSerializable {
	private EntityId	m_entityId;				// The folder entry to view.
	private int			m_x, m_y, m_cx, m_cy;	// The position and size of the dialog.  Only used when m_viewStyle is iframe.
	private String		m_family;				// The definition family of the folder entry.
	private String		m_title;				// The title of the folder entry.
	private String		m_viewStyle;			// The style to run the entry viewer.

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public ViewFolderEntryInfo() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
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
	public EntityId getEntityId()  {return m_entityId; }
	public int      getCX()        {return m_cx;       }
	public int      getCY()        {return m_cy;       }
	public int      getX()         {return m_x;        }
	public int      getY()         {return m_y;        }
	public String   getFamily()    {return m_family;   }
	public String   getTitle()     {return m_title;    }
	public String   getViewStyle() {return m_viewStyle;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntityId( EntityId entityId)  {m_entityId  = entityId; }
	public void setCX(       int      cx)        {m_cx        = cx;       }
	public void setCY(       int      cy)        {m_cy        = cy;       }
	public void setX(        int      x)         {m_x         = x;        }
	public void setY(        int      y)         {m_y         = y;        }
	public void setFamily(   String   family)    {m_family    = family;   }
	public void setTitle(    String   title)     {m_title     = title;    }
	public void setViewStyle(String   viewStyle) {m_viewStyle = viewStyle;}

	/**
	 * Returns true of the view should be shown in the content area and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isContentView() {
		return "newpage".equalsIgnoreCase(getViewStyle());
	}
	
	/**
	 * Returns true of the view should be shown in a dialog and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isDialogView() {
		return (!(isContentView()));
	}
	
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
