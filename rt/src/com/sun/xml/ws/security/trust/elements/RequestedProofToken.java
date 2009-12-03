/*
 * $Id: RequestedProofToken.java,v 1.3 2008-02-26 06:33:19 ofung Exp $
 */

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

package com.sun.xml.ws.security.trust.elements;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import java.net.URI;

/**
 * @author WS-Trust Implementation Team.
 */
public interface RequestedProofToken {
    
    /** constants indicating type of Proof Token 
     * @see getProofTokenType 
     */
    public static final String COMPUTED_KEY_TYPE = "ComputedKey";
    public static final String TOKEN_REF_TYPE = "SecurityTokenReference";
    public static final String ENCRYPTED_KEY_TYPE = "EncryptedKey";
    public static final String BINARY_SECRET_TYPE = "BinarySecret";
    public static final String CUSTOM_TYPE = "Custom";
    
    /**
     * Get the type of ProofToken present in this RequestedProofToken Instance
     */
    String getProofTokenType();

   /**
     * Set the type of ProofToken present in this RequestedProofToken Instance
     * @see getProofTokenType
     */
    void setProofTokenType(String proofTokenType);

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link Element }
     *     {@link Object }
     *     
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     {@link Object }
     *     
     */
    void setAny(Object value);
    
    /**
     * Set a SecurityTokenReference as the Proof Token 
     */
    void setSecurityTokenReference(SecurityTokenReference reference);
    
    /**
     * Gets the SecrityTokenReference if set 
     * @return SecurityTokenReference if set, null otherwise
     */
    SecurityTokenReference getSecurityTokenReference();
    
    /**
     *Sets the Computed Key URI (describing how to compute the Key)
     */
    void setComputedKey(URI computedKey);
    
    /**
     *Get the Computed Key URI (describing how to compute the Key)
     *@return computed key URI or null if none is set
     */
    URI getComputedKey();
    
    /**
     * Sets a wst:BinarySecret as the Proof Token
     */
     void setBinarySecret(BinarySecret secret);
     
     /**
      * Gets the BinarySecret proof Token if set
      * @return BinarySecret if set, null otherwise
      */
     BinarySecret getBinarySecret();
}
