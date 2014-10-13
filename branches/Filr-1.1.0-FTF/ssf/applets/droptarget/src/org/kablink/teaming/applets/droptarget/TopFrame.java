/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
 */
package org.kablink.teaming.applets.droptarget;

import java.applet.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
//import javax.net.*;
import javax.swing.JApplet;
import javax.swing.*;
import javax.swing.tree.*;
import netscape.javascript.JSObject;


public class TopFrame extends JApplet implements Runnable { 

    static TopFrame topFrame;  // the only instance of ourself

    static final int maxButtonHeight = 25;
    static final int infWidth = 10000; // no restriction but must give value

    boolean alreadyStarted;
    final String fileReceiverUrlParamName = "fileReceiverUrl";
    String fileReceiverUrl = ""; // where to post files
    final String fileCheckExistsUrlParamName = "fileCheckExistsUrl";
    String fileCheckExistsUrl = ""; // where to check files
    final String startingDirParamName = "startingDir";
    String startingDirStr = ""; // dir to place files in when uploading
    DataSink dataSink;


    ////////////////////////////////////////////////////////////////////////
    //
    //  Here's all the actual startup work, done in secondary thread.  See
    //  more comments at "start" routine.
    //
    ////////////////////////////////////////////////////////////////////////
    public void run () {
    	String strJavaVersion  = System.getProperty("java.version");
    	
        topFrame = this;

        Container cp = getContentPane();

       //        JList jFileList;

        // Allow secure interactions.
        Security.addProvider(
            new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty("java.protocol.handler.pkgs",
                           "com.sun.net.ssl.internal.www.protocol");
       // the following should be equivalent to
        // java -djavax.net.debug="ssl,session"
        // but isn't as I notice a can't-find-class-SocketFactory error
        // with former method
        System.setProperty("javax.net.debug", "");
        String task = null;
        setLookAndFeel();

        this.onLoad(this);

        fileReceiverUrl = getParameter(fileReceiverUrlParamName);
        dataSink = new DataSink(topFrame);

        cp.add(dataSink);
        JPanel p = (JPanel)getContentPane();
        p.revalidate();
    }


////////////////////////////////////////////////////////////////////////
//
//  The init routine is a standard applet startup routine.
//
////////////////////////////////////////////////////////////////////////
    public void init () {
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //    Note the extreme hackery performed in here whereby we force ourselves
    //    to do all the work in a separate thread, which apparently is the
    //    only way to allow drag-and-drop to work within an applet running in
    //    a browser.  I am grateful to Gene De Lisa at
    //    http://www.rockhoppertech.com/gene/ for this revelation.
    //
    ////////////////////////////////////////////////////////////////////////
    public void start () {
        if (this.alreadyStarted) {
            return;
        }
        else {
            this.alreadyStarted = true;
        }
        Thread thread = new Thread(this);
        thread.start();
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // We over-ride getParameter so we can read the ones supplied by class
    // Main as well as those sent by the PARAM tag within OBJECT tag in
    // html.
    //
    ////////////////////////////////////////////////////////////////////////
    public String getParameter(String paramName) {
        String paramValue;
        // Java doc says we merely get null if param not given, but behavior
        // seems to be that if we start from class Main instead of
        // loadApplet.html, we get exception instead of quiet null.  Hence
        // we use a "catch" here.  Then we check system properites as a way
        // to communicate parameters from class Main.
        try {
            paramValue = super.getParameter(paramName);
        } catch (NullPointerException e) {
            paramValue = System.getProperty(paramName);
        }
        // For these specific parameters, if none has been supplied,
        // we supply a default here.
        if (fileReceiverUrlParamName.equals(paramName))
            if (null == paramValue) {
                paramValue = "";
            }
        if (paramName.equals("startingDir"))
            if (null == paramValue) {
                paramValue = "";
            }
        if (paramName.equals("displayUrl"))
            if (null == paramValue) {
                paramValue = "1";
            }
        if (paramName.equals("savePreviousVersions"))
            if (null == paramValue) {
                paramValue = "yes";
            }
        if (paramName.equals("deactivationUrl"))
            if (null == paramValue) {
                paramValue = "";
            }
       if (paramName.equals("bgColorFunctionName"))
            if (null == paramValue) {
                paramValue = "";
            }
       if (paramName.equals("onLoadFunction"))
            if (null == paramValue) {
                paramValue = "";
            }
       if (paramName.equals("onCancelFunction"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("loadDirectory"))
           if (null == paramValue) {
               paramValue = "yes";
           }
       if (paramName.equals("directoryLoadErrorMessage"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("menuLabelPaste"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("menuLabelCancel"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("menuLabelDeactivate"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("fileLoadingInProgress"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("fileLoadingEnded"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("noFileAlertMessage"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("fileUploadNotSupported"))
           if (null == paramValue) {
               paramValue = "";
           }
       if (paramName.equals("isAppletChunkedStreamingModeSupported"))
           if (null == paramValue) {
               paramValue = "yes";
           }       
       if (paramName.equals("appletFileName"))
           if (null == paramValue) {
               paramValue = "ss_attachFile";
           }       
       return paramValue;
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Come here when submission of files is complete.
    //
    ////////////////////////////////////////////////////////////////////////
    void done () {
        //        System.exit(0); // hangs netscape 4.75
        destroy();
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //  This sets our look and feel.
    //
    /////////////////////////////////////////////////////////////////////////
    void setLookAndFeel() {
        String nativeStyle = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(nativeStyle);
            //            SwingUtilities.updateComponentTreeUI(parent);
        } catch (Exception e) {
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Given a string for an URL, make an actual URL object.
    //
    ////////////////////////////////////////////////////////////////////////
    static URL makeUrl(String UrlName) {
        URL completeUrl = null;
        try {
        completeUrl = new URL(UrlName);
        } catch (MalformedURLException e) {
        }
        return completeUrl;
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Given a File, make an actual URL object from it.  The "extras"
    // param may be null, but if not, contains extra params to tack on
    // the end of the url, for example "?color=red"
    //
    ////////////////////////////////////////////////////////////////////////
    static URL makeUrlFromFile(File file, String extras) {
        URL completeUrl = null;
        try {
        completeUrl = file.toURL();
        if (null != extras)
            completeUrl = new URL(completeUrl.toString() + extras);
        } catch (MalformedURLException e) {
        }
        return completeUrl;
    }




    ////////////////////////////////////////////////////////////////////////
    //
    // Determine whether we're in a browser or not.  If not, we
    // were presumably started up from class Main.  This routine returns
    // boolean true if we're in a browser.
    //
    ////////////////////////////////////////////////////////////////////////
    static boolean inBrowser() {
        AppletContext appletContext = getOurAppletContext();
        return (null != appletContext);


    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Determine whether we're in a browser or not.  If not, we
    // were presumably started up from class Main.  This routine returns
    // actual applet context if started from browser and null if not.
    //
    ////////////////////////////////////////////////////////////////////////
    static AppletContext getOurAppletContext() {
        AppletContext appletContext = null;
        try {
            appletContext = topFrame.getAppletContext();
        } catch(NullPointerException e) {
        }
        return appletContext;
    }


    private void onLoad(TopFrame topFrame)
    {
      try {
        String onLoadFunction = topFrame.getParameter("onLoadFunction");
        if (onLoadFunction.equals(null) || "".equals(onLoadFunction)) return;
        JSObject win = JSObject.getWindow(topFrame);
        String args[] = {};
        Object foo = win.call(onLoadFunction,args);
      } catch (Exception ignored) { }
    }

} // end of class
