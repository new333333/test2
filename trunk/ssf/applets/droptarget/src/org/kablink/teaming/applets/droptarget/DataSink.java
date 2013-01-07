/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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


import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.border.*;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import java.io.*;
import java.security.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.applet.AppletContext;
import java.net.URL;
import java.net.URLEncoder;

import netscape.javascript.JSObject;


/**
 * This allows File objects to be pasted or dropped onto it.
 */
public class DataSink extends JLabel implements DropTargetListener, ActionListener {
public static DataSink sink; 
public TopFrame topframe;
private JPopupMenu fPasteMenu;
private MouseAdapter fAdapter;
private Component fSelectedComponent;
final String StaticGif = "sym_s_closedfolder.gif";
final String OpenGif = "sym_s_openfolder.gif";
final String AnimGif = "sym_s_animfolder.gif";
static boolean GotColor = false;
static Color BGColor = null;
final String BGColorString = "#ffffff";
boolean fileUploadInProgress = false;

private static ArrayList xferFileList;
private static ArrayList xferFileListNames;

private static DataFlavor uriListFlavor;
private static DataFlavor gnomeIODataFlavor;
private static DataFlavor javaStringDataFlavor;

  /** Create a new DataSink object */
  public DataSink(TopFrame topFrame) {
    this.topframe = topFrame;
    makePopup();
    sink = this;
    // Listen for button3.  Use that to trigger a paste action.
    sink.addMouseListener(fAdapter);

    // Create a DropTarget object to support drag-and-drop.
    // It will listen for drops on top of us and notify our DropTargetListener
    // methods when drag-and-drop-related events occur.
    setDropTarget(new DropTarget(this, this));

    KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK);
    this.getInputMap().put(keyStroke, "PASTE");
    this.getActionMap().put("PASTE", paste);
    changeIcon(StaticGif);
    
