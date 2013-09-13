/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to represent the data used in home dir net folder creation.  This class should mirror
 * the data found in HomeDirConfig
 * @author jwootton
 *
 */
public class GwtHomeDirConfig implements IsSerializable
{
	/**
	 * This class represents the different options available for creating a home dir net folder.
	 * This class should mirror the data found in HomeDirCreationOption
	 */
	public enum GwtHomeDirCreationOption
	{
		USE_CUSTOM_CONFIG,
		USE_HOME_DIRECTORY_ATTRIBUTE,
		USE_CUSTOM_ATTRIBUTE,
		DONT_CREATE_HOME_DIR_NET_FOLDER,
		UNKNOWN
	}
	
	private GwtHomeDirCreationOption m_creationOption;
	
	// The following data members are relevant when the creation option is "custom config"
	private String m_netFolderServerName;
	private String m_path;

	// The following data members are relevant when the creation option is "custom attribute"
	private String m_attributeName;

	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtHomeDirConfig()
	{
	}
}
