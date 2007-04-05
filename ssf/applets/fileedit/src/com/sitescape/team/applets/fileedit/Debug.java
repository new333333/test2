/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.applets.fileedit;

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
      fw.flush();
      fw.close();
    } catch (Exception e) {}
  }
}
