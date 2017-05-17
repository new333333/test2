/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.docconverter.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.metadata.Metadata;  
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;  

import org.kablink.teaming.docconverter.TextStreamConverter;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.util.SimpleProfiler;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.xml.sax.ContentHandler;

/**
 * Performs file conversion to search index text using Apache Tika.
 * 
 * *** Warning *** Warning *** Warning *** Warning *** Warning ***
 * ***                                                         ***
 * *** The Tika text converter as it currently stands does NOT ***
 * *** work!  The only dependencies for it that we're bringing ***
 * *** in are tika-core.jar and tika-parsers.jar.  In order    ***
 * *** for it to work, we need to use tika-app.jar instead     ***
 * *** (which brings in dependencies that break other things)  ***
 * *** or track down and bring in all the dependencies of      ***
 * *** tika-parsers.jar (with Tika 1.8, there were 78 of       ***
 * *** them.)                                                  ***  
 * ***                                                         ***  
 * *** Warning *** Warning *** Warning *** Warning *** Warning ***  
 * 
 * @author drfoster@novell.com
 */
public class TextTikaConverter extends TextStreamConverter implements TextTikaConverterMBean, InitializingBean, DisposableBean {
	private Detector		m_detector;			//
	private Parser			m_parser;			//
	private ParseContext	m_parserContext;	//
	
	// The following control which properties from the metadata are
	// included in the conversion output.
	private final static Property[] METADATA_PROPS_TO_INDEX = new Property[] {
		TikaCoreProperties.COMMENTS,
		TikaCoreProperties.DESCRIPTION,
		TikaCoreProperties.KEYWORDS,
		TikaCoreProperties.TITLE,
	};
	  
	/**
	 * Constructor method
	 */
	public TextTikaConverter() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
	    m_parserContext = new ParseContext();  
	    m_detector      = new DefaultDetector();  
	    m_parser        = new AutoDetectParser(m_detector);  
	    m_parserContext.set(Parser.class, m_parser);  
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}	
	
	/*
	 * Appends a metadata String to a StringBuffer. 
	 */
	private static void appendMDToSB(String md, StringBuffer sb) {
		// If we have a String to append...
		if ((null != md) && (0 < md.length())) {
			// ...append it.
			sb.append(" ");	// Leading space to separate it from everything else.
			sb.append(md);
		}
	}
	
	@Override
	public void destroy() throws Exception {	
		// Close the socket connections, ... that you established in
		// afterPropertiesSet().  Do any other cleanup stuff as
		// necessary. 
	}

	/**
	 * Run the conversion using the given InputStream and OutputStream.
	 *
	 * @param fileName		The name of the file being indexed.
	 * @param is			Input stream to read the file from.
	 * @param os			Output stream to write the text to.
	 * @param parameters	?
	 * 
	 * @throws Exception
	 */
	@Override
	public void convert(String fileName, InputStream is, OutputStream os, String parameters) throws Exception {
		long startTime = begin();

		// Should files with this extension be converted?
		String tmp = ("," + m_excludedExtensions + ",");
		if (!(tmp.contains("," + EntityIndexUtils.getFileExtension(fileName.toLowerCase() + ",")))) {
			// Yes!  Convert it...
			SimpleProfiler.start("TextTikaConverter.convert");
			
		    ContentHandler handler = new BodyContentHandler(os);  
		    Metadata md = new Metadata();  
		    m_parser.parse(is, handler, md, m_parserContext);
		    
		    // ...create a StringBuffer with the metadata...
		    StringBuffer sb = new StringBuffer();
	    	for (Property mp:  METADATA_PROPS_TO_INDEX) {
	    		if (md.isMultiValued(mp)) {
	    			String[] mdMulti = md.getValues(mp);
	    			if (null != mdMulti) {
	    				for (String mdEach:  mdMulti) {
	    	    			appendMDToSB(mdEach, sb);
	    				}
	    			}
	    		}
	    		else {
	    			appendMDToSB(md.get(mp), sb);
	    		}
	    	}
	    	
	    	// ...and if there is any...
	    	if (0 < sb.length()) {
			    // ...write the metadata to the output stream.
	    		os.write(sb.toString().getBytes());
	    	}
		    
			SimpleProfiler.stop("TextTikaConverter.convert");
		}
		
		end(startTime, fileName);
	}
	
	/**
	 * Run the conversion using the given InputStream and returns the
	 * text as a String.
	 *
	 * @param fileName		The name of the file being indexed.
	 * @param is			Input stream to read the file from.
	 * @param parameters	?
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	@Override
	public String convert(String fileName, InputStream inputStream, String parameters) throws Exception {
		OutputStream os = null;
		
		try {
			os = new ByteArrayOutputStream();
			convert(fileName, inputStream, os, parameters);
			return os.toString();
		}
		
		finally {
			if (null != os) {
				os.close();
				os = null;
			}
		}
		
	}
	
	/**
	 * Return the ssf*.properties key used to look for additional
	 * extensions to be excluded from text conversions.
	 * 
	 * @return
	 */
	@Override
	public String getAdditionalExclusionsKey() {
		return "exclude.from.tika.indexing.extensions";
	}
}
