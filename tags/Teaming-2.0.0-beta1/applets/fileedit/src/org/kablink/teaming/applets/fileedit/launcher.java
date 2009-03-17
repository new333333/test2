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
package org.kablink.teaming.applets.fileedit;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import javax.swing.*;
import java.security.*;
import java.io.*;
import java.util.Properties;
import netscape.javascript.JSObject;

/**
 * Class: Launcher
 * Purpose: This Class is the starting point for the file edit applet.
 *          This signed applet allows a user to download, edit, and upload
 *          a file in one click.
 *
 *          This class in particular gets the parameters from the http server,
 *          and then looks on the users disk to see if the file already exists.
 *          If it does, then it asks the user whether or not to use that file,
 *          otherwise, it downloads the file from the http server.
 */

public class launcher extends JApplet // implements Runnable
{
  FEData data = new FEData();
  JButton downloadB;                // a button to use to test the applet
  final String FS = System.getProperty("file.separator");
  final String DOT = ".";
  static int retVal = 0;
  LaunchDoc launch;
  JRadioButton newCopyB, useCopyB;

  public void stop() {
    if ((downloadB != null) && (!downloadB.getText().equals(new String("foo"))))
    {
      super.stop();
    } else
      super.stop();

  }

  public void init()
  {
    // Get all the params

    data.setApplet(this);
    data.setOrigFileName(getParameter("fileName"));
    data.setPostUrl(getParameter("postURL"));
    data.setDocumentURL(getParameter("getURL"));
    data.setUpdateInForum(getParameter("updateFlag"));
    data.setRelativePath(getParameter("relativePath"));
    data.setForumName(getParameter("forumName"));
    data.setDocId(getParameter("docId"));
    data.setZoneName(getParameter("zoneName"));
    data.setParamText("saveText","Save changes to this file");
    data.setParamText("abandonText","Discard the edit");
    data.setParamText("unlockText","Unlock the entry");
    data.setParamText("prevText","Save previous versions");
    data.setParamText("reserveText","Reserve and edit file attachment");
    data.setParamText("savedText","A copy of this file is already saved to your computer.");
    data.setParamText("wantText","Do you want to:");
    data.setParamText("downloadText","Download a new copy of this file for editing");
    data.setParamText("useText","Use the file copy on disk");
    data.setParamText("okButtonText","OK");
    data.setParamText("cancelButtonText","Cancel");
    data.setParamText("helpButtonText","Help");
    data.setParamText("editingText","Editing ");
    data.setParamText("cautionText","Caution: If you leave this page, your edits will not be saved to the Forum");
    data.setParamText("processingText","Processing...");
    data.setParamText("noProcText","No process instantiated");
    data.setParamText("browserGoneText","The browser page changed or the browser was killed, edit's will not be saved");
    data.setParamText("badFileText","The file was not launched properly, exit value was:");
    data.setParamText("saveDiscardText","Save or discard changes and return to Forum");
    data.setParamText("saveAbandonText","Save or abandon changes?");
    data.setParamText("notificationBlockText","Do not send notifications for this action");
    data.setParamText("notificationBlock","no");
    if (data.getParamText("notificationBlock").equalsIgnoreCase("yes")){
        data.setUseNotificationBlock(true);
    }
    else {
        data.setUseNotificationBlock(false);
    }
    //this.getContentPane().add(data.statusLabel);
    repaint();
  }

