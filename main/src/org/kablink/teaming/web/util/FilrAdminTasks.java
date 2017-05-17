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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.util.XmlUtil;

/**
 * A FilrAdminTasks object contains a list of tasks that the Filr administrator needs to do.
 * At this time there are only 2 tasks:
 * 	1. Enter the proxy credentials for a Net Folder Server
 * 	2. Select the server type for a net folder server.
 * 
 * The xml looks like the following xml:
 *	<FilrAdminTasks>
 *		<EnterNetFolderServerProxyCredentials>
 *			<NetFolderServer id="some value" />
 *			<NetFolderServer id="some value" />
 *		</EnterNetFolderServerProxyCredentials>
 *		<EnterNetFolderServerProxyIdentity>
 *			<NetFolderServer id="some value" />
 *			<NetFolderServer id="some value" />
 *		</EnterNetFolderServerProxyIdentity>
 *		<SelectNetFolderServerType>
 *			<NetFolderServer id="some value" />
 *			<NetFolderServer id="some value" />
 *		</SelectNetFolderServerType>
 *	</FilrAdminTasks>
 * 
 * @author jwootton@novell.com
 */
public class FilrAdminTasks {
	private Document	m_adminTasksDoc;	//
	
	/**
	 * 
	 */
	public FilrAdminTasks()
	{
	}
	
	/**
	 * 
	 */
	public FilrAdminTasks( Document adminTasksDoc )
	{
		m_adminTasksDoc = adminTasksDoc;
	}

	/**
	 * 
	 */
	public FilrAdminTasks( String xmlEncoding )
	{
		if ( xmlEncoding == null )
			return;
		
		try
		{
			m_adminTasksDoc = XmlUtil.parseText( xmlEncoding );
		}
		catch ( Exception ex )
		{
		};
	}

	/**
	 * Add an "enter net folder server proxy credentials" task. 
	 */
	public Document addEnterNetFolderServerProxyCredentialsTask( Long netFolderServerId )
	{
		Element taskElement;
		Element parentElement;
		String idStr;
		
		if ( netFolderServerId == null )
			return null;
		
		idStr = String.valueOf( netFolderServerId );
		
		// Make sure we have a document to work with.
		getAdminTasksDoc();
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>?
		taskElement = getEnterProxyCredentialsTask( idStr );
		if ( taskElement != null )
		{
			// Yes
			return m_adminTasksDoc;
		}
		
		// Get the <EnterNetFolderServerProxyCredentials> element
		parentElement = getEnterProxyCredentialsElement();
		
		// Add <NetFolderServer id="some value" />
		taskElement = parentElement.addElement( "NetFolderServer" );
		taskElement.addAttribute( "id", idStr );
		
		return m_adminTasksDoc;
	}

	/**
	 * Add a 'select net folder server proxy identity' task. 
	 */
	public Document addEnterNetFolderServerProxyIdentityTask( Long netFolderServerId )
	{
		Element taskElement;
		Element parentElement;
		String idStr;
		
		if ( netFolderServerId == null )
			return null;
		
		idStr = String.valueOf( netFolderServerId );
		
		// Make sure we have a document to work with.
		getAdminTasksDoc();
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyIdentity>?
		taskElement = getEnterProxyIdentityTask( idStr );
		if ( taskElement != null )
		{
			// Yes
			return m_adminTasksDoc;
		}
		
		// Get the <EnterNetFolderServerProxyIdentity> element
		parentElement = getEnterProxyIdentityElement();
		
		// Add <NetFolderServer id="some value" />
		taskElement = parentElement.addElement( "NetFolderServer" );
		taskElement.addAttribute( "id", idStr );
		
		return m_adminTasksDoc;
	}

