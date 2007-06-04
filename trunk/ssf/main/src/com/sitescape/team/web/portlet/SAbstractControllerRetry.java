package com.sitescape.team.web.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.hibernate.StaleObjectStateException;

public abstract class SAbstractControllerRetry extends SAbstractController {
	protected abstract void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception;

	protected void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
		throws Exception {
		//wrap a return loop around the request. Retry on Optimistic lock exceptions
		//since the user can hit browser buttons faster than we can process them,
		//we get optimistic lock exceptions.  Try to reduce this by retrying.
		int retryCount =0;
		while (true) {
			try {
				handleActionRequestWithRetry(request, response);
				return;
			} catch (org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException ol) {
				++retryCount;
				if (retryCount >= 5) throw ol; //give up
			} catch (StaleObjectStateException os) {
				++retryCount;
				if (retryCount >= 5) throw os; //give up
			}
		}
	}

}
