package com.sitescape.ef.ssfs.server.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;

public class SiteScapeFileSystemImpl implements SiteScapeFileSystem {

	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private FileModule fileModule;

	protected FolderModule getFolderModule() {
		return folderModule;
	}
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	protected FileModule getFileModule() {
		return fileModule;
	}
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}

	
	public boolean objectExists(Map uri) throws NoAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	public void createResource(Map uri) throws NoAccessException, AlreadyExistsException {
		// TODO Auto-generated method stub
		
	}

	public void setResource(Map uri, InputStream content) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		
	}

	public InputStream getResource(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getResourceLength(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void removeResource(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		
	}

	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getChildrenNames(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

}