	/**
	 * Add a "select net folder server type" task. 
	 */
	public Document addSelectNetFolderServerTypeTask( Long netFolderServerId )
	{
		Element taskElement;
		Element parentElement;
		String idStr;
		
		if ( netFolderServerId == null )
			return null;
		
		idStr = String.valueOf( netFolderServerId );
		
		// Make sure we have a document to work with.
		getAdminTasksDoc();
		
		// Does a <NetFolderServer id="xxx" /> exists under <SelectNetFolderServerType>?
		taskElement = getSelectNetFolderServerTypeTask( idStr );
		if ( taskElement != null )
		{
			// Yes
			return m_adminTasksDoc;
		}
		
		// Get the <SelectNetFolderServerType> element
		parentElement = getSelectNetFolderServerTypeElement();
		
		// Add <NetFolderServer id="some value" />
		taskElement = parentElement.addElement( "NetFolderServer" );
		taskElement.addAttribute( "id", idStr );
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	private Document createAdminTasksRootDocument()
	{
		Document doc;

		doc = DocumentHelper.createDocument();
		doc.addElement( "FilrAdminTasks" );
		
		return doc;
	}
	
	/**
	 * 
	 */
	public Document deleteEnterNetFolderServerProxyCredentialsTask( Long netFolderServerId )
	{
		Element taskElement;
		String idStr;
		
		getAdminTasksDoc();
		
		idStr = String.valueOf( netFolderServerId );
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>?
		taskElement = getEnterProxyCredentialsTask( idStr );
		if ( taskElement != null )
		{
			taskElement.getParent().remove( taskElement );
		}
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	public Document deleteEnterNetFolderServerProxyIdentityTask( Long netFolderServerId )
	{
		Element taskElement;
		String idStr;
		
		getAdminTasksDoc();
		
		idStr = String.valueOf( netFolderServerId );
		
		// Does a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyIdentity>?
		taskElement = getEnterProxyIdentityTask( idStr );
		if ( taskElement != null )
		{
			taskElement.getParent().remove( taskElement );
		}
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	public Document deleteSelectNetFolderServerTypeTask( Long netFolderServerId )
	{
		Element taskElement;
		String idStr;
		
		getAdminTasksDoc();
		
		idStr = String.valueOf( netFolderServerId );
		
		// Does a <NetFolderServer id="xxx" /> exists under <SelectNetFolderServerType>?
		taskElement = getSelectNetFolderServerTypeTask( idStr );
		if ( taskElement != null )
		{
			taskElement.getParent().remove( taskElement );
		}
		
		return m_adminTasksDoc;
	}

	/**
	 * 
	 */
	private Document getAdminTasksDoc()
	{
		if ( m_adminTasksDoc == null )
		{
			m_adminTasksDoc = createAdminTasksRootDocument();
		}
		
		return m_adminTasksDoc;
	}
	
	/**
	 * Return all of the "enter proxy credentials" tasks
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<EnterProxyCredentialsTask> getAllEnterProxyCredentialsTasks()
	{
		ArrayList<EnterProxyCredentialsTask> listOfTasks;
		List listOfElements;
		
		listOfTasks = new ArrayList<EnterProxyCredentialsTask>();
		
		getAdminTasksDoc();
		
		// Get all of the <NetFolderServer> elements that live under <EnterNetFolderServerProxyCredentials>
		listOfElements = m_adminTasksDoc.selectNodes( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials/NetFolderServer" ); 
		
		if ( listOfElements != null )
		{
			Iterator iter;
			
			iter = listOfElements.iterator();
			while ( iter.hasNext() ) 
			{
				Element nextElement;
				Node node;
				
				nextElement = (Element) iter.next();
				node = nextElement.selectSingleNode( "@id" );
				if ( node != null && node instanceof Attribute )
				{
					Attribute attrib;
					EnterProxyCredentialsTask task;

					attrib = (Attribute) node;
					task = new EnterProxyCredentialsTask();
					task.setNetFolderServerId( attrib.getValue() );
					
					listOfTasks.add( task );
				}
			}
		}
		
		return listOfTasks;
	}
	
	/**
	 * Return all of the "enter proxy identity" tasks
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<EnterProxyIdentityTask> getAllEnterProxyIdentityTasks()
	{
		ArrayList<EnterProxyIdentityTask> listOfTasks;
		List listOfElements;
		
		listOfTasks = new ArrayList<EnterProxyIdentityTask>();
		
		getAdminTasksDoc();
		
		// Get all of the <NetFolderServer> elements that live under <EnterNetFolderServerProxyIdentity>
		listOfElements = m_adminTasksDoc.selectNodes( "/FilrAdminTasks/EnterNetFolderServerProxyIdentity/NetFolderServer" ); 
		
		if ( listOfElements != null )
		{
			Iterator iter;
			
			iter = listOfElements.iterator();
			while ( iter.hasNext() ) 
			{
				Element nextElement;
				Node node;
				
				nextElement = (Element) iter.next();
				node = nextElement.selectSingleNode( "@id" );
				if ( node != null && node instanceof Attribute )
				{
					Attribute attrib;
					EnterProxyIdentityTask task;

					attrib = (Attribute) node;
					task = new EnterProxyIdentityTask();
					task.setNetFolderServerId( attrib.getValue() );
					
					listOfTasks.add( task );
				}
			}
		}
		
		return listOfTasks;
	}
	
	/**
	 * Return all of the "select net folder server type" tasks
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<SelectNetFolderServerTypeTask> getAllSelectNetFolderServerTypeTasks()
	{
		ArrayList<SelectNetFolderServerTypeTask> listOfTasks;
		List listOfElements;
		
		listOfTasks = new ArrayList<SelectNetFolderServerTypeTask>();
		
		getAdminTasksDoc();
		
		// Get all of the <NetFolderServer> elements that live under <SelectNetFolderServerType>
		listOfElements = m_adminTasksDoc.selectNodes( "/FilrAdminTasks/SelectNetFolderServerType/NetFolderServer" ); 
		
		if ( listOfElements != null )
		{
			Iterator iter;
			
			iter = listOfElements.iterator();
			while ( iter.hasNext() ) 
			{
				Element nextElement;
				Node node;
				
				nextElement = (Element) iter.next();
				node = nextElement.selectSingleNode( "@id" );
				if ( node != null && node instanceof Attribute )
				{
					Attribute attrib;
					SelectNetFolderServerTypeTask task;

					attrib = (Attribute) node;
					task = new SelectNetFolderServerTypeTask();
					task.setNetFolderServerId( attrib.getValue() );
					
					listOfTasks.add( task );
				}
			}
		}
		
		return listOfTasks;
	}
	
	/**
	 * See if a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyCredentials>
	 */
	private Element getEnterProxyCredentialsTask( String netFolderServerId )
	{
		Element rootElement;
		Element serverElement;

		if ( netFolderServerId == null )
			return null;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <NetFolderServer> element
		serverElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials/NetFolderServer[@id='" + netFolderServerId + "']" ); 
			
		return serverElement;
	}
	
	/**
	 * See if a <NetFolderServer id="xxx" /> exists under <EnterNetFolderServerProxyIdentity>
	 */
	private Element getEnterProxyIdentityTask( String netFolderServerId )
	{
		Element rootElement;
		Element serverElement;

		if ( netFolderServerId == null )
			return null;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <NetFolderServer> element
		serverElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyIdentity/NetFolderServer[@id='" + netFolderServerId + "']" ); 
			
		return serverElement;
	}
	
	/**
	 * Find the <EnterNetFolderServerProxyCredentials> element.  If it doesn't exist, create it.
	 */
	private Element getEnterProxyCredentialsElement()
	{
		Element rootElement;
		Element enterProxyCredentialsElement;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <EnterNetFolderServerProxyCredentials> element
		enterProxyCredentialsElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyCredentials" ); 
			
		// Did we find a <EnterNetFolderServerProxyCredentials> element?
		if ( enterProxyCredentialsElement == null )
		{
			// No, create one.
			enterProxyCredentialsElement = rootElement.addElement( "EnterNetFolderServerProxyCredentials" );
		}
		
		return enterProxyCredentialsElement;
	}

	/**
	 * Find the <EnterNetFolderServerProxyIdentity> element.  If it doesn't exist, create it.
	 */
	private Element getEnterProxyIdentityElement()
	{
		Element rootElement;
		Element enterProxyIdentityElement;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <EnterNetFolderServerProxyIdentity> element
		enterProxyIdentityElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/EnterNetFolderServerProxyIdentity" ); 
			
		// Did we find a <EnterNetFolderServerProxyIdentity> element?
		if ( enterProxyIdentityElement == null )
		{
			// No, create one.
			enterProxyIdentityElement = rootElement.addElement( "EnterNetFolderServerProxyIdentity" );
		}
		
		return enterProxyIdentityElement;
	}

	/**
	 * See if a <NetFolderServer id="xxx" /> exists under <SelectNetFolderServerType>
	 */
	private Element getSelectNetFolderServerTypeTask( String netFolderServerId )
	{
		Element rootElement;
		Element serverElement;

		if ( netFolderServerId == null )
			return null;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <NetFolderServer> element
		serverElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/SelectNetFolderServerType/NetFolderServer[@id='" + netFolderServerId + "']" ); 
			
		return serverElement;
	}
	
	/**
	 * Find the <SelectNetFolderServerType> element.  If it doesn't exist, create it.
	 */
	private Element getSelectNetFolderServerTypeElement()
	{
		Element rootElement;
		Element selectNetFolderServerTypeElement;
		
		rootElement = m_adminTasksDoc.getRootElement();
		
		// Look for the <SelectNetFolderServerType> element
		selectNetFolderServerTypeElement = (Element)rootElement.selectSingleNode( "/FilrAdminTasks/SelectNetFolderServerType" ); 
			
		// Did we find a <SelectNetFolderServerType> element?
		if ( selectNetFolderServerTypeElement == null )
		{
			// No, create one.
			selectNetFolderServerTypeElement = rootElement.addElement( "SelectNetFolderServerType" );
		}
		
		return selectNetFolderServerTypeElement;
	}

	/**
	 * 
	 */
	@Override
	public String toString()
	{
		return getAdminTasksDoc().asXML();
	}
}
