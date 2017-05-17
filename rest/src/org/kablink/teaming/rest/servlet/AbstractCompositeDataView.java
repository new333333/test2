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
package org.kablink.teaming.rest.servlet;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 3/21/11
 * Time: 2:45 PM
 */
public abstract class AbstractCompositeDataView implements CompositeDataView {

    protected abstract Map<String, Object> toMap();

    @Override
    public CompositeData toCompositeData(CompositeType ct) {
        Map<String, Object> values = toMap();
        String [] itemNames = values.keySet().toArray(new String[values.size()]);
        OpenType[] itemTypes = new OpenType[itemNames.length];
        int i = 0;
        for (String itemName : itemNames) {
            OpenType type = SimpleType.VOID;
            Object value = values.get(itemName);
            if (value instanceof Integer) {
                type = SimpleType.INTEGER;
            } else if (value instanceof Long) {
                type = SimpleType.LONG;
            } else if (value instanceof String) {
                type = SimpleType.STRING;
            } else if (value instanceof Date) {
                type = SimpleType.DATE;
            } else if (value instanceof Double) {
                type = SimpleType.DOUBLE;
            }
            itemTypes[i++] = type;
        }
        try {
            CompositeType actualCt = new CompositeType(this.getClass().getName(), this.getClass().getName(), itemNames, itemNames, itemTypes);
            return new CompositeDataSupport(actualCt, values);
        } catch (Exception e) {
            throw new IllegalArgumentException(ct.getTypeName(), e);
        }
    }
}
