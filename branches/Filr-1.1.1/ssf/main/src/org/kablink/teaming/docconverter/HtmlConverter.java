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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.util.NLT;

/**
 * ?
 * 
 * @author ?
 */
public abstract class HtmlConverter extends Converter<String> {
	private   static final String HTML_SUBDIR			= "html";
	protected static final String HTML_PUBLIC_SUBDIR	= "html_public_";
	private   static final String HTML_FILE_SUFFIX		= ".html";

	/**
	 * Constructor method.
	 */
	public HtmlConverter() {
		super(ObjectKeys.CONVERTER_DIR_HTML);
	}
    
	/**
	 * Returns the ConverterType of this converter.
	 * 
	 * @return
	 */
	@Override
	public ConverterType getConverterType() {
		return ConverterType.HTML;
	}
	
	/**
	 * Returns the suffix to use for cached files.
	 * 
	 * @return
	 */
	@Override public String getBaseFileSuffix()       {return HTML_FILE_SUFFIX;}
	@Override public String getCompressedFileSuffix() {return null;            }
	@Override public String getCachedFileSuffix()     {return HTML_FILE_SUFFIX;}	// Returns the one currently in use.

	/**
	 * Returns true if cached files are compressed and false otherwise.
	 * 
	 * @return
	 */
	@Override public boolean compressCachedFiles()           {return false;}
	@Override public boolean supportsCompressedCachedFiles() {return false;}
	
    /*
     * Alter tag data held in HTML file.  We need to alter Image and
     * URL file path information to reflect were the image or files
     * actually reside on the server.  After converting a file into
     * HTML, the Image and URL file paths are specified to be relative
     * to the HTML just generated.  We must change these entries so
     * that we can recall these items and stream items into browser.
     */
    private StringBuffer alterTagData(String indata, String tag, String attrtag, String newdata) {
		String altdata  = "";
		String data     = "";
		String imageurl = "";
		String predata  = "";
		String s        = "";
		String src      = "";
		
		StringBuffer buffer = new StringBuffer();
		String[] splits = indata.split(tag);
        for (int x = 0; x < splits.length; x += 1) {
        	s = splits[x];
        	int i = s.indexOf(attrtag);
        	if (i > -1) {
        		predata = s.substring(0, i);
        		data = s.substring(i + attrtag.length());
        		imageurl = data.substring(0, data.indexOf("\""));
        		if (imageurl.startsWith("#") ||
        				imageurl.startsWith("http:") ||
        				imageurl.startsWith("https:")) {
        			src = imageurl;
        		}
        		else {
            		try {
            			imageurl = URLEncoder.encode(imageurl, "UTF-8");
            		}
            		catch(UnsupportedEncodingException e) {/* Ignored. */}
        			src = (newdata + imageurl);
        		}
        		
        		altdata  = (tag + predata);
        		altdata += (attrtag + (src + "\"" + data.substring(data.indexOf("\"") + 1)));
        		buffer.append(altdata);
        	}
        	else {
	        	// We could have a file like (ex) this is a test
        		// <a name='rsordillo' /> for testing we would not want
        		// to add 'tag' to beginning of file.
	        	if (0 == x)
	        	     buffer.append(s);
	        	else buffer.append(tag + s);
        	}
        }
        
        return buffer;
	}
	
    /**
     * Parse HTML file replacing URL an IMAGE paths to conform with
     * were the actual Images or Url files exist on the system.
     * 
     * @param fin			Input file to be adjusted
     * @param fout			Output file after adjustments have been made
     * @param attrdata		What attribute data to change if required
     * 
     * @throws Exception	Something goes wrong with parsing/changing input file
     */
	public void parseHtml(File fin, File fout, String attrdata) throws Exception {
		int				length   = 2048;
		FileReader		fr       = null;
		FileWriter		fw       = null;
		char[]			cbuf     = new char[length];
		StringBuffer	buffer   = null;
		String			fileData = "";
		
		try {
			buffer = new StringBuffer();
			
			fr = new FileReader(fin);			
			while (fr.read(cbuf, 0, length) > -1) {
				buffer.append(cbuf);
				
				// Clear buffer.
				for (int x = 0; x < cbuf.length; x += 1) {
					cbuf[x] = '\0';
				}
			}
			fileData = buffer.toString().trim();
			fr.close();
	
			buffer = alterTagData(fileData, "<img ", "src=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<IMG ", "SRC=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<a ", "href=\"",  attrdata.replaceAll("XXXX", "url"));
			buffer = alterTagData(buffer.toString(), "<A ", "HREF=\"",  attrdata.replaceAll("XXXX", "url"));
			
			fw = new FileWriter(fout);
	        fw.write(buffer.toString());
		}
		finally {
			try {
				if (fr != null) {
					fr.close();
				}
			}
			catch (Exception e) {/* Ignored. */}
			
			try {
				if (fw != null) {
					fw.flush();
					fw.close();
				}
			}
			catch (Exception e) {/* Ignored. */}
		}
        
        return;
	}

