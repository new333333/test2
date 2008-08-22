package com.sitescape.team.remoting.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import javax.activation.DataSource;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import com.sitescape.team.module.mail.MailModule;

public class CalendarDataSource implements DataSource {
	String data = "";
		
	public CalendarDataSource(Calendar cal)
	{
		StringWriter writer = new StringWriter();
		CalendarOutputter out = new CalendarOutputter();
		try {
			out.output(cal, writer);
			data = writer.toString();
		} catch(IOException e) {
		} catch(ValidationException e) {
		}
	}
		
	public String getName() { return "com.sitescape.team.CalendarDataSource"; }
	public String getContentType() { return MailModule.CONTENT_TYPE_CALENDAR; }
		
	public InputStream getInputStream() throws IOException
	{
		return new StringBufferInputStream(data);
	}
		
	public OutputStream getOutputStream() throws IOException
	{
		throw new IOException("Output not supported to this DataSource");
	}

}
