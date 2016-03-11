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
package org.kablink.teaming.smtp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

public class SendMail {

	public static void main(String[] args) {
		if(args.length < 1 || args.length > 2) {
			System.err.println("Usage: SendMail filename <port>");
			System.err.println("  Reads an RFC 2822 formatted (including headers, but NOT dot-encoded) file from file 'filename'");
			System.err.println("  and sends it to the SMTP server on localhost at port number 'port' (defaults to 2525).");
			System.err.println("  SMTP envelope info (MAIL FROM and RCPT TO) taken from To: and From: headers in file");
			System.exit(0);
		}
		
		File input = new File(args[0]);
		LineNumberReader reader = null;
		int port = 2525;
		String line = null;
		String sender = null;
		List<String> recipients = new LinkedList<String>();
		try {
			reader = new LineNumberReader(new FileReader(input));
			reader.setLineNumber(1);
			reader.mark(9196);
			boolean inHeader = true;
			while(inHeader && (line = reader.readLine()) != null) {
				if(line.length() == 0) {
					inHeader = false;
				}
				if(inHeader && line.startsWith("To:")) {
					recipients.add(line.substring(3).trim());
				} else if(inHeader && line.startsWith("From:")) {
					sender = line.substring(5).trim();
				}
			}
			if(recipients.size() == 0) {
				System.out.println("No 'To:' header found in file.  Cannot send mail.");
				System.exit(1);
			}
			if(sender == null) {
				System.out.println("No 'From:' header found in file.  Cannot send mail.");
				System.exit(1);
			}
			reader.reset();
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file: " + args[0]);
		} catch(IOException e) {
			System.err.println("Error reading from file " + args[0] + ": " + e.getMessage());
			if(reader != null) {
				System.err.println("  at or after line number " + reader.getLineNumber());
			}
		}
		SMTPClient client = null;
		try {
			client = new SMTPClient();
			client.connect("localhost", port);
			int reply = client.getReplyCode();
			if(!SMTPReply.isPositiveCompletion(reply)) {
				System.err.println("Unable to connect to localhost at port " + port + ": " + client.getReplyString());
				client.disconnect();
				System.exit(1);
			}
			if(!client.login()) {
				System.err.println("Unable to login to localhost at port " + port + ": " + client.getReplyString());
				client.disconnect();
				System.exit(1);
			}
			if(!client.setSender(sender)) {
				System.err.println("Server rejected sender " + sender + ": " + client.getReplyString());
				client.disconnect();
				System.exit(1);
			}
			for(String recipient : recipients) {
				if(!client.addRecipient(recipient)) {
					System.err.println("Server rejected recipient " + recipient + ": " + client.getReplyString());
					client.disconnect();
					System.exit(1);
				}
			}
			Writer writer = client.sendMessageData();
			while((line = reader.readLine()) != null) {
				writer.write(line);
				writer.write('\n');
			}
			writer.close();
			if(!client.completePendingCommand()) {
				System.err.println("Error sending message: " + client.getReplyString());
				client.disconnect();
				System.exit(1);
			}
			client.logout();
		} catch(IOException e) {
			System.err.println("Error communicating with localhost on port " + port + ": " + e.getMessage());
			if(client.isConnected()) {
				try {
					client.disconnect();
				} catch(IOException f) {
					// Do nothing
				}
			}
		}
	}
}
