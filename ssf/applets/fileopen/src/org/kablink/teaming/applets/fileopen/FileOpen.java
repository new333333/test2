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
 */
package org.kablink.teaming.applets.fileopen;

import java.applet.*;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import netscape.javascript.JSObject;

/**
 * The edit-in-place applet.
 *  
 * @author ?
 */
public class FileOpen extends JApplet implements Runnable {
    static FileOpen fileOpen;  // the only instance of ourself

    static final int maxButtonHeight = 25;
    static final int infWidth = 10000; // no restriction but must give value

    boolean alreadyStarted;
    final String vibeAddinEditor = "addinedit";
    final String fileReceiverUrlParamName = "fileReceiverUrl";
    String fileReceiverUrl = ""; // where to post files
    final String startingDirParamName = "startingDir";
    String startingDirStr = ""; // dir to place files in when uploading
    
    final String fileToOpen = "fileToOpen";
    final String editorType = "editorType";
    
    final String checkEditClicked = "checkEditClicked";
    final String resetEditClicked = "resetEditClicked";
    final String operatingSystem = "operatingSystem";
    
    String strUserName = "";
    String strFileName = "";
    String strEditorTypes = "";
    String strOperatingSystem = "";
    String uploadErrorMessage = "";
    String editorErrorMessage = "";
    boolean isLicenseRequiredEdition = false;
    boolean isOfficeAddInAllowed = true;

