package com.sitescape.ef.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadItem {

	public static final int TYPE_FILE = 1;
	public static final int TYPE_ATTACHMENT = 2;
	public static final int TYPE_TITLE = 3;
	
	private static final int THUMBNAIL_MAX_WIDTH_DEFAULT = 100;
	private static final int THUMBNAIL_MAX_HEIGHT_DEFAULT = 100;
	
	private static final String TEMP_FILE_PREFIX = "upload_";
	private static final String CONVERT_FILE_PREFIX = "convert_";
	private static final String HTMLCONVERTED_FILE_PREFIX = "htmlconvert_";
	
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
	
	private File tempFile;
	
	private boolean ready = false;

	// path info?
	
	public FileUploadItem(int type, String name, MultipartFile mf, String repositoryServiceName) {
		this.type = type;
		this.name = name;
		this.mf = mf;
		this.repositoryServiceName = repositoryServiceName;
	}
	
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

	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Return the original filename in the client's filesystem. 
	 * @return
	 */
	public String getOriginalFilename() {
		return mf.getOriginalFilename();
	}
	
	public long getSize() throws IOException {
		long size = mf.getSize();
		if(size < 0) {
			// This indicates that the underlying MultipartFile is not capable
			// of computing the size of the data without actually reading it
			// first. 
			if(!ready)
				setup();
			size = tempFile.length();
		}
		return size;
	}
	
	/*
	public MultipartFile getMultipartFile() {
		return mf;
	}*/

	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}
	
	public byte[] getBytes() throws IOException {
		if(!ready)
			setup();
		return FileCopyUtils.copyToByteArray(tempFile);
	}
	
	public InputStream getInputStream() throws IOException  {
		if(!ready)
			setup();
		return new FileInputStream(tempFile);
	}

	public File getFile() throws IOException {
		if(!ready)
			setup();
		return tempFile;
	}
	
	public void delete() throws IOException {
		if(tempFile != null) {
			FileHelper.delete(tempFile);
		}
	}
	
	private void setup() throws IOException {
		// Make sure that the uploaded data is accessible through File interface,
		// regardless of the mechanism used. May not be the most efficient way though.
		File tempDir = SPropsUtil.getFile("temp.dir");
		if(!tempDir.exists())
			FileHelper.mkdirs(tempDir);
		
		this.tempFile = File.createTempFile(TEMP_FILE_PREFIX, null, tempDir);
		
		this.mf.transferTo(this.tempFile);
		
		ready = true;
	}
	
	public File getTempFile()  throws IOException {
		return SPropsUtil.getTempFile(CONVERT_FILE_PREFIX);
	}
	
	/**
	 * Returns modification date or <code>null</code>.
	 * 
	 * @return
	 */
	public Date getModDate() {
		if(mf instanceof FileModDateSupport)
			return ((FileModDateSupport) mf).getModDate();
		else
			return null;
	}
}
