/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.writer.trex;

import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import java.io.PrintWriter;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;

/**
 * converts any supported languages into the equivalent TREX grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
	public static void main( String[] args ) throws Exception {
		
		if( args.length!=1 ) {
			System.out.println( localize(MSG_USAGE) );
			return;
		}
		
		// use Xerces. Since we are using Xerces to serialize XML,
		// it is a good idea to use Xerces for parsing, too.
		SAXParserFactoryImpl factory = new SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		
		// load a grammar.
		Grammar g = GrammarLoader.loadSchema(
			args[0],
			new DebugController( false,false, System.err ),
			factory );
		
		if( g==null ) {
			System.err.println(localize(MSG_GRAMMAR_ERROR));
			return;
		}
		
		TREXWriter writer = new TREXWriter();
		// use XMLSerializer of Apache to serialize SAX event into plain text.
		// OutputFormat specifies "pretty printing".
		writer.setDocumentHandler(
			new XMLSerializer( new PrintWriter(System.out),
			new OutputFormat("xml",null,true) ) );
		// visit TREXGrammar and generate its XML representation.
		writer.write( g );
	}
	
	
	public static String localize( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.writer.relaxng.Messages").getString(propertyName);
	    return java.text.MessageFormat.format(format, args );
	}
	public static String localize( String prop ) {
		return localize(prop,null);
	}
	public static String localize( String prop, Object arg1 ) {
		return localize(prop,new Object[]{arg1});
	}
	public static String localize( String prop, Object arg1, Object arg2 ) {
		return localize(prop,new Object[]{arg1,arg2});
	}
	
	private static final String MSG_USAGE = // arg:0
		"Driver.Usage";
	private static final String MSG_GRAMMAR_ERROR = // arg:0
		"Driver.GrammarError";
}
