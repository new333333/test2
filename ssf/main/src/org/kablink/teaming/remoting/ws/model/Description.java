/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Description implements Serializable {

	private static final Log logger = LogFactory.getLog(Description.class);
	
	private String text;
	private int format=1;//html default
	
	public Description() {}
	
	public Description(String text, int format) {
		setText(text);
		setFormat(format);
	}
	
	public int getFormat() {
		return format;
	}
	public void setFormat(int format) {
		this.format = format;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		if(text != null) {
			// Bug 752014: Filter out xml-unsafe characters before handing this object to Axis SOAP engine for serialization.
			StringBuilder sb = new StringBuilder();
			int length = text.length();
			char character;
			for(int i = 0; i < length; i++) {
				character = text.charAt(i);
				if(character <  0x20) {
					// discard this character
					logger.debug("The char '" + Integer.toHexString(character) + "' is not a valid XML character safe for SOAP. Discarding it.");
				}
				else {
					sb.append(character);
				}
			}
			this.text = sb.toString();
		}
		else {
			this.text = null;
		}
	}
	public String toString() {
		return getText();
	}
}
