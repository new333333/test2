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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;

import com.eemi.gwt.tour.client.GwtTour;
import com.eemi.gwt.tour.client.Tour;
import com.eemi.gwt.tour.client.TourStep;

/**
 * Encapsulates a Tour to supply localization, ...
 *  
 * @author drfoster@novell.com
 */
public class VibeTour extends Tour {
	/**
	 * Constructor method.
	 * 
	 * @param id
	 */
	public VibeTour(String id) {
		// Initialize the super class...
		super(id);
	
		// ...initialize the tour's localized strings...
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		setCloseTooltipTextText(messages.tourCloseAlt());
		setDoneBtnText(         messages.tourDone()    );
		setNextBtnText(         messages.tourNext()    );
		setPrevBtnText(         messages.tourPrev()    );
		setSkiptBtnText(        messages.tourSkip()    );

		// ...and initialize everything else.
		setShowPrevButton(true);
	}

	/**
	 * Sets the tour's steps ensuring that the last step will show 
	 * 'Done' instead of 'Next'.
	 * 
	 * @param steps
	 * 
	 * Overrides the Tour.setSteps() method.
	 */
	@Override
	public void setSteps(List<TourStep> steps) {
		int count = ((null == steps) ? 0 : steps.size());
		int iLast = (count - 1);
		for (int i = 0; i < count; i += 1) {
			steps.get(i).setShowNextButton(i != iLast);
		}
		super.setSteps(steps);
	}
	
	/**
	 * Starts this tour from the beginning.
	 */
	public void start() {
		GwtTour.startTour(this, 0);
	}
	
	/**
	 * Stops this tour if it's running.
	 */
	public void stop() {
		GwtTour.endTour();
	}
}
