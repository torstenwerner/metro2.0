/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.transport.tcp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Stashok
 */
public final class ContentType {
    private String mimeType;
    private final Map<String, String> parameters = new HashMap<String, String>(4);
    
    public ContentType() {
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void parse(final String contentType) throws WSTCPException {
        parameters.clear();
        
        final int mimeDelim = contentType.indexOf(';');
        if (mimeDelim == -1) { // If the contentType doesn't have params
            mimeType = contentType.trim().toLowerCase();
            return;
        } else {
            mimeType = contentType.substring(0, mimeDelim).trim().toLowerCase();
        }
        
        int delim = mimeDelim + 1;
        // Scan ContentType string's params, decode them
        while(delim < contentType.length()) {
            int nextDelim = contentType.indexOf(';', delim);
            if (nextDelim == -1) nextDelim = contentType.length();
            
            int eqDelim = contentType.indexOf('=', delim);
            if (eqDelim == -1) eqDelim = nextDelim;
            
            final String key = contentType.substring(delim, eqDelim).trim();
            final String value = contentType.substring(eqDelim + 1, nextDelim).trim();
            parameters.put(key, value);
            
            delim = nextDelim + 1;
        }
    }
    
    public boolean equals(Object o) {
        if (o != null && o instanceof ContentType) {
            ContentType ctToCompare = (ContentType) o;
            return ctToCompare.mimeType == mimeType && ctToCompare.parameters.equals(parameters);
        }
        
        return false;
    }

    public int hashCode() {
        return mimeType.hashCode() ^ parameters.hashCode();
    }
}
