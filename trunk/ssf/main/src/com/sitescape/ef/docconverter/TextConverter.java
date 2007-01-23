package com.sitescape.ef.docconverter;

import java.io.File;

public interface TextConverter {

	public String convertToText(File inputFile, long timeout) throws Exception;
}
