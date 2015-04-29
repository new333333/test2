/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.liquibase.change.custom;

import java.sql.Connection;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * @author Jong
 *
 */
public class MigrateMirroredFoldersChange implements CustomTaskChange {
	
	private String dbType;
	private String batchSize;
	private String transactionSize;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	public String getTransactionSize() {
		return transactionSize;
	}

	public void setTransactionSize(String transactionSize) {
		this.transactionSize = transactionSize;
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		// I don't see where this message is used, but ...
		return "Migration of net folder config information completed";
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#setFileOpener(liquibase.resource.ResourceAccessor)
	 */
	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		// This class doesn't need resource accessor
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#setUp()
	 */
	@Override
	public void setUp() throws SetupException {
		// Nothing to set up
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomChange#validate(liquibase.database.Database)
	 */
	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	/* (non-Javadoc)
	 * @see liquibase.change.custom.CustomTaskChange#execute(liquibase.database.Database)
	 */
	@Override
	public void execute(Database database) throws CustomChangeException {
        JdbcConnection databaseConnection = (JdbcConnection) database.getConnection();
        Connection conn = databaseConnection.getWrappedConnection();
        
        System.out.println("Migrating mirrored folders as needed. This may take a moment or two. See migrate-mirrored-folders.log for details.");
    
        try {
            MigrateMirroredFolders mnf = new MigrateMirroredFolders(conn, dbType, Integer.parseInt(batchSize), Integer.parseInt(transactionSize), "migrate-mirrored-folders.log");
            
			mnf.migrate();
		} catch (Exception e) {
			throw new CustomChangeException(e);
		}
        
        // Do NOT close the connection here, as it is still being used by the Liquibase!
	}

}
