package com.sitescape.ef.docconverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class HtmlConverter 
{
	protected final Log logger = LogFactory.getLog(getClass());
	
	public abstract void convert(String ifp, String ofp, long timeout)
		throws Exception;
}