    initDataFlavors();
  }

  private void initDataFlavors() {
	  if (null == uriListFlavor) {
		  try                  {uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");}
		  catch (Exception ex) {uriListFlavor = null;}
	  }
	  
	  if (null == gnomeIODataFlavor) {
		  try                  {gnomeIODataFlavor = new DataFlavor("x-special/gnome-copied-files;class=java.io.InputStream");}
		  catch (Exception ex) {gnomeIODataFlavor = null;}
	  }
	  
	  if (null == javaStringDataFlavor) {
		  try                  {javaStringDataFlavor = new DataFlavor("application/x-java-serialized-object;class=java.lang.String");}
		  catch (Exception ex) {javaStringDataFlavor = null;}
	  }
  }
  
  private static Action paste = new AbstractAction()
  {
    public void actionPerformed(ActionEvent e)
    {
        sink.addFilesToList();
    }
  };

  // The methods below are the methods of DropTargetListener.
  // They are invoked at various times when something is being
  // dragged over us, and allow us an opportunity to respond to the drag.

  // This is the border we display when the user is dragging over us.
  protected static Border dropBorder = new BevelBorder(BevelBorder.LOWERED);

  /**
   * Something is being dragged over us.  If we can support this data type.
   * tell the drag-and-drop system that we are interested, and display
   * a special border to tell the user that we're interested.
   */
  public void dragEnter(DropTargetDragEvent e) {
	  Debug.debugLog("DataSink.dragEnter()..........");
    if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || ((null != uriListFlavor) && e.isDataFlavorSupported(uriListFlavor))) {
      e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
      changeIcon(OpenGif);
      this.setBorder(dropBorder);
    }
  }

  /**
   * The user is no longer dragging over us, so restore the default border
   */
  public void dragExit(DropTargetEvent e) {
    Debug.debugLog("DataSink.dragExit()..........");
    changeIcon(StaticGif);
    this.setBorder(null);
  }

  /**
   * This method is invoked when something is pasted onto our icon.
   */
  public void addFilesToList() {
	// If we're already uploading files...
    if (fileUploadInProgress) {
      // ...we won't try anymore until they're done.
      informUploadInProgress();
      return;
    }
	  
      // Get the clipboard, and read its contents
    Clipboard c = this.getToolkit().getSystemClipboard();
    Transferable t = c.getContents(this);
    xferFileList = new ArrayList();
    xferFileListNames = new ArrayList();
    String topDir = "";
    changeIcon(AnimGif);
    
    //Code to not allow directory copy, when specified in the param
    boolean loadDirectory = true;
    String strLoadDirectory = topframe.getParameter("loadDirectory");
    if ("no".equalsIgnoreCase(strLoadDirectory)) loadDirectory = false;

    if (t == null) {             // If nothing to paste
      this.getToolkit().beep();  // then beep and do nothing
      return;
    }
    
/*
 * Code to check the data flavors supported    
    DataFlavor [] dataFlavor = t.getTransferDataFlavors();
    for (int i = 0; i < dataFlavor.length; i++) {
    	DataFlavor dataFlavor1 = dataFlavor[i];
    	Debug.writeLog("\n dataFlavor ["+ i +"]"+ dataFlavor1);
    	
    	if (t.isDataFlavorSupported(dataFlavor1)) {
    		Debug.writeLog("Supported :) !!!!!: "+ dataFlavor1 + "\n\n");
    		
    		try {
    			Object objData = t.getTransferData(dataFlavor1);
    			Debug.writeLog("objData: "+objData + "\n\n");
    		} catch(Exception oe) {
    			Debug.writeLog("Error: "+ oe + "\n\n");
    		}
    		
    	} else {
    		Debug.writeLog("Not Supported :( !!!!!: "+ dataFlavor1 + "\n\n");
    	}
    }
*/    
    try {
	    List files = new ArrayList();
	    // Code Got from Java Bug Database Bug Id: 4899516
	    // Check for types of data that we support
	    
	    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	    	files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
	    } else if (t.isDataFlavorSupported(gnomeIODataFlavor)) {
	    	InputStream ips = (InputStream)t.getTransferData(gnomeIODataFlavor);
	    	ByteArrayOutputStream ops = new ByteArrayOutputStream();

	    	int intReadValue;
	    	while ((intReadValue = ips.read()) != -1) {
            	ops.write(intReadValue);
            }
	    	
            String strFilesCopiedInformation = ops.toString();
            files = xSpecialListToFileList(strFilesCopiedInformation);
        } else if (t.isDataFlavorSupported(javaStringDataFlavor)) {
        	String strFiles = (String) t.getTransferData(javaStringDataFlavor);
        	files = stringListToFileList(strFiles);
        } else {
        	Debug.writeLog("File Upload Not supported...........");
        	uploadNotSupported();
        	changeIcon(StaticGif);
        }
	    
        for (int i = 0; i < files.size(); i++) {
            File f = (File) files.get(i);
            String strFileName = f.getName();
            topDir = f.getParent();
            
            if (topDir == null) topDir = "/";
            if ( f.isDirectory() ) {
          	  if (!loadDirectory) {
          		  errorMsgOnDirectoryLoad();
              	  changeIcon(StaticGif);
              	  return;
          	  }
              traverseDir(f);
            } else {
              if (strFileName.endsWith("~")) continue;
              xferFileList.add(f);
              xferFileListNames.add(strFileName);
            }
        }
          //e.dropComplete(true);
          Iterator fileIter = xferFileList.iterator();
          if (xferFileList.size() > 0) {
            fileLoadingInProgress();
            PostFiles poster = new PostFiles(topframe,
            		topframe.getParameter("fileReceiverUrl"),
            		topframe.getParameter("fileCheckExistsUrl"),
            		xferFileList, 
            		topDir);
            poster.start();
          }
          else {
        	  informNoFilesCopied();
        	  changeIcon(StaticGif);
          }
    }
    catch(Exception ex) {
    	ex.printStackTrace();
    }
  }

  private static java.util.List xSpecialListToFileList(String data) {
	  int intTokenCount = 0;
      java.util.List list = new java.util.ArrayList(1);
      for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
    	  String s = st.nextToken();
    	  
    	  if (intTokenCount == 0) {
    		  intTokenCount++;
    		  continue;
    	  }
          if (s.startsWith("#")) {
              // the line is a comment (as per the RFC 2483)
              continue;
          }
          try {
              java.net.URI uri = new java.net.URI(s);
              java.io.File file = new java.io.File(uri);
              list.add(file);
          } catch (java.net.URISyntaxException e) {
        	  Debug.writeLog("xSpecialListToFileList: "+e);
              // malformed URI
          } catch (IllegalArgumentException e) {
        	  Debug.writeLog("xSpecialListToFileList: "+e);
              // the URI is not a valid 'file:' URI
          }
          catch (Exception e) {
        	  Debug.writeLog("xSpecialListToFileList: "+e);
              // the URI is not a valid 'file:' URI
          }
      }
      return list;
  }  

  private static java.util.List stringListToFileList(String data) {
      java.util.List list = new java.util.ArrayList(1);
      for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
    	  String s = st.nextToken();
          try {
              java.io.File file = new java.io.File(s);
              boolean blnDoesFileExist = file.exists();
              if (blnDoesFileExist) list.add(file);
          } catch (IllegalArgumentException e) {
        	  Debug.writeLog("stringListToFileList: "+e);
              // the URI is not a valid 'file:' URI
          } catch (Exception e) {
        	  Debug.writeLog("stringListToFileList: "+e);
              // the URI is not a valid 'file:' URI
          }
      }
      return list;
  }    
  
  private void informNoFilesCopied() {
	makeJSCallWithAppletParam("noFileAlertMessage");
  }
  
  private void informUploadInProgress() {
    // 20100830 (DRF):  I left the following commented out for the
    // following reasons:
    // 1. I'm not sure we really want to jump in the user's face if
    //    they paste or drag & drop more files while other files are
    //    still being processed; and
    // 2. At the date I put this in, we were past the Durango string
    //    changes cutoff.
    // makeJSCallWithAppletParam("uploadInProgress");
  }
	  
  private void errorMsgOnDirectoryLoad() {
	makeJSCallWithAppletParam("directoryLoadErrorMessage");
  }
  
  private void uploadNotSupported() {
	makeJSCallWithAppletParam("fileUploadNotSupported");
  }
  
  private void makeJSCallWithAppletParam(String strAppletParam) {
	try {
	  String onLoadFunction = "alert";
	  JSObject win = JSObject.getWindow(topframe);
	  String args[] = {topframe.getParameter(strAppletParam)};
	  Object foo = win.call(onLoadFunction,args);
	} catch (Exception ignored) { }
  }

  /**
   * This method is invoked when the user drops something on us
   */
  public void drop(DropTargetDropEvent e){
    // If we're already uploading files...
    if (fileUploadInProgress) {
      // ...we won't try anymore until they're done.
      informUploadInProgress();
	  return;
    }  	  
	  
    this.setBorder(null);                  // Restore the default border
    Transferable t = e.getTransferable();  // Get the data that was dropped
    xferFileList = new ArrayList();
    xferFileListNames = new ArrayList();
    
    String topDir = "";
    changeIcon(AnimGif);

    //Code to not allow directory drag and drop, when specified in the param 
    boolean loadDirectory = true;
    String strLoadDirectory = topframe.getParameter("loadDirectory");
    if ("no".equalsIgnoreCase(strLoadDirectory)) loadDirectory = false;
    
    try {
	    List files = new ArrayList();
	    // Code Got from Java Bug Database Bug Id: 4899516
	    // Check for types of data that we support
	    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	      // If it was a file list, accept it, read the first file in the list
	      // and display the file contents
	      e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	      files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
	    } else if ((null != uriListFlavor) && t.isDataFlavorSupported(uriListFlavor)) {
	    	e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            String data = (String)t.getTransferData(uriListFlavor);
            files = textURIListToFileList(data);
        } else {  // If it wasn't a file list, reject it
            e.rejectDrop();
            return;
        }
	    
        for (int i = 0; i < files.size(); i++) {
            File f = (File) files.get(i);
            String strFileName = f.getName();
            topDir = f.getParent();
            
            if (topDir == null) topDir = "/";
            if ( f.isDirectory() ) {
          	  if (!loadDirectory) {
          		  errorMsgOnDirectoryLoad();
              	  changeIcon(StaticGif);
              	  return;
          	  }
              traverseDir(f);
            } else {
              if (strFileName.endsWith("~")) continue;
              xferFileList.add(f);
              xferFileListNames.add(strFileName);
            }
          }
          e.dropComplete(true);
          Iterator fileIter = xferFileList.iterator();
          if (xferFileList.size() > 0) {
            fileLoadingInProgress();
            PostFiles poster = new PostFiles(topframe,
            		topframe.getParameter("fileReceiverUrl"),
            		topframe.getParameter("fileCheckExistsUrl"),
            		xferFileList, 
            		topDir);
            poster.start();
          }
          else {
        	  informNoFilesCopied();
        	  changeIcon(StaticGif);
          }
    }
    catch(Exception ex) {
    	ex.printStackTrace();
    	e.dropComplete(false);
    }
  }
  
  /*
   * Code Got from Java Bug Database Bug Id: 4899516
   */
  private static java.util.List textURIListToFileList(String data) {
      java.util.List list = new java.util.ArrayList(1);
      for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n");
              st.hasMoreTokens();) {
          String s = st.nextToken();
          
          if (s.startsWith("#")) {
              // the line is a comment (as per the RFC 2483)
              continue;
          }
          try {
              java.net.URI uri = new java.net.URI(s);
              java.io.File file = new java.io.File(uri);
              list.add(file);
          } catch (java.net.URISyntaxException e) {
              // malformed URI
          } catch (IllegalArgumentException e) {
              // the URI is not a valid 'file:' URI
          }
      }
      return list;
  }
  
  /**
   * Recursive routine to find all the files under a dir
   */
  public static void traverseDir(File file) {
      if (file.isDirectory()) {
          String[] children = file.list();
          for (int i=0; i<children.length; i++) {
              traverseDir(new File(file, children[i]));
          }
      } else {
    	  String strFileName = file.getName();
    	  if (strFileName.endsWith("~")) return;
          xferFileList.add(file);
          xferFileListNames.add(strFileName);
      }
  }
  // These are unused DropTargetListener methods
  public void dragOver(DropTargetDragEvent e) {
	  Debug.debugLog("DataSink.dragOver()..........");
  }
  public void dropActionChanged(DropTargetDragEvent e) {
	  Debug.debugLog("DataSink.dropActionChanged()..........");	  
  }

  /**
   * This is a simple test program for DataSink
   * Note: This doesn't work anymore. (now needs to be tested in a browser
   * because it calls javascript functions on an html page.
   */
  public static void main(String[] args) {
    // Create a window
    JFrame f = new JFrame("DataSinkTest");
    f.getContentPane().setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    f.getContentPane().add(panel, BorderLayout.NORTH);
    // Add a DataSink
    sink = new DataSink(new TopFrame());

    f.getContentPane().add(sink, BorderLayout.CENTER);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Security.addProvider(
        new com.sun.net.ssl.internal.ssl.Provider());
    System.setProperty("java.protocol.handler.pkgs",
                       "com.sun.net.ssl.internal.www.protocol");

    // Pop it all up
    f.setSize(80, 100);
    f.show();
  }

  /**
   * Convenience routine to change the Icon for this applet
   */
  public void changeIcon(final String imageName)
  {
      String color = getBackgroundColor();
      
      if (color.equals("")) {
        BGColor = null;
      } else {
        BGColor = hexToColor(color);
      }
      
      sink.setIcon(new ImageIcon(TopFrame.class.getResource(imageName)));
      sink.setOpaque(true);
      if (BGColor != null)
        //sink.setBackground(Color.getColor(BGColor.substring(1)));
        sink.setBackground(BGColor);
      sink.repaint();
  }

  /**
   * Create the right click popup menu.
   * note: only show the "Deactivate" option if a deactivation
   * URL was passed in.
   */
  void makePopup () {
    String deactivate = topframe.getParameter("deactivationUrl");
    String menuPaste = topframe.getParameter("menuLabelPaste");
    String menuCancel = topframe.getParameter("menuLabelCancel");
    String menuDeactivate = topframe.getParameter("menuLabelDeactivate");

    fPasteMenu = new JPopupMenu ("Files");
    fPasteMenu.add (makeMenuItem (menuPaste) );
    fPasteMenu.add (makeMenuItem (menuCancel) );
    if (!deactivate.equals(""))
      fPasteMenu.add (makeMenuItem (menuDeactivate) );

    // Create a MouseAdapter that creates a Popup menu
    // when the right mouse or equivalent button clicked.
    fAdapter = new MouseAdapter () {

      // On some platforms, mouseReleased sets PopupTrigger.
      public void mouseReleased (MouseEvent e) {
        if (e.isPopupTrigger ()) {
            showPopupMenu (e);
        }
      }

      // And on other platforms, mousePressed sets PopupTrigger.
      public void mousePressed (MouseEvent e) {
        if (e.isPopupTrigger ()) {
           showPopupMenu (e);
        }
      }

      // Get the component over which the right button click
      // occurred and show the menu there.
      public void showPopupMenu (MouseEvent e) {
        fSelectedComponent = e.getComponent ();
        fPasteMenu.show (fSelectedComponent, e.getX (), e.getY ());
      }

    }; // anonymous MouseAdapter subclass

  } // makePopup

  /**
   * If the user right-clicks on the applet, they have the choice of
   * turning off showing the applet.  We'll look to see if the proper
   * parameters were passed in, and then call a javascript function to
   * to the deactivation.
   */
  private void deactivateApplet() {
    try {
      String reloadFunction = topframe.getParameter("reloadFunctionName");
      if (reloadFunction.equals(null)) return;
      String jsfunc = reloadFunction;
      String Url = topframe.getParameter("deactivationUrl");
      JSObject win = JSObject.getWindow(topframe);
      String args[] = {Url};
      Object foo = win.call(reloadFunction,args);
    } catch (Exception ignored) { }
  }

  /**
   * get the proper background color for this applet from the html page
   */
  private String getBackgroundColor() {
    String color = BGColorString;
    try {
      String gbcolorfunc = topframe.getParameter("bgColorFunctionName");
      if (gbcolorfunc.equals("")) {
        return color;
      }
      String jsfunc = gbcolorfunc;
      JSObject win = JSObject.getWindow(topframe);
      color = (String)win.call(jsfunc,null);
    } catch (Exception ignored) {color = BGColorString;}
    return color;
  }

  private void onCancelFunction()
  {
    try {
      String onCancelFunction = topframe.getParameter("onCancelFunction");
      if (onCancelFunction.equals(null) || "".equals(onCancelFunction)) return;
      JSObject win = JSObject.getWindow(topframe);
      String args[] = {};
      Object foo = win.call(onCancelFunction,args);
    } catch (Exception ignored) { }
  }
  
  public void fileLoadingInProgress()
  {
    try {
      String strFileLoadingInProgress = topframe.getParameter("fileLoadingInProgress");
      if (strFileLoadingInProgress.equals(null) || "".equals(strFileLoadingInProgress)) return;

      String strFileNames = "";
      
      if (xferFileListNames != null) {
          for (int i = 0; i < xferFileListNames.size(); i++) {
        	  if (!strFileNames.equals("")) {
        		  strFileNames += ", " + (String) xferFileListNames.get(i);
        	  } else {
        		  strFileNames += (String) xferFileListNames.get(i);
        	  }
          }
      }
      
      JSObject win = JSObject.getWindow(topframe);
      String args[] = {strFileNames};
      Object foo = win.call(strFileLoadingInProgress,args);
      fileUploadInProgress = true;
    } catch (Exception ignored) { }
  }

  public void fileLoadingEnded()
  {
    try {
      String strFileLoadingEnded = topframe.getParameter("fileLoadingEnded");
      if (strFileLoadingEnded.equals(null) || "".equals(strFileLoadingEnded)) return;
      JSObject win = JSObject.getWindow(topframe);
      String args[] = {};
      Object foo = win.call(strFileLoadingEnded,args);
      fileUploadInProgress = false;
    } catch (Exception ignored) { }
  }  
  
  /**
   * Convert a "#FFFFFF" hex string to a Color.
   * If the color specification is bad, an attempt
   * will be made to fix it up.
   */
  static final Color hexToColor(String value) {
    if (value.startsWith("#")) {
      String digits = value.substring(1, Math.min(value.length(), 7));
      String hstr = "0x" + digits;
      Color c = Color.decode(hstr);
      return c;
    }
    return null;
 }

  /**
   * do the appropriate action when the user makes
   * a choice in the right click menu on the applet.
   */
  public void actionPerformed (ActionEvent e) {
    String action = e.getActionCommand ();
    this.repaint();

    String menuPaste = topframe.getParameter("menuLabelPaste");
    String menuCancel = topframe.getParameter("menuLabelCancel");
    String menuDeactivate = topframe.getParameter("menuLabelDeactivate");
    
    if (action.equals (menuPaste) ){
      changeIcon(AnimGif);
      addFilesToList();
    } else if(action.equals (menuCancel) ) {
      changeIcon(StaticGif);
      onCancelFunction();
      return;
    } else if (action.equals(menuDeactivate) ) {
      deactivateApplet();
      return;
    }
  } // actionPerformed

  /**
   * A utility method for making menu items.
   */
  private JMenuItem makeMenuItem (String label) {
    JMenuItem item = new JMenuItem (label);
    item.addActionListener ( this );
    return item;
  } // makeMenuItem
}
