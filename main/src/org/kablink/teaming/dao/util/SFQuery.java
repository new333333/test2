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
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.dao.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * Wrapper object to abstract the hibernate query object.
 * 
 */
public class SFQuery implements Iterator {
    private Query query;
    private ScrollableResults scroll = null;
    // Indicates whether there is more item available that has NOT been read by the caller yet.
    // The implementation uses look-ahead strategy in order to keep this variable meaningful.
    private boolean more = true;
    
    public SFQuery(Query query) {
        this.query = query;
    }
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext()  {
        try {
        	initScroll();
        	return more;
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Object next() {
        try {
        	initScroll();
        	if(more) {
        		Object result = scroll.get();
        		more = scroll.next();
        		return result;
        	}
        	else {
        		throw new NoSuchElementException();
        	}
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }
    
    /*
     * Close out the scrollable results to release the resources
     */
    public void close() {
        try {
            if (scroll != null)
            	scroll.close();
        } catch (HibernateException ex) {
        } finally {
            scroll=null;            
        }
    }

    private void initScroll() {
    	if(scroll == null) {
    		scroll = query.scroll();
    		if(scroll.first()) {
    			more = true;
    		}
    		else {
    			more = false;
    		}
    	}
    }
}

