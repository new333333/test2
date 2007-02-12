package com.sitescape.team.applets.fileopen;

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


public class FileOpen extends JApplet implements Runnable {

    static FileOpen fileOpen;  // the only instance of ourself

    static final int maxButtonHeight = 25;
    static final int infWidth = 10000; // no restriction but must give value

    boolean alreadyStarted;
    final String fileReceiverUrlParamName = "fileReceiverUrl";
    String fileReceiverUrl = ""; // where to post files
    final String startingDirParamName = "startingDir";
    String startingDirStr = ""; // dir to place files in when uploading
    
    final String fileToOpen = "fileToOpen";
    final String editorType = "editorType";
    
    String strFileName = "";
    String strEditorTypes = "";

    ////////////////////////////////////////////////////////////////////////
    //
    //  Here's all the actual startup work, done in secondary thread.  See
    //  more comments at "start" routine.
    //
    ////////////////////////////////////////////////////////////////////////
    public void run () {
    	fileOpen = this;
    	try {
			strFileName = getParameter(fileToOpen);
			strEditorTypes = getParameter(editorType);
			
			String [] strEditorType = strEditorTypes.split(",");
			
			if (!strFileName.equals("") && !strEditorType.equals("")) {
					
				for (int i = 0; i < strEditorType.length; i++) {
					
        			boolean blnEditorErrorEncountered = false;
            		try {
                        String[] command =  new String[4];
                        command[0] = "cmd";
                        command[1] = "/C";
                        command[2] = "start " + strEditorType[i];
                        
                        String strURL = strFileName;
                        String strReplacedURL = strURL.replaceAll(" ", "%20");
                        
                        command[3] = strReplacedURL;
                        
                        Process p = Runtime.getRuntime().exec(command);
                        
                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        // read the output from the command
                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                            System.out.println("o/p: " + s);
                        }

                        // read any errors from the attempted command
                        System.out.println("Here is the standard error of the command (if any):\n");
                        
	                    while ((s = stdError.readLine()) != null) {
	                    	blnEditorErrorEncountered = true;
	                        System.out.println("Error: " + s);
	                    }
            		}
                	catch(IOException ioe) {
                		System.out.println("IO Err: "+ioe);
                	}
                	finally {
                		if (!blnEditorErrorEncountered)  break;
                	}
            	}
			}
    	}
    	catch(Exception e) {
    		System.out.println("Err: "+e);
    	}
    	finally {
    		strFileName = "";
    	}
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
        
        if (fileToOpen.equals(paramName)) {
            if (null == paramValue) {
                paramValue = "";
            }
        }
        
        // For these specific parameters, if none has been supplied,
        // we supply a default here.

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
            appletContext = fileOpen.getAppletContext();
        } catch(NullPointerException e) {
        }
        return appletContext;
    }

    private void onLoad(FileOpen fileOpen)
    {
      try {
        String onLoadFunction = fileOpen.getParameter("onLoadFunction");
        if (onLoadFunction.equals(null) || "".equals(onLoadFunction)) return;
        JSObject win = JSObject.getWindow(fileOpen);
        String args[] = {};
        Object foo = win.call(onLoadFunction,args);
      } catch (Exception ignored) { }
    }
    
    public void setFileToBeOpened(String strInputURL)
    {
      try {
    	strFileName = strInputURL;
      } 
      catch (Exception e) { 
    	  System.out.println("setFileToBeOpened: "+e);
      }
    }
    
} // end of class
