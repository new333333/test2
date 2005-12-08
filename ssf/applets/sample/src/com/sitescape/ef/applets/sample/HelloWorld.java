package com.sitescape.ef.applets.sample;

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