/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.dao.util;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.support.AbstractLobType;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import com.sitescape.ef.domain.SSBlobSerializable;
import com.sitescape.ef.domain.SSClobString;

/*
 * Copied from  the spring ClobStingType to implement lazy loading of clobs
 * Cannot extend, need to override one of its final methods
 */

public class SSClobStringType extends AbstractLobType {

 
	public SSClobStringType() {

	}
    public int[] sqlTypes() {
    	return new int[] {Types.CLOB};
    }
	public boolean isMutable() {
		return false;
	}
    public Class returnedClass() {
    	return SSClobString.class;
    }

 
	protected Object nullSafeGetInternal(
			ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
			throws SQLException, IOException, HibernateException {
	    return new SSClobString(rs.getClob(names[0]));
	}

	protected void nullSafeSetInternal(PreparedStatement ps, int index, Object value, LobCreator lobCreator)
			throws SQLException {
		String val = null;
		if (value != null)
			val = ((SSClobString)value).getText();
    	//Frontbase doesn't like set setClobAsString
    	lobCreator.setClobAsString(ps, index, val);
 
	}  
	//make sure clob is loaded for storage in cache
	public Serializable disassemble(Object value) throws HibernateException {
		if (value != null) {
			((SSClobString)value).getText();
		}
		return (Serializable) value;
	}

}