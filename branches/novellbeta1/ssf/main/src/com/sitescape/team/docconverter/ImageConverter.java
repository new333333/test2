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
package com.sitescape.team.docconverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ImageConverter 
{
	protected String _defaultImage = "";
	protected final Log logger = LogFactory.getLog(getClass());
	protected String _nullTransform = "";
	
	public abstract String convert(String ifp, String ofp, long timeout, int maxWidth, int maxHeight)
		throws Exception;
	
	public void setDefaultImage(String image_in)
	{
		_defaultImage = image_in;
	}
	
	public String getDefaultImage()
	{
		return _defaultImage;
	}
}