  /**
   * This is an event handler for the button press. When called, download the document
   * from the host, and then launch it.
   */
  public void start()
  {
    int temp;
    int c;
    URL url;
    HttpURLConnection urlConn = null;
    DataInputStream dis = null;
    FileOutputStream fos = null;

    String javaVersion = System.getProperty("java.version");
    if (javaVersion.startsWith("1.1") || javaVersion.startsWith("1.2") || javaVersion.startsWith("1.3")) {
        displayJavaVersionError();
        this.destroy();
        return;
    }


    // Allow secure interactions.  This startup instructed by
    // file:///C|/java/jsse1.0.2/INSTALL.txt
    Security.addProvider(
      new com.sun.net.ssl.internal.ssl.Provider());
    System.setProperty("java.protocol.handler.pkgs",
                        "com.sun.net.ssl.internal.www.protocol");
    // the following should be equivalent to
    // java -djavax.net.debug="ssl,session"
    // but isn't as I notice a can't-find-class-SocketFactory error
    // with former method
    System.setProperty("javax.net.debug", "");

    try
    {

      // get the temporary filename
      String tempDir = getTempDirName();
      String tmpFileName = data.getTempFileName();
      data.setLocalFileName(new String(tempDir + FS + tmpFileName));

      // see if the temp file already exists on the disk. If it does, ask the user
      // whether to use the one that's there, or... overwrite it.
      File prevFile = new File(data.getLocalFileName());
      if (prevFile.exists())
      {
        // see if the user want us to use that one, or, download a new one.
        askWhichToUse();
      }
      else
        downloadFile(data);

    }// End try statement
    catch(Exception generic)
    {
      System.out.println(generic.toString());
      //data.getStatusLabel().setText(generic.toString());
    }
    finally
    {
      try {
        dis.close();
      } catch (Exception ex) {}

      try {
        fos.close();
      } catch (Exception exe) {}

    }
    return;
  }  //end of action

  /**
   * This gets called in order to download the file from the server.
   */
  public void downloadFile(FEData data)
  {
    int temp;
    int c;
    URL url;
    HttpURLConnection urlConn = null;
    DataInputStream dis = null;
    FileOutputStream fos = null;
    try
    {
       // open the connection to the server, and set up to download the doc
      url = new URL(data.getDocumentURL());
      urlConn = (HttpURLConnection)url.openConnection();
      urlConn.setDoInput(true);
      urlConn.setUseCaches(false);
      urlConn.setAllowUserInteraction(false);
      urlConn.setContentHandlerFactory(null);

      // the Input stream
      dis = new DataInputStream(urlConn.getInputStream());

      // the output stream to the temp file
      fos = new FileOutputStream(data.getLocalFileName(), false);

      // read it and write it.
      // NOTE: Check for out of disk space!!!
      while ((c = dis.read()) != -1)
            fos.write(c);
      fos.close();

     // try to launch the application
     LaunchDoc launch = new LaunchDoc(data);
     launch.start();

     updateScreenInfo();

    }// End try statement
    catch(MalformedURLException ex)
    {
      System.err.println(ex);
    }
    catch(java.io.IOException iox)
    {
      System.out.println(iox);
    }
    catch(Exception generic)
    {
      System.out.println(generic.toString());
      //data.getStatusLabel().setText(generic.toString());
    }
    finally
    {
      try {
        dis.close();
      } catch (Exception ex) {}

      try {
        fos.close();
      } catch (Exception exe) {}

    }
    return;
  }  //end of action

  public void askWhichToUse()
  {

  // Set the title of the document
  try {
    JSObject win = JSObject.getWindow(data.getApplet());
    String args[] = {data.getParamText("reserveText")};
    Object foo = win.call("changeTitle",args);
    } catch (Exception je) {
    }
  // Now set up the panes...
  SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      JApplet applet = data.getApplet();
      Container pane = applet.getContentPane();
      //applet.remove(data.getStatusLabel());

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      pane.setLayout(gridbag);
      pane.setBackground(java.awt.Color.white);
      pane.setForeground(java.awt.Color.black);

      JPanel reUsePane = new JPanel();
      reUsePane.setBackground(java.awt.Color.white);
      reUsePane.setForeground(java.awt.Color.black);
      reUsePane.setSize(400,200);
      Util.setFont(data, reUsePane, data.PLAIN);

      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      reUsePane.setLayout(gb);
      gbc.fill = GridBagConstraints.VERTICAL;

      JLabel alreadyLabel = new JLabel(data.getParamText("savedText"));
      // Set the grid constraints for this label
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0,0,0,0);
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.ipady = 0;
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.WEST;
      gridbag.setConstraints(alreadyLabel, c);
      Util.setFont(data, alreadyLabel, data.PLAIN);
      pane.add(alreadyLabel);

