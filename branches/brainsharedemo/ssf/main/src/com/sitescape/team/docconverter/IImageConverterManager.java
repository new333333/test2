package com.sitescape.team.docconverter;

public interface IImageConverterManager
{
	static final String IMG_EXTENSION = ".jpg";
	static final int OPENOFFICE = 1,
	 				 STELLANT = 2,
	 				 IMAGEWIDTH = 200,
	 				 IMAGEHEIGHT = 200;

	public ImageConverter getConverter();
	public ImageConverter getConverter(int type);
}
