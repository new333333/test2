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

import java.io.File;
import java.awt.Color;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JApplet;
import javax.swing.JLabel;
import java.util.regex.Pattern;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class FEData {
  Hashtable textTable;              // A hashtable to hold all the text strings read in from the parameters
  String postURL;                   // The URL to post the resulting doc to
  String documentURL;               // The URL of the document to edit
  String abandonURL;                // The URL to send the unlock/lock parameter to when the edit is abandoned
  String origFileName;              // The filename of the document
  String updateString;              // Whether or not to save the resulting doc back to the forum
  String relativePath;              // The path of the file, relative to where it's stored as an attachment in the document
  String forumFileName;             // The full name with path of the file to place back in the forum
  String localFileName;             // the temporary local filename
  String forumName;                 // The name of the forum (used for unique tempname)
  String docId;                     // The docid within this forum (used for unique tempname)
  String zoneName;                  // The zone name (used for unique tempname)

  boolean updateInForum = false;    // Whether or not to post the resulting doc back to the forum
  JButton downloadB;                // a button to use to test the applet
  JApplet applet;                   // the applet itself
  JLabel  statusLabel;              // the label used to present the status to the user.
  long origLastModified;            // the last modified time of the temp file (so we know if the user modified it)
  long newLastModified;             // the last modified time of the temp file (so we know if the user modified it)
  boolean uploadFlag;               // Whether or not to upload the document.
  boolean unlockFlag;               // Whether or not to lock/unlock the document.
  boolean savePrevFlag;             // Whether or not to save the previous version of the document.
  boolean useNotificationBlock;     // Whether the notification block checkbox should be shown.
  boolean blockNotification;        // Whether or not to block notification.
  final String FS = "/";
  final String DOT = ".";
  final int PLAIN = 1;
  final int BOLD  = 2;
  final int SAMEFONT = 0;
  final int BIGGERFONT = 2;

  public FEData() {
    // Set initial values for all the params
    postURL = null;
    documentURL = null;
    origFileName = null;
    updateString = null;
    relativePath = null;
    forumFileName = null;
    localFileName = null;
    //launchMessage = null;
    forumName = null;
    docId = null;
    downloadB = null;
    origLastModified = 0;
    newLastModified = 0;
    savePrevFlag = true;
    textTable = new Hashtable();
  }

  /**
   * Set and get the url to post the new document to
   */
  public void setPostUrl(String posturl) {
    postURL = new String(posturl);
  }
  public String getPostUrl() {
    return postURL;
  }

  /**
   * Set and get the document url (the url to download the file)
   */
  public void setDocumentURL(String documenturl) {
    documentURL = new String(documenturl);
  }
  public String getDocumentURL() {
    return documentURL;
  }

  /**
   * Set and get the document url (the url to download the file)
   */
  public void setAbandonURL(String abandonurl) {
    abandonURL = new String(abandonurl);
  }
  public String getAbandonURL() {
    return abandonURL;
  }
  /**
   * Set and get the original file name
   */
  public void setOrigFileName(String origfilename) {
    origFileName = new String(origfilename);
  }
  public String getOrigFileName() {
    return origFileName;
  }

  /**
   * Get and set whether or not to save the resulting doc back to the forum
   */
  public void setUpdateInForum(String updatestring) {
    if (updatestring.equalsIgnoreCase("editable"))
      updateInForum = true;
  }
  public boolean getUpdateInForum() {
    return updateInForum;
  }

  /**
   * Get and set the path of the file, relative to where it's stored as an attachment in
   * a document in the forum
   */
  public void setRelativePath(String relativepath) {
    relativePath = new String(relativepath);
  }
  public String getRelativePath() {
    return relativePath;
  }

  /**
   * Get the full name of the file in the forum (relative path + filename)
   */
  public String getForumFileName() {
    return relativePath + FS + origFileName;
  }

  /**
   * Get and set the local name of the file (temporary working copy)
   */
  public void setLocalFileName(String localfilename) {
    localFileName = new String(localfilename);
  }
  public String getLocalFileName() {
    return localFileName;
  }

  /**
   * Get and set a pointer to the download button
   */
  public void setDownloadB(JButton downloadbutton) {
    downloadB = downloadbutton;
  }
  public JButton getDownloadB() {
    return downloadB;
  }

  /**
   * Get and set a pointer to the status label
   */
  public void setStatusLabel(JLabel statuslabel) {
    statusLabel = statuslabel;
  }
  public JLabel getStatusLabel() {
    return statusLabel;
  }

  /**
   * Get and set the forum name
   */
  public void setForumName(String forumname) {
    forumName = forumname;
  }
  public String getForumName() {
    return forumName;
  }

  /**
   * Get and set the document id
   */
  public void setDocId(String docid) {
    docId = docid;
  }
  public String getDocId() {
    return docId;
  }

  /**
   * Get and set the zone name
   */
  public void setZoneName(String zonename) {
    zoneName = zonename;
  }
  public String getZoneName() {
    return zoneName;
  }

  /**
   * Get and set the applet
   */
  public void setApplet(JApplet app) {
    applet = app;
  }
  public JApplet getApplet() {
    return applet;
  }

  /**
   * Get and set the unlock flag
   */
  public void setUnlockFlag(boolean unlockflag) {
      unlockFlag = unlockflag;
  }

  public boolean getUnlockFlag() {
      return unlockFlag;
  }

  /**
   * Get and set the save prev flag
   */
  public void setSavePrevFlag(boolean saveprevflag) {
      savePrevFlag = saveprevflag;
  }

  public boolean getSavePrevFlag() {
      return savePrevFlag;
  }

  /**
   * Get and set the update flag
   */
  public void setUploadFlag(boolean uploadflag) {
      uploadFlag = uploadflag;
  }

  public boolean getUploadFlag() {
      return uploadFlag;
  }
  /**
   * Get and set the update flag
   */
  public void setUseNotificationBlock(boolean unb) {
      useNotificationBlock = unb;
  }

  public boolean getUseNotificationBlock() {
      return useNotificationBlock;
  }
    /**
   * Get and set the update flag
   */
  public void setBlockNotification(boolean bn) {
      blockNotification = bn;
  }

  public boolean getBlockNotification() {
      return blockNotification;
  }

  /**
   * Get and set the last modified timestamp of a file.
   */
  public long getLastModified() {
      if (localFileName != null) {
        File tfile = new File(localFileName);
        return tfile.lastModified();
      }
      return 0;
  }

  /**
   * Get the local tempFileName
   */
   public String getTempFileName()
   {
     String tmpFileName = zoneName + DOT +
                          forumName + DOT +
                          docId + DOT +
                          origFileName;

     return tmpFileName;
  }


  /**
   * Get the Launch name (cmd.exe won't recognize a bunch of special chars)
   */
   public String getLaunchName(String fileName)
   {
     // Escape all the chars that the dos batch program won't recognize
     Pattern escaper = Pattern.compile("\\^|\\$|\\%|\\?|\\!|\\&|\\(|\\)| |,");
     String escapedName = escaper.matcher(fileName.toString()).replaceAll("\\^$0");
     return escapedName;
  }

  /**
   * Generic routine to read in the parameter text strings used by this applet and
   * place them into a hashtable. It's necessary to pass in all the user facing
   * strings because they're translated in the templates, not in the applet code.
   */
  public void setParamText(String paramName, String defaultString)
  {
    // get the parameter from the applet context and place into a hash
    // table. If it's not found then put the default string into the hash table.
    String paramText = this.applet.getParameter(paramName);
    if (paramText == null)
       textTable.put(paramName,defaultString);
    else
       textTable.put(paramName,paramText);
  }

  /**
   * Get a string out of the hashtable.
   */
  public String getParamText(String paramName)
  {
    // get the string from the hashtable
    return (String)textTable.get(paramName);
  }

}