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
package org.kablink.teaming.ical.util;

public enum PercentComplete {

	c000, c010, c020, c030, c040, c050, c060, c070, c080, c090, c100;
	
	public net.fortuna.ical4j.model.property.PercentComplete toIcalPercentComplete() {
		if (this.equals(c000)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(0);
		} else if (this.equals(c010)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(10);
		} else if (this.equals(c020)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(20);
		} else if (this.equals(c030)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(30);
		} else if (this.equals(c040)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(40);
		} else if (this.equals(c050)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(50);
		} else if (this.equals(c060)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(60);
		} else if (this.equals(c070)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(70);
		} else if (this.equals(c080)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(80);
		} else if (this.equals(c090)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(90);
		} else if (this.equals(c100)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(100);
		}
		return null;
	}
	
	public static PercentComplete fromIcalPercentComplete(net.fortuna.ical4j.model.property.PercentComplete percentComplete) {
		if (percentComplete == null) {
			return null;
		}
		
		if (percentComplete.getPercentage() == 0) {
			return c000;
		} else if (percentComplete.getPercentage() == 10) {
			return c010;
		} else if (percentComplete.getPercentage() == 20) {
			return c020;
		} else if (percentComplete.getPercentage() == 30) {
			return c030;
		} else if (percentComplete.getPercentage() == 40) {
			return c040;
		} else if (percentComplete.getPercentage() == 50) {
			return c050;
		} else if (percentComplete.getPercentage() == 60) {
			return c060;
		} else if (percentComplete.getPercentage() == 70) {
			return c070;
		} else if (percentComplete.getPercentage() == 80) {
			return c080;
		} else if (percentComplete.getPercentage() == 90) {
			return c090;
		} else if (percentComplete.getPercentage() == 100) {
			return c100;
		}
		
		return null;
	}
	
}
