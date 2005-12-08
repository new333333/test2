package com.sitescape.ef.util;

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
