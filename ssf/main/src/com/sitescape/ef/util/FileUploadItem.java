package com.sitescape.ef.util;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.repository.RepositoryServiceNames;

public class FileUploadItem {

	private MultipartFile mf;
	
	private String repositoryServiceName = RepositoryServiceNames.FILE_REPOSITORY_SERVICE; // default

	// path info?
	
	public FileUploadItem(MultipartFile mf) {
		setMultipartFile(mf);
	}
	
	public MultipartFile getMultipartFile() {
		return mf;
	}

	public void setMultipartFile(MultipartFile mf) {
		this.mf = mf;
	}

	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}

	public void setRepositoryServiceName(String repositoryServiceName) {
		this.repositoryServiceName = repositoryServiceName;
	}
}
