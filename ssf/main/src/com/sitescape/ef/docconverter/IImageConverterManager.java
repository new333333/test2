package com.sitescape.ef.docconverter;

public interface IImageConverterManager
{
	static final String IMG_EXTENSION = ".jpg";
	static final int OPENOFFICE = 1,
	 				 STELLANT = 2;

	public ImageConverter getConverter();
	public ImageConverter getConverter(int type);
}
