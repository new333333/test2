package com.sitescape.ef.lucene.server;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:
 * @author
 * @version 1.0
 */

import java.io.Reader;
import java.io.IOException;
import java.util.Stack;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.Token;


public class MixedCaseAnalyzer extends StandardAnalyzer {

    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new UpperLowerCaseFilter(result);
        return result;
    }

    public static void printTokens (TokenStream stream) {
        for(;;)	{
            try {
                org.apache.lucene.analysis.Token token = stream.next();
                if (token == null) {
                    break;
                }
                System.out.println("[" + token.termText() + " , " + token.getPositionIncrement() + "]");
            } catch (IOException e) {
                System.out.println("token error:" + e);
            }
        }
    }


    public final class UpperLowerCaseFilter extends TokenFilter {
        private Stack currentTokenAliases;

        public UpperLowerCaseFilter(TokenStream in) {
            super(in);
            currentTokenAliases = new Stack();
            input = in;
        }

        public Token next() throws IOException {
            if(currentTokenAliases.size() > 0) {
                return (Token)currentTokenAliases.pop();
            }

            Token nextToken = input.next();
            if (nextToken == null) {
                return null;
            } else {
                nextToken.setPositionIncrement(1);
                addAliasesToStack(nextToken, currentTokenAliases);
            }

            return nextToken;
        }

        private void addAliasesToStack(Token token, Stack aliasStack) {
            // Add lowercase version of word to stack, but only if at least one
            // uppercase character exists.
            if(token == null) return;
            String tokenString = token.termText();
            for (int i=0; i<tokenString.length(); i++) {
                if (!Character.isLowerCase(tokenString.charAt(i))) {
                    String nextAlias = tokenString.toLowerCase();
                    Token nextTokenAlias = new Token(nextAlias, 0, nextAlias.length());
                    nextTokenAlias.setPositionIncrement(0);
                    aliasStack.push(nextTokenAlias);
                    break;
                }
            }
        }
    }
}

