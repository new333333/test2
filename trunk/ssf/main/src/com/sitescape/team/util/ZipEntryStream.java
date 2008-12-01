package com.sitescape.team.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipEntryStream extends InputStream {
	ZipInputStream zipIn;
	public ZipEntryStream(ZipInputStream zipIn)
	{
		this.zipIn = zipIn;
	}
		
	public int read() throws IOException
	{
		return zipIn.read();
	}
		
	public void close() throws IOException
	{
	}

}
