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
package com.sitescape.team.applets.droptarget;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */


/**
 * Class PostFiles sends the files to the server.
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.applet.AppletContext;

import netscape.javascript.JSObject;

public class PostFiles extends Thread {
  String CR = System.getProperty("line.separator");
  AppletContext appletContext;
  OutputStream outputStream;
  TopFrame topFrame;
  String toAddr;
  ArrayList fileList;
  String topDir;
  String spv = "savePreviousVersions";

    private static void writeParam(String name, String value, OutputStream out, String boundary) {
        try {
            out.write(new String("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
            out.write(value.getBytes());
            out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
        } catch (Exception e) {
        }
    }

	private static String getEncodedFolderAndFileName(String strFolderAndFileName) throws Exception {
		String [] strSplitValue = strFolderAndFileName.split("/");
		String strEncodedFolder = "";
		for (int i = 0; i < strSplitValue.length; i++) {
			if (i != 0) {
				strEncodedFolder += "/" + URLEncoder.encode(strSplitValue[i], "UTF-8");  
			} else {
				strEncodedFolder = URLEncoder.encode(strSplitValue[i], "UTF-8");
			}
		}
		return strEncodedFolder;
	}    
    
    private static void writeFolderAndFileName(String localFileName, String topDir, TopFrame topFrame, String name, OutputStream out, String boundary) {
        String localfn;
        String localRelFileName;
        try {
            // replace any backslashes with slashes (backslashes are special in Java strings)
            String lfn = localFileName.replace('\\','/');
            String td = topDir.replace('\\','/');
            
            // clip off the topDir
            String relFileName = lfn.replaceFirst(td,"");

            // Drop the initial file separator
            if (relFileName.startsWith("/")) {
              localRelFileName = relFileName.substring(1);
            } else {
              localRelFileName = relFileName;
            }
            String localf = new String(topFrame.getParameter("startingDir")).replace('\\','/');
            
            if (localf.equals("")) {
              localfn = localRelFileName;
            } else {
              localfn = new String(localf + "/" + localRelFileName);
            }
            
            String strFolderName = "";
            if (localfn.lastIndexOf("/") != -1) {
            	strFolderName = localfn.substring(0, localfn.lastIndexOf("/"));
            }
            String strEncodedFolderName = getEncodedFolderAndFileName(strFolderName);
            
            out.write(new String("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
            out.write(strEncodedFolderName.getBytes());
            out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
        } catch (Exception e) {
        }
    }
    
    private  void writeFile(String localFileName, OutputStream out, String boundary, TopFrame topFrame, String topDir, String strFormFieldName) throws Exception {
        String localfn;
        String localRelFileName;
        try {
          System.gc();
          File localFile = new File(localFileName);
          
          // Get the path relative to the topDir, and then prepend it with the starting dir if
          // necessary. We need to strip off the topDir, so we can make the path relative to
          // the document we're attaching to.

          // replace any backslashes with slashes (backslashes are special in Java strings)
          String lfn = localFileName.replace('\\','/');
          String td = topDir.replace('\\','/');
          // clip off the topDir
          String relFileName = lfn.replaceFirst(td,"");
          
          // Drop the initial file separator
          if (relFileName.startsWith("/")) {
            localRelFileName = relFileName.substring(1);
          } else {
            localRelFileName = relFileName;
          }
          String localf = new String(topFrame.getParameter("startingDir")).replace('\\','/');

          if (localf.equals("")) {
            localfn = localRelFileName;
          } else {
            localfn = new String(localf + "/" + localRelFileName);
          }
          String strUTF8EncodedFileName = getEncodedFolderAndFileName(localfn);
          
          //out.write(new String("content-disposition: attachment; filename=\"" + localfn + "\"\r\n\r\n").getBytes());
          out.write(new String("content-disposition: form-data; name=\""+strFormFieldName+"\"; filename=\"" + strUTF8EncodedFileName + "\"\r\n\r\n").getBytes());

          FileInputStream fis = new FileInputStream(localFileName);
          if (localFile.length() != 0) {
            int amountRead=0;
            while (true) {
              synchronized (buffer) {
                amountRead = fis.read(buffer);
                if (amountRead == -1) {
                  break;
                }
                out.write(buffer, 0, amountRead);
              }
            }
          }
          fis.close();
          out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
        } catch (java.io.FileNotFoundException fnfe) {
        } catch (java.io.IOException ioe) {
        }
      }
    
    private  void writeFile(String localFileName, OutputStream out, String boundary, TopFrame topFrame, String topDir) {
      String localfn;
      String localRelFileName;
      try {
        System.gc();
        File localFile = new File(localFileName);
        // Get the path relative to the topDir, and then prepend it with the starting dir if
        // necessary. We need to strip off the topDir, so we can make the path relative to
        // the document we're attaching to.

        // replace any backslashes with slashes (backslashes are special in Java strings)
        String lfn = localFileName.replace('\\','/');
        String td = topDir.replace('\\','/');
        // clip off the topDir
        String relFileName = lfn.replaceFirst(td,"");

        // Drop the initial file separator
        if (relFileName.startsWith("/")) {
          localRelFileName = relFileName.substring(1);
        } else {
          localRelFileName = relFileName;
        }
        String localf = new String(topFrame.getParameter("startingDir")).replace('\\','/');

        if (localf.equals("")) {
          localfn = localRelFileName;
        } else {
          localfn = new String(localf + "/" + localRelFileName);
        }
        
        //out.write(new String("content-disposition: attachment; filename=\"" + localfn + "\"\r\n\r\n").getBytes());
        out.write(new String("content-disposition: form-data; name=\""+ topFrame.getParameter("appletFileName") +"\"; filename=\"" + localfn + "\"\r\n\r\n").getBytes());

        FileInputStream fis = new FileInputStream(localFileName);
        if (localFile.length() != 0) {
          int amountRead=0;
          while (true) {
            synchronized (buffer) {
              amountRead = fis.read(buffer);
              if (amountRead == -1) {
                break;
              }
              out.write(buffer, 0, amountRead);
            }
          }
        }
        fis.close();
        out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
      } catch (java.io.FileNotFoundException fnfe) {
      } catch (java.io.IOException ioe) {
      }
    }

    static final int BUFF_SIZE = 1024 * 128;
    static final byte[] buffer = new byte[BUFF_SIZE];

    /** Creates a new instance of PostFileConnection */
    public PostFiles() {
    }

    /**
    * @param args the command line arguments
    * Note: turned this into a threaded class to help the droptarget set icons
    * in a timely manner. (When this wasn't a separate thread, the droptarget
    * couldn't change the icon till the event handler finished.)
    */
    public PostFiles(TopFrame tf,String ta, ArrayList fl, String td) {
      topFrame = tf;
      toAddr = ta;
      fileList = fl;
      topDir = td;
      start();
    }
    
    public void run() {

      String unlockParam;
      String prevParam;
      String filename = "";
      HttpURLConnection conn=null;
      int totalBytes = 0;
      // Figure out the total number of bytes to send to the server
      totalBytes = countem(fileList);
        try {
            URL postTo = new URL(toAddr);
            conn= (HttpURLConnection)postTo.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            String boundary = "---------------------------7d72fd2f300520";
            
            conn.setRequestProperty("Content-type","multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            conn.setRequestProperty("Accept-Charset", "utf-8;q=0.9");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.connect();
            
            OutputStream out = conn.getOutputStream();
            //ChunkedOutputStream out = new ChunkedOutputStream(conn.getOutputStream());
            out.write(new String("--" + boundary + "\r\n").getBytes());
            
            String strFileName = topFrame.getParameter("appletFileName");
            
            for (int i=0; i<fileList.size(); i++) {
              File file = (File)(fileList.get(i));
              String localFilePath = file.getAbsolutePath();
              File f = new File(localFilePath);
              if (!f.canRead()) {
                 continue;
              }
              filename = f.getName();
             
              //Hemanth: 06/13/2007 - Used for Applet specific code in AbstractEntryProcessor.createNewEntryWithAttachmentAndTitle
              //writeFile(localFilePath, out, boundary, topFrame, topDir, "filesFromApplet"+(i+1));
              writeFile(localFilePath, out, boundary, topFrame, topDir, strFileName+(i+1));
              
              writeParam("startingDir", filename, out, boundary);
              writeParam("savePreviousVersions", topFrame.getParameter("savePreviousVersions"), out, boundary);
              writeFolderAndFileName(localFilePath, topDir, topFrame, "filesFromAppletFolderInfo"+(i+1), out, boundary);              
            }
            
            writeParam("lastDummyParam", "dummy", out, boundary+"--");

            try {
              out.flush();
              out.close();
            } catch (Exception ce) {
              displayResponse(topFrame,conn);
            }
            displayResponse(topFrame, conn);

        } catch (java.lang.OutOfMemoryError oome) {
        	//Hemanth: right now we cannot put the string to be translated into the messages.properties file
        	//once we are ready to put message strings into messages.properties file, we can put the error message
        	//into the messages.properties file and use it from there. Until then, we will use the hardcoded
        	//error messages mentioned below.
        	String strErrorMessage = "There is a problem with the file being uploaded. \nPlease check to see if file size is large.";
        	System.out.println("Error Uploading File: Out of Memory Error: "+ oome + "\n" + strErrorMessage);
        	reportErrorMessage(topFrame, conn, strErrorMessage);
        } catch (Exception e) {
        	System.out.println("Error Uploading File: Exception: "+e);
        	reportErrorMessage(topFrame, conn, e.toString());
        }
    }
    
    public void reportErrorMessage(TopFrame topFrame, HttpURLConnection conn, String strError) {
		try {
			topFrame.dataSink.changeIcon(topFrame.dataSink.StaticGif);
            topFrame.dataSink.fileLoadingEnded();
			
			String reloadFunction = topFrame.getParameter("reloadFunctionName");
			String uploadErrorMessage = topFrame.getParameter("uploadErrorMessage");
			if (reloadFunction == null || strError == null) return;
			JSObject win = JSObject.getWindow(topFrame);
			String args[] = {uploadErrorMessage + " : \n" + strError};
			Object foo = win.call(reloadFunction,args);
		} catch (Exception e) { 
			System.out.println("reportErrorMessage: e " + e);
		}
        finally {
        	conn.disconnect();
        	conn = null;
        }
    }
    
    /**
     * The web server is going to sendback an html page which contains the
     * results of the upload. This routine pulls it down, and displays it, and
     * then exits the applet.
     */
    public void displayResponse( TopFrame topFrame, HttpURLConnection conn) {
        try {
            InputStream httpStream = conn.getInputStream();
           
            boolean writing = false;
            String url = "";
            String jsfunc = "";

            String du = new String(topFrame.getParameter("displayUrl"));
            
            if (!du.equals("1")) {
              topFrame.dataSink.changeIcon(topFrame.dataSink.StaticGif);
              topFrame.dataSink.fileLoadingEnded();
			  try {
				  String reloadFunction = topFrame.getParameter("reloadFunctionName");
				  
				  if (reloadFunction.equals(null)) return;
				  JSObject win = JSObject.getWindow(topFrame);
				  String args[] = {url};
			      Object foo = win.call(reloadFunction,args);
				} catch (Exception ignored) { }
              return;
            }

            while (true) {
				byte bytes[] = new byte[5000];
				// Read available data.
				int nBytes = httpStream.read(bytes);
				if (nBytes == -1) {
					break;
				}
				if (!writing) {
					writing = true;
				}
				url += new String(bytes).toString().trim();
				
				System.out.println("url: "+url);
				
            }            
            
            if (writing) {
				writing = false;
				if (url == null) {
					url = "";
				} else {
					url = url.trim();
				}
				//url = url.substring(4);
				topFrame.dataSink.changeIcon(topFrame.dataSink.StaticGif);
	            topFrame.dataSink.fileLoadingEnded();
				try {
					String reloadFunction = topFrame.getParameter("reloadFunctionName");
					if (reloadFunction.equals(null)) return;
					JSObject win = JSObject.getWindow(topFrame);
					String args[] = {url};
					Object foo = win.call(reloadFunction,args);
				} catch (Exception ignored) { }
            }
        } catch (IOException e) {
        	System.out.println("handleResponse saw Exception:" + e);
        	return;
        }
        finally {
        	conn.disconnect();
        	conn = null;
        }
    }
    private int countem(ArrayList fileList)
    {
      int fileBytes = 0;
      for (int i=0; i<fileList.size(); i++) {
        File file = (File)(fileList.get(i));
        String localFilePath = file.getAbsolutePath();
        File f = new File(localFilePath);
        if (!f.canRead())
          continue;
        fileBytes += f.length();
      }
      return fileBytes;
    }
}