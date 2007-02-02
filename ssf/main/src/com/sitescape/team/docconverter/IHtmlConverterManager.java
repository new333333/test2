package com.sitescape.team.docconverter;

public interface IHtmlConverterManager
{
	static final int OPENOFFICE = 1,
					 STELLANT = 2;
	
	public HtmlConverter getConverter();
	public HtmlConverter getConverter(int type);
}
