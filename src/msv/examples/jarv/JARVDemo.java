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

package jarv;

import java.io.File;
import org.iso_relax.verifier.*;
import com.sun.msv.driver.textui.ReportErrorHandler;

/**
 * Uses <a href="http://iso-relax.sourceforge.net/apiDoc/">JARV</a> to validate documents.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class JARVDemo
{
    public static void main( String args[] ) throws Exception {
        
        if( args.length<2 ) {
            System.out.println("JARVDemo <schema file> <instance file 1> <instance file 2> ...");
            return;
        };
        /*
            Implementation independent way to create a VerifierFactory.
            This method will discover an appropriate JARV implementation and
            returns the factory of that implementation.
         
            To load a validator engine for RELAX NG, simply change
            the argument to "http://relaxng.org/ns/structure/0.9"
        */        
        // VerifierFactory factory = VerifierFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        /*
            MSV dependent way to create a VerifierFactory.
            But this allows MSV to detect the schema language.
        */
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
         
        // parse a schema.
        // other overloaded methods allows you to parse a schema from InputSource, URL, etc.
        Verifier verifier = factory.newVerifier(new File(args[0]));
        
        // set the error handler. This object receives validation errors.
        // you can pass any class that implements org.sax.ErrorHandler.
        verifier.setErrorHandler( new ReportErrorHandler() );
        
        // use the verify method to validate documents.
        // or you can validate SAX events by using the getVerifierHandler method.
        for( int i=1; i<args.length; i++ )
            if(verifier.verify(args[i]))
                System.out.println(args[i]+" is valid");
            else
                System.out.println(args[i]+" is NOT valid");
    }
}
