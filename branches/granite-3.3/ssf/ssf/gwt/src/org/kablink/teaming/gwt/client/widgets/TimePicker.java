/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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

/*
 * Copyright 2008 Google Inc.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.widgets.SpinnerListener;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner;
import org.kablink.teaming.gwt.client.widgets.Spinner.SpinnerResources;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner.ValueSpinnerResources;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * {@link TimePicker} widget to enter the time part of a date using spinners
 * 
 * 20110712 (DRF):
 *    I copied this class from the GWT incubator into the Vibe OnPrem
 *    ssf (open) source tree.
 */
public class TimePicker extends Composite implements HasValueChangeHandlers<Date> {
  private class TimeSpinner extends ValueSpinner {
    private DateTimeFormat dateTimeFormat;

    public TimeSpinner(Date date, DateTimeFormat dateTimeFormat, int step,
        ValueSpinnerResources styles, SpinnerResources images) {
      super(date.getTime(), styles, images);
      this.dateTimeFormat = dateTimeFormat;
      getSpinner().setMinStep(step);
      getSpinner().setMaxStep(step);
      // Refresh value after dateTimeFormat is set
      getSpinner().setValue(date.getTime(), true);
    }

	@Override
	protected String formatValue(double value) {
      dateInMillis = value;
      if (dateTimeFormat != null) {
        return dateTimeFormat.format(new Date((long) dateInMillis));
      }
      return "";
    }

	@Override
    protected double parseValue(String value) {
      Date parsedDate = new Date((long) dateInMillis);
      dateTimeFormat.parse(value, 0, parsedDate);
      return parsedDate.getTime();
    }
  }

  private static final int SECOND_IN_MILLIS = 1000;
  private static final int MINUTE_IN_MILLIS = 60000;
  private static final int HOUR_IN_MILLIS = 3600000;
  private static final int HALF_DAY_IN_MS = 43200000;
  private static final int DAY_IN_MS = 86400000;

  private List<TimeSpinner> timeSpinners = new ArrayList<TimeSpinner>();
  private double dateInMillis;
  private boolean enabled = true;

  private SpinnerListener listener = new SpinnerListener() {
	@Override
    public void onSpinning(double value) {
      ValueChangeEvent.fireIfNotEqual(TimePicker.this, new Date((long) dateInMillis),
          new Date((long) value));
    };
  };

  /**
   * @param use24Hours	If set to true the {@link TimePicker} will use 24h format.
   */
  public TimePicker(boolean use24Hours) {
    this(
    	new Date(),
    	use24Hours);
  }

  /**
   * @param date		The date providing the initial time to display.
   * @param use24Hours	If set to true the {@link TimePicker} will use 24h format.
   */
  public TimePicker(Date date, boolean use24Hours) {
    this(
    	date,
    	use24Hours,
    	DateTimeFormat.getFormat("ss"));
  }

  /**
   * @param date			The date providing the initial time to display.
   * @param use24Hours		If set to true the {@link TimePicker} will use 24h format.
   * @param secondsFormat	The format to display the seconds. Can be null to seconds field.
   */
  public TimePicker(Date date, boolean use24Hours, DateTimeFormat secondsFormat) {
    this(
    	date,
    	use24Hours ? null                           : DateTimeFormat.getFormat("aa"),
    	use24Hours ? DateTimeFormat.getFormat("HH") : DateTimeFormat.getFormat("hh"),
        DateTimeFormat.getFormat("mm"),
        secondsFormat);
  }

  /**
   * @param date 			The date providing the initial time to display.
   * @param amPmFormat		The format to display AM/PM. Can be null to hide AM/PM field.
   * @param hoursFormat		The format to display the hours. Can be null to hide hours field.
   * @param minutesFormat	The format to display the minutes. Can be null to hide minutes field.
   * @param secondsFormat	The format to display the seconds. Can be null to seconds field.
   */
  public TimePicker(Date date, DateTimeFormat amPmFormat, DateTimeFormat hoursFormat, DateTimeFormat minutesFormat, DateTimeFormat secondsFormat) {
    this(
    	date,
    	amPmFormat,
    	hoursFormat,
    	minutesFormat,
    	secondsFormat,
    	null,
    	null);
  }

