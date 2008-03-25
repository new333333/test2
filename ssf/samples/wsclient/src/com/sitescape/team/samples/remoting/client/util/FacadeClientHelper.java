/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.samples.remoting.client.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class FacadeClientHelper {

	public static void printXML(String xml) {
		System.out.println();
		try {
			Document document = DocumentHelper.parseText(xml);
			
			prettyPrintXML(document);
		} catch (DocumentException e) {
			System.out.println(e);
		}
		System.out.println();
	}
	
	public static String generateEntryInputDataAsXML(long binderId, String definitionId) {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<data>")
			.append("<property name=\"title\">WS test ")
			.append(new Date().getTime())
			.append("</property>")
			.append("<property name=\"description\">Added through Web Service</property>")
			.append("<property name=\"birthDate_date\">21</property>")
			.append("<property name=\"birthDate_month\">05</property>")
			.append("<property name=\"birthDate_year\">1992</property>")
			.append("<property name=\"birthDate_timezoneid\">GMT</property>")
			.append("<property name=\"colors\">white</property>")
			.append("<property name=\"colors\">blue</property>")
			.append("</data>");

		return sb.toString();
	}
	
	private static void prettyPrintXML(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}

	public static String readText(File file)
	{
		FileReader reader = null;
		String s = "";
		try {
			reader = new FileReader(file);
			s = readText(reader);
		} catch(IOException e) {
		} finally {
			if(reader != null) {try { reader.close(); } catch(Exception e) {}}
		}
		return s;
	}
	
	public static String readText(String filename)
	{
		FileReader reader = null;
		String s = "";
		try {
			reader = new FileReader(filename);
			s = readText(reader);
		} catch(IOException e) {
		} finally {
			if(reader != null) {try { reader.close(); } catch(Exception e) {}}
		}
		
		return s;
	}
	
	public static String readText(Reader reader)
	{
		StringBuffer buf = new StringBuffer();
		try {
			char in[] = new char[32768];
			int len;
			while((len = reader.read(in, 0, 32767)) > 0) {
				buf.append(in, 0, len);
			}
		} catch(IOException e) {
			System.err.println("Error reading file: " + e);
		}
		return buf.toString();
	}

	public static void main(String args[]) {
		try {
//		 turn validation on
			SAXReader reader = new SAXReader(true);
//		 request XML Schema validation
			reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			Document document = reader.read( args[0] );
			prettyPrintXML(document);
		} catch(Exception e) {
			System.err.println(e);
		}
	}
}
