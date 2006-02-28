/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.docconverter;

import com.stellent.scd.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * The <code>Converter</code> class uses the {@link Export Export} 
 * technology according to the properties provided in a given 
 * configuration file.  The configuration file is assumed to be 
 * correctly formatted.
 *
 * @author klein
 * @version 1.00
 * @see Export Export
 */

public class DocConverter implements InitializingBean, DisposableBean {

	private static final String INPUTPATHKEY = "inputpath";
	private static final String OUTPUTPATHKEY = "outputpath";
	private static final String OUTPUTIDKEY = "outputid";
	private String configFileName;
	private String nullTransform;
	
	Properties configProps = new Properties();
	protected final Log logger = LogFactory.getLog(getClass());

	public void afterPropertiesSet() throws Exception {
		try {
			setConverterConfiguration(getConfigFileName());
		} catch (Exception e) {}
	}	
	
	public void destroy() throws Exception {
		
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}
	
	/**
	 * @return Returns the configFileName.
	 */
	public String getConfigFileName() {
		return configFileName;
	}

	/**
	 * @param configFileName The configFileName to set.
	 */
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}


	/**
	 * @return Returns the nullTransform.
	 */
	public String getNullTransform() {
		return nullTransform;
	}

	/**
	 * @param nullTransform The nullTransform to set.
	 */
	public void setNullTransform(String nullTransform) {
		this.nullTransform = nullTransform;
	}

	/**
	 * @return Returns the nullTransform file.
	 */
	public File getNullTransformFile() {
		try {
			//load singleton with our config file
			return new ClassPathResource(nullTransform).getFile();
		}
        catch (Exception e) {
        	Log logger = LogFactory.getLog(getClass());
        	logger.error("DocConverter, transform file error: " + e.getLocalizedMessage());
        }
		return null;
	}
	
	/**
	 * This is going to be a bean, so, we need a bean style setter.
	 * @param configFileName
	 */
	
	public void setConverterConfiguration(String configFileName) {	
		Properties p = new Properties();
		
		Resource resource;
		
		resource = toResource(configFileName);
		
		try {
	
			if (resource != null) {
	            InputStream is = resource.getInputStream();
				configProps.load(is);
				is.close();
				logger.info("DOCCONVERTER: Loading " + configFileName);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a path to an properties file, return a Resource object for that file.
	 * 
	 * @param filePath
	 * @return Resource
	 */
	private Resource toResource(String filePath) {
        return new ClassPathResource(filePath);
    }

	public void convert(File ifp, File ofp, long timeout)
	{
		convert(ifp.getAbsolutePath(), ofp.getAbsolutePath(),timeout);
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 *  @param timeout Export process timeout in milliseconds.
	 */
	public void convert(String ifp, String ofp, long timeout)
	{
		String oid = configProps.getProperty(OUTPUTIDKEY);

		//  Remove extra control properties.
		configProps.remove(INPUTPATHKEY);
		configProps.remove(OUTPUTPATHKEY);
		
		//  Process the conversion.
		Export e = new Export(configProps);

		ExportStatusCode result = e.convert(ifp, ofp, oid, timeout);
        if (result.getCode() == ExportStatusCode.SCCERR_OK.getCode())
        {
			   logger.info("Conversion Successful!" + ifp + ":" + ofp);
        }
        else {
           logger.info("Conversion Error: " + result);
           System.out.println("Conversion Error: " + result );
        }
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *  Default the timeout to 0.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 */

	public void convert(String ifp, String ofp)
	{
		// default the timeout value to 0
		convert(ifp,ofp,0);
	}
	
}
