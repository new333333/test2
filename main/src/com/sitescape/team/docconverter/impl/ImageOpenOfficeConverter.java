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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

	/**
	 *  Run the conversion using the given input path, output path.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 *  @param timeout Export process timeout in milliseconds.
	 *  @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public void convert(String origFileName, String ifp, String ofp, long timeout, ImageConverter.Parameters parameters)
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
			
			try {
				is = new FileInputStream(ifp);
				inputData = FileCopyUtils.copyToByteArray(is);
			
				baos = new ByteArrayOutputStream();
				if (parameters.getHeight() == 0) {
					Thumbnail.createThumbnail(inputData, baos, parameters.getWidth());
				} else {
					Thumbnail.createThumbnail(inputData, baos, parameters.getHeight(), parameters.getWidth());
				}
			}
			catch(Exception e)
			{
				is = new FileInputStream(_defaultImage);
				inputData = FileCopyUtils.copyToByteArray(is);
				
				baos = new ByteArrayOutputStream();
				if (parameters.getHeight() == 0) {
					Thumbnail.createThumbnail(inputData, baos, parameters.getWidth());
				} else {
					Thumbnail.createThumbnail(inputData, baos, parameters.getHeight(), parameters.getWidth());
				}
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