	/*
	 */
	private void cleanUpURLs(String outFile, String url, Long binderId, Long entryId, String entityType, String fileId) throws IOException {
		try {
			int j = outFile.lastIndexOf(File.separator);
			
			//outFile = outFile.substring(0, j+1) + fileId + File.separator + outFile.substring(j+1);
			
			// When generating the HMTL equivalent file.
			// Many HTML files can be generated. Open file(s) and make
			// adjustments to image src attribute.
			// Every HTML file in directory should be related to
			// converter process.
			File outputDir = new File(outFile.substring(0, j+1));
			if (outputDir.isDirectory()) {
				String src = (url + "?binderId=" + binderId + "&entryId=" + entryId + "&entityType=" + entityType + "&fileId=" + fileId + "&viewType=XXXX&filename=");
				File[] files = outputDir.listFiles();
				for (int x = 0; x < files.length; x += 1) {
					if (files[x].isFile() && files[x].getName().endsWith(".html")) {
						parseHtml(files[x], files[x], src);
					}
				}
			}
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
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
	 * @param url
	 * 
	 * @throws IOException
	 */
	@Override
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa, String filePath, String relativeFilePath, String url)
			throws IOException {
		super.createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, url);
		cleanUpURLs(convertedFile.getAbsolutePath(), url, binder.getId(), entry.getId(), entry.getEntityType().name(), fa.getId());
	}

	/**
	 * ?
	 *  
	 * @param url
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream convert(String url, Binder binder, DefinableEntity entry, FileAttachment fa) throws IOException {
		if (fa.isEncrypted()) {
			throw new DocConverterException(NLT.get("html.converterError.encrypted"));
		}
		else {
			return super.convert(binder, entry, fa, url, HTML_SUBDIR, HTML_FILE_SUFFIX);
		}
	}

	/**
	 * ?
	 * 
	 * @param url
	 * @param shareItem
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream convert(String url, ShareItem shareItem, Binder binder, DefinableEntity entry, FileAttachment fa)
			throws IOException {
		if (fa.isEncrypted()) {
			throw new DocConverterException(NLT.get("html.converterError.encrypted"));
		}
		else {
			String subDir = (HTML_PUBLIC_SUBDIR + String.valueOf(shareItem.getId()));
			return super.convert(binder, entry, fa, url, subDir, HTML_FILE_SUFFIX);
		}
	}

	/**
	 * ?
	 * 
	 * @param binder
	 * @param entity
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void deleteCacheHtmlFile(Binder binder, DefinableEntity entity, FileAttachment fa)
			throws UncheckedIOException, RepositoryServiceException {
		super.deleteConvertedFile(binder, entity, fa, HTML_SUBDIR, HTML_FILE_SUFFIX);
	}

	/**
	 * ?
	 * 
	 * @param shareItem
	 * @param binder
	 * @param entity
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void deleteCacheHtmlFile(ShareItem shareItem, Binder binder, DefinableEntity entity, FileAttachment fa)
			throws UncheckedIOException, RepositoryServiceException {
		String subDir = HTML_PUBLIC_SUBDIR + String.valueOf(shareItem.getId());
		super.deleteConvertedFile(binder, entity, fa, subDir, HTML_FILE_SUFFIX);
	}

	/**
	 * ?
	 * 
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param fileName
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream getCachedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String fileName)
			throws IOException {
		return getCachedFile(binder, entry, fa, fileName, HTML_SUBDIR);
	}

	/**
	 * ?
	 * 
	 * @param shareItem
	 * @param binder
	 * @param entry
	 * @param fa
	 * @param fileName
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream getCachedFile(ShareItem shareItem, Binder binder, DefinableEntity entry, FileAttachment fa, String fileName)
			throws IOException {
		String subDir = (HTML_PUBLIC_SUBDIR + String.valueOf(shareItem.getId()));
		return getCachedFile(binder, entry, fa, fileName, subDir);
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
		// Simply create an empty file.
		convertedFile.createNewFile();
	}
}
