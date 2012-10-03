package org.kabling.teaming.install.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface AppResource extends Messages
{
	String userNameColon();

	String passwordColon();

	String login();

	String configuration();

	String logout();

	String yes();

	String no();

	String close();

	String cancel();

	String ok();
	
	String next();
	
	String previous();
	
	String finish();

	String environment();

	String clustering();

	String network();

	String lucene();

	String pleaseWait();

	String validatingCredentials();
	
	String creatingDatabase();

	String updatingDatabase();

	String reconfiguringServer();

	String startingServer();

	String allFieldsRequired();

	String wizDbPageTitleDesc();

	String database();
	
	String dbTypeColon();

	String hostNameColon();

	String portColon();

	String dbAdminColon();

	String dbAdminPasswordColon();

	String unableToConnectToDbServer();

	String dbNameColon();
	
	String wizLucenePageTitleDesc();
	
	String luceneServerAddressColon();
	
	String rmiPortColon();
	
	String validatingLuceneServer();
	
	String unableToConnectLuceneServer();
}