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
package com.sitescape.team.docconverter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import org.springframework.core.io.ClassPathResource;

public abstract class TextConverter 
{
	protected final Log logger = LogFactory.getLog(getClass());
	protected String _nullTransform = "";
	
	public abstract String convert(String ifp, String ofp, long timeout)
		throws Exception;
	
	public String convert(File ifp, File ofp, long timeout)
		throws Exception
	{
		return convert(ifp.getAbsolutePath(), ofp.getAbsolutePath(),timeout);
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *  Default the timeout to 0.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 */

	public void convert(String ifp, String ofp)
		throws Exception
	{
		// default the timeout value to 0
		convert(ifp,ofp,0);
	}
	
	protected org.dom4j.Document getDomDocument(File textFile) {
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		try {
			return reader.read(textFile);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
			return null;
		}	
	}
	
	protected String getTextFromXML(File ofile, File transformFile)
    {	
    	Locale l = Locale.getDefault();
		Templates trans;
		Transformer tranny = null;
		org.dom4j.Document tempfile = getDomDocument(ofile);
		if(tempfile != null) {

			try {

				TransformerFactory transFactory = TransformerFactory.newInstance();

				Source s = new StreamSource(transformFile);
				trans = transFactory.newTemplates(s);
				tranny =  trans.newTransformer();
			} catch (TransformerConfigurationException tce) {}

			StreamResult result = new StreamResult(new StringWriter());
			try {
				tranny.setParameter("Lang", l);
				tranny.transform(new DocumentSource(tempfile), result);
			} catch (Exception ex) {
				return ex.getMessage();
			}
			return result.getWriter().toString();
		}
		return "";
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
	
	protected String getCachedData(File ifile, File ofile)
	{
		
		if (ofile != null
		&& ofile.exists()
		&& ofile.lastModified() >= ifile.lastModified())
		{
			try
			{
				char[] cbuf = new char[2048];
				StringBuffer buffer = new StringBuffer("");
				FileReader fr = new FileReader(ofile);
				while (fr.read(cbuf, 0, cbuf.length) > -1)
					buffer.append(cbuf);
				 return buffer.toString().trim();
			 }
			 catch (IOException io) {}
		}

		return null;
	}
}
