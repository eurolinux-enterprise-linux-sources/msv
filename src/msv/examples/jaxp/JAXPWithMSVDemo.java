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

package jaxp;

import java.io.File;
import javax.xml.parsers.*;
import com.sun.msv.verifier.jaxp.SAXParserFactoryImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * Uses JAXP implementation of MSV to plug validation capability
 * into the existing application.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class JAXPWithMSVDemo
{
    public static void main( String[] args ) throws Exception {
        
        if( args.length<2 ) {
            System.out.println("JAXPWithMSVDemo <schema file> <instance files> ...");
            return;
        }

        // create SAXParserFactory that performs validation by the specified schema
        // this method will throw an exception if it fails to parse the document.
        SAXParserFactory factory = new SAXParserFactoryImpl(new File(args[0]));
        
        
        // once the parser factory is created, just do as you always do.
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        
        for( int i=1; i<args.length; i++ ) {
            // validation errors will be reported just like any other errors.
            final String fileName = args[i];
            parser.parse( new File(fileName), new DefaultHandler() {
                    
                boolean isValid = true;
                    
                public void error( SAXParseException e ) throws SAXException {
                    System.out.println( e );
                    isValid = false;
                }
                public void fatalError( SAXParseException e ) throws SAXException {
                    System.out.println( e );
                    isValid = false;
                }
                public void endDocument() {
                    if(isValid)
                        // successfully parsed without any error.
                        System.out.println(fileName+" is valid");
                    else
                        System.out.println(fileName+" is NOT valid");
                }
            });
        }
    }
}
