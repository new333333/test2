/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 12/10/2014.
 */
public class MergedOrderedListIterator implements Iterator{
    private List<ListWrapper> wrappers;
    private Comparator comparator;

    /**
     *
     * @param comparator
     * @param lists a list of ordered lists
     */
    public MergedOrderedListIterator(Comparator comparator, List... lists) {
        wrappers = new ArrayList<ListWrapper>(lists.length);
        for (List list : lists) {
            if (list!=null) {
                wrappers.add(new ListWrapper(list));
            }
        }
        this.comparator = comparator;
    }

    @Override
    public boolean hasNext() {
        for (ListWrapper wrapper : wrappers) {
            if (wrapper.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object next() {
        Object next = null;
        ListWrapper winningWrapper = null;
        for (ListWrapper wrapper : wrappers) {
            Object candidate = wrapper.peek();
            if (candidate!=null) {
                if (next == null || comparator.compare(next, candidate)>0) {
                    next = candidate;
                    winningWrapper = wrapper;
                }
            }
        }
        if (winningWrapper!=null) {
            winningWrapper.advance();
        }
        return next;
    }

    @Override
    public void remove() {

    }

    private static class ListWrapper implements Iterator {
        private List list;
        private Iterator listIterator;
        private Object next;

        public ListWrapper(List list) {
            this.list = list;
            this.listIterator = list.iterator();
        }

        public boolean hasNext() {
            return next!=null || listIterator.hasNext();
        }

        @Override
        public Object next() {
            Object retObj;
            if (next!=null) {
                retObj = next;
                next = null;
            } else {
                retObj = listIterator.next();
            }
            return retObj;
        }

        @Override
        public void remove() {

        }

        public void advance() {
            next();
        }

        public Object peek() {
            if (next==null && listIterator.hasNext()) {
                next = listIterator.next();
            }
            return next;
        }
    }
}
