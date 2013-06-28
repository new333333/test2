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
package org.kablink.teaming.docconverter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.util.FileUtil;
import org.springframework.util.FileCopyUtils;


public abstract class ImageConverter extends Converter<ImageConverter.Parameters>
{
	protected String _defaultImage = "";
	protected String _nullTransform = "";
	protected static final String SCALED_SUBDIR = "scaled";
	protected static final String THUMB_SUBDIR = "thumb";
	protected static final String IMG_FILE_SUFFIX = ".jpg";

	public ImageConverter() {
		super(ObjectKeys.CONVERTER_DIR_IMAGE);
	}

	public InputStream convertToScaledImage(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
	throws IOException
{
	return super.convert(binder, entry, fa, parameters, SCALED_SUBDIR, IMG_FILE_SUFFIX);
}

	public InputStream convertToThumbnail(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
	throws IOException
{
	return super.convert(binder, entry, fa, parameters, THUMB_SUBDIR, IMG_FILE_SUFFIX);
}

	public void setDefaultImage(String image_in)
	{
		_defaultImage = image_in;
	}
	
	public String getDefaultImage()
	{
		return _defaultImage;
	}
	
	public static class Parameters
	{
		int width;
		int height;
		
		public Parameters(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() { return width; }
		public int getHeight() { return height; }
	}

	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
			String filePath, String relativeFilePath, ImageConverter.Parameters parameters)
		throws IOException
	{
		//If the file attachment is encrypted, we have to do this in two steps
		if (!fa.isEncrypted()) {
			super.createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
		} else {
			String iPath = filePath + "._convert_.jpg";
			File intermediateFile = cacheFileStore.getFile(iPath);
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
			finally
			{
				if(intermediateFile != null && intermediateFile.exists()) {
					intermediateFile.delete();
				}
			}
		}
	}

	protected void createConvertedFileWithDefaultContent(File convertedFile) throws IOException {
		FileCopyUtils.copy(new File(_defaultImage), convertedFile);
	}


}
