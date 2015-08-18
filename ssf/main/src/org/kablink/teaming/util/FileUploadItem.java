/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.mail.MessagingException;

import org.apache.commons.io.output.NullOutputStream;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.module.mail.impl.DefaultEmailPoster.FileHandler;
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
	private String name; // This is NOT file name; it is the name of data field the file is attached to 
	private Description description; //This is the comment field associated with this file upload
	
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
	private boolean isEncrypted = false;
	private byte[] encryptionKey;
	
	private boolean synchToRepository = true; // can be false only for mirrored entries/files
	
	private String fileName = null;
    private String md5;

	// path info?
	
	public FileUploadItem(int type, String name, MultipartFile mf, String repositoryName) {
		this.type = type;
		this.name = name;
		this.mf = mf;
		this.repositoryName = repositoryName;
		this.description = new Description();
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

	public boolean getIsEncrypted() {
		return isEncrypted;
	}

	public void setIsEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public byte[] getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(byte[] encryptionKey) {
		this.encryptionKey = encryptionKey;
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
	
	public Description getDescription() {
		return description;
	}
	
	public void setDescription(Description description) {
		this.description = description;
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
		if(fileName == null)
			return mf.getOriginalFilename();
		else
			return fileName;
	}
	
	public void setOriginalFilename(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Return the original filename in the client's filesystem. 
	 * @return
	 */
	public String getContentId() {
		if(mf instanceof FileHandler)
			try {
				return ((FileHandler) mf).getContentId();
			} catch (MessagingException e) {
				return null;
			}
		
		return null;
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
		if (file != null) {
			if (isEncrypted) {
				InputStream bis = new BufferedInputStream(new FileInputStream(file));
				
				//Initialize the crypto session
				CryptoFileEncryption cfe = new CryptoFileEncryption(encryptionKey);
				return cfe.getEncryptionInputEncryptedStream(bis);
				
			} else {
				return new BufferedInputStream(new FileInputStream(file));
			}
		} else {
			if (isEncrypted) {
				CryptoFileEncryption cfe = new CryptoFileEncryption(encryptionKey);
				return cfe.getEncryptionInputEncryptedStream(mf.getInputStream());
			} else {
				return mf.getInputStream();
			}
		}
	}
	
	public void delete() throws IOException {
		if(file != null && isTempFile)
			FileHelper.delete(file);

		if(mf instanceof SimpleMultipartFile)
			((SimpleMultipartFile) mf).close();
	}

    public String getMd5() throws IOException {
        if (md5==null) {
            if (file!=null) {
                DigestOutputStream os = new DigestOutputStream(new NullOutputStream());
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(is, os);
                md5 = os.getDigest();
            } else if (mf instanceof SimpleMultipartFile) {
                md5 = ((SimpleMultipartFile)mf).getMd5();
            }
        }
        return md5;
    }

    public long getSize() throws IOException {
        if(file != null)
            return file.length();
        else
            return mf.getSize();
    }
	
	public SizeMd5Pair makeReentrant() throws IOException {
        if (file==null) {
            if (!(mf instanceof SimpleMultipartFile) || !((SimpleMultipartFile) mf).isReentrant()) {
                transferToTempFile();
            }
        }
        String md5 = getMd5();

		// Returns the length of the file
		if(file != null)
			return new SizeMd5Pair(file.length(), md5);
		else
			return new SizeMd5Pair(mf.getSize(), md5);
	}

	/*
	 * Return the backing file used to guarantee reentrancy. It is expected and required
	 * that makeReentrant() was called prior to using this method.
	 */
	public File getReentrantBackingFile() {
		if(file == null) {
			if(mf instanceof SimpleMultipartFile) {
				return ((SimpleMultipartFile)mf).getFile();
			}
			else {
				return null; 
			}
		}
		else {
			return file;
		}
	}
	
	public String getExpectedMd5() {
		if (mf instanceof FileExtendedSupport) {
			return ((FileExtendedSupport) mf).getExpectedMd5();
		}
		return null;
	}

    public boolean verifyCheckSum() throws IOException {
        if (mf instanceof FileExtendedSupport) {
            String expectedMd5 = ((FileExtendedSupport)mf).getExpectedMd5();
            if (expectedMd5!=null && !expectedMd5.equals(getMd5())) {
                return false;
            }
        }
        return true;
    }

    private void transferToTempFile() throws IOException {
        if(file == null) {
            file = TempFileUtil.createTempFile(TEMP_FILE_PREFIX);
            isTempFile = true;
            mf.transferTo(file);
            if (mf instanceof SimpleMultipartFile) {
                md5 = ((SimpleMultipartFile) mf).getMd5();
            }
        }
    }
	
	/**
	 * Returns modification date or <code>null</code>.
	 * 
	 * @return
	 */
	public Date getModDate() {
		if(mf instanceof FileExtendedSupport)
			return ((FileExtendedSupport) mf).getModDate();
		else
			return null;
	}

	public Long getModTime() {
		Date date = getModDate();
		if(date != null)
			return date.getTime();
		else
			return null;
	}
	
	public String getModifierName() {
		if(mf instanceof FileExtendedSupport)
			return ((FileExtendedSupport) mf).getModifierName();
		else
			return null;
	}
	public Long getModifierId() {
		if(mf instanceof FileExtendedSupport)
			return ((FileExtendedSupport) mf).getModifierId();
		else
			return null;
	}
	
	public String getCreatorName() {
		if(mf instanceof FileExtendedSupport)
			return ((FileExtendedSupport) mf).getCreatorName();
		else
			return null;
	}
	public Long getCreatorId() {
		if(mf instanceof FileExtendedSupport)
			return ((FileExtendedSupport) mf).getCreatorId();
		else
			return null;
	}
	
	public boolean isSynchToRepository() {
		return synchToRepository;
	}

	public void setSynchToRepository(boolean synchToRepository) {
		this.synchToRepository = synchToRepository;
	}

	/*
	 * Return the content length, if any, that the caller of this facility specified.
	 */
	public Long getCallerSpecifiedContentLength() {
		if(mf instanceof SimpleMultipartFile)
			return ((SimpleMultipartFile)mf).getCallerSpecifiedContentLength();
		else
			return null;
	}
	
	/*
	 * Return whether or not the caller of this facility is the file sync process
	 */
	public boolean calledByFileSync() {
		if(mf instanceof SimpleMultipartFile) {
			SimpleMultipartFile smf = (SimpleMultipartFile) mf;
			return (smf.getCallerSpecifiedContentLength() != null && !isSynchToRepository());
		}
		return false;
	}
}
