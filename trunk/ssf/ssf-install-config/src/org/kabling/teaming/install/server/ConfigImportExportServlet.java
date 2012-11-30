/*
 * ========================================================================
 *
 * Copyright (c) 2012 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package org.kabling.teaming.install.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * @author Rajesh
 * 
 */
public class ConfigImportExportServlet extends HttpServlet
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.ConfigImportExportServlet");
	private static final long serialVersionUID = 1L;
	private static Map<String, String> filesToZipMap = new HashMap<String, String>();
	
	private static final String CONFIG_ZIP_NAME = "filrconfig.zip";
	private static final String ZIP_MIME_TYPE = "application/zip";

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		//Add the files that need to be zipped part of export
		if (TimeZoneHelper.isUnix())
		{
			filesToZipMap.put("installer.xml", "/filrinstall/installer.xml");
			filesToZipMap.put("configurationDetails.properties", "/filrinstall/configurationDetails.properties");
		}
		else
			filesToZipMap.put("installer.xml", "c:/test/installer.xml");
			
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			//return the current configurations settings as a zip file
			getFilrConfigurationSettings(resp);
		}
		catch (Exception e)
		{
			logger.error("Error trying to get filr configuation as a zip file "+e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest res, HttpServletResponse response) throws ServletException, IOException
	{

		// Commons file upload classes are specifically instantiated
		FileItemFactory factory = new DiskFileItemFactory();

		ServletFileUpload upload = new ServletFileUpload(factory);
		ServletOutputStream out = null;

		try
		{
			// Parse the incoming HTTP request
			// Commons takes over incoming request at this point
			// Get an iterator for all the data that was sent
			List<?> items = upload.parseRequest(res);
			Iterator<?> iter = items.iterator();

			while (iter.hasNext())
			{
				// Get the current item in the iteration
				FileItem item = (FileItem) iter.next();

				// Specify where on disk to write the file
				// Write the file data to disk
				// TODO: Place restrictions on upload data

				byte[] data = item.get();

				ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(data));
				ZipEntry entry = null;
				
				//Go through each file entry
				while ((entry = zipStream.getNextEntry()) != null)
				{
					String entryName = entry.getName();
					String filePath = filesToZipMap.get(entryName);

					//If it is a file we know, we can save it to the file system
					if (filePath != null)
					{
						FileOutputStream outStream = new FileOutputStream(filePath);

						byte[] buf = new byte[4096];
						int bytesRead = 0;
						while ((bytesRead = zipStream.read(buf)) != -1)
						{
							outStream.write(buf, 0, bytesRead);
						}
						outStream.close();
						zipStream.closeEntry();
					}
				}
				zipStream.close();
			}
		}
		catch (FileUploadException fue)
		{
			logger.error("File Upload exception "+fue.getMessage());
		}
		catch (IOException ioe)
		{
			logger.error("IO exception "+ioe.getMessage());
		}
		catch (Exception e)
		{
			logger.error("Exception "+e.getMessage());
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}

	private void getFilrConfigurationSettings(HttpServletResponse response) throws Exception
	{
		ServletOutputStream op = response.getOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		//Zip all the files that we need to send as part of export configuration
		for (Entry<String, String> entry : filesToZipMap.entrySet())
		{
			//If the file does not exist, ignore and continue
			if (!(new File(entry.getValue()).exists()))
				continue;
			
			zos.putNextEntry(new ZipEntry(entry.getKey()));
			byte[] b = new byte[1024];
			int len;
			FileInputStream fis = new FileInputStream(entry.getValue());
			while ((len = fis.read(b)) != -1)
			{
				zos.write(b, 0, len);
			}
			fis.close();
			zos.closeEntry();
		}
		zos.close();

		response.setContentType(ZIP_MIME_TYPE);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + CONFIG_ZIP_NAME + "\"");

		op.write(baos.toByteArray());
		op.flush();

	}
}
