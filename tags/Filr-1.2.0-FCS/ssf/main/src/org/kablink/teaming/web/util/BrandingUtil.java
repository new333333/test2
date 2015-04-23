/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.web.WebKeys;
import org.springframework.util.FileCopyUtils;


/**
 * 
 * @author jwootton
 *
 */
public class BrandingUtil
{
	protected static Log m_logger = LogFactory.getLog( BrandingUtil.class );

	
	/**
	 * Copy the given image from the given binder into the /ssf/branding/ directory
	 * We do this so that all users (including the guest user) can see the images used for branding.
	 * 
	 * We copy the image to the ssf/branding/xxx directory so it will be visible to
	 * all users (including the guest user).
	 * 
	 * Return the url to the image if the copy was successful.
	 */
	private static String copyBrandingImgToBrandingDir(
		AbstractAllModulesInjected allModules,
		HttpServletRequest httpReq,
		ServletContext servletContext,
		Binder brandingSourceBinder,
		String imgName,
		String subDirFSPath,
		String subDirUrlPath )
	{
		String fileUrl = null;
		
		try
		{
			FileAttachment fa;
			InputStream in;
			FileOutputStream out;
			String realPathToRootDir;
			String path;
			String pathToBrandingDir;
			File dir;
			File brandingImgFile;

			// Get the image as a FileAttachment.
			fa = brandingSourceBinder.getFileAttachment( imgName );
			if ( fa == null )
				throw new IOException( "Unable to get file attachment for branding image." );
			
			// Get the image's input stream
			in = allModules.getFileModule().readFile( brandingSourceBinder, brandingSourceBinder, fa );
			if ( in == null )
				throw new IOException( "Unable to get input stream for branding image." );

			// Find the file system path to /ssf/
			path = "/";
			realPathToRootDir = servletContext.getRealPath( path );
			if ( realPathToRootDir == null || realPathToRootDir.length() == 0 )
				throw new IOException( "Unable to get real path to static directory." );
			
			// Append the sub-directory name
			pathToBrandingDir = realPathToRootDir + "branding" + File.separator + subDirFSPath;
			
			// Create the directory where the branding image will be copied to.
			dir = new File( pathToBrandingDir );
			FileHelper.mkdirsIfNecessary( dir );
			
			// Create the branding image file.
			brandingImgFile = new File( dir.getAbsolutePath() + File.separator + imgName );
			brandingImgFile.createNewFile();
			out = new FileOutputStream( brandingImgFile );
			
			// Copy the branding image from the binder into the given directory
			FileCopyUtils.copy( in, out );
			
			fileUrl = WebUrlUtil.getSSFContextRootURL( httpReq ) + "branding/" + subDirUrlPath + "/" + imgName;

			in.close();
			out.close();
		}
		catch ( IOException ioEx )
		{
			m_logger.error( "Error copying branding image: " + imgName + " error: " + ioEx.getMessage() );
		}
		catch ( Exception ex )
		{
			m_logger.error( "Unknown error copying branding image: " + imgName );
		}

		return fileUrl;
	}

	/**
	 * Get the url to the given image which is an attachment to the given binder.
	 * We will try to copy the image to the ssf/branding/binder/binderId directory.  If that is
	 * successful we will return a url to that location.  Otherwise, we will return a "readFile"
	 * url.
	 * 
	 * We copy the image to the ssf/branding/login-dialog directory so it will be visible to
	 * all users (including the guest user).
	 */
	public static String getUrlToBinderBrandingImg(
		AbstractAllModulesInjected allModules,
		HttpServletRequest httpReq,
		ServletContext servletContext,
		Binder brandingSourceBinder,
		String imgName )
	{
		String webPath;
		String tmpUrl;
		String fileUrl;
		
		webPath = WebUrlUtil.getServletRootURL( httpReq );

		// Get a "readFile" url to the image
		fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );

		// Copy the image file to a location where all users (including guest) can see it.
		tmpUrl = copyBrandingImgToBrandingDir(
										allModules,
										httpReq,
										servletContext,
										brandingSourceBinder,
										imgName,
										"binder" + File.separator + brandingSourceBinder.getId(),
										"binder/" + brandingSourceBinder.getId() );
		
		if ( tmpUrl != null && tmpUrl.length() > 0 )
			fileUrl = tmpUrl;
		
		return fileUrl;
	}

	/**
	 * Get the url to the given image which is an attachment to the given binder.
	 * We will try to copy the image to the ssf/branding/login-dialog directory.  If that is
	 * successful we will return a url to that location.  Otherwise, we will return a "readFile"
	 * url.
	 * 
	 * We copy the image to the ssf/branding/login-dialog directory so it will be visible to
	 * all users (including the guest user).
	 */
	public static String getUrlToLoginBrandingImg(
		AbstractAllModulesInjected allModules,
		HttpServletRequest httpReq,
		ServletContext servletContext,
		Binder brandingSourceBinder,
		String imgName )
	{
		String webPath;
		String tmpUrl;
		String fileUrl;
		
		webPath = WebUrlUtil.getServletRootURL( httpReq );

		// Get a "readFile" url to the image
		fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );

		// Copy the image file to a location where all users (including guest) can see it.
		tmpUrl = copyBrandingImgToBrandingDir(
										allModules,
										httpReq,
										servletContext,
										brandingSourceBinder,
										imgName,
										"login-dialog",
										"login-dialog" );
		
		if ( tmpUrl != null && tmpUrl.length() > 0 )
			fileUrl = tmpUrl;
		
		return fileUrl;
	}
}
