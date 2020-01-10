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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Monitors the SAX events and compute the XPath expression
 * that points to the current location.
 * 
 * <p>
 * The XPath computed by this tool is of the form:
 * /foo/bar[3]/zot[2] ...
 * 
 * <p>
 * This can be used for example to point to the location of the error.
 * To do this, set up SAX pipeline as follows:
 * 
 * <pre>
 * EventSource -> XPathLocationTracker -> Verifier -> ...
 * </pre>
 * 
 * <p>
 * Then when you receive an error from a verifier, query this component
 * about the XPath location of the error.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XPathLocationTracker extends XMLFilterImpl {
    
    public XPathLocationTracker( XMLReader r ) {
        super(r);
    }
    
    public XPathLocationTracker( ContentHandler handler ) {
        setContentHandler(handler);
    }
    
    /**
     * Captures the occurrences of elements among siblings.
     */
    private static final class State {
        /**
         * Counts the occurence of element names.
         * Counting is done by using rawName.
         */
        private final Map counters = new HashMap();
        
        /**
         * Parent state, or null if this state governs the document element.
         */
        private final State parent;
        
        /**
         * Child state, or null if it's not yet created.
         * 
         * <p>
         * To reuse {@link State} objects, we only create one child state.
         */
        private State child;
        
        /**
         * Name of the current element which we are parsing right now.
         */
        private String currentName;
        
        State( State parent ) {
            this.parent = parent;
        }
        
        /**
         * Accounts a new cihld element and then
         * returns a new child state.
         */
        protected State push( String rawName ) {
            count(rawName);
            currentName = rawName;
            if(child==null)
                child = new State(this);
            else
                child.reset();
            return child;
        }
        
        /**
         * Goes back to the parent state.
         */
        protected State pop() {
            parent.currentName = null;
            return parent;
        }
        
        private void count( String rawName ) {
            Integer i = (Integer)counters.get(rawName);
            if(i==null)
                i = getInt(1);
            else
                i = getInt(i.intValue()+1);
            counters.put(rawName,i);
        }
        
        void reset() {
            counters.clear();
            currentName = null;
        }
        
        String getXPath() {
            String r;
            if(parent==null) {
                // root state
                r = "/";
                if(currentName!=null)
                    r += currentName;
            } else {
                // child state
                r = parent.getXPath();
                if(currentName!=null) {
                    r += '/' + currentName;
                    Integer i = (Integer)counters.get(currentName);
                    r += '[' + i.toString() + ']';
                }
            }
            return r;
        }
    }
    
    /**
     * Current state.
     */
    private State state;
    
    public void startDocument() throws SAXException {
        state = new State(null);
        super.startDocument();
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        state = null;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        state = state.push(qName);
        super.startElement(uri, localName, qName, atts);
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        state = state.pop();
    }
    
    /**
     * Gets the XPath expression that points to the current location.
     * 
     * @throws IllegalStateException
     *      If the component is not parsing a document.
     */
    public final String getXPath() {
        if(state==null)
            throw new IllegalStateException("startDocument event is not invoked");
        return state.getXPath();
    }
    
    
    /**
     * Effectively the same as <pre>new Integer(i)</pre>
     */
    private static Integer getInt(int i) {
        if(i<ints.length)
            return ints[i];
        else
            return new Integer(i);
    }
    
    private static final Integer[] ints = new Integer[] {
        new Integer(0),
        new Integer(1),
        new Integer(2),
        new Integer(3),
        new Integer(4)
    };
}
