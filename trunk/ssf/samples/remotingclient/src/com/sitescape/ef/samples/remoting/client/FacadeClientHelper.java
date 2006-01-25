package com.sitescape.ef.samples.remoting.client;

import java.io.IOException;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
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

}
