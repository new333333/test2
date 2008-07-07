/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.ssfs.server;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.sitescape.team.ssfs.AlreadyExistsException;
import com.sitescape.team.ssfs.LockException;
import com.sitescape.team.ssfs.NoAccessException;
import com.sitescape.team.ssfs.NoSuchObjectException;
import com.sitescape.team.ssfs.TypeMismatchException;

public interface SiteScapeFileSystem {

	public void createResource(Map uri) throws 
	NoAccessException, AlreadyExistsException, TypeMismatchException;
	
	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException;
	
	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException;

	public void createDirectory(Map uri) throws  
	NoAccessException, AlreadyExistsException, TypeMismatchException;
	
	public InputStream getResource(Map uri) throws  
	NoAccessException, NoSuchObjectException, TypeMismatchException;
	
	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException;
	*/
	
	public void removeObject(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	/*
	public Date getLastModified(Map uri) throws NoAccessException, 
	NoSuchObjectException;

	public Date getCreationDate(Map uri) throws NoAccessException, 
	NoSuchObjectException;
	*/

	public String[] getChildrenNames(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	public Map getProperties(Map uri) throws  
	NoAccessException, NoSuchObjectException;
	
	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo)
	throws NoAccessException, NoSuchObjectException, 
	LockException, TypeMismatchException;
	
	public void unlockResource(Map uri, String lockId) throws  
	NoAccessException, NoSuchObjectException, TypeMismatchException;
	
	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, boolean recursive)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException;
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException;
}
