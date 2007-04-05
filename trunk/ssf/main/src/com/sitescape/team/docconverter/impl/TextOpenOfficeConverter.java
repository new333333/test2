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
/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.docconverter.impl;

import java.io.File;

//UNO API
import com.sitescape.team.docconverter.TextConverter;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uri.ExternalUriReferenceTranslator;
import com.sun.star.connection.NoConnectException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * The <code>Converter</code> class uses the {@link Export Export} 
 * technology according to the properties provided in a given 
 * configuration file.  The configuration file is assumed to be 
 * correctly formatted.
 *
 *	IMPORTANT: OpenOffice Server must be running; to start:
 *					% C:\Program Files\OpenOffice.org 2.0\program\soffice.exe "-accept=socket,port=8100;urp;"
 * @author rsordillo
 * @version 1.00
 * @see Export Export
 */
public class TextOpenOfficeConverter
	extends TextConverter
	implements TextOpenOfficeConverterMBean, InitializingBean, DisposableBean
{
	private int _port = 0;
	private String _host = null,
				   _configFileName = null;
	
	public TextOpenOfficeConverter()
	{
		super();
	}
	
	public void afterPropertiesSet() throws Exception {
		
	}	
	
	public void destroy() throws Exception 
	{	
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}
	
	public String getHost() {
		return _host;
	}

	public void setHost(String host_in) {
		_host = host_in;
	}
	
	public int getPort() {
		return _port;
	}

	public void setPort(int port_in) {
		_port = port_in;
	}

	public void convert(File ifp, File ofp, long timeout)
		throws Exception
	{
		convert(ifp.getAbsolutePath(), ofp.getAbsolutePath(),timeout);
	}
	
	public String convertToUrl(File f, XComponentContext xComponentContext)
		throws java.net.MalformedURLException 
	{
		String returnUrl = null;
		
		java.net.URL u = f.toURL();
		returnUrl =  ExternalUriReferenceTranslator.create(xComponentContext).translateToInternal(u.toExternalForm());
		
		return returnUrl;
	}

	
	/**
	 *  Run the conversion using the given input path, output path.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 *  @param timeout Export process timeout in milliseconds.
	 *  @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public String convert(String ifp, String ofp, long timeout)
		throws Exception
	{
		org.dom4j.Document doc = null;
		XStorable xstorable = null;
		XComponent xcomponent = null;
		XUnoUrlResolver xurlresolver = null;
		XComponentContext xcomponentcontext = null;
		XComponentLoader xcomponentloader = null;
		XPropertySet xpropertysetMultiComponentFactory = null;
		XMultiComponentFactory xmulticomponentfactory = null;
		File ifile = null,
			 ofile = null;
		Object objectUrlResolver = null,
			   objectInitial = null,
			   objectDocumentToStore = null,
			   objectDefaultContext = null;
		String url = "",
			   convertType = "";
	    
		try
		{
			// OpenOffice can not conver these types of files. Will cause OpenOffice crash in some cases
			if (ifp.toLowerCase().endsWith(".jpg")
			|| ifp.toLowerCase().endsWith(".jpeg")
			|| ifp.toLowerCase().endsWith(".gif"))
				return "";

			/**
			 * If the output file exist an has a modified date equal or greating than incoming file
			 * do not perform any conversion. 
			 */
			ifile = new File(ifp);
			ofile = new File(ofp);
			
			if (!ifile.exists()
			|| (ofile != null
			&& ofile.exists()
			&& ofile.lastModified() >= ifile.lastModified()))
				return "";
				
			/* Bootstraps a component context with the jurt base components
			 * registered. Component context to be granted to a component for running.
			 * Arbitrary values can be retrieved from the context.
			 */
			xcomponentcontext = Bootstrap.createInitialComponentContext(null);
	      
			/* Gets the service manager instance to be used (or null). This method has
			 * been added for convenience, because the service manager is a often used object.
			 */
			xmulticomponentfactory = xcomponentcontext.getServiceManager();
	      
			/* Creates an instance of the component UnoUrlResolver which supports the services specified by the factory. */
			objectUrlResolver = xmulticomponentfactory.createInstanceWithContext("com.sun.star.bridge.UnoUrlResolver", xcomponentcontext);
	      
			// Create a new url resolver
			xurlresolver = (XUnoUrlResolver)UnoRuntime.queryInterface(XUnoUrlResolver.class, objectUrlResolver);
	      
			// Resolves an object that is specified as follow:
			// uno:<connection description>;<protocol description>;<initial object name>
			objectInitial = xurlresolver.resolve("uno:socket,host=" + _host + ",port=" + _port + ";urp;StarOffice.ServiceManager");
	      
			// Create a service manager from the initial object
			xmulticomponentfactory = (XMultiComponentFactory)UnoRuntime.queryInterface(XMultiComponentFactory.class, objectInitial);
	      
			// Query for the XPropertySet interface.
			xpropertysetMultiComponentFactory = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xmulticomponentfactory);
	      
			// Get the default context from the office server.
			objectDefaultContext = xpropertysetMultiComponentFactory.getPropertyValue("DefaultContext");
	      
			// Query for the interface XComponentContext.
			xcomponentcontext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, objectDefaultContext);
	      
			/* A desktop environment contains tasks with one or more
			 * frames in which components can be loaded. Desktop is the
			 * environment for components which can instanciate within frames.
			 */
			xcomponentloader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, xmulticomponentfactory.createInstanceWithContext("com.sun.star.frame.Desktop", xcomponentcontext));

			// Preparing properties for loading the document
			PropertyValue propertyValues[] = new PropertyValue[1];
			// Setting the flag for hidding the open document
			propertyValues[0] = new PropertyValue();
			propertyValues[0].Name = "Hidden";
			propertyValues[0].Value = new Boolean(true);
			
			// Loading the wanted document
			url = convertToUrl(ifile, xcomponentcontext);
			objectDocumentToStore = xcomponentloader.loadComponentFromURL(url, "_blank", 0, propertyValues);
			if (objectDocumentToStore == null)
			{
				logger.error("OpenOffice Text Converter, could not load file: " + url);
				return "";
			}
			
			// Getting an object that will offer a simple way to store a document to a URL.
			xstorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, objectDocumentToStore);
	      
			// Determine convert type based on input file name extension
			if (ifp.toLowerCase().endsWith(".odp")
			|| ifp.toLowerCase().endsWith(".ppt"))
				convertType = "XHTML Draw File";
			else
			if (ifp.toLowerCase().endsWith(".ods")
			|| ifp.toLowerCase().endsWith(".xls"))
				convertType = "XHTML Calc File";
			else
				convertType = "XHTML Writer File";

