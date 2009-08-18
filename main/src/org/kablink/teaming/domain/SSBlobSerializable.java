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

package org.kablink.teaming.domain;

import java.sql.Blob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.codec.binary.Base64;
import org.kablink.teaming.dao.util.SSBlobSerializableType;
/**
 * @author Janet McCann
 * This is an immutable object.  Its value cannot be changed.  This allows
 * lazy value loading to work.  If not, the deepcopy method of a user type, would have
 * to read the value for later comparision. If the object is in the secondary cache, using  
 * this type is a waste, cause the value has to be loaded to cache anyway.
 * Create new instancs to change values 
 */
public class SSBlobSerializable implements Serializable {
	   transient Blob blob;
	   Object value=null;
	    
	    public SSBlobSerializable(Blob blob) {
	        this.blob = blob;
	    }
	    public SSBlobSerializable(Object value) {
	        this.value = value;
	    }
	    
	   public Object getValue() {
            if (value != null) return value;
            if (blob == null) {return null;}
 
    	    try {
    	    	 InputStream is = blob.getBinaryStream();
    	    	 if (is == null) return null;
    	    	 ObjectInputStream ois = new ObjectInputStream(is);
    	    	 try {
    	    	 	value = ois.readObject();
    	    	 	return value;
    	    	 }  catch (ClassNotFoundException ex) {
    	    	 	throw new IOException("Could not deserialize BLOB contents: " + ex.getLocalizedMessage());
    	    	 } finally {
    	    	 	ois.close();
    	    	 }
    	    } catch (Exception e) {
    	    	return null;
    	    }

    }
    public String toString() {
    	Object val = getValue();
    	if(val != null)
    		return val.toString();
    	else
    		return "";
    }
    public String toBase64String() {
    	Object val = getValue();
		if (val != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(SSBlobSerializableType.OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
			ObjectOutputStream oos=null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(val);
				oos.flush();
				return new String(Base64.encodeBase64(baos.toByteArray()),"utf-8");
			} catch (IOException ex) {
				return "";
			} finally {
				if (oos != null) 
					try {
						oos.close();
					} catch (Exception ex) {};
			}
		}
		else {
			return "";
		}

    }
}
