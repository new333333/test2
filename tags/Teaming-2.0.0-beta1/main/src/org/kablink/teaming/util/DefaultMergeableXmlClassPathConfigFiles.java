/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentSource;
import org.kablink.teaming.ConfigurationException;
import org.springframework.core.io.Resource;

/**
 *
 * @author Jong Kim
 */
public class DefaultMergeableXmlClassPathConfigFiles extends MergeableXmlClassPathConfigFiles {
    protected Resource styleSheet;
    public void setStyleSheet(Resource styleSheet) {
    	this.styleSheet = styleSheet;
    }

    /**
     * This method overrides {@link MergeableXmlClassPathConfigFile#mergeIntoSingleDom4jDocument}
     */
    protected Document mergeIntoSingleDom4jDocument() throws DocumentException, IOException {
    	TransformerFactory transFactory = TransformerFactory.newInstance();
    	try {
    		if (size() > 1 && styleSheet != null) {   	
    			Source xsltSource = new StreamSource(styleSheet.getFile());
    			Transformer trans = transFactory.newTransformer(xsltSource);
    			trans.setParameter("merge", getAsFile(1).toURI().toString());
    			StreamResult result = new StreamResult(new StringWriter());
    			trans.transform(new DocumentSource(getAsDom4jDocument(0)), result);
    			Document document = null;
//    			String it = result.getWriter().toString();
   		    	document = DocumentHelper.parseText(result.getWriter().toString());
   		    	return document;
    		} else return getAsDom4jDocument();
    	} catch (TransformerConfigurationException te) {
    		throw new ConfigurationException(te.getLocalizedMessage(), te);
       	} catch (TransformerException te) {
    		throw new ConfigurationException(te.getLocalizedMessage(), te);
    	}
    }
}
