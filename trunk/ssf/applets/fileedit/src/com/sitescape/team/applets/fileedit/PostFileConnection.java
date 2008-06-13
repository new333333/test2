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
package com.sitescape.team.applets.fileedit;
/**
 * Class PostFileConnection posts the resultant file to the server.
 */

import java.net.*;
import java.io.*;
import java.applet.AppletContext;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container;
import netscape.javascript.JSObject;
import javax.swing.JProgressBar;

public class PostFileConnection {
  String CR = System.getProperty("line.separator");
  AppletContext appletContext;
  FEData data;


    private static void writeParam(String name, String value, DataOutputStream out, String boundary) {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n" + "--" + boundary + "\r\n");
        } catch (Exception e) {  System.out.println(e.toString());      }
    }

    private static void writeFile(FEData data, DataOutputStream out, String boundary) {
      final int BUFF_SIZE = 1024;
      byte[] buffer = new byte[BUFF_SIZE];
      try {

        out.writeBytes("content-disposition: attachment; filename=\""
                            + data.getForumFileName() + "\"\r\n\r\n");

            //Debug.debug("LocalFilename = " + data.getLocalFileName());
            FileInputStream fis = new FileInputStream(data.getLocalFileName());
            while (true) {
                synchronized (buffer) {
                    int amountRead = fis.read(buffer);
                        if (amountRead == -1) {
                            break;
                        }
                    out.write(buffer, 0, amountRead);
                    }
            }
            fis.close();
            out.writeBytes("\r\n" + "--" + boundary + "\r\n");
       } catch (Exception e) {  System.out.println(e.toString());      }
    }



    /** Creates a new instance of PostFileConnection */
    public PostFileConnection() {
    }

    /**
    * @param args the command line arguments
    */
    public PostFileConnection(FEData data) {
      String unlockParam;
      String prevParam;
      String value = "";
        try {
            this.data = data;
            appletContext = data.getApplet().getAppletContext();
            URL postee = new URL(data.getPostUrl());
            HttpURLConnection conn=(HttpURLConnection)postee.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setUseCaches(false);
            String boundary = "---------------------------7d226f700d0";
            conn.setRequestProperty("Content-type","multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Cache-Control", "private");
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes("--" + boundary + "\r\n");
            if (data.getUnlockFlag())
              unlockParam = "on";
            else
              unlockParam = "off";
            writeParam("unlockEntry", unlockParam, out, boundary);
            if (data.getSavePrevFlag())
              prevParam = "on";
            else
              prevParam = "off";
            writeParam("saveVersions", prevParam, out, boundary);
            if (data.getUseNotificationBlock()) {
                if (data.getBlockNotification())
                    value = "yes";
                else
                    value = "no";
                writeParam("notificationBlock", value, out, boundary);
            }
            if (data.getUploadFlag() && data.getUpdateInForum())
              writeFile(data, out, boundary);
            out.flush();
            out.close();

            displayResponse(data, conn);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * The web server is going to sendback a url to display. This routine
     * pulls it down, and displays it, and then exits the applet.
     */
    public void displayResponse( FEData data, HttpURLConnection conn) {
        boolean error = false;
        try {
            InputStream httpStream = conn.getInputStream();
            File responseFile = null;
            boolean writing = false;
            FileOutputStream toStream = null;
            String url = "";
            String jsfunc = "";

            while (true)
              {
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
                for (int i = 0; i<url.length(); i++) {
                  if (url.startsWith("errorPage"))
                  {
                    error = true;
                    break;
                  }
                }
                if (!error)
                  deleteFile(data);

                if (true)
                {
                   AppletContext appletContext = data.getApplet().getAppletContext();
                   appletContext.showDocument(new URL(url),"_self");
                }
                //else
                //{
                //  jsfunc = new String("loadURL");
                //  try {
                //    JSObject win = JSObject.getWindow(data.getApplet());
                //    String args[] = {url.toString()};
                //    Object foo = win.call(jsfunc,args);
                //  } catch (Exception e) {
                //  }
                //}
            }
        } catch (IOException e)
        {
          //Debug.debug("handleResponse saw Exception:" + e);
          return;
        }
    }
    public void deleteFile(FEData data)
    {
      File tfile = new File(data.getLocalFileName());
      tfile.delete();
    }
}
