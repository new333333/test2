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
package org.kablink.teaming.gwt.client.mainmenu;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate favorites information between the client
 * (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getFavorites().)
 * 
 * @author drfoster@novell.com
 *
 */
public class FavoriteInfo implements IsSerializable {
	private String m_action;	// The favorite's action.
	private String m_category;	// The favorite's category.
	private String m_eletype;	// The favorite's element type.
	private String m_hover;		// The favorite's hover.
	private String m_id;		// The favorite's ID.
	private String m_name;		// The favorite's name.
	private String m_type;		// The favorite's type.
	private String m_value;		// The favorite's value.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FavoriteInfo() {
		// Nothing to do.
	}

	/**
	 * Public get'er methods.
	 * 
	 * @return
	 */
	public String getAction()  {return m_action;}
	public String getCategory(){return m_category;}
	public String getEletype() {return m_eletype;}
	public String getHover()   {return m_hover;}
	public String getId()      {return m_id;}
	public String getName()    {return m_name;}
	public String getType()    {return m_type;}
	public String getValue()   {return m_value;}
	
	/**
	 * Public set'er methods.
	 * 
	 * @param s
	 */
	public void setAction(  String s){m_action   = s;}
	public void setCategory(String s){m_category = s;}
	public void setEletype( String s){m_eletype  = s;}
	public void setHover(   String s){m_hover    = s;}
	public void setId(      String s){m_id       = s;}
	public void setName(    String s){m_name     = s;}
	public void setType(    String s){m_type     = s;}
	public void setValue(   String s){m_value    = s;}
}
