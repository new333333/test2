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
 * Class used to communicate information about buckets in a workspace
 * tree between the client (i.e., the WorkspaceTreeControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTreeInfo().)
 * 
 *	<child
 *		title="folder.1 &lt;--&gt; folder.1087"
 *		id="3785.0"
 *		hasChildren="true"
 *		type="range"
 *		image="/icons/range.gif"
 *		imageClass="ss_twImg8"
 *		page="0"
 *		pageTuple="folder.1//folder.1087"
 *		tuple1="folder.1"
 *		tuple2="folder.1087"
 *		action=""
 *		displayOnly="true" />
 *		
 * @author drfoster@novell.com
 */
public class BucketInfo implements IsSerializable {
	private String m_bucketId;
	private String m_bucketPage;
	private String m_bucketPageTuple;
	private String m_bucketTitle;
	private String m_bucketTuple1;
	private String m_bucketTuple2;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public BucketInfo() {
		// Nothing to do.
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String getBucketId()        {return m_bucketId;       }
	public String getBucketPage()      {return m_bucketPage;     }
	public String getBucketPageTuple() {return m_bucketPageTuple;}
	public String getBucketTitle()     {return m_bucketTitle;    }
	public String getBucketTuple1()    {return m_bucketTuple1;   }
	public String getBucketTuple2()    {return m_bucketTuple2;   }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBucketId(       String bucketId)        {m_bucketId        = bucketId;       }
	public void setBucketPage(     String bucketPage)      {m_bucketPage      = bucketPage;     }
	public void setBucketPageTuple(String bucketPageTuple) {m_bucketPageTuple = bucketPageTuple;}
	public void setBucketTitle(    String bucketTitle)     {m_bucketTitle     = bucketTitle;    }
	public void setBucketTuple1(   String bucketTuple1)    {m_bucketTuple1    = bucketTuple1;   }
	public void setBucketTuple2(   String bucketTuple2)    {m_bucketTuple2    = bucketTuple2;   }
}
