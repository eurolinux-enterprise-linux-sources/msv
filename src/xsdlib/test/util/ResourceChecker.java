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

package util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

/**
 * checks the existance of message resource.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ResourceChecker {
    
    /**
     * checks the existance of message resource.
     * 
     * this method utilizes Java reflection to read values of 
     * "public static final String ERR_*****.
     * 
     * @param prefix
     *        Fields whose name does not start with this prefix are not tested.
     *        Can be "".
     */
    public static void check( Class cls, String prefix, Checker checker ) throws Exception {
        Field[] fields = cls.getDeclaredFields();
        
        for( int i=0; i<fields.length; i++ ) {
            int mod = fields[i].getModifiers();
            if( Modifier.isStatic(mod)
            &&    Modifier.isPublic(mod)
            &&    Modifier.isFinal(mod)
            &&  fields[i].getType() == String.class
            &&  fields[i].getName().startsWith(prefix) ){
                
                // test English resource
                Locale.setDefault( Locale.ENGLISH );
                checker.check( (String)fields[i].get(null) );
                
                // also test Japanese resource
                Locale.setDefault( Locale.JAPANESE );
                checker.check( (String)fields[i].get(null) );
            }
        }
    }
}
