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

package com.sun.msv.verifier.jarv;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * tests JARV.
 * 
 * This class is <b>NOT</b> a part of the JUnit test cases.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class JARVTester
{
    protected abstract VerifierFactory getFactory(String language) throws Exception;
    
    public void run(java.lang.String[] args) throws Exception {
        if(args.length<3) {
            System.out.println(
                "Usage: FactoryLoaderTester <language> <schema> <instance> ...\n");
            return;
        }
        
        VerifierFactory factory = getFactory(args[0]);
        if(factory==null) {
            System.out.println("unable to find an implementation");
            return;
        }
        
        Schema schema = factory.compileSchema(args[1]);
        if(schema==null) {
            System.out.println("unable to parse this schema");
            return;
        }
        
        Verifier verifier = schema.newVerifier();
        verifier.setErrorHandler( new ErrorHandler(){
            public void fatalError( SAXParseException e ) {
                System.out.println("fatal:"+e);
            }
            public void error( SAXParseException e ) {
                System.out.println("error:"+e);
            }
            public void warning( SAXParseException e ) {
                System.out.println("warning:"+e);
            }
        });
        
        for( int i=2; i<args.length; i++ )
            verifier.verify(args[2]);
    }
}
