/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.docconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;

import org.springframework.util.FileCopyUtils;

/**
 * ?
 * 
 * @author ?
 */
public abstract class ImageConverter extends Converter<ImageConverter.Parameters> {
	private String m_defaultImage = "";
	
	private static final String SCALED_SUBDIR	= "scaled";
	private static final String THUMB_SUBDIR	= "thumb";
	private static final String IMG_FILE_SUFFIX	= ".jpg";

	/**
	 * Inner class used to ...
	 */
	public static class Parameters {
		private int m_width;	//
		private int m_height;	//

		/**
		 * Constructor method.
		 * 
		 * @param width
		 * @param height
		 */
		public Parameters(int width, int height) {
			m_width  = width;
			m_height = height;
		}
		
		public int getWidth()  {return m_width; }
		public int getHeight() {return m_height;}
	}
	
	/**
	 * Constructor method.
	 */
	public ImageConverter() {
		super(ObjectKeys.CONVERTER_DIR_IMAGE);
	}

	/**
	 * Returns the ConverterType of this converter.
	 * 
	 * @return
	 */
	@Override
	public ConverterType getConverterType() {
		return ConverterType.IMAGE;
	}
	
	/**
	 * Returns the suffix to use for cached files.
	 * 
	 * @return
	 */
	@Override public String getBaseFileSuffix()       {return IMG_FILE_SUFFIX;}
	@Override public String getCompressedFileSuffix() {return null;           }
	@Override public String getCachedFileSuffix()     {return IMG_FILE_SUFFIX;}	// Returns the one currently in use.

	/**
	 * Returns true if cached files are compressed and false otherwise.
	 * 
	 * @return
	 */
	@Override public boolean compressCachedFiles()           {return false;}
	@Override public boolean supportsCompressedCachedFiles() {return false;}
	
	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param parameters
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream convertToScaledImage(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
			throws IOException {
		return super.convert(binder, entry, fa, parameters, SCALED_SUBDIR, IMG_FILE_SUFFIX);
	}

	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param parameters
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream convertToThumbnail(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
			throws IOException {
		return super.convert(binder, entry, fa, parameters, THUMB_SUBDIR, IMG_FILE_SUFFIX);
	}

	/**
	 * ?
	 * 
	 * @param image_in
	 */
	public void setDefaultImage(String image_in) {
		m_defaultImage = image_in;
	}
	
	/**
	 * ?
	 * 
	 * @return
	 */
	public String getDefaultImage() {
		return m_defaultImage;
	}

	/**
	 * ?
	 * 
	 * @param convertedFile
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param filePath
	 * @param relativeFilePath
	 * @param parameters
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	@Override
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa, String filePath, String relativeFilePath, ImageConverter.Parameters parameters)
			throws IOException {
		// If the file attachment is encrypted, we have to do this in
		// two steps.
		if (!fa.isEncrypted()) {
			super.createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
		}
		else {
			String iPath = (filePath + "._convert_.jpg");
			File intermediateFile = getCacheFileStore().getFile(iPath);
			try {
				super.createCachedFile(intermediateFile, binder, entry, fa, filePath, relativeFilePath, parameters);
				OutputStream fos = new FileOutputStream(convertedFile);
				CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
				fos = cfe.getEncryptionOutputEncryptedStream(fos);
				try {
					InputStream fis = new FileInputStream(intermediateFile);
					FileCopyUtils.copy(fis, fos);
					fis.close();
				}
				finally {
					fos.close();
				}
			}
			finally {
				if ((null != intermediateFile) && intermediateFile.exists()) {
					intermediateFile.delete();
				}
			}
		}
	}

	/**
	 * ?
	 * 
	 * @param convertedFIle
	 * 
	 * @throws IOException
	 */
	@Override
	protected void createConvertedFileWithDefaultContent(File convertedFile)
			throws IOException {
		FileCopyUtils.copy(new File(m_defaultImage), convertedFile);
	}
}
