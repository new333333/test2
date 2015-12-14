package org.kabling.teaming.install.server;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * @author Rajesh
 * 
 */
public class LicenseImportServlet extends HttpServlet
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.LicenseImportServlet");
	private static final long serialVersionUID = 1L;

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

			boolean licenseKey = false;
			while (iter.hasNext())
			{
				// Get the current item in the iteration
				FileItem item = (FileItem) iter.next();

				if (item.isFormField() && item.getFieldName().equals("licenseKey"))
				{
					licenseKey = Boolean.valueOf(item.getString());
					continue;
				}

                //Copy the license key
				if (licenseKey)
				{
					FileOutputStream outStream = new FileOutputStream("/filrinstall/license-key.xml");

					outStream.write(item.get());
					outStream.close();
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Exception " + e.getMessage());
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}
}
