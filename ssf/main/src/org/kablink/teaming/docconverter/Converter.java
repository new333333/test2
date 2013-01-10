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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.util.FileCharsetDetectorUtil;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.FilePathUtil;
import org.kablink.teaming.util.FileStore;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TempFileUtil;


public abstract class Converter<T>
{
	protected FileStore cacheFileStore;
	protected long maxTextLength;
	private int conversionTimeoutMS = 30000;
	private FileModule fileModule;
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final String TEXT_FILE_SUFFIX = ".txt";
	
	public Converter(String subDir) {
		cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"), subDir);
		maxTextLength = SPropsUtil.getLong("doc.max.text.extraction.size.threshold", 1048576);
		
		String to = SPropsUtil.getString("conversion.timeout.ms");
		if ((null != to) && (0 < to.length())) {
			try {
				int toi = Integer.valueOf(to).intValue();
				conversionTimeoutMS = toi;
			}
			catch (Exception e) {}
		}
	}
	
	protected FileModule getFileModule() {
		return fileModule;
	}
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}

	/*
	 * The vendor-specific subclasses (like TextStellentConverter, etc) will implement this method,
	 *   which does the actual file-to-file conversion.  Intermediate classes (like TextConverter) just
	 *   leave it undefined and declare themselves abstract.
	 */
	public abstract void convert(String origFileName, String ifp, String ofp, long timeout, T parameters)
		throws Exception;

	public abstract void deleteConvertedFile(Binder binder, DefinableEntity entry, FileAttachment fa)
	throws UncheckedIOException, RepositoryServiceException;
	
	protected void deleteConvertedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String subdir, String suffix)
			throws UncheckedIOException, RepositoryServiceException {
		String relativeFilePath = fa.getFileItem().getName();
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, relativeFilePath);
		String convertedFilePath = filePath + suffix;
		File convertedFile = cacheFileStore.getFile(convertedFilePath);
		if (convertedFile != null) {
			convertedFile.delete();
		}
	}

	private boolean shouldConvert(File convertedFile, FileAttachment fa) {
		if(!convertedFile.exists())
			return true; // This file was never converted.
		if(convertedFile.lastModified() < fa.getModification().getDate().getTime())
			return true; // The conversion took place "unambiguously" before the current version
		// This additional check takes care of the subtle edge condition reported in bug #715463.
		if(convertedFile.length() == 0 &&
				Integer.valueOf(1).equals(fa.getLastVersion()))
			return true;
		return false;
	}
	
	/*
	 * Direct subclasses (like TextConverter) will provide public convert() methods, which (for the most part)
	 *   will just pass their arguments along to this method, adding the sub-directory name and file extension
	 *   to be used in the conversion
	 */
	protected InputStream convert(Binder binder, DefinableEntity entry, FileAttachment fa, T parameters, String subdir, String suffix)
		throws IOException
	{
		String relativeFilePath = fa.getFileItem().getName();
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, relativeFilePath);
		String convertedFilePath = filePath + suffix;
		File convertedFile = cacheFileStore.getFile(convertedFilePath);

		if(shouldConvert(convertedFile, fa)) 
		{
			if(fa.getFileItem().getLength() > SPropsUtil.getLong("doc.conversion.size.threshold", 31457280L)) {
				convertedFile.delete();
				File parentDir = convertedFile.getParentFile();
				if(!parentDir.exists())
					parentDir.mkdirs();
				createConvertedFileWithDefaultContent(convertedFile);
			}
			else {
				createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
			}
		}

		if(convertedFile.exists()) {
			// check if it is a text file and too large
			if( convertedFile.getPath().endsWith(TEXT_FILE_SUFFIX) &&  convertedFile.length() > maxTextLength) {
				//concatenate the file
				shortenConvertedFile(convertedFile.getPath(), maxTextLength);
				convertedFile = cacheFileStore.getFile(convertedFilePath);
			} 
			InputStream fis =  new FileInputStream(convertedFile);
			if (fa.isEncrypted()) {
				//This cached file is encrypted, so add a decryptor to it
				CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
				fis = cfe.getEncryptionInputDecryptedStream(fis);
			}
			return fis;
		}	
		else
			throw new DocConverterException("Conversion failed");
	}

	private void shortenConvertedFile(String convertedFilePath, long maxLength) {
		try {

			RandomAccessFile raf = new RandomAccessFile(convertedFilePath, "rw");
			raf.setLength(maxLength); // truncate file
			raf.close();
		}
		catch (java.io.IOException ex) {
			//truncation didn't work, don't do anything
		}
	}
	
	protected abstract void createConvertedFileWithDefaultContent(File convertedFile) throws IOException;

	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
									String filePath, String relativeFilePath, T parameters)
		throws IOException
	{
		InputStream is = null;
		File copyOfOriginalFile = null;
		File tempConvertedFile = null;
		try
		{
			String timestamp = getTimestamp();
			
			File parentDir = convertedFile.getParentFile();
			if(!parentDir.exists())
				parentDir.mkdirs();

			is = getFileModule().readFile(binder, entry, fa);
			copyOfOriginalFile = TempFileUtil.createTempFileWithContent
			("s" + timestamp, getSuffix(new File(filePath)), cacheFileStore.getFile(filePath).getParentFile(), true, is);

			tempConvertedFile = TempFileUtil.createTempFile("t" + timestamp, getSuffix(convertedFile), convertedFile.getParentFile(), false);
			// correct encoding here
			if (filePath.endsWith(TEXT_FILE_SUFFIX)) {
				checkAndConvertEncoding(copyOfOriginalFile);
			}
			
    		long begin = System.nanoTime();
			convert(relativeFilePath, copyOfOriginalFile.getAbsolutePath(), tempConvertedFile.getAbsolutePath(), conversionTimeoutMS, parameters);
			end(begin, relativeFilePath);
			
			FileHelper.move(tempConvertedFile, convertedFile);
		}
		catch(IOException e) {
			throw e;
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new DocConverterException(e);
		}
		finally
		{
			if (is != null)
				is.close();
			if(copyOfOriginalFile != null && copyOfOriginalFile.exists()) {
				copyOfOriginalFile.delete();
			}
			if(tempConvertedFile != null && tempConvertedFile.exists()) {
				tempConvertedFile.delete();
			}
		}
	}

	protected void end(long begin, String fileName) {
		if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			logger.debug(diff + " ms, converting " + fileName);
		}	
	}
	
	private String getTimestamp() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	private String getSuffix(File file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if(index > -1)
			return name.substring(index);
		else
			return null;
	}
	
	protected InputStream getCachedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String fileName, String subdir)
		throws IOException
	{
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, fileName);
		File imageFile = cacheFileStore.getFile(filePath);
		return new FileInputStream(imageFile);
	}
	
	protected String checkAndConvertEncoding(File origFile) throws IOException {

		String encoding = "";
		// Get the encoding of the inputstream
		encoding = FileCharsetDetectorUtil.charDetect(origFile);
		String timestamp = getTimestamp();
		File tempEncodedFile = TempFileUtil.createTempFile("e" + timestamp, getSuffix(origFile), origFile.getParentFile(), true);
		FileCharsetDetectorUtil.convertEncoding(origFile, tempEncodedFile, encoding, "Unicode");
		FileHelper.move(tempEncodedFile, origFile);
		return encoding;
	}

}