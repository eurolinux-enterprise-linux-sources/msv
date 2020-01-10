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
 * test every possible combination of child patterns.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class FullCombinationPattern implements TestPattern
{
    private final TestPattern[] children;
    /** True indicates 'AND' mode. False is 'OR' mode. */
    private final boolean mergeMode;
    
    private boolean noMore = false;
    
    public FullCombinationPattern( TestPattern[] children, boolean mergeMode )
    {
        for( int i=0; i<children.length; i++ )
            children[i] = new PatternWithEmpty(children[i]);
        this.children = children;
        this.mergeMode = mergeMode;
        reset();
    }

    /** returns the number of test cases to be generated */
    public long totalCases()
    {
        // to enumerate every possible combination
        // of every possible underlying patterns,
        // we have to need l times m times n ... where
        // l,m,n are numbers of child test cases.
        long result = 1;
        for( int i=0; i<children.length; i++ )
            result *= children[i].totalCases();
        // every test pattern comes with empty facet, and
        // as a result this calculation includes empty case,
        // without any further hussle.
        return result;
    }

    /** restart generating test cases */
    public void reset()
    {
        for( int i=0; i<children.length; i++ )
            children[i].reset();
        noMore=false;
    }

    public String get( TypeIncubator ti ) throws DatatypeException
    {
        String answer=null;
        for( int i=0; i<children.length; i++ )
            answer = TestPatternGenerator.merge( answer, children[i].get(ti), mergeMode );
        
        return answer;
    }

    /** generate next test case */
    public void next()
    {
        // increment.
        // Imagine a increment of number.
        // 09999 + 1 => 10000
        // so if an increment results in carry, reset the digit
        // and increment the next digit.
        // this's what is done here.
        int i;
        for( i=children.length-1; i>=0; i-- )
        {
            children[i].next();
            if( children[i].hasMore() )
            {
                for( i++; i<children.length; i++ )
                    children[i].reset();
                return;
            }
        }
        
        noMore = true;
    }

    public boolean hasMore()
    {
        return !noMore;
    }
    
    /**
     * adds empty test case to the base pattern
     */
    private static class PatternWithEmpty implements TestPattern
    {
        private final TestPattern base;
        private int mode;
        
        PatternWithEmpty( TestPattern base ) { this.base=base; reset(); }
        
        public long totalCases() { return base.totalCases()+1; }
    
        public void reset() { base.reset();mode=0; }
    
        public String get( TypeIncubator ti ) throws DatatypeException
        {
            switch(mode)
            {
            case 0:        return base.get(ti);
            case 1:        return null;
            default:    throw new Error();
            }
        }
    
        public void next()
        {
            switch(mode)
            {
            case 0:
                base.next();
                if(!base.hasMore())        mode=1;
                return;
            case 1:
                mode=2; return;
            default:
                throw new Error();
            }
        }
    
        public boolean hasMore()    { return mode!=2; }
    }
}
