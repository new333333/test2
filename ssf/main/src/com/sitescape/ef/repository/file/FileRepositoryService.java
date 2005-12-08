package com.sitescape.ef.repository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.FileHelper;

public class FileRepositoryService implements RepositoryService {

	private String rootDirPath;
	
	public String getRootDirPath() {
		return rootDirPath;
	}

	public void setRootDirPath(String rootPath) throws IOException {
		this.rootDirPath = new File(rootPath).getCanonicalPath();
		
		if(!rootDirPath.endsWith(File.separator))
			rootDirPath += File.separator;
		
		FileHelper.mkdirsIfNecessary(rootDirPath);
	}

	public void write(Folder folder, FolderEntry entry, MultipartFile mf) throws RepositoryServiceException {
		
		File dir = getDir(folder, entry);
		
		if(!dir.exists())
			dir.mkdirs();
    	
        try {
        	mf.transferTo(new File(dir, mf.getOriginalFilename()));
		} catch (Exception e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Folder folder, FolderEntry entry, String fileName, OutputStream out) throws RepositoryServiceException {
		String filePath = getFilePath(folder, entry, fileName);
		
		FileInputStream in = null;
		
		try {
			in = new FileInputStream(filePath);
		
			FileHelper.copyContent(in, out);
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} 
				catch (IOException e) {}
			}
		}
	}

	private String getDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(rootDirPath).append(zoneName).append(File.separator).append(folder.getId()).append(File.separator).append(entry.getId()).append(File.separator).toString();
	}
	
	private File getDir(Folder folder, FolderEntry entry) {
		return new File(getDirPath(folder, entry));
	}
	
	private String getFilePath(Folder folder, FolderEntry entry, String fileName) {
		return new StringBuffer(getDirPath(folder, entry)).append(fileName).toString();
	}
}
