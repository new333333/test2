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
package org.kablink.teaming.web.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Random;

import org.hibernate.StaleObjectStateException;

public abstract class SAbstractControllerRetry extends SAbstractController {
	protected abstract void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception;

	protected void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
		throws Exception {
		//wrap a return loop around the request. Retry on Optimistic lock exceptions
		//since the user can hit browser buttons faster than we can process them,
		//we get optimistic lock exceptions.  Try to reduce this by retrying.

		int retryCount =0;
		Random random=null;
		while (true) {
			try {
				handleActionRequestWithRetry(request, response);
				return;
			} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
				++retryCount;
				if (random == null) random = new Random();
				Thread.sleep(random.nextInt(20));
				if (retryCount >= 5) throw ol; //give up
			} catch (StaleObjectStateException os) {
				++retryCount;
				if (random == null) random = new Random();
				Thread.sleep(random.nextInt(20));
				if (retryCount >= 5) throw os; //give up
			}
		}
	}

}
