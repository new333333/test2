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
package org.kablink.teaming.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.mozilla.universalchardet.UniversalDetector;

public class FileCharsetDetectorUtil {

	public static String charDetect(File file) throws java.io.IOException {
		byte[] buf = new byte[4096];

		java.io.FileInputStream fis = new java.io.FileInputStream(file);
		try {
			UniversalDetector detector = new UniversalDetector(null);

			// Feed some data to the detector 
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// Notify the detector of the end of data
			detector.dataEnd();

			// Get the detected encoding name 
			String encoding = detector.getDetectedCharset();

			// reset the detector
			detector.reset();
			return encoding;
		} finally {
			fis.close();
		}
	}

	public static void convertEncoding(File infile, File outfile, String from,
			String to) throws IOException, UnsupportedEncodingException {
		// Set up byte streams.
		InputStream in = null;
		OutputStream out = null;
		Reader r = null;
		Writer w = null;
		
		try {
			if (infile != null)
				in = new FileInputStream(infile);
			else
				in = System.in;
			outfile.createNewFile();		
			if (outfile != null)
				out = new FileOutputStream(outfile);
			else
				out = System.out;
	
			// Use default encoding if no encoding is specified.
			if (from == null)
				from = System.getProperty("file.encoding");
			if (to == null)
				to = "Unicode";
	
			// Set up character streams.
			r = new BufferedReader(new InputStreamReader(in, from));
			w = new BufferedWriter(new OutputStreamWriter(out, to));
	
			// Copy characters from input to output. The InputStreamReader
			// converts from the input encoding to Unicode, and the
			// OutputStreamWriter writes the file out in Unicode.
			// Characters that cannot be represented in the output encoding are
			// output as '?'
			char[] buffer = new char[4096];
			int len;
			while ((len = r.read(buffer)) != -1)
				// Read a block of input.
				w.write(buffer, 0, len); // And write it out.
		}
		finally {
			try {
				if(r != null)
					r.close(); // Close the input. This should close 'in' as well.
				else if(in != null)
					in.close();
			}
			catch(IOException e) {}
			try {
				if(w != null)
					w.close(); // Flush and close output. This should close 'out' as well.
				else if(out != null)
					out.close();
			}
			catch(IOException e) {}
		}
	}
}