/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package org.kablink.teaming.rest.v1.model.admin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A schedule.
 */
@XmlRootElement(name="schedule")
public class Schedule {
    public enum DayFrequency {
        daily,
        selected_days
    }

    private Boolean enabled;
    private String dayFrequency;
    private SelectedDays selectedDays;
    private Time at;
    private Time every;

    /**
     * Indicates whether or not this schedule is enabled.
     */
    public Boolean getEnabled() {
        return enabled==null ? Boolean.TRUE : enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Can be "daily" or "selected_days".  If "selected_days", the "selected_days" field should also be set.
     */
    @XmlElement(name="when")
    public String getDayFrequency() {
        return dayFrequency;
    }

    public void setDayFrequency(String dayFrequency) {
        this.dayFrequency = dayFrequency;
    }

    /**
     * If "when" is "selected_days", this defines on which days the schedule is active.
     */
    @XmlElement(name="selected_days")
    public SelectedDays getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(SelectedDays selectedDays) {
        this.selectedDays = selectedDays;
    }

    /**
     * For once-a-day schedules, this is the time (GMT) at which the schedule should activate.
     */
    public Time getAt() {
        return at;
    }

    public void setAt(Time at) {
        this.at = at;
    }

    /**
     * For repeating schedules, this is the interval with which the schedule activates.
     *
     * <p>Valid time values are 0:15, 0:30, 0:45, 1:00, 2:00, 3:00, 4:00, 6:00, 8:00 and 12:00</p>
     */
    public Time getEvery() {
        return every;
    }

    public void setEvery(Time every) {
        this.every = every;
    }


}
