package com.sitescape.ef.applets.fileedit;

/**
 * Class Util provides utilities for the file edit applet.
 */

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container;
import netscape.javascript.JSObject;
import javax.swing.JProgressBar;
import java.awt.GraphicsEnvironment;
import javax.swing.JComponent;
import java.awt.Font;

public class Util {
  static FEData data;


    /** Creates a new instance of PostFileConnection */
  public Util(FEData data) {
    this.data = data;
  }

  public static void changeTitle(FEData data, String titleString)
  {
    // Set the title of the document
    try {
      JSObject win = JSObject.getWindow(data.getApplet());
      String args[] = {titleString};
      Object foo = win.call("changeTitle",args);
    } catch (Exception je) {
    }
  }
  // put up a progress bar to let the user know that something is happening
  public static void progressBar(FEData dat, String titleString)
  {
    final FEData data = dat;
    // Set the title of the document
    changeTitle(data, titleString);
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

        JProgressBar jpbar = new JProgressBar();
        // set the progress bar to go back and forth.
        jpbar.setIndeterminate(true);
        jpbar.setBackground(java.awt.Color.white);
        jpbar.setForeground(java.awt.Color.black);
        jpbar.setString(data.getParamText("processingText"));
        jpbar.setStringPainted(true);
        jpbar.setSize(400,200);
        //setFont(data, jpbar, data.PLAIN);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        jpbar.setLayout(gb);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        pane.add(jpbar);
        applet.validate();
        applet.repaint();
      }
    });
    return;
  }
  public static void setFont(FEData data, JComponent component, int fontStyle) {
        // We set font to Ariel in same size and style as current.
        boolean found;
        int style = data.PLAIN;
        GraphicsEnvironment ge = GraphicsEnvironment.
            getLocalGraphicsEnvironment();
        String typefaceName = "Arial";
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        found = false;
        // make sure the desired typeface exists.
        for (int i=0; i<fontFamilies.length; i++) {
            //            log.it("family " + i + " = " + fontFamilies[i]);
            if (typefaceName.equals(fontFamilies[i])) {
                found = true;
            }
        }
        Font oldFont = component.getFont();

        if (! found) {
          // use the default
          typefaceName = oldFont.getFontName();
        }

        //int style = oldFont.getStyle();
        if (fontStyle == data.BOLD)
           style = oldFont.BOLD;
        else
           style = oldFont.PLAIN;
        int size = oldFont.getSize();
        Font newFont = new Font(typefaceName, style, size);
        component.setFont(newFont);
    }
}
