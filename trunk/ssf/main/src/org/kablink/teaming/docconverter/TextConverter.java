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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.util.SimpleProfiler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


public abstract class TextConverter extends Converter<String> implements EntityResolver
{
	protected final Log logger = LogFactory.getLog(getClass());
	protected String _nullTransform = "";
	protected String excludedExtensions = "";
	private static final String TEXT_SUBDIR = "text",
		   TEXT_FILE_SUFFIX = ".txt";
	
	public String convert(Binder binder, DefinableEntity entry, FileAttachment fa)
		throws IOException
	{
		String result = "";
		String tmp = "," + excludedExtensions + ",";
		if(! tmp.contains("," + EntityIndexUtils.getFileExtension(fa.getFileItem().getName()).toLowerCase() + ",")) {
			SimpleProfiler.startProfiler("TextConverter.convert");
			InputStream textStream = super.convert(binder, entry, fa, null, TEXT_SUBDIR, TEXT_FILE_SUFFIX);
			StringWriter textWriter = new StringWriter();
			FileCopyUtils.copy(new InputStreamReader(textStream), textWriter);
			result = textWriter.toString();
			SimpleProfiler.stopProfiler("TextConverter.convert");
		}
		return result;
		
	}
	
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
			String filePath, String relativeFilePath, String parameters)
		throws IOException
	{
		String iPath = filePath + "._convert_.xml";
		File intermediateFile = cacheFileStore.getFile(iPath);
		try {
			super.createCachedFile(intermediateFile, binder, entry, fa, filePath, relativeFilePath, parameters);
			getTextFromXML(intermediateFile, getNullTransformFile(), new FileOutputStream(convertedFile));
		} catch (DocumentException de) {
			logger.warn("Failed to convert file: " + fa.getFileItem().getName() + " in Binder: " + binder.getPathName(), de);
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
    	Locale l = Locale.getDefault();
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
		try {
			//load singleton with our config file
			return new ClassPathResource(_nullTransform).getFile();
		}
        catch (Exception e) {
        	Log logger = LogFactory.getLog(getClass());
        	logger.error("DocConverter, transform file error: " + e.getLocalizedMessage());
        }
		return null;
	}
	
	protected void createConvertedFileWithDefaultContent(File convertedFile) throws IOException {
		// simply create an empty file
		convertedFile.createNewFile();
	}

}
