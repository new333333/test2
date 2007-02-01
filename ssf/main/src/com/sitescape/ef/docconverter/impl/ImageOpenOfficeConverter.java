/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.docconverter.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.docconverter.IImageConverterManager;
import com.sitescape.ef.docconverter.ImageConverter;
import com.sitescape.ef.util.DirPath;
import com.sitescape.ef.util.FileStore;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.Thumbnail;


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
public class ImageOpenOfficeConverter
	extends ImageConverter
	implements InitializingBean, DisposableBean
{
	private int _port = 0;
	FileStore _cacheFileStore = null;
	private String _host = null,
				   _configFileName = null;
	
	public ImageOpenOfficeConverter()
	{
		super();
		_defaultImage = DirPath.getThumbnailDirPath() + File.separator + "NoImage.jpeg";
		_cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"));
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

	public void convert(File ifp, File ofp, long timeout, int maxWidth, int maxHeight)
		throws Exception
	{
		convert(ifp.getAbsolutePath(), ofp.getAbsolutePath(), timeout, maxWidth, maxHeight);
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 *  @param timeout Export process timeout in milliseconds.
	 *  @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public String convert(String ifp, String ofp, long timeout, int maxWidth, int maxHeight)
		throws Exception
	{
		FileInputStream is = null;
		FileOutputStream os = null;
		byte[] inputData = null;
		
		try
		{
			// Can not handle anything other than JPEG
			if (ifp.toLowerCase().endsWith(".jpg")
			|| ifp.toLowerCase().endsWith(".jpeg"))
			{
				is = new FileInputStream(ifp);
				inputData = new byte[is.available()];
				is.read(inputData);
			}
			else
			{
				is = new FileInputStream(_defaultImage);
				inputData = new byte[is.available()];
				is.read(inputData);			
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
			if (maxHeight == 0) {
				Thumbnail.createThumbnail(inputData, baos, maxWidth);
			} else {
				Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);
			}

			os = new FileOutputStream(ofp + IImageConverterManager.IMG_EXTENSION);
			os.write(baos.toByteArray());				
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
		finally
		{
			if (is != null)
				is.close();
			if (os != null)
				os.close();
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
		convert(ifp, ofp, 0, 300, 300);
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