      JLabel wantLabel = new JLabel("Do you want to:");
      // Set the grid constraints for this label
      c.gridx = 0;
      c.gridy = 1;
      c.insets = new Insets(0,0,0,0);
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.ipady = 0;
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.WEST;
      gridbag.setConstraints(wantLabel, c);
      Util.setFont(data, wantLabel, data.PLAIN);
      pane.add(wantLabel);


      newCopyB = new JRadioButton(data.getParamText("downloadText"));
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridwidth = 5;
      //gbc.insets = new Insets(70,20,5,20);
      gbc.insets = new Insets(0,0,0,0);
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.ipady = 0;
      gbc.anchor = GridBagConstraints.WEST;
      gb.setConstraints(newCopyB, gbc);
      newCopyB.setBackground(java.awt.Color.white);
      newCopyB.setForeground(java.awt.Color.black);
      newCopyB.setSelected(true);
      Util.setFont(data, newCopyB, data.PLAIN);
      reUsePane.add(newCopyB);



      useCopyB = new JRadioButton(data.getParamText("useText"));
      gbc.gridx = 0;
      gbc.gridy = 1;
      //gbc.insets = new Insets(5,20,70,20);
      gbc.insets = new Insets(0,0,0,0);
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.ipady = 0;
      //gbc.gridheight = 1;
      gbc.anchor = GridBagConstraints.WEST;
      gb.setConstraints(useCopyB, gbc);
      useCopyB.setBackground(java.awt.Color.white);
      useCopyB.setForeground(java.awt.Color.black);
      Util.setFont(data, useCopyB, data.PLAIN);
      reUsePane.add(useCopyB);

      // Group the radio buttons.
      ButtonGroup group = new ButtonGroup();
      group.add(newCopyB);
      group.add(useCopyB);

      // Set the grid constraints for the reUsePane
      c.gridx = 0;
      c.gridy = 2;
      c.insets = new Insets(0,0,0,0);
      c.weightx = 0.5;
      c.weighty = 0.5;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.ipady = 0;
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.WEST;
      gridbag.setConstraints(reUsePane, c);
      pane.add(reUsePane);


      // create a pane to hold the forum buttons
      JPanel buttonPane = new JPanel();
      buttonPane.setBackground(java.awt.Color.white);
      buttonPane.setForeground(java.awt.Color.black);
      buttonPane.setSize(400,200);
      Util.setFont(data, buttonPane, data.BOLD);

      buttonPane.setLayout(gb);
      gbc.fill = GridBagConstraints.VERTICAL;

      JButton okB = new JButton(data.getParamText("okButtonText"));
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.ipady = 0;
      gbc.insets = new Insets(30,30,30,30);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(okB, gbc);
      //okB.setBackground(java.awt.Color.white);
      okB.setForeground(java.awt.Color.black);
      Util.setFont(data, okB, data.BOLD);
      buttonPane.add(okB);

      JButton cancelB = new JButton(data.getParamText("cancelButtonText"));
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.ipady = 0;
      //c.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(cancelB, gbc);
      //cancelB.setBackground(java.awt.Color.white);
      cancelB.setForeground(java.awt.Color.black);
      Util.setFont(data, cancelB, data.BOLD);
      buttonPane.add(cancelB);

      JButton helpB = new JButton(data.getParamText("helpButtonText"));
      gbc.gridx = 2;
      gbc.gridy = 0;
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.ipady = 0;
      //c.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(helpB, gbc);
      //helpB.setBackground(java.awt.Color.white);
      helpB.setForeground(java.awt.Color.black);
      Util.setFont(data, helpB, data.BOLD);
      buttonPane.add(helpB);


      // Set the grid constraints for the button pane
      c.gridx = 0;
      c.gridy = 3;
      c.insets = new Insets(30,30,30,30);
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.gridheight = 1;
      c.gridwidth = 3;
      c.ipady = 0;
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.WEST;
      gridbag.setConstraints(buttonPane, c);
      pane.add(buttonPane);
      applet.validate();
      applet.repaint();

