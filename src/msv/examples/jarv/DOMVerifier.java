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

import org.iso_relax.verifier.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import java.io.File;

/**
 * Uses <a href="http://iso-relax.sourceforge.net/apiDoc/">JARV</a>
 * to validate DOM documents/subtree.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DOMVerifier
{
    public static void main( String[] args ) throws Exception {
        if(args.length<2) {
            System.out.println("Usage: DOMVerifier <schema> <instance> ...");
            return;
        }
        
        // setup JARV and compile a schema.
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        Verifier verifier = factory.compileSchema(args[0]).newVerifier();
            // instead, you can call factory.newVerifier(args[0])
            // this will result in the same behavior.
        
        // setup JAXP
        DocumentBuilderFactory domf = DocumentBuilderFactory.newInstance();
        domf.setNamespaceAware(true);
        DocumentBuilder builder = domf.newDocumentBuilder();
        
        for( int i=1; i<args.length; i++ ) {
            // parse a document into a DOM.
            Document dom = builder.parse(new File(args[i]));
            
            // performs the validation on the whole tree.
            // instead, you can pass an Element to the verify method, too.
            // e.g.,  verifier.verify(dom.getDocumentElement())
            if(verifier.verify(dom))
                System.out.println("valid  :"+args[i]);
            else
                System.out.println("invalid:"+args[i]);
        }
    }
}
