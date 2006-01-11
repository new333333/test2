package com.sitescape.ef.util;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadItem {

	public static final int TYPE_FILE = 1;
	public static final int TYPE_ATTACHMENT = 2;
	
	private static final int THUMBNAIL_MAX_WIDTH_DEFAULT = 100;
	private static final int THUMBNAIL_MAX_HEIGHT_DEFAULT = 100;
	
	private int type;
	
	private String name; // This is NOT file name.
	
	// Used for generating scaled file - Zero value indicates no need for 
	// generating scaled file. 
	private int maxWidth = 0;
	private int maxHeight = 0;
	
	// Used for generating thumbnail file - the following boolean flag is
	// used to indicate whether to generate thumbnail or not.
	private boolean generateThumbnail = false;
	private boolean thumbnailDirectlyAccessible = true;
	private int thumbnailMaxWidth = SPropsUtil.getInt("thumbnail.max.width", THUMBNAIL_MAX_WIDTH_DEFAULT);
	private int thumbnailMaxHeight = SPropsUtil.getInt("thumbnail.max.height", THUMBNAIL_MAX_HEIGHT_DEFAULT);
	
	private MultipartFile mf;
	
	private String repositoryServiceName;

	// path info?
	
	public boolean getGenerateThumbnail() {
		return generateThumbnail;
	}

	public void setGenerateThumbnail(boolean generateThumbnail) {
		this.generateThumbnail = generateThumbnail;
	}

	public boolean isThumbnailDirectlyAccessible() {
		return thumbnailDirectlyAccessible;
	}

	public void setThumbnailDirectlyAccessible(boolean thumbnailDirectlyAccessible) {
		this.thumbnailDirectlyAccessible = thumbnailDirectlyAccessible;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getThumbnailMaxHeight() {
		return thumbnailMaxHeight;
	}

	public void setThumbnailMaxHeight(int thumbnailMaxHeight) {
		this.thumbnailMaxHeight = thumbnailMaxHeight;
	}

	public int getThumbnailMaxWidth() {
		return thumbnailMaxWidth;
	}

	public void setThumbnailMaxWidth(int thumbnailMaxWidth) {
		this.thumbnailMaxWidth = thumbnailMaxWidth;
	}

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
	
	public MultipartFile getMultipartFile() {
		return mf;
	}

	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}
}
