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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "Link to Folder" widget in a landing page.
 * @author jwootton
 *
 */
public class LinkToFolderProperties
	implements PropertiesObj
{
	private String		m_title;
	private boolean	m_openInNewWindow;
	private String m_folderId;
	private String m_folderName;
	private AsyncCallback<GwtFolder> m_folderCallback;
	private boolean m_rpcInProgress;
	
	/**
	 * 
	 */
	public LinkToFolderProperties()
	{
		m_title = null;
		m_openInNewWindow = false;
		m_folderId = null;
		m_folderName = null;
		m_rpcInProgress = false;
		
		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		m_folderCallback = new AsyncCallback<GwtFolder>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				//!!! Do something here.
				Window.alert( "The request to get the GwtFolder object failed." );
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtFolder gwtFolder )
			{
				if ( gwtFolder != null )
					m_folderName = gwtFolder.getFolderName();
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};
	}// end LinkToFolderProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof LinkToFolderProperties )
		{
			LinkToFolderProperties folderProps;
			
			folderProps = (LinkToFolderProperties) props;
			
			setFolderId( folderProps.getFolderId() );
			setFolderName( folderProps.getFolderName() );
			setTitle( folderProps.getTitle() );
			setOpenInNewWindow( folderProps.getOpenInNewWindow() );
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "binderUrl,binderId=370,title=something,popup=1;"
		str = "binderUrl,binderId=";
		if ( m_folderId != null )
			str += m_folderId;
		str += ",";
		
		str += "title=";
		if ( m_title != null )
			str += ConfigData.encodeConfigData( m_title );
		str += ",";
		
		str += "popup=";
		if ( m_openInNewWindow )
			str += "1";
		else
			str += "0";
		str += ";";

		return str;
	}// end createConfigString()
	
	
	/**
	 * Issue an ajax request to get the folder's name from the server.
	 */
	public void getDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		// Do we have a folder id?
		if ( m_folderId != null )
		{
			// Yes, Issue an ajax request to get the GwtFolder object for the given folder id.
			m_rpcInProgress = true;
			rpcService = GwtTeaming.getRpcService();
			rpcService.getFolder( m_folderId, m_folderCallback );
		}
	}// end getDataFromServer()
	
	
	/**
	 * Return the folder id.
	 */
	public String getFolderId()
	{
		return m_folderId;
	}// end getFolderId()
	
	
	/**
	 * Return the name of the folder.
	 */
	public String getFolderName()
	{
		return m_folderName;
	}// end getFolderName()
	
	
	/**
	 * Return the value of the "Open the url in a new window" property
	 */
	public boolean getOpenInNewWindow()
	{
		return m_openInNewWindow;
	}// getOpenInNewWindow()
	
	
	/**
	 * Return the "title" property.
	 */
	public String getTitle()
	{
		return m_title;
	}// end getTitle()
	
	
	/**
	 * Return whether an rpc call is in progress.
	 */
	public boolean isRpcInProgress()
	{
		return m_rpcInProgress;
	}// end isRpcInProgress()
	
	
	/**
	 * 
	 */
	public void setFolderId( String folderId )
	{
		// Did the folder id change?
		if ( m_folderId != null && m_folderId.equalsIgnoreCase( folderId ) )
		{
			// Yes
			// Since we are changing the folder id clear out the folder name
			m_folderName = "???";
		}
		
		m_folderId = folderId;
	}// end setFolderId()
	
	
	/**
	 * 
	 */
	public void setFolderName( String folderName )
	{
		m_folderName = folderName;
	}// end setFolderName()
	
	
	/**
	 * 
	 */
	public void setOpenInNewWindow( boolean value )
	{
		m_openInNewWindow = value;
	}// end setOpenInNewWindow()
	
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}// end setTitle()
}// end LinkToFolderProperties