    ////////////////////////////////////////////////////////////////////////
    //
    //  Here's all the actual startup work, done in secondary thread.  See
    //  more comments at "start" routine.
    //
    ////////////////////////////////////////////////////////////////////////
    public void run () {
        String msg;
    	fileOpen = this;
        strUserName = getParameter("userName");
		strFileName = getParameter(fileToOpen);
		strEditorTypes = getParameter(editorType);
		strOperatingSystem = getParameter(operatingSystem);
		uploadErrorMessage = getParameter("uploadErrorMessage");
		if (uploadErrorMessage == null || uploadErrorMessage.equals("")) uploadErrorMessage = "Error";
		editorErrorMessage = getParameter("EditorErrorMessage");
		if (editorErrorMessage == null || editorErrorMessage.equals("")) editorErrorMessage = "Error from command";
        isLicenseRequiredEdition = Boolean.parseBoolean(getParameter("isLicenseRequiredEdition"));
        isOfficeAddInAllowed = Boolean.parseBoolean(getParameter("isOfficeAddInAllowed"));
		try {
			boolean ifEditIsClicked = checkEditClicked();
			resetEditClicked();
			String [] strEditorType = strEditorTypes.split(",");
			
			if (!strFileName.equals("") && !strEditorType.equals("") && ifEditIsClicked) {
					
				for (int i = 0; i < strEditorType.length; i++) {
					
        			boolean blnEditorErrorEncountered = false;
            		try {
                        String strURL = strFileName;
                        String [] command;
                        if (strOperatingSystem.equalsIgnoreCase("windows")) {
                            String[] editorAndUrl = getWindowsEditorAndUrl(strEditorType[i], strURL);
                       	 	command =  new String[4];
                            command[0] = "cmd";
                            command[1] = "/C";
                            command[2] = "start " + editorAndUrl[0];
                            command[3] = editorAndUrl[1];
                            //System.out.println("command: "+ command[0] + " " + command[1] + " " + command[2] + " " + command[3]);
                        } else if (strOperatingSystem.equalsIgnoreCase("linux") || strOperatingSystem.equalsIgnoreCase("mac")) {
                        	command =  new String[3];
                            command[0] = "bash";
                            command[1] = "-c";
                            //command[2] = "ooffice " + "'"+strReplacedURL+"'";
                            command[2] = strEditorType[i] + " '"+strURL+"'";
                            //System.out.println("command: "+ command[0] + " " + command[1] + " " + command[2]);
                        } else {
                        	msg = (uploadErrorMessage + ":\n Operating System " + strOperatingSystem + " not Handled!");
                        	System.out.println(msg);
                       		makeJSAlertCall(msg);
                        	return;
                        }
                        
                        // Bugzilla 481096:  Only show a JavaScript
                        // alert(...) for the data from the stdout and
                        // stderr streams if the application returns a
                        // non-0 exit code.
                        Process p = Runtime.getRuntime().exec(command);
                        p.waitFor();
                        int exitValue = p.exitValue();
                        boolean alertOnOutput = (0 != exitValue);
                        
                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        // read the output from the command
                        String strOutputFromCommand = "";
                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                        	strOutputFromCommand += s;
                        }
                        if (!strOutputFromCommand.equals("")) {
                        	msg = (editorErrorMessage + " (" + exitValue + "):\n " + strOutputFromCommand);
                        	System.out.println(msg);
                        	if (alertOnOutput) {
                        		makeJSAlertCall(msg);
                        	}
                        }
                        
                        // read any errors from the attempted command
                        String strErrorFromCommand = "";
                        s = null;
	                    while ((s = stdError.readLine()) != null) {
	                    	if (!blnEditorErrorEncountered) blnEditorErrorEncountered = true;
	                    	strErrorFromCommand += s;
	                    }
                        if (!strErrorFromCommand.equals("")) {
                        	msg = (editorErrorMessage + " (" + exitValue + "):\n " + strErrorFromCommand);
                        	System.out.println(msg);
                        	if (alertOnOutput) {
                        		makeJSAlertCall(msg);
                        	}
                        }
            		}
                	catch(IOException ioe) {
                		ioe.printStackTrace();
                		msg = (editorErrorMessage + ": \n " + ioe.toString());
                    	System.out.println(msg);
                   		makeJSAlertCall(msg);
                	}
                	catch(Exception e) {
                		e.printStackTrace();
                		msg = (editorErrorMessage + ": \n " + e.toString());
                    	System.out.println(msg);
                   		makeJSAlertCall(msg);
                	}
                	finally {
                		if (!blnEditorErrorEncountered)  break;
                	}
            	}
			}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		msg = (editorErrorMessage + ": \n" + e.toString());
        	System.out.println(msg);
        	makeJSAlertCall(msg);
    	}
    	finally {
    		strFileName = "";
    	}
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //  Swaps out the current editor for the vibe add-in launcher if:
    //   1) the current editor is one of the ms office editors
    //   2) we are running the "license required" edition of teaming
    //   3) the vibe add-in is installed
    //
    //  If we are launching the vibe add-in, then we also modify the URL
    //   to include the user's name so that the add-in can unambiguously
    //   match it to an account.
    //
    ////////////////////////////////////////////////////////////////////////
    protected String[] getWindowsEditorAndUrl(String strEditor, String strUrl){
    	System.out.println("Is Office Add-in allowed: " + String.valueOf(isOfficeAddInAllowed));
        if(isLicenseRequiredEdition && isOfficeAddInAllowed){
            Pattern p = Pattern.compile("((?:winword)|(?:excel)|(?:powerpnt))(?:\\.exe)?", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(strEditor);
            if(m.matches() && isAddinInstalled()){
                String app = m.group(1);
                if(app.equalsIgnoreCase("winword"))
                    app = "word";
                else if(app.equalsIgnoreCase("powerpnt"))
                    app = "powerpoint";
                strEditor = vibeAddinEditor + " /" + app;
                // Add the username to the URL if available
                if(strUserName != null && strUserName.length() > 0){
                	// Note that we preserve the original '/dave...'
                	// portion of the URL to retain any escaping
                	// it may have contained.
                	int davePos = strUrl.indexOf("/dave");
                	String davePart;
                	if (0 < davePos)
                	     davePart = strUrl.substring(davePos);
                	else davePart = null;
                    try {
                        URI uri = new URI(strUrl);
                        uri = new URI(uri.getScheme(), strUserName,
                                uri.getHost(), uri.getPort(), uri.getPath(),
                                uri.getQuery(), uri.getFragment());
                        strUrl = uri.toString();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    if (null != davePart) {
                    	davePos = strUrl.indexOf("/dave");
                    	if (0 < davePos) {
                    		strUrl = (strUrl.substring(0, davePos) + davePart);
                    	}
                    }
                }
            }
        }
        String[] editorAndUrl = new String[2];
        editorAndUrl[0] = strEditor;
        editorAndUrl[1] = strUrl;
        return editorAndUrl;
    }

    ////////////////////////////////////////////////////////////////////////
    //
    //  Determines if the vibe add-in launcher executable is installed by
    //   querying the windows registry via command line. Note that "reg query"
    //   will return a non-zero exit code if the specified registry key
    //   does not exist.
    //
    ////////////////////////////////////////////////////////////////////////
    protected boolean isAddinInstalled(){
        boolean installed = false;
        StringBuilder regQuery = new StringBuilder();
        regQuery.append("reg query \"HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\App Paths\\");
        regQuery.append(vibeAddinEditor);
        regQuery.append(".exe\" /ve");
        String[] command = new String[3];
        command[0] = "cmd";
        command[1] = "/c";
        command[2] = regQuery.toString();
        try
        {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            installed = (p.exitValue() == 0);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return installed;
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

    private void onLoad(FileOpen fileOpen) {
      try {
        String onLoadFunction = fileOpen.getParameter("onLoadFunction");
        if (onLoadFunction.equals(null) || "".equals(onLoadFunction)) return;
        JSObject win = JSObject.getWindow(fileOpen);
        String args[] = {};
        Object foo = win.call(onLoadFunction,args);
      } catch (Exception ignored) { }
    }
    
    private boolean checkEditClicked() {
        try {
          String strCheckEditClicked = fileOpen.getParameter(checkEditClicked);
          if (strCheckEditClicked.equals("")) return false;
          String jsfunc = strCheckEditClicked;
          JSObject win = JSObject.getWindow(fileOpen);
          String strEditClicked = (String)win.call(jsfunc,null);
          if (strEditClicked.equalsIgnoreCase("true")) return true;
        } catch (Exception ignored) { }
        return false;
    }    
    
    private void resetEditClicked() {
        try {
          String strResetEditClicked = fileOpen.getParameter(resetEditClicked);
          if (strResetEditClicked.equals("")) return;
          String jsfunc = strResetEditClicked;
          JSObject win = JSObject.getWindow(fileOpen);
          win.call(jsfunc,null);
        } catch (Exception ignored) { }
      }    

    public void setFileToBeOpened(String strInputURL) {
      try {
    	strFileName = strInputURL;
      } 
      catch (Exception e) { 
    	  System.out.println("setFileToBeOpened: "+e);
      }
    }
    
    private void makeJSAlertCall(String strAlertMessage) {
    	try {
    	  String onLoadFunction = "alert";
    	  JSObject win = JSObject.getWindow(fileOpen);
    	  if (strAlertMessage == null || strAlertMessage.equals("")) return;
    	  String args[] = {strAlertMessage};
    	  Object foo = win.call(onLoadFunction,args);
    	} catch (Exception ignored) { }
    }
}
