package com.sitescape.ef.repository.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.activation.DataSource;
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

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.repository.webdav.SWebdavResource;
import com.sitescape.ef.repository.webdav.WebdavRepositorySession.WebDavDataSource;
import com.sitescape.ef.util.Constants;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

public class JCRRepositorySession implements RepositorySession {

	protected Session session;
	protected FileTypeMap mimeTypes;
	
	public JCRRepositorySession(Session session, FileTypeMap mimeTypes) {
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
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return createVersioned(binder, entity, relativeFilePath, mf.getInputStream());
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public String createVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return createFile(binder, entity, relativeFilePath, in, true);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void createUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			createFile(binder, entity, relativeFilePath, in, false);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void update(Binder binder, DefinableEntity entity, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			update(binder, entity, relativeFilePath, mf.getInputStream());
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void update(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in) 
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

	public void read(Binder binder, DefinableEntity entity, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException {
		InputStream is = read(binder, entity, relativeFilePath);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public InputStream read(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException, 
			UncheckedIOException {
		try {
			return readFileAsStream(binder, entity, relativeFilePath, null);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}


	public void readVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, OutputStream out) 
	throws RepositoryServiceException, UncheckedIOException {
		InputStream is = readVersion(binder, entity, relativeFilePath, versionName);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	public InputStream readVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return readFileAsStream(binder, entity, relativeFilePath, versionName);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public DataSource getDataSource(Binder binder, DefinableEntity entity, 
			String relativeFilePath, FileTypeMap fileTypeMap) 
		throws RepositoryServiceException, UncheckedIOException {
		return new JCRDataSource(binder, entity, relativeFilePath, null, fileTypeMap);
	}

	public DataSource getDataSourceVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap) 
		throws RepositoryServiceException, UncheckedIOException {
		return new JCRDataSource(binder, entity, relativeFilePath, versionName, fileTypeMap);
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

	public long getContentLength(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException, 
			UncheckedIOException {
		try {
			return getFileContentLength(binder, entity, relativeFilePath, null);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public long getContentLength(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return getFileContentLength(binder, entity, relativeFilePath, versionName);
		}
		catch(RepositoryException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void miniMove(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String newRelativeFilePath) 
	throws RepositoryServiceException, UncheckedIOException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		String newFileNodePath = getFileNodePath(binder, entity, newRelativeFilePath);

		try {
			session.move(fileNodePath, newFileNodePath);
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
		
		return entityNodePath + relativeFilePath;
	}
	
	private String getEntityNodePath(Binder binder, DefinableEntity entity) {
		return RepositoryUtil.getEntityPath(binder, entity, Constants.SLASH);
	}
	
	private String createFile(Binder binder, DefinableEntity entity,
			String relativeFilePath, InputStream is, boolean versioned) 
		throws RepositoryException {
		String fileNodePath = getFileNodePath(binder, entity, relativeFilePath);
		
		Node folderNode = createFoldersIfNecessary(getFolderNodePath(fileNodePath));
		
		String fileName = getFileName(relativeFilePath);
		
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
	
	private void updateFile(Binder binder, DefinableEntity entity,
			String relativeFilePath, InputStream is) throws RepositoryException {
		String fileName = getFileName(relativeFilePath);
		
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
					dirNode = dirNode.addNode(
						dirNameArray[i], JCRConstants.NT_FOLDER);
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
	
	private void checkoutFile(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
		
		contentNode.checkout();
	}
	
	private String checkinFile(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryException {
		Node contentNode = getFileContentNode(binder, entity, relativeFilePath);
		
		Version version = contentNode.checkin();
		
		return version.getName();
	}
	
	private long getFileContentLength(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryException {
		Node contentNode = getVersionContentNode(binder, entity, 
				relativeFilePath, versionName);
		
		return contentNode.getProperty(JCRConstants.JCR_DATA).getLength();
	}

	public class JCRDataSource implements DataSource {
		protected Binder binder;
		protected DefinableEntity entity;
		protected String relativeFilePath;
		protected String versionName;
		protected FileTypeMap fileMap;
		
		public JCRDataSource(Binder binder, DefinableEntity entity, 
				String relativeFilePath, String versionName, FileTypeMap fileMap) {
			this.binder = binder;
			this.entity = entity;
			this.relativeFilePath = relativeFilePath;
			this.versionName = versionName;
			this.fileMap = fileMap;
		}
		
		public java.io.InputStream getInputStream() throws java.io.IOException {
			return readVersion(binder, entity, relativeFilePath, versionName);
		}
		
		public java.io.OutputStream getOutputStream() throws java.io.IOException {
			return null;
		}
		
		public java.lang.String getContentType() {
			return fileMap.getContentType(getFileName(relativeFilePath));
		}
		public java.lang.String getName() {
			return relativeFilePath;
		}
	}
}
