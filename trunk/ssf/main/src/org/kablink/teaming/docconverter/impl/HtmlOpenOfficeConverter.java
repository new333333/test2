/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Jun 24, 2005
 */
package org.kablink.teaming.docconverter.impl;

import java.io.File;

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

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.docconverter.HtmlConverter;
import org.kablink.teaming.docconverter.util.OpenOfficeHelper;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.util.GangliaMonitoring;
import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Performs file conversions to HTML using OpenOffice.
 * 
 * The <code>Converter</code> class uses the {@link Export Export} 
 * technology according to the properties provided in a given 
 * configuration file.  The configuration file is assumed to be 
 * correctly formatted.
 *
 *	IMPORTANT: OpenOffice Server must be running; to start:
 *					% C:\Program Files\OpenOffice.org 2.0\program\soffice.exe "-accept=socket,port=8100;urp;"
 *
 * @author rsordillo
 * @version 1.00
 * @see Export Export
 */
public class HtmlOpenOfficeConverter extends HtmlConverter implements HtmlOpenOfficeConverterMBean, InitializingBean, DisposableBean {
	private int		m_port = 0;		//
	private String	m_host = null;	//

	/**
	 * Constructor method.
	 */
	public HtmlOpenOfficeConverter() {
		super();
	}

	/**
	 * ?
	 * 
	 * @throws Exception.
	 */
	@Override
	public void afterPropertiesSet()
			throws Exception {
	}	

	/**
	 * ?
	 * 
	 * @throws Exception
	 */
	@Override
	public void destroy()
			throws Exception {	
	}

	/**
	 * ?
	 * 
	 * @return
	 */
	@Override
	public String getHost() {
		return m_host;
	}

	/**
	 * ?
	 * 
	 * @param host_in
	 */
	public void setHost(String host_in) {
		m_host = host_in;
	}

	/**
	 * ?
	 * 
	 * @return
	 */
	@Override
	public int getPort() {
		return m_port;
	}

	/**
	 * ?
	 * 
	 * @param port_in
	 */
	public void setPort(int port_in) {
		m_port = port_in;
	}

	/**
	 * ?
	 * 
	 * @param f
	 * @param xComponentContext
	 * 
	 * @return
	 * 
	 * @throws java.net.MalformedURLException
	 */
	public String convertToUrl(File f, XComponentContext xComponentContext)
			throws java.net.MalformedURLException {
		String returnUrl = null;
	
		@SuppressWarnings("deprecation")
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
	 *  
	 *  @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	@Override
	public void convert(String origFileName, String ifp, String ofp, long timeout, String parameters)
			throws Exception {
		XStorable xstorable = null;
		XComponent xcomponent = null;
		XUnoUrlResolver xurlresolver = null;
		XComponentContext xcomponentcontext = null;
		XComponentLoader xcomponentloader = null;
		XPropertySet xpropertysetMultiComponentFactory = null;
		XMultiComponentFactory xmulticomponentfactory = null;
		File ofile = null;
		File ifile = null;
		Object objectUrlResolver = null;
		Object objectInitial = null;
		Object objectDocumentToStore = null;
		Object objectDefaultContext = null;
		String url = "";
		String convertType = "";
	    
		try {
			ifile = new File(ifp);
			ofile = new File(ofp);
			
			/*
			 * As part of fixing Bugzilla 480931, I removed the check
			 * here for the output file existing and having a modified
			 * date greater than the incoming file.  In essence, this
			 * will now ALWAYS perform the conversion.
			 * 
			 * In debugging Bugzilla 480931, there were cases where the
			 * file existed but the HTML wasn't getting updated.
			 */
			if (!ifile.exists()) {
				return;
			}
				
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
			objectInitial = xurlresolver.resolve("uno:socket,host=" + m_host + ",port=" + m_port + ";urp;StarOffice.ServiceManager");
	      
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
			
			// Setting the flag for hiding the open document
			propertyValues[0] = new PropertyValue();
			propertyValues[0].Name = "Hidden";
			propertyValues[0].Value = new Boolean(true);
	      
			// Loading the wanted document
			url = convertToUrl(ifile, xcomponentcontext);
			objectDocumentToStore = xcomponentloader.loadComponentFromURL(url, "_blank", 0, propertyValues);
			if (objectDocumentToStore == null) {
				logger.error("HtmlOpenOfficeConverter.convert( \"Could not load file '" + url + "'\" )");
				return;
			}
			
			// Getting an object that will offer a simple way to store a document to a URL.
			xstorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, objectDocumentToStore);
	      
			// Determine the convert type based on the input file
			// name's extension.
			convertType = OpenOfficeHelper.getConvertType(
				OpenOfficeHelper.ConvertType.VIEW,
				OpenOfficeHelper.getExtension(ifp));
			
			// Did we arrive at a convert type for this file name
			// extension?
			if (!(MiscUtil.hasString(convertType))) {
				// No!  Log the error and bail.
				logger.error("HtmlOpenOfficeConverter.convert( \"Could not determine the convert type for '" + ifp + "' type files.\" )");
				return;
			}
			logger.debug("HtmlOpenOfficeConverter.convert( \"Using convert type '" + convertType + "' for '" + url + "' \" )");
			
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
		finally {
			//	Getting the method dispose() for closing the document
			if (xstorable != null) {
				xcomponent = ((XComponent) UnoRuntime.queryInterface(XComponent.class, xstorable));
				
				// Closing the converted document
				if (xcomponent != null) {
					xcomponent.dispose();
				}
			}
		}
	    
	    // After a successful conversion, increment the conversions
	    // count.
		GangliaMonitoring.incrementFilePreviewConversions();
	  }

	/**
	 * ?
	 *
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	@Override
	public void deleteConvertedFile(Binder binder, DefinableEntity entry, FileAttachment fa)
			throws UncheckedIOException, RepositoryServiceException {
		super.deleteConvertedFile(binder, entry, fa, getCacheSubDir(), getCachedFileSuffix());
	}

	/**
	 * ?
	 *
	 * @param shareItem
	 * @param binder
	 * @param entry
	 * @param fa
	 * 
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	@Override
	public void deleteConvertedFile(ShareItem shareItem, Binder binder, DefinableEntity entry, FileAttachment fa)
			throws UncheckedIOException, RepositoryServiceException {
		String subDir = (HTML_PUBLIC_SUBDIR + String.valueOf(shareItem.getId()));
		super.deleteConvertedFile(binder, entry, fa, subDir, getCachedFileSuffix());
	}
}
