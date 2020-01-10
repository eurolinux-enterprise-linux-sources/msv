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

package com.sun.msv.reader.xmlschema;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController2;

public class MultiSchemaReaderTest {

    private static class LocalController implements GrammarReaderController2 {

        public LSResourceResolver getLSResourceResolver() {
            return null;
        }

        public void error(Locator[] locs, String errorMessage, Exception nestedException) {
            StringBuffer errors = new StringBuffer();
            for (Locator loc : locs) {
                errors.append("in " + loc.getSystemId() + " " + loc.getLineNumber() + ":"
                              + loc.getColumnNumber());
            }
            throw new RuntimeException(errors.toString(), nestedException);
        }

        public void warning(Locator[] locs, String errorMessage) {
            StringBuffer errors = new StringBuffer();
            for (Locator loc : locs) {
                errors.append("in " + loc.getSystemId() + " " + loc.getLineNumber() + ":"
                              + loc.getColumnNumber());
            }
            // no warning allowed.
            throw new RuntimeException("warning: " + errors.toString());
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    };

    @Test
    public void testWsdlMultiSchema() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        URL wsdlUri = getClass().getResource("test.wsdl");
        Document wsdl = documentBuilder.parse(wsdlUri.openStream());
        String wsdlSystemId = wsdlUri.toExternalForm();
        DOMSource source = new DOMSource(wsdl);
        source.setSystemId(wsdlSystemId);

        LocalController controller = new LocalController();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLSchemaGrammar result = WSDLSchemaReader.read(source, factory, controller);
        assertNotNull(result);
    }
    
    @Test
    public void testWsdlMultiRefSchema() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        URL wsdlUri = getClass().getResource("multireference.wsdl");
        Document wsdl = documentBuilder.parse(wsdlUri.openStream());
        String wsdlSystemId = wsdlUri.toExternalForm();
        DOMSource source = new DOMSource(wsdl);
        source.setSystemId(wsdlSystemId);

        LocalController controller = new LocalController();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLSchemaGrammar result = WSDLSchemaReader.read(source, factory, controller);
        assertNotNull(result);
    }
}
