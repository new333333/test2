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
package org.kablink.teaming.ssfs.server.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.activation.FileTypeMap;

import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.ssfs.AlreadyExistsException;
import org.kablink.teaming.ssfs.CrossContextConstants;
import org.kablink.teaming.ssfs.LockException;
import org.kablink.teaming.ssfs.NoAccessException;
import org.kablink.teaming.ssfs.NoSuchObjectException;
import org.kablink.teaming.ssfs.TypeMismatchException;
import org.kablink.teaming.ssfs.server.KablinkFileSystem;
import org.kablink.teaming.util.AbstractAllModulesInjected;


public class KablinkFileSystemImpl extends AbstractAllModulesInjected 
implements KablinkFileSystem {

	private KablinkFileSystemInternal ssfsInt;
	private KablinkFileSystemLibrary ssfsLib;
	
	public KablinkFileSystemImpl() {
		ssfsInt = new KablinkFileSystemInternal(this);
		ssfsLib = new KablinkFileSystemLibrary(this);
	}
	
	public void setMimeTypes(FileTypeMap mimeTypes) {
		ssfsInt.setMimeTypes(mimeTypes);
		ssfsLib.setMimeTypes(mimeTypes);
	}

	public void setCoreDao(CoreDao coreDao) {
		ssfsInt.setCoreDao(coreDao);
		ssfsLib.setCoreDao(coreDao);		
	}
	
	public void createResource(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createResource(uri);
		else
			ssfsLib.createResource(uri);
	}

	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.setResource(uri, content);
		else
			ssfsLib.setResource(uri, content);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createAndSetResource(uri, content);
		else
			ssfsLib.createAndSetResource(uri, content);
	}
	
	public void createDirectory(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createDirectory(uri);
		else
			ssfsLib.createDirectory(uri);
	}
	
	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			return ssfsInt.getResource(uri);
		else
			return ssfsLib.getResource(uri);
	}

	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			return ssfsInt.getResourceLength(uri);
		else
			return ssfsLib.getResourceLength(uri);
	}*/

	public void removeObject(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			ssfsInt.removeObject(uri);
		else
			ssfsLib.removeObject(uri);
	}
	
	/*
	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getLastModified(uri);
		else
			return ssfsLib.getLastModified(uri);
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getCreationDate(uri);
		else
			return ssfsLib.getCreationDate(uri);
	}*/

	public String[] getChildrenNames(Map uri) throws NoAccessException, 
	NoSuchObjectException, WriteFilesException {
		if(isInternal(uri))
			return ssfsInt.getChildrenNames(uri);
		else
			return ssfsLib.getChildrenNames(uri);
	}

	public Map getProperties(Map uri) throws NoAccessException,
	NoSuchObjectException {
		if(isInternal(uri))
			return ssfsInt.getProperties(uri);
		else
			return ssfsLib.getProperties(uri);
	}
	
	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo)
	throws NoAccessException, NoSuchObjectException, LockException,
	TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.lockResource(uri, lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
		else
			ssfsLib.lockResource(uri, lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
	}
	
	public void unlockResource(Map uri, String lockId) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.unlockResource(uri, lockId);
		else
			ssfsLib.unlockResource(uri, lockId);
	}
	
	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, boolean recursive)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(sourceUri))
			ssfsInt.copyObject(sourceUri, targetUri, overwrite, recursive);
		else
			ssfsLib.copyObject(sourceUri, targetUri, overwrite, recursive);
	}
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite)
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException, WriteEntryDataException, WriteFilesException {
		if(isInternal(sourceUri))
			ssfsInt.moveObject(sourceUri, targetUri, overwrite);
		else
			ssfsLib.moveObject(sourceUri, targetUri, overwrite);		
	}
	
	private boolean isInternal(Map uri) {
		if(((String) uri.get(CrossContextConstants.URI_TYPE)).equals(CrossContextConstants.URI_TYPE_INTERNAL))
			return true;
		else
			return false;
	}
}
