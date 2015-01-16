/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 *
 */
package org.kablink.teaming.applets.droptarget;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.applet.AppletContext;

import javax.swing.*;

import netscape.javascript.JSObject;

/**
 * Class PostFiles sends the files to the server.
 */
public class PostFiles extends Thread {
  String CR = System.getProperty("line.separator");
  AppletContext appletContext;
  OutputStream outputStream;
  TopFrame topFrame;
  String toAddr;
  String checkExistsAddr;
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
            String relFileName;
            if (0 == lfn.indexOf(td))
                 relFileName = lfn.substring(td.length());
            else relFileName = lfn;

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
        FileInputStream fis = null;
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
          String relFileName;
          if (0 == lfn.indexOf(td))
               relFileName = lfn.substring(td.length());
          else relFileName = lfn;
          
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

          fis = new FileInputStream(localFileName);
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
          fis = null;
          out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
        } catch (java.io.FileNotFoundException fnfe) {
        } catch (java.io.IOException ioe) {
        } finally {
        	if (fis != null) {
        		try {
        			fis.close();
        		} catch(Exception e) {}
        	}
        }
      }
    
    private  void writeFile(String localFileName, OutputStream out, String boundary, TopFrame topFrame, String topDir) {
      String localfn;
      String localRelFileName;
      FileInputStream fis = null;
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
        String relFileName;
        if (0 == lfn.indexOf(td))
             relFileName = lfn.substring(td.length());
        else relFileName = lfn;

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

        fis = new FileInputStream(localFileName);
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
        fis = null;
        out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
      } catch (java.io.FileNotFoundException fnfe) {
      } catch (java.io.IOException ioe) {
      } finally {
    	  if (fis != null) {
    		  try {
    			  fis.close();
    		  } catch(Exception e) {}
    	  }
      }
    }

    static final int CHUNK_SIZE = 1024 * 8;
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
    public PostFiles(TopFrame tf, String ta, String ca, ArrayList fl, String td) {
      topFrame = tf;
      toAddr = ta;
      checkExistsAddr = ca;
      fileList = fl;
      topDir = td;
    }
    
    public void run() {

      String unlockParam;
      String prevParam;
      String filename = "";
      HttpURLConnection conn=null;
      int totalBytes = 0;
      boolean sendFiles = false;
      try {
    	  sendFiles = checkForExistingFiles(topFrame, checkExistsAddr, fileList);
      } catch(Exception e) {
      	Debug.writeLog("Error Checking File List: "+e);
    	reportErrorMessage(topFrame, conn, e.toString());
      }
      if (!sendFiles) {
    	  //We are not going to send any files to the server
    	  fileList.clear();
      }
      
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
            conn.setRequestProperty("Cache-Control", "private");
            conn.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            conn.setRequestProperty("Accept-Charset", "utf-8;q=0.9");
            conn.setRequestProperty("Connection", "keep-alive");
            if ("yes".equals(topFrame.getParameter("isAppletChunkedStreamingModeSupported"))) {
            	conn.setChunkedStreamingMode(CHUNK_SIZE);
            }
            try {
            	conn.connect();
            } catch(Exception e) {
            	Debug.writeLog("Error Uploading File: Could not connect to the server. Exception: "+e);
            	reportErrorMessage(topFrame, conn, e.toString());
            }
            
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
             
              String sFileSizeLimit = new String(topFrame.getParameter("fileUploadMaxSize"));
              if (!sFileSizeLimit.equals("")) {
            	  //There is a file upload size limit, check it
            	  Long sizeLimit = Long.valueOf(sFileSizeLimit);
            	  if (f.length() > sizeLimit.longValue()) {
                      Debug.writeLog("File size limit: " + sFileSizeLimit);
                      Debug.writeLog("File size: " + String.valueOf(f.length()));
                  	  String translatedString = new String(topFrame.getParameter("fileUploadSizeExceeded"));
            		  Debug.writeLog(translatedString);
            		  reportErrorMessage(topFrame, conn, translatedString + " (" + filename + ")" );
            		  continue;
            	  }
              }
              
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
        	String strErrorMessage = "There is a problem with the file being uploaded. \nIt is likely that the file size is too large.";
        	String translatedString = new String(topFrame.getParameter("uploadErrorFileTooLarge"));
        	if (translatedString != null && !translatedString.equals("")) strErrorMessage = translatedString;
        	Debug.writeLog("Error Uploading File: Out of Memory Error: "+ oome + "\n" + strErrorMessage);
        	reportErrorMessage(topFrame, conn, strErrorMessage);
        } catch (Exception e) {
        	Debug.writeLog("Error Uploading File: Exception: "+e);
        	reportErrorMessage(topFrame, conn, e.toString());
        }
    }
    
    //Routine to check that there are files being overwritten
    public boolean checkForExistingFiles(TopFrame tf, String fileCheckUrl, List fileList) {
    	boolean result = true;
    	HttpURLConnection connection = null;
    	OutputStreamWriter wr = null;
    	BufferedReader rd  = null;
    	StringBuilder sb = null;
    	String line = null;
    	List existingFiles = new ArrayList();
	
    	URL serverAddress = null;
	
    	try {
    		serverAddress = new URL(fileCheckUrl);
    		//set up out communications stuff
    		connection = null;
	
    		//Set up the initial connection
    		connection = (HttpURLConnection)serverAddress.openConnection();
    		connection.setRequestMethod("GET");
    		connection.setDoOutput(true);
    		connection.setReadTimeout(10000);
	            
    		connection.connect();
	
    		//get the output stream writer and write the output to the server
    		wr = new OutputStreamWriter(connection.getOutputStream());
    		StringBuffer sbuf = new StringBuffer();
    		sbuf.append("fileNames=");
    		for (int i = 0; i < fileList.size(); i++) {
    			File file = (File)(fileList.get(i));
    			if (i > 0) sbuf.append(",");
    			sbuf.append(file.getName());
    		}
    		wr.write(sbuf.toString());
    		wr.flush();
	
    		//read the result from the server
    		//The server will return a list of files that already exist
    		//If nothing is returned, then there were no files that already existed
    		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		sbuf = new StringBuffer();
    		String str;
    		while ((str = rd.readLine()) != null) {
    			if (!str.trim().equals("")) {
    				sbuf.append(str.trim() + "\n");
    			}
    		}
    		if (sbuf.length() > 0) {
	    		//Custom button text
	    		Object[] options = {tf.getParameter("strYes"),
	    				tf.getParameter("strNo")};
	    		int n = JOptionPane.showConfirmDialog(
	    				tf,
	    				tf.getParameter("strFilesExistConfirm") + "\n" + sbuf.toString(),
	    				tf.getParameter("strFilesExist"),
	    			    JOptionPane.YES_NO_OPTION);
	    		if (n == 0) {
	    			result = true;
	    		} else {
	    			result = false;
	    		}
    		}
    		
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	} catch (ProtocolException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	finally
    	{
    		//close the connection, set all objects to null
    		connection.disconnect();
    		rd = null;
    		sb = null;
    		wr = null;
    		connection = null;
    	}
    	return result;
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
			Debug.writeLog("reportErrorMessage: e " + e);
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
            InputStreamReader httpStreamReader = new InputStreamReader(conn.getInputStream(),"UTF-8");
           
            boolean writing = false;
            String url = "";
            String jsfunc = "";

            String du = new String(topFrame.getParameter("displayUrl"));

            while (true) {
				char chars[] = new char[5000];
				// Read available data.
				int nBytes = httpStreamReader.read(chars);
				if (nBytes == -1) {
					break;
				}
				if (!writing) {
					writing = true;
				}
				url += new String(chars).toString().trim();
            }            

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

			Debug.writeLog("url: "+url);

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
        	Debug.writeLog("handleResponse saw Exception:" + e);
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
