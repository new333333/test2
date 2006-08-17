package com.sitescape.ef.ssfs.server.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.LockException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.TypeMismatchException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;

public class SiteScapeFileSystemImpl implements SiteScapeFileSystem {

	protected final Log logger = LogFactory.getLog(getClass());

	private SiteScapeFileSystemInternal ssfsInt;
	private SiteScapeFileSystemLibrary ssfsLib;
	
	public SiteScapeFileSystemImpl() {
		ssfsInt = new SiteScapeFileSystemInternal();
		ssfsLib = new SiteScapeFileSystemLibrary();
	}
	
	public void setFolderModule(FolderModule folderModule) {
		ssfsInt.setFolderModule(folderModule);
		ssfsLib.setFolderModule(folderModule);
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		ssfsInt.setDefinitionModule(definitionModule);
		ssfsLib.setDefinitionModule(definitionModule);
	}
	public void setBinderModule(BinderModule binderModule) {
		ssfsInt.setBinderModule(binderModule);
		ssfsLib.setBinderModule(binderModule);
	}
	public void setProfileModule(ProfileModule profileModule) {
		ssfsInt.setProfileModule(profileModule);
		ssfsLib.setProfileModule(profileModule);
	}
	public void setFileModule(FileModule fileModule) {
		ssfsInt.setFileModule(fileModule);
		ssfsLib.setFileModule(fileModule);
	}
	public void setWorkspaceModule(WorkspaceModule wsModule) {
		ssfsInt.setWorkspaceModule(wsModule);
		ssfsLib.setWorkspaceModule(wsModule);
	}
	public void setMimeTypes(FileTypeMap mimeTypes) {
		ssfsInt.setMimeTypes(mimeTypes);
		ssfsLib.setMimeTypes(mimeTypes);
	}
	/*
	public void setCoreDao(CoreDao coreDao) {
		ssfsInt.setCoreDao(coreDao);
		ssfsLib.setCoreDao(coreDao);		
	}*/

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
	
	public void createFolder(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.createFolder(uri);
		else
			ssfsLib.createFolder(uri);
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
	NoSuchObjectException {
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
			Date lockExpirationDate) 
	throws NoAccessException, NoSuchObjectException, LockException,
	TypeMismatchException {
		if(isInternal(uri))
			ssfsInt.lockResource(uri, lockId, lockSubject, lockExpirationDate);
		else
			ssfsLib.lockResource(uri, lockId, lockSubject, lockExpirationDate);
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
	AlreadyExistsException, TypeMismatchException {
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
