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
            
            out.write(new String("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
            out.write(strFolderName.getBytes());
            out.write(new String("\r\n" + "--" + boundary + "\r\n").getBytes());
        } catch (Exception e) {
        }
    }
    
    private  void writeFile(String localFileName, OutputStream out, String boundary, TopFrame topFrame, String topDir, String strFormFieldName) {
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
          out.write(new String("content-disposition: form-data; name=\""+strFormFieldName+"\"; filename=\"" + localfn + "\"\r\n\r\n").getBytes());

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
        out.write(new String("content-disposition: form-data; name=\"ss_attachFile\"; filename=\"" + localfn + "\"\r\n\r\n").getBytes());

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
            conn.setRequestProperty("Connection", "keep-alive");
            conn.connect();
            
            OutputStream out = conn.getOutputStream();
            //ChunkedOutputStream out = new ChunkedOutputStream(conn.getOutputStream());
            out.write(new String("--" + boundary + "\r\n").getBytes());
            
            for (int i=0; i<fileList.size(); i++) {
              File file = (File)(fileList.get(i));
              String localFilePath = file.getAbsolutePath();
              File f = new File(localFilePath);
              if (!f.canRead()) {
                 continue;
              }
              filename = f.getName();
              
              writeFile(localFilePath, out, boundary, topFrame, topDir, "filesFromApplet"+(i+1));
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
          String jsfunc = new String("memoryError");
        } catch (Exception e) {
        	System.out.println("Exception e: "+e);
            displayResponse(topFrame, conn);
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
            }
            
            if (writing) {
				writing = false;
				url = url.substring(4);
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