      // create the action listener, so when the user hits the button, it downloads and launches
      //okB.addActionListener(okPressed());

      okB.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (useCopyB.isSelected())
          {
            LaunchDoc launch = new LaunchDoc(data);
            launch.start();
            updateScreenInfo();
          }
          else
            downloadFile(data);
        }
      });

      cancelB.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          data.setUploadFlag(false);
          data.setUnlockFlag(true);
          data.setSavePrevFlag(false);
          final_actionPerformed(e,data);
        }
      });

      helpB.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            JSObject win = JSObject.getWindow(data.getApplet());
            String args[] = {"Help is here"};
            Object foo = win.call("showHelp",args);
          } catch (Exception je) {
        }}
      });

    }
  });

  }

  public void displayJavaVersionError()
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JApplet applet = data.getApplet();
        Container pane = applet.getContentPane();
        //default title and icon
        String javaVersion = System.getProperty("java.version");
        //JOptionPane.showMessageDialog(pane,"This applet requires Java 1.4. Your browser is using version:  " + javaVersion,"Incorrect Java version",JOptionPane.ERROR_MESSAGE);
        //appletContext.showDocument("http://java.sun.com/getjava/download.html","_self");

        Object[] options = {"Download the 1.4 Plugin",
                            "Don't use this applet"};
        int n = JOptionPane.showOptionDialog(pane,
                                           "This applet requires Java 1.4. Your browser is using version:  " +
                                           javaVersion +
                                           "Would you like to download the 1.4 Plugin?",
                                           "Download the Plugin?",
                                           JOptionPane.YES_NO_OPTION,
                                           JOptionPane.QUESTION_MESSAGE,
                                           null,
                                           options,
                                           options[0]);
        try {
          if (n == JOptionPane.YES_OPTION)
            data.getApplet().getAppletContext().showDocument(new URL("http://java.sun.com/getjava/download.html"),"_self");
          else
            data.getApplet().getAppletContext().showDocument(new URL(data.getAbandonURL()),"_self");
        } catch (Exception u) {}

      }
    });
  }


  public String getTempDirName()
  {
    String tmpdir = System.getProperty("java.io.tmpdir");
    if (tmpdir != null) return tmpdir;
    return ("./");
  }

  public String ok()
  {
    return "OK";
  }

  public void updateScreenInfo()
  {

  // Set the title of the document
  try {
    JSObject win = JSObject.getWindow(data.getApplet());
    String args[] = {data.getParamText("editingText") + " " + data.getOrigFileName()};
    Object foo = win.call("changeTitle",args);
    } catch (Exception je) {
    }
  // Now set up the panes...
  SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      JApplet applet = data.getApplet();
      Container pane = applet.getContentPane();
      pane.removeAll();

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      pane.setLayout(gridbag);
      pane.setBackground(java.awt.Color.white);
      pane.setForeground(java.awt.Color.black);
      pane.setSize(400,200);

      JPanel updateInfoPane = new JPanel();
      updateInfoPane.setBackground(java.awt.Color.white);
      updateInfoPane.setForeground(java.awt.Color.red);
      updateInfoPane.setSize(400,200);
      Util.setFont(data, updateInfoPane, data.PLAIN);

      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      updateInfoPane.setLayout(gb);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      String newInfo = new String(data.getParamText("cautionText"));
      JLabel infoLabel = new JLabel(newInfo);
      infoLabel.setSize(400,200);
      // Set the grid constraints for this label
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0,0,0,0);
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.ipady = 0;
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.WEST;
      gridbag.setConstraints(infoLabel, c);
      Util.setFont(data, infoLabel, data.PLAIN);
      pane.add(infoLabel);
      applet.validate();
      applet.repaint();
    }
    });
  }
  public boolean final_actionPerformed(ActionEvent e,FEData data)
  {
    Util.progressBar(data,data.getParamText("processingText"));
    PostFileConnection poster = new PostFileConnection(data);
    return true;
  }
}// End of launcher

