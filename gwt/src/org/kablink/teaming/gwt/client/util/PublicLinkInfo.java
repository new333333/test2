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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate information about a public link to a file.
 * 
 * @author drfoster@novell.com
 */
public class PublicLinkInfo implements IsSerializable {
	private boolean					m_expired;			//
	private Long					m_shareId;			//
	private ShareExpirationValue	m_expirationValue;	//
	private String					m_comment;			//
	private String					m_downloadUrl;		//
	private String					m_expiration;		//
	private String					m_imageUrl;			//
	private String					m_path;				//
	private String					m_sharedOn;			//
	private String					m_title;			//
	private String					m_viewUrl;			//

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public PublicLinkInfo() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 *
	 * @param shareId
	 * @param title
	 * @param path
	 * @param imageUrl
	 * @param downloadUrl
	 * @param viewUrl
	 * @param comment
	 * @param sharedOn
	 * @param expired
	 * @param expiration
	 * @param expirationValue
	 */
	public PublicLinkInfo(Long shareId, String title, String path, String imageUrl, String downloadUrl, String viewUrl, String comment, String sharedOn, boolean expired, String expiration, ShareExpirationValue expirationValue) {
		// Initialize the this object...
		this();
		
		// ...and store the parameters.
		setShareId(        shareId        );
		setTitle(          title          );
		setPath(           path           );
		setImageUrl   (    imageUrl       );
		setDownloadUrl(    downloadUrl    );
		setViewUrl(        viewUrl        );
		setComment(        comment        );
		setSharedOn(       sharedOn       );
		setExpired(        expired        );
		setExpiration(     expiration     );
		setExpirationValue(expirationValue);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean              isExpired()          {return m_expired;        }
	public Long                 getShareId()         {return m_shareId;        }
	public ShareExpirationValue getExpirationValue() {return m_expirationValue;}
	public String               getComment()         {return m_comment;        }
	public String               getDownloadUrl()     {return m_downloadUrl;    }
	public String               getExpiration()      {return m_expiration;     }
	public String               getImageUrl()        {return m_imageUrl;       }
	public String               getTitle()           {return m_title;          }
	public String               getPath()            {return m_path;           }
	public String               getSharedOn()        {return m_sharedOn;       }
	public String               getViewUrl()         {return m_viewUrl;        }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setExpired(        boolean              expired)         {m_expired         = expired;        }
	public void setShareId(        Long                 shareId)         {m_shareId         = shareId;        }
	public void setExpirationValue(ShareExpirationValue expirationValue) {m_expirationValue = expirationValue;}
	public void setComment(        String               comment)         {m_comment         = comment;        }
	public void setDownloadUrl(    String               downloadUrl)     {m_downloadUrl     = downloadUrl;    }
	public void setExpiration(     String               expiration)      {m_expiration      = expiration;     }
	public void setImageUrl(       String               imageUrl)        {m_imageUrl        = imageUrl;       }
	public void setPath(           String               path)            {m_path            = path;           }
	public void setSharedOn(       String               sharedOn)        {m_sharedOn        = sharedOn;       }
	public void setTitle(          String               title)           {m_title           = title;          }
	public void setViewUrl(        String               viewUrl)         {m_viewUrl         = viewUrl;        }
}
