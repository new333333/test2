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
	implements InitializingBean, DisposableBean
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
		File f = null;
		org.dom4j.Document doc = null;
		XStorable xstorable = null;
		XComponent xcomponent = null;
		XUnoUrlResolver xurlresolver = null;
		XComponentContext xcomponentcontext = null;
		XComponentLoader xcomponentloader = null;
		XPropertySet xpropertysetMultiComponentFactory = null;
		XMultiComponentFactory xmulticomponentfactory = null;
		Object objectUrlResolver = null,
			   objectInitial = null,
			   objectDocumentToStore = null,
			   objectDefaultContext = null;
		String url = "file://",
	    	   convertType = "";
	    
		try
		{
			// OpenOffice can not conver these types of files. Will cause OpenOffice crash in some cases
			if (ifp.endsWith(".jpg")
			|| ifp.endsWith(".JPG")
			|| ifp.endsWith(".jpeg")
			|| ifp.endsWith(".JPEG")
			|| ifp.endsWith(".gif")
			|| ifp.endsWith(".GIF"))
				return "";
				
			// Are we dealing with Windows
			if (ifp.indexOf("\\") > 0)
				url = "file:///";
			
			ifp = ifp.replace('\\', '/');
			ofp = ofp.replace('\\', '/');
			f = new File(ofp);
				
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
			xmulticomponentfactory = (XMultiComponentFactory)UnoRuntime.queryInterface( XMultiComponentFactory.class, objectInitial);
	      
			// Query for the XPropertySet interface.
			xpropertysetMultiComponentFactory = (XPropertySet)UnoRuntime.queryInterface( XPropertySet.class, xmulticomponentfactory);
	      
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
			objectDocumentToStore = xcomponentloader.loadComponentFromURL(url + ifp, "_blank", 0, propertyValues);
	      
			// Getting an object that will offer a simple way to store a document to a URL.
			xstorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, objectDocumentToStore);
	      
			// Determine convert type based on input file name extension

			if (ifp.endsWith(".odp")
			|| ifp.endsWith(".ppt"))
				convertType = "XHTML Draw File";
			else
			if (ifp.endsWith(".ods")
			|| ifp.endsWith(".xls"))
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
			xstorable.storeToURL("file:///" + ofp, propertyValues);
			if (f.exists() && f.length() > 0)
			{
				doc = getDomDocument(f);
				if(doc != null)
				{
					// Run the stylesheet to extract text from the xml. 
					return getTextFromXML(doc, getNullTransformFile());
					// Note: Roy, for some reason, the text coming out of the transformer
					// always contain <?xml version="1.0" encoding="UTF-8"?> prefix??
				}
			}
		}
		finally
		{
			// Getting the method dispose() for closing the document
			xcomponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xstorable);
			
			// Closing the converted document
			if (xcomponent != null)
				xcomponent.dispose();
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
