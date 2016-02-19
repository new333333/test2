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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.util.Html;

/**
 * ?
 * 
 * @author Peter Hurley
 */
public class TextToHtml {
	protected static Log logger = LogFactory.getLog(NLT.class);
	
	private static final String PATTERN_LINE       = "([^\\n]*)\\n(.*)";
	private static final String PATTERN_INDENT     = "((?:\\s*|\\s*[>]*\\s+))([^\\s]?.*)";
	private static final String PATTERN_LIST_LINE  = "^\\s*(?:o |- |\\* |[0-9]+\\)|[0-9]+\\.|[0-9]+ |>>).*$";
	private static final String PATTERN_LIST_LINE2 = "^\\s*[^\\s]+:.*$";
	private static final String PATTERN_URL        = "(?:^|[^\"'])\\s*(https*://[^ ]+)";
	private static final String PATTERN_URL2       = "(https*://[^ ]+)";
	private static final int    INDENTATION_FACTOR = 4;  // Translation between spaces and "px". i.e., 4px = 1 space.
	
	private Boolean			breakOnLines = false;
	private Boolean			escapeHtml   = true;
	private Boolean			stripHtml    = false;
	private List<Para>		paraList     = new ArrayList<Para>();
	private List<String>	lines        = new ArrayList<String>();

	/**
	 * ?
	 * 
	 * @param inputText
	 */
	public void parseText(String inputText) {
		// Break the text into a list of lines.
		String s = inputText;
		
		// Make sure there aren't any "<" or ">" chars that might turn
		// into bogus HTML.
		if (escapeHtml) s = StringEscapeUtils.escapeHtml(s);
		if (stripHtml)  s = Html.stripHtml(              s);
		
		Pattern pLines = Pattern.compile(PATTERN_LINE, Pattern.DOTALL);
		Matcher mLines = pLines.matcher(s);
		while (mLines.find()) {
			String line = mLines.group(1).replaceAll("\\r", "");
			lines.add(line);
			s = mLines.group(2);
			mLines = pLines.matcher(s);
		}
		if (!s.equals("")) lines.add(s);
		
		//Split the lines into paragraphs by looking for blank lines
		Para p = new Para();
		paraList.add(p);
		for (String line : lines) {
			p.addLine(line);
			if (line.equals("")) {
				p = new Para();
				paraList.add(p);
			}
		}
		
		//Process each paragraph looking for indented blocks
		for (int i = 0; i < paraList.size();) {
			Para nextPara = paraList.get(i);
			List<Para> pList = scanForBlocks(nextPara);
			if (pList.size() > 1) {
				//This para was split into more than one 
				//  leave i un-incremented so these paras are checked
				paraList.remove(i);
				paraList.addAll(i, pList);
			} else {
				i++;
			}
		}
		
		//Process each para again to fix up URLs
		for (int i = 0; i < paraList.size(); i++) {
			Para nextPara = paraList.get(i);
			scanForUrls(nextPara);
		}
				
	}
	
	/**
	 * Escape HTML in the text.
	 * 
	 * @param escapeHtml
	 */
	public void setEscapeHtml(Boolean escapeHtml) {
		this.escapeHtml = escapeHtml;
	}
	
	/**
	 * Strip HTML from the text.
	 * 
	 * @param stripHtml
	 */
	public void setStripHtml(Boolean stripHtml) {
		this.stripHtml = stripHtml;
	}
	
	/**
	 * Break on lines.
	 * 
	 * @param breakOnLines
	 */
	public void setBreakOnLines(Boolean breakOnLines) {
		this.breakOnLines = breakOnLines;
	}
	
