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
package org.kablink.teaming.ssfs.server;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.ssfs.AlreadyExistsException;
import org.kablink.teaming.ssfs.LockException;
import org.kablink.teaming.ssfs.NoAccessException;
import org.kablink.teaming.ssfs.NoSuchObjectException;
import org.kablink.teaming.ssfs.TypeMismatchException;


public interface KablinkFileSystem {

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
	NoAccessException, NoSuchObjectException, WriteFilesException;
	
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
	AlreadyExistsException, TypeMismatchException, WriteEntryDataException, WriteFilesException;
}
