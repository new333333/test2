/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get file conflicts info'
 * RPC command.
 * 
 * @author drfoster@novell.com
 */
public class FileConflictsInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private DisplayInfo			m_folderDisplay;			//
	private List<DisplayInfo>	m_fileConflictsDisplayList;	//

	/**
	 * Inner class used to encapsulate display information about a
	 * single entity (file, folder, ...) 
	 */
	public static class DisplayInfo implements IsSerializable {
		private String m_iconUrl;	//
		private String m_name;		//
		private String m_path;		//
		
		/**
		 * Constructor method.
		 */
		public DisplayInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param name
		 * @param path
		 * @param iconUrl
		 */
		public DisplayInfo(String name, String path, String iconUrl) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setName(   name   );
			setPath(   path   );
			setIconUrl(iconUrl);
		}
		
		/**
		 * Get'er methods
		 * 
		 * @return
		 */
		public String getIconUrl() {return m_iconUrl;}
		public String getName()    {return m_name;   }
		public String getPath()    {return m_path;   }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setIconUrl(String iconUrl) {m_iconUrl = iconUrl;}
		public void setName(   String name)    {m_name    = name;   }
		public void setPath(   String path)    {m_path    = path;   }
	}
	
	/**
	 * Constructor method. 
	 */
	public FileConflictsInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else that requires it.
		m_fileConflictsDisplayList = new ArrayList<DisplayInfo>();
	}
	
	/**
	 * Get'er methods
	 * 
	 * @return
	 */
	public DisplayInfo       getFolderDisplay()            {return m_folderDisplay;           }
	public List<DisplayInfo> getFileConflictsDisplayList() {return m_fileConflictsDisplayList;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFolderDisplay(           DisplayInfo       folderDisplay)           {m_folderDisplay            = folderDisplay;          }
	public void setFileConflictsDisplayList(List<DisplayInfo> fileConfictsDisplayList) {m_fileConflictsDisplayList = fileConfictsDisplayList;}
	
	/**
	 * Adds a DisplayInfo to the file conflicts display list.
	 * 
	 * @param fileConflict
	 */
	public void addFileConflictDisplay(DisplayInfo fileConflict) {
		m_fileConflictsDisplayList.add(fileConflict);
	}

	/**
	 * Returns a count of the conflicts.
	 * 
	 * @return
	 */
	public int getConflictCount() {
		return ((null == m_fileConflictsDisplayList) ? 0 : m_fileConflictsDisplayList.size());
	}
}
