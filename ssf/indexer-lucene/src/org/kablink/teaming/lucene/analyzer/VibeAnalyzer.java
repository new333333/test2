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
package org.kablink.teaming.lucene.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.util.Version;

public abstract class VibeAnalyzer extends Analyzer {
	protected String stemmerName;
	protected Set<String> stopSet;
	protected boolean foldToAscii = false;
	protected boolean decomposeToken = false;
	protected boolean useStandard = true;

	protected static final Version VERSION = Version.LUCENE_34;

	public VibeAnalyzer() {
		init();
	}
	
	public VibeAnalyzer(String stemmerName) {
		this.stemmerName = stemmerName;
		init();
	}

	public VibeAnalyzer(boolean foldToAscii) {
		this.foldToAscii = foldToAscii;
		init();
	}
	
	public VibeAnalyzer(Set stopWords,
			String stemmerName, boolean foldToAscii) {
		stopSet = stopWords;
		this.stemmerName = stemmerName;
		this.foldToAscii = foldToAscii;
		init();
	}

	public VibeAnalyzer(Set stopWords,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) {
		stopSet = stopWords;
		this.stemmerName = stemmerName;
		this.foldToAscii = foldToAscii;
		this.decomposeToken = decomposeToken;
		this.useStandard = useStandard;
		init();
	}

	public VibeAnalyzer(File stopwords,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) throws IOException {
		stopSet = WordlistLoader.getWordSet(stopwords);
		this.stemmerName = stemmerName;
		this.foldToAscii = foldToAscii;
		this.decomposeToken = decomposeToken;
		this.useStandard = useStandard;
		init();
	}

	public VibeAnalyzer(Reader stopwords,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) throws IOException {
		stopSet = WordlistLoader.getWordSet(stopwords);
		this.stemmerName = stemmerName;
		this.foldToAscii = foldToAscii;
		this.decomposeToken = decomposeToken;
		this.useStandard = useStandard;
		init();
	}

	public String getStemmerName() {
		return stemmerName;
	}

	public void setStemmerName(String stemmerName) {
		this.stemmerName = stemmerName;
	}

	public Set<String> getStopSet() {
		return stopSet;
	}

	public void setStopSet(Set<String> stopSet) {
		this.stopSet = stopSet;
	}

	public boolean isFoldToAscii() {
		return foldToAscii;
	}

	public void setFoldToAscii(boolean foldToAscii) {
		this.foldToAscii = foldToAscii;
	}

	public boolean isDecomposeToken() {
		return decomposeToken;
	}

	public void setDecomposeToken(boolean decomposeToken) {
		this.decomposeToken = decomposeToken;
	}

	public boolean isUseStandard() {
		return useStandard;
	}

	public void setUseStandard(boolean useStandard) {
		this.useStandard = useStandard;
	}

	private void init() {
	}

	protected class SavedStreams {
		Tokenizer source;
		TokenStream result;
	};

	protected static Reader openStopWordFile(File stopWordFile, String stopWordFileCharset) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(stopWordFile), stopWordFileCharset));
	}

}
