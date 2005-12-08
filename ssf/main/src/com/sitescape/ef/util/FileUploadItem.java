package com.sitescape.ef.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadItem {

	private String name; // This is NOT file name.
	
	private MultipartFile mf;
	
	private String repositoryServiceName;

	// path info?
	
	public FileUploadItem(String name, MultipartFile mf, String repositoryServiceName) {
		this.name = name;
		this.mf = mf;
		this.repositoryServiceName = repositoryServiceName;
	}
	
	public String getName() {
		return name;
	}
	
	public MultipartFile getMultipartFile() {
		return mf;
	}

	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}
}
