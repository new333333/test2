package com.sitescape.ef.docconverter.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.docconverter.TextConverter;

public class TextOpenOfficeConverter implements TextConverter {

	protected Log logger = LogFactory.getLog(getClass());

	public String convertToText(File inputFile, long timeout) throws Exception {
		logger.warn("No text conversion for " + inputFile);
		return ""; // No converion for now
	}

}
