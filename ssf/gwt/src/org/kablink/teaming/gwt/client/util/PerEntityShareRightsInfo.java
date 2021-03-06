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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to track sharing rights on a per entity basis. 
 * 
 * @author drfoster@novell.com
 */
public class PerEntityShareRightsInfo implements IsSerializable, VibeRpcResponseData {
	private boolean m_allowExternal;	//
	private boolean m_allowForwarding;	//
	private boolean m_allowInternal;	//
	private boolean m_allowPublic;		//
	private boolean m_allowPublicLinks;	//
	private boolean m_allowFolderExternal;
	private boolean m_allowFolderInternal;
	private boolean m_allowFolderPublic;
	private boolean m_allowFolderForwarding;
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public PerEntityShareRightsInfo() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method. 
	 * 
	 * @param allowExternal
	 * @param allowForwarding
	 * @param allowInternal
	 * @param allowPublic
	 * @param allowPublicLinks
	 */
	public PerEntityShareRightsInfo(boolean allowExternal, boolean allowForwarding, boolean allowInternal, boolean allowPublic, boolean allowPublicLinks, boolean allowFolderExternal, boolean allowFolderInternal, boolean allowFolderPublic, boolean allowFolderForwarding) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setAllowExternal(   allowExternal   );
		setAllowForwarding( allowForwarding );
		setAllowInternal(   allowInternal   );
		setAllowPublic(     allowPublic     );
		setAllowPublicLinks(allowPublicLinks);
		setAllowFolderExternal( allowFolderExternal );
		setAllowFolderInternal( allowFolderInternal );
		setAllowFolderPublic( allowFolderPublic );
		setAllowFolderForwarding( allowFolderForwarding );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isAllowExternal()    {return m_allowExternal;   }
	public boolean isAllowForwarding()  {return m_allowForwarding; }
	public boolean isAllowInternal()    {return m_allowInternal;   }
	public boolean isAllowPublic()      {return m_allowPublic;     }
	public boolean isAllowPublicLinks() {return m_allowPublicLinks;}
	public boolean isAllowFolderExternal() {return m_allowFolderExternal; }
	public boolean isAllowFolderInternal() {return m_allowFolderInternal; }
	public boolean isAllowFolderPublic() {return m_allowFolderPublic; }
	public boolean isAllowFolderForwarding() {return m_allowFolderForwarding; }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAllowExternal(   boolean allowExternal)    {m_allowExternal    = allowExternal;   }
	public void setAllowForwarding( boolean allowForwarding)  {m_allowForwarding  = allowForwarding; }
	public void setAllowInternal(   boolean allowInternal)    {m_allowInternal    = allowInternal;   }
	public void setAllowPublic(     boolean allowPublic)      {m_allowPublic      = allowPublic;     }
	public void setAllowPublicLinks(boolean allowPublicLinks) {m_allowPublicLinks = allowPublicLinks;}
	public void setAllowFolderExternal( boolean allowFolderExternal) {m_allowFolderExternal = allowFolderExternal; }
	public void setAllowFolderInternal( boolean allowFolderInternal) {m_allowFolderInternal = allowFolderInternal; }
	public void setAllowFolderPublic( boolean allowFolderPublic) {m_allowFolderPublic = allowFolderPublic;}
	public void setAllowFolderForwarding( boolean allowFolderForwarding) {m_allowFolderForwarding = allowFolderForwarding; }

	/**
	 * Returns true if all of the flags are set and false otherwise.
	 * 
	 * @return
	 */
	public boolean allFlagsSet() {
		return (
			m_allowExternal   &&
			m_allowForwarding &&
			m_allowInternal   &&
			m_allowPublic     &&
			m_allowPublicLinks);
	}
	
	public boolean allFolderFlagsSet(){
		return (m_allowFolderExternal && m_allowFolderInternal && m_allowFolderPublic && m_allowFolderForwarding);
	}

	/**
	 * Returns true if any of the flags are set and false otherwise.
	 * 
	 * @return
	 */
	public boolean anyFlagsSet() {
		return (
			m_allowExternal   ||
			m_allowForwarding ||
			m_allowInternal   ||
			m_allowPublic     ||
			m_allowPublicLinks);
	}
	
	public boolean anyFolderFlagsSet(){
		return (m_allowFolderExternal || m_allowFolderInternal || m_allowFolderPublic || m_allowFolderForwarding);
	}
}
