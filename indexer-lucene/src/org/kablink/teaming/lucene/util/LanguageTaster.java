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
package org.kablink.teaming.lucene.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.Character.UnicodeBlock;

public class LanguageTaster {
	
	public static String DEFAULT = "DEFAULT";
	public static String CJK = "CJK";
	public static String ARABIC = "ARABIC";
	public static String HEBREW = "HEBREW";

	// read thru the char char buffer, figure out 
	// which unicode codeblock the chars fall into, and 
	// tally them up to see which analyzer to use.
	public static String taste(char[] cbuf) {

		try {
			int buffCount = 0;			
			int arabicCount = 0;
			int hebrewCount = 0;
			int cjkCount = 0;
			int defCount = 0;
			
			int bt = cbuf.length;
			while (buffCount < bt) {
				char c = cbuf[buffCount];
				UnicodeBlock cu = UnicodeBlock.of(c);
				if (cu == UnicodeBlock.ARABIC || 
						cu == UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
						cu == UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) {
					arabicCount++;
				} else if (cu == UnicodeBlock.HEBREW) {
					hebrewCount++;
				} else if (cu == UnicodeBlock.CJK_COMPATIBILITY || 
						cu == UnicodeBlock.CJK_COMPATIBILITY_FORMS ||
						cu == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || 
						cu == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT ||
						cu == UnicodeBlock.CJK_RADICALS_SUPPLEMENT ||
						cu == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
						cu == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
						cu == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
						cu == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
						cu == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO || 
						cu == UnicodeBlock.HANGUL_JAMO || 
						cu == UnicodeBlock.HANGUL_SYLLABLES ||
						cu == UnicodeBlock.HIRAGANA ||
						cu == UnicodeBlock.KATAKANA || 
						cu == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS ||
						cu == UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS ||
						cu == UnicodeBlock.YIJING_HEXAGRAM_SYMBOLS) {
					cjkCount++;
				} else {
					defCount++;
				}
				buffCount++;
			}

			if (cjkCount > 0) {
				return CJK;
			} else if (hebrewCount > 0) {
				return HEBREW;
			} else if (arabicCount > 0) {
				return ARABIC;
			} else {
				return DEFAULT;
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
		return "DEFAULT";
	}
	
	
	// Here for testing purposes only
	public static void main(String[] args) {

		InputStream fis = null;
		char cbuf[] = new char[1024];

		try {
			fis = new FileInputStream("/languages/japan.txt");
			Reader rd = new InputStreamReader(fis, "UTF-8");
			BufferedReader buff = new BufferedReader(rd);
			
			int bt = buff.read(cbuf,  0, 1024);
			if (bt == -1) {
				System.out.println("Uh Oh");
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		System.out.println(taste(cbuf));
	}
}
