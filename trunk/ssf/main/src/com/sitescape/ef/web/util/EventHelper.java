/*
 * Created on Jul 18, 2005
 *
 */
package com.sitescape.ef.web.util;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.sitescape.util.cal.DayAndPosition;

import com.sitescape.ef.domain.Event;
import com.sitescape.ef.module.shared.InputDataAccessor;

/**
 * @author billmers
 *
 */
public class EventHelper {
    

    // default method assumes duration and recurrence patterns
    static public Event getEventFromMap (InputDataAccessor inputData, String id) {
        Boolean hasDur = new Boolean("true");
        Boolean hasRecur = new Boolean("true");
        Event e = getEventFromMap(inputData, id, hasDur, hasRecur);
        return e;
    }

    
    // basic method
    static public Event getEventFromMap (InputDataAccessor inputData, String id, 
    		Boolean hasDuration, Boolean hasRecurrence) { 
        // we make the id match what the event editor would do
        Event e = new Event();
        String prefix = id + "_";
        if (hasDuration.booleanValue()) {
            // duration present means there is a start and end id
            String startId = "dp_" + id;
            String endId = "dp2_" + id;

            Date start = DateHelper.getDateFromInput(inputData, startId);
            Date end = DateHelper.getDateFromInput(inputData, endId);
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
            Date when = DateHelper.getDateFromInput(inputData, whenId);
            if (when == null) {
                return null;
            }
            GregorianCalendar whenc = new GregorianCalendar();
            whenc.setTime(when);
            e.setDtStart(whenc);
            e.setDtEnd(whenc);
        }
        if (hasRecurrence.booleanValue()) {
            String repeatUnit = inputData.getSingleValue(prefix+"repeatUnit");
            String intervalStr = inputData.getSingleValue(prefix+"everyN");
            // rangeSel is the count/ until/ forever radio button
            String rangeSel = inputData.getSingleValue(prefix+"rangeSel");
            // this array maps the form checkboxes to the day-and-position constants
            int daysints[] = { 
                    Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 
                    Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY 
            };
            if (repeatUnit.equals("none")) {
                e.setFrequency(Event.NO_RECURRENCE);
            }
            if (repeatUnit.equals("day")) {
                e.setFrequency(Event.DAILY);
                e.setInterval(intervalStr);
            }
            if (repeatUnit.equals("week")) {
                e.setFrequency(Event.WEEKLY);
                e.setInterval(intervalStr);
                // first we count the number of checkboxes so that we can
                // make the array of the correct size (setByDay will try to 
                // clone the array, so any nulls inside will throw an exception)
                String days[] = new String[7];
                if (inputData.exists(prefix+"day0")) {
                	days[0] = inputData.getSingleValue(prefix+"day0");
                } else {
                	days[0] = "";
                }
                if (inputData.exists(prefix+"day1")) {
                	days[1] = inputData.getSingleValue(prefix+"day1");
                } else {
                	days[1] = "";
                }
                if (inputData.exists(prefix+"day2")) {
                	days[2] = inputData.getSingleValue(prefix+"day2");
                } else {
                	days[2] = "";
                }
                if (inputData.exists(prefix+"day3")) {
                	days[3] = inputData.getSingleValue(prefix+"day3");
                } else {
                	days[3] = "";
                }
                if (inputData.exists(prefix+"day4")) {
                	days[4] = inputData.getSingleValue(prefix+"day4");
                } else {
                	days[4] = "";
                }
                if (inputData.exists(prefix+"day5")) {
                	days[5] = inputData.getSingleValue(prefix+"day5");
                } else {
                	days[5] = "";
                }
                if (inputData.exists(prefix+"day6")) {
                	days[6] = inputData.getSingleValue(prefix+"day6");
                } else {
                	days[6] = "";
                }
                int arraysz = 0;
                for (int ct = 0; ct < 7; ct++) {
                    if (days[ct].equals("on")) {
                        arraysz++;
                    }
                }
                if (arraysz > 0) {
                    DayAndPosition dpa[] = new DayAndPosition[arraysz];
                    for (int i = 0, j=0; i < 7; i++) {
                        if (days[i].equals("on")) {
                            dpa[j] = new DayAndPosition();
                            dpa[j++].setDayOfWeek(daysints[i]);
                        }
                    }
                    e.setByDay(dpa);
                }
            }
            
            if (repeatUnit.equals("month")) {
                e.setFrequency(Event.MONTHLY);
                e.setInterval(intervalStr);
                String onDayCard = inputData.getSingleValue(prefix+"onDayCard");
                String dow = inputData.getSingleValue(prefix+"dow");
                int dayNum = 0;
                if (onDayCard.equals("first")) {
                    dayNum = 1;
                } else if (onDayCard.equals("second")) {
                    dayNum = 2;
                } else if (onDayCard.equals("third")) {
                    dayNum = 3;
                } else if (onDayCard.equals("fourth")) {
                    dayNum = 4;
                } else if (onDayCard.equals("last")) {
                    dayNum = 5;
                }
                int day = 0;
                if (dow.equals("Sunday")) {
                    day = Calendar.SUNDAY;
                } else if (dow.equals("Monday")) {
                    day = Calendar.MONDAY;
                } else if (dow.equals("Tuesday")) {
                    day = Calendar.TUESDAY;
                } else if (dow.equals("Wednesday")) {
                    day = Calendar.WEDNESDAY;
                } else if (dow.equals("Thursday")) {
                    day = Calendar.THURSDAY;
                } else if (dow.equals("Friday")) {
                    day = Calendar.FRIDAY;
                } else if (dow.equals("Saturday")) {
                    day = Calendar.SATURDAY;
                }
                if (!onDayCard.equals("none")) {
                    DayAndPosition dpa[] = new DayAndPosition[1];
                    dpa[0] = new DayAndPosition();
                    dpa[0].setDayOfWeek(day);
                    dpa[0].setDayPosition(dayNum);
                    e.setByDay(dpa);
                }
            }
            if (rangeSel.equals("count")) {
                String repeatCount = inputData.getSingleValue(prefix+"repeatCount");
                e.setCount(repeatCount);
            } else if (rangeSel.equals("until")) {
                String untilId = "endRange_" + id;
                Date until = DateHelper.getDateFromInput(inputData, untilId);
                GregorianCalendar untilCal = new GregorianCalendar();
                untilCal.setTime(until);
                e.setUntil(untilCal);
            } else if (rangeSel.equals("forever")) {
            	e.setCount(0);
            }

        } else {
            e.setFrequency(Event.NO_RECURRENCE);
        }
        
        return e;
    }
    
}

