/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
 * Copyright 2010 Traction Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kablink.teaming.gwt.client.widgets;

import java.util.Date;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A wrapper around a DateBox that implements HasValue<Long> where the
 * value is the number of milliseconds since January 1, 1970, 00:00:00 GMT
 * <b>at midnight on the day, month, and year selected</b>. This avoids
 * timezone conversion issues encountered using the DateBox.
 * 
 * Based on the open source gwt-traction project's
 * 'com.tractionsoftware.gwt.user.client.ui.UTCDateBox' class.
 */
public class TZDateBox extends Composite implements HasValue<Long>, HasValueChangeHandlers<Long> {
    private DateBox m_datebox;				//
	private long	m_timezoneOffsetMillis;	//

	/**
	 * Constructor method.
	 */
    public TZDateBox() {
        init(new DateBox(), new Date());
    }

    /**
     * Constructor method.
     * 
     * @param picker
     * @param date
     * @param format
     */
    public TZDateBox(DatePicker picker, long date, DateBox.Format format) {
        init(new DateBox(picker, tz2date(date), format), new Date(date));
    }

    /*
     * Initializes the TZDateBox.
     */
    private void init(DateBox datebox, Date date) {
    	m_timezoneOffsetMillis = GwtClientHelper.getTimeZoneOffsetMillis(date);
    	
        m_datebox = datebox;       
        m_datebox.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                // pass this event onto our handlers after converting the value
                fireValueChangeEvent(date2tz(event.getValue()));
            }
        });
        
        {
        	DateTimeFormat dateFormat;
        	DateBox.Format format;

        	dateFormat = GwtClientHelper.getShortDateFormat();

        	format = new DateBox.DefaultFormat( dateFormat );
        	m_datebox.setFormat( format );        	
        }
       
        initWidget(m_datebox);
    }
       
    /**
     * Provides access to the underlying DateBox. Beware using this
     * directly because anything that returns a Date might need to be
     * adjusted to the timezone using date2tz.
     * 
     * @return
     */
    public DateBox getDateBox() {
        return m_datebox;
    }

    /**
     * Returns the current time from the DateBox as a Long.
     * 
     * @return
     */
    @Override
	public Long getValue() {
        return date2tz(m_datebox.getValue());
    }

    /**
     * Returns the raw time from the DateBox as a Long.
     * 
     * @return
     */
    public Long getRawValue() {
        return m_datebox.getValue().getTime();
    }

    /**
     * Stores a new value in the date box without firing a change
     * event.
     * 
     * @param value
     */
    @Override
	public void setValue(Long value) {
        setValue(value, false);
    }
   
    /**
     * Stores a new value in the date box and optionally fires a change
     * event.
     *  
     * @param value
     * @param fireEvents
     */
    @Override
	public void setValue(Long value, boolean fireEvents) {
        m_datebox.setValue(((null == value) ? null : tz2date(value)), fireEvents);
    }

    /*
     * Fires a change event for the date box.
     */
    private void fireValueChangeEvent(long value) {
        ValueChangeEvent.fire(this, new Long(value));            
    }

    /**
     * Adds a ValueChangeHandler to the date box.
     * 
     * @param handler
     * 
     * @return
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /*
     * Converts a time in the timezone to a GWT Date object which is in
     * adjusted for the specified timezone.
     */
    private final Date tz2date(long time) {
        // Don't accept negative values
        if (0 > time) {
        	return null;
        }
       
        // Add the timezone offset.
        return new Date(time + m_timezoneOffsetMillis);
    }

    /*
     * Converts a GWT Date in the timezone of the current browser to a
     * time in the specified timezone.
     */
    private final long date2tz(Date date) {
        // Use -1 to mean a bogus date.
        if (null == date) {
        	return -1;
        }
       
        // Remove the timezone offset.        
        return (date.getTime() - m_timezoneOffsetMillis);
    }

    /**
     * Returns the current timezone offset being used.
     * 
     * @return
     */
    public long getTZOffset() {
    	return m_timezoneOffsetMillis;
    }
    
    /**
     * Sets a new timezone offset to use.
     * 
     * @param timezoneOffsetMillis
     */
    public void setTZOffset(long timezoneOffsetMillis) {
    	m_timezoneOffsetMillis = timezoneOffsetMillis;
    }
}