  /**
   * @param date			The date providing the initial time to display.
   * @param amPmFormat		The format to display AM/PM. Can be null to hide AM/PM field.
   * @param hoursFormat		The format to display the hours. Can be null to hide hours field.
   * @param minutesFormat	The format to display the minutes. Can be null to hide minutes field.
   * @param secondsFormat	The format to display the seconds. Can be null to seconds field.
   * @param styles			Styles to be used by this TimePicker instance.
   * @param images			Images to be used by all nested Spinner widgets.
   * 
   */
  public TimePicker(Date date, DateTimeFormat amPmFormat, DateTimeFormat hoursFormat, DateTimeFormat minutesFormat, DateTimeFormat secondsFormat, ValueSpinnerResources styles, SpinnerResources images) {
    boolean hasPickerDate = (null != date);
    if (!hasPickerDate) {
    	date = new Date();
    }
    this.dateInMillis = date.getTime();
    HorizontalPanel horizontalPanel = new HorizontalPanel();
    horizontalPanel.setStylePrimaryName("gwt-TimePicker");
    
    if (hoursFormat != null) {
      TimeSpinner hoursSpinner = new TimeSpinner(date, hoursFormat, HOUR_IN_MILLIS, styles, images);
      timeSpinners.add(hoursSpinner);
      horizontalPanel.add(hoursSpinner);
    }
    
    if (minutesFormat != null) {
      TimeSpinner minutesSpinner = new TimeSpinner(date, minutesFormat, MINUTE_IN_MILLIS, styles, images);
      timeSpinners.add(minutesSpinner);
      horizontalPanel.add(minutesSpinner);
    }
    
    if (secondsFormat != null) {
      TimeSpinner secondsSpinner = new TimeSpinner(date, secondsFormat, SECOND_IN_MILLIS, styles, images);
      timeSpinners.add(secondsSpinner);
      horizontalPanel.add(secondsSpinner);
    }
    
    if (amPmFormat != null) {
        TimeSpinner amPmSpinner = new TimeSpinner(date, amPmFormat, HALF_DAY_IN_MS, styles, images);
        timeSpinners.add(amPmSpinner);
        horizontalPanel.add(amPmSpinner);
    }
    
    for (TimeSpinner timeSpinner:  timeSpinners) {
      for (TimeSpinner nestedSpinner:  timeSpinners) {
        if (nestedSpinner != timeSpinner) {
          timeSpinner.getSpinner().addSpinnerListener(nestedSpinner.getSpinnerListener());
        }
      }
      timeSpinner.getSpinner().addSpinnerListener(listener);
    }
    
    initWidget(horizontalPanel);
    
    if (!hasPickerDate) {
    	clearTime();
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  /**
   * @return the date specified by this {@link TimePicker}
   */
  public Date getDateTime() {
	if (!hasTime()) {
		return null;
	}
    return new Date((long) dateInMillis);
  }

  /**
   * @return Gets whether this widget is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param date	The date to be set. Only the date part will be set, the time part will not be affected.
   */
  public void setDate(Date date) {
    boolean hasPickerDate = (null != date);
	if (!hasPickerDate) {
		date = new Date();
	}
	
    // Only change the date part, leave time part untouched
    dateInMillis = (double) ((Math.floor(date.getTime() / DAY_IN_MS) + 1) * DAY_IN_MS) + dateInMillis % DAY_IN_MS;
    for (TimeSpinner spinner:  timeSpinners) {
      spinner.getSpinner().setValue(dateInMillis, false);
    }
    
    if (!hasPickerDate) {
    	clearTime();
    }
  }

  /**
   * @param date	The date to be set. Both date and time part will be set.
   */
  public void setDateTime(Date date) {
    boolean hasPickerDate = (null != date);
	if (!hasPickerDate) {
		date = new Date();
	}
		
    dateInMillis = date.getTime();
    for (TimeSpinner spinner:  timeSpinners) {
      spinner.getSpinner().setValue(dateInMillis, true);
    }
    
    if (!hasPickerDate) {
    	clearTime();
    }
  }

  /**
   * Sets whether this widget is enabled.
   * 
   * @param enabled	True to enable the widget, false to disable it.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    for (TimeSpinner spinner:  timeSpinners) {
      spinner.setEnabled(enabled);
    }
  }

  /**
   * Clears the values from the picker's spinners.
   */
  public void clearTime() {
	  for (TimeSpinner spinner:  timeSpinners) {
		  spinner.clearValue();
	  }
  }
  
  /**
   * Returns true if all the time picker spinners contain a value and
   * false otherwise.
   * 
   * @return
   */
  public boolean hasTime() {
	  // If any of the spinners are blank...
	  for (TimeSpinner spinner:  timeSpinners) {
		  if (!(spinner.hasValue())) {
			  // ...we have no value.  Return false.
			  return false;
		  }
	  }
	  
	  // If we get here, all the spinners have values!  Return true.
	  return true;
  }
}
