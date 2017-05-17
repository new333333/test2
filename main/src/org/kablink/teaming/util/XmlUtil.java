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
package org.kablink.teaming.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * ?
 * 
 * @author ?
 */
public class XmlUtil {
	protected static Log m_logger = LogFactory.getLog(XmlUtil.class);
	
	/**
	 * ?
	 * 
	 * @param doc
	 * 
	 * @return
	 */
	public static String asPrettyString(Document doc) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(doc);
			return new String(out.toByteArray(), "UTF-8");
		}
		catch(IOException e) {
			return "";
		}
	}
	
	/**
	 * Constructs and returns a 'safe' SAXReader.
	 * 
	 * @return
	 */
	public static SAXReader getSAXReader() {
		return fixSAXReaderSecurity(new SAXReader());
	}
	
	/**
	 * Constructs and returns a 'safe' SAXReader.
	 *
	 * @param validating
	 * 
	 * @return
	 */
	public static SAXReader getSAXReader(boolean validating) {
		return fixSAXReaderSecurity(new SAXReader(validating));
	}
	
	/*
	 * Implements a fix for bug#901787 on a newly constructed
	 * SAXReader.
	 */
	private static SAXReader fixSAXReaderSecurity(SAXReader saxReader) {
		try {
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities",   false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		catch (SAXException e) {
			m_logger.error("fixSAXReaderSecurity( SAXException ):  ", e);
			saxReader = null;
		}
		return saxReader;
	}
	
    public static Document parseText(String text) throws DocumentException {
        Document result = null;

        SAXReader reader = getSAXReader();
        String encoding = getEncoding(text);

        InputSource source = new InputSource(new StringReader(text));
        source.setEncoding(encoding);

        result = reader.read(source);

        // if the XML parser doesn't provide a way to retrieve the encoding,
        // specify it manually
        if (result.getXMLEncoding() == null) {
            result.setXMLEncoding(encoding);
        }

        return result;
    }
	
    private static String getEncoding(String text) {
        String result = null;

        String xml = text.trim();

        if (xml.startsWith("<?xml")) {
            int end = xml.indexOf("?>");
            String sub = xml.substring(0, end);
            StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");

            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();

                if ("encoding".equals(token)) {
                    if (tokens.hasMoreTokens()) {
                        result = tokens.nextToken();
                    }

                    break;
                }
            }
        }

        return result;
    }

}
