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
		sb.append("<entry>")
			.append("<attribute name=\"title\">WS test ")
			.append(new Date().getTime())
			.append("</attribute>")
			.append("<attribute name=\"description\">Added through Web Service</attribute>")
			.append("<attribute name=\"birthDate_date\">21</attribute>")
			.append("<attribute name=\"birthDate_month\">05</attribute>")
			.append("<attribute name=\"birthDate_year\">1992</attribute>")
			.append("<attribute name=\"birthDate_timezoneid\">GMT</attribute>")
			.append("<attribute name=\"colors\">white</attribute>")
			.append("<attribute name=\"colors\">blue</attribute>")
			.append("</entry>");

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
