/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.DocumentException;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.impl.CryptoFileEncryption;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * ?
 * 
 * @author ?
 */
public abstract class TextConverter extends Converter<String> implements EntityResolver, InitializingBean
{
	protected String _nullTransform = "";
	protected String excludedExtensions = "";
	protected static final String TEXT_SUBDIR = "text",
		   TEXT_FILE_SUFFIX = ".txt";

	public TextConverter() {
		super(ObjectKeys.CONVERTER_DIR_TEXT);
	}

	private String[] m_additionalExclusions = null;
	
	private File nullTransformFile = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		nullTransformFile = new ClassPathResource(_nullTransform).getFile();
	}
	
	public String convert(Binder binder, DefinableEntity entry, FileAttachment fa)
		throws IOException
	{
		long startTime = System.nanoTime();

		String result = "";
		String tmp = "," + excludedExtensions + ",";
		if(! tmp.contains("," + EntityIndexUtils.getFileExtension(fa.getFileItem().getName()).toLowerCase() + ",")) {
			SimpleProfiler.start("TextConverter.convert");
			InputStream textStream = super.convert(binder, entry, fa, null, TEXT_SUBDIR, TEXT_FILE_SUFFIX);
			StringWriter textWriter = new StringWriter();
			FileCopyUtils.copy(new InputStreamReader(textStream), textWriter);
			result = textWriter.toString();
			SimpleProfiler.stop("TextConverter.convert");
		}
		
		end(startTime, fa.getFileItem().getName());
		
		return result;
		
	}
	
	@Override
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
			String filePath, String relativeFilePath, String parameters)
		throws IOException
	{
		String iPath = filePath + "._convert_.xml";
		File intermediateFile = cacheFileStore.getFile(iPath);
		try {
			super.createCachedFile(intermediateFile, binder, entry, fa, filePath, relativeFilePath, parameters);
			OutputStream fos = new FileOutputStream(convertedFile);
			if (fa.isEncrypted()) {
				CryptoFileEncryption cfe = new CryptoFileEncryption(fa.getEncryptionKey());
				fos = cfe.getEncryptionOutputEncryptedStream(fos);
			}
			try {
				getTextFromXML(intermediateFile, getNullTransformFile(), fos);
			}
			finally {
				fos.close();
			}
		} catch (DocumentException de) {
			String deMsg = de.toString();
			logger.warn("Failed to convert file: " + fa.getFileItem().getName() + " in Binder: " + binder.getPathName() + ":  " + deMsg);
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to convert file: " + fa.getFileItem().getName() + " in Binder: " + binder.getPathName() + ":EXCEPTION", de);
			}
		}
		finally
		{
			if(intermediateFile != null && intermediateFile.exists()) {
				intermediateFile.delete();
			}
		}
	}

	// Bugzilla 480931:  View and Edit Buttons for documents are not working.
	// Bugzilla 524410:  Depending on version of OO, XHTML sometimes cannot be parsed.
	//
	// In researching these issues, I found the following web site:
	//      http://forums.java.net/jive/thread.jspa?threadID=38493
	// which describes one solution which is to always replace the
	// DTD's with references to an empty XML document.
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
	
	protected org.dom4j.Document getDomDocument(File textFile) throws DocumentException {
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		
		// Bugzilla 480931:  View and Edit Buttons for documents are not working.
		// Bugzilla 524410:  Depending on version of OO, XHTML sometimes cannot be parsed.
		//
		// See resolveEntity() in this module for what this does.
		reader.setEntityResolver(this);
		return reader.read(textFile);	
	}
	
	protected void getTextFromXML(File ofile, File transformFile, OutputStream out) throws DocumentException
    {	
    	Locale l = NLT.getTeamingLocale();
		Templates trans;
		Transformer tranny = null;
		org.dom4j.Document tempfile = null;
		if(ofile.exists()) { 
			tempfile = getDomDocument(ofile);
		}
		if(tempfile != null) {
			try {
				TransformerFactory transFactory = TransformerFactory.newInstance();

				Source s = new StreamSource(transformFile);
				trans = transFactory.newTemplates(s);
				tranny =  trans.newTransformer();
			} catch (TransformerConfigurationException tce) {}

			StreamResult result = new StreamResult(out);
			try {
				tranny.setParameter("Lang", l);
				tranny.transform(new DocumentSource(tempfile), result);
			} catch (Exception ex) {
			}
		}
	}

	public String getExcludedExtensions()
	{
		return excludedExtensions;
	}
	public void setExcludedExtensions(String excludedExtensions)
	{
		// Are there any additional exclusions specified in the
		// ssf*.properties files?
		String[] additionalExclusions = getAdditionalExclusions();
		int c = ((null == additionalExclusions) ? 0 : additionalExclusions.length);
		if (0 < c) {
			// Yes!  Scan them...
			StringBuffer eeBuf = new StringBuffer((null == excludedExtensions) ? "" : excludedExtensions);
			for (int i = 0; i < c; i +=1) {
				// ...appending each to the excluded extensions.
				if ((0 < i) || (0 < eeBuf.length())) {
					eeBuf.append(",");
				}
				eeBuf.append(additionalExclusions[i]);
			}
			excludedExtensions = eeBuf.toString();
		}
		this.excludedExtensions = excludedExtensions;
	}
	
	/**
	 * @return Returns the nullTransform.
	 */
	public String getNullTransform() {
		return _nullTransform;
	}

	/**
	 * @param nullTransform The nullTransform to set.
	 */
	public void setNullTransform(String nullTransform) {
		_nullTransform = nullTransform;
	}

	/**
	 * @return Returns the nullTransform file.
	 */
	protected File getNullTransformFile() {
		return nullTransformFile;
	}
	
	@Override
	protected void createConvertedFileWithDefaultContent(File convertedFile) throws IOException {
		// simply create an empty file
		convertedFile.createNewFile();
	}

	/**
	 * By default, there are no additional exclusions.  Each class that
	 * extends this class this may define one, however.
	 * 
	 * @return
	 */
	public String getAdditionalExclusionsKey() {
		return null;
	}
	
	// Returns a String[] of any additional file extensions that are to
	// be excluded from OpenOffice text conversions specified in the
	// ssf*.properties files.
	private String[] getAdditionalExclusions() {
		initAdditionalExclusions();
		return m_additionalExclusions;
	}

	/*
	 * Initializes the static array of extensions that are to be
	 * excluded by the OpenOffice text converter.
	 */
	private void initAdditionalExclusions() {
		// If we've already initialized the excluded extensions...
		if (null != m_additionalExclusions) {
			// ...bail.
			return;
		}
		

		// If there is no key defined to access any additional
		// extensions in the ssf*.properties files...
		String key = getAdditionalExclusionsKey();
		if ((null == key) || (0 == key.length())) {
			// ...define an empty array of them.
			m_additionalExclusions = new String[0];
			return;
		}

		// Are there any excluded extensions specified in the
		// ssf*.properties files?
		String excludeThese = SPropsUtil.getString(key, "");
		if ((null != excludeThese) && (0 < excludeThese.length())) {
			// Yes!  Extract and count them...
			m_additionalExclusions = excludeThese.toLowerCase().split(",");
			int count = ((null == m_additionalExclusions) ? 0 : m_additionalExclusions.length);
			
			// ...and ensure none of them start with a period.
			for (int i = 0; i < count; i += 1) {
				String excludeThis = m_additionalExclusions[i];
				if (excludeThis.startsWith(".")) {
					m_additionalExclusions[i] = excludeThis.substring(1);
				}
			}
		}
		else {
			// No, there aren't any excluded extensions specified!
			m_additionalExclusions = new String[0];
		}
	}
}