/*
  				convertType = "XHTML Impress File";
				convertType = "XHTML Writer File";				
				convertType = "XHTML Draw File";
				convertType = "writer_pdf_Export";
				convertType = "Text";
				convertType = "XHTML 1.0 strict (.html)";
				convertType = "XHTML (StarWriter)";
				convertType = "HTML (StarWriter)";
				convertType = "XHTML 1.0 Strict";
*/
			
			// Preparing properties for converting the document
			propertyValues = new PropertyValue[2];
			// Setting the flag for overwriting
			propertyValues[0] = new PropertyValue();
			propertyValues[0].Name = "Overwrite";
			propertyValues[0].Value = new Boolean(true);
			// Setting the filter name
			propertyValues[1] = new PropertyValue();
			propertyValues[1].Name = "FilterName";
			propertyValues[1].Value = convertType;
	      
			// Storing and converting the document
			url = convertToUrl(ofile, xcomponentcontext);
			xstorable.storeToURL(url, propertyValues);
			if (ofile.exists() && ofile.length() > 0)
			{
				doc = getDomDocument(ofile);
				if(doc != null)
				{
					// Run the stylesheet to extract text from the xml. 
					return getTextFromXML(doc, getNullTransformFile());
					// Note: Roy, for some reason, the text coming out of the transformer
					// always contain <?xml version="1.0" encoding="UTF-8"?> prefix??
				}
			}
		}
		catch (Exception e)
		{
			// Create empty file if exception is for unable to transform the document
			if (!(e instanceof NoConnectException))
			{
				if (!ofile.exists())
					ofile.createNewFile();
			}
				
			throw e;
		}
		finally
		{
			// Getting the method dispose() for closing the document
			if (xstorable != null)
			{
				xcomponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xstorable);
				
				// Closing the converted document
				if (xcomponent != null)
					xcomponent.dispose();
			}
		}
	    
		return "";
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
	
	/**
	 * @return Returns the configFileName.
	 */
	public String getConfigFileName() {
		return _configFileName;
	}

	/**
	 * @param configFileName The configFileName to set.
	 */
	public void setConfigFileName(String configFileName) {
		_configFileName = configFileName;
	}
}
