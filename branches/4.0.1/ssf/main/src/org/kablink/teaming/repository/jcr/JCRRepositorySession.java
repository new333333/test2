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
package org.kablink.teaming.repository.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.activation.FileTypeMap;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.util.Text;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositorySession;
import org.kablink.teaming.repository.RepositorySessionFactory;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.repository.jcr.jackrabbit.JCRRepositorySessionFactory;
import org.kablink.teaming.util.Constants;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;


public class JCRRepositorySession implements RepositorySession {

	private JCRRepositorySessionFactory factory;
	protected String workspaceName;
	protected Session session;
	protected FileTypeMap mimeTypes;
	
	public JCRRepositorySession(JCRRepositorySessionFactory factory, 
			String workspaceName, Session session, FileTypeMap mimeTypes) {
		this.factory = factory;
		this.workspaceName = workspaceName;
		this.session = session;
		this.mimeTypes = mimeTypes;
	}
	
	public void close() throws RepositoryServiceException, UncheckedIOException {
		if(session != null) {
			session.logout();
			session = null;
		}
	}

	public int fileInfo(Binder binder, DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return getFileInfo(binder, entity, relativeFilePath);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public String createVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return createFile(binder, entity, relativeFilePath, in, true);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void createUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			createFile(binder, entity, relativeFilePath, in, false);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void update(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			updateFile(binder, entity, relativeFilePath, in);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void delete(Binder binder, DefinableEntity entity, 
			String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteFile(binder, entity, relativeFilePath);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void delete(Binder binder, DefinableEntity entity) 
	throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteDir(binder, entity);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void delete(Binder binder) 
	throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteDir(binder);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void readUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException {
		InputStream is = readUnversioned(binder, entity, relativeFilePath);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public InputStream readUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException, 
			UncheckedIOException {
		try {
			return readFileAsStream(binder, entity, relativeFilePath, null);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}


	public void readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, 
			String latestVersionName, OutputStream out) 
	throws RepositoryServiceException, UncheckedIOException {
		InputStream is = readVersioned(binder, entity, relativeFilePath, versionName, latestVersionName);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	public InputStream readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, String latestVersionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return readFileAsStream(binder, entity, relativeFilePath, versionName);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void checkout(Binder binder, DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			checkoutFile(binder, entity, relativeFilePath);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void uncheckout(Binder binder, DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		throw new UnsupportedOperationException("JCR does not support uncheckout");
	}

	public String checkin(Binder binder, DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return checkinFile(binder, entity, relativeFilePath);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public long getContentLengthUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException, 
			UncheckedIOException {
		try {
			return getFileContentLength(binder, entity, relativeFilePath, null);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public long getContentLengthVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return getFileContentLength(binder, entity, relativeFilePath, versionName);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void move(Binder binder, DefinableEntity entity, 
			String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntity, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {

		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		String newFileNodePath = getFileNodePath(destBinder, destEntity, destRelativeFilePath);

		try {
			createFoldersIfNecessary(getFolderNodePath(newFileNodePath));
			
			Node dirNode = getRootNode();
			String absFileNodePath = dirNode.getCorrespondingNodePath(workspaceName) + "/" + fileNodePath;
			String newAbsFileNodePath = dirNode.getCorrespondingNodePath(workspaceName) + "/" + newFileNodePath;
			
			session.getWorkspace().move(absFileNodePath, newAbsFileNodePath);
		} catch (ItemExistsException e) {
			throw new RepositoryServiceException(e);
		} catch (PathNotFoundException e) {
			throw new RepositoryServiceException(e);
		} catch (VersionException e) {
			throw new RepositoryServiceException(e);
		} catch (ConstraintViolationException e) {
			throw new RepositoryServiceException(e);
		} catch (LockException e) {
			throw new RepositoryServiceException(e);
		} catch (RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}


	public void copy(Binder binder, DefinableEntity entity, String relativeFilePath, 
			Binder destBinder, DefinableEntity destEntity, String destRelativeFilePath) 
	throws RepositoryServiceException, UncheckedIOException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		String newFileNodePath = getFileNodePath(destBinder, destEntity, destRelativeFilePath);

		try {
			createFoldersIfNecessary(getFolderNodePath(newFileNodePath));

			Node dirNode = getRootNode();
			String absFileNodePath = dirNode.getCorrespondingNodePath(workspaceName) + "/" + fileNodePath;
			String newAbsFileNodePath = dirNode.getCorrespondingNodePath(workspaceName) + "/" + newFileNodePath;
			
			session.getWorkspace().copy(absFileNodePath, newAbsFileNodePath);
		} catch (ItemExistsException e) {
			throw new RepositoryServiceException(e);
		} catch (PathNotFoundException e) {
			throw new RepositoryServiceException(e);
		} catch (VersionException e) {
			throw new RepositoryServiceException(e);
		} catch (ConstraintViolationException e) {
			throw new RepositoryServiceException(e);
		} catch (LockException e) {
			throw new RepositoryServiceException(e);
		} catch (RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}
	
	public void deleteVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteFileVersion(binder, entity, relativeFilePath, versionName);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}
	
	// For internal use only
	public List<String> getVersionNames(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException {
		try {
			return getFileVersionNames(binder, entity, relativeFilePath);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}			
	}

	private int getFileInfo(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);

		Node rootNode = getRootNode();
		
		if(rootNode.hasNode(fileNodePath)) {
			Node fileNode = rootNode.getNode(fileNodePath);
			Node contentNode = fileNode.getNode(JCRConstants.JCR_CONTENT);
			try {
				contentNode.getVersionHistory();
				
				return VERSIONED_FILE;
			}
			catch(UnsupportedRepositoryOperationException e) {
				return UNVERSIONED_FILE;
			}
		}
		else {
			return NON_EXISTING_FILE;
		}
	}
	
	private Node getFolderNode(Node node, String name)
			throws RepositoryException {

		Node folderNode = null;

		if (node.hasNode(name)) {
			folderNode = node.getNode(name);
		} else {
			folderNode = node.addNode(name, JCRConstants.NT_FOLDER);
		}

		return folderNode;
	}

	/**
	 * Returns node corresponding to the context zone. 
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	private Node getRootNode() throws RepositoryException {
		String zoneName = RequestContextHolder.getRequestContext()
				.getZoneName();

		Node zoneNode = getFolderNode(session.getRootNode(), zoneName);

		return zoneNode;
	}

	/**
	 * Return path to the node representing the specified file. Returned path
	 * is relative to the root node. 
	 *  
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 */
	private String getFileNodePath(Binder binder, DefinableEntity entity, String relativeFilePath) {
		return getFileNodePath(getEntityNodePath(binder, entity), relativeFilePath);
	}

	private String getFileNodePath(String entityNodePath, String relativeFilePath) {
		if(relativeFilePath.startsWith(Constants.SLASH))
			relativeFilePath = relativeFilePath.substring(1);
		
		relativeFilePath = Text.escapeIllegalJcrChars(relativeFilePath);
		
		return entityNodePath + relativeFilePath;
	}
	
	private String getEntityNodePath(Binder binder, DefinableEntity entity) {
		return RepositoryUtil.getEntityPath(binder, entity, Constants.SLASH);
	}
	
	private String getBinderNodePath(Binder binder) {
		return RepositoryUtil.getBinderPath(binder, Constants.SLASH);
	}
	
	private String createFile(Binder binder, DefinableEntity entity,
			String relativeFilePath, InputStream is, boolean versioned) 
		throws RepositoryException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		
		Node folderNode = createFoldersIfNecessary(getFolderNodePath(fileNodePath));
		
		String fileName = getEscapedFileName(relativeFilePath);
		
		Node fileNode = folderNode.addNode(fileName, JCRConstants.NT_FILE);
		
		Node contentNode = fileNode.addNode(JCRConstants.JCR_CONTENT, 
				JCRConstants.NT_RESOURCE);
		
		if(versioned)
			contentNode.addMixin(JCRConstants.MIX_VERSIONABLE);
		
		contentNode.setProperty(JCRConstants.JCR_MIME_TYPE, 
				mimeTypes.getContentType(fileName));
		
		contentNode.setProperty(JCRConstants.JCR_DATA, is);
		
		contentNode.setProperty(JCRConstants.JCR_LAST_MODIFIED, Calendar.getInstance());
		
		session.save();
		
		if(versioned) {
			Version version = contentNode.checkin();
			return version.getName();
		}
		else {
			return null;
		}
	}
	
	private Node getFileNode(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		
		Node rootNode = getRootNode();
		
		return rootNode.getNode(fileNodePath);
	}
	
	private Node getEntityNode(Binder binder, DefinableEntity entity) 
	throws RepositoryException {
		String entityNodePath = getEntityNodePath(binder, entity);
		
		Node rootNode = getRootNode();
		
		return rootNode.getNode(entityNodePath);
	}
	
	private Node getBinderNode(Binder binder) 
	throws RepositoryException {
		String binderNodePath = getBinderNodePath(binder);
		
		Node rootNode = getRootNode();
		
		return rootNode.getNode(binderNodePath);
	}
	
	private void updateFile(Binder binder, DefinableEntity entity,
			String relativeFilePath, InputStream is) throws RepositoryException {
		String fileName = getEscapedFileName(relativeFilePath);
		
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
				
		contentNode.setProperty(JCRConstants.JCR_MIME_TYPE, mimeTypes.getContentType(fileName));
		
		contentNode.setProperty(JCRConstants.JCR_DATA, is);
		
		contentNode.setProperty(JCRConstants.JCR_LAST_MODIFIED, Calendar.getInstance());
		
		session.save();
	}
	
	private void deleteFile(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node fileNode = getFileNode(binder, entity, relativeFilePath);
		
		fileNode.remove();
		
		session.save();
	}
	
	private void deleteDir(Binder binder, DefinableEntity entity) 
	throws RepositoryException {
		Node entityNode = getEntityNode(binder, entity);
		
		entityNode.remove();
		
		session.save();
	}
	
	private void deleteDir(Binder binder) 
	throws RepositoryException {
		Node binderNode = getBinderNode(binder);
		
		binderNode.remove();
		
		session.save();
	}
	
	private Node getVersionContentNode(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
		
		if(versionName != null) {
			VersionHistory versionHistory = contentNode.getVersionHistory();
			
			Version version = versionHistory.getVersion(versionName);
			
			contentNode = version.getNode(JCRConstants.JCR_FROZEN_NODE);
		}
		
		return contentNode;
	}
	
	private InputStream readFileAsStream(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) throws RepositoryException {
		
		Node contentNode = getVersionContentNode(binder, entity, 
				relativeFilePath, versionName);
		
		Property data = contentNode.getProperty(JCRConstants.JCR_DATA);

		return data.getStream();
	}
	
	private Node getFileContentNode(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node fileNode = getFileNode(binder, entity, relativeFilePath);
		
		return fileNode.getNode(JCRConstants.JCR_CONTENT);
	}
	
	private String getFolderNodePath(String fileNodePath) {
		return fileNodePath.substring(0, fileNodePath.lastIndexOf(Constants.SLASH) + 1);		
	}
	
	private Node createFoldersIfNecessary(String folderNodePath) throws RepositoryException {
		String[] dirNameArray = StringUtil.split(folderNodePath, Constants.SLASH);

		Node dirNode = getRootNode();

		for (int i = 0; i < dirNameArray.length; i++) {
			if (Validator.isNotNull(dirNameArray[i])) {
				if (dirNode.hasNode(dirNameArray[i])) {
					dirNode = dirNode.getNode(dirNameArray[i]);
				}
				else {
					dirNode = dirNode.addNode(dirNameArray[i], JCRConstants.NT_FOLDER);
				}
			}
		}
		
		session.save();
		
		return dirNode;
	}
	
	private String getFileName(String relativeFilePath) {
		int index = relativeFilePath.lastIndexOf(Constants.SLASH);
		
		if(index < 0)
			return relativeFilePath;
		else
			return relativeFilePath.substring(index + 1);
	}
	
	private String getEscapedFileName(String relativeFilePath) {
		return Text.escapeIllegalJcrChars(getFileName(relativeFilePath));
	}
	
	private void checkoutFile(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
		
		contentNode.checkout();
	}
	
	private String checkinFile(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
		
		if(contentNode.isCheckedOut()) {
			// currently checked out
			Version version = contentNode.checkin();
		
			return version.getName();
		}
		else {
			// not checked out
			return null;
		}
	}
	
	private long getFileContentLength(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryException {
		Node contentNode = getVersionContentNode(binder, entity, 
				relativeFilePath, versionName);
		
		return contentNode.getProperty(JCRConstants.JCR_DATA).getLength();
	}

	private void deleteFileVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);

		VersionHistory versionHistory = contentNode.getVersionHistory();
		
		Version version = versionHistory.getVersion(versionName);
		
		Version lastVersion = contentNode.getBaseVersion();
		
		if(version.equals(lastVersion))
			contentNode.restore(version.getPredecessors()[0],true);
		
		versionHistory.removeVersion(versionName);
	}

	private List<String> getFileVersionNames(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);

		VersionHistory versionHistory = contentNode.getVersionHistory();

		List<String> list = new ArrayList<String>();
		for(VersionIterator it = versionHistory.getAllVersions(); it.hasNext();) {
			Version v = it.nextVersion();
			list.add(v.getName());
		}
		return list;
	}

	public RepositorySessionFactory getFactory() {
		return factory;
	}
}