	/**
	 * Return the HTML string.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Para p : paraList) {
			buf.append(p.toString());
		}
		return buf.toString();
	}
	
	/*
	 * Look at a paragraph to see if is a block (or multiple blocks) of
	 * lines.  Capture indentation level.  Strip leading spaces
	 * (or >>>).
	 */
	protected List<Para> scanForBlocks(Para p) {
		List<Para> results = new ArrayList<Para>();
		List<String> lines = p.getLines();
		if (!lines.isEmpty()) {
			Para firstPara = new Para();
			results.add(firstPara);
			Pattern pIndent = Pattern.compile(PATTERN_INDENT);
			Matcher mIndent = pIndent.matcher(lines.get(0));
			String indentationModel = "";
			if (mIndent.find()) indentationModel = mIndent.group(1);
			for (int i = 0; i < lines.size(); i++) {
				mIndent = pIndent.matcher(lines.get(i));
				String indentationTest = "";
				if (mIndent.find()) indentationTest = mIndent.group(1);
				if (!indentationTest.equals(indentationModel)) {
					//The indentation is different; this is a break in the para; split it into two
					Para secondPara = new Para(lines.subList(i, lines.size()));
					results.add(secondPara);
					return results;
				}
				firstPara.setIndentation(indentationModel.length());
				firstPara.addLine(mIndent.group(2));
			}
			//All lines were the same format; just return one para
		} else {
			//Empty para, return the original para
			results.add(p);
		}
		return results;
	}
	
	/*
	 * Replace all http:// links with <a>...</a>
	 */
	protected void scanForUrls(Para p) {
		List<String> lines = p.getLines();
		if (!lines.isEmpty()) {
			Pattern pUrl = Pattern.compile(PATTERN_URL);
			Pattern pUrl2 = Pattern.compile(PATTERN_URL2);
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				StringBuffer buf = new StringBuffer(line);
				Matcher matcher = pUrl.matcher(buf.toString());
				int loopDetector;
				if (matcher.find()) {
					loopDetector = 0;
					buf = new StringBuffer();
					do {
						if (loopDetector++ > 2000) {
							logger.error("Error processing urls: " + line);
							return;
						}
			    		String url = matcher.group();

						Matcher httpMatcher = pUrl2.matcher(url);
						if (httpMatcher.find()) {
							String href = "<a href=\"" + httpMatcher.group() + "\">" + httpMatcher.group() + "</a>";
							url = httpMatcher.replaceFirst(href);
						}

				    	matcher.appendReplacement(buf, Matcher.quoteReplacement(url));
			    	} while (matcher.find());
					matcher.appendTail(buf);

			    }
		    	lines.set(i, buf.toString());
			}
		}
	}
	
	/*
	 * A paragraph is a set of lines. It may be a block or a list. It
	 * is put within <div>...</div>
	 */
	protected class Para {
		private int				indentation;
		private List<String>	lines;
		
		public Para() {
			lines = new ArrayList<String>();
		}
		
		public Para(List<String> lineList) {
			lines = lineList;
		}
		
		public void addLine(String line) {
			lines.add(line);
		}
		
		public List<String> getLines() {
			return lines;
		}
		
		public void setIndentation(int i) {
			indentation = i;
		}
		
		@Override
		public String toString() {
			boolean firstLine = true;
			StringBuffer buf = new StringBuffer();
			buf.append("<div style=\"padding:0px 0px 10px "+getIndentationPx(indentation)+";\">\n");
			for (String line : lines) {
				if (!buf.toString().equals("")) buf.append("\n");
				if (!firstLine) {
					//See if this is a list line
					//  If the line also starts with "xxx:" then add a break. (This is primarially for mail headers)
					if (line.matches(PATTERN_LIST_LINE) || line.matches(PATTERN_LIST_LINE2) || breakOnLines) {
						buf.append("\n<br/>\n");
					}
				}
				buf.append(line);
				firstLine = false;
			}
			buf.append("</div>\n");
			return buf.toString();
		}
	}

	/*
	 * Get the indentation
	 */
	private String getIndentationPx(int i) {
		return String.valueOf(i * INDENTATION_FACTOR) + "px";
	}
}
