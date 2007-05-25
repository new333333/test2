/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import javax.transaction.SystemException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;

/**
 *
 * @author Jong Kim
 */
public class JtaTransactionManager extends org.springframework.transaction.jta.JtaTransactionManager {
    
	protected void applyIsolationLevel(int isolationLevel)
            throws InvalidIsolationLevelException, SystemException {
        if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
            isolationLevel = TransactionDefinition.ISOLATION_DEFAULT;
        }
        super.applyIsolationLevel(isolationLevel);
    }
}
