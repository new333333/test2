package com.sitescape.team.domain;

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
    			xml.append("<baseDn>"+us.getBaseDn()+"</baseDn>");
    			xml.append("<filter>"+us.getFilter()+"</filter>");
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
}
