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

package xpathloc;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Demonstrates how to use {@link xpathloc.XPathLocationTracker}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * 
 * @see
 *     XPathLocationTracker
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if( args.length<2 ) {
            System.out.println("Main <schema file> <instance file 1> <instance file 2> ...");
            return;
        };
        
        // see JARVDemo for more about how you "properly" use JARV.
        
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        Verifier verifier = factory.newVerifier(new File(args[0]));
        
        // create a SAX Parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        XMLReader reader = spf.newSAXParser().getXMLReader();
        
        // set up a pipeline
        VerifierHandler handler = verifier.getVerifierHandler();
        XPathLocationTracker tracker = new XPathLocationTracker(handler);
        reader.setContentHandler(tracker);

        verifier.setErrorHandler( new ErrorHandlerImpl(tracker) );
        
        for( int i=1; i<args.length; i++ ) {
            System.out.println("parsing "+args[i]);
            reader.parse(new InputSource(new FileInputStream(args[i])));
        }
    }
    
    private static class ErrorHandlerImpl implements ErrorHandler {
        
        private final XPathLocationTracker tracker;
        
        ErrorHandlerImpl(XPathLocationTracker _tracker) {
            this.tracker = _tracker;
        }
        
        public void warning(SAXParseException exception) throws SAXException {
            print("warning ",exception);
        }

        public void error(SAXParseException exception) throws SAXException {
            print("error ",exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            print("fatal ",exception);
        }

        private void print(String name, SAXParseException exception) {
            System.out.println(name + exception.getMessage());
            System.out.println("  "+tracker.getXPath());
        }
        
    }
}
