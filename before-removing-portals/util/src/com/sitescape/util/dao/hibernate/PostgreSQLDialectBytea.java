package com.sitescape.util.dao.hibernate;
import java.sql.Types;

import org.hibernate.dialect.PostgreSQLDialect;
public class PostgreSQLDialectBytea extends PostgreSQLDialect  {
	public PostgreSQLDialectBytea() {
		super();
		//oid require a transaction which breaks lazy loading
		registerColumnType( Types.BLOB, "bytea" );
	}
}
