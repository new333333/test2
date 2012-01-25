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
/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.SQLException;
/**
 * Hide a clob string and lazy load the value.
 * This is an immutable object.  Its value cannot be changed.  This allows
 * lazy value loading to work.  If not, the deepcopy method of a user type, would have
 * to read the value for later comparision.
 * 
 * Create new instancs to change values 
 */
public class SSClobString implements Serializable {
    transient Clob clob;
    String value=null;
    
    public SSClobString(Clob clob) {
        this.clob = clob;
    }
    public SSClobString(String value) {
        this.value = value;
    }
    
    public String getText() {
        try {
            if (value != null) return value;
            if (clob == null) {return "";}
 
            value = clob.getSubString(1, (int) clob.length());
            if (value != null) return value;
            return "";
        } catch (SQLException ex) {
            return "";
        }

    }
	public boolean equals(Object obj) {
		//Normally == would be enough of a test because this object is immutable.
		//In most cases that will be enough.  But if the owning object is
		//refreshed, == will fail when compared to the 'deepCopy' and the object
		// will appear dirty.  So - to catch this and avoid unnecessary updates, 
		// compare values - at this point the clob will have been read anyway.
		if (this == obj) return true;
		if (obj==null) return false;
		if (!(obj instanceof SSClobString)) return false;
		SSClobString y = (SSClobString)obj;
		return getText().equals(y.getText());
	}

    public String toString() {
        return getText();
    }
}

