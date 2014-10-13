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
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.util.FileCharsetDetectorUtil;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.FilePathUtil;
import org.kablink.teaming.util.FileStore;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TempFileUtil;

/**
 * ?
 * 
 * @author ?
 */
public abstract class Converter<T> {
	protected Log		logger = LogFactory.getLog(getClass());
	
	private FileModule	m_fileModule;					//
	private	FileStore	m_cacheFileStore;				//
	private int			m_conversionTimeoutMS = 30000;	//
	private int			m_GZipBufferSize;				//
	private long		m_emptyCachedFileLength;		//
	private long		m_maxTextEncodeThreshold;		//
	private long		m_maxTextLength;				//
	private String		m_cacheSubDir;					//
	
	private static final int	MIN_GZIP_BUFFER_SIZE		=   512;	// This is the default used by these streams when no size is specified in their constructor.
	private static final int	DEFAULT_GZIP_BUFFER_SIZE	= 65536;	// I got this value from www.java-performance.com
	
	/**
	 * Enumeration type the defines the type of converter that's
	 * extending this class.
	 */
	public enum ConverterType {
		HTML,
		IMAGE,
		TEXT,
		
		UNKNOWN;
		
		public boolean isHtml()    {return this.equals(HTML );}
		public boolean isImage()   {return this.equals(IMAGE);}
		public boolean isText()    {return this.equals(TEXT );}
		public boolean isUnknown() {return this.equals(TEXT );}
	}

	/**
	 * Constructor method.
	 * 
	 * @param subDir
	 */
	public Converter(String subDir) {
		m_cacheSubDir            = subDir;
		m_cacheFileStore         = new FileStore(SPropsUtil.getString("cache.file.store.dir"),                  m_cacheSubDir);
		m_maxTextLength          =               SPropsUtil.getLong(  "doc.max.text.extraction.size.threshold", 1048576L     );
		m_maxTextEncodeThreshold =               SPropsUtil.getLong(  "doc.max.txt.encode.size.threshold",      1048576L     );
		if (0 >= m_maxTextEncodeThreshold) {
			m_maxTextEncodeThreshold = Long.MAX_VALUE;
		}
		
		if (compressCachedFiles())
		     m_emptyCachedFileLength = 20l;
		else m_emptyCachedFileLength =  0l;
		m_GZipBufferSize = SPropsUtil.getInt("conversion.compress.buffer.size", DEFAULT_GZIP_BUFFER_SIZE);
		if (MIN_GZIP_BUFFER_SIZE > m_GZipBufferSize) {
			m_GZipBufferSize = MIN_GZIP_BUFFER_SIZE;
		}
		
		String to = SPropsUtil.getString("conversion.timeout.ms");
		if ((null != to) && (0 < to.length())) {
			try {
				int toi = Integer.valueOf(to).intValue();
				m_conversionTimeoutMS = toi;
			}
			catch (Exception e) {/* Ignored. */}
		}
	}

	/**
	 * Returns the ConverterType of this converter.
	 * 
	 * @return
	 */
	public abstract ConverterType getConverterType();
	
	/**
	 * Returns the suffix to use for cached files.
	 * 
	 * @return
	 */
	public abstract String getBaseFileSuffix();			// Returns the suffix if cached files are not being compressed.
	public abstract String getCompressedFileSuffix();	// Returns the suffix if cached files are     being compressed.
	public abstract String getCachedFileSuffix();		// Returns the suffix currently in use (one of the two above.)

	/**
	 * ?
	 *  
	 * @return
	 */
	protected FileModule getFileModule() {
		return m_fileModule;
	}
	
	/**
	 * ?
	 *  
	 * @param fileModule
	 */
	public void setFileModule(FileModule fileModule) {
		m_fileModule = fileModule;
	}

	/**
	 * Returns information about whether the converter handles
	 * compressing cached files.
	 * 
	 * @return
	 */
	public abstract boolean compressCachedFiles();
	public abstract boolean supportsCompressedCachedFiles();
	
	/**
	 * Returns the length of an empty cached file.
	 * 
	 * @return
	 */
	public final long getEmptyCachedFileLength() {
		return m_emptyCachedFileLength;
	}
	
	/**
	 * Returns the buffer size to use for buffering
	 * GZIPInput/OutputStream's.
	 * 
	 * @return
	 */
	public final int getGZipBufferSize() {
		return m_GZipBufferSize;
	}

	/**
	 * Returns the cache FileStore for this converter.
	 * 
	 * @return
	 */
	public final FileStore getCacheFileStore() {
		return m_cacheFileStore;
	}

	/**
	 * Returns the sub-directory cached files are stored in for this
	 * converter.
	 * 
	 * @return
	 */
	public final String getCacheSubDir() {
		return m_cacheSubDir;
	}
	
