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

import java.io.*;
import java.security.*;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JApplet;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.Font;
import javax.swing.*;
import java.applet.AppletContext;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import netscape.javascript.JSObject;

/**
 * Class: LaunchDoc
 * Purpose: This class is responsible for launching the document. It uses DOS's
 *          mechanism for launching files based on their extension. If the extension
 *          isn't mapped to an application type, it'll ask the user for an application
 *          type.
 */


public class LaunchDoc extends Thread {
  FileOutputStream fos = null;
  FileWriter fw = null;
  String localFileName = null;
  JButton downloadB;
  //JRadioButton lockButton, unlockButton;
  JCheckBox unlockButton, prevButton, notificationButton;
  JRadioButton uploadB, abandonB;
  JApplet applet;
  long origLastModified;
  long newLastModified;
  FEData data;


  public LaunchDoc(FEData newData)
  {
     data = newData;
     applet = data.getApplet();
     downloadB = data.getDownloadB();
     localFileName = data.getLocalFileName();
  }

  /**
   * Launch the document using the standard launching mechanism.
   * (Let's windows deal with figuring out the correct mime-type)
   */
  public void run()
  {
    // get the last modified timestamp of the file before launching an application to
    // modify it, so we can see if it's been edited
    applet.repaint();
    origLastModified = data.getLastModified();
    Process proc = null;
    String launchString = new String("cmd.exe /c " + "\"" + data.getLaunchName(localFileName) + "\"");

    Runtime rt = Runtime.getRuntime();
    // launch the application (let windows figure out what app applies to this file
    try {
      proc = rt.exec(launchString);
    } catch (IOException ioe) {}
    if (proc == null) {
      downloadB.setText(data.getParamText("noProcText") + " [" + launchString + "] " + rt.toString());
      //Debug.debug("No process instantiated [" + launchString + "] " + rt.toString() + "\n");
      // TBD: POST a message saying that no app was launched
      return;
    }
    // wait for the application to exit
    try {
      proc.waitFor();
    } catch (InterruptedException iee) {
      // if you get here, it means the browser page was changed,
      // Nothing you can do since the applet is dead.
      try {
      int exitValue = proc.exitValue();
      if (exitValue != 0)
      {
        downloadB.setText(data.getParamText("badFileText") + " " + exitValue);
      }
      try {wait(10);} catch (Exception e){}
    } catch (java.lang.IllegalThreadStateException tse) {
      // getting here means the browser left the page, and the app was still running
      // the applet is dead at this point, no use to do anything
    }
    try {
        proc.waitFor();
      } catch (Exception ee)
      {
        //Debug.debug(ee.toString());
      }
    }

  // see if the mod time has changed
  newLastModified = data.getLastModified();
  try {
    JSObject win = JSObject.getWindow(data.getApplet());
    String args[] = {data.getParamText("saveDiscardText")};
    Object foo = win.call("changeTitle",args);
    } catch (Exception je) {
    }
  SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      JApplet applet = data.getApplet();
      Container pane = applet.getContentPane();
      pane.removeAll();

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      pane.setLayout(gridbag);
      pane.setBackground(java.awt.Color.white);
      pane.setForeground(java.awt.Color.black);

      JTextField fileNameText = new JTextField(data.getOrigFileName());
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0,7,10,0);
      c.ipadx = 0;
      c.ipady = 0;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.anchor = GridBagConstraints.NORTH;
      gridbag.setConstraints(fileNameText, c);
      fileNameText.setBackground(java.awt.Color.white);
      fileNameText.setForeground(java.awt.Color.black);
      Util.setFont(data, fileNameText, data.BOLD);
      pane.add(fileNameText);
      fileNameText.setBorder(null);

      JPanel saveDiscPane = new JPanel();
      saveDiscPane.setBackground(java.awt.Color.white);
      saveDiscPane.setForeground(java.awt.Color.black);
      saveDiscPane.setSize(400,200);
      Util.setFont(data, saveDiscPane, data.PLAIN);

      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      saveDiscPane.setLayout(gb);
      gbc.fill = GridBagConstraints.VERTICAL;


      uploadB = new JRadioButton(data.getParamText("saveText"));
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridwidth = 5;
      gbc.insets = new Insets(0,0,0,0);
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.ipady = 0;
      gbc.anchor = GridBagConstraints.WEST;
      gb.setConstraints(uploadB, gbc);
      uploadB.setBackground(java.awt.Color.white);
      uploadB.setForeground(java.awt.Color.black);
      uploadB.setSelected(true);
      Util.setFont(data, uploadB, data.PLAIN);
      saveDiscPane.add(uploadB);



      abandonB = new JRadioButton(data.getParamText("abandonText"));
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.insets = new Insets(0,0,0,0);
      gbc.weightx = 0.1;
      gbc.weighty = 0.1;
      gbc.ipady = 0;
      gbc.anchor = GridBagConstraints.WEST;
      gb.setConstraints(abandonB, gbc);
      abandonB.setBackground(java.awt.Color.white);
      abandonB.setForeground(java.awt.Color.black);
      Util.setFont(data, abandonB, data.PLAIN);

      //pane.add(abandonB);
      saveDiscPane.add(abandonB);

