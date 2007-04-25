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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FilePathUtil;
import com.sitescape.team.util.FileStore;
import com.sitescape.team.util.SPropsUtil;

public abstract class HtmlConverter extends Converter<String>
{
	private static final String HTML_SUBDIR = "html";
	private static final String HTML_FILE_SUFFIX = ".html";
	protected final Log logger = LogFactory.getLog(getClass());	
    
    /**
     * Alter tag data held in HTML file. We need to alter Image and Url file path information to reflect were
     * the image or files actually reside on the server. After converted a file into an HTML file the Image an
     * Url file paths are specified to be relative to HTML file just generated. We must change these entries so
     * we can recall these items and stream items into browser.
     * 
     * @param indata		Data to check for alterations
     * @param tag			What tag item are we going to look to change
     * @param attrtag		What attribute on 'tag' are we going to change
     * @param newdata		New data to insert into attribute we are changing
     * 
     * @return				Altered 'indata'
     */
    private StringBuffer alterTagData(String indata, String tag, String attrtag, String newdata)
	{
		String[] splits = null;
		StringBuffer buffer = null;
		String s = "",
			   src = "",
			   data = "",
			   altdata = "",
			   predata = "",
			   imageurl = "";
		
		buffer = new StringBuffer();
		splits = indata.split(tag);
        for (int x=0; x < splits.length; x++)
        {
        	s = splits[x];
        	int i = s.indexOf(attrtag);
        	if (i > -1)
        	{
        		predata = s.substring(0, i);
        		data = s.substring(i + attrtag.length());
        		imageurl = data.substring(0, data.indexOf("\""));
        		if (imageurl.startsWith("#")
        		|| imageurl.startsWith("http:")
        		|| imageurl.startsWith("https:"))
        			src = imageurl;
        		else
        			src = newdata + imageurl;
        		
        		altdata = tag + predata;
        		altdata += attrtag + (src + "\"" + data.substring(data.indexOf("\"")+1));
        		buffer.append(altdata);
        	}
        	else
        	// we could have a file like (ex) this is a test <a name='rsordillo' /> for testing
        	// we would not want to add 'tag' to beginning of file
        	if (x == 0)
        		buffer.append(s);
        	else
        		buffer.append(tag + s);
        }
        
        return buffer;
	}
	
    /**
     * Parse HTML file replacing URL an IMAGE paths to conform with were the actual Images or Url files exist
     * on the system.
     * 
     * @param fin			Input file to be adjusted
     * @param fout			Output file after adjustments have been made
     * @param attrdata		What attribute data to change if required
     * 
     * @throws Exception	Something goes wrong with parsing/changing input file
     * 
     */
	public void parseHtml(File fin, File fout, String attrdata)
		throws Exception
	{
		int length = 2048;
		FileReader fr = null;
		FileWriter fw = null;
		char[] cbuf = new char[length];
		StringBuffer buffer = null;
		String fileData = "";
		
		try
		{
			buffer = new StringBuffer();
			
			fr = new FileReader(fin);			
			while (fr.read(cbuf, 0, length) > -1)
			{
				buffer.append(cbuf);
				// clear buffer
				for (int x=0; x < cbuf.length; x++)
					cbuf[x] = '\0';
			}
			fileData = buffer.toString().trim();
			fr.close();
	
			buffer = alterTagData(fileData, "<img ", "src=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<IMG ", "SRC=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<a ", "href=\"", attrdata.replaceAll("XXXX", "url"));
			buffer = alterTagData(buffer.toString(), "<A ", "HREF=\"", attrdata.replaceAll("XXXX", "url"));
			
			fw = new FileWriter(fout);
	        fw.write(buffer.toString());
		}
		finally
		{
			try
			{
				if (fr != null)
					fr.close();
			} catch (Exception e) {}
			
			try
			{
				if (fw != null)
				{
					fw.flush();
					fw.close();
				}
			}
			catch (Exception e) {}
		}
        
        return;
	}

	private void cleanUpURLs(String outFile, String url, Long binderId, Long entryId, String fileId)
		throws IOException
	{
		try
		{
			int j = outFile.lastIndexOf(File.separator);
			//outFile = outFile.substring(0, j+1) + fileId + File.separator + outFile.substring(j+1);
			
			// When generating the HMTL equivalent file.
			// Many HTML files can be generated. Open file(s) an make adjustments to image src attribute
			// Every HTML file in directory should be related to converter process
			File outputDir = new File(outFile.substring(0, j+1));
			if (outputDir.isDirectory())
			{
				String src = url + "?binderId=" + binderId + "&entryId=" + entryId + "&fileId=" + fileId + "&viewType=XXXX&filename=";
				File[] files = outputDir.listFiles();
				for (int x=0; x < files.length; x++)
				{
					if (files[x].isFile() && files[x].getName().endsWith(".html"))
						parseHtml(files[x], files[x], src);
				}
			}
		}
		catch (Exception e)
		{
			throw new IOException(e.getLocalizedMessage());
		}
	}
	
	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
			String filePath, String relativeFilePath, String url)
		throws IOException
	{
		super.createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, url);
		cleanUpURLs(convertedFile.getAbsolutePath(), url, binder.getId(), entry.getId(), fa.getId());
	
	}
	
	public InputStream convert(String url, Binder binder, DefinableEntity entry, FileAttachment fa)
		throws IOException
	{
		return super.convert(binder, entry, fa, url, HTML_SUBDIR, HTML_FILE_SUFFIX);
	}
	
	public InputStream getCachedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String fileName)
	throws IOException
	{
		return getCachedFile(binder, entry, fa, fileName, HTML_SUBDIR);
	}
}
