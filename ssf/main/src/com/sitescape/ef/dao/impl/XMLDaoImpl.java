
package com.sitescape.ef.dao.impl;

import java.util.List;
import com.sitescape.ef.dao.XMLDao;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.EntityMode;
/**
 * @author Janet McCann
 *
 */
public class XMLDaoImpl extends HibernateDaoSupport implements XMLDao {
    public Element load(final Class clazz, final Long id) {
    	Element dom = (Element)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	Session dom = session.getSession(EntityMode.DOM4J); 
		        	Element obj = (Element)dom.get(clazz, id);
					return obj;
		        }
	        }
	
		);
		String val = dom.getStringValue();
		return dom;
    }
}
