/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by david on 4/13/16.
 */
public class Width implements IsSerializable {
    public enum Unit implements IsSerializable {
        pixels,
        percentage
    }
    private int value;
    private Unit unit;

    public static Width parseWidth(String widthStr) {
        Width width = null;
        if (widthStr!=null) {
            widthStr = widthStr.trim();
            try {
                if (widthStr.endsWith("%")) {
                    if (widthStr.length() > 1) {
                        width = new Width(Integer.parseInt(widthStr.substring(0, widthStr.length() - 1)), Unit.percentage);
                    }
                } else if (widthStr.endsWith("px")) {
                    if (widthStr.length() > 2) {
                        width = new Width(Integer.parseInt(widthStr.substring(0, widthStr.length() - 2)), Unit.pixels);
                    }
                } else {
                    width = new Width(Integer.parseInt(widthStr), Unit.pixels);
                }
            } catch (NumberFormatException e) {
            }
        }
        return width;
    }

    public Width() {
    }

    public Width(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (unit==Unit.percentage) {
            return Integer.toString(value) + "%";
        }
        return Integer.toString(value);
    }
}
