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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: David
 * Date: 11/20/13
 * Time: 9:40 PM
 */
@XmlRootElement(name="selected_days")
public class SelectedDays {
    private Boolean sun;
    private Boolean mon;
    private Boolean tue;
    private Boolean wed;
    private Boolean thu;
    private Boolean fri;
    private Boolean sat;

    public Boolean getSun() {
        return sun==null ? Boolean.FALSE : sun;
    }

    public void setSun(Boolean sun) {
        this.sun = sun;
    }

    public Boolean getMon() {
        return mon==null ? Boolean.FALSE : mon;
    }

    public void setMon(Boolean mon) {
        this.mon = mon;
    }

    public Boolean getTue() {
        return tue==null ? Boolean.FALSE : tue;
    }

    public void setTue(Boolean tue) {
        this.tue = tue;
    }

    public Boolean getWed() {
        return wed==null ? Boolean.FALSE : wed;
    }

    public void setWed(Boolean wed) {
        this.wed = wed;
    }

    public Boolean getThu() {
        return thu==null ? Boolean.FALSE : thu;
    }

    public void setThu(Boolean thu) {
        this.thu = thu;
    }

    public Boolean getFri() {
        return fri==null ? Boolean.FALSE : fri;
    }

    public void setFri(Boolean fri) {
        this.fri = fri;
    }

    public Boolean getSat() {
        return sat==null ? Boolean.FALSE : sat;
    }

    public void setSat(Boolean sat) {
        this.sat = sat;
    }
}
