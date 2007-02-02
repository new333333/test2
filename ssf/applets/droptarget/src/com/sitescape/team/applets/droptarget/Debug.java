package com.sitescape.team.applets.droptarget;

import java.io.File;
import java.io.FileWriter;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class Debug {

  static File file = null;
  static FileWriter fw = null;

  public Debug() {
  }

  public static void debug(String text)
  {
    try {
      fw = new FileWriter("c:/temp/debug.txt", true);
      fw.write(text);
      fw.write("\n\r");
      fw.write("\n\r");
      fw.flush();
      fw.close();
    } catch (Exception e) {}
  }
}
