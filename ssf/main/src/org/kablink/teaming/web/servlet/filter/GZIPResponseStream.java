/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package org.kablink.teaming.web.servlet.filter;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.servlet.*;
import javax.servlet.http.*;

public class GZIPResponseStream extends ServletOutputStream {
  // abstraction of the output stream used for compression
  protected OutputStream bufferedOutput = null;
  // state keeping variable for if close() has been called
  protected boolean closed = false;
  // reference to original response
  protected HttpServletResponse response = null;
  // reference to the output stream to the client's browser
  protected ServletOutputStream output = null;
  // default size of the in-memory buffer
  private int bufferSize = 50000;

  public GZIPResponseStream(HttpServletResponse response) throws IOException {
    super();
    closed = false;
    this.response = response;
    this.output = response.getOutputStream();
    bufferedOutput = new ByteArrayOutputStream();
  }

  public void close() throws IOException {
    // This hack shouldn't be needed, but it seems to make JBoss and Struts
    // like the code more without hurting anything.
    /*
    // verify the stream is yet to be closed
    if (closed) {
      throw new IOException("This output stream has already been closed");
    }
    */
    // if we buffered everything in memory, gzip it
    if (bufferedOutput instanceof ByteArrayOutputStream) {
      // get the content
      ByteArrayOutputStream baos = (ByteArrayOutputStream)bufferedOutput;
      // prepare a gzip stream
      ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();
      GZIPOutputStream gzipstream = new GZIPOutputStream(compressedContent);
      byte[] bytes = baos.toByteArray();
      gzipstream.write(bytes);
      gzipstream.finish();
      // get the compressed content
      byte[] compressedBytes = compressedContent.toByteArray();
      // set appropriate HTTP headers
      response.setContentLength(compressedBytes.length); 
      // Don't let this filter to say final word on Cache-Control. Instead, other
      // filter will make this decision based on the type and nature of the resource
      // being requested (bug #523610)
	  //response.addHeader("Cache-Control", "private");
      response.addHeader("Content-Encoding", "gzip");
      output.write(compressedBytes);
      output.flush();
      output.close();
      closed = true;
    }
    // if things were not buffered in memory, finish the GZIP stream and response
    else if (bufferedOutput instanceof GZIPOutputStream) {
      // cast to appropriate type
      GZIPOutputStream gzipstream = (GZIPOutputStream)bufferedOutput;
      // finish the compression
      gzipstream.finish();
      // finish the response
      output.flush();
      output.close();
      closed = true;
    }
  }

  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Cannot flush a closed output stream");
    }
    bufferedOutput.flush();
  }

  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    // make sure we aren't over the buffer's limit
    checkBufferSize(1);
    // write the byte to the temporary output
    bufferedOutput.write((byte)b);
  }

  private void checkBufferSize(int length) throws IOException {
    // check if we are buffering too large of a file
    if (bufferedOutput instanceof ByteArrayOutputStream) {
      ByteArrayOutputStream baos = (ByteArrayOutputStream)bufferedOutput;
      if (baos.size()+length > bufferSize) {
        // files too large to keep in memory are sent to the client without Content-Length specified
        // Don't let this filter to say final word on Cache-Control. Instead, other
        // filter will make this decision based on the type and nature of the resource
        // being requested (bug #523610)
    	//response.addHeader("Cache-Control", "private");
        response.addHeader("Content-Encoding", "gzip");
        // get existing bytes
        byte[] bytes = baos.toByteArray();
        // make new gzip stream using the response output stream
        GZIPOutputStream gzipstream = new GZIPOutputStream(output);
        gzipstream.write(bytes);
        // we are no longer buffering, send content via gzipstream
        bufferedOutput = gzipstream;
      }
    }
  }
  
  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte b[], int off, int len) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    // make sure we aren't over the buffer's limit
    checkBufferSize(len);
    // write the content to the buffer
    bufferedOutput.write(b, off, len);
  }

  public boolean closed() {
    return (this.closed);
  }
  
  public void reset() {
    //noop
  }

	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public void setWriteListener(WriteListener listener) {
	}
}
