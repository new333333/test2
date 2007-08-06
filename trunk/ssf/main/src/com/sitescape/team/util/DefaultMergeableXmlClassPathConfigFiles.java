/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

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
import org.springframework.core.io.Resource;

import com.sitescape.team.ConfigurationException;
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
