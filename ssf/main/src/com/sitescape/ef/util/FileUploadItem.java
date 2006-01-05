package com.sitescape.ef.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadItem {

	public static final int TYPE_FILE = 1;
	public static final int TYPE_ATTACHMENT = 2;
	
	private int type;
	
	private String name; // This is NOT file name.
	private Integer maxWidth;
	private Integer maxHeight;
	
	private MultipartFile mf;
	
	private String repositoryServiceName;

	// path info?
	
	public FileUploadItem(int type, String name, MultipartFile mf, String repositoryServiceName) {
		this.type = type;
		this.name = name;
		this.mf = mf;
		this.repositoryServiceName = repositoryServiceName;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getMaxWidth() {
		return maxWidth;
	}
	
	public Integer getMaxHeight() {
		return maxHeight;
	}
	
	public void setMaxWidth(Integer value) {
		maxWidth = value;
	}
	
	public void setMaxHeight(Integer value) {
		maxHeight = value;
	}
	
	public MultipartFile getMultipartFile() {
		return mf;
	}

	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}
}
