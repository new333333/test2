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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.docconverter.IImageConverterManager;
import com.sitescape.team.docconverter.ImageConverter;
import com.sitescape.team.util.DirPath;
import com.sitescape.team.util.FileStore;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.Thumbnail;


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
	implements ImageOpenOfficeConverterMBean, InitializingBean, DisposableBean
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
		ByteArrayOutputStream baos = null;
		byte[] inputData = null;
		File ifile = null,
			 ofile = null;
		
		try
		{
			/**
			 * If the output file exist an has a modified date equal or greating than incoming file
			 * do not perform any conversion. 
			 */
			ifile = new File(ifp);
			ofile = new File(ofp);
			
			if (ofile != null
			&& ofile.exists()
			&& ofile.lastModified() >= ifile.lastModified())
				return "";
				
			// Can not handle anything other than JPEG
			if (ifp.toLowerCase().endsWith(".jpg")
			|| ifp.toLowerCase().endsWith(".jpeg"))
			{
				is = new FileInputStream(ifp);
			}
			else
			{
				is = new FileInputStream(_defaultImage);
			}
			inputData = FileCopyUtils.copyToByteArray(is);
			
			baos = new ByteArrayOutputStream();
			if (maxHeight == 0) {
				Thumbnail.createThumbnail(inputData, baos, maxWidth);
			} else {
				Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);
			}

			if (!(ofp.toLowerCase().endsWith(".jpg")
			|| ofp.toLowerCase().endsWith(".jpeg")))
				os = new FileOutputStream(ofp + IImageConverterManager.IMG_EXTENSION);
			else
				os = new FileOutputStream(ofp);
			
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
