package com.sitescape.ef.repository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;

public class FileRepositoryService implements RepositoryService {

	private static final int BUFFER_SIZE = 4096;
	
	private String rootDirPath;
	
	public String getRootDirPath() {
		return rootDirPath;
	}

	public void setRootDirPath(String rootDirPath) {
		File rootDir = new File(rootDirPath);
		if(!rootDir.exists()) {
			rootDir.mkdirs();
		}
		if(!rootDir.isDirectory())
			throw new IllegalArgumentException("Specified path '" + rootDirPath + "' is not a directory");
			
		if(!rootDirPath.endsWith(File.separator))
			rootDirPath += File.separator;
		
		this.rootDirPath = rootDirPath;
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
		
			copyContent(in, out);
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
		return new StringBuffer(rootDirPath).append(folder.getId()).append(File.separator).append(entry.getId()).append(File.separatorChar).toString();
	}
	
	private File getDir(Folder folder, FolderEntry entry) {
		return new File(getDirPath(folder, entry));
	}
	
	private String getFilePath(Folder folder, FolderEntry entry, String fileName) {
		return new StringBuffer(getDirPath(folder, entry)).append(fileName).toString();
	}
	
	private void copyContent(InputStream in, OutputStream out) throws IOException {
		int len;
		byte[] buffer = new byte[BUFFER_SIZE];
		while((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
	}
}
