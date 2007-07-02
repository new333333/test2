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
/*
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.dao.util;

import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * @author janet
 * Wrapper object to abstract the hibernate query object.
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFQuery implements Iterator {
    private Query query;
    private ScrollableResults scroll = null;
    private boolean more = true;
    
    public SFQuery(Query query) {
        this.query = query;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()  {
        // TODO Auto-generated method stub
        try {
            if (scroll == null) {
                scroll = query.scroll();
                return scroll.first();
            }            
            //haven't choosen yet
            if (scroll.isFirst()) return true;
            return more;
            
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        try {
            if (scroll == null) {
                scroll = query.scroll();
                scroll.first();
            }
            Object result = scroll.get();
            more = scroll.next();
            return result;
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }
    /*
     * Close out the scrollable results to release the resources
     */
    public void close() {
        try {
            if (scroll != null) {scroll.close();};
        } catch (HibernateException ex) {
        } finally {
            scroll=null;            
        }
    }

}

