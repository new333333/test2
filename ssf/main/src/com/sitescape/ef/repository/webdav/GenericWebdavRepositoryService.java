package com.sitescape.ef.repository.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.WebdavResource;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.webdav.client.WebdavResourceFactory;
import com.sitescape.ef.webdav.client.WebdavUtil;

public class WebdavRepositoryService implements RepositoryService {

	protected Log logger = LogFactory.getLog(getClass());
	
	private WebdavResourceFactory webdavResourceFactory;
	
	protected WebdavResourceFactory getWebdavResourceFactory() {
		return webdavResourceFactory;
	}

	public void setWebdavResourceFactory(WebdavResourceFactory webdavResourceFactory) {
		this.webdavResourceFactory = webdavResourceFactory;
	}

	public void write(Folder folder, FolderEntry entry, MultipartFile mf) throws RepositoryServiceException {
		try {
			writeInternal(folder, entry, mf);
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Folder folder, FolderEntry entry, String fileName, OutputStream out) throws RepositoryServiceException {
		try {
			readInternal(folder, entry, fileName, out);
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	private void writeInternal(Folder folder, FolderEntry entry, MultipartFile mf) throws IOException {
		// How do we get WebDAV username/password for individual users??
		WebdavResource wdr = getWebdavResourceFactory().openSession("root", "root");
		
		boolean result;
		
		try {
			// Get the path for the entry containing the file. 
			String entryDirPath = getEntryDirPath(folder, entry);

			/*
			boolean b = wdr.headMethod("/slide/files");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			b = wdr.headMethod("/slide/files/sitescape");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			*/
			
			// If necessary, create containing collections (recursively) before
			// writing the file itself. 
			WebdavUtil.createCollectionIfNecessary(wdr, entryDirPath);
			
			// Get the path for the file resource. 
			String filePath = entryDirPath + mf.getOriginalFilename();
			
			if(wdr.headMethod(filePath)) {
				// The file resource already exists.
				// Since we always put file resource under version control as 
				// soon as it is created, it's largely unnecessary to do it
				// again here. But we can think of a couple of scenarios where
				// the file may not have been put into version control propertly.
				// For example, the file was created successfully but the 
				// subsequent command for turning it into a version controlled
				// resource failed. Another example would be that the file was
				// created through some other means (i.e., other than this
				// interface), although it's very unlikely. 
				// The following command will be noop if the resource was already
				// version controlled. 
				result = wdr.versionControlMethod(filePath);
				// Write the file creating a new version. 
				result = wdr.putMethod(filePath, mf.getInputStream());
			}
			else {
				// The file resource does not exist. 
				// We must write the file first. 
				result = wdr.putMethod(filePath, mf.getInputStream());
				// Put the file under version control. 
				result = wdr.versionControlMethod(filePath);
			}
		}
		catch(IOException e) {
			// Log the HTTP status code and error message.
			logger.error("status code=" + wdr.getStatusCode() + ", status message=[" + wdr.getStatusMessage() + "]");
			// The actual exception object will be logged higher up.
		}
		finally {
			wdr.close();
		}
	}
	
	public void readInternal(Folder folder, FolderEntry entry, String fileName, OutputStream out) throws IOException {
		// How do we get WebDAV username/password for individual users??
		WebdavResource wdr = getWebdavResourceFactory().openSession("root", "root");
		
		try {
			String filePath = getFilePath(folder, entry, fileName);
			
			InputStream is = wdr.getMethodData(filePath);
			
			FileHelper.copyContent(is, out);

			WebdavUtil.dumpAllProps(wdr, filePath); // for debugging	
			
			WebdavUtil.dumpAllProps(wdr, "/slide/history/101"); // for debugging
		}
		catch(IOException e) {
			// Log the HTTP status code and error message.
			logger.error("status code=" + wdr.getStatusCode() + ", status message=[" + wdr.getStatusMessage() + "]");
			// The actual exception object will be logged higher up.
		}
		finally {
			wdr.close();
		}
	}

	private String getEntryDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(getWebdavResourceFactory().getDocRootDir()).
			append(zoneName).
			append(Constants.SLASH).
			append(folder.getId()).
			append(Constants.SLASH).
			append(entry.getId()).
			append(Constants.SLASH).
			toString();
	}
	
	private String getFilePath(Folder folder, FolderEntry entry, String fileName) {
		return getEntryDirPath(folder, entry) + fileName;
	}
}
