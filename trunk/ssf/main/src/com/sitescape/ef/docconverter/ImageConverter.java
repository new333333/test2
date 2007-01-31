package com.sitescape.ef.docconverter;

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