      // Group the radio buttons.
      ButtonGroup group = new ButtonGroup();
      group.add(uploadB);
      group.add(abandonB);

      TitledBorder title;
      title = BorderFactory.createTitledBorder(data.getParamText("saveAbandonText"));
      saveDiscPane.setBorder(title);


      // Set the grid constraints for the save discard pane
      c.gridx = 0;
      c.gridy = 1;
      c.insets = new Insets(0,0,0,0);
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.ipady = 0;
      c.anchor = GridBagConstraints.NORTH;
      gridbag.setConstraints(saveDiscPane, c);
      pane.add(saveDiscPane);


      //add a checkbox for saving the previous version
      prevButton = new JCheckBox(data.getParamText("prevText"));
      prevButton.setMnemonic(KeyEvent.VK_D);
      prevButton.setSelected(true);
      c.gridx = 0;
      c.gridy = 2;
      c.insets = new Insets(0,0,0,0);
      c.ipadx = 0;
      c.ipady = 0;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.anchor = GridBagConstraints.SOUTH;
      gridbag.setConstraints(prevButton, c);
      prevButton.setBackground(java.awt.Color.white);
      prevButton.setForeground(java.awt.Color.black);
      Util.setFont(data, prevButton, data.PLAIN);
      pane.add(prevButton);

      //add a checkbox for saving the previous version
      unlockButton = new JCheckBox(data.getParamText("unlockText"));
      unlockButton.setMnemonic(KeyEvent.VK_B);
      unlockButton.setSelected(true);
      c.gridx = 0;
      c.gridy = 3;
      c.insets = new Insets(0,0,0,0);
      c.ipadx = 0;
      c.ipady = 0;
      c.gridheight = 1;
      c.gridwidth = 5;
      c.weightx = 0.5;
      c.weighty = 0.2;
      c.anchor = GridBagConstraints.CENTER;
      gridbag.setConstraints(unlockButton, c);
      unlockButton.setBackground(java.awt.Color.white);
      unlockButton.setForeground(java.awt.Color.black);
      Util.setFont(data, unlockButton, data.PLAIN);
      pane.add(unlockButton);

      //add a checkbox for blocking notification if necessary
      if (data.getUseNotificationBlock()) {
          notificationButton = new JCheckBox(data.getParamText("notificationBlockText"));
          notificationButton.setMnemonic(KeyEvent.VK_N);
          notificationButton.setSelected(true);
          c.gridx = 0;
          c.gridy = 4;
          c.insets = new Insets(0,0,0,0);
          c.ipadx = 0;
          c.ipady = 0;
          c.gridheight = 1;
          c.gridwidth = 5;
          c.weightx = 0.5;
          c.weighty = 0.2;
          c.anchor = GridBagConstraints.NORTH;
          gridbag.setConstraints(notificationButton, c);
          notificationButton.setBackground(java.awt.Color.white);
          notificationButton.setForeground(java.awt.Color.black);
          Util.setFont(data, notificationButton, data.PLAIN);
          pane.add(notificationButton);
      }



      // create a pane to hold the forum buttons
      JPanel buttonPane = new JPanel();
      buttonPane.setBackground(java.awt.Color.white);
      buttonPane.setForeground(java.awt.Color.black);
      buttonPane.setSize(400,200);
      Util.setFont(data, buttonPane, data.PLAIN);

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
      gbc.insets = new Insets(0,30,0,30);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.WEST;
      gridbag.setConstraints(okB, gbc);
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
      gridbag.setConstraints(cancelB, gbc);
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
      gridbag.setConstraints(helpB, gbc);
      helpB.setForeground(java.awt.Color.black);
      Util.setFont(data, helpB, data.BOLD);
      buttonPane.add(helpB);


      // Set the grid constraints for the button pane
      c.gridx = 0;
      c.gridy = 6;
      c.insets = new Insets(0,0,0,0);
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
        if (uploadB.isSelected())
          data.setUploadFlag(true);
        else
          data.setUploadFlag(false);
        data.setUnlockFlag(unlockButton.isSelected());
        data.setSavePrevFlag(prevButton.isSelected());
        if (data.getUseNotificationBlock()) {
            data.setBlockNotification(notificationButton.isSelected());
        }
        final_actionPerformed(e,data);
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

      prevButton.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          Object source = e.getItemSelectable();
          if (e.getStateChange() == ItemEvent.SELECTED)
            if (source == prevButton)
              data.setSavePrevFlag(true);

          if (e.getStateChange() == ItemEvent.DESELECTED)
            if (source == prevButton)
              data.setSavePrevFlag(false);
        }
      });
    }
  });

  }
  public void okPressed(ActionEvent e) {
    if (uploadB.isSelected())
      data.setUploadFlag(true);
    else
      data.setUploadFlag(false);
    data.setUnlockFlag(unlockButton.isSelected());
    data.setSavePrevFlag(prevButton.isSelected());
    if (data.getUseNotificationBlock())
        data.setBlockNotification(notificationButton.isSelected());
    final_actionPerformed(e,data);
  }

  public boolean final_actionPerformed(ActionEvent e,FEData data)
  {
    Util.progressBar(data,data.getParamText("processingText"));
    PostFileConnection poster = new PostFileConnection(data);
    return true;
  }
}
