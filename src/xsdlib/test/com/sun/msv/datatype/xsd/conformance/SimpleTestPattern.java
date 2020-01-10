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

import com.sun.msv.datatype.xsd.TypeIncubator;
import org.relaxng.datatype.DatatypeException;

/**
 * test pattern that corresponds with one test case.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class SimpleTestPattern implements TestPattern
{
    /** returns the number of test cases to be generated */
    public long totalCases() { return 1; }    // pattern itself or the empty
    
    /** restart generating test cases */
    public void reset() { idx=0; }
    
    /** get the current test case */
    public String get( TypeIncubator incubator ) throws DatatypeException
    {
        switch(idx)
        {
        case 0:
            incubator.addFacet( facetName, facetValue, false, DummyContextProvider.theInstance );
            return answer;
        default:
            throw new Error();
        }
    }
    
    /** generate next test case */
    public void next() { idx++; }
    
    public boolean hasMore() { return idx!=1; }

    private final String facetName;
    private final String facetValue;
    private final String answer;
    private int idx=0;
    
    SimpleTestPattern( String facetName, String facetValue, String answer )
    {
        this.facetName    = facetName;
        this.facetValue    = facetValue;
        this.answer        = answer;
        reset();
    }
}
