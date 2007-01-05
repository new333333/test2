/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.docconverter;

import com.stellent.scd.*;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

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
 * @author rsordillo
 * @version 1.00
 * @see Export Export
 */

public class HtmlStellentConverter
	extends HtmlConverter
	implements InitializingBean, DisposableBean
{
	private static final String OUTPUTIDKEY = "outputid";
	private String _configFileName;
	private Properties _configProps = new Properties();
	
	public HtmlStellentConverter()
	{
		super();
	}
	
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
		return _configFileName;
	}

	/**
	 * @param configFileName The configFileName to set.
	 */
	public void setConfigFileName(String configFileName) {
		_configFileName = configFileName;
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
	            _configProps.load(is);
				is.close();
				logger.info("HTMLCONVERTER: Loading " + configFileName);
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
	public void convert(String ifp, String ofp, long timeout)
		throws Exception
	{
		ExportStatusCode result = null;
		String oid = null;
		
		oid = _configProps.getProperty(OUTPUTIDKEY);

		//  Process the conversion.
		Export e = new Export(_configProps);
		result = e.convert(ifp, ofp, oid, timeout);
	    if (result.getCode() != ExportStatusCode.SCCERR_OK.getCode())
	    	throw (new Exception(result.toString()));
	    
	    return;
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
	
}
