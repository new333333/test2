/*
 * Copyright © 2009-2016 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package org.kablink.teaming.gwt.client.widgets;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * Created by david on 8/31/16.
 */
public class VibeHtmlPanel extends HTMLPanel
        implements VibeEntityViewPanel {

    public VibeHtmlPanel(String html) {
        super(html);
    }

    public VibeHtmlPanel(String tag, String html) {
        super(tag, html);
    }

    public void setInnerText(String text) {
        if (text !=null) {
            getElement().setInnerText(text);
        }
    }

    public void setRootAttributes(Map<String, String> attributes) {
        if (attributes !=null) {
            Element elem = getElement();
            for (String key : attributes.keySet()) {
                elem.setAttribute(key, attributes.get(key));
            }
        }
    }

    @Override
    public void showWidget(Widget widget) {
        add(widget);
    }

    @Override
    public int getContainingHeight(Widget widget) {
        int totalHeight = getOffsetHeight();
        //GwtClientHelper.consoleLog("VibeHtmlPanel.getContainingHeight(): totalHeight=" + totalHeight);
        for (Widget child : getChildren()) {
            if (child!=widget) {
//				GwtClientHelper.consoleLog("VibeHtmlPanel.getContainingHeight(): found other widget; height=" + child.getOffsetHeight());
                totalHeight -= child.getOffsetHeight();
            }
        }
//		GwtClientHelper.consoleLog("VibeHtmlPanel.getContainingHeight(): final containing height=" + totalHeight);
        return totalHeight;
    }

    @Override
    public int getContainingWidth(Widget widget) {
        return getOffsetWidth();
    }
}
