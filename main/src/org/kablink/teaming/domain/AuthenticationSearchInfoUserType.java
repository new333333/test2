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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.hibernate.HibernateException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;

public class AuthenticationSearchInfoUserType extends ClobStringType { 

    public Class returnedClass() { 
        return List.class; 
    }
 
    @Override
    protected Object nullSafeGetInternal(ResultSet resultSet, String[] names, Object owner, LobHandler lobHandler) throws SQLException{ 
        List<LdapConnectionConfig.SearchInfo> result = new LinkedList<LdapConnectionConfig.SearchInfo>();
        if (!resultSet.wasNull()) {
			String searches = lobHandler.getClobAsString(resultSet,names[0]);
    		try {
    			Document doc = DocumentHelper.parseText(searches);
    			for(Object o : doc.selectNodes("//search")) {
    				Node node = (Node) o;
    				String baseDn = node.selectSingleNode("baseDn").getText();
    				String filter = node.selectSingleNode("filter").getText();
    				boolean searchSubtree = "true".equals(node.selectSingleNode("@searchSubtree").getText());
    				result.add(new LdapConnectionConfig.SearchInfo(baseDn, filter, searchSubtree));
    			}
    		} catch(Exception e) {
    			logger.warn("Unable to parse searches: " + searches);
    		}
        } 
        return result; 
    }
 
    public void nullSafeSetInternal(PreparedStatement preparedStatement, int index, Object value, LobCreator lobCreator) throws SQLException { 
        if (null == value) { 
            preparedStatement.setNull(index, Types.CLOB); 
        } else {
        	List<LdapConnectionConfig.SearchInfo> searches = (List<LdapConnectionConfig.SearchInfo>) value;
    		StringBuffer xml = new StringBuffer("<searches>");
    		for(LdapConnectionConfig.SearchInfo us : searches) {
    			xml.append("<search searchSubtree=\"" + (us.isSearchSubtree()?"true":"false") + "\">");

				// If the base dn has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
				// and try to create an xml document.  Wrap the base dn with <![CDATA[]]>.
    			xml.append( "<baseDn>" + wrapWithCDATA( us.getBaseDn() ) + "</baseDn>");

				// If the filter has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
				// and try to create an xml document.  Wrap the filter with <![CDATA[]]>.
    			xml.append( "<filter>" + wrapWithCDATA( us.getFilter() ) + "</filter>");
    			xml.append("</search>");
    		}
    		xml.append("</searches>");

            lobCreator.setClobAsString(preparedStatement,index, xml.toString()); 
        } 
    } 
 
    public Object deepCopy(Object value) throws HibernateException{ 
        return new LinkedList<LdapConnectionConfig.SearchInfo>((List<LdapConnectionConfig.SearchInfo>) value); 
    } 
 
    public boolean isMutable() { 
        return true; 
    } 
 
    public Object assemble(Serializable cached, Object owner) throws HibernateException { 
         return deepCopy(cached);
    } 

    public Serializable disassemble(Object value) throws HibernateException { 
        return (Serializable)deepCopy(value); 
    } 
 
    public Object replace(Object original, Object target, Object owner) throws HibernateException { 
        return new LinkedList<LdapConnectionConfig.SearchInfo>((List<LdapConnectionConfig.SearchInfo>) original); 
    }

	/**
	 * Wrap the given text with <![CDATA[ ]]>
	 */
	private String wrapWithCDATA( String str )
	{
		StringBuffer	wrappedStr;
		
		wrappedStr = new StringBuffer( "<![CDATA[" );
		if ( str != null && str.length() > 0 )
			wrappedStr.append( str );
		
		wrappedStr.append( "]]>" );
		
		return wrappedStr.toString();
	}// end wrapWithCDATA()
}