	/**
	 * The vendor-specific subclasses (like TextStellentConverter, etc)
	 * will implement this method, which does the actual file-to-file
	 * conversion.  Intermediate classes (like TextConverter) just
	 * leave it undefined and declare themselves abstract.
	 * 
	 * @param origFileName
	 * @param ifp
	 * @param ofp
	 * @param timeout
	 * @param parameters
	 * 
	 * @throws Exception
	 */
	public abstract void convert(String origFileName, String ifp, String ofp, long timeout, T parameters) 
		throws Exception;

	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public abstract void deleteConvertedFile(Binder binder, DefinableEntity entry, FileAttachment fa) 
		throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * ?
	 * 
	 * @param shareItem
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public abstract void deleteConvertedFile(ShareItem shareItem, Binder binder, DefinableEntity entry, FileAttachment fa)
		throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param subdir
	 * @param suffix
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	protected void deleteConvertedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String subdir, String suffix)
			throws UncheckedIOException, RepositoryServiceException {
		String relativeFilePath = fa.getFileItem().getName();
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, relativeFilePath);
		String convertedFilePath = filePath + suffix;
		File convertedFile = getCacheFileStore().getFile(convertedFilePath);
		if (convertedFile != null) {
			convertedFile.delete();
		}
	}

	/*
	 * Given a previous converted cached file and a file attachment,
	 * determines whether the file needs to be converted (again.) 
	 */
	private boolean shouldConvert(File convertedFile, FileAttachment fa) {
		if (!convertedFile.exists()) {
			logger.debug("Conversion required because there is no previous converted file '" + convertedFile.getName() + "'.");
			return true; // This file was never converted.
		}
		if (convertedFile.lastModified() < fa.getModification().getDate().getTime()) {
			logger.debug("Conversion required because last conversion occurred before the current version of the file '" + convertedFile.getName() + "'.");
			return true; // The conversion took place "unambiguously" before the current version
		}
		/*
		 * 06042014 JK - Testing can no longer reproduce the condition that the following block of
		 * code was written to deal with. Instead, this code only seems to get executed for files
		 * added via non-WebDAV clients. For that reason, I'm declaring that this block of code
		 * is no longer needed and I'm commenting it out.
		// This additional check takes care of the subtle edge condition reported in bug #715463.
		if (convertedFile.length() == getEmptyCachedFileLength() &&
				Integer.valueOf(1).equals(fa.getLastVersion())  &&
				getConverterType().isText()) {
			logger.debug("Conversion required because of webdav mapped folder edge case (bug #715463) '" + convertedFile.getName() + "'.");
			return true;
		}
		*/
		return false;
	}
	
	/**
	 * Direct subclasses (like TextConverter) will provide public
	 * convert() methods, which (for the most part) will just pass
	 * their arguments along to this method, adding the sub-directory
	 * name and file extension to be used in the conversion
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param parameters
	 * @param subdir
	 * @param suffic
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	protected InputStream convert(Binder binder, DefinableEntity entry, FileAttachment fa, T parameters, String subdir, String suffix)
			throws IOException {
		String relativeFilePath = fa.getFileItem().getName();
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, relativeFilePath);
		String convertedFilePath = filePath + suffix;
		File convertedFile = getCacheFileStore().getFile(convertedFilePath);
		boolean compressCachedFiles = compressCachedFiles();
		boolean shouldConvert = shouldConvert(convertedFile, fa);
		if (shouldConvert) {
			if ((!(convertedFile.exists())) && supportsCompressedCachedFiles()) {
				String convertedFilePath2;
				if (compressCachedFiles())											// If  compressing...
				     convertedFilePath2 = (filePath + getBaseFileSuffix());			// ...check for an uncompressed file...
				else convertedFilePath2 = (filePath + getCompressedFileSuffix());	// ...otherwise, check for a compressed file.
				File convertedFile2 = getCacheFileStore().getFile(convertedFilePath2);
				
				// Does a file of the opposite type exist?
				if (convertedFile2.exists()) {
					// Yes!  Is it still valid?
					shouldConvert = shouldConvert(convertedFile2, fa);
					if (shouldConvert) {
						// Yes!  Delete convertedFile2 since it's no
						// longer useful.  If we can't delete it,
						// simply log the error and continue.
						logger.debug("Deleting unused cached file '" + convertedFile2.getName() + "'.  Using '" + convertedFile.getName() + "' instead.");
						try                  {convertedFile2.delete();                                                                      }
						catch (Exception ex) {logger.error("Could not delete the unused cached file '" + convertedFile2.getName() + "'.");}
					}
					else {
						// Yes, it's still valid!  Switch and use
						// the other file type.
						logger.debug("Switching cached file to '" + convertedFile2.getName() + "' instead of '" + convertedFile.getName() + "'.");
						convertedFile       = convertedFile2;
						compressCachedFiles = (!compressCachedFiles);
					}
				}
			}
		}

		if (shouldConvert) 
		{
			if (fa.getFileItem().getLength() > SPropsUtil.getLong("doc.conversion.size.threshold", 31457280L)) {
				convertedFile.delete();
				File parentDir = convertedFile.getParentFile();
				if (!parentDir.exists())
					parentDir.mkdirs();
				createConvertedFileWithDefaultContent(convertedFile);
				if(logger.isDebugEnabled()) {
					logger.debug("Conversion Skipped, file exceeds 'doc.conversion.size.threshold'! " + fa.getFileItem().getName());
				}
			}
			else {
				createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
			}
		}

		if (convertedFile.exists()) {
			// If a plain text cached file is too large...
			if ((!(compressCachedFiles)) && getConverterType().isText() && convertedFile.getPath().endsWith(getCachedFileSuffix()) &&  convertedFile.length() > m_maxTextLength) {
				// ...concatenate it.
				shortenConvertedFile(convertedFile.getPath(), m_maxTextLength);
				convertedFile = getCacheFileStore().getFile(convertedFilePath);
			} 
			InputStream fis = new FileInputStream(convertedFile);
			if (compressCachedFiles) {
				fis = new GZIPInputStream(fis, getGZipBufferSize());
			}
			// If the cached file is to be encrypted...
			if (fa.isEncrypted()) {
				// ...add a decryptor to it.
				CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
				fis = cfe.getEncryptionInputDecryptedStream(fis);
			}
			return fis;
		}	
		else {
			throw new DocConverterException("Conversion failed");
		}
	}

	/*
	 */
	private void shortenConvertedFile(String convertedFilePath, long maxLength) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(convertedFilePath, "rw");
			raf.setLength(maxLength); // Truncate file.
			raf.close();
			raf = null;
		}
		catch (java.io.IOException ex) {
			// Truncation didn't work, don't do anything.
		}
		finally {
			if (null != raf) {
				try {
					raf.close();
				} catch(Exception e) {/* Ignored. */}
			}
		}
	}

	/**
	 * ?
	 * 
	 * @param convertedFile
	 * 
	 * @throws IOException
	 */
	protected abstract void createConvertedFileWithDefaultContent(File convertedFile)
		throws IOException;

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
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa, String filePath, String relativeFilePath, T parameters)
			throws IOException {
		InputStream is                 = null;
		File        copyOfOriginalFile = null;
		File        tempConvertedFile  = null;
		try {
			String timestamp = getTimestamp();
			
			File parentDir = convertedFile.getParentFile();
			if (!(parentDir.exists())) {
				parentDir.mkdirs();
			}

			is = getFileModule().readFile(binder, entry, fa);
			copyOfOriginalFile = TempFileUtil.createTempFileWithContent(
				("s" + timestamp),
				getSuffix(new File(filePath)),
				getCacheFileStore().getFile(filePath).getParentFile(),
				true,
				is);

			tempConvertedFile = TempFileUtil.createTempFile(
				("t" + timestamp),
				getSuffix(convertedFile),
				convertedFile.getParentFile(),
				false);
			
			// Correct text file encoding here.
			if (getConverterType().isText() && filePath.endsWith(getBaseFileSuffix())) {
				checkAndConvertEncoding(copyOfOriginalFile, fa.getFileItem().getName());
			}
			
    		long begin = System.nanoTime();
			convert(relativeFilePath, copyOfOriginalFile.getAbsolutePath(), tempConvertedFile.getAbsolutePath(), m_conversionTimeoutMS, parameters);
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
		finally {
			if (is != null) {
				is.close();
			}
			if ((null != copyOfOriginalFile) && copyOfOriginalFile.exists()) {
				copyOfOriginalFile.delete();
			}
			if ((null != tempConvertedFile) && tempConvertedFile.exists()) {
				tempConvertedFile.delete();
			}
		}
	}

	/**
	 * ?
	 * 
	 * @param begin
	 * @param fileName
	 */
	protected void end(long begin, String fileName) {
		if (logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			logger.debug(diff + " ms, converting '" + fileName + "'.");
		}	
	}

	/*
	 */
	private String getTimestamp() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	/*
	 */
	private String getSuffix(File file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index > -1)
		     return name.substring(index);
		else return null;
	}

	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param fileName
	 * @param subdir
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	protected InputStream getCachedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String fileName, String subdir)
			throws IOException {
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, fileName);
		File imageFile = getCacheFileStore().getFile(filePath);
		return new FileInputStream(imageFile);
	}

	/**
	 * ?
	 * 
	 * @param origFile
	 * 
	 * @throws IOException
	 */
	protected void checkAndConvertEncoding(File origFile, String baseFileName) throws IOException {
		// If the file is larger than our maximum plain text encoding
		// size...
		if (origFile.length() > m_maxTextEncodeThreshold) {
			// ...simply bail and don't mess with the file's encoding.
			if(logger.isDebugEnabled()) {
				logger.debug("Text Encoding Skipped, file exceeds 'doc.max.txt.encode.size.threshold'! " + baseFileName);
			}
			return;
		}
		
		// Get the encoding of the input stream.
		String encoding = FileCharsetDetectorUtil.charDetect(origFile);
		String timestamp = getTimestamp();
		File tempEncodedFile = TempFileUtil.createTempFile("e" + timestamp, getSuffix(origFile), origFile.getParentFile(), true);
		FileCharsetDetectorUtil.convertEncoding(origFile, tempEncodedFile, encoding, "Unicode");
		FileHelper.move(tempEncodedFile, origFile);
	}
}
