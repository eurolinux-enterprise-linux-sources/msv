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

package schemaLookup;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.xmlschema.MultiSchemaReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.verifier.jarv.SchemaImpl;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    public static void main( String[] args ) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        for( int i=1; i<args.length; i++ ) {        
            String xmlFile = args[i];
            System.out.println(xmlFile);
            
            Document dom = dbf.newDocumentBuilder().parse(new File(xmlFile));
            
            // collect all namespaces and assemble a schema
            SchemaBuilder sb = new SchemaBuilder(args[0]);
            NamespaceScanner.scan( dom, sb );
            
            Schema schema = sb.getResult();
            if( schema==null ) {
                System.out.println("failed to parse a schema");
                continue;
            }
            Verifier verifier = schema.newVerifier();
            
            if( verifier.verify(dom) )
                System.out.println("valid");
            else
                System.out.println("invalid");
        }
    }

    private static class SchemaBuilder implements NamespaceReceiver {
        
        private final CatalogResolver resolver = new CatalogResolver();
        private final MultiSchemaReader msr = new MultiSchemaReader(
            new XMLSchemaReader(new DebugController(true)));
        
        public SchemaBuilder( String catalogFile ) throws IOException {
            resolver.getCatalog().parseCatalog(catalogFile);
        }
        
        public void onNamespace(String ns) {
            InputSource is = resolver.resolveEntity(ns,"");
            if(is==null) {
                System.out.println("no schema found for the namespace "+ns);
                return;
            }
            msr.parse(is);
        }
        
        public Schema getResult() {
            Grammar result = msr.getResult();
            if(result==null)    return null;
            else                return new SchemaImpl(result);
        }
    }
}
