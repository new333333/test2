package com.sitescape.ef.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadItem {

	private MultipartFile mf;
	
	private String repositoryServiceName;

	// path info?
	
	public FileUploadItem(MultipartFile mf, String repositoryServiceName) {
		setMultipartFile(mf);
		setRepositoryServiceName(repositoryServiceName);
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
