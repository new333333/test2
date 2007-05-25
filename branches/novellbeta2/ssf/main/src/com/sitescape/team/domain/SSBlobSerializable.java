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

package com.sitescape.team.domain;

import java.sql.Blob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sitescape.team.dao.util.SSBlobSerializableType;
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
				return baos.toString("utf-8");
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
