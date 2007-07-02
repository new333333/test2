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

package com.sitescape.team.dao.impl;

import java.util.List;

import com.sitescape.team.dao.XMLDao;

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
