/*
 * Created on Jul 18, 2005
 *
 */
package com.sitescape.ef.web.util;

import java.util.Map;
import java.util.Date;
import java.util.GregorianCalendar;
import com.sitescape.ef.domain.Event;

/**
 * @author billmers
 *
 */
public class EventHelper {
    

    // default method assumes duration and recurrence patterns
    static public Event getEventFromMap (Map formData, String formName, String id) {
        Boolean hasDur = new Boolean("true");
        Boolean hasRecur = new Boolean("true");
        Event e = getEventFromMap(formData, formName, id, hasDur, hasRecur);
        return e;
    }

    
    // basic method
    static public Event getEventFromMap (Map formData, String formName, 
            String id, Boolean hasDuration, Boolean hasRecurrence) { 
        // we make the id match what the event editor would do
        Event e = new Event();
        if (hasDuration.booleanValue()) {
            // duration present means there is a start and end id
            String startId = "dp_" + id;
            String endId = "dp2_" + id;

            Date start = DateHelper.getDateFromMap(formData, formName, startId);
            Date end = DateHelper.getDateFromMap(formData, formName, endId);
            // for now, if either date in the range is missing, we return null Event
            // (consider instead making a checked exception?)
            if (start == null || end == null) {
                return null;
            }
            GregorianCalendar startc = new GregorianCalendar();
            GregorianCalendar endc = new GregorianCalendar();
            startc.setTime(start);
            endc.setTime(end);
            e.setDtStart(startc);
            e.setDtEnd(endc);
        } else {
            String whenId = "dp3_" + id;
            Date when = DateHelper.getDateFromMap(formData, formName, whenId);
            if (when == null) {
                return null;
            }
            GregorianCalendar whenc = new GregorianCalendar();
            whenc.setTime(when);
            e.setDtStart(whenc);
            e.setDtEnd(whenc);
        }
        return e;
    }
}

