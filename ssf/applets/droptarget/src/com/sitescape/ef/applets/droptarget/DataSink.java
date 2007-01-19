package com.sitescape.ef.applets.droptarget;


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

private static ArrayList xferFileList;
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

    KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                                                     InputEvent.CTRL_MASK);
    this.getInputMap().put(keyStroke, "PASTE");
    this.getActionMap().put("PASTE", paste);
    changeIcon(StaticGif);
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
    if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
      changeIcon(OpenGif);
      this.setBorder(dropBorder);
    }
  }

  /**
   * The user is no longer dragging over us, so restore the default border
   */
  public void dragExit(DropTargetEvent e) {
    changeIcon(StaticGif);
    this.setBorder(null);
  }

  /**
   * This method is invoked when something is pasted onto our icon.
   */
  public void addFilesToList() {
      // Get the clipboard, and read its contents
    Clipboard c = this.getToolkit().getSystemClipboard();
    Transferable t = c.getContents(this);
    xferFileList = new ArrayList();
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
    try {
      // If the clipboard contained a color, use it as the background color
      if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        List files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
        for (int i = 0; i < files.size(); i++) {
          File f = (File) files.get(i);
          topDir = f.getParent();
          if (topDir == null) topDir = "/";
          if ( f.isDirectory() ) {
        	  if (!loadDirectory) {
        		  errorMsgOnDirectoryLoad(topframe);
            	  changeIcon(StaticGif);
            	  return;
        	  }
            traverseDir(f);
          } else {
            xferFileList.add(f);
          }
        }
      // Otherwise, we don't know how to paste the data, so just beep
      } else this.getToolkit().beep();
      Iterator fileIter = xferFileList.iterator();
      if (xferFileList.size() > 0) {
        PostFiles poster = new PostFiles(topframe,topframe.getParameter("fileReceiverUrl"),xferFileList, topDir);
        try {
          //Thread.sleep(3000);
        } catch (Exception e) {}
      }
    }
    catch (UnsupportedFlavorException ex) { this.getToolkit().beep(); }
    catch (IOException ex) { this.getToolkit().beep(); }
  }

  private void errorMsgOnDirectoryLoad(TopFrame topFrame)
  {
    try {
      String onLoadFunction = "alert";
      JSObject win = JSObject.getWindow(topFrame);
      String args[] = {topframe.getParameter("directoryLoadErrorMessage")};
      Object foo = win.call(onLoadFunction,args);
    } catch (Exception ignored) { }
  }
  
  /**
   * This method is invoked when the user drops something on us
   */
  public void drop(DropTargetDropEvent e){
    this.setBorder(null);                  // Restore the default border
    Transferable t = e.getTransferable();  // Get the data that was dropped
    xferFileList = new ArrayList();
    String topDir = "";
    changeIcon(AnimGif);

    //Code to not allow directory drag and drop, when specified in the param 
    boolean loadDirectory = true;
    String strLoadDirectory = topframe.getParameter("loadDirectory");
    if ("no".equalsIgnoreCase(strLoadDirectory)) loadDirectory = false;

    // Check for types of data that we support
    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      // If it was a file list, accept it, read the first file in the list
      // and display the file contents
      e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      try {
        List files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
        for (int i = 0; i < files.size(); i++) {
          File f = (File) files.get(i);
          topDir = f.getParent();
          if (topDir == null) topDir = "/";
          if ( f.isDirectory() ) {
        	  if (!loadDirectory) {
        		  errorMsgOnDirectoryLoad(topframe);
            	  changeIcon(StaticGif);
            	  return;
        	  }
            traverseDir(f);
          } else {
            xferFileList.add(f);
          }
        }
        e.dropComplete(true);
        Iterator fileIter = xferFileList.iterator();
        if (xferFileList.size() > 0) {
          PostFiles poster = new PostFiles(topframe,topframe.getParameter("fileReceiverUrl"),xferFileList, topDir);
        }
      }
      catch (Exception ex) { e.dropComplete(false); }
    } else {  // If it wasn't a file list, reject it
      e.rejectDrop();
      return;
    }

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
          xferFileList.add(file);
      }
  }
  // These are unused DropTargetListener methods
  public void dragOver(DropTargetDragEvent e) {}
  public void dropActionChanged(DropTargetDragEvent e) {}

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
    String color = "";
    try {
      String gbcolorfunc = topframe.getParameter("bgColorFunctionName");
      if (gbcolorfunc.equals("")) {
        return "";
      }
      String jsfunc = gbcolorfunc;
      JSObject win = JSObject.getWindow(topframe);
      color = (String)win.call(jsfunc,null);
    } catch (Exception ignored) { }
    return color;
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
