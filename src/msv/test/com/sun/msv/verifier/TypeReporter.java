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

package com.sun.msv.verifier;

import javax.xml.parsers.SAXParserFactory;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.trex.typed.TypedElementPattern;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.typed.TypedTREXGrammarInterceptor;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * dumps RELAX label assigned to each element.
 * 
 * Example of type-assignment.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeReporter extends DefaultHandler
{
    
    public static void main( String[] args ) throws Exception {
        new TypeReporter().run(args);
    }
    
    private VerifierFilter filter;
    
    private void run( String[] args ) throws Exception {
        if( args.length!=3 ) {
            System.out.println("Usage: TypeReporter (relaxNS|relaxCore|trex|xsd) <schema> <XML instance>\n");
            return;
        }
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        Grammar grammar;
        
        if( args[0].equals("trex") ) {
            TREXGrammarReader reader = new TREXGrammarReader(
                new com.sun.msv.driver.textui.DebugController(false,false),
                factory,
                new TypedTREXGrammarInterceptor(),
                new ExpressionPool() );
            ((XMLFilter)reader).parse(args[1]);
            grammar = reader.getResult();
        } else {
            grammar = GrammarLoader.loadSchema( args[1],
                new com.sun.msv.driver.textui.DebugController(false,false),
                factory );
        }
        
        if( grammar==null ) {
            System.err.println("failed to load a grammar");
            return;
        }
        
        filter = new VerifierFilter( new REDocumentDeclaration(grammar),
            new com.sun.msv.driver.textui.ReportErrorHandler() );
        
        filter.setParent(factory.newSAXParser().getXMLReader());
        filter.setContentHandler(this);
        filter.parse( args[2] );
    }
    
    
    private int indent = 0;
    
    private void printIndent() {
        for( int i=0; i<indent; i++ )
            System.out.print("  ");
    }
    
    
    public void startElement( String namespaceUri, String localName, String qName, Attributes atts ) {
        printIndent();
        indent++;
        System.out.print("<"+qName+"> :");
        
        Object o = filter.getVerifier().getCurrentElementType();
        
        if( o instanceof ElementRule ) {
            // for RELAX
            ElementRule er = (ElementRule)o;
            if( er.getParent()==null )
                System.out.println("##inline");
            else
                System.out.println(er.getParent().name);
            return;
        }
        if( o instanceof TypedElementPattern ) {
            // for typed TREX
            System.out.println( ((TypedElementPattern)o).label );
            return;
        }
        if( o instanceof ElementPattern ) {
            System.out.println( ExpressionPrinter.printContentModel(
                ((ElementPattern)o).contentModel ) ); 
            return;
        }
        
        System.out.println("???");
    }
    
    public void endElement( String namespaceUri, String localName, String qName ) {
        Datatype[] types = filter.getVerifier().getLastCharacterType();
        if( types!=null ) {
            String r="";
            for( int i=0; i<types.length; i++ ) {
                if( types[i] instanceof XSDatatype )
                    r+=((XSDatatype)types[i]).displayName()+" ";
                else
                    r+=types[i]+" ";
            }
            
            printIndent();
            System.out.println("-- "+r+" --");
        }
        indent--;
        printIndent();
        System.out.println("</"+qName+">");
    }

}
