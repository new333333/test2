/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.util;

import java.io.BufferedInputStream;
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
	
	private int type;
	private boolean uniqueName=false;
	private boolean registered=false;
	private boolean markup=false; //indicates whether this file is referenced from a text area
	private String markupFieldName = null; //data field name that references this attachment = not the same as name.
	private String name; // This is NOT file name; it is the naem of data field the file is attached to 
	
	// Used for generating scaled file - Zero value indicates no need for 
	// generating scaled file. 
	private int maxWidth = 0;
	private int maxHeight = 0;
	
	// Used for generating thumbnail file - the following boolean flag is
	// used to indicate whether to generate thumbnail or not.
	private boolean generateThumbnail = false;
	private boolean isSquareThumbnail = false;
	private int thumbnailMaxWidth = SPropsUtil.getInt("thumbnail.max.width", THUMBNAIL_MAX_WIDTH_DEFAULT);
	private int thumbnailMaxHeight = SPropsUtil.getInt("thumbnail.max.height", THUMBNAIL_MAX_HEIGHT_DEFAULT);
	
	private MultipartFile mf;
	
	private String repositoryName;
	
	private File file;
	private boolean isTempFile = false;
	
	private boolean synchToRepository = true; // can be false only for mirrored entries/files

	// path info?
	
	public FileUploadItem(int type, String name, MultipartFile mf, String repositoryName) {
		this.type = type;
		this.name = name;
		this.mf = mf;
		this.repositoryName = repositoryName;
	}
	
	public boolean getGenerateThumbnail() {
		return generateThumbnail;
	}

	public void setGenerateThumbnail(boolean generateThumbnail) {
		this.generateThumbnail = generateThumbnail;
	}

	public boolean getIsSquareThumbnail() {
		return isSquareThumbnail;
	}

	public void setIsSquareThumbnail(boolean isSquareThumbnail) {
		this.isSquareThumbnail = isSquareThumbnail;
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
		if (isSquareThumbnail) {
			return 0;
		} else {
			return thumbnailMaxHeight;			
		}
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
	//name represents the data name associated with this field that this file is attached to
	public String getName() {
		return name;
	}
	public boolean isUniqueName() {
		return uniqueName;
	}
	public void setUniqueName(boolean uniqueName) {
		this.uniqueName = uniqueName;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	public boolean isMarkup() {
		return markup;
	}
	public void setMarkup(boolean markup) {
		this.markup = markup;
	}
	public String getMarkupFieldName() {
		return markupFieldName;
	}
	public void setMarkupFieldName(String markupFieldName) {
		this.markupFieldName = markupFieldName;
	}
	
	/**
	 * Return the original filename in the client's filesystem. 
	 * @return
	 */
	public String getOriginalFilename() {
		return mf.getOriginalFilename();
	}

	public String getRepositoryName() {
		return repositoryName;
	}
	
	public byte[] getBytes() throws IOException {
		if(file != null)
			return FileCopyUtils.copyToByteArray(file);
		else
			return mf.getBytes();
	}
	
	public InputStream getInputStream() throws IOException  {
		if(file != null)
			return new BufferedInputStream(new FileInputStream(file));
		else 
			return mf.getInputStream();
	}
	
	public void delete() throws IOException {
		if(file != null && isTempFile)
			FileHelper.delete(file);

		if(mf instanceof SimpleMultipartFile)
			((SimpleMultipartFile) mf).close();
	}
	
	public void makeReentrant() throws IOException {
		if(mf instanceof SimpleMultipartFile) {
			SimpleMultipartFile smp = (SimpleMultipartFile) mf;
			if(smp.getFile() == null) {
				if(file == null) {
					file = TempFileUtil.createTempFile(TEMP_FILE_PREFIX);
					mf.transferTo(file);
					isTempFile = true;										
				}
			}
		}
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

	public String getModifierName() {
		if(mf instanceof FileModDateSupport)
			return ((FileModDateSupport) mf).getModifier();
		else
			return null;
	}
	public boolean isSynchToRepository() {
		return synchToRepository;
	}

	public void setSynchToRepository(boolean synchToRepository) {
		this.synchToRepository = synchToRepository;
	}
	
}
