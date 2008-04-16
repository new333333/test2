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
/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.docconverter.impl;

import java.io.File;

//UNO API
import com.sitescape.team.docconverter.HtmlConverter;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uri.ExternalUriReferenceTranslator;
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
public class HtmlOpenOfficeConverter
	extends HtmlConverter
	implements HtmlOpenOfficeConverterMBean, InitializingBean, DisposableBean
{
	private String _host = null;
	private int _port = 0;

	public HtmlOpenOfficeConverter()
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
	public void convert(String origFileName, String ifp, String ofp, long timeout, String parameters)
		throws Exception
	{
		XStorable xstorable = null;
		XComponent xcomponent = null;
		XUnoUrlResolver xurlresolver = null;
		XComponentContext xcomponentcontext = null;
		XComponentLoader xcomponentloader = null;
		XPropertySet xpropertysetMultiComponentFactory = null;
		XMultiComponentFactory xmulticomponentfactory = null;
		File ofile = null,
			 ifile = null;
		Object objectUrlResolver = null,
			   objectInitial = null,
			   objectDocumentToStore = null,
			   objectDefaultContext = null;
		String url = "",
			   convertType = "";
	    
		try
		{
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
				return;
				
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
			url = convertToUrl(ifile, xcomponentcontext);
			objectDocumentToStore = xcomponentloader.loadComponentFromURL(url, "_blank", 0, propertyValues);
			if (objectDocumentToStore == null)
			{
				logger.error("OpenOffice Html Converter, could not load file: " + url);
				return;
			}
			
			// Getting an object that will offer a simple way to store a document to a URL.
			xstorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, objectDocumentToStore);
	      
			// Determine convert type based on input file name extension
			if (ifp.toLowerCase().endsWith(".odp")
			|| ifp.toLowerCase().endsWith(".ppt"))
				convertType = "impress_html_Export";
			else
			if (ifp.toLowerCase().endsWith(".ods")
			|| ifp.toLowerCase().endsWith(".xls"))
				convertType = "scalc: HTML (StarCalc)";
			else
				convertType = "HTML (StarWriter)";
			
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
		}
		finally
		{
			//	Getting the method dispose() for closing the document
			if (xstorable != null)
			{
				xcomponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xstorable);
				
				// Closing the converted document
				if (xcomponent != null)
					xcomponent.dispose();
			}
		}
	    
	    return;
	  }
}
