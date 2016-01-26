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
package org.kablink.teaming.dao;

import java.util.Map;

import org.kablink.teaming.util.encrypt.HibernateEncryptor;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;

/**
 * @author Jong
 *
 */
public interface JdbcDao {

	/**
	 * Migrate those items in the database (most likely passwords) that were previously
	 * encrypted by Hibernate through Hibernate/Jasypt/Spring integration.
	 * 
	 * The reason for such migration is to use a new and more secure encryption key. 
	 * The value was encrypted using old key and now the value should be re-encrypted
	 * using new key. The algorithm used for encryption may or may not be the same.
	 * 
	 * @param hibernateEncryptorWithOldKey
	 * @param hibernateEncryptorWithNewKey
	 * @return
	 */
	public Map<String,Integer> migrateHibernateEncryptedItems(HibernateEncryptor hibernateEncryptorWithOldKey, HibernateEncryptor hibernateEncryptorWithNewKey);
	
	/**
	 * Migrate user passwords.
	 * 
	 * @param oldkey_encryptor_first_gen
	 * @param oldkey_encryptor_second_gen
	 * @param newkey_encryptor_first_gen
	 * @param newkey_encryptor_second_gen
	 * @return
	 */
	public Map<String,Integer> migrateUserPasswords(ExtendedPBEStringEncryptor oldkey_encryptor_first_gen,
			ExtendedPBEStringEncryptor oldkey_encryptor_second_gen,
			ExtendedPBEStringEncryptor newkey_encryptor_first_gen,
			ExtendedPBEStringEncryptor newkey_encryptor_second_gen);
}
