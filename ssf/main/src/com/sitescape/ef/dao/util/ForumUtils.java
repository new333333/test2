/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.dao.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ForumUtils {
 
    
    public static Object XmlDecodeByteArrayToObject(byte[] xml) {
	    InputStream ois = new ByteArrayInputStream(xml);
	    if (ois == null) return null;
	    XMLDecoder d = new XMLDecoder(ois);
	    try {
	        return d.readObject();	        
	    }
	    finally {
	        d.close();
	    }
    }
    public static byte[] XmlEncodeObjectToByteArray(Object src) {
        if (src == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(baos);
		try {
			e.writeObject(src);
			e.flush();
		}
		finally {
			e.close();
		}
		return baos.toByteArray();        
 
    }
    public static Document XmlDecodeByteArrayToDocument(byte[] xml) {
	    InputStream ois = new ByteArrayInputStream(xml);
	    if (ois == null) return null;
	    SAXBuilder parser = new SAXBuilder();
	    try {
		    return parser.build(ois);
	    } catch (JDOMException ex) {
	        
	    } /* The following catch clause does not compile.
	     catch (IOException ioe) {
	    }*/
	    finally {   
	        try {ois.close();} catch (IOException ios) {} finally {}
	    }
        return null;
    }
 }
