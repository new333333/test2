package com.sitescape.ef.repository.webdav;

import java.io.InputStream;
import java.io.OutputStream;

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
		} catch (Exception e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Folder folder, FolderEntry entry, String fileName, OutputStream out) throws RepositoryServiceException {
		try {
			readInternal(folder, entry, fileName, out);
		} catch (Exception e) {
			throw new RepositoryServiceException(e);
		}
	}

	private void writeInternal(Folder folder, FolderEntry entry, MultipartFile mf) throws Exception {
		// How do we get WebDAV username/password for individual users??
		WebdavResource wdr = getWebdavResourceFactory().openSession("root", "root");
		
		try {
			String entryDirPath = getEntryDirPath(folder, entry);

			/*
			boolean b = wdr.headMethod("/slide/files");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			b = wdr.headMethod("/slide/files/sitescape");
			System.out.println("Status Code: " + wdr.getStatusCode());
			System.out.println("Status Message: " + wdr.getStatusMessage());
			*/
			
			WebdavUtil.createCollectionIfNecessary(wdr, entryDirPath);
			
			String filePath = entryDirPath + mf.getOriginalFilename();
			
			boolean result = wdr.putMethod(filePath, mf.getInputStream());
		}
		finally {
			wdr.close();
		}
	}
	
	public void readInternal(Folder folder, FolderEntry entry, String fileName, OutputStream out) throws Exception {
		// How do we get WebDAV username/password for individual users??
		WebdavResource wdr = getWebdavResourceFactory().openSession("root", "root");
		
		try {
			String filePath = getFilePath(folder, entry, fileName);
			
			InputStream is = wdr.getMethodData(filePath);
			
			FileHelper.copyContent(is, out);
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
