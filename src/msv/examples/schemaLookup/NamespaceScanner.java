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

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Enumerates all the namespaces from a DOM tree.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class NamespaceScanner {
    public static void scan( Document d, NamespaceReceiver r ) {
        scan(d.getDocumentElement(),r);
    }
    public static void scan( Element e, NamespaceReceiver r ) {
        new NamespaceScanner(r).scan(e);
    } 
    
    private final Set nss = new HashSet();
    private final NamespaceReceiver receiver;
    
    private NamespaceScanner( NamespaceReceiver r ) {
        this.receiver = r;
        // xmlns
        nss.add("http://www.w3.org/2000/xmlns/");
    }
    
    private void scan( Element e ) {
        onNamespace(e.getNamespaceURI());
        
        NamedNodeMap atts = e.getAttributes();
        for( int i=0; i<atts.getLength(); i++ )
            onNamespace(atts.item(i).getNamespaceURI());
        
        for( int i=0; i<e.getChildNodes().getLength(); i++ ) {
            Node n = e.getChildNodes().item(i);
            if( n instanceof Element )
                scan( (Element)n );
        }
    }
    
    private void onNamespace( String ns ) {
        if(ns==null)    ns="";
        if(nss.add(ns))
            receiver.onNamespace(ns);
    }
}
