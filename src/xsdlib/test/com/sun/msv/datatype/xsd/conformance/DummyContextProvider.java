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

package com.sun.msv.datatype.xsd.conformance;

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * dummy implementation of ValidationContextProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class DummyContextProvider implements ValidationContext, SerializationContext
{
    private DummyContextProvider() {}
    
    public static final DummyContextProvider theInstance
        = new DummyContextProvider();
    
    public String resolveNamespacePrefix( String prefix ) {
        if( prefix.equals("foo") )
            return "http://foo.examples.com";
        if( prefix.equals("bar") || prefix.equals("baz") )
            return "http://bar.examples.com";
        if( prefix.equals("") || prefix.equals("emp") )
            return "http://empty.examples.com";
        
        return null;    // undefined
    }
    
    public String getNamespacePrefix( String uri ) {
        if( uri.equals("http://foo.examples.com") )
            return "foo";
        if( uri.equals("http://bar.examples.com") )
            return "bar";
        if( uri.equals("http://empty.examples.com") )
            return null;    // the default namespace.
        return "xyz";    // undefined
    }
    
    
    public boolean isUnparsedEntity( String name ) {
        return name.equals("foo") || name.equals("bar");
    }
    
    public boolean isNotation( String name ) {
        return name.equals("foo") || name.equals("bar");
    }

    public String getBaseUri() { return null; }
}
