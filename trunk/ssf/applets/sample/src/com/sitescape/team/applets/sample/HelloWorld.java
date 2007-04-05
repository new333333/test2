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
package com.sitescape.team.applets.sample;

import java.applet.Applet;
import java.awt.Graphics;

public class HelloWorld extends Applet {
	
    public void paint(Graphics g) {
    	//Draw a Rectangle around the applet's display area.
        g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

    	//Draw a string inside the rectangle.
         g.drawString("Hello World Applet", 5, 15);
    }
}